package com.weixin.njuteam.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Zyi
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface NeedManagerToken {

	/**
	 * is need token
	 *
	 * @return true if the method need token, false otherwise
	 */
	boolean isRequired() default true;
}
