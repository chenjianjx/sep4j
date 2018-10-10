package org.sep4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
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

public class SsioTest {

	@Rule
	public ExpectedException expectedEx = ExpectedException.none();

	@Test
	public void shouldSaveTest() {
		Assert.assertTrue(Ssio.shouldSave(null, true));
		Assert.assertTrue(Ssio.shouldSave(Arrays.asList(new DatumError()), true));

		Assert.assertTrue(Ssio.shouldSave(null, false));
		Assert.assertTrue(Ssio.shouldSave(new ArrayList<DatumError>(), false));
		Assert.assertFalse(Ssio.shouldSave(Arrays.asList(new DatumError()), false));
	}

	@Test
	public void validateRecordClass_NullClass() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("null");
		Ssio.validateRecordClass(null);
	}

	@Test
	public void createRecordIndexTest() {
		Ssio.createRecordInstance(DefaultConstructorBean.class);
		Ssio.createRecordInstance(PrivateClassBean.class);
	}

	@Test
	public void createRecordIndexTest_NoDefaultConstructor() {
		expectedEx.expect(RuntimeException.class);
		expectedEx.expectMessage("<init>()");
		Ssio.createRecordInstance(NoDefaultConstructorBean.class);
	}

	@Test
	public void validateReverseHeaderMapTest_Null() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("null");
		Ssio.validateReverseHeaderMap(null);
	}

	@Test
	public void validateReverseHeaderMapTest_Empty() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("empty");
		Map<String, String> map = new HashMap<String, String>();
		Ssio.validateReverseHeaderMap(map);
	}

	@Test
	public void validateReverseHeaderMapTest_Positive() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("someText", "someProp");
		Ssio.validateReverseHeaderMap(map);
	}

	@Test
	public void validateReverseHeaderMapTest_TextBlank() {
		Map<String, String> map = new HashMap<String, String>();
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("blank headerText");
		expectedEx.expectMessage("1");

		map.put("text1", "prop1");
		map.put("	", "prop2");
		Ssio.validateReverseHeaderMap(map);
	}

	@Test
	public void validateReverseHeaderMapTest_PropBlank() {
		Map<String, String> map = new HashMap<String, String>();
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("blank propName");
		expectedEx.expectMessage("1");

		map.put("text1", "prop1");
		map.put("text2", "	");
		Ssio.validateReverseHeaderMap(map);
	}

	@Test
	public void validateHeaderMapTest_Null() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("null");
		Ssio.validateHeaderMap(null);
	}

	@Test
	public void validateHeaderMapTest_Empty() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("empty");
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		Ssio.validateHeaderMap(map);
	}

	@Test
	public void validateHeaderMapTest_Positive() {
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("someProp", "someText");
		Ssio.validateHeaderMap(map);
	}

	@Test
	public void validateHeaderMapTest_PropBlank() {
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("blank propName");
		expectedEx.expectMessage("1");

		map.put("prop1", "text1");
		map.put("	", "text2");
		Ssio.validateHeaderMap(map);
	}

	@Test
	public void setPropertyWithCellTextTest_StrProp() {
		SsioUnitTestRecord record = new SsioUnitTestRecord();

		Ssio.setPropertyWithCellValue(SsioUnitTestRecord.class, record, "strProp", "abc");
		Assert.assertEquals("abc", record.getStrProp());

		Ssio.setPropertyWithCellValue(SsioUnitTestRecord.class, record, "strProp", null);
		Assert.assertNull(record.getStrProp());
	}

	@Test
	public void setPropertyWithCellTextTest_IntObjProp() {
		SsioUnitTestRecord record = new SsioUnitTestRecord();

		Ssio.setPropertyWithCellValue(SsioUnitTestRecord.class, record, "intObjProp", "123");
		Assert.assertEquals(new Integer(123), record.getIntObjProp());

		Ssio.setPropertyWithCellValue(SsioUnitTestRecord.class, record, "intObjProp", null);
		Assert.assertNull(record.getIntObjProp());

	}

	@Test
	public void setPropertyWithCellTextTest_IntObjProp_NotNumer() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("No suitable setter");

		SsioUnitTestRecord record = new SsioUnitTestRecord();
		Ssio.setPropertyWithCellValue(SsioUnitTestRecord.class, record, "intObjProp", "abc");

	}

	@Test
	public void setPropertyWithCellTextTest_PrimIntProp() {
		SsioUnitTestRecord record = new SsioUnitTestRecord();

		Ssio.setPropertyWithCellValue(SsioUnitTestRecord.class, record, "primIntProp", "123");
		Assert.assertEquals(123, record.getPrimIntProp());
	}

	@Test
	public void setPropertyWithCellTextTest_PrimIntProp_NullText() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("No suitable setter");

		SsioUnitTestRecord record = new SsioUnitTestRecord();
		Ssio.setPropertyWithCellValue(SsioUnitTestRecord.class, record, "primIntProp", null);
	 

	}

	@Test
	public void setPropertyWithCellTextTest_PrimIntProp_NotNumber() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("No suitable setter");

		SsioUnitTestRecord record = new SsioUnitTestRecord();
		Ssio.setPropertyWithCellValue(SsioUnitTestRecord.class, record, "primIntProp", "abc");
	}

	@Test
	public void readCellAsStringOrDateTest() {
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

		Date now = new Date();
		Cell dateCell = row.createCell(6, Cell.CELL_TYPE_NUMERIC);
		dateCell.setCellValue(now);
		CellStyle dateStyle = row.getSheet().getWorkbook().createCellStyle();
		dateStyle.setDataFormat(row.getSheet().getWorkbook().createDataFormat().getFormat("yyyy-MM-dd HH:mm:ss.SSS"));
		dateCell.setCellStyle(dateStyle);

		Assert.assertNull(Ssio.readCellAsStringOrDate(null));
		Assert.assertNull(Ssio.readCellAsStringOrDate(blankCell));
		Assert.assertEquals("true", Ssio.readCellAsStringOrDate(boolCell));
		Assert.assertNull(Ssio.readCellAsStringOrDate(errCell));
		Assert.assertNull(Ssio.readCellAsStringOrDate(formulaCell));
		Assert.assertEquals("100.00", Ssio.readCellAsStringOrDate(numericCell));
		Assert.assertEquals("abc", Ssio.readCellAsStringOrDate(strCell));
		Assert.assertEquals(now, Ssio.readCellAsStringOrDate(dateCell));

	}

	@SuppressWarnings("unused")
	private static class SsioUnitTestRecord {
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
