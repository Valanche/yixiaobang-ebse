package com.weixin.njuteam.service.impl.help;

import com.weixin.njuteam.config.cache.RedisOperator;
import com.weixin.njuteam.dao.help.SeekHelpInfoMapper;
import com.weixin.njuteam.entity.po.help.SeekHelpImage;
import com.weixin.njuteam.entity.po.help.SeekHelpInfoClickPO;
import com.weixin.njuteam.entity.po.help.SeekHelpInfoPO;
import com.weixin.njuteam.entity.po.help.SeekHelpSearchHistoryPO;
import com.weixin.njuteam.entity.vo.UserVO;
import com.weixin.njuteam.entity.vo.help.*;
import com.weixin.njuteam.enums.SeekHelpFinishStatus;
import com.weixin.njuteam.exception.AddTaskException;
import com.weixin.njuteam.exception.FormatException;
import com.weixin.njuteam.service.RecommendService;
import com.weixin.njuteam.service.SeekHelpInfoService;
import com.weixin.njuteam.service.TaskService;
import com.weixin.njuteam.service.UserService;
import com.weixin.njuteam.util.FileUtil;
import com.weixin.njuteam.util.TencentCOS;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
public class SeekHelpInfoServiceImpl implements SeekHelpInfoService {

	private static final String CACHE_KEY = "seekHelpInfoKeyword";
	private final SeekHelpInfoMapper infoMapper;
	private final TaskService taskService;
	private final RedisOperator redisOperator;
	private final RecommendService recommendService;
	private final UserService userService;

	@Autowired
	public SeekHelpInfoServiceImpl(SeekHelpInfoMapper infoMapper, TaskService taskService, RecommendService recommendService,
								   RedisOperator redisOperator, UserService userService) {
		this.infoMapper = infoMapper;
		this.taskService = taskService;
		this.recommendService = recommendService;
		this.redisOperator = redisOperator;
		this.userService = userService;
	}

	@Override
	public SeekHelpInfoAndUserVO querySeekInfoById(@NotNull Long id) {
		SeekHelpInfoPO seekInfoPo = infoMapper.querySeekInfoById(id);
		if (seekInfoPo == null) {
			return null;
		}
		UserVO user = userService.queryUserById(seekInfoPo.getUserId());

		return user == null ? null : new SeekHelpInfoAndUserVO(seekInfoPo, user.getNickName(), user.getAvatarUrl());
	}

	@Override
	public List<SeekHelpInfoVO> querySeekInfoByUserId(@NotNull String openId) {
		long userId = userService.queryUserId(openId);
		List<SeekHelpInfoPO> seekInfoPoList = infoMapper.querySeekInfoByUserId(userId);

		return convertList(seekInfoPoList);
	}

	@Override
	@Cacheable(cacheNames = "seekHelpInfo", key = "'all'")
	public Map<String, List<SeekHelpInfoAndUserVO>> getSeekInfoGroupByTag() {
		List<SeekHelpInfoPO> helpInfoList = infoMapper.querySeekInfoInProgress();
		Map<String, List<SeekHelpInfoAndUserVO>> map = new HashMap<>();

		for (SeekHelpInfoPO helpInfo : helpInfoList) {
			String tag = helpInfo.getTag();
			UserVO user = userService.queryUserById(helpInfo.getUserId());
			if (user != null) {
				map.putIfAbsent(tag, new ArrayList<>());
				map.get(tag).add(new SeekHelpInfoAndUserVO(helpInfo, user.getNickName(), user.getAvatarUrl()));
			}
		}

		return map;
	}

	@Override
	public List<SeekHelpInfoAndUserVO> getRecommendList(@NotNull String openId) {
		List<SeekHelpInfoVO> recommendList = recommendService.getSeekHelpInfoRecommendList(openId);
		List<SeekHelpInfoAndUserVO> resList = new ArrayList<>(recommendList.size());

		for (SeekHelpInfoVO helpInfo : recommendList) {
			Long userId = helpInfo.getUserId();
			UserVO user = userService.queryUserById(userId);
			if (user == null) {
				return Collections.emptyList();
			}
			resList.add(new SeekHelpInfoAndUserVO(helpInfo, user.getNickName(), user.getAvatarUrl()));
		}

		return resList;
	}

	@Override
	@Cacheable(cacheNames = "helpInfoKeyword", key = "#keyword")
	public List<SeekHelpInfoAndUserVO> searchSeekInfo(@NotNull String openId, String keyword) {
		if (keyword == null) {
			return Collections.emptyList();
		}

		List<SeekHelpInfoPO> allSeekHelpInfoList = infoMapper.querySeekInfoInProgress();
		// 同时插入搜索历史记录
		long userId = userService.queryUserId(openId);
		SeekHelpSearchHistoryPO historyPo = SeekHelpSearchHistoryPO.builder()
			.userId(userId)
			.keyword(keyword)
			.build();
		infoMapper.insertSearchHistory(historyPo);

		List<SeekHelpInfoAndUserVO> resultList = new ArrayList<>();
		for (SeekHelpInfoPO seekHelpInfo : allSeekHelpInfoList) {
			if (seekHelpInfo.getName().toLowerCase().contains(keyword.toLowerCase()) || seekHelpInfo.getComment().toLowerCase().contains(keyword.toLowerCase())) {
				UserVO user = userService.queryUserById(seekHelpInfo.getUserId());
				if (user == null) {
					return Collections.emptyList();
				}
				resultList.add(new SeekHelpInfoAndUserVO(seekHelpInfo, user.getNickName(), user.getAvatarUrl()));
			}
		}

		return resultList;
	}

	@Override
	public List<SeekHelpSearchHistoryVO> querySearchHistory(@NotNull String openId, int size) {
		long userId = userService.queryUserId(openId);
		List<SeekHelpSearchHistoryPO> historyPoList = infoMapper.querySearchHistory(userId, size);

		return historyPoList == null ? null : convertHistoryList(historyPoList);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	@CacheEvict(cacheNames = "seekHelpInfo", key = "'all'")
	public SeekHelpInfoVO insertSeekInfo(SeekHelpInfoVO seekInfoVo, @NotNull String openId) throws AddTaskException {
		if (seekInfoVo == null) {
			return null;
		}

		// 删除历史记录搜索到的缓存
		redisOperator.deleteAll(CACHE_KEY);

		long userId = userService.queryUserId(openId);
		seekInfoVo.setUserId(userId);
		SeekHelpInfoPO seekInfoPo = new SeekHelpInfoPO(seekInfoVo);
		int i = infoMapper.insertSeekInfo(seekInfoPo);
		seekInfoVo.setId(seekInfoPo.getId());

		addTask(seekInfoVo.getId(), seekInfoVo.getDeadLine());

		return i > 0 ? seekInfoVo : null;
	}

	@Override
	public SeekHelpInfoClickVO clickHelpInfo(@NotNull String openId, @NotNull Long seekHelpId) {
		long userId = userService.queryUserId(openId);
		if (userId <= 0) {
			return null;
		}
		SeekHelpInfoClickPO clickPo = infoMapper.queryClick(userId, seekHelpId);
		if (clickPo == null) {
			clickPo = SeekHelpInfoClickPO.builder()
				.userId(userId)
				.seekHelpId(seekHelpId)
				.build();

			int i = infoMapper.insertClick(clickPo);
			return i > 0 ? new SeekHelpInfoClickVO(clickPo) : null;
		}
		int i = infoMapper.updateClick(clickPo.getId());
		return i > 0 ? new SeekHelpInfoClickVO(clickPo) : null;
	}

	@Override
	public boolean endClick(@NotNull Long clickId) {
		int i = infoMapper.updateClickEndTime(clickId);
		return i > 0;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public List<SeekHelpImage> insertImageList(@NotNull Long seekHelpId, MultipartFile[] imageList) {
		return writeImage(seekHelpId, imageList, true);
	}

	@Override
	@CacheEvict(cacheNames = "seekHelpInfo", key = "'all'")
	public SeekHelpInfoVO updateSeekInfo(SeekHelpInfoVO seekInfoVo, @NotNull String openId) {
		// update need seek help info value object contains id
		// we can still use token to get user id
		if (seekInfoVo == null || seekInfoVo.getId() == null || seekInfoVo.getId() <= 0) {
			return null;
		}

		long userId = userService.queryUserId(openId);
		seekInfoVo.setUserId(userId);
		SeekHelpInfoPO seekInfoPo = new SeekHelpInfoPO(seekInfoVo);
		// 判断是否有将finishStatus从已完成更新为进行中
		SeekHelpInfoPO seekHelpInfo = infoMapper.querySeekInfoById(seekInfoPo.getId());

		// 如果更改了新的截止日期
		Date newDeadLine = seekInfoVo.getDeadLine();
		// 只有在变成已截止且新的截止日期比现在还晚的时候才需要更新状态
		if (seekHelpInfo.getFinishStatus().equals(SeekHelpFinishStatus.NOT_FINISHED.getValue()) && newDeadLine != null && newDeadLine.after(new Date(System.currentTimeMillis()))) {
			// 说明要更新状态
			seekInfoVo.setFinishStatus(SeekHelpFinishStatus.IN_PROGRESS.getValue());
			seekInfoPo.setFinishStatus(SeekHelpFinishStatus.IN_PROGRESS.getValue());
			addTask(seekInfoVo.getId(), newDeadLine);
		}

		// 如果需要更改状态
		if (seekInfoPo.getFinishStatus() != null && seekHelpInfo.getFinishStatus().equals(SeekHelpFinishStatus.NOT_FINISHED.getValue()) && seekInfoPo.getFinishStatus().equals(SeekHelpFinishStatus.IN_PROGRESS.getValue())) {
			// 加入任务列表
			addTask(seekHelpInfo.getId(), seekHelpInfo.getDeadLine());
		}

		int i = infoMapper.updateSeekInfo(seekInfoPo);
		return i > 0 ? seekInfoVo : null;
	}

	@Override
	public List<SeekHelpImage> updateImageList(@NotNull Long seekHelpId, MultipartFile[] imageList) {
		return writeImage(seekHelpId, imageList, false);
	}

	@Override
	@CacheEvict(cacheNames = "seekHelpInfo", key = "'all'")
	public SeekHelpInfoVO updateFinishStatus(@NotNull Long seekHelpId, SeekHelpFinishStatus finishStatus) {
		String taskName = "seekHelpInfo" + seekHelpId;
		if (finishStatus.equals(SeekHelpFinishStatus.FINISHED) && taskService.containsTask(taskName)) {
			// 停止任务
			taskService.stopTask(taskName);
		}
		int i = infoMapper.updateFinishStatus(seekHelpId, finishStatus);

		SeekHelpInfoPO helpInfoPo = infoMapper.querySeekInfoById(seekHelpId);
		return (i > 0 && helpInfoPo == null) ? null : new SeekHelpInfoVO(helpInfoPo);
	}

	@Override
	@CacheEvict(cacheNames = "seekHelpInfo", key = "'all'")
	public boolean deleteSeekInfo(@NotNull Long id) {
		String taskName = "seekHelpInfo" + id;
		if (taskService.containsTask(taskName)) {
			// 停止任务
			taskService.stopTask(taskName);
		}

		int i = infoMapper.updateFinishStatus(id, SeekHelpFinishStatus.DELETED);
		return i > 0;
	}

	@Override
	public boolean deleteImage(@NotNull Long imageId) {
		SeekHelpImage image = infoMapper.querySeekHelpImage(imageId);
		if (image == null) {
			return false;
		}
		String url = image.getImageUrl();
		TencentCOS.deleteImage("SeekImg" + url.substring(url.lastIndexOf("/")));
		int i = infoMapper.deleteImage(imageId);
		return i > 0;
	}

	@Override
	public boolean deleteSearchHistory(@NotNull Long historyId) {
		redisOperator.deleteAll(CACHE_KEY);
		int i = infoMapper.deleteSearchHistory(historyId);
		return i > 0;
	}

	@Override
	public boolean deleteAllHistory(@NotNull String openId) {
		redisOperator.deleteAll(CACHE_KEY);
		long userId = userService.queryUserId(openId);
		int i = infoMapper.deleteAllSearchHistory(userId);

		return i > 0;
	}

	private List<SeekHelpInfoVO> convertList(List<SeekHelpInfoPO> list) {
		return list.stream().filter(Objects::nonNull).map(SeekHelpInfoVO::new).collect(Collectors.toList());
	}

	private List<SeekHelpSearchHistoryVO> convertHistoryList(List<SeekHelpSearchHistoryPO> list) {
		return list.stream().filter(Objects::nonNull).map(SeekHelpSearchHistoryVO::new).collect(Collectors.toList());
	}

	private List<SeekHelpImage> writeImage(Long seekHelpId, MultipartFile[] imageList, boolean isInsert) {
		if (imageList == null || imageList.length == 0) {
			return Collections.emptyList();
		}

		List<SeekHelpImage> newImageList = new ArrayList<>();
		List<String> urlList = FileUtil.writeSyncFile(imageList, "SeekImg/");
		// 遍历看是否有格式错误的
		for (int i = 0; i < urlList.size(); i++) {
			if ("Format Error".equals(urlList.get(i))) {
				throw new FormatException("index = " + i);
			}
		}
		for (String url : urlList) {
			if (url == null) {
				return Collections.emptyList();
			}
			SeekHelpImage seekHelpImage = SeekHelpImage.builder()
				.seekHelpId(seekHelpId)
				.imageUrl(url)
				.build();

			int i = isInsert ? infoMapper.insertSeekImage(seekHelpImage) : infoMapper.updateSeekImage(seekHelpImage);
			if (i <= 0) {
				return Collections.emptyList();
			}

			newImageList.add(seekHelpImage);
		}

		return newImageList;
	}

	private void addTask(Long seekHelpId, Date deadLine) {
		SeekHelpTaskVO seekHelpTask = SeekHelpTaskVO.builder()
			.seekHelpId(seekHelpId)
			.deadLine(deadLine)
			.finishStatus(SeekHelpFinishStatus.NOT_FINISHED)
			.build();

		taskService.addUserNucleicAcidTask(seekHelpTask);
	}
}
