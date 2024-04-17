package com.weixin.njuteam.util;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.beans.PropertyDescriptor;
import java.util.HashSet;
import java.util.Set;

/**
 * 忽略null属性
 *
 * @author Zyi
 */
public class IgnoreUtil {

	private IgnoreUtil() {

	}

	/**
	 * Gets the properties to be ignored
	 *
	 * @param source source bean
	 * @return the properties whose value is null
	 */
	public static String[] getNullPropertyNames(Object source) {
		final BeanWrapper src = new BeanWrapperImpl(source);
		PropertyDescriptor[] pds = src.getPropertyDescriptors();

		Set<String> emptyNames = new HashSet<>();
		for (PropertyDescriptor pd : pds) {
			Object srcValue = src.getPropertyValue(pd.getName());
			//  The judgment here can be modified according to the demand
			if (srcValue == null) {
				emptyNames.add(pd.getName());
			}
		}

		String[] result = new String[emptyNames.size()];
		return emptyNames.toArray(result);
	}
}
