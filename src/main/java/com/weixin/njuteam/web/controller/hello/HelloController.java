package com.weixin.njuteam.web.controller.hello;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RequestMapping("/hello")
public class HelloController {
	@RequestMapping("/hello")
	@ResponseBody
	public String hello(){
		return "Hello father mocker";
	}
}
