package com.weixin.njuteam.web.controller.chat;

import com.weixin.njuteam.annotation.CurUserInfo;
import com.weixin.njuteam.annotation.NeedUserToken;
import com.weixin.njuteam.entity.vo.MessageVO;
import com.weixin.njuteam.entity.vo.UserVO;
import com.weixin.njuteam.enums.StatusCode;
import com.weixin.njuteam.service.MessageService;
import com.weixin.njuteam.service.UserService;
import com.weixin.njuteam.web.Response;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Zyi
 */
@RestController
@RequestMapping("/message")
@Slf4j
public class MessageController {

	private final MessageService messageService;
	private final UserService userService;

	@Autowired
	public MessageController(MessageService messageService, UserService userService) {
		this.messageService = messageService;
		this.userService = userService;
	}

	@NeedUserToken
	@GetMapping("/get-chat-history")
	@ApiOperation(value = "获取聊天记录")
	public Response<List<MessageVO>> getChatHistory(@ApiParam(value = "对方id", name = "friendId", example = "2") @RequestParam("friendId") Long friendId,
													@ApiParam(value = "页面序号(从0开始)", name = "pageNo", example = "1") @RequestParam("pageNo") int pageNo,
													@ApiParam(value = "页面大小", name = "pageSize", example = "10") @RequestParam("pageSize") int pageSize,
													@CurUserInfo UserVO userVo) {
		// query的时候默认按时间新的在前面排序 这样可以获取最新的页面
		long id = userService.queryUserId(userVo.getOpenId());
		List<MessageVO> messageList = messageService.queryChatHistory(id, friendId, pageNo, pageSize);

		return new Response<>(StatusCode.OK, "获取聊天记录成功", messageList);
	}
}
