package com.weixin.njuteam.web.controller.manager;

import com.weixin.njuteam.annotation.CurManagerInfo;
import com.weixin.njuteam.annotation.NeedManagerToken;
import com.weixin.njuteam.config.authorization.JwtManagerConfig;
import com.weixin.njuteam.entity.vo.manager.LoginManager;
import com.weixin.njuteam.entity.vo.manager.ManagerAndStudentVO;
import com.weixin.njuteam.entity.vo.manager.ManagerVO;
import com.weixin.njuteam.enums.StatusCode;
import com.weixin.njuteam.exception.ManagerExistException;
import com.weixin.njuteam.exception.NickNameDuplicateException;
import com.weixin.njuteam.exception.PasswordException;
import com.weixin.njuteam.service.ManagerService;
import com.weixin.njuteam.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Zyi
 */
@RestController
@RequestMapping("/manager")
@Api(value = "管理员有关接口", tags = {"管理员有关接口"})
@Slf4j
public class ManagerController {

	private static final String TOKEN_ERROR = "token获取管理员id失败";
	private final ManagerService managerService;
	private final JwtManagerConfig jwtManagerConfig;

	@Autowired
	public ManagerController(ManagerService managerService, JwtManagerConfig jwtManagerConfig) {
		this.managerService = managerService;
		this.jwtManagerConfig = jwtManagerConfig;
	}

	@NeedManagerToken
	@GetMapping("/info")
	@ApiOperation(value = "获得管理员信息 need token")
	public Response<ManagerVO> getManagerInfo(@CurManagerInfo ManagerVO managerVo) {
		Long id = managerVo.getId();
		if (id == null || id <= 0) {
			return new Response<>(StatusCode.BAD_REQUEST, TOKEN_ERROR);
		}
		ManagerVO manager = managerService.queryManagerById(id);

		return manager == null ? new Response<>(StatusCode.INTERNAL_SERVER_ERROR, "获取管理员信息失败", null) : new Response<>(StatusCode.OK, "获取管理员信息成功", manager);
	}

	@NeedManagerToken
	@GetMapping("/student-list")
	@ApiModelProperty(value = "获得学生信息 need token")
	public Response<ManagerAndStudentVO> getStudentList(@CurManagerInfo ManagerVO managerVo) {
		Long id = managerVo.getId();
		if (id == null || id <= 0) {
			return new Response<>(StatusCode.BAD_REQUEST, TOKEN_ERROR);
		}
		ManagerAndStudentVO studentList = managerService.queryStudentList(id);

		return studentList == null ? new Response<>(StatusCode.INTERNAL_SERVER_ERROR, "获取学生信息列表失败", null) : new Response<>(StatusCode.OK, "获取学生信息列表成功", studentList);
	}

	@PostMapping("/register")
	@ApiOperation(value = "注册")
	public Response<ManagerVO> managerRegister(@ApiParam(value = "管理员信息", name = "managerVo", example = "xxx") @RequestBody ManagerVO managerVo) {
		ManagerVO registerManager = null;
		try {
			registerManager = managerService.register(managerVo);
		} catch (PasswordException e) {
			return new Response<>(StatusCode.BAD_REQUEST, "用户密码不能为空!");
		} catch (NickNameDuplicateException e) {
			return new Response<>(StatusCode.BAD_REQUEST, "用户名重复");
		} catch (ManagerExistException e) {
			return new Response<>(StatusCode.BAD_REQUEST, "用户信息已存在");
		}

		return registerManager == null ? new Response<>(StatusCode.INTERNAL_SERVER_ERROR, "管理员注册失败") : new Response<>(StatusCode.OK, "管理员注册成功", registerManager);
	}

	@PostMapping("/login")
	@ApiOperation(value = "管理员登录并获取token")
	public Response<Map<String, String>> managerLogin(@ApiParam(value = "登录的管理员", name = "loginManager", example = "xxx") @RequestBody LoginManager loginManager) {
		if (loginManager.getNickName() == null || loginManager.getPassword() == null) {
			return new Response<>(StatusCode.BAD_REQUEST, "用户名和密码不能为空!");
		}
		ManagerVO manager = null;
		try {
			manager = managerService.authLogin(loginManager.getNickName(), loginManager.getPassword());
		} catch (PasswordException e) {
			return new Response<>(StatusCode.BAD_REQUEST, "用户密码错误！");
		}

		if (manager == null || manager.getId() == null) {
			return new Response<>(StatusCode.BAD_REQUEST, "用户未注册");
		}

		// 生成token
		Map<String, String> authToken = new HashMap<>();
		String token = jwtManagerConfig.createMangerJwt(manager);
		authToken.put("token", token);

		return new Response<>(StatusCode.OK, "用户认证成功", authToken);
	}

	@PostMapping("/wechat-login")
	@ApiOperation(value = "验证某个用户的登录并获取token")
	public Response<Map<String, String>> managerWechatLogin(@ApiParam(value = "管理员信息", name = "managerVo", example = "xxx", required = true) @RequestBody ManagerVO managerVo) {
		if (managerVo == null) {
			return new Response<>(StatusCode.BAD_REQUEST, "用户信息为空");
		}
		ManagerVO manager = null;
		try {
			manager = managerService.authLogin(managerVo);
		} catch (SQLException e) {
			log.error("新用户插入失败");
		} catch (NickNameDuplicateException e) {
			return new Response<>(StatusCode.BAD_REQUEST, "管理员用户名不可重复");
		}

		if (manager == null || manager.getOpenId() == null) {
			return new Response<>(StatusCode.BAD_REQUEST, "用户openid不能为空！", null);
		}

		Map<String, String> authToken = new HashMap<>();

		// 生成token
		String token = jwtManagerConfig.createMangerJwt(manager);
		authToken.put("token", token);

		return new Response<>(StatusCode.OK, "用户认证成功", authToken);
	}

	@NeedManagerToken
	@PostMapping("/update")
	@ApiOperation(value = "更新管理员信息 need token")
	public Response<ManagerVO> updateManagerInfo(@CurManagerInfo ManagerVO managerId, @RequestBody ManagerVO managerVo) {
		Long id = managerId.getId();
		if (id == null || id <= 0) {
			return new Response<>(StatusCode.BAD_REQUEST, TOKEN_ERROR);
		}
		if (managerVo == null) {
			return new Response<>(StatusCode.BAD_REQUEST, "更新管理员信息不能为空!", null);
		}

		ManagerVO updateManager = managerService.updateManagerInfo(id, managerVo);
		return updateManager == null ? new Response<>(StatusCode.INTERNAL_SERVER_ERROR, "更新管理员信息失败", null) : new Response<>(StatusCode.OK, "更新管理员信息成功", updateManager);
	}

	@NeedManagerToken
	@PostMapping("/change-password")
	@ApiOperation(value = "更新管理员密码 need token")
	public Response<ManagerVO> updateManagerPassword(@CurManagerInfo ManagerVO managerVo, @RequestParam("password") String password) {
		Long id = managerVo.getId();
		if (id == null || id <= 0) {
			return new Response<>(StatusCode.BAD_REQUEST, TOKEN_ERROR);
		}

		ManagerVO updateManager = managerService.updateManagerPassword(id, password);
		return updateManager == null ? new Response<>(StatusCode.INTERNAL_SERVER_ERROR, "更新管理员密码失败", null) : new Response<>(StatusCode.OK, "更新管理员密码成功", updateManager);
	}

	@NeedManagerToken
	@DeleteMapping("/delete")
	@ApiOperation(value = "删除管理员信息 need token")
	public Response<Boolean> deleteManagerInfo(@CurManagerInfo ManagerVO managerVo) {
		Long id = managerVo.getId();
		if (id == null || id <= 0) {
			return new Response<>(StatusCode.BAD_REQUEST, TOKEN_ERROR);
		}
		boolean isDeleted = managerService.deleteManager(id);
		return isDeleted ? new Response<>(StatusCode.OK, "删除管理员信息成功", true) : new Response<>(StatusCode.INTERNAL_SERVER_ERROR, "删除管理员信息失败", false);
	}
}
