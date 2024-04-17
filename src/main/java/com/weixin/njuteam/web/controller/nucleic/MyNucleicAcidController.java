package com.weixin.njuteam.web.controller.nucleic;

import com.weixin.njuteam.annotation.CurUserInfo;
import com.weixin.njuteam.annotation.NeedUserToken;
import com.weixin.njuteam.entity.vo.UserVO;
import com.weixin.njuteam.entity.vo.nucleic.NucleicAcidVO;
import com.weixin.njuteam.enums.StatusCode;
import com.weixin.njuteam.service.NucleicAcidService;
import com.weixin.njuteam.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Zyi
 */
@RestController
@RequestMapping("/nucleic-acid")
@Api(value = "获取用户的核酸信息", tags = {"用户核酸信息有关接口"})
public class MyNucleicAcidController {

	private final NucleicAcidService service;

	@Autowired
	public MyNucleicAcidController(NucleicAcidService service) {
		this.service = service;
	}

	@NeedUserToken
	@GetMapping("/schedule")
	@ApiOperation(value = "获取用户的核酸三个通知完成状态列表 need token")
	public Response<List<NucleicAcidVO>> getUserNucleicAcid(@CurUserInfo UserVO userVo) {
		String openId = userVo.getOpenId();

		List<NucleicAcidVO> list = service.getUserNucleicAcid(openId);
		if (list == null) {
			return new Response<>(StatusCode.INTERNAL_SERVER_ERROR, "获取用户核酸列表失败");
		}

		return new Response<>(StatusCode.OK, "获取用户核酸列表成功", list);
	}

}
