package com.weixin.njuteam.web.controller.nucleic;

import com.weixin.njuteam.annotation.CurUserInfo;
import com.weixin.njuteam.annotation.NeedUserToken;
import com.weixin.njuteam.entity.vo.UserVO;
import com.weixin.njuteam.entity.vo.nucleic.NucleicAcidBookingVO;
import com.weixin.njuteam.enums.FinishStatus;
import com.weixin.njuteam.enums.StatusCode;
import com.weixin.njuteam.exception.AddTaskException;
import com.weixin.njuteam.service.NucleicAcidBookingService;
import com.weixin.njuteam.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

/**
 * @author Zyi
 */
@RestController
@RequestMapping("/nucleic-acid/booking")
@Api(value = "核酸预约通知有关接口", tags = {"核酸预约接口"})
public class NucleicAcidBookingController {

	private static final String ID_ERROR = "id传输错误";
	private final NucleicAcidBookingService bookingService;

	@Autowired
	public NucleicAcidBookingController(NucleicAcidBookingService bookingService) {
		this.bookingService = bookingService;
	}

	@GetMapping("/query/{id}")
	@ApiOperation("通过id获得核酸预约通知的信息")
	public Response<NucleicAcidBookingVO> getBookingInfo(@ApiParam(value = "预约通知id", name = "id", example = "1") @PathVariable Long id) {
		if (id == null || id <= 0) {
			return new Response<>(StatusCode.BAD_REQUEST, ID_ERROR);
		}
		NucleicAcidBookingVO bookingVo = bookingService.queryBookingById(id);

		return bookingVo == null ? new Response<>(StatusCode.INTERNAL_SERVER_ERROR, "获取核酸预约通知失败") : new Response<>(StatusCode.OK, "获取核酸预约通知成功", bookingVo);
	}

	/**
	 * 通过token来获得该用户的核酸预约通知
	 *
	 * @param userVo 不用传的参数 通过token获得
	 * @return list of nucleic acid booking info
	 */
	@GetMapping("/user-list")
	@NeedUserToken
	@ApiOperation("获取某个用户相关的核酸预约通知 need token")
	public Response<List<NucleicAcidBookingVO>> getUserBookingList(@CurUserInfo UserVO userVo) {
		List<NucleicAcidBookingVO> bookingVoList;

		try {
			bookingVoList = bookingService.queryBookingByUserId(userVo.getOpenId());
		} catch (SQLException e) {
			return new Response<>(StatusCode.INTERNAL_SERVER_ERROR, "获取用户核酸预约通知失败");
		}

		return new Response<>(StatusCode.OK, "获取用户核酸预约通知成功", bookingVoList);
	}

	@NeedUserToken
	@GetMapping("/count")
	@ApiOperation(value = "获取某个用户待预约的数量")
	public Response<Integer> getUserBookingCount(@CurUserInfo UserVO userVo) {
		int count = bookingService.queryBookingCount(userVo.getOpenId(), FinishStatus.IN_PROGRESS);

		if (count == -1) {
			return new Response<>(StatusCode.INTERNAL_SERVER_ERROR, "获取用户待上报数量失败");
		}

		return new Response<>(StatusCode.OK, "获取用户待上报数量成功", count);
	}

	@PostMapping("/insert")
	@ApiOperation("插入新的核酸预约通知 该接口只用于测试 不需调用")
	public Response<NucleicAcidBookingVO> insertBookingInfo(@ApiParam(value = "核酸预约信息 json格式", name = "bookingVo", example = "xxx") @RequestBody NucleicAcidBookingVO bookingVo) {
		if (bookingVo.getUserId() == null || bookingVo.getUserId() <= 0) {
			return new Response<>(StatusCode.BAD_REQUEST, "核酸上报通知必须包括用户id!");
		}
		NucleicAcidBookingVO newBookingVo;

		try {
			newBookingVo = bookingService.insertBooking(bookingVo);
		} catch (AddTaskException e) {
			return new Response<>(StatusCode.INTERNAL_SERVER_ERROR, "添加定时任务失败");
		}

		if (newBookingVo == null) {
			return new Response<>(StatusCode.OK, "插入核酸预约通知失败");
		}

		return new Response<>(StatusCode.OK, "插入核酸预约通知成功", bookingVo);
	}

	@NeedUserToken
	@PostMapping("/finish/{bookingId}")
	@ApiOperation(value = "调整预约通知的进度为已完成 need token")
	public Response<NucleicAcidBookingVO> updateBookingFinish(@CurUserInfo UserVO userVo, @ApiParam(value = "预约通知的id", name = "bookingId", example = "1") @PathVariable Long bookingId) {
		if (bookingId == null || bookingId <= 0) {
			return new Response<>(StatusCode.BAD_REQUEST, ID_ERROR);
		}
		NucleicAcidBookingVO bookingVo = bookingService.updateFinish(userVo.getOpenId(), bookingId);

		if (bookingVo == null) {
			return new Response<>(StatusCode.INTERNAL_SERVER_ERROR, "更新完成状态失败");
		}

		return new Response<>(StatusCode.OK, "更新完成状态成功", bookingVo);
	}

	@NeedUserToken
	@PostMapping("/remind/{bookingId}")
	@ApiOperation(value = "打开预约通知提醒功能 need token")
	public Response<NucleicAcidBookingVO> openBookingRemind(@CurUserInfo UserVO user,
															@ApiParam(value = "预约通知的id", name = "testingId", example = "1") @PathVariable Long bookingId,
															@ApiParam(value = "是否打开提醒", name = "isOpenRemind", example = "true") @RequestParam("isOpenRemind") Boolean isOpenRemind) {
		if (bookingId == null || bookingId <= 0) {
			return new Response<>(StatusCode.BAD_REQUEST, ID_ERROR);
		}
		NucleicAcidBookingVO bookingVo = bookingService.openRemind(user.getOpenId(), bookingId, isOpenRemind);

		if (bookingVo == null) {
			return new Response<>(StatusCode.INTERNAL_SERVER_ERROR, "打开预约通知失败");
		}

		return new Response<>(StatusCode.OK, "打开预约通知成功", bookingVo);
	}

	@PostMapping("/update")
	@ApiOperation("更新核酸预约通知")
	public Response<NucleicAcidBookingVO> updateBookingInfo(@ApiParam(value = "新的核酸预约通知", name = "bookingVo", example = "xxx") @RequestBody NucleicAcidBookingVO bookingVo) {
		NucleicAcidBookingVO newBookingVo = bookingService.updateBooking(bookingVo);

		if (newBookingVo == null) {
			return new Response<>(StatusCode.INTERNAL_SERVER_ERROR, "更新核酸预约通知失败");
		}

		return new Response<>(StatusCode.OK, "更新核酸预约通知成功", bookingVo);
	}

	@DeleteMapping("/delete/{id}")
	@ApiOperation("删除核酸预约通知")
	public Response<Boolean> deleteBookingInfo(@ApiParam(value = "核酸预约通知id", name = "id", example = "1") @PathVariable Long id) {
		if (id == null || id <= 0) {
			return new Response<>(StatusCode.BAD_REQUEST, ID_ERROR);
		}
		boolean isDeleted = bookingService.deleteBooking(id);

		return isDeleted ? new Response<>(StatusCode.OK, "删除核酸预约通知成功", true) : new Response<>(StatusCode.INTERNAL_SERVER_ERROR, "删除核酸预约通知失败", false);
	}
}
