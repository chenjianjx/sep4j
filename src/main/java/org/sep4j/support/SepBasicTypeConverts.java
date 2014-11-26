package org.sep4j.support;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 
 * do type conversion for basic types
 * 
 * @author chenjianjx
 */
public class SepBasicTypeConverts {

	private static final Map<Class<?>, CanFromStringTypeMeta> CanFromStringTypeMetas = new LinkedHashMap<Class<?>, CanFromStringTypeMeta>();
	static {
		init();
	}

	private static void init() {
		addCanFromStringTypeMeta(new ShortType());
		addCanFromStringTypeMeta(new IntType());
		addCanFromStringTypeMeta(new LongType());
		addCanFromStringTypeMeta(new FloatType());
		addCanFromStringTypeMeta(new DoubleType());
		addCanFromStringTypeMeta(new BooleanType());

		addCanFromStringTypeMeta(new ShortObjectType());
		addCanFromStringTypeMeta(new IntegerObjectType());
		addCanFromStringTypeMeta(new LongObjectType());
		addCanFromStringTypeMeta(new FloatObjectType());
		addCanFromStringTypeMeta(new DoubleObjectType());
		addCanFromStringTypeMeta(new BooleanObjectType());

		addCanFromStringTypeMeta(new BigIntegerType());
		addCanFromStringTypeMeta(new BigDecimalType());

		addCanFromStringTypeMeta(new StringType());
	}

	private static void addCanFromStringTypeMeta(CanFromStringTypeMeta meta) {
		CanFromStringTypeMetas.put(meta.getType(), meta);
	}

	/**
	 * can this string be parsed as that type?
	 * 
	 * @param str
	 * @param targetType
	 * @return
	 */
	public static boolean canFromThisString(String str, Class<?> targetType) {
		CanFromStringTypeMeta typeMeta = CanFromStringTypeMetas.get(targetType);
		if (typeMeta == null) {
			return false;
		}
		return typeMeta.canFromThisString(str);
	}

	/**
	 * parse from this string. You need to call
	 * {@link #canFromThisString(String, Class)} first.
	 * 
	 * @param str
	 * @param targetType
	 * @return
	 */
	public static Object fromThisString(String str, Class<?> targetType) {
		if (!canFromThisString(str, targetType)) {
			throw new IllegalArgumentException("Please call fromThisString(String str, targetType) first to confirm");
		}
		CanFromStringTypeMeta typeMeta = CanFromStringTypeMetas.get(targetType);
		return typeMeta.fromThisString(str);
	}

	/**
	 * a wrapper for types that can be parsed from a string
	 * 
	 * 
	 */
	private static interface CanFromStringTypeMeta {

		public static final String ERR_PARSE_WITHOUT_ACCESS = "Please call fromThisString(String str) first to confirm";

		/**
		 * the type
		 * 
		 * @return
		 */
		public Class<?> getType();

		/**
		 * can it be parsed from this string?
		 * 
		 * @param str
		 * @return
		 */
		public boolean canFromThisString(String str);

		/**
		 * parse from this string. You need to call
		 * {@link #canFromThisString(String)} first.
		 * 
		 * @param str
		 * @return Note "null" doesn't mean anything wrong. if it returns null,
		 *         then null is the value you are looking for.
		 * @throws IllegalArgumentException
		 */
		public Object fromThisString(String str) throws IllegalArgumentException;
	}

	private static class ShortType implements CanFromStringTypeMeta {

		public Class<?> getType() {
			return short.class;
		}

		public boolean canFromThisString(String str) {
			if (str == null) {
				return false;
			}
			try {
				Short.parseShort(str);
				return true;
			} catch (NumberFormatException nfe) {
				return false;
			}
		}

		public Object fromThisString(String str) {
			if (!canFromThisString(str)) {
				throw new IllegalArgumentException(ERR_PARSE_WITHOUT_ACCESS);
			}
			return Short.parseShort(str);
		}

	}

	private static class IntType implements CanFromStringTypeMeta {

		public Class<?> getType() {
			return int.class;
		}

		public boolean canFromThisString(String str) {
			if (str == null) {
				return false;
			}
			try {
				Integer.parseInt(str);
				return true;
			} catch (NumberFormatException nfe) {
				return false;
			}
		}

		public Object fromThisString(String str) {
			if (!canFromThisString(str)) {
				throw new IllegalArgumentException(ERR_PARSE_WITHOUT_ACCESS);
			}
			return Integer.parseInt(str);
		}

	}

	private static class LongType implements CanFromStringTypeMeta {

		public Class<?> getType() {
			return long.class;
		}

		public boolean canFromThisString(String str) {
			if (str == null) {
				return false;
			}
			try {
				Long.parseLong(str);
				return true;
			} catch (NumberFormatException nfe) {
				return false;
			}
		}

		public Object fromThisString(String str) {
			if (!canFromThisString(str)) {
				throw new IllegalArgumentException(ERR_PARSE_WITHOUT_ACCESS);
			}
			return Long.parseLong(str);
		}

	}

	private static class FloatType implements CanFromStringTypeMeta {

		public Class<?> getType() {
			return float.class;
		}

		public boolean canFromThisString(String str) {
			if (str == null) {
				return false;
			}
			try {
				Float.parseFloat(str);
				return true;
			} catch (NumberFormatException nfe) {
				return false;
			}
		}

		public Object fromThisString(String str) {
			if (!canFromThisString(str)) {
				throw new IllegalArgumentException(ERR_PARSE_WITHOUT_ACCESS);
			}
			return Float.parseFloat(str);
		}

	}

	private static class DoubleType implements CanFromStringTypeMeta {

		public Class<?> getType() {
			return double.class;
		}

		public boolean canFromThisString(String str) {
			if (str == null) {
				return false;
			}
			try {
				Double.parseDouble(str);
				return true;
			} catch (NumberFormatException nfe) {
				return false;
			}
		}

		public Object fromThisString(String str) {
			if (!canFromThisString(str)) {
				throw new IllegalArgumentException(ERR_PARSE_WITHOUT_ACCESS);
			}
			return Double.parseDouble(str);
		}

	}

	private static class BooleanType implements CanFromStringTypeMeta {

		public Class<?> getType() {
			return boolean.class;
		}

		public boolean canFromThisString(String str) {
			if (str == null) {
				return false;
			}
			try {
				Boolean.parseBoolean(str);
				return true;
			} catch (NumberFormatException nfe) {
				return false;
			}
		}

		public Object fromThisString(String str) {
			if (!canFromThisString(str)) {
				throw new IllegalArgumentException(ERR_PARSE_WITHOUT_ACCESS);
			}
			return Boolean.parseBoolean(str);
		}
	}

	private static class ShortObjectType implements CanFromStringTypeMeta {

		public Class<?> getType() {
			return Short.class;
		}

		public boolean canFromThisString(String str) {
			if (str == null) {
				return true;
			}
			try {
				Short.valueOf(str);
				return true;
			} catch (NumberFormatException nfe) {
				return false;
			}
		}

		public Object fromThisString(String str) {
			if (!canFromThisString(str)) {
				throw new IllegalArgumentException(ERR_PARSE_WITHOUT_ACCESS);
			}
			if (str == null) {
				return null;
			}
			return Short.valueOf(str);
		}

	}

	private static class IntegerObjectType implements CanFromStringTypeMeta {

		public Class<?> getType() {
			return Integer.class;
		}

		public boolean canFromThisString(String str) {
			if (str == null) {
				return true;
			}
			try {
				Integer.valueOf(str);
				return true;
			} catch (NumberFormatException nfe) {
				return false;
			}
		}

		public Object fromThisString(String str) {
			if (!canFromThisString(str)) {
				throw new IllegalArgumentException(ERR_PARSE_WITHOUT_ACCESS);
			}
			if (str == null) {
				return null;
			}
			return Integer.valueOf(str);
		}

	}

	private static class LongObjectType implements CanFromStringTypeMeta {

		public Class<?> getType() {
			return Long.class;
		}

		public boolean canFromThisString(String str) {
			if (str == null) {
				return true;
			}
			try {
				Long.valueOf(str);
				return true;
			} catch (NumberFormatException nfe) {
				return false;
			}
		}

		public Object fromThisString(String str) {
			if (!canFromThisString(str)) {
				throw new IllegalArgumentException(ERR_PARSE_WITHOUT_ACCESS);
			}
			if (str == null) {
				return null;
			}
			return Long.valueOf(str);
		}

	}

	private static class FloatObjectType implements CanFromStringTypeMeta {

		public Class<?> getType() {
			return Float.class;
		}

		public boolean canFromThisString(String str) {
			if (str == null) {
				return true;
			}
			try {
				Float.valueOf(str);
				return true;
			} catch (NumberFormatException nfe) {
				return false;
			}
		}

		public Object fromThisString(String str) {
			if (!canFromThisString(str)) {
				throw new IllegalArgumentException(ERR_PARSE_WITHOUT_ACCESS);
			}
			if (str == null) {
				return null;
			}
			return Float.valueOf(str);
		}

	}

	private static class DoubleObjectType implements CanFromStringTypeMeta {

		public Class<?> getType() {
			return Double.class;
		}

		public boolean canFromThisString(String str) {
			if (str == null) {
				return true;
			}
			try {
				Double.valueOf(str);
				return true;
			} catch (NumberFormatException nfe) {
				return false;
			}
		}

		public Object fromThisString(String str) {
			if (!canFromThisString(str)) {
				throw new IllegalArgumentException(ERR_PARSE_WITHOUT_ACCESS);
			}
			if (str == null) {
				return null;
			}
			return Double.valueOf(str);
		}

	}

	private static class BooleanObjectType implements CanFromStringTypeMeta {

		public Class<?> getType() {
			return Boolean.class;
		}

		public boolean canFromThisString(String str) {
			if (str == null) {
				return true;
			}
			try {
				Boolean.valueOf(str);
				return true;
			} catch (NumberFormatException nfe) {
				return false;
			}
		}

		public Object fromThisString(String str) {
			if (!canFromThisString(str)) {
				throw new IllegalArgumentException(ERR_PARSE_WITHOUT_ACCESS);
			}
			if (str == null) {
				return null;
			}
			return Boolean.valueOf(str);
		}

	}

	private static class BigIntegerType implements CanFromStringTypeMeta {

		public Class<?> getType() {
			return BigInteger.class;
		}

		public boolean canFromThisString(String str) {
			if (str == null) {
				return true;
			}
			try {
				new BigInteger(str);
				return true;
			} catch (NumberFormatException nfe) {
				return false;
			}
		}

		public Object fromThisString(String str) {
			if (!canFromThisString(str)) {
				throw new IllegalArgumentException(ERR_PARSE_WITHOUT_ACCESS);
			}
			if (str == null) {
				return null;
			}
			return new BigInteger(str);
		}

	}

	private static class BigDecimalType implements CanFromStringTypeMeta {

		public Class<?> getType() {
			return BigDecimal.class;
		}

		public boolean canFromThisString(String str) {
			if (str == null) {
				return true;
			}
			try {
				new BigDecimal(str);
				return true;
			} catch (NumberFormatException nfe) {
				return false;
			}
		}

		public Object fromThisString(String str) {
			if (!canFromThisString(str)) {
				throw new IllegalArgumentException(ERR_PARSE_WITHOUT_ACCESS);
			}
			if (str == null) {
				return null;
			}
			return new BigDecimal(str);
		}

	}

	private static class StringType implements CanFromStringTypeMeta {

		public Class<?> getType() {
			return String.class;
		}

		public boolean canFromThisString(String str) {
			return true;
		}

		public Object fromThisString(String str) {
			return str;
		}

	}

}
