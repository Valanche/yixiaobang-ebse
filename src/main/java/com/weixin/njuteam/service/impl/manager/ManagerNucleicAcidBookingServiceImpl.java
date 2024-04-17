package com.weixin.njuteam.service.impl.manager;

import com.weixin.njuteam.dao.manager.ManagerNucleicAcidBookingMapper;
import com.weixin.njuteam.entity.po.manager.Student;
import com.weixin.njuteam.entity.po.manager.nucleic.ManagerNucleicAcidBookingPO;
import com.weixin.njuteam.entity.vo.manager.ManagerAndStudentVO;
import com.weixin.njuteam.entity.vo.manager.nucleic.ManagerNucleicAcidBookingVO;
import com.weixin.njuteam.entity.vo.nucleic.NucleicAcidBookingVO;
import com.weixin.njuteam.entity.vo.nucleic.NucleicAcidTaskVO;
import com.weixin.njuteam.enums.FinishStatus;
import com.weixin.njuteam.enums.HelpFinishStatus;
import com.weixin.njuteam.enums.NucleicAcidType;
import com.weixin.njuteam.exception.AddNucleicAcidException;
import com.weixin.njuteam.exception.DuplicateTitleException;
import com.weixin.njuteam.exception.UpdateNucleicAcidException;
import com.weixin.njuteam.service.ManagerNucleicAcidBookingService;
import com.weixin.njuteam.service.ManagerService;
import com.weixin.njuteam.service.NucleicAcidBookingService;
import com.weixin.njuteam.service.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Zyi
 */
@Service
@Slf4j
public class ManagerNucleicAcidBookingServiceImpl implements ManagerNucleicAcidBookingService {

	private final ManagerNucleicAcidBookingMapper bookingMapper;
	private final ManagerService managerService;
	private final NucleicAcidBookingService bookingService;
	private final TaskService taskService;

	@Autowired
	public ManagerNucleicAcidBookingServiceImpl(ManagerNucleicAcidBookingMapper bookingMapper,
												ManagerService managerService, NucleicAcidBookingService bookingService,
												TaskService taskService) {
		this.bookingMapper = bookingMapper;
		this.managerService = managerService;
		this.bookingService = bookingService;
		this.taskService = taskService;
	}

	@Override
	public List<ManagerNucleicAcidBookingVO> queryManagerBookingInfoList(Long managerId) {
		List<ManagerNucleicAcidBookingPO> bookingPoList = bookingMapper.queryManagerBookingList(managerId);
		return bookingPoList == null ? null : convertList(bookingPoList);
	}

	@Override
	public ManagerNucleicAcidBookingVO queryBookingInfoByManagerIdAndTitle(Long managerId, String title) {
		ManagerNucleicAcidBookingPO bookingPo = bookingMapper.queryBookingInfoByManagerIdAndTitle(managerId, title);
		return bookingPo == null ? null : new ManagerNucleicAcidBookingVO(bookingPo);
	}

	@Override
	public ManagerNucleicAcidBookingVO queryBookingInfoById(Long id) {
		ManagerNucleicAcidBookingPO bookingPo = bookingMapper.queryBookingById(id);
		return bookingPo == null ? null : new ManagerNucleicAcidBookingVO(bookingPo);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public ManagerNucleicAcidBookingVO insertBookingInfo(Long managerId, ManagerNucleicAcidBookingVO bookingVo) throws DuplicateTitleException {
		if (bookingVo == null) {
			return null;
		}
		bookingVo.setManagerId(managerId);
		// 首先判断有没有相同标题的
		ManagerNucleicAcidBookingPO testBookingPo = bookingMapper.queryBookingInfoByManagerIdAndTitle(managerId, bookingVo.getTitle());
		if (testBookingPo != null) {
			throw new DuplicateTitleException("title duplicate!");
		}
		ManagerNucleicAcidBookingPO bookingPo = new ManagerNucleicAcidBookingPO(bookingVo);
		int i = bookingMapper.insertBooking(bookingPo);
		bookingVo.setId(bookingPo.getId());

		// 同时给对应的学生插入数据
		if (i <= 0) {
			return null;
		}
		alterUserBookingInfo(bookingVo, true, null);

		// 加入任务列表
		addTask(bookingVo.getId(), bookingVo.getDeadLine());

		return bookingVo;
	}

	@Override
	public ManagerNucleicAcidBookingVO updateBookingInfo(Long managerId, ManagerNucleicAcidBookingVO bookingVo, String oldTitle) throws DuplicateTitleException {
		if (bookingVo == null || bookingVo.getId() == null || bookingVo.getId() <= 0) {
			return null;
		}

		// 首先判断是否有标题相同的
		if (bookingVo.getTitle() != null) {
			ManagerNucleicAcidBookingPO titleTestPo = bookingMapper.queryBookingInfoByManagerIdAndTitle(managerId, bookingVo.getTitle());
			if (titleTestPo != null) {
				throw new DuplicateTitleException("title duplicate");
			}
		}
		bookingVo.setManagerId(managerId);
		ManagerNucleicAcidBookingPO bookingPo = new ManagerNucleicAcidBookingPO(bookingVo);
		ManagerNucleicAcidBookingPO booking = bookingMapper.queryBookingById(bookingVo.getId());
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
		int i = bookingMapper.updateBooking(bookingPo);

		// 同时给对应的学生插入数据
		if (i <= 0) {
			return null;
		}
		alterUserBookingInfo(bookingVo, false, oldTitle);

		return bookingVo;
	}

	@Override
	public boolean deleteBookingInfoById(Long managerId, Long bookingId) {
		ManagerNucleicAcidBookingPO bookingPo = bookingMapper.queryBookingById(bookingId);
		if (bookingPo == null) {
			return false;
		}
		int i = bookingMapper.deleteBookingById(bookingId);
		// 同时需要删除学生对应的
		// 获取管理员对应的学生
		ManagerAndStudentVO studentVo = managerService.queryStudentList(managerId);
		List<Student> studentList = studentVo.getStudentList();

		for (Student student : studentList) {
			boolean isDeleted = bookingService.deleteBookingInfoByUserIdAndTitle(student.getUserId(), bookingPo.getTitle());

			if (!isDeleted) {
				return false;
			}
		}

		return i > 0;
	}

	@Override
	public boolean deleteManagerAllBookingInfo(Long managerId) {
		List<ManagerNucleicAcidBookingVO> infoVoList = queryManagerBookingInfoList(managerId);

		for (ManagerNucleicAcidBookingVO infoVo : infoVoList) {
			boolean isDeleted = deleteBookingInfoById(managerId, infoVo.getId());

			if (!isDeleted) {
				return false;
			}
		}

		return true;
	}

	private List<ManagerNucleicAcidBookingVO> convertList(List<ManagerNucleicAcidBookingPO> bookingPoList) {
		return bookingPoList.stream().map(ManagerNucleicAcidBookingVO::new).collect(Collectors.toList());
	}

	private void alterUserBookingInfo(ManagerNucleicAcidBookingVO bookingVo, boolean isInsert, String oldTitle) throws AddNucleicAcidException, UpdateNucleicAcidException {
		ManagerAndStudentVO studentVo = managerService.queryStudentList(bookingVo.getManagerId());
		List<Student> studentList = studentVo.getStudentList();

		NucleicAcidBookingVO userBookingInfo = NucleicAcidBookingVO.builder()
			.managerId(bookingVo.getManagerId())
			.deadLine(bookingVo.getDeadLine())
			.title(bookingVo.getTitle())
			.build();

		for (Student student : studentList) {
			// 给对应的用户插入数据
			userBookingInfo.setUserId(student.getUserId());
			NucleicAcidBookingVO booking;
			if (isInsert) {
				booking = bookingService.insertBooking(userBookingInfo);
				if (booking == null) {
					throw new AddNucleicAcidException("add testing info error!");
				}
			} else {
				// 通过user id和title来更新
				booking = bookingService.updateBookingByUserIdAndTitle(userBookingInfo, oldTitle);
				if (booking == null) {
					throw new UpdateNucleicAcidException("update testing info error!");
				}
			}
		}
	}

	private void addTask(Long bookingId, Date newDeadLine) {
		// 加入任务列表
		NucleicAcidTaskVO task = NucleicAcidTaskVO.builder()
			.id(bookingId)
			.type(NucleicAcidType.BOOKING)
			.finishStatus(FinishStatus.DONE)
			.deadLine(newDeadLine)
			.build();

		taskService.addManagerNucleicAcidTask(task);
	}
}
