package com.weixin.njuteam.web.controller.help;

import com.weixin.njuteam.annotation.CurUserInfo;
import com.weixin.njuteam.annotation.NeedUserToken;
import com.weixin.njuteam.entity.vo.UserVO;
import com.weixin.njuteam.entity.vo.help.HelpClickHistoryVO;
import com.weixin.njuteam.entity.vo.help.SeekHelpClickHistoryVO;
import com.weixin.njuteam.enums.StatusCode;
import com.weixin.njuteam.service.ClickHistoryService;
import com.weixin.njuteam.web.Response;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Zyi
 */
@RestController
@RequestMapping("/click-history")
public class ClickHistoryController {

	private final ClickHistoryService clickHistoryService;

	@Autowired
	public ClickHistoryController(ClickHistoryService clickHistoryService) {
		this.clickHistoryService = clickHistoryService;
	}

	@GetMapping("/query/help/{id}")
	@ApiOperation(value = "获取单独的一条帮助信息浏览记录")
	public Response<HelpClickHistoryVO> queryHelpClickHistory(@ApiParam(value = "帮助信息浏览记录id", name = "id", example = "1") @PathVariable Long id) {
		if (id == null || id <= 0) {
			return new Response<>(StatusCode.BAD_REQUEST, "id传输有误！");
		}

		HelpClickHistoryVO historyVo = clickHistoryService.queryHelpClickById(id);

		return historyVo == null ? new Response<>(StatusCode.INTERNAL_SERVER_ERROR, "获取帮助信息浏览记录失败") : new Response<>(StatusCode.OK, "获取帮助信息浏览记录成功", historyVo);
	}

	@GetMapping("/query/seek-help/{id}")
	@ApiOperation(value = "获取单独的一条历史记录")
	public Response<SeekHelpClickHistoryVO> querySeekHelpClickHistory(@ApiParam(value = "求助信息浏览记录id", name = "id", example = "1") @PathVariable Long id) {
		if (id == null || id <= 0) {
			return new Response<>(StatusCode.BAD_REQUEST, "id传输有误！");
		}

		SeekHelpClickHistoryVO historyVo = clickHistoryService.querySeekHelpClickById(id);

		return historyVo == null ? new Response<>(StatusCode.INTERNAL_SERVER_ERROR, "获取求助信息浏览记录失败") : new Response<>(StatusCode.OK, "获取求助信息浏览记录成功", historyVo);
	}

	@NeedUserToken
	@GetMapping("/user/help")
	@ApiOperation(value = "获取用户浏览帮助信息的历史记录 need token")
	public Response<List<HelpClickHistoryVO>> queryUserHelpClickHistory(@CurUserInfo UserVO user) {
		String openId = user.getOpenId();
		if (openId == null) {
			return new Response<>(StatusCode.BAD_REQUEST, "用户token失效");
		}

		List<HelpClickHistoryVO> list = clickHistoryService.queryHelpClickByUserId(openId);
		return list == null ? new Response<>(StatusCode.INTERNAL_SERVER_ERROR, "获取用户帮助信息浏览记录失败") : new Response<>(StatusCode.OK, "获取用户帮助信息浏览记录成功", list);
	}

	@NeedUserToken
	@GetMapping("/user/seek-help")
	@ApiOperation(value = "获取用户浏览帮助信息的历史记录 need token")
	public Response<List<SeekHelpClickHistoryVO>> queryUserSeekHelpClickHistory(@CurUserInfo UserVO user) {
		String openId = user.getOpenId();
		if (openId == null) {
			return new Response<>(StatusCode.BAD_REQUEST, "用户token失效");
		}

		List<SeekHelpClickHistoryVO> list = clickHistoryService.querySeekHelpClickByUserId(openId);
		return list == null ? new Response<>(StatusCode.INTERNAL_SERVER_ERROR, "获取用户求助信息浏览记录失败") : new Response<>(StatusCode.OK, "获取用户求助信息浏览记录成功", list);
	}

	@NeedUserToken
	@DeleteMapping("/delete/help/{id}")
	@ApiOperation(value = "删除某条帮助信息浏览记录 need token")
	public Response<Boolean> deleteHelpClickHistory(@CurUserInfo UserVO user, @ApiParam(value = "帮助信息历史记录id", name = "id", example = "1") @PathVariable Long id) {
		boolean isDeleted = clickHistoryService.deleteHelpClickById(id);

		return isDeleted ? new Response<>(StatusCode.OK, "删除帮助信息浏览记录成功", true) : new Response<>(StatusCode.INTERNAL_SERVER_ERROR, "删除帮助信息浏览记录失败", false);
	}

	@NeedUserToken
	@DeleteMapping("/delete/seek-help/{id}")
	@ApiOperation(value = "删除某条帮助信息浏览记录 need token")
	public Response<Boolean> deleteSeekHelpClickHistory(@CurUserInfo UserVO user, @ApiParam(value = "帮助信息历史记录id", name = "id", example = "1") @PathVariable Long id) {
		boolean isDeleted = clickHistoryService.deleteSeekHelpClickById(id);

		return isDeleted ? new Response<>(StatusCode.OK, "删除求助信息浏览记录成功", true) : new Response<>(StatusCode.INTERNAL_SERVER_ERROR, "删除求助信息浏览记录失败", false);
	}
}
