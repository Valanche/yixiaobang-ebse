package com.weixin.njuteam.web.controller.manager;

import com.weixin.njuteam.annotation.CurManagerInfo;
import com.weixin.njuteam.annotation.NeedManagerToken;
import com.weixin.njuteam.entity.vo.manager.ManagerVO;
import com.weixin.njuteam.entity.vo.manager.nucleic.ManagerNucleicAcidTestingVO;
import com.weixin.njuteam.enums.StatusCode;
import com.weixin.njuteam.exception.AddNucleicAcidException;
import com.weixin.njuteam.exception.DuplicateTitleException;
import com.weixin.njuteam.exception.UpdateNucleicAcidException;
import com.weixin.njuteam.service.ManagerNucleicAcidTestingService;
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
@RequestMapping("/manager/testing")
@Api(value = "管理端核酸检测接口", tags = {"管理端核酸检测有关接口"})
@Slf4j
public class ManagerAcidTestingController {

	private final ManagerNucleicAcidTestingService testingService;

	@Autowired
	public ManagerAcidTestingController(ManagerNucleicAcidTestingService testingService) {
		this.testingService = testingService;
	}

	@NeedManagerToken
	@GetMapping("/all")
	@ApiOperation(value = "获取管理员的所有核酸检测通知 need token")
	public Response<List<ManagerNucleicAcidTestingVO>> queryManagerTestingInfoList(@CurManagerInfo ManagerVO managerVo) {
		Long managerId = managerVo.getId();
		List<ManagerNucleicAcidTestingVO> testingInfoList = testingService.queryManagerTestingInfoList(managerId);

		return testingInfoList == null ? new Response<>(StatusCode.INTERNAL_SERVER_ERROR, "获取管理员检测列表失败") : new Response<>(StatusCode.OK, "获取管理员检测列表成功", testingInfoList);
	}

	@GetMapping("/query/{testingId}")
	@ApiOperation(value = "获取单独一条的核酸检测通知")
	public Response<ManagerNucleicAcidTestingVO> queryManagerTestingInfo(@ApiParam(value = "检测id", name = "testingId", example = "1") @PathVariable Long testingId) {
		if (testingId == null || testingId <= 0) {
			return new Response<>(StatusCode.BAD_REQUEST, "id不能为空或负数！");
		}

		ManagerNucleicAcidTestingVO testingVo = testingService.queryTestingInfoById(testingId);
		return testingVo == null ? new Response<>(StatusCode.INTERNAL_SERVER_ERROR, "获取核酸检测信息失败") : new Response<>(StatusCode.OK, "获取核酸检测信息成功", testingVo);
	}

	@NeedManagerToken
	@PostMapping("/insert")
	@ApiOperation(value = "插入新的核酸检测通知 need token")
	public Response<ManagerNucleicAcidTestingVO> insertManagerTestingInfo(@CurManagerInfo ManagerVO managerVo,
																		  @ApiParam(value = "核酸检测通知", name = "testingVo", example = "xxx") @RequestBody ManagerNucleicAcidTestingVO testingVo) {
		if (testingVo == null) {
			return new Response<>(StatusCode.INTERNAL_SERVER_ERROR, "核酸检测通知不能为空!");
		}

		Long managerId = managerVo.getId();
		ManagerNucleicAcidTestingVO insertInfo = null;
		try {
			insertInfo = testingService.insertTestingInfo(managerId, testingVo);
		} catch (AddNucleicAcidException e) {
			return new Response<>(StatusCode.INTERNAL_SERVER_ERROR, "插入用户核酸检测信息失败");
		} catch (DuplicateTitleException e) {
			return new Response<>(StatusCode.BAD_REQUEST, "标题重复");
		}
		return insertInfo == null ? new Response<>(StatusCode.INTERNAL_SERVER_ERROR, "插入核酸检测信息失败") : new Response<>(StatusCode.OK, "插入核酸检测信息成功", insertInfo);
	}

	@NeedManagerToken
	@PostMapping("/update/{oldTitle}")
	@ApiOperation(value = "更新新的核酸检测通知 need token")
	public Response<ManagerNucleicAcidTestingVO> updateManagerTestingInfo(@CurManagerInfo ManagerVO managerVo,
																		  @ApiParam(value = "核酸检测通知", name = "testingVo", example = "xxx") @RequestBody ManagerNucleicAcidTestingVO testingVo,
																		  @ApiParam(value = "旧标题 如果更新了新标题记得传改之前的标题", name = "oldTitle", example = "xxx") @PathVariable String oldTitle) {
		if (testingVo == null || testingVo.getId() == null || testingVo.getId() <= 0) {
			return new Response<>(StatusCode.INTERNAL_SERVER_ERROR, "核酸检测通知不能为空!");
		}

		Long managerId = managerVo.getId();
		ManagerNucleicAcidTestingVO updateInfo = null;
		try {
			updateInfo = testingService.updateTestingInfo(managerId, testingVo, oldTitle);
		} catch (UpdateNucleicAcidException e) {
			return new Response<>(StatusCode.INTERNAL_SERVER_ERROR, "更新用户核酸检测信息失败");
		} catch (DuplicateTitleException e) {
			return new Response<>(StatusCode.BAD_REQUEST, "标题重复更新失败");
		}
		return updateInfo == null ? new Response<>(StatusCode.INTERNAL_SERVER_ERROR, "插入核酸检测信息失败") : new Response<>(StatusCode.OK, "插入核酸检测信息成功", updateInfo);
	}

	@NeedManagerToken
	@DeleteMapping("/delete/{testingId}")
	@ApiOperation(value = "删除核酸上报通知 need token")
	public Response<Boolean> deleteManagerTestingInfo(@CurManagerInfo ManagerVO managerVo,
													  @ApiParam(value = "核酸检测id", name = "testingId", example = "1") @PathVariable Long testingId) {
		if (testingId == null || testingId <= 0) {
			return new Response<>(StatusCode.BAD_REQUEST, "id不能为空");
		}

		boolean isDeleted = testingService.deleteTestingInfoById(managerVo.getId(), testingId);
		return isDeleted ? new Response<>(StatusCode.OK, "删除核酸检测通知成功", true) : new Response<>(StatusCode.INTERNAL_SERVER_ERROR, "删除核酸检测通知失败", false);
	}

	@NeedManagerToken
	@DeleteMapping("/delete-all")
	@ApiOperation(value = "删除所有核酸上报通知 need token")
	public Response<Boolean> deleteAllManagerTestingInfo(@CurManagerInfo ManagerVO managerVo) {
		boolean isDeleted = testingService.deleteManagerAllTestingInfo(managerVo.getId());
		return isDeleted ? new Response<>(StatusCode.OK, "删除核酸检测通知成功", true) : new Response<>(StatusCode.INTERNAL_SERVER_ERROR, "删除核酸检测通知失败", false);
	}
}
