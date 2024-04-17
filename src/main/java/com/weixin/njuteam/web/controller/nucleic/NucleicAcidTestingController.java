package com.weixin.njuteam.web.controller.nucleic;

import com.weixin.njuteam.annotation.CurUserInfo;
import com.weixin.njuteam.annotation.NeedUserToken;
import com.weixin.njuteam.entity.vo.UserVO;
import com.weixin.njuteam.entity.vo.nucleic.NucleicAcidTestingVO;
import com.weixin.njuteam.enums.FinishStatus;
import com.weixin.njuteam.enums.StatusCode;
import com.weixin.njuteam.exception.AddTaskException;
import com.weixin.njuteam.service.NucleicAcidTestingService;
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
@RequestMapping("/nucleic-acid/testing")
@Api(value = "核酸检测通知有关接口", tags = {"核酸检测接口"})
public class NucleicAcidTestingController {

	private static final String ID_ERROR = "id传输错误";
	private final NucleicAcidTestingService testingService;

	@Autowired
	public NucleicAcidTestingController(NucleicAcidTestingService testingService) {
		this.testingService = testingService;
	}

	@NeedUserToken
	@GetMapping("/user-list")
	@ApiOperation(value = "获得某个用户的核酸检测通知列表 need token")
	public Response<List<NucleicAcidTestingVO>> getUserTestingList(@CurUserInfo UserVO userVo) {
		List<NucleicAcidTestingVO> testingVoList;

		try {
			testingVoList = testingService.queryTestingByUserId(userVo.getOpenId());
		} catch (SQLException e) {
			return new Response<>(StatusCode.INTERNAL_SERVER_ERROR, "获取用户核酸检测通知失败");
		}

		return new Response<>(StatusCode.OK, "获取用户核酸检测通知成功", testingVoList);
	}

	@NeedUserToken
	@GetMapping("/count")
	@ApiOperation("获取某个用户检测通知进行中的数量")
	public Response<Integer> getUserTestingCount(@CurUserInfo UserVO userVo) {
		int count = testingService.queryTestingCount(userVo.getOpenId(), FinishStatus.IN_PROGRESS);

		if (count == -1) {
			return new Response<>(StatusCode.INTERNAL_SERVER_ERROR, "获取用户待检测数量失败");
		}

		return new Response<>(StatusCode.OK, "获取用户待检测数量成功", count);
	}

	@GetMapping("/query/{id}")
	@ApiOperation(value = "通过id获得某个核酸检测通知")
	public Response<NucleicAcidTestingVO> getTestingInfo(@ApiParam(value = "核酸检测通知的id", name = "id", example = "1", required = true) @PathVariable Long id) {
		if (id == null || id <= 0) {
			return new Response<>(StatusCode.BAD_REQUEST, ID_ERROR);
		}
		NucleicAcidTestingVO testingVo = testingService.queryTestingById(id);

		if (testingVo == null) {
			return new Response<>(StatusCode.INTERNAL_SERVER_ERROR, "获取核酸检测通知失败");
		}

		return new Response<>(StatusCode.OK, "获取核酸通知成功", testingVo);
	}

	@PostMapping("/insert")
	@ApiOperation(value = "插入新的核酸检测通知 该接口只用于测试 不需调用")
	public Response<NucleicAcidTestingVO> insertTestingInfo(@ApiParam(value = "核酸检测通知", name = "testingVo", example = "xxx", required = true) @RequestBody NucleicAcidTestingVO testingVo) {
		if (testingVo.getUserId() == null || testingVo.getUserId() <= 0) {
			return new Response<>(StatusCode.BAD_REQUEST, "核酸检测通知必须包括用户id!");
		}
		NucleicAcidTestingVO newTestingVo;

		try {
			newTestingVo = testingService.insertTesting(testingVo);
		} catch (AddTaskException e) {
			return new Response<>(StatusCode.INTERNAL_SERVER_ERROR, "开启定时任务失败");
		}

		if (newTestingVo == null) {
			return new Response<>(StatusCode.INTERNAL_SERVER_ERROR, "插入核酸检测通知失败");
		}

		return new Response<>(StatusCode.OK, "插入核酸通知成功", newTestingVo);
	}


	@NeedUserToken
	@PostMapping("/finish/{testingId}")
	@ApiOperation(value = "调整检测通知的进度为已完成 need token")
	public Response<NucleicAcidTestingVO> updateTestingFinish(@CurUserInfo UserVO userVo, @ApiParam(value = "检测通知的id", name = "testingId", example = "1") @PathVariable Long testingId) {
		if (testingId == null || testingId <= 0) {
			return new Response<>(StatusCode.BAD_REQUEST, ID_ERROR);
		}
		NucleicAcidTestingVO testingVo = testingService.updateFinish(userVo.getOpenId(), testingId);

		if (testingVo == null) {
			return new Response<>(StatusCode.INTERNAL_SERVER_ERROR, "更新完成状态失败");
		}

		return new Response<>(StatusCode.OK, "更新完成状态成功", testingVo);
	}

	@NeedUserToken
	@PostMapping("/remind/{testingId}")
	@ApiOperation(value = "打开检测通知提醒功能 need token")
	public Response<NucleicAcidTestingVO> openTestingRemind(@CurUserInfo UserVO user,
															@ApiParam(value = "检测通知的id", name = "testingId", example = "1") @PathVariable Long testingId,
															@ApiParam(value = "是否打开提醒", name = "isOpenRemind", example = "true") @RequestParam("isOpenRemind") Boolean isOpenRemind) {
		if (testingId == null || testingId <= 0) {
			return new Response<>(StatusCode.BAD_REQUEST, ID_ERROR);
		}
		NucleicAcidTestingVO testingVo = testingService.openRemind(user.getOpenId(), testingId, isOpenRemind);

		if (testingVo == null) {
			return new Response<>(StatusCode.INTERNAL_SERVER_ERROR, "打开检测通知失败");
		}

		return new Response<>(StatusCode.OK, "打开检测通知成功", testingVo);
	}

	@PostMapping("/update")
	@ApiOperation(value = "更新核酸检测通知 需要包括通知id")
	public Response<NucleicAcidTestingVO> updateTestingInfo(@ApiParam(value = "核酸检测通知", name = "testingVo", example = "xxx", required = true) @RequestBody NucleicAcidTestingVO testingVo) {
		NucleicAcidTestingVO newTestingVo = testingService.updateTesting(testingVo);

		if (newTestingVo == null) {
			return new Response<>(StatusCode.INTERNAL_SERVER_ERROR, "更新核酸检测通知失败");
		}

		return new Response<>(StatusCode.OK, "更新核酸通知成功", newTestingVo);
	}

	@DeleteMapping("/delete/{id}")
	@ApiOperation(value = "删除某个核酸检测通知")
	public Response<Boolean> deleteTestingInfo(@ApiParam(value = "核酸检测通知的id", name = "id", example = "1", required = true) @PathVariable Long id) {
		if (id == null || id <= 0) {
			return new Response<>(StatusCode.BAD_REQUEST, ID_ERROR);
		}
		boolean isDeleted = testingService.deleteTesting(id);

		return isDeleted ? new Response<>(StatusCode.INTERNAL_SERVER_ERROR, "删除核酸检测通知失败", false) : new Response<>(StatusCode.OK, "删除核酸通知成功", true);
	}
}
