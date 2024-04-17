package com.weixin.njuteam.service.impl.nucleic;

import com.weixin.njuteam.config.cache.RedisOperator;
import com.weixin.njuteam.dao.nucleic.NucleicAcidBookingMapper;
import com.weixin.njuteam.entity.po.nucleic.NucleicAcidBookingPO;
import com.weixin.njuteam.entity.po.nucleic.NucleicAcidInfoPO;
import com.weixin.njuteam.entity.po.nucleic.UpdateBookingPO;
import com.weixin.njuteam.entity.vo.nucleic.NucleicAcidBookingVO;
import com.weixin.njuteam.entity.vo.nucleic.NucleicAcidTaskVO;
import com.weixin.njuteam.enums.FinishStatus;
import com.weixin.njuteam.enums.HelpFinishStatus;
import com.weixin.njuteam.enums.NucleicAcidType;
import com.weixin.njuteam.exception.AddTaskException;
import com.weixin.njuteam.service.NucleicAcidBookingService;
import com.weixin.njuteam.service.TaskService;
import com.weixin.njuteam.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
@Slf4j
public class NucleicAcidBookingServiceImpl implements NucleicAcidBookingService {

	private static final String CACHE_KEY = "bookingUser::";
	private final NucleicAcidBookingMapper bookingMapper;
	private final TaskService taskService;
	private final UserService userService;
	private final RedisOperator redisOperator;

	@Autowired
	public NucleicAcidBookingServiceImpl(NucleicAcidBookingMapper bookingMapper, TaskService taskService,
										 UserService userService, RedisOperator redisOperator) {
		this.bookingMapper = bookingMapper;
		this.taskService = taskService;
		this.userService = userService;
		this.redisOperator = redisOperator;
	}

	@Override
	@Cacheable(cacheNames = "booking", key = "#id")
	public NucleicAcidBookingVO queryBookingById(@NotNull Long id) {
		if (id <= 0) {
			return null;
		}

		NucleicAcidBookingPO bookingPo = bookingMapper.queryBookingById(id);
		if (bookingPo == null) {
			// beanUtils can not convert null
			return null;
		}

		return new NucleicAcidBookingVO(bookingPo);
	}

	@Override
	@Cacheable(cacheNames = "bookingUser", key = "#openId")
	public List<NucleicAcidBookingVO> queryBookingByUserId(@NotNull String openId) throws SQLException {
		long userId = userService.queryUserId(openId);
		if (userId <= 0) {
			return new ArrayList<>();
		}

		List<NucleicAcidBookingPO> bookingPoList = bookingMapper.queryBookingByUserId(userId);
		bookingPoList = sortByFinishStatusAndTime(bookingPoList);
		if (bookingPoList.isEmpty()) {
			return new ArrayList<>();
		}

		return convertList(bookingPoList);
	}

	@Override
	public int queryBookingCount(@NotNull String openId, FinishStatus finishStatus) {
		long userId = userService.queryUserId(openId);
		if (userId <= 0) {
			return -1;
		}

		return bookingMapper.queryBookingCount(userId, finishStatus);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	@CachePut(cacheNames = "booking", key = "#bookingVo.getId()")
	public NucleicAcidBookingVO insertBooking(NucleicAcidBookingVO bookingVo) throws AddTaskException {
		if (bookingVo == null || bookingVo.getUserId() == null || bookingVo.getUserId() <= 0) {
			return null;
		}

		if (bookingVo.getFinishStatus() == null) {
			bookingVo.setFinishStatus("进行中");
		}

		NucleicAcidBookingPO bookingPo = new NucleicAcidBookingPO(bookingVo);
		int i = bookingMapper.insertBooking(bookingPo);
		bookingVo.setId(bookingPo.getId());

		// 加入任务列表
		addTask(bookingVo.getId(), bookingVo.getDeadLine());

		// 删除缓存
		redisOperator.delete(CACHE_KEY + userService.queryUserOpenId(bookingVo.getUserId()));

		return i > 0 ? bookingVo : null;
	}

	@Override
	@CacheEvict(cacheNames = "booking", key = "#bookingVo.getId()")
	public NucleicAcidBookingVO updateBooking(NucleicAcidBookingVO bookingVo) {
		if (bookingVo == null || bookingVo.getId() == null || bookingVo.getId() <= 0) {
			return null;
		}

		NucleicAcidBookingPO bookingPo = new NucleicAcidBookingPO(bookingVo);
		int i = bookingMapper.updateBooking(bookingPo);
		// 删除缓存
		redisOperator.delete(CACHE_KEY + userService.queryUserOpenId(bookingVo.getUserId()));

		return i > 0 ? bookingVo : null;
	}

	@Override
	public NucleicAcidBookingVO updateBookingByUserIdAndTitle(NucleicAcidBookingVO bookingVo, String oldTitle) {
		if (bookingVo == null) {
			return null;
		}

		NucleicAcidBookingPO bookingPo = new NucleicAcidBookingPO(bookingVo);
		NucleicAcidBookingPO booking = bookingMapper.queryBookingByUserIdAndTitle(bookingVo.getUserId(), oldTitle);
		// 如果更新了时间
		Date newDeadLine = bookingVo.getDeadLine();
		if (booking.getFinishStatus().equals(FinishStatus.DONE.getValue()) && newDeadLine != null && newDeadLine.after(new Date(System.currentTimeMillis()))) {
			// 如果将deadLine更新了且时间在未来
			// 需要更新状态
			bookingVo.setFinishStatus(HelpFinishStatus.IN_PROGRESS.getValue());
			bookingPo.setFinishStatus(HelpFinishStatus.IN_PROGRESS.getValue());
			// 加入任务列表
			addTask(bookingVo.getId(), newDeadLine);
		}
		int i = bookingMapper.updateBookingByUserIdAndTitle(new UpdateBookingPO(bookingPo, oldTitle));
		// 删除缓存
		redisOperator.delete(CACHE_KEY + userService.queryUserOpenId(bookingVo.getUserId()));

		return i > 0 ? bookingVo : null;
	}

	@Override
	@CacheEvict(cacheNames = "booking", key = "#bookingId")
	public NucleicAcidBookingVO updateFinish(@NotNull String openId, @NotNull Long bookingId) {
		if (bookingId <= 0) {
			return null;
		}

		NucleicAcidBookingVO bookingVo = new NucleicAcidBookingVO(bookingMapper.queryBookingById(bookingId));
		if (bookingVo.getUserId() != userService.queryUserId(openId)) {
			return null;
		}

		int i = bookingMapper.updateFinish(bookingId, FinishStatus.DONE);
		bookingVo.setFinishStatus("已完成");

		// 停止任务
		String taskName = "nucleicAcidTask" + NucleicAcidType.BOOKING.getValue() + bookingVo.getId();
		if (taskService.containsTask(taskName)) {
			taskService.stopTask(taskName);
		}

		redisOperator.delete(CACHE_KEY + userService.queryUserOpenId(bookingVo.getUserId()));
		return i > 0 ? bookingVo : null;
	}

	@Override
	@CacheEvict(cacheNames = "booking", key = "#bookingId")
	public NucleicAcidBookingVO openRemind(@NotNull String openId, @NotNull Long bookingId, Boolean isOpenRemind) {
		NucleicAcidBookingVO bookingVo = new NucleicAcidBookingVO(bookingMapper.queryBookingById(bookingId));
		if (bookingVo.getUserId() != userService.queryUserId(openId)) {
			return null;
		}

		int i = bookingMapper.openRemind(bookingId, isOpenRemind);
		bookingVo.setIsOpenRemind(true);

		redisOperator.delete(CACHE_KEY + userService.queryUserOpenId(bookingVo.getUserId()));
		return i > 0 ? bookingVo : null;
	}

	@Override
	@CacheEvict(cacheNames = "booking", key = "#id")
	public boolean deleteBooking(@NotNull Long id) {
		// 删除缓存
		String openId = userService.queryUserOpenId(bookingMapper.queryBookingById(id).getUserId());
		redisOperator.delete(CACHE_KEY + openId);

		int i = bookingMapper.deleteBooking(id);
		return i > 0;
	}

	@Override
	public boolean deleteBookingInfoByUserIdAndTitle(Long userId, String title) {
		int i = bookingMapper.deleteBookingInfoByUserIdAndTitle(userId, title);
		// 删除缓存
		String openId = userService.queryUserOpenId(userId);
		redisOperator.delete(CACHE_KEY + openId);
		return i > 0;
	}

	private List<NucleicAcidBookingVO> convertList(List<NucleicAcidBookingPO> list) {
		return list.stream().filter(Objects::nonNull).map(NucleicAcidBookingVO::new).collect(Collectors.toList());
	}

	private void addTask(Long bookingId, Date newDeadLine) {
		// 加入任务列表
		NucleicAcidTaskVO task = NucleicAcidTaskVO.builder()
			.id(bookingId)
			.type(NucleicAcidType.BOOKING)
			.finishStatus(FinishStatus.TO_BE_CONTINUE)
			.deadLine(newDeadLine)
			.build();

		taskService.addUserNucleicAcidTask(task);
	}

	private List<NucleicAcidBookingPO> sortByFinishStatusAndTime(List<NucleicAcidBookingPO> bookingPoList) {
		// 根据完成状态和时间来排序
		bookingPoList.sort(Comparator.comparing(o -> FinishStatus.getStatusByValue(o.getFinishStatus())));
		// 然后每个里面根据时间排序
		int continueIndex = -1;
		int doneIndex = -1;

		for (int i = 0; i < bookingPoList.size(); i++) {
			NucleicAcidBookingPO bookingPo = bookingPoList.get(i);
			if (continueIndex == -1 && bookingPo.getFinishStatus().equals(FinishStatus.TO_BE_CONTINUE.getValue())) {
				continueIndex = i;
			}

			if (bookingPo.getFinishStatus().equals(FinishStatus.DONE.getValue())) {
				doneIndex = i;
				break;
			}
		}

		// 然后排序
		bookingPoList = sortInProgress(bookingPoList, continueIndex, doneIndex);
		bookingPoList = sortToBeContinue(bookingPoList, continueIndex, doneIndex);
		bookingPoList = sortDone(bookingPoList, continueIndex, doneIndex);
		return bookingPoList;
	}

	private List<NucleicAcidBookingPO> sortInProgress(List<NucleicAcidBookingPO> bookingPoList, int continueIndex, int doneIndex) {
		if (continueIndex == 0) {
			// 说明没有进行中的信息
			return bookingPoList;
		} else if (continueIndex == -1) {
			// 说明没有未完成的信息
			if (doneIndex == -1) {
				// 说明没有已完成的信息
				// 说明整个列表都是进行中
				bookingPoList.sort(Comparator.comparing(NucleicAcidBookingPO::getDeadLine));
				return bookingPoList;
			} else {
				return sort(bookingPoList, 0, doneIndex);
			}
		} else {
			// 说明有未完成的信息
			// 范围一定是[0, continueIndex)
			return sort(bookingPoList, 0, continueIndex);
		}
	}

	private List<NucleicAcidBookingPO> sortToBeContinue(List<NucleicAcidBookingPO> bookingPoList, int continueIndex, int doneIndex) {
		if (continueIndex == -1) {
			// 说明没有未完成的信息
			return bookingPoList;
		} else {
			if (doneIndex == -1) {
				// 没有已完成的信息
				// 范围: [continueIndex, bookingPoList.size())
				return sort(bookingPoList, continueIndex, bookingPoList.size());
			} else {
				// 范围: [continueIndex, doneIndex)
				return sort(bookingPoList, continueIndex, doneIndex);
			}
		}
	}

	private List<NucleicAcidBookingPO> sortDone(List<NucleicAcidBookingPO> bookingPoList, int continueIndex, int doneIndex) {
		if (doneIndex == -1) {
			return bookingPoList;
		} else {
			return sort(bookingPoList, doneIndex, bookingPoList.size());
		}
	}

	private List<NucleicAcidBookingPO> sort(List<NucleicAcidBookingPO> bookingPoList, int startIndex, int endIndex) {
		// 这里采取的做法是构造一个新的list来替换
		// 因为自己写的排序效率太低了
		List<NucleicAcidBookingPO> sortList = bookingPoList.subList(startIndex, endIndex);
		sortList.sort(Comparator.comparing(NucleicAcidBookingPO::getDeadLine));
		sortList.addAll(bookingPoList.subList(endIndex, bookingPoList.size()));

		List<NucleicAcidBookingPO> resList = bookingPoList.subList(0, startIndex);
		resList.addAll(sortList);
		return resList;
	}
}
