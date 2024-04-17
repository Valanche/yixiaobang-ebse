package com.weixin.njuteam.util;

import com.alibaba.fastjson.JSON;
import com.weixin.njuteam.enums.StatusCode;
import com.weixin.njuteam.web.Response;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Zyi
 */
@Slf4j
public class HttpResponseUtil {

	private HttpResponseUtil() {

	}

	public static void returnJson(HttpServletResponse response, String msg, Object data) {
		response.setCharacterEncoding("UTF-8");
		// 设置response返回json
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");

		try {
			Response<Object> res = Response.builder()
				.code(StatusCode.UNAUTHORIZED)
				.msg(msg)
				.data(data)
				.build();
			response.getWriter().print(JSON.toJSONString(res));
			response.getWriter().flush();
		} catch (IOException e) {
			log.error("return json error in http response");
		}
	}
}
