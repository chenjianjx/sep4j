package org.sep4j;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.junit.Test;

public class HeaderUtilsTest {
	
	@Test
	public void  generateHeaderMapFromPropsTest() {
		Map<String, String> headerMap = HeaderUtils.generateHeaderMapFromProps(HeaderUtilsTestRecord.class);
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
		Map<String, String> reverseHeaderMap = HeaderUtils.generateReverseHeaderMapFromProps(HeaderUtilsTestRecord.class);
		assertEquals(5, reverseHeaderMap.size());
		assertEquals("primIntProp", reverseHeaderMap.get("Prim Int Prop"));
		assertEquals("intObjProp", reverseHeaderMap.get("Int Obj Prop"));
		assertEquals("strProp", reverseHeaderMap.get("Str Prop"));
		assertEquals("dateProp", reverseHeaderMap.get("Date Prop"));
		assertEquals("writeOnlyProp", reverseHeaderMap.get("Write Only Prop"));
	}

}
