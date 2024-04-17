package com.weixin.njuteam.web.controller;

import com.weixin.njuteam.annotation.CurUserInfo;
import com.weixin.njuteam.annotation.NeedUserToken;
import com.weixin.njuteam.config.authorization.JwtUserConfig;
import com.weixin.njuteam.entity.vo.FriendVO;
import com.weixin.njuteam.entity.vo.UserChatHistoryVO;
import com.weixin.njuteam.entity.vo.UserVO;
import com.weixin.njuteam.enums.StatusCode;
import com.weixin.njuteam.service.UserService;
import com.weixin.njuteam.util.Pair;
import com.weixin.njuteam.web.Response;
import io.jsonwebtoken.Claims;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Zyi
 */
@RestController
@RequestMapping("/user")
@Slf4j
@Api(value = "用户有关接口")
public class UserController {

	private final UserService userService;
	private final JwtUserConfig jwtUserConfig;

	@Autowired
	public UserController(UserService userService, JwtUserConfig jwtUserConfig) {
		this.userService = userService;
		this.jwtUserConfig = jwtUserConfig;
	}

	@GetMapping("/sessionId")
	@ApiOperation(value = "获取某个用户的openid和session_key")
	public String getSessionId(@ApiParam(value = "通过wx.login()获得的code", required = true) @RequestParam(name = "code") String code) {
		return userService.getSessionId(code);
	}

	@GetMapping("/access-token")
	@ApiOperation(value = "获取access_token")
	public Response<String> getAccessToken() {
		log.info("activated-ACCESS_TOKEN");
		return new Response<>(StatusCode.OK, "获取接口调用凭证成功", userService.getAccessToken());
	}

	@PostMapping("/login")
	@ApiOperation(value = "验证某个用户的登录并获取token")
	public Response<Map<String, String>> userLogin(@ApiParam(value = "用户信息", name = "userVo", example = "xxx", required = true) @RequestBody UserVO userVO) {
		if (userVO == null) {
			return new Response<>(StatusCode.BAD_REQUEST, "用户信息为空");
		}
		UserVO user = null;
		try {
			user = userService.authLogin(userVO);
		} catch (SQLException e) {
			log.error("新用户插入失败");
		}

		if (user == null || user.getOpenId() == null) {
			return new Response<>(StatusCode.BAD_REQUEST, "用户openid不能为空！", null);
		}

		Map<String, String> authToken = new HashMap<>();

		// 生成token
		String token = jwtUserConfig.createUserJwt(user);
		authToken.put("token", token);

		return new Response<>(StatusCode.OK, "用户认证成功", authToken);
	}

	@NeedUserToken
	@GetMapping("/info")
	@ApiOperation(value = "通过token获得某个用户信息 请优先调用该接口")
	public Response<UserVO> getUserInfo(@CurUserInfo UserVO userVo) {
		String openId = userVo.getOpenId();
		UserVO user = userService.queryUser(openId);

		return user == null ? new Response<>(StatusCode.INTERNAL_SERVER_ERROR, "获取用户信息失败") : new Response<>(StatusCode.OK, "获取用户信息成功", user);
	}

	@GetMapping("/auth")
	@ApiOperation(value = "通过token来获取某个用户的信息 请优先使用头部传token的方式")
	public Response<UserVO> userAuth(@ApiParam(value = "token", required = true) @RequestParam(name = "token") String token) {
		Claims claims = jwtUserConfig.parseUserJwt(token);
		String openId = claims.get("openID", String.class);
		UserVO userVO = userService.queryUser(openId);
		return userVO == null ? new Response<>(StatusCode.OK, "认证token失败") : new Response<>(StatusCode.OK, "认证token成功", userVO);
	}

	@NeedUserToken
	@PostMapping("/query")
	@ApiOperation(value = "获得某个用户的信息 need token")
	public Response<UserVO> queryUser(@CurUserInfo UserVO userVO) {
		// 这里通过token拿到userVO信息，前端不需要再传userVO进来，只需要在header里加上token信息即可
		// token里仅仅保存了用户的openID
		UserVO newUser = userService.queryUser(userVO.getOpenId());

		return newUser == null ? new Response<>(StatusCode.INTERNAL_SERVER_ERROR, "获取用户信息失败") : new Response<>(StatusCode.OK, "获取用户信息成功", newUser);
	}

	@NeedUserToken
	@PostMapping("/update")
	@ApiOperation(value = "更新某个用户的信息 need token")
	public Response<UserVO> updateUser(@CurUserInfo UserVO token, @ApiParam(value = "新的用户信息 可以不用带openid和userid", required = true) @RequestBody UserVO userVO) {
		// 更新的信息不一定要包括全部的user内容，只要有更新的信息 + openID 即可
		userVO.setOpenId(token.getOpenId());
		UserVO newUser = userService.updateUser(userVO);

		if (newUser == null) {
			return new Response<>(StatusCode.INTERNAL_SERVER_ERROR, "更新用户信息失败");
		}

		return new Response<>(StatusCode.OK, "更新用户信息成功", newUser);
	}

	@NeedUserToken
	@GetMapping("/friend-list")
	@ApiOperation(value = "获取用户的好友列表 need token")
	public Response<List<Pair<Integer, UserChatHistoryVO>>> getUserFriendList(@CurUserInfo UserVO userVO) {
		List<Pair<Integer, UserChatHistoryVO>> chatHistoryAndNoReadAmountPairList = userService.queryChatHistory(userVO.getOpenId());

		if (chatHistoryAndNoReadAmountPairList == null) {
			return new Response<>(StatusCode.BAD_REQUEST, "获取好友列表失败");
		}

		return new Response<>(StatusCode.OK, "获取好友列表成功", chatHistoryAndNoReadAmountPairList);
	}

	@GetMapping("/info/{id}")
	public Response<FriendVO> queryFriend(@PathVariable Long id) {
		FriendVO friendVO = userService.queryFriend(id);

		return friendVO == null ? new Response<>(StatusCode.BAD_REQUEST, "获取信息失败") : new Response<>(StatusCode.OK, "获取信息成功", friendVO);
	}

	@DeleteMapping("/delete/{openId}")
	@ApiOperation(value = "删除某个用户")
	public Response<Boolean> deleteUser(@ApiParam(value = "用户openId", name = "openId", example = "xxx") @PathVariable String openId) {
		boolean isDeleted = userService.deleteUser(openId);

		return isDeleted ? new Response<>(StatusCode.OK, "删除用户成功", true) : new Response<>(StatusCode.INTERNAL_SERVER_ERROR, "删除用户失败", false);
	}
}
