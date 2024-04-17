package com.weixin.njuteam.config.authorization;

import com.weixin.njuteam.annotation.NeedManagerToken;
import com.weixin.njuteam.entity.vo.manager.ManagerVO;
import com.weixin.njuteam.service.ManagerService;
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
 * 可以用切面来实现
 *
 * @author Zyi
 */
@Component
@Slf4j
public class JwtManagerInterceptor implements HandlerInterceptor {

	private final JwtManagerConfig jwtManagerConfig;
	private final ManagerService managerService;

	@Autowired
	public JwtManagerInterceptor(JwtManagerConfig jwtManagerConfig, ManagerService managerService) {
		this.jwtManagerConfig = jwtManagerConfig;
		this.managerService = managerService;
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		if (!(handler instanceof HandlerMethod)) {
			// 如果不是映射到方法的就是直接跳过
			return true;
		}

		// 获取header携带的token
		String token = request.getHeader(HttpHeaders.AUTHORIZATION);
		HandlerMethod handlerMethod = (HandlerMethod) handler;
		Method method = handlerMethod.getMethod();

		// 如果方法上有 @NeedToken 注解
		if (method.isAnnotationPresent(NeedManagerToken.class)) {
			NeedManagerToken needManagerToken = method.getAnnotation(NeedManagerToken.class);

			if (needManagerToken.isRequired()) {
				// 如果该方法需要token
				if (token == null || token.trim().isEmpty()) {
					// 说明token为空
					HttpResponseUtil.returnJson(response, "该请求没有token或者token为空", null);
					return false;
				}

				Claims claims = jwtManagerConfig.parseManagerJwt(token);

				if (claims != null && !jwtManagerConfig.isExpiration(token)) {
					// 说明token是有效的
					// 把id放到请求的attribute中 然后注入到@CurManagerInfo 标识的 ManagerVO 实体中
					String subject = claims.getSubject();
					if (subject != null && !subject.trim().isEmpty()) {
						String[] subjectList = subject.split(",");
						assert subjectList.length == 4;
						ManagerVO managerVo = managerService.queryManagerByInfo(subjectList[0], subjectList[1], subjectList[2], subjectList[3]);

						if (managerVo == null) {
							return false;
						}

						request.setAttribute("curManagerInfo", managerVo);
						return true;
					} else {
						log.info("manager id error!");
						return false;
					}
				} else {
					// token过期了
					// 返回400给前端
					HttpResponseUtil.returnJson(response, "管理员令牌token无效", null);
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
