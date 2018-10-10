package org.sep4j.support;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * do type conversion for basic types
 * 
 * @author chenjianjx
 */
public class SepBasicTypeConverts {

	private static final Map<Class<?>, CanFromStringTypeMeta> canFromStringTypeMetas = new LinkedHashMap<Class<?>, CanFromStringTypeMeta>();
	private static final Map<Class<?>, CanFromNullTypeMeta> canFromNullTypeMetas = new LinkedHashMap<Class<?>, CanFromNullTypeMeta>();

	static {
		init();
	}

	//TODO: replace them with singletons
	private static void init() {
		// can from null
		addCanFromNullTypeMeta(new ShortObjectType());
		addCanFromNullTypeMeta(new IntegerObjectType());
		addCanFromNullTypeMeta(new LongObjectType());
		addCanFromNullTypeMeta(new FloatObjectType());
		addCanFromNullTypeMeta(new DoubleObjectType());
		addCanFromNullTypeMeta(new BooleanObjectType());
		addCanFromNullTypeMeta(new BigIntegerType());
		addCanFromNullTypeMeta(new BigDecimalType());
		addCanFromNullTypeMeta(new StringType());
		addCanFromNullTypeMeta(new DateType());

		// can from string
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
		canFromStringTypeMetas.put(meta.getType(), meta);
	}

	private static void addCanFromNullTypeMeta(CanFromNullTypeMeta meta) {
		canFromNullTypeMetas.put(meta.getType(), meta);
	}

	/**
	 * can this type take null?
	 */
	public static boolean canFromNull(Class<?> targetType) {
		CanFromNullTypeMeta typeMeta = canFromNullTypeMetas.get(targetType);
		return typeMeta != null;
	}

	/**
	 * can this string be parsed as that type?
	 */
	public static boolean canFromThisString(String str, Class<?> targetType) {
		CanFromStringTypeMeta typeMeta = canFromStringTypeMetas.get(targetType);
		if (typeMeta == null) {
			return false;
		}
		try {
			typeMeta.fromThisString(str);
			return true;
		} catch (RuntimeException e) {
			return false;
		}
	}
	
	/**
	 * can you parse some string to this type? 
	 * @param targetType
	 * @return
	 */
	public static boolean canTypeFromString(Class<?> targetType) {
		return canFromStringTypeMetas.containsKey(targetType);
	}
	

	/**
	 * parse from this string. You need to call
	 * {@link #canFromThisString(String, Class)} first.
	 * 
	 * @param str  the string
	 * @param targetType the target type you want to convert the string to
	 * @return Note "null" doesn't mean anything wrong. if it returns null, then
	 *         null is the value you are looking for.
	 */
	public static Object fromThisString(String str, Class<?> targetType) {
		if (!canFromThisString(str, targetType)) {
			throw new IllegalArgumentException("Please call fromThisString(String str, targetType) first to confirm");
		}
		CanFromStringTypeMeta typeMeta = canFromStringTypeMetas.get(targetType);
		return typeMeta.fromThisString(str);
	}

	private static interface BasicType {
		/**
		 * the type
		 * 
		 * @return
		 */
		public Class<?> getType();
	}

	/**
	 * 
	 * a wrapper for types that can take null as its value
	 * 
	 */
	private static interface CanFromNullTypeMeta extends BasicType {

	}

	/**
	 * a wrapper for types that can be parsed from a string
	 * 
	 * 
	 */
	private static interface CanFromStringTypeMeta extends BasicType {

		/**
		 * parse from this string.
		 * 
		 * @param str
		 * @return Note "null" doesn't mean anything wrong. if it returns null,
		 *         then null is the value you are looking for.
		 * @throws RuntimeException
		 *             If an exception is thrown, it means this string cannot be
		 *             parsed
		 */
		public Object fromThisString(String str) throws RuntimeException;
	}

	private static class ShortType implements CanFromStringTypeMeta {

		public Class<?> getType() {
			return short.class;
		}

		public Object fromThisString(String str) {
			str = retainWholeIfDecimalPartZero(str);
			return Short.parseShort(str);
		}

	}

	private static class IntType implements CanFromStringTypeMeta {

		public Class<?> getType() {
			return int.class;
		}

		public Object fromThisString(String str) {
			str = retainWholeIfDecimalPartZero(str);
			return Integer.parseInt(str);
		}

	}

	private static class LongType implements CanFromStringTypeMeta {

		public Class<?> getType() {
			return long.class;
		}

		public Object fromThisString(String str) {
			str = retainWholeIfDecimalPartZero(str);
			return Long.parseLong(str);
		}

	}

	private static class FloatType implements CanFromStringTypeMeta {

		public Class<?> getType() {
			return float.class;
		}

		public Object fromThisString(String str) {
			return Float.parseFloat(str);
		}

	}

	private static class DoubleType implements CanFromStringTypeMeta {

		public Class<?> getType() {
			return double.class;
		}

		public Object fromThisString(String str) {
			return Double.parseDouble(str);
		}

	}

	private static class BooleanType implements CanFromStringTypeMeta {

		public Class<?> getType() {
			return boolean.class;
		}

		public Object fromThisString(String str) {
			if (str == null) {
				throw new IllegalArgumentException("don't take null for primitive boolean type");
			}

			return Boolean.parseBoolean(str);
		}
	}

	private static class ShortObjectType implements CanFromStringTypeMeta, CanFromNullTypeMeta {

		public Class<?> getType() {
			return Short.class;
		}

		public Object fromThisString(String str) {

			if (str == null) {
				return null;
			}
			str = retainWholeIfDecimalPartZero(str);
			return Short.valueOf(str);
		}

	}

	private static class IntegerObjectType implements CanFromStringTypeMeta, CanFromNullTypeMeta {

		public Class<?> getType() {
			return Integer.class;
		}

		public Object fromThisString(String str) {
			if (str == null) {
				return null;
			}
			str = retainWholeIfDecimalPartZero(str);
			return Integer.valueOf(str);
		}

	}

	private static class LongObjectType implements CanFromStringTypeMeta, CanFromNullTypeMeta {

		public Class<?> getType() {
			return Long.class;
		}

		public Object fromThisString(String str) {
			if (str == null) {
				return null;
			}
			str = retainWholeIfDecimalPartZero(str);
			return Long.valueOf(str);
		}

	}

	private static class FloatObjectType implements CanFromStringTypeMeta, CanFromNullTypeMeta {

		public Class<?> getType() {
			return Float.class;
		}

		public Object fromThisString(String str) {
			if (str == null) {
				return null;
			}
			return Float.valueOf(str);
		}

	}

	private static class DoubleObjectType implements CanFromStringTypeMeta, CanFromNullTypeMeta {

		public Class<?> getType() {
			return Double.class;
		}

		public Object fromThisString(String str) {
			if (str == null) {
				return null;
			}
			return Double.valueOf(str);
		}

	}

	private static class BooleanObjectType implements CanFromStringTypeMeta, CanFromNullTypeMeta {

		public Class<?> getType() {
			return Boolean.class;
		}

		public Object fromThisString(String str) {
			if (str == null) {
				return null;
			}
			return Boolean.valueOf(str);
		}

	}

	private static class BigIntegerType implements CanFromStringTypeMeta, CanFromNullTypeMeta {

		public Class<?> getType() {
			return BigInteger.class;
		}

		public Object fromThisString(String str) {
			if (str == null) {
				return null;
			}
			str = retainWholeIfDecimalPartZero(str);
			return new BigInteger(str);
		}

	}

	private static class BigDecimalType implements CanFromStringTypeMeta, CanFromNullTypeMeta {

		public Class<?> getType() {
			return BigDecimal.class;
		}

		public Object fromThisString(String str) {
			if (str == null) {
				return null;
			}
			return new BigDecimal(str);
		}

	}

	private static class StringType implements CanFromStringTypeMeta, CanFromNullTypeMeta {

		public Class<?> getType() {
			return String.class;
		}

		public Object fromThisString(String str) {
			return str;
		}

	}

	private static class DateType implements CanFromNullTypeMeta {
		public Class<?> getType() {
			return java.util.Date.class;
		}

	}

	/**
	 * get the whole number part if the string is numeric and the decimal part
	 * is zero <br/>
	 * e.g. null => null<br/>
	 * "3.00" => "3"<br/>
	 * "3.02" => "3.02" <br/>
	 * "3" =>"3". <br/>
	 * It also accommodates scientific notions with precision loss. <br/>
	 * e.g. "1.23457E+17 => 123457000000000000
	 * 
	 * @param s
	 * @return
	 */
	static String retainWholeIfDecimalPartZero(String s) {
		if (s == null) {
			return s;
		}

		try {
			// using a big decimal here can also take care of scientific
			// notions
			BigDecimal d = new BigDecimal(s);
			s = d.toPlainString();
		} catch (NumberFormatException e) {
			// not a number
			return s;
		}

		Pattern pattern = Pattern.compile("^(\\d+)\\.0*$");
		Matcher matcher = pattern.matcher(s);
		if (matcher.find()) {
			return matcher.group(1);
		}
		return s;
	}

}
