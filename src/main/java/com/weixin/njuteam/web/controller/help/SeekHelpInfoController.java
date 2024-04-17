package com.weixin.njuteam.web.controller.help;

import com.weixin.njuteam.annotation.CurUserInfo;
import com.weixin.njuteam.annotation.NeedUserToken;
import com.weixin.njuteam.entity.po.help.SeekHelpImage;
import com.weixin.njuteam.entity.vo.UserVO;
import com.weixin.njuteam.entity.vo.help.SeekHelpInfoAndUserVO;
import com.weixin.njuteam.entity.vo.help.SeekHelpInfoClickVO;
import com.weixin.njuteam.entity.vo.help.SeekHelpInfoVO;
import com.weixin.njuteam.entity.vo.help.SeekHelpSearchHistoryVO;
import com.weixin.njuteam.enums.SeekHelpFinishStatus;
import com.weixin.njuteam.enums.StatusCode;
import com.weixin.njuteam.exception.FormatException;
import com.weixin.njuteam.service.SeekHelpInfoService;
import com.weixin.njuteam.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * @author Zyi
 */
@RestController
@RequestMapping("/seek-help")
@Api(value = "求助信息有关接口", tags = {"求助信息接口"})
@Slf4j
public class SeekHelpInfoController {

	private static final String ID_ERROR = "id传输错误";
	private final SeekHelpInfoService seekInfoService;

	@Autowired
	public SeekHelpInfoController(SeekHelpInfoService seekInfoService) {
		this.seekInfoService = seekInfoService;
	}

	@GetMapping("/query/{id}")
	@ApiOperation(value = "通过id来获取某条求助信息")
	public Response<SeekHelpInfoAndUserVO> getSeekInfo(@ApiParam(value = "求助信息id", name = "id", example = "1") @PathVariable Long id) {
		if (id == null || id <= 0) {
			return new Response<>(StatusCode.BAD_REQUEST, ID_ERROR);
		}
		SeekHelpInfoAndUserVO helpInfo = seekInfoService.querySeekInfoById(id);

		return helpInfo == null ? new Response<>(StatusCode.INTERNAL_SERVER_ERROR, "获取求助信息失败") : new Response<>(StatusCode.OK, "获取求助信息成功", helpInfo);
	}

	@NeedUserToken
	@GetMapping("/user-list")
	@ApiOperation(value = "获得某个用户的求助信息 need token")
	public Response<List<SeekHelpInfoVO>> getUserSeekInfo(@CurUserInfo UserVO userVo) {
		String openId = userVo.getOpenId();
		List<SeekHelpInfoVO> helpInfoList = seekInfoService.querySeekInfoByUserId(openId);

		if (helpInfoList == null) {
			return new Response<>(StatusCode.INTERNAL_SERVER_ERROR, "获取用户求助信息失败");
		}

		return new Response<>(StatusCode.OK, "获取用户求助信息成功", helpInfoList);
	}

	@NeedUserToken
	@GetMapping("/tag")
	@ApiOperation(value = "获得按tag分类的所有帮助信息 need token")
	public Response<Map<String, List<SeekHelpInfoAndUserVO>>> getSeekInfoGroupByTag(@CurUserInfo UserVO userVo) {
		Map<String, List<SeekHelpInfoAndUserVO>> tagHelpInfoMap = seekInfoService.getSeekInfoGroupByTag();

		if (tagHelpInfoMap == null) {
			return new Response<>(StatusCode.INTERNAL_SERVER_ERROR, "获取所有帮助信息失败");
		}

		return new Response<>(StatusCode.OK, "获取所有帮助信息成功", tagHelpInfoMap);
	}

	@NeedUserToken
	@GetMapping("/recommend")
	@ApiOperation(value = "获得推荐内容 need token")
	public Response<List<SeekHelpInfoAndUserVO>> getRecommendSeekHelpInfo(@CurUserInfo UserVO user) {
		List<SeekHelpInfoAndUserVO> helpInfoList = seekInfoService.getRecommendList(user.getOpenId());

		if (helpInfoList == null) {
			return new Response<>(StatusCode.INTERNAL_SERVER_ERROR, "获取推荐内容失败");
		}

		return new Response<>(StatusCode.OK, "获取推荐内容成功", helpInfoList);
	}

	@NeedUserToken
	@GetMapping("/search/{keyword}")
	@ApiOperation(value = "通过关键词搜索求助信息 need token")
	public Response<List<SeekHelpInfoAndUserVO>> getSearchSeekResult(@CurUserInfo UserVO userVo, @ApiParam(value = "搜索关键词", name = "keyword", example = "ababa") @PathVariable String keyword) {
		List<SeekHelpInfoAndUserVO> seekInfoList = seekInfoService.searchSeekInfo(userVo.getOpenId(), keyword);

		if (seekInfoList == null) {
			return new Response<>(StatusCode.INTERNAL_SERVER_ERROR, "关键词搜索失败");
		}

		return new Response<>(StatusCode.OK, "关键词搜索成功", seekInfoList);
	}

	@NeedUserToken
	@GetMapping("/history/{size}")
	@ApiOperation(value = "获得用户帮助信息的搜索记录 need token")
	public Response<List<SeekHelpSearchHistoryVO>> getUserSeekHelpSearchHistory(@CurUserInfo UserVO userVo, @ApiParam(value = "要获取的历史记录的数量", name = "size", example = "20") @PathVariable Integer size) {
		if (size <= 0 && size != -1) {
			return new Response<>(StatusCode.BAD_REQUEST, "获取数量应该大于0");
		}

		List<SeekHelpSearchHistoryVO> searchList = seekInfoService.querySearchHistory(userVo.getOpenId(), size);
		if (searchList == null) {
			return new Response<>(StatusCode.INTERNAL_SERVER_ERROR, "获取历史记录失败");
		}

		return new Response<>(StatusCode.OK, "获取历史记录成功", searchList);
	}

	@NeedUserToken
	@PostMapping("/insert")
	@ApiOperation(value = "插入新的求助信息 need token")
	public Response<SeekHelpInfoVO> insertSeekInfo(@CurUserInfo UserVO userVo, @ApiParam(value = "要插入的求助信息 可以不需要userId", name = "seekInfo", example = "xxx") @RequestBody SeekHelpInfoVO seekInfo) {
		String openId = userVo.getOpenId();

		SeekHelpInfoVO newInfo = seekInfoService.insertSeekInfo(seekInfo, openId);
		if (newInfo == null) {
			return new Response<>(StatusCode.INTERNAL_SERVER_ERROR, "插入求助信息失败");
		}

		return new Response<>(StatusCode.OK, "插入求助信息成功", newInfo);
	}

	@NeedUserToken
	@PostMapping("/click/{seekHelpId}")
	@ApiOperation(value = "用户点击帮助页面时调用 need token")
	public Response<SeekHelpInfoClickVO> clickSeekHelpInfo(@CurUserInfo UserVO user, @ApiParam(value = "帮助信息id", name = "helpId", example = "1") @PathVariable Long seekHelpId) {
		if (seekHelpId == null || seekHelpId <= 0) {
			return new Response<>(StatusCode.BAD_REQUEST, ID_ERROR);
		}
		SeekHelpInfoClickVO clickVo = seekInfoService.clickHelpInfo(user.getOpenId(), seekHelpId);
		if (clickVo == null) {
			return new Response<>(StatusCode.INTERNAL_SERVER_ERROR, "用户点击页面失败");
		}

		return new Response<>(StatusCode.OK, "用户点击页面成功", clickVo);
	}

	@NeedUserToken
	@PostMapping("/end-click/{clickId}")
	@ApiOperation(value = "用户退出帮助页面时调用 need token")
	public Response<Boolean> endSeekClickHelpInfo(@CurUserInfo UserVO user, @ApiParam(value = "点击的id, 进入页面调用点击接口时会获得", name = "clickId", example = "1") @PathVariable Long clickId) {
		if (clickId == null || clickId <= 0) {
			return new Response<>(StatusCode.BAD_REQUEST, ID_ERROR);
		}
		boolean isEnd = seekInfoService.endClick(clickId);

		return isEnd ? new Response<>(StatusCode.OK, "用户退出页面更新成功", true) : new Response<>(StatusCode.INTERNAL_SERVER_ERROR, "用户退出页面更新失败", false);
	}

	@NeedUserToken
	@PostMapping("/upload/{id}")
	@ApiOperation(value = "插入求助信息的图片 请在调用/insert后调用该接口")
	public Response<List<SeekHelpImage>> uploadSeekImages(@CurUserInfo UserVO userVo, @ApiParam(value = "求助通知的id", name = "id", example = "1") @PathVariable Long id,
														  @ApiParam(value = "要插入的求助信息的图片", name = "imageList", example = "xxx") @RequestBody MultipartFile[] imageList) {
		if (id == null || id <= 0) {
			return new Response<>(StatusCode.BAD_REQUEST, ID_ERROR);
		}
		List<SeekHelpImage> newHelpImageList;
		try {
			newHelpImageList = seekInfoService.insertImageList(id, imageList);
		} catch (FormatException e) {
			return new Response<>(StatusCode.BAD_REQUEST, "图片格式错误, " + e.getMessage());
		}

		if (newHelpImageList == null) {
			return new Response<>(StatusCode.INTERNAL_SERVER_ERROR, "插入求助信息图片失败");
		}

		return new Response<>(StatusCode.OK, "插入求助信息成图片成功", newHelpImageList);
	}

	@NeedUserToken
	@PostMapping("/update")
	@ApiOperation(value = "更新求助信息 不包括更新图片 need token")
	public Response<SeekHelpInfoVO> updateSeekInfo(@CurUserInfo UserVO userVo, @ApiParam(value = "要更新的求助信息 可以不需要userId", name = "seekInfo", example = "xxx") @RequestBody SeekHelpInfoVO seekInfo) {
		String openId = userVo.getOpenId();

		SeekHelpInfoVO newSeekInfo = seekInfoService.updateSeekInfo(seekInfo, openId);
		if (newSeekInfo == null) {
			return new Response<>(StatusCode.INTERNAL_SERVER_ERROR, "更新求助信息失败");
		}

		return new Response<>(StatusCode.OK, "更新求助信息成功", newSeekInfo);
	}

	@NeedUserToken
	@PostMapping("/update-image/{id}")
	@ApiOperation(value = "更新图片 需要该求助信息的id need token")
	public Response<List<SeekHelpImage>> updateSeekImages(@CurUserInfo UserVO userVo, @ApiParam(value = "帮助通知的id", name = "id", example = "1") @PathVariable Long id,
														  @ApiParam(value = "要插入的求助信息的图片", name = "imageList", example = "xxx") @RequestBody MultipartFile[] imageList) {
		if (id == null || id <= 0) {
			return new Response<>(StatusCode.BAD_REQUEST, ID_ERROR);
		}
		List<SeekHelpImage> newHelpImageList;
		try {
			newHelpImageList = seekInfoService.updateImageList(id, imageList);
		} catch (FormatException e) {
			return new Response<>(StatusCode.BAD_REQUEST, "图片格式错误, " + e.getMessage());
		}
		if (newHelpImageList == null) {
			return new Response<>(StatusCode.INTERNAL_SERVER_ERROR, "更新求助信息图片失败");
		}

		return new Response<>(StatusCode.OK, "更新求助信息成图片成功", newHelpImageList);
	}

	@NeedUserToken
	@PostMapping("/update-status/{id}")
	@ApiOperation(value = "更新帮助信息的完成状态 need token")
	public Response<SeekHelpInfoVO> updateSeekHelpInfoFinishStatus(@CurUserInfo UserVO user,
																   @ApiParam(value = "帮助通知的id", name = "id", example = "1") @PathVariable Long id,
																   @ApiParam(value = "帮助通知要更新的完成状态", name = "finishStatus", example = "已截止") @RequestParam("finishStatus") SeekHelpFinishStatus finishStatus) {
		if (id == null || id <= 0) {
			return new Response<>(StatusCode.BAD_REQUEST, ID_ERROR);
		}
		// 更新信息的完成状态
		SeekHelpInfoVO helpInfo = seekInfoService.updateFinishStatus(id, finishStatus);

		if (helpInfo == null) {
			return new Response<>(StatusCode.INTERNAL_SERVER_ERROR, "更新帮助通知失败");
		}

		return new Response<>(StatusCode.OK, "更新帮助通知成功", helpInfo);
	}

	@DeleteMapping("/delete/{id}")
	@ApiOperation(value = "删除某个求助信息")
	public Response<Boolean> deleteSeekInfo(@ApiParam(value = "要删除的求助信息的id", name = "id", example = "1") @PathVariable Long id) {
		if (id == null || id <= 0) {
			return new Response<>(StatusCode.BAD_REQUEST, ID_ERROR);
		}
		boolean isDeleted = seekInfoService.deleteSeekInfo(id);

		return isDeleted ? new Response<>(StatusCode.OK, "删除帮助信息成功", true) : new Response<>(StatusCode.INTERNAL_SERVER_ERROR, "删除帮助信息失败", false);
	}

	@DeleteMapping("/delete-image/{imageId}")
	@ApiOperation(value = "删除某张图片")
	public Response<Boolean> deleteSeekImage(@ApiParam(value = "要删除的图片id", name = "imageId", example = "1") @PathVariable Long imageId) {
		if (imageId == null || imageId <= 0) {
			return new Response<>(StatusCode.BAD_REQUEST, ID_ERROR);
		}
		boolean isDeleted = seekInfoService.deleteImage(imageId);

		return isDeleted ? new Response<>(StatusCode.OK, "删除图片成功", true) : new Response<>(StatusCode.INTERNAL_SERVER_ERROR, "删除图片失败", false);
	}

	@NeedUserToken
	@DeleteMapping("/delete-history/{id}")
	@ApiOperation(value = "删除某次搜索历史记录")
	public Response<Boolean> deleteSearchSeekHelpInfoHistory(@CurUserInfo UserVO userVo, @ApiParam(value = "要删除的搜索历史记录关键词", name = "id", example = "1") @PathVariable Long id) {
		if (id == null || id <= 0) {
			return new Response<>(StatusCode.BAD_REQUEST, ID_ERROR);
		}
		boolean isDeleted = seekInfoService.deleteSearchHistory(id);

		return isDeleted ? new Response<>(StatusCode.OK, "删除历史成功", true) : new Response<>(StatusCode.INTERNAL_SERVER_ERROR, "删除历史记录失败", false);
	}

	@NeedUserToken
	@DeleteMapping("/delete-all-history")
	@ApiOperation(value = "删除用户所有的历史记录 need token")
	public Response<Boolean> deleteAllSearchSeekHelpInfoHistory(@CurUserInfo UserVO user) {
		boolean isDeleted = seekInfoService.deleteAllHistory(user.getOpenId());

		return isDeleted ? new Response<>(StatusCode.OK, "删除所有历史记录成功", true) : new Response<>(StatusCode.INTERNAL_SERVER_ERROR, "删除所有历史记录失败", false);
	}
}
