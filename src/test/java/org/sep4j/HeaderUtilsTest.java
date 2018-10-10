package org.sep4j;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;
import java.util.Date;
import java.util.Map;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;
import org.junit.Test;

public class HeaderUtilsTest {
	
	@Test
	public void  generateHeaderMapFromPropsTest() {
		Map<String, String> headerMap = HeaderUtils.generateHeaderMapFromProps(HeaderUtilsUnitTestRecord.class);
		assertEquals(6, headerMap.size());
		assertEquals("Prim Int Prop", headerMap.get("primIntProp"));
		assertEquals("Int Obj Prop", headerMap.get("intObjProp"));
		assertEquals("Str Prop", headerMap.get("strProp"));
		assertEquals("Date Prop", headerMap.get("dateProp"));
		assertEquals("Date Prop Str", headerMap.get("datePropStr"));
		assertEquals("Read Only Prop", headerMap.get("readOnlyProp"));
	}
	
	
	@Test
	public void generateReverseHeaderMapFromPropsTest() {
		Map<String, String> reverseHeaderMap = HeaderUtils.generateReverseHeaderMapFromProps(HeaderUtilsUnitTestRecord.class);
		assertEquals(5, reverseHeaderMap.size());
		assertEquals("primIntProp", reverseHeaderMap.get("Prim Int Prop"));
		assertEquals("intObjProp", reverseHeaderMap.get("Int Obj Prop"));
		assertEquals("strProp", reverseHeaderMap.get("Str Prop"));
		assertEquals("dateProp", reverseHeaderMap.get("Date Prop"));
		assertEquals("writeOnlyProp", reverseHeaderMap.get("Write Only Prop"));
	}

	@SuppressWarnings("unused")
	private static class HeaderUtilsUnitTestRecord {
		private int primIntProp;
		private Integer intObjProp;
		private String strProp;
		private Date dateProp;
		private String readOnlyProp; 
		private String writeOnlyProp;

		public int getPrimIntProp() {
			return primIntProp;
		}

		public void setPrimIntProp(int primIntProp) {
			this.primIntProp = primIntProp;
		}

		public Integer getIntObjProp() {
			return intObjProp;
		}

		public void setIntObjProp(Integer intObjProp) {
			this.intObjProp = intObjProp;
		}

		public String getStrProp() {
			return strProp;
		}

		public void setStrProp(String strProp) {
			this.strProp = strProp;
		}

		public Date getDateProp() {
			return dateProp;
		}

		public void setDateProp(Date date) {
			this.dateProp = date;
		}

		public String getDatePropStr() {
			if (dateProp == null) {
				return null;
			}
			return DateFormatUtils.format(dateProp, "yyyy-MM-dd HH:mm:ss");
		}

		public void setDateProp(String s) {
			if (s == null) {
				return;
			}
			try {
				Date d = DateUtils.parseDate(s, new String[] { "yyyy-MM-dd HH:mm:ss" });
				this.setDateProp(d);
			} catch (ParseException e) {
				throw new IllegalArgumentException(e);
			}

		}

		public String getReadOnlyProp() {
			return readOnlyProp;
		}

		public void setWriteOnlyProp(String writeOnlyProp) {
			this.writeOnlyProp = writeOnlyProp;
		}
		
		
	}

}
