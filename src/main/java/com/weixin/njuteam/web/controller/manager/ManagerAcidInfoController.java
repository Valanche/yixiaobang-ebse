package com.weixin.njuteam.web.controller.manager;

import com.weixin.njuteam.annotation.CurManagerInfo;
import com.weixin.njuteam.annotation.NeedManagerToken;
import com.weixin.njuteam.entity.vo.manager.ManagerVO;
import com.weixin.njuteam.entity.vo.manager.nucleic.ManagerAndStudentInfoVO;
import com.weixin.njuteam.entity.vo.manager.nucleic.ManagerNucleicAcidInfoVO;
import com.weixin.njuteam.enums.StatusCode;
import com.weixin.njuteam.exception.NoStudentException;
import com.weixin.njuteam.service.ManagerNucleicAcidInfoService;
import com.weixin.njuteam.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Zyi
 */
@RestController
@RequestMapping("/manager/info")
@Api(value = "管理端核酸上报接口", tags = {"管理端核酸上报有关接口"})
@Slf4j
public class ManagerAcidInfoController {

	private final ManagerNucleicAcidInfoService infoService;

	@Autowired
	public ManagerAcidInfoController(ManagerNucleicAcidInfoService infoService) {
		this.infoService = infoService;
	}

	@NeedManagerToken
	@GetMapping("/all")
	@ApiOperation(value = "获取管理员的所有核酸上报通知 need token")
	public Response<List<ManagerNucleicAcidInfoVO>> queryManagerReportingInfoList(@CurManagerInfo ManagerVO managerVo) {
		Long managerId = managerVo.getId();
		List<ManagerNucleicAcidInfoVO> infoList = infoService.queryManagerInfoList(managerId);

		return infoList == null ? new Response<>(StatusCode.INTERNAL_SERVER_ERROR, "获取管理员上报列表失败") : new Response<>(StatusCode.OK, "获取管理员上报列表成功", infoList);
	}

	@GetMapping("/query/{infoId}")
	@ApiOperation(value = "获取单独一条的核酸上报通知")
	public Response<ManagerNucleicAcidInfoVO> queryManagerReportingInfo(@ApiParam(value = "上报id", name = "infoId", example = "1") @PathVariable Long infoId) {
		if (infoId == null || infoId <= 0) {
			return new Response<>(StatusCode.BAD_REQUEST, "id不能为空或负数！");
		}

		ManagerNucleicAcidInfoVO infoVo = infoService.queryInfoById(infoId);
		return infoVo == null ? new Response<>(StatusCode.INTERNAL_SERVER_ERROR, "获取核酸上报信息失败") : new Response<>(StatusCode.OK, "获取核酸上报信息成功", infoVo);
	}

	@NeedManagerToken
	@GetMapping("/reporting-result/{infoId}")
	@ApiOperation(value = "获得学生的上报情况")
	public Response<ManagerAndStudentInfoVO> queryStudentReportingResult(@CurManagerInfo ManagerVO managerVo,
																		 @ApiParam(value = "对应的上报信息id", name = "infoId", example = "1") @PathVariable Long infoId) {
		if (infoId == null || infoId <= 0) {
			return new Response<>(StatusCode.BAD_REQUEST, "id不能为空或负数！");
		}
		ManagerAndStudentInfoVO infoVo = null;
		try {
			infoVo = infoService.queryStudentReportingInfo(managerVo.getId(), infoId);
		} catch (NoStudentException e) {
			return new Response<>(StatusCode.OK, "获取学生上报信息成功, 管理员无学生");
		}

		return infoVo == null ? new Response<>(StatusCode.INTERNAL_SERVER_ERROR, "获取学生上报信息失败") : new Response<>(StatusCode.OK, "获取学生上报信息成功", infoVo);
	}

	@NeedManagerToken
	@DeleteMapping("/delete/{infoId}")
	@ApiOperation(value = "删除核酸上报通知 need token")
	public Response<Boolean> deleteManagerReportingInfo(@CurManagerInfo ManagerVO managerVo,
														@ApiParam(value = "核酸上报id", name = "infoId", example = "1") @PathVariable Long infoId) {
		if (infoId == null || infoId <= 0) {
			return new Response<>(StatusCode.BAD_REQUEST, "id不能为空");
		}

		boolean isDeleted = infoService.deleteInfoById(managerVo.getId(), infoId);
		return isDeleted ? new Response<>(StatusCode.OK, "删除核酸上报通知成功", true) : new Response<>(StatusCode.INTERNAL_SERVER_ERROR, "删除核酸上报通知失败", false);
	}

	@NeedManagerToken
	@DeleteMapping("/delete-all")
	@ApiOperation(value = "删除所有核酸上报通知 need token")
	public Response<Boolean> deleteAllManagerReportingInfo(@CurManagerInfo ManagerVO managerVo) {
		boolean isDeleted = infoService.deleteManagerAllInfo(managerVo.getId());
		return isDeleted ? new Response<>(StatusCode.OK, "删除核酸上报通知成功", true) : new Response<>(StatusCode.INTERNAL_SERVER_ERROR, "删除核酸上报通知失败", false);
	}
}
