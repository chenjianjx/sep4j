package org.sep4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * 
 * @author chenjianjx
 * 
 */

public class SepExcelUtilsTest {

	@Rule
	public ExpectedException expectedEx = ExpectedException.none();

	@Test
	public void shouldSaveTest() {
		Assert.assertTrue(SepExcelUtils.shouldSave(null, true));
		Assert.assertTrue(SepExcelUtils.shouldSave(Arrays.asList(new DatumError()), true));

		Assert.assertTrue(SepExcelUtils.shouldSave(null, false));
		Assert.assertTrue(SepExcelUtils.shouldSave(new ArrayList<DatumError>(), false));
		Assert.assertFalse(SepExcelUtils.shouldSave(Arrays.asList(new DatumError()), false));
	}

	@Test
	public void validateRecordClass_NullClass() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("null");
		SepExcelUtils.validateRecordClass(null);
	}

	@Test
	public void createRecordIndexTest() {
		SepExcelUtils.createRecordInstance(DefaultConstructorBean.class);
		SepExcelUtils.createRecordInstance(PrivateClassBean.class);
	}

	@Test
	public void createRecordIndexTest_NoDefaultConstructor() {
		expectedEx.expect(RuntimeException.class);
		expectedEx.expectMessage("<init>()");
		SepExcelUtils.createRecordInstance(NoDefaultConstructorBean.class);
	}

	@Test
	public void validateReverseHeaderMapTest_Null() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("null");
		SepExcelUtils.validateReverseHeaderMap(null);
	}

	@Test
	public void validateReverseHeaderMapTest_Empty() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("empty");
		Map<String, String> map = new HashMap<String, String>();
		SepExcelUtils.validateReverseHeaderMap(map);
	}

	@Test
	public void validateReverseHeaderMapTest_Positive() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("someText", "someProp");
		SepExcelUtils.validateReverseHeaderMap(map);
	}

	@Test
	public void validateReverseHeaderMapTest_TextBlank() {
		Map<String, String> map = new HashMap<String, String>();
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("blank headerText");
		expectedEx.expectMessage("1");

		map.put("text1", "prop1");
		map.put("	", "prop2");
		SepExcelUtils.validateReverseHeaderMap(map);
	}

	@Test
	public void validateReverseHeaderMapTest_PropBlank() {
		Map<String, String> map = new HashMap<String, String>();
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("blank propName");
		expectedEx.expectMessage("1");

		map.put("text1", "prop1");
		map.put("text2", "	");
		SepExcelUtils.validateReverseHeaderMap(map);
	}

	@Test
	public void validateHeaderMapTest_Null() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("null");
		SepExcelUtils.validateHeaderMap(null);
	}

	@Test
	public void validateHeaderMapTest_Empty() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("empty");
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		SepExcelUtils.validateHeaderMap(map);
	}

	@Test
	public void validateHeaderMapTest_Positive() {
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("someProp", "someText");
		SepExcelUtils.validateHeaderMap(map);
	}

	@Test
	public void validateHeaderMapTest_PropBlank() {
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("blank propName");
		expectedEx.expectMessage("1");

		map.put("prop1", "text1");
		map.put("	", "text2");
		SepExcelUtils.validateHeaderMap(map);
	}

	@Test
	public void setPropertyWithCellTextTest_StrProp() {
		UTRecord uTRecord = new UTRecord();

		SepExcelUtils.setPropertyWithCellText(UTRecord.class, uTRecord, "strProp", "abc");
		Assert.assertEquals("abc", uTRecord.getStrProp());

		SepExcelUtils.setPropertyWithCellText(UTRecord.class, uTRecord, "strProp", null);
		Assert.assertNull(uTRecord.getStrProp());
	}

	@Test
	public void setPropertyWithCellTextTest_IntObjProp() {
		UTRecord uTRecord = new UTRecord();

		SepExcelUtils.setPropertyWithCellText(UTRecord.class, uTRecord, "intObjProp", "123");
		Assert.assertEquals(new Integer(123), uTRecord.getIntObjProp());

		SepExcelUtils.setPropertyWithCellText(UTRecord.class, uTRecord, "intObjProp", null);
		Assert.assertNull(uTRecord.getIntObjProp());

	}

	@Test
	public void setPropertyWithCellTextTest_IntObjProp_NotNumer() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("No suitable setter");

		UTRecord uTRecord = new UTRecord();
		SepExcelUtils.setPropertyWithCellText(UTRecord.class, uTRecord, "intObjProp", "abc");

	}

	@Test
	public void setPropertyWithCellTextTest_PrimIntProp() {
		UTRecord uTRecord = new UTRecord();

		SepExcelUtils.setPropertyWithCellText(UTRecord.class, uTRecord, "primIntProp", "123");
		Assert.assertEquals(123, uTRecord.getPrimIntProp());
	}

	@Test
	public void setPropertyWithCellTextTest_PrimIntProp_NullText() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("No suitable setter");

		UTRecord uTRecord = new UTRecord();
		SepExcelUtils.setPropertyWithCellText(UTRecord.class, uTRecord, "primIntProp", null);
		Assert.assertNull(uTRecord.getPrimIntProp());

	}

	@Test
	public void setPropertyWithCellTextTest_PrimIntProp_NotNumber() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("No suitable setter");

		UTRecord uTRecord = new UTRecord();
		SepExcelUtils.setPropertyWithCellText(UTRecord.class, uTRecord, "primIntProp", "abc");
	}

	@Test
	public void readCellAsStringTest() {
		Row row = createRowForTest();

		Cell blankCell = row.createCell(0, Cell.CELL_TYPE_BLANK);

		Cell boolCell = row.createCell(1, Cell.CELL_TYPE_BOOLEAN);
		boolCell.setCellValue(true);

		Cell errCell = row.createCell(2, Cell.CELL_TYPE_ERROR);

		Cell formulaCell = row.createCell(3, Cell.CELL_TYPE_FORMULA);
		formulaCell.setCellValue("a1 + a2");

		Cell numericCell = row.createCell(4, Cell.CELL_TYPE_NUMERIC);
		numericCell.setCellValue("100.00");

		Cell strCell = row.createCell(5, Cell.CELL_TYPE_STRING);
		strCell.setCellValue("	abc	");

		Assert.assertNull(SepExcelUtils.readCellAsString(null));
		Assert.assertNull(SepExcelUtils.readCellAsString(blankCell));
		Assert.assertEquals("true", SepExcelUtils.readCellAsString(boolCell));
		Assert.assertNull(SepExcelUtils.readCellAsString(errCell));
		Assert.assertNull(SepExcelUtils.readCellAsString(formulaCell));
		Assert.assertEquals("100.00", SepExcelUtils.readCellAsString(numericCell));
		Assert.assertEquals("abc", SepExcelUtils.readCellAsString(strCell));

	}

	@SuppressWarnings("unused")
	private static class UTRecord {
		private int primIntProp;
		private Integer intObjProp;
		private String strProp;

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

	}

	private static class PrivateClassBean {

	}

	public static class DefaultConstructorBean {

	}

	public static class NoDefaultConstructorBean {
		public NoDefaultConstructorBean(Object param) {
		}
	}

	private Row createRowForTest() {
		Workbook wb = new XSSFWorkbook();
		Sheet sheet = wb.createSheet();
		Row row = sheet.createRow(0);
		return row;
	}
}
