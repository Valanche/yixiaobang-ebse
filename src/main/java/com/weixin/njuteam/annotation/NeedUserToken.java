package com.weixin.njuteam.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 需要jwt token认证的注解
 * 请在需要token的controller方法上标上该注解即可
 * 该注解只会在运行时被检查
 *
 * @author Zyi
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface NeedUserToken {

	/**
	 * 默认需要
	 *
	 * @return 返回是否需要jwt token认证
	 */
	boolean isRequired() default true;
}
