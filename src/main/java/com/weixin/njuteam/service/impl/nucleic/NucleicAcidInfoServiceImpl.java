package com.weixin.njuteam.service.impl.nucleic;

import com.weixin.njuteam.config.cache.RedisOperator;
import com.weixin.njuteam.dao.nucleic.NucleicAcidInfoMapper;
import com.weixin.njuteam.entity.po.InfoAssociatedTestingPO;
import com.weixin.njuteam.entity.po.nucleic.NucleicAcidInfoPO;
import com.weixin.njuteam.entity.po.nucleic.UpdateInfoPO;
import com.weixin.njuteam.entity.vo.UserVO;
import com.weixin.njuteam.entity.vo.nucleic.NucleicAcidInfoVO;
import com.weixin.njuteam.entity.vo.nucleic.NucleicAcidTaskVO;
import com.weixin.njuteam.entity.vo.nucleic.RecognizeResultVO;
import com.weixin.njuteam.enums.FinishStatus;
import com.weixin.njuteam.enums.HelpFinishStatus;
import com.weixin.njuteam.enums.NucleicAcidType;
import com.weixin.njuteam.exception.AddTaskException;
import com.weixin.njuteam.exception.UpdateNucleicAcidException;
import com.weixin.njuteam.service.NucleicAcidInfoService;
import com.weixin.njuteam.service.NucleicAcidTestingService;
import com.weixin.njuteam.service.TaskService;
import com.weixin.njuteam.service.UserService;
import com.weixin.njuteam.util.FileUtil;
import com.weixin.njuteam.util.OCRUtil;
import com.weixin.njuteam.util.Pair;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import com.weixin.njuteam.util.AccurateBasicUtil;

/**
 * 流程：
 * 用户上传核酸截图，通过OCR识别出对应该用户所拥有的通知的时间段
 * 然后将相应的通知标记为已完成。
 *
 * @author Zyi
 */
@Service
@Slf4j
@CacheConfig(cacheNames = "info")
public class NucleicAcidInfoServiceImpl implements NucleicAcidInfoService {

	private static final String LINE_SEPARATOR = System.lineSeparator();
	private static final String FILE_SEPARATOR = File.separator;
	private static final String IMG_PATH = "/root/WeixinData/image/".replace("/", FILE_SEPARATOR);

	private static final String CACHE_KEY = "infoUser::";

	/**
	 * 匹配格式: xxxx-xx-xx
	 */
	private static final Pattern PATTERN = Pattern.compile("(([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]|[0-9][1-9][0-9]{2}|[1-9][0-9]{3})-(((0[13578]|1[02])-(0[1-9]|[12][0-9]|3[01]))|((0[469]|11)-(0[1-9]|[12][0-9]|30))|(02-(0[1-9]|[1][0-9]|2[0-8]))))|((([0-9]{2})(0[48]|[2468][048]|[13579][26])|((0[48]|[2468][048]|[3579][26])00))-02-29)(([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]|[0-9][1-9][0-9]{2}|[1-9][0-9]{3})-(((0[13578]|1[02])-(0[1-9]|[12][0-9]|3[01]))|((0[469]|11)-(0[1-9]|[12][0-9]|30))|(02-(0[1-9]|[1][0-9]|2[0-8]))))|((([0-9]{2})(0[48]|[2468][048]|[13579][26])|((0[48]|[2468][048]|[3579][26])00))-02-29)");

	private final NucleicAcidInfoMapper infoMapper;
	private final UserService userService;
	private final TaskService taskService;
	private final NucleicAcidTestingService testingService;
	private final RedisOperator redisOperator;

	@Autowired
	public NucleicAcidInfoServiceImpl(NucleicAcidInfoMapper infoMapper, UserService userService,
									  NucleicAcidTestingService testingService, TaskService taskService,
									  RedisOperator redisOperator) {
		this.infoMapper = infoMapper;
		this.userService = userService;
		this.testingService = testingService;
		this.taskService = taskService;
		this.redisOperator = redisOperator;
	}

	@Override
	@Cacheable(cacheNames = "info", key = "#id")
	public NucleicAcidInfoVO queryInfoById(@NotNull Long id) {
		NucleicAcidInfoPO infoPo = infoMapper.queryInfoById(id);

		if (infoPo == null) {
			return null;
		}

		return new NucleicAcidInfoVO(infoPo);
	}

	@Override
	@Cacheable(cacheNames = "infoUser", key = "#openId")
	public List<NucleicAcidInfoVO> queryInfoByUserId(@NotNull String openId) throws SQLException {
		long userId = userService.queryUserId(openId);
		if (userId <= 0) {
			return null;
		}
		List<NucleicAcidInfoPO> infoPoList = infoMapper.queryInfoByUserId(userId);
		infoPoList = sortByFinishStatusAndTime(infoPoList);

		return convertList(infoPoList);
	}

	@Override
	public int queryInfoCount(String openId, FinishStatus finishStatus) {
		long userId = userService.queryUserId(openId);
		if (userId <= 0) {
			return -1;
		}

		return infoMapper.queryInfoCount(userId, finishStatus);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	@CachePut(cacheNames = "info", key = "#infoVo.getId()")
	public NucleicAcidInfoVO insertInfo(NucleicAcidInfoVO infoVo) throws AddTaskException {
		if (infoVo == null || infoVo.getUserId() == null || infoVo.getUserId() <= 0) {
			return null;
		}

		// 设置status默认值
		if (infoVo.getStatus() == null) {
			infoVo.setStatus("进行中");
		}

		NucleicAcidInfoPO infoPo = new NucleicAcidInfoPO(infoVo);
		int i = infoMapper.insertInfo(infoPo);
		infoVo.setId(infoPo.getId());

		// 加入任务列表
		addTask(infoVo.getId(), infoVo.getDeadLine());

		// 删除缓存
		redisOperator.delete(CACHE_KEY + userService.queryUserOpenId(infoVo.getUserId()));

		return i > 0 ? infoVo : null;
	}

	@Override
	public RecognizeResultVO recognize(MultipartFile image, @NotNull String openId, @NotNull Long infoId) {
		// 删除缓存
		redisOperator.delete(CACHE_KEY + openId);
		// 将该文件存到本地
		// 然后从数据库中获取该用户的核酸通知
		// 一个个时间比对 看是否有能够消除的通知
		if (FileUtil.isIncorrectFileFormat(image, new String[]{".png", ".jpg", "jpeg"})) {
			return null;
		}

		// 将该文件写入本地
		String imageName = FileUtil.writeFile(image, IMG_PATH);

		// 更新该次上报的imageName
		int i = infoMapper.updateImageName(infoId, imageName);
		String[] lines = AccurateBasicUtil.accurateBasic(IMG_PATH + FILE_SEPARATOR + imageName);

		Pair<String, List<String>> result = imageRecognize(lines);
		RecognizeResultVO recognizeResultVO = new RecognizeResultVO();
		// 1. 识别名字成功：此时就直接寻找数据库中对应的通知
		// 2. 识别名字不成功: 此时就需要返回识别出的名字和日期列表和通知让用户选择
		long userId = userService.queryUserId(openId);
		// 获取该用户的上报通知对应的检测通知
		InfoAssociatedTestingPO resultPo = infoMapper.queryTestingByInfoId(infoId, userId);
		Date startTime = resultPo.getTestingStartTime();
		Date endTime = resultPo.getTestingEndTime();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		boolean isHaveMatchTime = false;
		Date trueDate = null;

		// 从OCR结果中提取时间
		try {
			for (String strDate : result.getValue()) {
				Date date = new Date(dateFormat.parse(strDate).getTime());
				// 检查该时间是否在范围之内
				if (checkTime(date, startTime, endTime)) {
					recognizeResultVO.setIsMatch(true);
					trueDate = date;
					recognizeResultVO.setInfoVo(new NucleicAcidInfoVO(infoMapper.queryInfoById(infoId)));
					isHaveMatchTime = true;
					break;
				}
			}

			// 所有都遍历完仍然没有有效的
			// 没有匹配的时间
			if (!isHaveMatchTime) {
				recognizeResultVO.setIsMatch(false);
			}
		} catch (ParseException e) {
			log.error("transfer date error in nucleic acid info service ocr");
		}

		if (!isHaveMatchTime) {
			return recognizeResultVO;
		} else {
			// 判断名字
			if (isMatch(result.getKey(), openId)) {
				recognizeResultVO.setIsRecSuccess(true);
				// 名字匹配成功
				// 更新数据库
				int j = infoMapper.updateRecordFinish(infoId, FinishStatus.DONE);
				stopTask(infoId);
				// 更新真正的预测时间
				boolean isUpdated = testingService.updateTrueTime(resultPo.getTestingId(), trueDate);
				return i > 0 && j > 0 && isUpdated ? recognizeResultVO : null;
			} else {
				recognizeResultVO.setIsRecSuccess(false);
				// 识别出的结果的名字
				recognizeResultVO.setUserName(result.getKey());
				return i > 0 ? recognizeResultVO : null;
			}
		}
	}

	@Override
	@CacheEvict(cacheNames = "info", key = "#infoId")
	public NucleicAcidInfoVO updateComment(@NotNull Long infoId, @NotNull String openId, String comment) throws UpdateNucleicAcidException {
		int i = infoMapper.updateComment(infoId, comment);
		if (i <= 0) {
			throw new UpdateNucleicAcidException("update comment error");
		}

		// 更新成功则删除缓存
		redisOperator.delete(CACHE_KEY + openId);
		// 更新已完成
		int j = infoMapper.updateRecordFinish(infoId, FinishStatus.DONE);
		if (j <= 0) {
			throw new UpdateNucleicAcidException("update finish status error!");
		}
		NucleicAcidInfoPO infoPo = infoMapper.queryInfoById(infoId);
		// 更新对应检测未完成
		boolean isUpdatedSuccessfully = testingService.updateNotFinish(infoPo.getTitle(), infoPo.getUserId());
		return isUpdatedSuccessfully ? new NucleicAcidInfoVO(infoPo) : null;
	}

	@Override
	public NucleicAcidInfoVO fixResult(String userName, @NotNull String openId, @NotNull Long infoId) {
		UserVO userVo = userService.queryUser(openId);
		int i = -1;
		if (userVo.getName().equals(userName)) {
			i = infoMapper.updateRecordFinish(infoId, FinishStatus.DONE);
			stopTask(infoId);
		}

		return i > 0 ? new NucleicAcidInfoVO(infoMapper.queryInfoById(infoId)) : null;
	}

	@Override
	@CacheEvict(cacheNames = "info", key = "#infoVo.getId()")
	public NucleicAcidInfoVO updateInfo(NucleicAcidInfoVO infoVo) {
		if (infoVo == null || infoVo.getId() == null || infoVo.getId() <= 0) {
			return null;
		}

		// 删除缓存
		redisOperator.delete(CACHE_KEY + userService.queryUserOpenId(infoVo.getUserId()));
		NucleicAcidInfoPO infoPo = new NucleicAcidInfoPO(infoVo);
		int i = infoMapper.updateInfo(infoPo);

		if (i <= 0) {
			return null;
		}

		return infoVo;
	}

	@Override
	@CacheEvict(cacheNames = "info", key = "#id")
	public boolean updateRecordFinish(@NotNull Long id, FinishStatus status) {
		// 删除缓存
		String openId = userService.queryUserOpenId(infoMapper.queryInfoById(id).getUserId());
		redisOperator.delete(CACHE_KEY + openId);

		int i = infoMapper.updateRecordFinish(id, status);
		return i > 0;
	}

	@Override
	public NucleicAcidInfoVO updateInfoByUserIdAndTitle(NucleicAcidInfoVO infoVo, String oldTitle) {
		if (infoVo == null) {
			return null;
		}

		// 删除缓存
		redisOperator.delete(CACHE_KEY + userService.queryUserOpenId(infoVo.getUserId()));
		NucleicAcidInfoPO infoPo = new NucleicAcidInfoPO(infoVo);
		NucleicAcidInfoPO info = infoMapper.queryInfoByUserIdAndTitle(infoVo.getUserId(), oldTitle);
		// 如果更新了时间
		Date newDeadLine = infoVo.getDeadLine();
		if (info.getStatus().equals(FinishStatus.DONE.getValue()) && newDeadLine != null && newDeadLine.after(new Date(System.currentTimeMillis()))) {
			// 如果将deadLine更新了且时间在未来
			// 需要更新状态
			infoVo.setStatus(HelpFinishStatus.IN_PROGRESS.getValue());
			infoPo.setStatus(HelpFinishStatus.IN_PROGRESS.getValue());
			// 加入任务列表
			addTask(infoVo.getId(), newDeadLine);
		}
		int i = infoMapper.updateTestingByUserIdAndTitle(new UpdateInfoPO(infoPo, oldTitle));

		if (i <= 0) {
			return null;
		}

		return infoVo;
	}

	@Override
	@CacheEvict(cacheNames = "info", key = "#infoId")
	public NucleicAcidInfoVO openRemind(@NotNull String openId, @NotNull Long infoId, Boolean isOpenRemind) {
		NucleicAcidInfoVO infoVo = new NucleicAcidInfoVO(infoMapper.queryInfoById(infoId));
		if (infoVo.getUserId() != userService.queryUserId(openId)) {
			return null;
		}

		int i = infoMapper.openRemind(infoId, isOpenRemind);
		infoVo.setIsOpenRemind(true);

		// delete cache
		redisOperator.delete(CACHE_KEY + openId);
		return i > 0 ? infoVo : null;
	}

	@Override
	@CacheEvict(cacheNames = "info", key = "#id")
	public boolean deleteInfoById(@NotNull Long id) {
		String openId = userService.queryUserOpenId(infoMapper.queryInfoById(id).getUserId());
		redisOperator.delete(CACHE_KEY + openId);
		int i = infoMapper.deleteInfo(id);
		return i > 0;
	}

	@Override
	public boolean deleteInfoByUserIdAndTitle(Long userId, String title) {
		int i = infoMapper.deleteInfoByUserIdAndTitle(userId, title);
		// 删除缓存
		String openId = userService.queryUserOpenId(userId);
		redisOperator.delete(CACHE_KEY + openId);
		return i > 0;
	}

	private boolean isMatch(String name, String openId) {
		UserVO user = userService.queryUser(openId);
		return name.equals(user.getName());
	}

	/**
	 * OCR识别对应的时间
	 *
	 * @param lines OCR结果
	 * @return <K, V>: <名字, 所有检测时间>
	 */
	private Pair<String, List<String>> imageRecognize(String[] lines) {
		String name = null;
		List<String> dateList = new ArrayList<>();

		for (int i = 0; i < lines.length; i++) {
			if (lines[i] == null) {
				continue;
			}
			// 去除所有的空格
			lines[i] = lines[i].replace(" ", "");
			if (lines[i].contains("姓名") && i != lines.length - 1 && name == null) {
				// 这里保证核酸截图是不被p过的，即所有姓名都是一样的
				// 说明是名字 此时可以获得用户的名字
				String x = new String(lines[i]);

				if (x.length() >= 5) {
					if (x.charAt(x.indexOf("姓名") + 1) == ' ') {
						int p = x.indexOf("姓名");
						name = x.substring(p + 2).replace(" ", "");
					} else if (x.contains("：")) {
						int p = x.indexOf("：");
						name = x.substring(p + 1).replace(" ", "");
					} else if (x.contains(":")) {
						int p = x.indexOf(":");
						name = x.substring(p + 1).replace(" ", "");
					}
				}else{
					x = lines[i+1];
				}
			}
			// 正则表达式来匹配日期格式
			Matcher matcher = PATTERN.matcher(lines[i]);
			if (matcher.find()) {
				// 如果有匹配的
				dateList.add(lines[i].substring(matcher.start(), matcher.end()));
			}
		}

		return new Pair<>(name, dateList);
	}

	private boolean checkTime(Date curTime, Date startTime, Date endTime) {
		// 判断时间是否在范围里
		// Optional来避免null的情况
		curTime = Optional.ofNullable(curTime).orElse(new Date());
		return curTime.after(startTime) && curTime.before(endTime);
	}

	private List<NucleicAcidInfoVO> convertList(List<NucleicAcidInfoPO> list) {
		return list.stream().filter(Objects::nonNull).map(NucleicAcidInfoVO::new).collect(Collectors.toList());
	}

	private void stopTask(long infoId) {
		String taskName = "nucleicAcidTask" + NucleicAcidType.REPORTING.getValue() + infoId;
		if (taskService.containsTask(taskName)) {
			taskService.stopTask(taskName);
		}
	}

	private void addTask(Long infoId, Date newDeadLine) {
		// 加入任务列表
		NucleicAcidTaskVO task = NucleicAcidTaskVO.builder()
			.id(infoId)
			.type(NucleicAcidType.REPORTING)
			.finishStatus(FinishStatus.TO_BE_CONTINUE)
			.deadLine(newDeadLine)
			.build();

		taskService.addUserNucleicAcidTask(task);
	}

	private List<NucleicAcidInfoPO> sortByFinishStatusAndTime(List<NucleicAcidInfoPO> infoPoList) {
		// 根据完成状态和时间来排序
		infoPoList.sort(Comparator.comparing(o -> FinishStatus.getStatusByValue(o.getStatus())));
		// 然后每个里面根据时间排序
		int continueIndex = -1;
		int doneIndex = -1;

		for (int i = 0; i < infoPoList.size(); i++) {
			NucleicAcidInfoPO infoPo = infoPoList.get(i);
			if (continueIndex == -1 && infoPo.getStatus().equals(FinishStatus.TO_BE_CONTINUE.getValue())) {
				continueIndex = i;
			}

			if (infoPo.getStatus().equals(FinishStatus.DONE.getValue())) {
				doneIndex = i;
				break;
			}
		}

		// 然后排序
		infoPoList = sortInProgress(infoPoList, continueIndex, doneIndex);
		infoPoList = sortToBeContinue(infoPoList, continueIndex, doneIndex);
		infoPoList = sortDone(infoPoList, continueIndex, doneIndex);
		return infoPoList;
	}

	private List<NucleicAcidInfoPO> sortInProgress(List<NucleicAcidInfoPO> infoPoList, int continueIndex, int doneIndex) {
		if (continueIndex == 0) {
			// 说明没有进行中的信息
			return infoPoList;
		} else if (continueIndex == -1) {
			// 说明没有未完成的信息
			if (doneIndex == -1) {
				// 说明没有已完成的信息
				// 说明整个列表都是进行中
				infoPoList.sort(Comparator.comparing(NucleicAcidInfoPO::getDeadLine));
				return infoPoList;
			} else {
				return sort(infoPoList, 0, doneIndex);
			}
		} else {
			// 说明有未完成的信息
			// 范围一定是[1, continueIndex]
			return sort(infoPoList, 0, continueIndex);
		}
	}

	private List<NucleicAcidInfoPO> sortToBeContinue(List<NucleicAcidInfoPO> infoPoList, int continueIndex, int doneIndex) {
		if (continueIndex == -1) {
			// 说明没有未完成的信息
			return infoPoList;
		} else {
			if (doneIndex == -1) {
				// 没有已完成的信息
				// 范围: [continueIndex, infoPoList.size())
				return sort(infoPoList, continueIndex, infoPoList.size());
			} else {
				// 范围: [continueIndex, doneIndex)
				return sort(infoPoList, continueIndex, doneIndex);
			}
		}
	}

	private List<NucleicAcidInfoPO> sortDone(List<NucleicAcidInfoPO> infoPoList, int continueIndex, int doneIndex) {
		if (doneIndex == -1) {
			return infoPoList;
		} else {
			return sort(infoPoList, doneIndex, infoPoList.size());
		}
	}

	private List<NucleicAcidInfoPO> sort(List<NucleicAcidInfoPO> infoPoList, int startIndex, int endIndex) {
		// 这里采取的做法是构造一个新的list来替换
		// 因为自己写的排序效率太低了
		List<NucleicAcidInfoPO> sortList = infoPoList.subList(startIndex, endIndex);
		sortList.sort(Comparator.comparing(NucleicAcidInfoPO::getDeadLine));
		sortList.addAll(infoPoList.subList(endIndex, infoPoList.size()));

		List<NucleicAcidInfoPO> resList = infoPoList.subList(0, startIndex);
		resList.addAll(sortList);
		return resList;
	}
}
