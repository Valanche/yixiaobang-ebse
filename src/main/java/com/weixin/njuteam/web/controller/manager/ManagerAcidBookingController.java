package com.weixin.njuteam.web.controller.manager;

import com.weixin.njuteam.annotation.CurManagerInfo;
import com.weixin.njuteam.annotation.NeedManagerToken;
import com.weixin.njuteam.entity.vo.manager.ManagerVO;
import com.weixin.njuteam.entity.vo.manager.nucleic.ManagerNucleicAcidBookingVO;
import com.weixin.njuteam.enums.StatusCode;
import com.weixin.njuteam.exception.UpdateNucleicAcidException;
import com.weixin.njuteam.service.ManagerNucleicAcidBookingService;
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
@RequestMapping("/manager/booking")
@Api(value = "管理端核酸预约接口", tags = {"管理端核酸预约有关接口"})
@Slf4j
public class ManagerAcidBookingController {

	private final ManagerNucleicAcidBookingService bookingService;

	@Autowired
	public ManagerAcidBookingController(ManagerNucleicAcidBookingService bookingService) {
		this.bookingService = bookingService;
	}

	@NeedManagerToken
	@GetMapping("/all")
	@ApiOperation(value = "获取管理员的所有核酸预约通知 need token")
	public Response<List<ManagerNucleicAcidBookingVO>> queryManagerBookingInfoList(@CurManagerInfo ManagerVO managerVo) {
		Long managerId = managerVo.getId();
		List<ManagerNucleicAcidBookingVO> bookingInfoList = bookingService.queryManagerBookingInfoList(managerId);

		return bookingInfoList == null ? new Response<>(StatusCode.INTERNAL_SERVER_ERROR, "获取管理员预约列表失败") : new Response<>(StatusCode.OK, "获取管理员预约列表成功", bookingInfoList);
	}

	@GetMapping("/query/{bookingId}")
	@ApiOperation(value = "获取单独一条的核酸预约通知")
	public Response<ManagerNucleicAcidBookingVO> queryManagerBookingInfo(@ApiParam(value = "预约id", name = "bookingId", example = "1") @PathVariable Long bookingId) {
		if (bookingId == null || bookingId <= 0) {
			return new Response<>(StatusCode.BAD_REQUEST, "id不能为空或负数！");
		}

		ManagerNucleicAcidBookingVO bookingVo = bookingService.queryBookingInfoById(bookingId);
		return bookingVo == null ? new Response<>(StatusCode.INTERNAL_SERVER_ERROR, "获取核酸预约信息失败") : new Response<>(StatusCode.OK, "获取核酸预约信息成功", bookingVo);
	}

	@NeedManagerToken
	@PostMapping("/update/{oldTitle}")
	@ApiOperation(value = "更新新的核酸预约通知 need token")
	public Response<ManagerNucleicAcidBookingVO> updateManagerBookingInfo(@CurManagerInfo ManagerVO managerVo,
																		  @ApiParam(value = "核酸预约通知", name = "bookingVo", example = "xxx") @RequestBody ManagerNucleicAcidBookingVO bookingVo,
																		  @ApiParam(value = "旧标题 如果更新了新标题记得传改之前的标题", name = "oldTitle", example = "xxx") @PathVariable String oldTitle) {
		if (bookingVo == null || bookingVo.getId() == null || bookingVo.getId() <= 0) {
			return new Response<>(StatusCode.INTERNAL_SERVER_ERROR, "核酸预约通知不能为空!");
		}

		Long managerId = managerVo.getId();
		ManagerNucleicAcidBookingVO updateInfo = null;
		try {
			updateInfo = bookingService.updateBookingInfo(managerId, bookingVo, oldTitle);
		} catch (UpdateNucleicAcidException e) {
			return new Response<>(StatusCode.INTERNAL_SERVER_ERROR, "更新用户核酸预约信息失败");
		}
		return updateInfo == null ? new Response<>(StatusCode.INTERNAL_SERVER_ERROR, "插入核酸预约信息失败") : new Response<>(StatusCode.OK, "插入核酸预约信息成功", updateInfo);
	}

	@NeedManagerToken
	@DeleteMapping("/delete/{bookingId}")
	@ApiOperation(value = "删除核酸上报通知 need token")
	public Response<Boolean> deleteManagerBookingInfo(@CurManagerInfo ManagerVO managerVo,
													  @ApiParam(value = "核酸预约id", name = "bookingId", example = "1") @PathVariable Long bookingId) {
		if (bookingId == null || bookingId <= 0) {
			return new Response<>(StatusCode.BAD_REQUEST, "id不能为空");
		}

		boolean isDeleted = bookingService.deleteBookingInfoById(managerVo.getId(), bookingId);
		return isDeleted ? new Response<>(StatusCode.OK, "删除核酸检测预约成功", true) : new Response<>(StatusCode.INTERNAL_SERVER_ERROR, "删除核酸预约通知失败", false);
	}

	@NeedManagerToken
	@DeleteMapping("/delete-all")
	@ApiOperation(value = "删除所有核酸上报通知 need token")
	public Response<Boolean> deleteAllManagerBookingInfo(@CurManagerInfo ManagerVO managerVo) {
		boolean isDeleted = bookingService.deleteManagerAllBookingInfo(managerVo.getId());
		return isDeleted ? new Response<>(StatusCode.OK, "删除核酸预约通知成功", true) : new Response<>(StatusCode.INTERNAL_SERVER_ERROR, "删除核酸预约通知失败", false);
	}
}
