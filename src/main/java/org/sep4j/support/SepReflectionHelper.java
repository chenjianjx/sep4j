package org.sep4j.support;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.MessageFormat;

import org.apache.commons.lang.StringUtils;

/**
 * help with reflection. commons-beanutils have too many limitations (for
 * example, accessing properties of a private internal class). So we write this.
 * 
 * @author chenjianjx
 */
public class SepReflectionHelper {

	/**
	 * get property value through getter methods
	 * 
	 * @param object
	 * @param propName
	 * @return
	 * @throws RuntimeException
	 */
	public static Object getProperty(Object object, String propName) throws RuntimeException {
		if (object == null) {
			throw new IllegalArgumentException("The object cannot be null");
		}
		if (propName == null) {
			throw new IllegalArgumentException("The propName cannot be null");
		}

		Class<?> clazz = object.getClass();
		Method getter = findGetterByPropName(clazz, propName);
		if (getter == null) {
			String err = MessageFormat.format("Class {0} has no getter method for property \"{1}\"", clazz, propName);
			throw new IllegalArgumentException(err);
		}
		return invokeGetter(getter, object);
	}

	public static Method findSetterByPropNameAndType(Class<?> objClass, String propName, Class<?> propClass) {
		if (objClass == null) {
			throw new IllegalArgumentException("The objClass cannot be null");
		}
		if (propName == null) {
			throw new IllegalArgumentException("The propName cannot be null");
		}
		if (propClass == null) {
			throw new IllegalArgumentException("The propClass cannot be null");
		}

		try {
			return objClass.getMethod("set" + StringUtils.capitalize(propName), new Class<?>[] { propClass });
		} catch (SecurityException e) {
			throw new IllegalStateException(e);
		} catch (NoSuchMethodException e) {
			return null;
		}
	}

	private static Object invokeGetter(Method getter, Object object) {
		try {
			return getter.invoke(object, new Object[0]);
		} catch (IllegalAccessException e) {
			throw new IllegalStateException(e);
		} catch (InvocationTargetException e) {
			throw new IllegalStateException(e);
		}
	}

	public static void invokeSetter(Method setter, Object object, Object propValue) {
		try {
			setter.invoke(object, new Object[] { propValue });
		} catch (IllegalAccessException e) {
			throw new IllegalStateException(e);
		} catch (InvocationTargetException e) {
			throw new IllegalStateException(e);
		}
	}

	private static Method findGetterByPropName(Class<?> clazz, String propName) {
		Method get = findGet(clazz, propName);
		if (get != null) {
			return get;
		}

		Method is = findIsReturningPrimitiveBoolean(clazz, propName);
		if (is != null) {
			return is;
		}

		return null;
	}

	private static Method findIsReturningPrimitiveBoolean(Class<?> clazz, String propName) {
		try {
			Method is = clazz.getMethod("is" + StringUtils.capitalize(propName), new Class<?>[0]);
			if (is.getReturnType().equals(boolean.class)) {
				return is;
			}
			return null;
		} catch (SecurityException e) {
			throw new IllegalStateException(e);
		} catch (NoSuchMethodException e) {
			return null;
		}
	}

	private static Method findGet(Class<?> clazz, String propName) {
		try {
			return clazz.getMethod("get" + StringUtils.capitalize(propName), new Class<?>[0]);
		} catch (SecurityException e) {
			throw new IllegalStateException(e);
		} catch (NoSuchMethodException e) {
			return null;
		}
	}

}
