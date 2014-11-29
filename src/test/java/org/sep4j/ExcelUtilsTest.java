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

public class ExcelUtilsTest {

	@Rule
	public ExpectedException expectedEx = ExpectedException.none();

	@Test
	public void shouldSaveTest() {
		Assert.assertTrue(ExcelUtils.shouldSave(null, true));
		Assert.assertTrue(ExcelUtils.shouldSave(Arrays.asList(new DatumError()), true));

		Assert.assertTrue(ExcelUtils.shouldSave(null, false));
		Assert.assertTrue(ExcelUtils.shouldSave(new ArrayList<DatumError>(), false));
		Assert.assertFalse(ExcelUtils.shouldSave(Arrays.asList(new DatumError()), false));
	}

	@Test
	public void validateRecordClass_NullClass() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("null");
		ExcelUtils.validateRecordClass(null);
	}

	@Test
	public void createRecordIndexTest() {
		ExcelUtils.createRecordInstance(DefaultConstructorBean.class);
		ExcelUtils.createRecordInstance(PrivateClassBean.class);
	}

	@Test
	public void createRecordIndexTest_NoDefaultConstructor() {
		expectedEx.expect(RuntimeException.class);
		expectedEx.expectMessage("<init>()");
		ExcelUtils.createRecordInstance(NoDefaultConstructorBean.class);
	}

	@Test
	public void validateReverseHeaderMapTest_Null() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("null");
		ExcelUtils.validateReverseHeaderMap(null);
	}

	@Test
	public void validateReverseHeaderMapTest_Empty() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("empty");
		Map<String, String> map = new HashMap<String, String>();
		ExcelUtils.validateReverseHeaderMap(map);
	}

	@Test
	public void validateReverseHeaderMapTest_Positive() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("someText", "someProp");
		ExcelUtils.validateReverseHeaderMap(map);
	}

	@Test
	public void validateReverseHeaderMapTest_TextBlank() {
		Map<String, String> map = new HashMap<String, String>();
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("blank headerText");
		expectedEx.expectMessage("1");

		map.put("text1", "prop1");
		map.put("	", "prop2");
		ExcelUtils.validateReverseHeaderMap(map);
	}

	@Test
	public void validateReverseHeaderMapTest_PropBlank() {
		Map<String, String> map = new HashMap<String, String>();
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("blank propName");
		expectedEx.expectMessage("1");

		map.put("text1", "prop1");
		map.put("text2", "	");
		ExcelUtils.validateReverseHeaderMap(map);
	}

	@Test
	public void validateHeaderMapTest_Null() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("null");
		ExcelUtils.validateHeaderMap(null);
	}

	@Test
	public void validateHeaderMapTest_Empty() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("empty");
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		ExcelUtils.validateHeaderMap(map);
	}

	@Test
	public void validateHeaderMapTest_Positive() {
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("someProp", "someText");
		ExcelUtils.validateHeaderMap(map);
	}

	@Test
	public void validateHeaderMapTest_PropBlank() {
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("blank propName");
		expectedEx.expectMessage("1");

		map.put("prop1", "text1");
		map.put("	", "text2");
		ExcelUtils.validateHeaderMap(map);
	}

	@Test
	public void setPropertyWithCellTextTest_StrProp() {
		UTRecord record = new UTRecord();

		ExcelUtils.setPropertyWithCellValue(UTRecord.class, record, "strProp", "abc");
		Assert.assertEquals("abc", record.getStrProp());

		ExcelUtils.setPropertyWithCellValue(UTRecord.class, record, "strProp", null);
		Assert.assertNull(record.getStrProp());
	}

	@Test
	public void setPropertyWithCellTextTest_IntObjProp() {
		UTRecord record = new UTRecord();

		ExcelUtils.setPropertyWithCellValue(UTRecord.class, record, "intObjProp", "123");
		Assert.assertEquals(new Integer(123), record.getIntObjProp());

		ExcelUtils.setPropertyWithCellValue(UTRecord.class, record, "intObjProp", null);
		Assert.assertNull(record.getIntObjProp());

	}

	@Test
	public void setPropertyWithCellTextTest_IntObjProp_NotNumer() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("No suitable setter");

		UTRecord record = new UTRecord();
		ExcelUtils.setPropertyWithCellValue(UTRecord.class, record, "intObjProp", "abc");

	}

	@Test
	public void setPropertyWithCellTextTest_PrimIntProp() {
		UTRecord record = new UTRecord();

		ExcelUtils.setPropertyWithCellValue(UTRecord.class, record, "primIntProp", "123");
		Assert.assertEquals(123, record.getPrimIntProp());
	}

	@Test
	public void setPropertyWithCellTextTest_PrimIntProp_NullText() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("No suitable setter");

		UTRecord record = new UTRecord();
		ExcelUtils.setPropertyWithCellValue(UTRecord.class, record, "primIntProp", null);
	 

	}

	@Test
	public void setPropertyWithCellTextTest_PrimIntProp_NotNumber() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("No suitable setter");

		UTRecord record = new UTRecord();
		ExcelUtils.setPropertyWithCellValue(UTRecord.class, record, "primIntProp", "abc");
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

		Assert.assertNull(ExcelUtils.readCellAsStringOrDate(null));
		Assert.assertNull(ExcelUtils.readCellAsStringOrDate(blankCell));
		Assert.assertEquals("true", ExcelUtils.readCellAsStringOrDate(boolCell));
		Assert.assertNull(ExcelUtils.readCellAsStringOrDate(errCell));
		Assert.assertNull(ExcelUtils.readCellAsStringOrDate(formulaCell));
		Assert.assertEquals("100.00", ExcelUtils.readCellAsStringOrDate(numericCell));
		Assert.assertEquals("abc", ExcelUtils.readCellAsStringOrDate(strCell));
		Assert.assertEquals(now, ExcelUtils.readCellAsStringOrDate(dateCell));

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
