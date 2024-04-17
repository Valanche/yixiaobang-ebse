package com.weixin.njuteam.service.impl.nucleic;

import com.weixin.njuteam.config.cache.RedisOperator;
import com.weixin.njuteam.dao.nucleic.NucleicAcidTestingMapper;
import com.weixin.njuteam.entity.po.nucleic.NucleicAcidInfoPO;
import com.weixin.njuteam.entity.po.nucleic.NucleicAcidTestingPO;
import com.weixin.njuteam.entity.po.nucleic.UpdateTestingPO;
import com.weixin.njuteam.entity.vo.nucleic.NucleicAcidTaskVO;
import com.weixin.njuteam.entity.vo.nucleic.NucleicAcidTestingVO;
import com.weixin.njuteam.enums.FinishStatus;
import com.weixin.njuteam.enums.HelpFinishStatus;
import com.weixin.njuteam.enums.NucleicAcidType;
import com.weixin.njuteam.exception.AddTaskException;
import com.weixin.njuteam.exception.UpdateNucleicAcidException;
import com.weixin.njuteam.service.NucleicAcidTestingService;
import com.weixin.njuteam.service.TaskService;
import com.weixin.njuteam.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Zyi
 */
@Service
@CacheConfig(cacheNames = "testing")
public class  NucleicAcidTestingServiceImpl implements NucleicAcidTestingService {

	private static final String CACHE_KEY = "testingUser::";
	private final NucleicAcidTestingMapper testingMapper;
	private final UserService userService;
	private final TaskService taskService;
	private final RedisOperator redisOperator;

	@Autowired
	public NucleicAcidTestingServiceImpl(NucleicAcidTestingMapper testingMapper, UserService userService,
										 TaskService taskService, RedisOperator redisOperator) {
		this.testingMapper = testingMapper;
		this.userService = userService;
		this.taskService = taskService;
		this.redisOperator = redisOperator;
	}

	@Override
	@Cacheable(cacheNames = "testing", key = "#id")
	public NucleicAcidTestingVO queryTestingById(@NotNull Long id) {
		NucleicAcidTestingPO testingPo = testingMapper.queryTestingById(id);
		if (testingPo == null) {
			return null;
		}

		return new NucleicAcidTestingVO(testingPo);
	}

	@Override
	@Cacheable(cacheNames = "testingUser", key = "#openId")
	public List<NucleicAcidTestingVO> queryTestingByUserId(@NotNull String openId) throws SQLException {
		long userId = userService.queryUserId(openId);
		if (userId <= 0) {
			return null;
		}
		List<NucleicAcidTestingPO> testingPoList = testingMapper.queryTestingByUserId(userId);
		testingPoList = sortByFinishStatusAndTime(testingPoList);

		if (testingPoList.isEmpty()) {
			return new ArrayList<>();
		}

		return convertList(testingPoList);
	}

	@Override
	public int queryTestingCount(@NotNull String openId, FinishStatus finishStatus) {
		long userId = userService.queryUserId(openId);
		if (userId <= 0) {
			return -1;
		}

		return testingMapper.queryTestingCount(userId, finishStatus);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	@CachePut(cacheNames = "testing", key = "#testingVo.getId()")
	public NucleicAcidTestingVO insertTesting(NucleicAcidTestingVO testingVo) throws AddTaskException {
		if (testingVo == null) {
			return null;
		}

		if (testingVo.getFinishStatus() == null) {
			testingVo.setFinishStatus("进行中");
		}

		NucleicAcidTestingPO testingPo = new NucleicAcidTestingPO(testingVo);
		int i = testingMapper.insertTesting(testingPo);
		testingVo.setId(testingPo.getId());

		// 加入任务列表
		addTask(testingVo.getId(), testingVo.getEndTime());

		// 删除缓存
		redisOperator.delete(CACHE_KEY + userService.queryUserOpenId(testingVo.getUserId()));

		return i > 0 ? testingVo : null;
	}

	@Override
	@CacheEvict(cacheNames = "testing", key = "#testingVo.getId()")
	public NucleicAcidTestingVO updateTesting(NucleicAcidTestingVO testingVo) {
		if (testingVo == null || testingVo.getId() == null || testingVo.getId() <= 0) {
			return null;
		}
		NucleicAcidTestingPO testingPo = new NucleicAcidTestingPO(testingVo);
		int i = testingMapper.updateTesting(testingPo);

		// delete cache
		String openId = userService.queryUserOpenId(testingVo.getUserId());
		redisOperator.delete(CACHE_KEY + openId);

		return i > 0 ? testingVo : null;
	}

	@Override
	public NucleicAcidTestingVO updateTestingByUserIdAndTitle(NucleicAcidTestingVO testingVo, String oldTitle) {
		if (testingVo == null) {
			return null;
		}
		NucleicAcidTestingPO testingPo = new NucleicAcidTestingPO(testingVo);
		NucleicAcidTestingPO testing = testingMapper.queryTestingByUserIdAndTitle(testingVo.getUserId(), oldTitle);
		// 如果更新了时间
		Date newDeadLine = testingVo.getEndTime();
		if (testing.getFinishStatus().equals(FinishStatus.DONE.getValue()) && newDeadLine != null && newDeadLine.after(new Date(System.currentTimeMillis()))) {
			// 如果将deadLine更新了且时间在未来
			// 需要更新状态
			testingVo.setFinishStatus(HelpFinishStatus.IN_PROGRESS.getValue());
			testingPo.setFinishStatus(HelpFinishStatus.IN_PROGRESS.getValue());
			// 加入任务列表
			addTask(testingVo.getId(), newDeadLine);
		}
		int i = testingMapper.updateTestingByUserIdAndTitle(new UpdateTestingPO(testingPo, oldTitle));

		// delete cache
		String openId = userService.queryUserOpenId(testingVo.getUserId());
		redisOperator.delete(CACHE_KEY + openId);

		return i > 0 ? testingVo : null;
	}

	@Override
	@CacheEvict(cacheNames = "testing", key = "#testingId")
	public NucleicAcidTestingVO updateFinish(@NotNull String openId, @NotNull Long testingId) {
		NucleicAcidTestingVO testingVo = new NucleicAcidTestingVO(testingMapper.queryTestingById(testingId));
		if (testingVo.getUserId() != userService.queryUserId(openId)) {
			return null;
		}

		int i = testingMapper.updateFinish(testingId, FinishStatus.DONE);
		testingVo.setFinishStatus("已完成");

		stopTask(testingId);

		// delete cache
		redisOperator.delete(CACHE_KEY + openId);
		return i > 0 ? testingVo : null;
	}

	@Override
	public boolean updateNotFinish(String title, Long userId) {
		NucleicAcidTestingPO testingPo = testingMapper.queryTestingByUserIdAndTitle(userId, title);
		int i = testingMapper.updateFinish(testingPo.getId(), FinishStatus.TO_BE_CONTINUE);

		if (i <= 0) {
			throw new UpdateNucleicAcidException("update nucleic acid testing info not finish fail!");
		}

		// 删除缓存
		redisOperator.delete("testing::" + testingPo.getId());
		redisOperator.delete(CACHE_KEY + userService.queryUserOpenId(userId));
		return true;
	}

	@Override
	@CacheEvict(cacheNames = "testing", key = "#testingId")
	public NucleicAcidTestingVO openRemind(@NotNull String openId, @NotNull Long testingId, Boolean isOpenRemind) {
		NucleicAcidTestingVO testingVo = new NucleicAcidTestingVO(testingMapper.queryTestingById(testingId));
		if (testingVo.getUserId() != userService.queryUserId(openId)) {
			return null;
		}

		int i = testingMapper.openRemind(testingId, isOpenRemind);
		testingVo.setIsOpenRemind(true);

		// delete cache
		redisOperator.delete(CACHE_KEY + openId);
		return i > 0 ? testingVo : null;
	}

	@Override
	public boolean updateTrueTime(@NotNull Long testingId, Date trueTime) {
		// delete cache
		String openId = userService.queryUserOpenId(testingMapper.queryTestingById(testingId).getUserId());
		redisOperator.delete(CACHE_KEY + openId);

		return testingMapper.updateTrueTime(testingId, trueTime) > 0;
	}

	@Override
	@CacheEvict(cacheNames = "testing", key = "#id")
	public boolean deleteTesting(@NotNull Long id) {
		String openId = userService.queryUserOpenId(testingMapper.queryTestingById(id).getUserId());
		redisOperator.delete(CACHE_KEY + openId);

		int i = testingMapper.deleteTesting(id);
		return i > 0;
	}

	@Override
	public boolean deleteTestingInfoByUserIdAndTitle(Long userId, String title) {
		int i = testingMapper.deleteTestingInfoByUserIdAndTitle(userId, title);
		// 删除缓存
		String openId = userService.queryUserOpenId(userId);
		redisOperator.delete(CACHE_KEY + openId);
		return i > 0;
	}

	private List<NucleicAcidTestingVO> convertList(List<NucleicAcidTestingPO> list) {
		return list.stream().filter(Objects::nonNull).map(NucleicAcidTestingVO::new).collect(Collectors.toList());
	}

	private void stopTask(long testingId) {
		String taskName = "nucleicAcidTask" + NucleicAcidType.TESTING.getValue() + testingId;
		if (taskService.containsTask(taskName)) {
			taskService.stopTask(taskName);
		}
	}

	private void addTask(Long testingId, Date newDeadLine) {
		// 加入任务列表
		NucleicAcidTaskVO task = NucleicAcidTaskVO.builder()
			.id(testingId)
			.type(NucleicAcidType.TESTING)
			.finishStatus(FinishStatus.TO_BE_CONTINUE)
			.deadLine(newDeadLine)
			.build();

		taskService.addUserNucleicAcidTask(task);
	}

	private List<NucleicAcidTestingPO> sortByFinishStatusAndTime(List<NucleicAcidTestingPO> testingPoList) {
		// 根据完成状态和时间来排序
		testingPoList.sort(Comparator.comparing(o -> FinishStatus.getStatusByValue(o.getFinishStatus())));
		// 然后每个里面根据时间排序
		int continueIndex = -1;
		int doneIndex = -1;

		for (int i = 0; i < testingPoList.size(); i++) {
			NucleicAcidTestingPO testingPo = testingPoList.get(i);
			if (continueIndex == -1 && testingPo.getFinishStatus().equals(FinishStatus.TO_BE_CONTINUE.getValue())) {
				continueIndex = i;
			}

			if (testingPo.getFinishStatus().equals(FinishStatus.DONE.getValue())) {
				doneIndex = i;
				break;
			}
		}

		// 然后排序
		testingPoList = sortInProgress(testingPoList, continueIndex, doneIndex);
		testingPoList = sortToBeContinue(testingPoList, continueIndex, doneIndex);
		testingPoList = sortDone(testingPoList, continueIndex, doneIndex);
		return testingPoList;
	}

	private List<NucleicAcidTestingPO> sortInProgress(List<NucleicAcidTestingPO> testingPoList, int continueIndex, int doneIndex) {
		if (continueIndex == 0) {
			// 说明没有进行中的信息
			return testingPoList;
		} else if (continueIndex == -1) {
			// 说明没有未完成的信息
			if (doneIndex == -1) {
				// 说明没有已完成的信息
				// 说明整个列表都是进行中
				testingPoList.sort(Comparator.comparing(NucleicAcidTestingPO::getEndTime));
				return testingPoList;
			} else {
				return sort(testingPoList, 0, doneIndex);
			}
		} else {
			// 说明有未完成的信息
			// 范围一定是[1, continueIndex]
			return sort(testingPoList, 0, continueIndex);
		}
	}

	private List<NucleicAcidTestingPO> sortToBeContinue(List<NucleicAcidTestingPO> testingPoList, int continueIndex, int doneIndex) {
		if (continueIndex == -1) {
			// 说明没有未完成的信息
			return testingPoList;
		} else {
			if (doneIndex == -1) {
				// 没有已完成的信息
				// 范围: [continueIndex, testingPoList.size())
				return sort(testingPoList, continueIndex, testingPoList.size());
			} else {
				// 范围: [continueIndex, doneIndex)
				return sort(testingPoList, continueIndex, doneIndex);
			}
		}
	}

	private List<NucleicAcidTestingPO> sortDone(List<NucleicAcidTestingPO> infoPoList, int continueIndex, int doneIndex) {
		if (doneIndex == -1) {
			return infoPoList;
		} else {
			return sort(infoPoList, doneIndex, infoPoList.size());
		}
	}

	private List<NucleicAcidTestingPO> sort(List<NucleicAcidTestingPO> testingPoList, int startIndex, int endIndex) {
		// 这里采取的做法是构造一个新的list来替换
		// 因为自己写的排序效率太低了
		List<NucleicAcidTestingPO> sortList = testingPoList.subList(startIndex, endIndex);
		sortList.sort(Comparator.comparing(NucleicAcidTestingPO::getEndTime));
		sortList.addAll(testingPoList.subList(endIndex, testingPoList.size()));

		List<NucleicAcidTestingPO> resList = testingPoList.subList(0, startIndex);
		resList.addAll(sortList);
		return resList;
	}
}
