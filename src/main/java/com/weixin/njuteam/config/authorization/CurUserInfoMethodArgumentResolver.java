package com.weixin.njuteam.config.authorization;

import com.weixin.njuteam.annotation.CurUserInfo;
import com.weixin.njuteam.entity.vo.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

/**
 * 通过CurUserInfo注解来获取Token中的用户信息
 *
 * @author Zyi
 */
@Component
@Slf4j
public class CurUserInfoMethodArgumentResolver implements HandlerMethodArgumentResolver {

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		// 需要参数类型是UserVO且参数被标记了@CurUserInfo注解
		return parameter.getParameterType().isAssignableFrom(UserVO.class) && parameter.hasParameterAnnotation(CurUserInfo.class);
	}

	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
		// 从拦截器中获取当前用户的信息
		UserVO userVO = (UserVO) webRequest.getAttribute("curUserInfo", RequestAttributes.SCOPE_REQUEST);

		if (userVO != null) {
			return userVO;
		}

		// 如果当前用户信息为null  则抛出异常
		log.error("from attribute get usre info is null");
		throw new MissingServletRequestPartException("UserVO is null");
	}
}
