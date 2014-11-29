package org.sep4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import junit.framework.Assert;

import org.apache.commons.io.FileUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * the integration test
 * 
 * @author chenjianjx
 * 
 */

public class SepExcelUtilsITest {

	@Rule
	public ExpectedException expectedEx = ExpectedException.none();

	@Test
	public void saveIfNoErrorTest() throws InvalidFormatException, IOException {
		LinkedHashMap<String, String> headerMap = new LinkedHashMap<String, String>();
		headerMap.put("fake", "Not Real");

		ITRecord record = new ITRecord();

		Collection<ITRecord> records = Arrays.asList(record);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		List<DatumError> datumErrors = new ArrayList<DatumError>();

		// save it
		SepExcelUtils.saveIfNoDatumError(headerMap, records, outputStream, null, datumErrors);

		byte[] excel = outputStream.toByteArray();
		Assert.assertEquals(0, excel.length);
		Assert.assertEquals(1, datumErrors.size());

	}

	@Test
	public void saveTest_ValidAndInvalid() throws InvalidFormatException, IOException {
		LinkedHashMap<String, String> headerMap = new LinkedHashMap<String, String>();
		headerMap.put("primInt", "Primitive Int");
		headerMap.put("fake", "Not Real");

		ITRecord record = new ITRecord();
		record.setPrimInt(123);

		Collection<ITRecord> records = Arrays.asList(record);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		String datumErrPlaceholder = "!!ERROR!!";
		List<DatumError> datumErrors = new ArrayList<DatumError>();

		// save it
		SepExcelUtils.save(headerMap, records, outputStream, datumErrPlaceholder, datumErrors);
		byte[] excel = outputStream.toByteArray();

		// do a save for human eye check
		FileUtils.writeByteArrayToFile(newFile("saveTest_ValidAndInvalid"), excel);

		// then parse it
		Workbook workbook = WorkbookFactory.create(new ByteArrayInputStream(excel));

		/*** do assertions ***/
		Sheet sheet = workbook.getSheetAt(0);
		Row headerRow = sheet.getRow(0);
		Row dataRow = sheet.getRow(1);

		Cell cell00 = headerRow.getCell(0);
		Cell cell01 = headerRow.getCell(1);
		Cell cell10 = dataRow.getCell(0);
		Cell cell11 = dataRow.getCell(1);

		// size
		Assert.assertEquals(1, sheet.getLastRowNum());
		Assert.assertEquals(2, headerRow.getLastCellNum()); // note cell num is
															// 1-based
		Assert.assertEquals(2, dataRow.getLastCellNum());

		// types
		Assert.assertEquals(Cell.CELL_TYPE_STRING, cell00.getCellType());
		Assert.assertEquals(Cell.CELL_TYPE_STRING, cell01.getCellType());
		Assert.assertEquals(Cell.CELL_TYPE_STRING, cell10.getCellType());
		Assert.assertEquals(Cell.CELL_TYPE_STRING, cell11.getCellType());

		// texts
		Assert.assertEquals("Primitive Int", cell00.getStringCellValue());
		Assert.assertEquals("Not Real", cell01.getStringCellValue());
		Assert.assertEquals("123", cell10.getStringCellValue());
		Assert.assertEquals("!!ERROR!!", cell11.getStringCellValue());

		// errors
		DatumError datumError = datumErrors.get(0);
		Assert.assertEquals(1, datumErrors.size());
		Assert.assertEquals(0, datumError.getRecordIndex());
		Assert.assertEquals("fake", datumError.getPropName());
		Assert.assertTrue(datumError.getCause().getMessage().contains("no getter method"));

	}

	@Test
	public void saveTest_IngoringErrors() throws InvalidFormatException, IOException {
		LinkedHashMap<String, String> headerMap = new LinkedHashMap<String, String>();
		headerMap.put("fake", "Not Real");

		ITRecord record = new ITRecord();

		Collection<ITRecord> records = Arrays.asList(record);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		// save it
		SepExcelUtils.save(headerMap, records, outputStream);
		byte[] excel = outputStream.toByteArray();

		// do a save for human eye check
		FileUtils.writeByteArrayToFile(newFile("saveTest_IngoringErrors"), excel);

		// then parse it
		Workbook workbook = WorkbookFactory.create(new ByteArrayInputStream(excel));

		/*** do assertions ***/
		Sheet sheet = workbook.getSheetAt(0);
		Row headerRow = sheet.getRow(0);
		Row dataRow = sheet.getRow(1);

		Cell cell00 = headerRow.getCell(0);
		Cell cell10 = dataRow.getCell(0);

		// size
		Assert.assertEquals(1, sheet.getLastRowNum());
		Assert.assertEquals(1, headerRow.getLastCellNum()); // note cell num is
															// 1-based
		Assert.assertEquals(1, dataRow.getLastCellNum());

		// types
		Assert.assertEquals(Cell.CELL_TYPE_STRING, cell00.getCellType());
		Assert.assertEquals(Cell.CELL_TYPE_STRING, cell10.getCellType());

		// texts
		Assert.assertEquals("Not Real", cell00.getStringCellValue());
		Assert.assertEquals("", cell10.getStringCellValue());

	}

	@Test
	public void saveTest_HeadersOnly() throws InvalidFormatException, IOException {

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		// save it
		SepExcelUtils.save(ITRecord.getHeaderMap(), null, outputStream);
		byte[] excel = outputStream.toByteArray();

		// do a save for human eye check
		FileUtils.writeByteArrayToFile(newFile("saveTest_HeadersOnly"), excel);

		// then parse it
		Workbook workbook = WorkbookFactory.create(new ByteArrayInputStream(excel));

		/*** do assertions ***/
		Sheet sheet = workbook.getSheetAt(0);
		Row headerRow = sheet.getRow(0);

		// size
		Assert.assertEquals(0, sheet.getLastRowNum());
		Assert.assertEquals(ITRecord.getHeaderMap().size(), headerRow.getLastCellNum());

	}

	@Test
	public void saveTest_BigNumber() throws InvalidFormatException, IOException {
		LinkedHashMap<String, String> headerMap = new LinkedHashMap<String, String>();
		headerMap.put("bigInteger", "Big Int");
		headerMap.put("bigDecimal", "Big Decimal");

		String bigIntegerStr = "" + Long.MAX_VALUE;
		String bigDecimalStr = Long.MAX_VALUE + "." + Long.MAX_VALUE;
		ITRecord record = new ITRecord();
		record.setBigInteger(new BigInteger(bigIntegerStr));
		record.setBigDecimal(new BigDecimal(bigDecimalStr));

		Collection<ITRecord> records = Arrays.asList(record);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		List<DatumError> datumErrors = new ArrayList<DatumError>();

		// save it
		SepExcelUtils.save(headerMap, records, outputStream, null, datumErrors);
		byte[] excel = outputStream.toByteArray();

		// do a save for human eye check
		FileUtils.writeByteArrayToFile(newFile("saveTest_BigNumber"), excel);

		// then parse it
		Workbook workbook = WorkbookFactory.create(new ByteArrayInputStream(excel));

		/*** do assertions ***/
		Sheet sheet = workbook.getSheetAt(0);
		Row dataRow = sheet.getRow(1);

		Cell cell10 = dataRow.getCell(0);
		Cell cell11 = dataRow.getCell(1);

		// texts
		Assert.assertEquals(bigIntegerStr, cell10.getStringCellValue());
		Assert.assertEquals(bigDecimalStr, cell11.getStringCellValue());

		// errors
		Assert.assertEquals(0, datumErrors.size());

	}

	private File newFile(String prefix) {
		File dir = new File(System.getProperty("user.home"), "/temp/sep");
		dir.mkdirs();
		String filename = prefix + System.currentTimeMillis() + ".xlsx";
		File file = new File(dir, filename);
		return file;
	}

	@SuppressWarnings("unused")
	private static class ITRecord {
		private static LinkedHashMap<String, String> headerMap = new LinkedHashMap<String, String>();
		static {
			headerMap.put("primShort", "Primitive Short");
			headerMap.put("primInt", "Primitive Int");
			headerMap.put("primLong", "Primitive Long");
			headerMap.put("primFloat", "Primitive Float");
			headerMap.put("primDouble", "Primitive Double");
			headerMap.put("primBoolean", "Primitive Boolean");

			headerMap.put("objShort", "Object Short");
			headerMap.put("objInt", "Object Int");
			headerMap.put("objLong", "Object Long");
			headerMap.put("objFloat", "Object Float");
			headerMap.put("objDouble", "Object Double");
			headerMap.put("objBoolean", "Object Boolean");

			headerMap.put("bigInteger", "Big Integer");
			headerMap.put("bigDecimal", "Big Decimal");
			headerMap.put("str", "String");
			headerMap.put("date", "Date");
		}
		private short primShort;
		private int primInt;
		private long primLong;
		private float primFloat;
		private double primDouble;
		private boolean primBoolean;

		private Short objShort;
		private Integer objInt;
		private Long objLong;
		private Float objFloat;
		private Double objDouble;
		private Boolean objBoolean;

		private BigInteger bigInteger;
		private BigDecimal bigDecimal;

		private String str;
		private Date date;

		public static LinkedHashMap<String, String> getHeaderMap() {
			return new LinkedHashMap<String, String>(headerMap);
		}

		public Date getDate() {
			return date;
		}

		public void setDate(Date date) {
			this.date = date;
		}

		public short getPrimShort() {
			return primShort;
		}

		public void setPrimShort(short primShort) {
			this.primShort = primShort;
		}

		public int getPrimInt() {
			return primInt;
		}

		public void setPrimInt(int primInt) {
			this.primInt = primInt;
		}

		public long getPrimLong() {
			return primLong;
		}

		public void setPrimLong(long primLong) {
			this.primLong = primLong;
		}

		public float getPrimFloat() {
			return primFloat;
		}

		public void setPrimFloat(float primFloat) {
			this.primFloat = primFloat;
		}

		public double getPrimDouble() {
			return primDouble;
		}

		public void setPrimDouble(double primDouble) {
			this.primDouble = primDouble;
		}

		public boolean isPrimBoolean() {
			return primBoolean;
		}

		public void setPrimBoolean(boolean primBoolean) {
			this.primBoolean = primBoolean;
		}

		public Short getObjShort() {
			return objShort;
		}

		public void setObjShort(Short objShort) {
			this.objShort = objShort;
		}

		public Integer getObjInt() {
			return objInt;
		}

		public void setObjInt(Integer objInt) {
			this.objInt = objInt;
		}

		public Long getObjLong() {
			return objLong;
		}

		public void setObjLong(Long objLong) {
			this.objLong = objLong;
		}

		public Float getObjFloat() {
			return objFloat;
		}

		public void setObjFloat(Float objFloat) {
			this.objFloat = objFloat;
		}

		public Double getObjDouble() {
			return objDouble;
		}

		public void setObjDouble(Double objDouble) {
			this.objDouble = objDouble;
		}

		public Boolean getObjBoolean() {
			return objBoolean;
		}

		public void setObjBoolean(Boolean objBoolean) {
			this.objBoolean = objBoolean;
		}

		public BigDecimal getBigDecimal() {
			return bigDecimal;
		}

		public void setBigDecimal(BigDecimal bigDecimal) {
			this.bigDecimal = bigDecimal;
		}

		public BigInteger getBigInteger() {
			return bigInteger;
		}

		public void setBigInteger(BigInteger bigInteger) {
			this.bigInteger = bigInteger;
		}

		public String getStr() {
			return str;
		}

		public void setStr(String str) {
			this.str = str;
		}

	}
}
