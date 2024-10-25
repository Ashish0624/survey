package com.ncert.survey.utils;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.log4j.Logger;

public class SafeFieldUtils {
	private static final Logger logger = Logger.getLogger(SafeFieldUtils.class);

	@SuppressWarnings("unchecked")
	public static <T> T readField(Object target, String fieldName, boolean forceAccess, Class<T> clazz) {
		Object o = null;
		try {
			if (FieldUtils.getField(target.getClass(), fieldName, forceAccess) != null) {
				o = FieldUtils.readField(target, fieldName, forceAccess);
			}
		} catch (IllegalAccessException e) {
			logger.error("IllegalAccess access field " + fieldName + " on target " + target.getClass().getName());
		} catch (IllegalArgumentException e) {
			logger.warn(
					"IllegalArgumentException access field " + fieldName + " on target " + target.getClass().getName());
		}
		if (o != null && clazz.isAssignableFrom(o.getClass())) {
			return (T) o;
		} else {
			return null;
		}
	}
}
