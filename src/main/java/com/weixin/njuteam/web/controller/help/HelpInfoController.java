package com.weixin.njuteam.web.controller.help;

import com.weixin.njuteam.annotation.CurUserInfo;
import com.weixin.njuteam.annotation.NeedUserToken;
import com.weixin.njuteam.entity.po.help.HelpImage;
import com.weixin.njuteam.entity.vo.UserVO;
import com.weixin.njuteam.entity.vo.help.HelpInfoAndUserVO;
import com.weixin.njuteam.entity.vo.help.HelpInfoClickVO;
import com.weixin.njuteam.entity.vo.help.HelpInfoVO;
import com.weixin.njuteam.entity.vo.help.HelpSearchHistoryVO;
import com.weixin.njuteam.enums.HelpFinishStatus;
import com.weixin.njuteam.enums.StatusCode;
import com.weixin.njuteam.exception.AddTaskException;
import com.weixin.njuteam.exception.FormatException;
import com.weixin.njuteam.service.HelpInfoService;
import com.weixin.njuteam.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * @author Zyi
 */
@RestController
@RequestMapping("/help-info")
@Api(value = "帮助信息有关接口", tags = {"帮助信息接口"})
public class HelpInfoController {

	private static final String ID_ERROR = "id传输错误！";
	private final HelpInfoService helpInfoService;

	@Autowired
	public HelpInfoController(HelpInfoService helpInfoService) {
		this.helpInfoService = helpInfoService;
	}

	@GetMapping("/query/{id}")
	@ApiOperation(value = "通过id来获取某条帮助信息")
	public Response<HelpInfoAndUserVO> getHelpInfo(@ApiParam(value = "帮助信息id", name = "id", example = "1") @PathVariable Long id) {
		if (id == null || id <= 0) {
			return new Response<>(StatusCode.BAD_REQUEST, ID_ERROR);
		}
		HelpInfoAndUserVO helpInfo = helpInfoService.queryHelpInfoById(id);

		return helpInfo == null ? new Response<>(StatusCode.INTERNAL_SERVER_ERROR, "获取帮助信息失败") : new Response<>(StatusCode.OK, "获取帮助信息成功", helpInfo);
	}

	@NeedUserToken
	@GetMapping("/user-list")
	@ApiOperation(value = "获得某个用户的帮助信息 need token")
	public Response<List<HelpInfoVO>> getUserHelpInfo(@CurUserInfo UserVO userVo) {
		String openId = userVo.getOpenId();
		List<HelpInfoVO> helpInfoList = helpInfoService.queryHelpInfoByUserId(openId);

		if (helpInfoList == null) {
			return new Response<>(StatusCode.INTERNAL_SERVER_ERROR, "获取用户帮助信息失败");
		}

		return new Response<>(StatusCode.OK, "获取用户帮助信息成功", helpInfoList);
	}

	@NeedUserToken
	@GetMapping("/tag")
	@ApiOperation(value = "获得按tag分类的所有帮助信息 need token")
	public Response<Map<String, List<HelpInfoAndUserVO>>> getHelpInfoGroupByTag(@CurUserInfo UserVO userVo) {
		Map<String, List<HelpInfoAndUserVO>> tagHelpInfoMap = helpInfoService.getHelpInfoGroupByTag();

		if (tagHelpInfoMap == null) {
			return new Response<>(StatusCode.INTERNAL_SERVER_ERROR, "获取所有帮助信息失败");
		}

		return new Response<>(StatusCode.OK, "获取所有帮助信息成功", tagHelpInfoMap);
	}

	@NeedUserToken
	@GetMapping("/recommend")
	@ApiOperation(value = "获得推荐内容 need token")
	public Response<List<HelpInfoAndUserVO>> getRecommendHelpInfo(@CurUserInfo UserVO user) {
		List<HelpInfoAndUserVO> helpInfoList = helpInfoService.getRecommendList(user.getOpenId());

		if (helpInfoList == null) {
			return new Response<>(StatusCode.INTERNAL_SERVER_ERROR, "获取推荐内容失败");
		}

		return new Response<>(StatusCode.OK, "获取推荐内容成功", helpInfoList);
	}

	@NeedUserToken
	@GetMapping("/search/{keyword}")
	@ApiOperation(value = "通过关键词搜索帮助信息 need token")
	public Response<List<HelpInfoAndUserVO>> getSearchHelpResult(@CurUserInfo UserVO userVo, @ApiParam(value = "搜索关键词", name = "keyword", example = "ababa") @PathVariable String keyword) {
		List<HelpInfoAndUserVO> helpInfoList = helpInfoService.searchHelpInfo(userVo.getOpenId(), keyword);

		if (helpInfoList == null) {
			return new Response<>(StatusCode.INTERNAL_SERVER_ERROR, "关键词搜索失败");
		}

		return new Response<>(StatusCode.OK, "关键词搜索成功", helpInfoList);
	}

	@NeedUserToken
	@GetMapping("/history/{size}")
	@ApiOperation(value = "获得用户帮助信息的搜索记录 need token")
	public Response<List<HelpSearchHistoryVO>> getUserHelpInfoSearchHistory(@CurUserInfo UserVO userVo, @ApiParam(value = "要获取的历史记录的数量 如果要获取全部请传-1", name = "size", example = "20") @PathVariable Integer size) {
		if (size <= 0 && size != -1) {
			return new Response<>(StatusCode.BAD_REQUEST, "获取数量应该大于0");
		}

		List<HelpSearchHistoryVO> searchList = helpInfoService.querySearchHistory(userVo.getOpenId(), size);
		if (searchList == null) {
			return new Response<>(StatusCode.INTERNAL_SERVER_ERROR, "获取历史记录失败");
		}

		return new Response<>(StatusCode.OK, "获取历史记录成功", searchList);
	}

	@NeedUserToken
	@PostMapping("/insert")
	@ApiOperation(value = "插入新的帮助信息 need token")
	public Response<HelpInfoVO> insertHelpInfo(@CurUserInfo UserVO userVo, @ApiParam(value = "要插入的帮助信息 可以不需要userId", name = "helpInfo", example = "xxx") @RequestBody HelpInfoVO helpInfo) {
		String openId = userVo.getOpenId();

		HelpInfoVO newInfo = null;
		try {
			newInfo = helpInfoService.insertHelpInfo(helpInfo, openId);
		} catch (AddTaskException e) {
			return new Response<>(StatusCode.BAD_REQUEST, "对该信息插入任务失败, 失败信息: " + e.getMessage());
		}
		if (newInfo == null) {
			return new Response<>(StatusCode.INTERNAL_SERVER_ERROR, "插入帮助信息失败");
		}

		return new Response<>(StatusCode.OK, "插入帮助信息成功", newInfo);
	}

	@NeedUserToken
	@PostMapping("/click/{helpId}")
	@ApiOperation(value = "用户点击帮助页面时调用 need token")
	public Response<HelpInfoClickVO> clickHelpInfo(@CurUserInfo UserVO user, @ApiParam(value = "帮助信息id", name = "helpId", example = "1") @PathVariable Long helpId) {
		if (helpId == null || helpId <= 0) {
			return new Response<>(StatusCode.BAD_REQUEST, "helpId传输错误！");
		}
		HelpInfoClickVO clickVo = helpInfoService.clickHelpInfo(user.getOpenId(), helpId);
		if (clickVo == null) {
			return new Response<>(StatusCode.INTERNAL_SERVER_ERROR, "用户点击页面失败");
		}

		return new Response<>(StatusCode.OK, "用户点击页面成功", clickVo);
	}

	@NeedUserToken
	@PostMapping("/end-click/{clickId}")
	@ApiOperation(value = "用户退出帮助页面时调用 need token")
	public Response<Boolean> endClickHelpInfo(@CurUserInfo UserVO user, @ApiParam(value = "点击的id, 进入页面调用点击接口时会获得", name = "clickId", example = "1") @PathVariable Long clickId) {
		if (clickId == null || clickId <= 0) {
			return new Response<>(StatusCode.BAD_REQUEST, "clickId传输错误！");
		}
		boolean isEnd = helpInfoService.endClick(clickId);

		return isEnd ? new Response<>(StatusCode.OK, "用户退出页面更新成功", true) : new Response<>(StatusCode.INTERNAL_SERVER_ERROR, "用户退出页面更新失败", false);
	}

	@NeedUserToken
	@PostMapping("/upload/{id}")
	@ApiOperation(value = "插入帮助信息的图片 请在调用/insert后调用该接口")
	public Response<List<HelpImage>> uploadHelpImages(@CurUserInfo UserVO userVo, @ApiParam(value = "帮助通知的id", name = "id", example = "1") @PathVariable Long id,
													  @ApiParam(value = "要插入的帮助信息的图片", name = "imageList", example = "xxx") @RequestBody MultipartFile[] imageList) {
		if (id == null || id <= 0) {
			return new Response<>(StatusCode.BAD_REQUEST, ID_ERROR);
		}
		List<HelpImage> newHelpImageList = null;
		try {
			newHelpImageList = helpInfoService.insertImageList(id, imageList);
		} catch (FormatException e) {
			return new Response<>(StatusCode.BAD_REQUEST, "图片格式错误, " + e.getMessage());
		}

		if (newHelpImageList == null || newHelpImageList.isEmpty()) {
			return new Response<>(StatusCode.INTERNAL_SERVER_ERROR, "插入帮助信息图片失败");
		}

		return new Response<>(StatusCode.OK, "插入帮助信息成图片成功", newHelpImageList);
	}

	@NeedUserToken
	@PostMapping("/update")
	@ApiOperation(value = "更新帮助信息 不包括更新图片 need token")
	public Response<HelpInfoVO> updateHelpInfo(@CurUserInfo UserVO userVo, @ApiParam(value = "要更新的帮助信息 可以不需要userId", name = "helpInfo", example = "xxx") @RequestBody HelpInfoVO helpInfo) {
		String openId = userVo.getOpenId();

		HelpInfoVO newHelpInfo = helpInfoService.updateHelpInfo(helpInfo, openId);
		if (newHelpInfo == null) {
			return new Response<>(StatusCode.INTERNAL_SERVER_ERROR, "更新帮助信息失败");
		}

		return new Response<>(StatusCode.OK, "更新帮助信息成功", newHelpInfo);
	}

	@NeedUserToken
	@PostMapping("/update-image/{id}")
	@ApiOperation(value = "更新图片 需要该帮助信息的id need token")
	public Response<List<HelpImage>> updateHelpImages(@CurUserInfo UserVO userVo, @ApiParam(value = "帮助通知的id", name = "id", example = "1") @PathVariable Long id,
													  @ApiParam(value = "要插入的帮助信息的图片", name = "imageList", example = "xxx") @RequestBody MultipartFile[] imageList) {
		if (id == null || id <= 0) {
			return new Response<>(StatusCode.BAD_REQUEST, ID_ERROR);
		}
		List<HelpImage> newHelpImageList = null;
		try {
			newHelpImageList = helpInfoService.updateImageList(id, imageList);
		} catch (FormatException e) {
			return new Response<>(StatusCode.BAD_REQUEST, "图片格式错误, " + e.getMessage());
		}

		if (newHelpImageList == null || newHelpImageList.isEmpty()) {
			return new Response<>(StatusCode.INTERNAL_SERVER_ERROR, "更新帮助信息图片失败");
		}

		return new Response<>(StatusCode.OK, "更新帮助信息成图片成功", newHelpImageList);
	}

	@NeedUserToken
	@PostMapping("/update-status/{id}")
	@ApiOperation(value = "更新帮助信息的完成状态 need token")
	public Response<HelpInfoVO> updateHelpInfoFinishStatus(@CurUserInfo UserVO user,
														   @ApiParam(value = "帮助通知的id", name = "id", example = "1") @PathVariable Long id,
														   @ApiParam(value = "帮助通知要更新的完成状态", name = "finishStatus", example = "已截止") @RequestParam("finishStatus") HelpFinishStatus finishStatus) {
		if (id == null || id <= 0) {
			return new Response<>(StatusCode.BAD_REQUEST, ID_ERROR);
		}
		// 更新信息的完成状态
		HelpInfoVO helpInfo = helpInfoService.updateFinishStatus(id, finishStatus);

		if (helpInfo == null) {
			return new Response<>(StatusCode.INTERNAL_SERVER_ERROR, "更新帮助通知失败");
		}

		return new Response<>(StatusCode.OK, "更新帮助通知成功", helpInfo);
	}

	@DeleteMapping("/delete/{id}")
	@ApiOperation(value = "删除某个帮助信息")
	public Response<Boolean> deleteHelpInfo(@ApiParam(value = "要删除的帮助信息的id", name = "id", example = "1") @PathVariable Long id) {
		if (id == null || id <= 0) {
			return new Response<>(StatusCode.BAD_REQUEST, ID_ERROR);
		}
		boolean isDeleted = helpInfoService.deleteHelpInfo(id);

		return isDeleted ? new Response<>(StatusCode.OK, "删除帮助信息成功", true) : new Response<>(StatusCode.INTERNAL_SERVER_ERROR, "删除帮助信息失败", false);
	}

	@DeleteMapping("/delete-image/{imageId}")
	@ApiOperation(value = "删除某张图片")
	public Response<Boolean> deleteHelpImage(@ApiParam(value = "要删除的图片id", name = "imageId", example = "1") @PathVariable Long imageId) {
		if (imageId == null || imageId <= 0) {
			return new Response<>(StatusCode.BAD_REQUEST, "imageId传输错误！");
		}
		boolean isDeleted = helpInfoService.deleteImage(imageId);

		return isDeleted ? new Response<>(StatusCode.OK, "删除图片成功", true) : new Response<>(StatusCode.INTERNAL_SERVER_ERROR, "删除图片失败", false);
	}

	@NeedUserToken
	@DeleteMapping("/delete-history/{id}")
	@ApiOperation(value = "删除某次搜索历史记录 need token")
	public Response<Boolean> deleteSearchHelpInfoHistory(@CurUserInfo UserVO userVo, @ApiParam(value = "要删除的搜索历史记录关键词", name = "id", example = "1") @PathVariable Long id) {
		if (id == null || id <= 0) {
			return new Response<>(StatusCode.INTERNAL_SERVER_ERROR, ID_ERROR);
		}
		boolean isDeleted = helpInfoService.deleteSearchHelpInfoHistory(id);

		return isDeleted ? new Response<>(StatusCode.OK, "删除历史成功", true) : new Response<>(StatusCode.INTERNAL_SERVER_ERROR, "删除历史记录失败", false);
	}

	@NeedUserToken
	@DeleteMapping("/delete-all-history")
	@ApiOperation(value = "删除用户所有的历史记录 need token")
	public Response<Boolean> deleteAllSearchHelpInfoHistory(@CurUserInfo UserVO user) {
		boolean isDeleted = helpInfoService.deleteAllHistory(user.getOpenId());

		return isDeleted ? new Response<>(StatusCode.OK, "删除所有历史记录成功", true) : new Response<>(StatusCode.INTERNAL_SERVER_ERROR, "删除所有历史记录失败", false);
	}


}
