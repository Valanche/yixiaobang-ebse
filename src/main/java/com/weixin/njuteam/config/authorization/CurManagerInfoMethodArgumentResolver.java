package com.weixin.njuteam.config.authorization;

import com.weixin.njuteam.annotation.CurManagerInfo;
import com.weixin.njuteam.entity.vo.manager.ManagerVO;
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
 * @author Zyi
 */
@Component
@Slf4j
public class CurManagerInfoMethodArgumentResolver implements HandlerMethodArgumentResolver {

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		// 判断parameter是否有 @CurManagerInfo 注解
		// 判断parameter是否是 ManagerVO 类型
		return parameter.getParameterType().isAssignableFrom(ManagerVO.class) && parameter.hasParameterAnnotation(CurManagerInfo.class);
	}

	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
		// 从拦截器中获取用户信息并保存到parameter中
		ManagerVO managerVo = (ManagerVO) webRequest.getAttribute("curManagerInfo", RequestAttributes.SCOPE_REQUEST);

		// 放置到parameter中
		if (managerVo != null) {
			return managerVo;
		}

		log.error("from attribute get Manager info error!");
		throw new MissingServletRequestPartException("ManagerVO is null!");
	}
}
