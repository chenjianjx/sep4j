package org.sep4j.support;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNull;
import static org.sep4j.support.SepBasicTypeConverts.retainWholeIfDecimalPartZero;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.junit.Test;

/**
 * @author chenjianjx
 * 
 * 
 */
public class SepBasicTypeConvertsTest {

	BigInteger SHORT_MAX_PLUS_ONE = new BigInteger(String.valueOf(Short.MAX_VALUE)).add(new BigInteger("1"));
	BigInteger SHORT_MIN_MINUS_ONE = new BigInteger(String.valueOf(Short.MIN_VALUE)).add(new BigInteger("-1"));

	BigInteger INT_MAX_PLUS_ONE = new BigInteger(String.valueOf(Integer.MAX_VALUE)).add(new BigInteger("1"));
	BigInteger INT_MIN_MINUS_ONE = new BigInteger(String.valueOf(Integer.MIN_VALUE)).add(new BigInteger("-1"));

	BigInteger LONG_MAX_PLUS_ONE = new BigInteger(String.valueOf(Long.MAX_VALUE)).add(new BigInteger("1"));
	BigInteger LONG_MIN_MINUS_ONE = new BigInteger(String.valueOf(Long.MIN_VALUE)).add(new BigInteger("-1"));

	@Test
	public void retainWholeIfDecimalPartZeroTest() {

	 
		assertEquals("0", retainWholeIfDecimalPartZero("0."));
		assertEquals("0", retainWholeIfDecimalPartZero("0.0"));
		assertEquals("2", retainWholeIfDecimalPartZero("2.0"));
		assertEquals("2", retainWholeIfDecimalPartZero("2.000000"));
		assertEquals("2", retainWholeIfDecimalPartZero("2"));
		assertEquals("2.01", retainWholeIfDecimalPartZero("2.01"));
		assertEquals("123457000000000000", retainWholeIfDecimalPartZero("1.23457E+17"));
		
		
		assertNull(retainWholeIfDecimalPartZero(null));
		assertEquals("abc", retainWholeIfDecimalPartZero("abc"));
		assertEquals("abc.00", retainWholeIfDecimalPartZero("abc.00"));
		assertEquals("a2.00", retainWholeIfDecimalPartZero("a2.00"));

	}
	
	@Test
	public void unsupportedTypeTest(){
		assertFalse(SepBasicTypeConverts.canFromThisString("ab123", Object.class));
	}

	@Test
	public void shortTypeTest() {

		assertEquals((short) 1, SepBasicTypeConverts.fromThisString("1", short.class));
		assertEquals((short) 1, SepBasicTypeConverts.fromThisString("1.0", short.class));

		assertFalse(SepBasicTypeConverts.canFromThisString(null, short.class));
		assertFalse(SepBasicTypeConverts.canFromThisString("1.02", short.class));

		assertFalse(SepBasicTypeConverts.canFromThisString(String.valueOf(SHORT_MAX_PLUS_ONE), short.class));
		assertFalse(SepBasicTypeConverts.canFromThisString(String.valueOf(SHORT_MIN_MINUS_ONE), short.class));
		assertFalse(SepBasicTypeConverts.canFromThisString("abc", short.class));
		assertFalse(SepBasicTypeConverts.canFromThisString("123abc", short.class));
	}

	@Test
	public void intTypeTest() {

		assertEquals((int) 1, SepBasicTypeConverts.fromThisString("1", int.class));
		assertEquals((int) 1, SepBasicTypeConverts.fromThisString("1.0", int.class));

		assertFalse(SepBasicTypeConverts.canFromThisString(null, int.class));
		assertFalse(SepBasicTypeConverts.canFromThisString("1.02", int.class));

		assertFalse(SepBasicTypeConverts.canFromThisString(String.valueOf(INT_MAX_PLUS_ONE), int.class));
		assertFalse(SepBasicTypeConverts.canFromThisString(String.valueOf(INT_MIN_MINUS_ONE), int.class));
		assertFalse(SepBasicTypeConverts.canFromThisString("abc", int.class));
		assertFalse(SepBasicTypeConverts.canFromThisString("123abc", int.class));
	}

	@Test
	public void longTypeTest() {

		assertEquals((long) 1, SepBasicTypeConverts.fromThisString("1", long.class));
		assertEquals((long) 1, SepBasicTypeConverts.fromThisString("1.0", long.class));

		assertFalse(SepBasicTypeConverts.canFromThisString(null, long.class));
		assertFalse(SepBasicTypeConverts.canFromThisString("1.02", long.class));
		assertFalse(SepBasicTypeConverts.canFromThisString(String.valueOf(LONG_MAX_PLUS_ONE), long.class));
		assertFalse(SepBasicTypeConverts.canFromThisString(String.valueOf(LONG_MIN_MINUS_ONE), long.class));
		assertFalse(SepBasicTypeConverts.canFromThisString("abc", long.class));
		assertFalse(SepBasicTypeConverts.canFromThisString("123abc", long.class));
	}

	@Test
	public void floatTypeTest() {

		assertEquals((float) 1, SepBasicTypeConverts.fromThisString("1", float.class));
		assertEquals((float) 1, SepBasicTypeConverts.fromThisString("1.0", float.class));
		assertEquals((float) 1.02, SepBasicTypeConverts.fromThisString("1.02", float.class));

		assertFalse(SepBasicTypeConverts.canFromThisString(null, float.class));
		assertFalse(SepBasicTypeConverts.canFromThisString("abc", float.class));
		assertFalse(SepBasicTypeConverts.canFromThisString("123abc", float.class));
	}

	@Test
	public void doubleTypeTest() {

		assertEquals((double) 1, SepBasicTypeConverts.fromThisString("1", double.class));
		assertEquals((double) 1, SepBasicTypeConverts.fromThisString("1.0", double.class));
		assertEquals((double) 1.02, SepBasicTypeConverts.fromThisString("1.02", double.class));

		assertFalse(SepBasicTypeConverts.canFromThisString(null, double.class));
		assertFalse(SepBasicTypeConverts.canFromThisString("abc", double.class));
		assertFalse(SepBasicTypeConverts.canFromThisString("123abc", double.class));
	}

	@Test
	public void shortObjectTypeTest() {

		assertEquals(new Short("1"), SepBasicTypeConverts.fromThisString("1", Short.class));
		assertEquals(new Short("1"), SepBasicTypeConverts.fromThisString("1.0", Short.class));
		assertNull(SepBasicTypeConverts.fromThisString(null, Short.class));
		
		
		assertFalse(SepBasicTypeConverts.canFromThisString("1.02", Short.class));

		assertFalse(SepBasicTypeConverts.canFromThisString(String.valueOf(SHORT_MAX_PLUS_ONE), Short.class));
		assertFalse(SepBasicTypeConverts.canFromThisString(String.valueOf(SHORT_MIN_MINUS_ONE), Short.class));
		assertFalse(SepBasicTypeConverts.canFromThisString("abc", Short.class));
		assertFalse(SepBasicTypeConverts.canFromThisString("123abc", Short.class));
	}

	@Test
	public void integerObjectTypeTest() {

		assertEquals((Integer) 1, SepBasicTypeConverts.fromThisString("1", Integer.class));
		assertEquals((Integer) 1, SepBasicTypeConverts.fromThisString("1.0", Integer.class));
		assertNull(SepBasicTypeConverts.fromThisString(null, Integer.class));
		
		assertFalse(SepBasicTypeConverts.canFromThisString("1.02", Integer.class));

		assertFalse(SepBasicTypeConverts.canFromThisString(String.valueOf(INT_MAX_PLUS_ONE), Integer.class));
		assertFalse(SepBasicTypeConverts.canFromThisString(String.valueOf(INT_MIN_MINUS_ONE), Integer.class));
		assertFalse(SepBasicTypeConverts.canFromThisString("abc", Integer.class));
		assertFalse(SepBasicTypeConverts.canFromThisString("123abc", Integer.class));
	}

	@Test
	public void longObjectTypeTest() {

		assertEquals(1l, SepBasicTypeConverts.fromThisString("1", Long.class));
		assertEquals(1l, SepBasicTypeConverts.fromThisString("1.0", Long.class));
		assertNull(SepBasicTypeConverts.fromThisString(null, Long.class));
		
		assertFalse(SepBasicTypeConverts.canFromThisString("1.02", Long.class));
		assertFalse(SepBasicTypeConverts.canFromThisString(String.valueOf(LONG_MAX_PLUS_ONE), Long.class));
		assertFalse(SepBasicTypeConverts.canFromThisString(String.valueOf(LONG_MIN_MINUS_ONE), Long.class));
		assertFalse(SepBasicTypeConverts.canFromThisString("abc", Long.class));
		assertFalse(SepBasicTypeConverts.canFromThisString("123abc", Long.class));
	}

	@Test
	public void floatObjectTypeTest() {

		assertEquals(new Float(1), SepBasicTypeConverts.fromThisString("1", Float.class));
		assertEquals(new Float(1.0), SepBasicTypeConverts.fromThisString("1.0", Float.class));
		assertEquals(new Float(1.02), SepBasicTypeConverts.fromThisString("1.02", Float.class));
		assertNull(SepBasicTypeConverts.fromThisString(null, Float.class));


		assertFalse(SepBasicTypeConverts.canFromThisString("abc", Float.class));
		assertFalse(SepBasicTypeConverts.canFromThisString("123abc", Float.class));
	}

	@Test
	public void doubleObjectTypeTest() {

		assertEquals(new Double(1), SepBasicTypeConverts.fromThisString("1", Double.class));
		assertEquals(new Double(1.0), SepBasicTypeConverts.fromThisString("1.0", Double.class));
		assertEquals(new Double(1.02), SepBasicTypeConverts.fromThisString("1.02", Double.class));
		assertNull(SepBasicTypeConverts.fromThisString(null, Double.class));

		
		assertFalse(SepBasicTypeConverts.canFromThisString("abc", Double.class));
		assertFalse(SepBasicTypeConverts.canFromThisString("123abc", Double.class));
	}
	
	
	@Test
	public void booleanTypeTest() {

		assertEquals(true, SepBasicTypeConverts.fromThisString("true", boolean.class));
		assertEquals(true, SepBasicTypeConverts.fromThisString("True", boolean.class));			
		assertEquals(false, SepBasicTypeConverts.fromThisString("anything", boolean.class));
		
		assertFalse(SepBasicTypeConverts.canFromThisString(null, boolean.class));	
	}
	
	
	@Test
	public void booleanObjectTypeTest() {
		
		assertEquals(true, SepBasicTypeConverts.fromThisString("true", Boolean.class));
		assertEquals(true, SepBasicTypeConverts.fromThisString("True", Boolean.class));			
		assertEquals(false, SepBasicTypeConverts.fromThisString("anything", Boolean.class));
		
		assertNull(SepBasicTypeConverts.fromThisString(null, Boolean.class));
	}
	
	@Test
	public void bigIntegerTypeTest(){

		assertEquals(new BigInteger("1"), SepBasicTypeConverts.fromThisString("1", BigInteger.class));
		assertEquals(new BigInteger("1"), SepBasicTypeConverts.fromThisString("1.0", BigInteger.class));
		
		assertNull(SepBasicTypeConverts.fromThisString(null, Integer.class));		
		assertFalse(SepBasicTypeConverts.canFromThisString("1.02", BigInteger.class));

		assertFalse(SepBasicTypeConverts.canFromThisString("abc", BigInteger.class));
		assertFalse(SepBasicTypeConverts.canFromThisString("123abc", BigInteger.class));
	
	}
	
	

	@Test
	public void bigDecimalTypeTest() {

		assertEquals(new BigDecimal("1"), SepBasicTypeConverts.fromThisString("1", BigDecimal.class));
		assertEquals(new BigDecimal("1.0"), SepBasicTypeConverts.fromThisString("1.0", BigDecimal.class));
		assertEquals(new BigDecimal("1.02"), SepBasicTypeConverts.fromThisString("1.02", BigDecimal.class));
		assertNull(SepBasicTypeConverts.fromThisString(null, BigDecimal.class));

		
		assertFalse(SepBasicTypeConverts.canFromThisString("abc", BigDecimal.class));
		assertFalse(SepBasicTypeConverts.canFromThisString("123abc", BigDecimal.class));
	}
	
	
}
