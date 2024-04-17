package com.weixin.njuteam.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 通过token来获取当前用户信息
 * 注解在Controller方法的参数上
 * 运行时检测
 *
 * @author Zyi
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface CurUserInfo {

	boolean isParseInfo() default true;
}
