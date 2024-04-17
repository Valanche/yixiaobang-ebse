package com.weixin.njuteam.service.impl.manager;

import com.weixin.njuteam.dao.manager.ManagerNucleicAcidInfoMapper;
import com.weixin.njuteam.entity.po.manager.Student;
import com.weixin.njuteam.entity.po.manager.nucleic.ManagerAndStudentInfoPO;
import com.weixin.njuteam.entity.po.manager.nucleic.ManagerNucleicAcidInfoPO;
import com.weixin.njuteam.entity.po.manager.nucleic.StudentReportingInfo;
import com.weixin.njuteam.entity.vo.manager.ManagerAndStudentVO;
import com.weixin.njuteam.entity.vo.manager.nucleic.ManagerAndStudentInfoVO;
import com.weixin.njuteam.entity.vo.manager.nucleic.ManagerNucleicAcidInfoVO;
import com.weixin.njuteam.entity.vo.nucleic.NucleicAcidInfoVO;
import com.weixin.njuteam.entity.vo.nucleic.NucleicAcidTaskVO;
import com.weixin.njuteam.enums.FinishStatus;
import com.weixin.njuteam.enums.HelpFinishStatus;
import com.weixin.njuteam.enums.NucleicAcidType;
import com.weixin.njuteam.exception.AddNucleicAcidException;
import com.weixin.njuteam.exception.DuplicateTitleException;
import com.weixin.njuteam.exception.NoStudentException;
import com.weixin.njuteam.exception.UpdateNucleicAcidException;
import com.weixin.njuteam.service.ManagerNucleicAcidInfoService;
import com.weixin.njuteam.service.ManagerService;
import com.weixin.njuteam.service.NucleicAcidInfoService;
import com.weixin.njuteam.service.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Zyi
 */
@Service
@Slf4j
public class ManagerNucleicAcidInfoServiceImpl implements ManagerNucleicAcidInfoService {

	private static final String URL_PREFIX = "https://xjk-advisor.com/NAImg/";
	private final ManagerNucleicAcidInfoMapper infoMapper;
	private final ManagerService managerService;
	private final NucleicAcidInfoService infoService;
	private final TaskService taskService;

	@Autowired
	public ManagerNucleicAcidInfoServiceImpl(ManagerNucleicAcidInfoMapper infoMapper,
											 ManagerService managerService,
											 NucleicAcidInfoService infoService, TaskService taskService) {
		this.infoMapper = infoMapper;
		this.managerService = managerService;
		this.infoService = infoService;
		this.taskService = taskService;
	}

	@Override
	public List<ManagerNucleicAcidInfoVO> queryManagerInfoList(@NotNull Long managerId) {
		List<ManagerNucleicAcidInfoPO> infoPoList = infoMapper.queryManagerInfoList(managerId);
		// 这里其实不会返回null 因为Mybatis对List如果没有找到对应的元素，返回的是个空列表
		if (infoPoList == null) {
			return new ArrayList<>();
		}

		return convertList(infoPoList);
	}

	@Override
	public ManagerNucleicAcidInfoVO queryInfoById(@NotNull Long id) {
		ManagerNucleicAcidInfoPO infoPo = infoMapper.queryInfoById(id);
		return infoPo == null ? null : new ManagerNucleicAcidInfoVO(infoPo);
	}

	@Override
	public ManagerAndStudentInfoVO queryStudentReportingInfo(@NotNull Long managerId, @NotNull Long infoId) throws NoStudentException {
		// 先看是否有学生
		ManagerAndStudentVO studentList = managerService.queryStudentList(managerId);
		if (studentList == null || studentList.getStudentList().isEmpty()) {
			// 说明管理员没有对应学生
			throw new NoStudentException();
		}

		ManagerAndStudentInfoPO infoPo = infoMapper.queryStudentReportingInfo(infoId, managerId);
		// 然后加上前缀
		if (infoPo == null) {
			return null;
		}
		List<StudentReportingInfo> reportInfoList = infoPo.getStudentList();

		for (StudentReportingInfo reportingInfo : reportInfoList) {
			reportingInfo.setImageName(URL_PREFIX + reportingInfo.getImageName());
		}


		return new ManagerAndStudentInfoVO(infoPo);
	}

	@Override
	public ManagerNucleicAcidInfoVO queryInfoByManagerIdAndTitle(Long managerId, String title) {
		ManagerNucleicAcidInfoPO infoPo = infoMapper.queryInfoByManagerIdAndTitle(managerId, title);
		return infoPo == null ? null : new ManagerNucleicAcidInfoVO(infoPo);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public ManagerNucleicAcidInfoVO insertInfo(@NotNull Long managerId, ManagerNucleicAcidInfoVO infoVo) throws AddNucleicAcidException, DuplicateTitleException {
		// 插入需要给用户添加相同的插入数据
		if (infoVo == null) {
			return null;
		}
		infoVo.setManagerId(managerId);
		// 首先判断有没有相同标题的
		ManagerNucleicAcidInfoPO testInfoPo = infoMapper.queryInfoByManagerIdAndTitle(managerId, infoVo.getTitle());
		if (testInfoPo != null) {
			throw new DuplicateTitleException("title duplicate!");
		}
		ManagerNucleicAcidInfoPO infoPo = new ManagerNucleicAcidInfoPO(infoVo);
		int i = infoMapper.insertInfo(infoPo);
		infoVo.setId(infoPo.getId());

		if (i <= 0) {
			return null;
		}
		alterUserInfo(infoVo, true, null);

		// 加入任务列表
		addTask(infoVo.getId(), infoVo.getDeadLine());

		return new ManagerNucleicAcidInfoVO(infoPo);
	}

	@Override
	public ManagerNucleicAcidInfoVO updateInfo(Long managerId, ManagerNucleicAcidInfoVO infoVo, String oldTitle) throws UpdateNucleicAcidException, DuplicateTitleException {
		// 更新的同时也要更新对应用户的信息
		if (infoVo == null) {
			return null;
		}
		// 首先判断是否有标题相同的
		if (infoVo.getTitle() != null) {
			ManagerNucleicAcidInfoPO titleTestPo = infoMapper.queryInfoByManagerIdAndTitle(managerId, infoVo.getTitle());
			if (titleTestPo != null) {
				throw new DuplicateTitleException("title duplicate");
			}
		}
		infoVo.setManagerId(managerId);
		ManagerNucleicAcidInfoPO infoPo = new ManagerNucleicAcidInfoPO(infoVo);
		ManagerNucleicAcidInfoPO info = infoMapper.queryInfoById(infoVo.getId());
		// 如果更新了时间
		Date newDeadLine = infoVo.getDeadLine();
		if (info.getFinishStatus().equals(FinishStatus.DONE.getValue()) && newDeadLine != null && newDeadLine.after(new Date(System.currentTimeMillis()))) {
			// 如果将deadLine更新了且时间在未来
			// 需要更新状态
			infoVo.setFinishStatus(HelpFinishStatus.IN_PROGRESS.getValue());
			infoPo.setFinishStatus(HelpFinishStatus.IN_PROGRESS.getValue());
			// 加入任务列表
			addTask(infoVo.getId(), newDeadLine);
		}
		int i = infoMapper.updateInfo(infoPo);

		if (i <= 0) {
			return null;
		}
		alterUserInfo(infoVo, false, oldTitle);

		return new ManagerNucleicAcidInfoVO(infoPo);
	}

	@Override
	public boolean deleteInfoById(Long managerId, Long infoId) {
		ManagerNucleicAcidInfoPO infoPo = infoMapper.queryInfoById(infoId);
		if (infoPo == null) {
			return false;
		}
		int i = infoMapper.deleteInfoById(infoId);
		// 同时需要删除学生对应的
		// 获取管理员对应的学生
		ManagerAndStudentVO studentVo = managerService.queryStudentList(managerId);
		List<Student> studentList = studentVo.getStudentList();

		for (Student student : studentList) {
			boolean isDeleted = infoService.deleteInfoByUserIdAndTitle(student.getUserId(), infoPo.getTitle());

			if (!isDeleted) {
				return false;
			}
		}

		return i > 0;
	}

	@Override
	public boolean deleteManagerAllInfo(Long managerId) {
		List<ManagerNucleicAcidInfoVO> infoVoList = queryManagerInfoList(managerId);

		for (ManagerNucleicAcidInfoVO infoVo : infoVoList) {
			boolean isDeleted = deleteInfoById(managerId, infoVo.getId());

			if (!isDeleted) {
				return false;
			}
		}

		return true;
	}

	private List<ManagerNucleicAcidInfoVO> convertList(List<ManagerNucleicAcidInfoPO> infoPoList) {
		return infoPoList.stream().map(ManagerNucleicAcidInfoVO::new).collect(Collectors.toList());
	}

	private void alterUserInfo(ManagerNucleicAcidInfoVO infoVo, boolean isInsert, String oldTitle) throws UpdateNucleicAcidException, AddNucleicAcidException {
		ManagerAndStudentVO studentVo = managerService.queryStudentList(infoVo.getManagerId());
		List<Student> studentList = studentVo.getStudentList();

		NucleicAcidInfoVO userInfoVo = NucleicAcidInfoVO.builder()
			.managerId(infoVo.getManagerId())
			.deadLine(infoVo.getDeadLine())
			.title(infoVo.getTitle())
			.status(FinishStatus.getStatusByValue(infoVo.getFinishStatus()))
			.build();

		for (Student student : studentList) {
			// 给对应的用户插入数据
			userInfoVo.setUserId(student.getUserId());
			if (isInsert) {
				NucleicAcidInfoVO insertInfo = infoService.insertInfo(userInfoVo);
				if (insertInfo == null) {
					throw new AddNucleicAcidException("add info error!");
				}
			} else {
				NucleicAcidInfoVO updateInfo = infoService.updateInfoByUserIdAndTitle(userInfoVo, oldTitle);
				if (updateInfo == null) {
					throw new UpdateNucleicAcidException("update info error!");
				}
			}
		}
	}

	private void addTask(Long infoId, Date newDeadLine) {
		// 加入任务列表
		NucleicAcidTaskVO task = NucleicAcidTaskVO.builder()
			.id(infoId)
			.type(NucleicAcidType.REPORTING)
			.finishStatus(FinishStatus.DONE)
			.deadLine(newDeadLine)
			.build();

		taskService.addManagerNucleicAcidTask(task);
	}
}
