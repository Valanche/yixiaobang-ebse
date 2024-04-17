package com.weixin.njuteam.web.controller.nucleic;

import com.weixin.njuteam.annotation.CurUserInfo;
import com.weixin.njuteam.annotation.NeedUserToken;
import com.weixin.njuteam.entity.vo.UserVO;
import com.weixin.njuteam.entity.vo.nucleic.NucleicAcidInfoVO;
import com.weixin.njuteam.entity.vo.nucleic.RecognizeResultVO;
import com.weixin.njuteam.enums.FinishStatus;
import com.weixin.njuteam.enums.StatusCode;
import com.weixin.njuteam.exception.AddTaskException;
import com.weixin.njuteam.exception.UpdateNucleicAcidException;
import com.weixin.njuteam.service.NucleicAcidInfoService;
import com.weixin.njuteam.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.sql.SQLException;
import java.util.List;

/**
 * 核酸上报模块
 *
 * @author Zyi
 */
@RestController
@RequestMapping("/nucleic-acid/info")
@Api(value = "核酸上报通知有关接口", tags = {"核酸上报接口"})
public class NucleicAcidInfoController {

	private static final String ID_ERROR = "id传输错误!";
	private final NucleicAcidInfoService infoService;

	@Autowired
	public NucleicAcidInfoController(NucleicAcidInfoService infoService) {
		this.infoService = infoService;
	}

	@NeedUserToken
	@GetMapping("/user-list")
	@ApiOperation("获取小程序客户端看到的通知")
	public Response<List<NucleicAcidInfoVO>> queryUserInfoList(@CurUserInfo UserVO userVo) {
		List<NucleicAcidInfoVO> infoList;
		try {
			infoList = infoService.queryInfoByUserId(userVo.getOpenId());
		} catch (SQLException e) {
			return new Response<>(StatusCode.INTERNAL_SERVER_ERROR, "获取核酸通知上报列表失败");
		}

		return new Response<>(StatusCode.OK, "获取核酸通知上报列表成功", infoList);
	}

	@GetMapping("/query/{id}")
	@ApiOperation(value = "通过核酸上报通知id来获取通知")
	public Response<NucleicAcidInfoVO> queryInfo(@ApiParam(value = "上报通知的id", name = "id", example = "1") @PathVariable Long id) {
		if (id == null || id <= 0) {
			return new Response<>(StatusCode.BAD_REQUEST, ID_ERROR);
		}
		NucleicAcidInfoVO infoVo = infoService.queryInfoById(id);

		if (infoVo == null) {
			return new Response<>(StatusCode.INTERNAL_SERVER_ERROR, "获取核酸上报通知失败");
		}

		return new Response<>(StatusCode.OK, "获取核酸上报通知成功", infoVo);
	}

	@NeedUserToken
	@GetMapping("/count")
	@ApiOperation("获取某个用户待上报的数量")
	public Response<Integer> getUserInfoCount(@CurUserInfo UserVO userVo) {
		int count = infoService.queryInfoCount(userVo.getOpenId(), FinishStatus.IN_PROGRESS);

		if (count == -1) {
			return new Response<>(StatusCode.INTERNAL_SERVER_ERROR, "获取用户待上报数量失败");
		}

		return new Response<>(StatusCode.OK, "获取用户待上报数量成功", count);
	}

	@PostMapping("/insert")
	@ApiOperation("插入核酸上报通知 该接口只用于测试 不需调用")
	public Response<NucleicAcidInfoVO> insertInfo(@ApiParam(value = "核酸上报通知", name = "infoVo", example = "xxx") @RequestBody NucleicAcidInfoVO infoVo) {
		if (infoVo.getUserId() == null || infoVo.getUserId() <= 0) {
			return new Response<>(StatusCode.BAD_REQUEST, "核酸上报通知必须包括用户id!");
		}
		NucleicAcidInfoVO newInfo;

		try {
			newInfo = infoService.insertInfo(infoVo);
		} catch (AddTaskException e) {
			return new Response<>(StatusCode.INTERNAL_SERVER_ERROR, "定时任务开启失败");
		}

		if (newInfo == null) {
			return new Response<>(StatusCode.INTERNAL_SERVER_ERROR, "插入核酸上报通知失败");
		}

		return new Response<>(StatusCode.OK, "插入核酸上报通知成功", infoVo);
	}

	@NeedUserToken
	@PostMapping("/upload/{infoId}")
	@ApiOperation(value = "上传核酸截图, 通过OCR识别 返回是否识别成功 是否有与检测匹配的时间")
	public Response<RecognizeResultVO> uploadNucleicAcidImage(@CurUserInfo UserVO userVo, @ApiParam(value = "上报通知的id", name = "infoId", example = "1") @PathVariable Long infoId,
															  @ApiParam(value = "需要上传的核酸截图", required = true) @RequestBody MultipartFile image) {
		// 上传核酸图片，存到本地
		// 通过OCR识别信息来返回
		if (infoId == null || infoId <= 0) {
			return new Response<>(StatusCode.BAD_REQUEST, ID_ERROR);
		}
		RecognizeResultVO resultVo = infoService.recognize(image, userVo.getOpenId(), infoId);

		if (resultVo == null) {
			return new Response<>(StatusCode.INTERNAL_SERVER_ERROR, "未知错误!");
		}

		return new Response<>(StatusCode.OK, "获取OCR结果成功！", resultVo);
	}

	@NeedUserToken
	@PostMapping("/comment/{infoId}")
	@ApiOperation(value = "学生更新不做核酸的理由")
	public Response<NucleicAcidInfoVO> leaveCommentOnNotTesting(@CurUserInfo UserVO userVo, @ApiParam(value = "上报通知id", name = "infoId", example = "1") @PathVariable Long infoId,
																@ApiParam(value = "未做核酸的理由", name = "comment", example = "xxx") @RequestParam(value = "comment") String comment) {
		if (infoId == null || infoId <= 0) {
			return new Response<>(StatusCode.BAD_REQUEST, ID_ERROR);
		}

		NucleicAcidInfoVO infoVo;
		try {
			infoVo = infoService.updateComment(infoId, userVo.getOpenId(), comment);
		} catch (UpdateNucleicAcidException e) {
			return new Response<>(StatusCode.INTERNAL_SERVER_ERROR, "更新理由失败");
		}
		return infoVo == null ? new Response<>(StatusCode.INTERNAL_SERVER_ERROR, "更新核酸理由失败") : new Response<>(StatusCode.OK, "更新核酸理由成功", infoVo);
	}

	@NeedUserToken
	@PostMapping("/fix/{infoId}")
	@ApiOperation(value = "通过用户手动调整姓名来更新完成状态")
	public Response<NucleicAcidInfoVO> updateFinishInfo(@CurUserInfo UserVO userVo, @ApiParam(value = "用户真实姓名 OCR调整后", name = "name", example = "xxx") @RequestParam("name") String name,
														@ApiParam(value = "上报通知id", name = "infoId", example = "1") @PathVariable Long infoId) {
		if (infoId == null || infoId <= 0) {
			return new Response<>(StatusCode.BAD_REQUEST, ID_ERROR);
		}
		NucleicAcidInfoVO resultVO = infoService.fixResult(name, userVo.getOpenId(), infoId);

		if (resultVO == null) {
			return new Response<>(StatusCode.INTERNAL_SERVER_ERROR, "未知错误!");
		}

		return new Response<>(StatusCode.OK, "修正结果成功！", resultVO);
	}

	@PostMapping("update")
	@ApiOperation(value = "更新核酸上报通知信息")
	public Response<NucleicAcidInfoVO> updateInfo(@ApiParam(value = "核酸上报通知信息", name = "infoVo", example = "xxx") @RequestBody NucleicAcidInfoVO infoVo) {
		NucleicAcidInfoVO newInfoVo = infoService.updateInfo(infoVo);

		return infoVo == null ? new Response<>(StatusCode.INTERNAL_SERVER_ERROR, "更新核酸上报通知失败") : new Response<>(StatusCode.OK, "更新核酸上报通知成功", newInfoVo);
	}

	@NeedUserToken
	@PostMapping("/remind/{infoId}")
	@ApiOperation(value = "打开上报通知提醒功能 need token")
	public Response<NucleicAcidInfoVO> openInfoRemind(@CurUserInfo UserVO user,
													  @ApiParam(value = "上报通知的id", name = "testingId", example = "1") @PathVariable Long infoId,
													  @ApiParam(value = "是否打开提醒", name = "isOpenRemind", example = "true") @RequestParam("isOpenRemind") Boolean isOpenRemind) {
		if (infoId == null || infoId <= 0) {
			return new Response<>(StatusCode.BAD_REQUEST, ID_ERROR);
		}
		NucleicAcidInfoVO testingVo = infoService.openRemind(user.getOpenId(), infoId, isOpenRemind);

		if (testingVo == null) {
			return new Response<>(StatusCode.INTERNAL_SERVER_ERROR, "打开上报通知失败");
		}

		return new Response<>(StatusCode.OK, "打开上报通知成功", testingVo);
	}

	@DeleteMapping("/delete/{id}")
	@ApiOperation(value = "删除核酸上报通知")
	public Response<Boolean> deleteInfo(@ApiParam(value = "通知id", name = "id", example = "1") @PathVariable Long id) {
		if (id == null || id <= 0) {
			return new Response<>(StatusCode.BAD_REQUEST, ID_ERROR);
		}
		boolean isDeleted = infoService.deleteInfoById(id);

		return isDeleted ? new Response<>(StatusCode.OK, "删除核酸上报通知成功", true) : new Response<>(StatusCode.INTERNAL_SERVER_ERROR, "删除核酸上报通知失败", false);
	}

}
