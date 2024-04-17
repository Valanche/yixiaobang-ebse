package com.weixin.njuteam.config;

import com.weixin.njuteam.config.authorization.CurManagerInfoMethodArgumentResolver;
import com.weixin.njuteam.config.authorization.CurUserInfoMethodArgumentResolver;
import com.weixin.njuteam.config.authorization.JwtManagerInterceptor;
import com.weixin.njuteam.config.authorization.JwtUserInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.*;

import java.util.List;

/**
 * @author Zyi
 */
@Configuration
public class WebResourceConfig implements WebMvcConfigurer {

	private final JwtUserInterceptor jwtUserInterceptor;
	private final CurUserInfoMethodArgumentResolver curUserInfoMethodArgumentResolver;
	private final JwtManagerInterceptor jwtManagerInterceptor;
	private final CurManagerInfoMethodArgumentResolver curManagerInfoMethodArgumentResolver;
	private final StringTypeConverterFactory typeConverterFactory;

	@Autowired
	public WebResourceConfig(JwtUserInterceptor jwtUserInterceptor,
							 CurUserInfoMethodArgumentResolver curUserInfoMethodArgumentResolver,
							 JwtManagerInterceptor jwtManagerInterceptor,
							 CurManagerInfoMethodArgumentResolver curManagerInfoMethodArgumentResolver,
							 StringTypeConverterFactory typeConverterFactory) {
		this.jwtUserInterceptor = jwtUserInterceptor;
		this.curUserInfoMethodArgumentResolver = curUserInfoMethodArgumentResolver;
		this.jwtManagerInterceptor = jwtManagerInterceptor;
		this.curManagerInfoMethodArgumentResolver = curManagerInfoMethodArgumentResolver;
		this.typeConverterFactory = typeConverterFactory;
	}

	@Override
	public void configurePathMatch(PathMatchConfigurer configurer) {
		// 为所有的RestController暴露的服务接口url 添加前缀/api
		configurer.addPathPrefix("/api", c -> c.isAnnotationPresent(RestController.class));
	}

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
		// 参数的处理器
		// 用以处理@CurUserInfo
		resolvers.add(curUserInfoMethodArgumentResolver);
		resolvers.add(curManagerInfoMethodArgumentResolver);
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		// 添加拦截器
		registry.addInterceptor(jwtUserInterceptor).addPathPatterns("/**");
		registry.addInterceptor(jwtManagerInterceptor).addPathPatterns("/**");
	}

	@Override
	public void addFormatters(FormatterRegistry registry) {
		// 添加类型转换
		registry.addConverterFactory(typeConverterFactory);
	}

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		// 跨域配置
		registry.addMapping("/**")
			.allowedOriginPatterns("*")
			// 允许POST GET PUT DELETE OPTIONS请求
			.allowedMethods("POST", "GET", "PUT", "DELETE", "OPTIONS")
			// 允许token放置于请求头
			.exposedHeaders(HttpHeaders.AUTHORIZATION)
			.maxAge(3600)
			.allowCredentials(true);
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		// 静态资源配置
		registry.addResourceHandler("/HelpImg/**").addResourceLocations("file:/root/WeixinData/helpImage/");
		registry.addResourceHandler("/NAImg/**").addResourceLocations("file:/root/WeixinData/image/");
		registry.addResourceHandler("/SeekHelpImg/**").addResourceLocations("file:/root/WeixinData/seekHelpImage/");
	}
}
