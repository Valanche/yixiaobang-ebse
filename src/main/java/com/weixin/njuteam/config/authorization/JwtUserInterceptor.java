package com.weixin.njuteam.config.authorization;

import com.weixin.njuteam.annotation.NeedUserToken;
import com.weixin.njuteam.entity.vo.UserVO;
import com.weixin.njuteam.service.UserService;
import com.weixin.njuteam.util.HttpResponseUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * 自定义的Token拦截器
 * 拦截请求的Header中的"Authorization"
 *
 * @author Zyi
 */
@Component
@Slf4j
public class JwtUserInterceptor implements HandlerInterceptor {

	private final JwtUserConfig jwtUserConfig;
	private final UserService userService;

	@Autowired
	public JwtUserInterceptor(JwtUserConfig jwtUserConfig, UserService userService) {
		this.jwtUserConfig = jwtUserConfig;
		this.userService = userService;
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		if (!(handler instanceof HandlerMethod)) {
			// 如果不是映射到方法直接通过
			return true;
		}

		String token = request.getHeader(HttpHeaders.AUTHORIZATION);

		HandlerMethod handlerMethod = (HandlerMethod) handler;
		Method method = handlerMethod.getMethod();

		// 检查有没有需要用户权限的注解
		if (method.isAnnotationPresent(NeedUserToken.class)) {
			NeedUserToken needUserToken = method.getAnnotation(NeedUserToken.class);

			// 判断是否需要token
			if (needUserToken.isRequired()) {
				// 执行认证
				if (token == null || token.trim().isEmpty()) {
					// 没有token 说明需要登录
					HttpResponseUtil.returnJson(response, "没有token", null);
					return false;
				}

				// 校验token是否有效
				if (jwtUserConfig == null) {
					log.error("JWTConfig 自动注入失败");
					throw new NullPointerException("JWTConfig 自动注入失败");
				}

				Claims claims = jwtUserConfig.parseUserJwt(token);

				if (claims != null && !jwtUserConfig.isExpiration(token)) {
					// 说明token是有效的
					String openId = claims.getSubject();
					if (openId != null && !openId.trim().isEmpty()) {
						UserVO userVO = userService.queryUser(openId);

						if (userVO == null) {
							return false;
						}

						request.setAttribute("curUserInfo", userVO);
						return true;
					} else {
						log.info("用户openid有问题");
						return false;
					}
				} else {
					// token过期了
					// 返回400给前端
					HttpResponseUtil.returnJson(response, "用户令牌token无效", null);
					return false;
				}
			}
		}

		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) throws Exception {
		// do not need to handle after request
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
		// do not need to handler after request complete
	}
}
