package com.weixin.njuteam.service.impl.manager;

import com.weixin.njuteam.dao.manager.ManagerNucleicAcidTestingMapper;
import com.weixin.njuteam.entity.po.manager.Student;
import com.weixin.njuteam.entity.po.manager.nucleic.ManagerNucleicAcidTestingPO;
import com.weixin.njuteam.entity.vo.manager.ManagerAndStudentVO;
import com.weixin.njuteam.entity.vo.manager.nucleic.ManagerNucleicAcidBookingVO;
import com.weixin.njuteam.entity.vo.manager.nucleic.ManagerNucleicAcidInfoVO;
import com.weixin.njuteam.entity.vo.manager.nucleic.ManagerNucleicAcidTestingVO;
import com.weixin.njuteam.entity.vo.nucleic.NucleicAcidTaskVO;
import com.weixin.njuteam.entity.vo.nucleic.NucleicAcidTestingVO;
import com.weixin.njuteam.enums.FinishStatus;
import com.weixin.njuteam.enums.HelpFinishStatus;
import com.weixin.njuteam.enums.NucleicAcidType;
import com.weixin.njuteam.exception.AddNucleicAcidException;
import com.weixin.njuteam.exception.DuplicateTitleException;
import com.weixin.njuteam.exception.UpdateNucleicAcidException;
import com.weixin.njuteam.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 插入核酸检测通知时同时插入核酸预约通知
 *
 * @author Zyi
 */
@Service
@Slf4j
public class ManagerNucleicAcidTestingServiceImpl implements ManagerNucleicAcidTestingService {

	private final ManagerNucleicAcidTestingMapper testingMapper;
	private final ManagerService managerService;
	private final ManagerNucleicAcidBookingService bookingService;
	private final ManagerNucleicAcidInfoService infoService;
	private final NucleicAcidTestingService testingService;
	private final TaskService taskService;

	@Autowired
	public ManagerNucleicAcidTestingServiceImpl(ManagerNucleicAcidTestingMapper testingMapper, ManagerService managerService,
												ManagerNucleicAcidBookingService bookingService, NucleicAcidTestingService testingService,
												ManagerNucleicAcidInfoService infoService, TaskService taskService) {
		this.testingMapper = testingMapper;
		this.managerService = managerService;
		this.bookingService = bookingService;
		this.infoService = infoService;
		this.testingService = testingService;
		this.taskService = taskService;
	}

	@Override
	public List<ManagerNucleicAcidTestingVO> queryManagerTestingInfoList(Long managerId) {
		List<ManagerNucleicAcidTestingPO> testingPoList = testingMapper.queryManagerTestingList(managerId);
		return testingPoList == null ? null : convertList(testingPoList);
	}

	@Override
	public ManagerNucleicAcidTestingVO queryTestingInfoById(Long id) {
		ManagerNucleicAcidTestingPO testingPo = testingMapper.queryTestingById(id);
		return testingPo == null ? null : new ManagerNucleicAcidTestingVO(testingPo);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public ManagerNucleicAcidTestingVO insertTestingInfo(Long managerId, ManagerNucleicAcidTestingVO testingVo) throws AddNucleicAcidException, DuplicateTitleException {
		// 插入检测通知时同时插入预约通知
		if (testingVo == null) {
			return null;
		}
		testingVo.setManagerId(managerId);
		ManagerNucleicAcidTestingPO testingTitlePo = testingMapper.queryTestingByManagerIdAndTitle(managerId, testingVo.getTitle());
		if (testingTitlePo != null) {
			throw new DuplicateTitleException("title duplicate");
		}
		ManagerNucleicAcidTestingPO testingPo = new ManagerNucleicAcidTestingPO(testingVo);
		int i = testingMapper.insertTestingInfo(testingPo);
		testingVo.setId(testingPo.getId());

		// 同时给对应的学生插入数据
		if (i <= 0) {
			return null;
		}
		alterUserTestingInfo(testingVo, true, null);
		// 加入任务列表,更新完成时间
		addTask(testingVo.getId(), testingVo.getEndTime());

		// 插入预约通知
		ManagerNucleicAcidBookingVO insertBookingVo = insertBooking(managerId, testingVo.getEndTime(), testingVo.getTitle());
		ManagerNucleicAcidInfoVO insertInfoVo = insertInfoVo(managerId, getInfoTime(testingVo.getEndTime()), testingVo.getTitle());
		return insertBookingVo != null && insertInfoVo != null ? testingVo : null;
	}

	@Override
	public ManagerNucleicAcidTestingVO updateTestingInfo(Long managerId, ManagerNucleicAcidTestingVO testingVo, String oldTitle) throws UpdateNucleicAcidException, DuplicateTitleException {
		// 插入检测通知时同时插入预约通知
		if (testingVo == null) {
			return null;
		}

		// 首先判断是否有标题相同的
		if (testingVo.getTitle() != null) {
			ManagerNucleicAcidTestingPO titleTestPo = testingMapper.queryTestingByManagerIdAndTitle(managerId, testingVo.getTitle());
			if (titleTestPo != null) {
				throw new DuplicateTitleException("title duplicate");
			}
		}
		testingVo.setManagerId(managerId);
		ManagerNucleicAcidTestingPO testingPo = new ManagerNucleicAcidTestingPO(testingVo);
		ManagerNucleicAcidTestingPO testing = testingMapper.queryTestingById(testingVo.getId());
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
		int i = testingMapper.updateTestingInfo(testingPo);

		// 同时给对应的学生插入数据
		if (i <= 0) {
			return null;
		}
		// 给学生更新对应的检测信息
		alterUserTestingInfo(testingVo, false, oldTitle);

		// 更新预约
		ManagerNucleicAcidBookingVO updateBookingVo = updateBooking(managerId, oldTitle, testingVo.getEndTime(), testingVo.getTitle());
		ManagerNucleicAcidInfoVO updateInfoVo = updateInfo(managerId, oldTitle, getInfoTime(testingVo.getEndTime()), testingVo.getTitle());
		return updateBookingVo != null && updateInfoVo != null ? testingVo : null;
	}

	@Override
	public boolean deleteTestingInfoById(Long managerId, Long testingId) {
		ManagerNucleicAcidTestingPO testingPo = testingMapper.queryTestingById(testingId);
		if (testingPo == null) {
			return false;
		}
		int i = testingMapper.deleteTestingInfoById(testingId);
		// 同时需要删除学生对应的
		// 获取管理员对应的学生
		ManagerAndStudentVO studentVo = managerService.queryStudentList(managerId);
		List<Student> studentList = studentVo.getStudentList();

		for (Student student : studentList) {
			boolean isDeleted = testingService.deleteTestingInfoByUserIdAndTitle(student.getUserId(), testingPo.getTitle());

			if (!isDeleted) {
				return false;
			}
		}


		boolean isDeleteBooking = deleteBooking(managerId, testingPo.getTitle());
		boolean isDeleteReporting = deleteInfo(managerId, testingPo.getTitle());

		return i > 0 && isDeleteBooking && isDeleteReporting;
	}

	@Override
	public boolean deleteManagerAllTestingInfo(Long managerId) {
		List<ManagerNucleicAcidTestingVO> infoVoList = queryManagerTestingInfoList(managerId);

		for (ManagerNucleicAcidTestingVO infoVo : infoVoList) {
			boolean isDeleted = deleteTestingInfoById(managerId, infoVo.getId());

			if (!isDeleted) {
				return false;
			}
		}

		return true;
	}

	private List<ManagerNucleicAcidTestingVO> convertList(List<ManagerNucleicAcidTestingPO> testingPoList) {
		return testingPoList.stream().map(ManagerNucleicAcidTestingVO::new).collect(Collectors.toList());
	}

	private void alterUserTestingInfo(ManagerNucleicAcidTestingVO testingVo, boolean isInsert, String oldTitle) throws UpdateNucleicAcidException, AddNucleicAcidException {
		ManagerAndStudentVO studentVo = managerService.queryStudentList(testingVo.getManagerId());
		List<Student> studentList = studentVo.getStudentList();

		NucleicAcidTestingVO userTestingInfoVo = NucleicAcidTestingVO.builder()
			.managerId(testingVo.getManagerId())
			.require(testingVo.getRequire())
			.place(testingVo.getPlace())
			.startTime(testingVo.getStartTime())
			.endTime(testingVo.getEndTime())
			.title(testingVo.getTitle())
			.build();

		for (Student student : studentList) {
			// 给对应的用户插入数据
			userTestingInfoVo.setUserId(student.getUserId());
			NucleicAcidTestingVO testingInfo;
			if (isInsert) {
				testingInfo = testingService.insertTesting(userTestingInfoVo);
				if (testingInfo == null) {
					throw new AddNucleicAcidException("add testing info error!");
				}
			} else {
				testingInfo = testingService.updateTestingByUserIdAndTitle(userTestingInfoVo, oldTitle);
				if (testingInfo == null) {
					throw new UpdateNucleicAcidException("update testing info error!");
				}
			}
		}
	}

	private ManagerNucleicAcidBookingVO insertBooking(Long managerId, Date deadLine, String title) {
		ManagerNucleicAcidBookingVO bookingVo = ManagerNucleicAcidBookingVO.builder()
			.deadLine(deadLine)
			.title(title)
			.build();

		return bookingService.insertBookingInfo(managerId, bookingVo);
	}

	private ManagerNucleicAcidBookingVO updateBooking(Long managerId, String oldTitle, Date deadLine, String newTitle) {
		ManagerNucleicAcidBookingVO booking = bookingService.queryBookingInfoByManagerIdAndTitle(managerId, oldTitle);
		ManagerNucleicAcidBookingVO bookingVo = ManagerNucleicAcidBookingVO.builder()
			.id(booking.getId())
			.deadLine(deadLine)
			.title(newTitle)
			.build();

		return bookingService.updateBookingInfo(managerId, bookingVo, oldTitle);
	}

	private ManagerNucleicAcidInfoVO insertInfoVo(Long managerId, Date deadLine, String title) {
		ManagerNucleicAcidInfoVO infoVo = ManagerNucleicAcidInfoVO.builder()
			.deadLine(deadLine)
			.title(title).build();

		return infoService.insertInfo(managerId, infoVo);
	}

	private ManagerNucleicAcidInfoVO updateInfo(Long managerId, String oldTitle, Date deadLine, String newTitle) {
		ManagerNucleicAcidInfoVO info = infoService.queryInfoByManagerIdAndTitle(managerId, oldTitle);
		ManagerNucleicAcidInfoVO infoVo = ManagerNucleicAcidInfoVO.builder()
			.id(info.getId())
			.deadLine(deadLine)
			.title(newTitle)
			.build();

		return infoService.updateInfo(managerId, infoVo, oldTitle);
	}

	private Date getInfoTime(Date testingEndTime) {
		// 上报时间在检测时间 + 24h(86400000ms)
		if (testingEndTime == null) {
			return null;
		}
		return new Date(testingEndTime.getTime() + 86400000);
	}

	private void addTask(Long testingId, Date newDeadLine) {
		// 加入任务列表
		NucleicAcidTaskVO task = NucleicAcidTaskVO.builder()
			.id(testingId)
			.type(NucleicAcidType.TESTING)
			.finishStatus(FinishStatus.DONE)
			.deadLine(newDeadLine)
			.build();

		taskService.addManagerNucleicAcidTask(task);
	}

	private boolean deleteBooking(Long managerId, String title) {
		ManagerNucleicAcidBookingVO bookingVo = bookingService.queryBookingInfoByManagerIdAndTitle(managerId, title);
		return bookingService.deleteBookingInfoById(managerId, bookingVo.getId());
	}

	private boolean deleteInfo(Long managerId, String title) {
		ManagerNucleicAcidInfoVO infoVo = infoService.queryInfoByManagerIdAndTitle(managerId, title);
		return infoService.deleteInfoById(managerId, infoVo.getId());
	}
}
