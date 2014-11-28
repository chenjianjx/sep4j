package org.sep4j.support;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

/**
 * help with reflection. commons-beanutils have too many limitations (for
 * example, accessing properties of a private internal class is not supported).
 * So we write this.
 * 
 * @author chenjianjx
 */
public class SepReflectionHelper {

	/**
	 * get property value through getter methods. A runtime exception will be
	 * thrown if no getter found
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

	/**
	 * find a setter by a properti's name and type
	 * 
	 * @param objClass
	 * @param propName
	 * @param propClass
	 * @return
	 */
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

	/**
	 * a property may have several setters, each of which take a new parameter
	 * type
	 * 
	 * @param objClass
	 * @param propName
	 * @return
	 */
	public static List<Method> findSettersByPropName(Class<?> objClass, String propName) {
		if (objClass == null) {
			throw new IllegalArgumentException("The objClass cannot be null");
		}
		if (propName == null) {
			throw new IllegalArgumentException("The propName cannot be null");
		}

		List<Method> setters = new ArrayList<Method>();
		Method[] methodArray = objClass.getMethods();
		if (methodArray == null) {
			return setters;
		}
		for (Method method : methodArray) {
			if (method.getName().equals("set" + StringUtils.capitalize(propName))
					&& (method.getParameterTypes() != null && method.getParameterTypes().length == 1)) {
				setters.add(method);
			}
		}
		return setters;
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

	/**
	 * invoke a setter method
	 * 
	 * @param setter
	 * @param object
	 * @param propValue
	 */
	public static void invokeSetter(Method setter, Object object, Object propValue) {
		if (setter == null) {
			throw new IllegalArgumentException("The setter method cannot be null");
		}

		if (object == null) {
			throw new IllegalArgumentException("The object cannot be null");
		}

		try {
			setter.invoke(object, new Object[] { propValue });
		} catch (IllegalAccessException e) {
			throw new IllegalStateException(e);
		} catch (InvocationTargetException e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * find a getter
	 * 
	 * @param clazz
	 * @param propName
	 * @return
	 */
	private static Method findGetterByPropName(Class<?> clazz, String propName) {
		Method get = findGetLiterally(clazz, propName);
		if (get != null) {
			return get;
		}
		Method is = findIsLiterallyForBoolean(clazz, propName);
		if (is != null) {
			return is;
		}
		return null;
	}

	/**
	 * find a getter method for primitive boolean
	 * 
	 * @param clazz
	 * @param propName
	 * @return
	 */
	static Method findIsLiterallyForBoolean(Class<?> clazz, String propName) {
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

	/**
	 * find a getter method which starts "get"
	 * 
	 * @param clazz
	 * @param propName
	 * @return
	 */
	static Method findGetLiterally(Class<?> clazz, String propName) {
		try {
			return clazz.getMethod("get" + StringUtils.capitalize(propName), new Class<?>[0]);
		} catch (SecurityException e) {
			throw new IllegalStateException(e);
		} catch (NoSuchMethodException e) {
			return null;
		}
	}

}
