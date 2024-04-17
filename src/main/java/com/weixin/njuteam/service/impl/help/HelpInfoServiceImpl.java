package com.weixin.njuteam.service.impl.help;

import com.weixin.njuteam.config.cache.RedisOperator;
import com.weixin.njuteam.dao.help.HelpInfoMapper;
import com.weixin.njuteam.entity.po.help.HelpImage;
import com.weixin.njuteam.entity.po.help.HelpInfoClickPO;
import com.weixin.njuteam.entity.po.help.HelpInfoPO;
import com.weixin.njuteam.entity.po.help.HelpSearchHistoryPO;
import com.weixin.njuteam.entity.vo.UserVO;
import com.weixin.njuteam.entity.vo.help.*;
import com.weixin.njuteam.enums.HelpFinishStatus;
import com.weixin.njuteam.exception.AddTaskException;
import com.weixin.njuteam.exception.FormatException;
import com.weixin.njuteam.service.HelpInfoService;
import com.weixin.njuteam.service.RecommendService;
import com.weixin.njuteam.service.TaskService;
import com.weixin.njuteam.service.UserService;
import com.weixin.njuteam.util.FileUtil;
import com.weixin.njuteam.util.TencentCOS;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Zyi
 */
@Service
@Slf4j
@CacheConfig(cacheNames = "helpInfo")
public class HelpInfoServiceImpl implements HelpInfoService {

	private static final String CACHE_KEY = "helpInfoKeyword";
	private final HelpInfoMapper helpInfoMapper;
	private final UserService userService;
	private final RecommendService recommendService;
	private final TaskService taskService;
	private final RedisOperator redisOperator;

	@Autowired
	public HelpInfoServiceImpl(HelpInfoMapper helpInfoMapper, UserService userService, TaskService taskService,
							   RecommendService recommendService, RedisOperator redisOperator) {
		this.helpInfoMapper = helpInfoMapper;
		this.userService = userService;
		this.taskService = taskService;
		this.recommendService = recommendService;
		this.redisOperator = redisOperator;
	}

	@Override
	public List<HelpInfoPO> queryAllHelpInfo() {
		return helpInfoMapper.queryHelpInfoInProgress();
	}

	@Override
	public HelpInfoAndUserVO queryHelpInfoById(@NotNull Long id) {
		HelpInfoPO helpInfoPo = helpInfoMapper.queryHelpInfoById(id);
		if (helpInfoPo == null) {
			return null;
		}
		UserVO user = userService.queryUserById(helpInfoPo.getUserId());

		return user == null ? null : new HelpInfoAndUserVO(helpInfoPo, user.getNickName(), user.getAvatarUrl());
	}

	@Override
	public List<HelpInfoVO> queryHelpInfoByUserId(@NotNull String openId) {
		long userId = userService.queryUserId(openId);
		List<HelpInfoPO> helpInfoPoList = helpInfoMapper.queryHelpInfoByUserId(userId);

		return helpInfoPoList == null ? null : convertList(helpInfoPoList);
	}

	@Override
	@Cacheable(cacheNames = "helpInfo", key = "'all'")
	public Map<String, List<HelpInfoAndUserVO>> getHelpInfoGroupByTag() {
		List<HelpInfoPO> helpInfoList = helpInfoMapper.queryHelpInfoInProgress();
		Map<String, List<HelpInfoAndUserVO>> map = new HashMap<>();

		for (HelpInfoPO helpInfo : helpInfoList) {
			String tag = helpInfo.getTag();
			UserVO user = userService.queryUserById(helpInfo.getUserId());
			if (user != null) {
				map.putIfAbsent(tag, new ArrayList<>());
				map.get(tag).add(new HelpInfoAndUserVO(helpInfo, user.getNickName(), user.getAvatarUrl()));
			}
		}

		return map;
	}

	@Override
	@Cacheable(cacheNames = CACHE_KEY, key = "#keyword")
	public List<HelpInfoAndUserVO> searchHelpInfo(@NotNull String openId, String keyword) {
		if (keyword == null) {
			return new ArrayList<>();
		}

		List<HelpInfoPO> allHelpInfoList = helpInfoMapper.queryHelpInfoInProgress();
		// 同时插入搜索历史记录
		long userId = userService.queryUserId(openId);
		HelpSearchHistoryPO historyPo = HelpSearchHistoryPO.builder()
			.userId(userId)
			.keyword(keyword)
			.build();
		helpInfoMapper.insertSearchHistory(historyPo);

		List<HelpInfoAndUserVO> resultList = new ArrayList<>();
		for (HelpInfoPO helpInfo : allHelpInfoList) {
			if (helpInfo.getName().toLowerCase().contains(keyword.toLowerCase()) || helpInfo.getComment().toLowerCase().contains(keyword.toLowerCase())) {
				UserVO user = userService.queryUserById(helpInfo.getUserId());
				if (user == null) {
					return new ArrayList<>();
				}
				resultList.add(new HelpInfoAndUserVO(helpInfo, user.getNickName(), user.getAvatarUrl()));
			}
		}

		return resultList;
	}

	@Override
	public List<HelpInfoAndUserVO> getRecommendList(@NotNull String openId) {
		List<HelpInfoVO> recommendList = recommendService.getHelpInfoRecommendList(openId);
		List<HelpInfoAndUserVO> resList = new ArrayList<>(recommendList.size());

		for (HelpInfoVO helpInfo : recommendList) {
			Long userId = helpInfo.getUserId();
			UserVO user = userService.queryUserById(userId);
			resList.add(new HelpInfoAndUserVO(helpInfo, user.getNickName(), user.getAvatarUrl()));
		}

		return resList;
	}

	@Override
	public List<HelpSearchHistoryVO> querySearchHistory(@NotNull String openId, int size) {
		long userId = userService.queryUserId(openId);
		List<HelpSearchHistoryPO> historyPoList = helpInfoMapper.querySearchHistory(userId, size);

		return historyPoList == null ? null : convertHistoryList(historyPoList);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	@CacheEvict(cacheNames = "helpInfo", key = "'all'")
	public HelpInfoVO insertHelpInfo(HelpInfoVO helpInfoVo, @NotNull String openId) throws AddTaskException {
		if (helpInfoVo == null) {
			return null;
		}

		// 删除历史记录搜索到的缓存
		redisOperator.deleteAll(CACHE_KEY);

		long userId = userService.queryUserId(openId);
		helpInfoVo.setUserId(userId);
		HelpInfoPO helpInfoPo = new HelpInfoPO(helpInfoVo);
		int i = helpInfoMapper.insertHelpInfo(helpInfoPo);
		helpInfoVo.setId(helpInfoPo.getId());

		// 加入任务列表
		addTask(helpInfoVo.getId(), helpInfoVo.getDeadLine());
		return i > 0 ? helpInfoVo : null;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	@CacheEvict(cacheNames = "helpInfo", key = "'all'")
	public List<HelpImage> insertImageList(@NotNull Long helpId, MultipartFile[] imageList) {
		return writeImage(helpId, imageList, true);
	}

	@Override
	public HelpInfoClickVO clickHelpInfo(@NotNull String openId, @NotNull Long helpId) {
		long userId = userService.queryUserId(openId);
		if (userId <= 0) {
			return null;
		}
		HelpInfoClickPO clickPo = helpInfoMapper.queryClick(userId, helpId);
		if (clickPo == null) {
			clickPo = HelpInfoClickPO.builder()
				.userId(userId)
				.helpId(helpId)
				.build();

			int i = helpInfoMapper.insertClick(clickPo);
			return i > 0 ? new HelpInfoClickVO(clickPo) : null;
		}
		int i = helpInfoMapper.updateClick(clickPo.getId());
		return i > 0 ? new HelpInfoClickVO(clickPo) : null;
	}

	@Override
	public boolean endClick(Long clickId) {
		int i = helpInfoMapper.updateClickEndTime(clickId);
		return i > 0;
	}

	@Override
	@CacheEvict(cacheNames = "helpInfo", key = "'all'")
	public HelpInfoVO updateHelpInfo(HelpInfoVO helpInfoVo, @NotNull String openId) {
		// update need help info value object contains id
		// we can still use token to get user id
		if (helpInfoVo == null || helpInfoVo.getId() == null || helpInfoVo.getId() <= 0) {
			return null;
		}

		long userId = userService.queryUserId(openId);
		helpInfoVo.setUserId(userId);
		HelpInfoPO helpInfoPo = new HelpInfoPO(helpInfoVo);

		// 判断是否有将finishStatus从已完成更新为进行中
		HelpInfoPO helpInfo = helpInfoMapper.queryHelpInfoById(helpInfoPo.getId());

		Date newDeadLine = helpInfoVo.getDeadLine();
		// 如果更新了时间
		if (helpInfo.getFinishStatus().equals(HelpFinishStatus.CLOSED.getValue()) && newDeadLine != null && newDeadLine.after(new Date(System.currentTimeMillis()))) {
			// 如果将deadLine更新了且时间在未来
			// 需要更新状态
			helpInfoVo.setFinishStatus(HelpFinishStatus.IN_PROGRESS.getValue());
			helpInfoPo.setFinishStatus(HelpFinishStatus.IN_PROGRESS.getValue());
			// 加入任务列表
			addTask(helpInfoVo.getId(), newDeadLine);
		}

		// 如果需要更改状态
		if (helpInfoPo.getFinishStatus() != null && helpInfo.getFinishStatus().equals(HelpFinishStatus.CLOSED.getValue()) && helpInfoPo.getFinishStatus().equals(HelpFinishStatus.IN_PROGRESS.getValue())) {
			// 加入任务列表
			addTask(helpInfo.getId(), helpInfo.getDeadLine());
		}

		int i = helpInfoMapper.updateHelpInfo(helpInfoPo);
		return i > 0 ? helpInfoVo : null;
	}

	@Override
	@CacheEvict(cacheNames = "helpInfo", key = "'all'")
	public List<HelpImage> updateImageList(@NotNull Long helpId, MultipartFile[] imageList) {
		return writeImage(helpId, imageList, false);
	}

	@Override
	public HelpInfoVO updateFinishStatus(@NotNull Long helpId, HelpFinishStatus finishStatus) {
		// 停止任务
		String taskName = "helpInfo" + helpId;
		if (finishStatus.equals(HelpFinishStatus.CLOSED) && taskService.containsTask(taskName)) {
			taskService.stopTask(taskName);
		}
		int i = helpInfoMapper.updateFinishStatus(helpId, finishStatus);

		HelpInfoPO helpInfoPo = helpInfoMapper.queryHelpInfoById(helpId);
		return (i > 0 && helpInfoPo == null) ? null : new HelpInfoVO(helpInfoPo);
	}

	@Override
	@CacheEvict(cacheNames = "helpInfo", key = "'all'")
	public boolean deleteHelpInfo(@NotNull Long id) {
		// 删除任务
		String taskName = "helpInfo" + id;
		if (taskService.containsTask(taskName)) {
			taskService.stopTask(taskName);
		}
		int i = helpInfoMapper.updateFinishStatus(id, HelpFinishStatus.DELETED);
		return i > 0;
	}

	@Override
	@CacheEvict(cacheNames = "helpInfo", key = "'all'")
	public boolean deleteImage(@NotNull Long imageId) {
		// 删除云存储图片
		HelpImage image = helpInfoMapper.queryHelpImage(imageId);
		if (image == null) {
			return false;
		}
		String url = image.getImageUrl();
		TencentCOS.deleteImage("HelpImg" + url.substring(url.lastIndexOf("/")));
		int i = helpInfoMapper.deleteImage(imageId);
		return i > 0;
	}

	@Override
	public boolean deleteSearchHelpInfoHistory(@NotNull Long historyId) {
		redisOperator.deleteAll(CACHE_KEY);

		int i = helpInfoMapper.deleteSearchHistory(historyId);
		return i > 0;
	}

	@Override
	public boolean deleteAllHistory(@NotNull String openId) {
		redisOperator.deleteAll(CACHE_KEY);

		long userId = userService.queryUserId(openId);
		int i = helpInfoMapper.deleteAllSearchHistory(userId);

		return i > 0;
	}

	private List<HelpInfoVO> convertList(List<HelpInfoPO> list) {
		return list.stream().filter(Objects::nonNull).map(HelpInfoVO::new).collect(Collectors.toList());
	}

	private List<HelpSearchHistoryVO> convertHistoryList(List<HelpSearchHistoryPO> list) {
		return list.stream().filter(Objects::nonNull).map(HelpSearchHistoryVO::new).collect(Collectors.toList());
	}

	private List<HelpImage> writeImage(Long helpId, MultipartFile[] imageList, boolean isInsert) {
		if (imageList == null || imageList.length == 0) {
			return new ArrayList<>();
		}
		List<HelpImage> list = new ArrayList<>();
		List<String> urlList = FileUtil.writeSyncFile(imageList, "HelpImg/");
		// 遍历看是否有格式错误的
		for (int i = 0; i < urlList.size(); i++) {
			if ("Format Error".equals(urlList.get(i))) {
				throw new FormatException("index = " + i);
			}
		}

		for (String url : urlList) {
			if (url == null) {
				return new ArrayList<>();
			}
			HelpImage helpImage = HelpImage.builder()
				.helpId(helpId)
				.imageUrl(url)
				.build();

			int i = isInsert ? helpInfoMapper.insertImage(helpImage) : helpInfoMapper.updateImage(helpImage);
			if (i <= 0) {
				return new ArrayList<>();
			}

			list.add(helpImage);
		}

		return list;
	}

	private void addTask(Long infoId, Date deadLine) {
		// 加入任务列表
		HelpTaskVO helpTask = HelpTaskVO.builder()
			.helpId(infoId)
			.deadLine(deadLine)
			.finishStatus(HelpFinishStatus.CLOSED)
			.build();

		taskService.addUserNucleicAcidTask(helpTask);
	}
}
