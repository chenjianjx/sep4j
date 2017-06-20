package org.sep4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.sep4j.support.SepBasicTypeConverts;
import org.sep4j.support.SepReflectionHelper;

/**
 * The facade to do records saving and retrieving
 * 
 * @author chenjianjx
 * 
 * 
 */
public class ExcelUtils {

	/**
	 * save records to a new workbook even if there are datum errors in the
	 * records. Any datum error will lead to an empty cell.
	 * 
	 * @param headerMap
	 *            <propName, headerText>, for example <"username" field of User
	 *            class, "User Name" as the excel header text>.
	 * @param records
	 *            the records to save.
	 * @param outputStream
	 *            the output stream for the excel
	 * 
	 */
	public static <T> void save(LinkedHashMap<String, String> headerMap, Collection<T> records, OutputStream outputStream) {
		save(headerMap, records, outputStream, null, null, true);
	}

	/**
	 * save records to a new workbook even if there are datum errors in the
	 * records. Any datum error will lead to datumErrPlaceholder being written
	 * to the cell.
	 * 
	 * @param headerMap
	 *            <propName, headerText>, for example <"username" of User class,
	 *            "User Name" as the excel header>.
	 * 
	 * @param records
	 *            the records to save.
	 * @param outputStream
	 *            the output stream for the excel
	 * @param datumErrPlaceholder
	 *            if some datum is wrong, write this place holder to the cell
	 *            (stillSaveIfDataError should be set true)
	 * 
	 * 
	 */
	public static <T> void save(LinkedHashMap<String, String> headerMap, Collection<T> records, OutputStream outputStream, String datumErrPlaceholder) {
		save(headerMap, records, outputStream, datumErrPlaceholder, null, true);
	}

	/**
	 * save records to a new workbook even if there are datum errors in the
	 * records. Any datum error will lead to datumErrPlaceholder being written
	 * to the cell. All the datum errors will be saved to datumErrors indicating
	 * the recordIndex of the datum
	 * 
	 * @param headerMap
	 *            <propName, headerText>, for example <"username" of User class,
	 *            "User Name" as the excel header>.
	 * @param records
	 *            the records to save.
	 * @param outputStream
	 *            the output stream for the excel
	 * @param datumErrPlaceholder
	 *            if some datum is wrong, write this place holder to the cell
	 *            (stillSaveIfDataError should be set true)
	 * @param datumErrors
	 *            all data errors in the records
	 * 
	 * 
	 */
	public static <T> void save(LinkedHashMap<String, String> headerMap, Collection<T> records, OutputStream outputStream,
			String datumErrPlaceholder, List<DatumError> datumErrors) {
		save(headerMap, records, outputStream, datumErrPlaceholder, datumErrors, true);
	}

	/**
	 * save records to a new workbook only if there are no datum errors in the
	 * records. Any datum error will lead to datumErrPlaceholder being written
	 * to the cell. All the datum errors will be saved to datumErrors indicating
	 * the recordIndex of the datum
	 * 
	 * @param headerMap
	 *            <propName, headerText>, for example <"username" of User class,
	 *            "User Name" as the excel header>.
	 * @param records
	 *            the records to save.
	 * @param outputStream
	 *            the output stream for the excel
	 * @param datumErrPlaceholder
	 *            if some datum is wrong, write this place holder to the cell
	 *            (stillSaveIfDataError should be set true)
	 * @param datumErrors
	 *            all data errors in the records
	 * 
	 * 
	 */
	public static <T> void saveIfNoDatumError(LinkedHashMap<String, String> headerMap, Collection<T> records, OutputStream outputStream,
			String datumErrPlaceholder, List<DatumError> datumErrors) {
		save(headerMap, records, outputStream, datumErrPlaceholder, datumErrors, false);
	}

	/**
	 * please check the doc of {@link #parse(Map, InputStream, List, Class)}.
	 * The difference is that this class ignore any all the errors and make sure
	 * no checked exception is thrown(There may still be unchecked exceptions,
	 * though).
	 * 
	 * @param reverseHeaderMap
	 * @param inputStream
	 * @param recordClass
	 * @return
	 */
	public static <T> List<T> parseIgnoringErrors(Map<String, String> reverseHeaderMap, InputStream inputStream, Class<T> recordClass) {
		try {
			return parse(reverseHeaderMap, inputStream, null, recordClass);
		} catch (InvalidFormatException e1) {
			// ignore
			return new ArrayList<T>();
		} catch (InvalidHeaderRowException e1) {
			// ignore
			return new ArrayList<T>();
		}

	}

	/**
	 * parse an excel to a list of beans. <br/>
	 * The columns are not identified by the column indexes, but by the header
	 * rows' text of the columns specified by parameter reverseHeaderMap , i.e.
	 * you don't have to worry which column to put "username". All you need to
	 * do is to let the excel have a header column named "User Name" and
	 * associate it with "username" property in parameter reverseHeaderMap
	 * 
	 * @param reverseHeaderMap
	 *            <headerText, propName>, for example <"User Name" as the excel
	 *            header, "username" of User class>.
	 * 
	 * @param inputStream
	 * @param cellErrors
	 *            the errors of data rows (not including header row) found while
	 *            being parsed. The error here can tell you which cell is wrong.
	 * @param recordClass
	 *            the class the java bean. It must have a default constructor
	 * @return a list of beans
	 * @throws InvalidFormatException
	 *             the input stream doesn't represent a valid excel
	 * @throws InvalidHeaderRowException
	 *             the header row of the excel is not valid, for example, no
	 *             headerText accords to that of the reverseHeaerMap
	 */
	public static <T> List<T> parse(Map<String, String> reverseHeaderMap, InputStream inputStream, List<CellError> cellErrors, Class<T> recordClass)
			throws InvalidFormatException, InvalidHeaderRowException {
		validateReverseHeaderMap(reverseHeaderMap);

		validateRecordClass(recordClass);

		Workbook workbook = toWorkbook(inputStream);
		if (workbook.getNumberOfSheets() <= 0) {
			return new ArrayList<T>();
		}

		Sheet sheet = workbook.getSheetAt(0);

		// key = columnIndex, value= {propName, headerText}
		Map<Short, ColumnMeta> columnMetaMap = parseHeader(reverseHeaderMap, sheet.getRow(0));
		if (columnMetaMap.isEmpty()) {
			throw new InvalidHeaderRowException();
		}

		// now do the data rows
		List<T> records = new ArrayList<T>();
		for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
			Row row = sheet.getRow(rowIndex);
			if(row == null){
				continue;
			}
			T record = parseDataRow(columnMetaMap, row, rowIndex, recordClass, cellErrors);
			records.add(record);
		}
		return records;
	}

	/**
	 * save records to a new workbook.
	 * 
	 * @param headerMap
	 *            <propName, headerText>, for example <"username" of User class,
	 *            "User Name" as the excel header>.
	 * @param records
	 *            the records to save.
	 * @param outputStream
	 *            the output stream for the excel
	 * @param datumErrPlaceholder
	 *            if some datum is wrong, write this place holder to the cell
	 *            (stillSaveIfDataError should be set true)
	 * @param datumErrors
	 *            all data errors in the records
	 * @param stillSaveIfDataError
	 *            if there are errors in data, should we still save the records
	 *            ?
	 * 
	 * 
	 */
	static <T> void save(LinkedHashMap<String, String> headerMap, Collection<T> records, OutputStream outputStream, String datumErrPlaceholder,
			List<DatumError> datumErrors, boolean stillSaveIfDataError) {
		validateHeaderMap(headerMap);

		if (records == null) {
			records = new ArrayList<T>();
		}
		if (outputStream == null) {
			throw new IllegalArgumentException("the outputStream can not be null");
		}

		Workbook wb = new XSSFWorkbook();
		Sheet sheet = wb.createSheet();

		createHeaders(headerMap, sheet);

		int recordIndex = 0;
		for (T record : records) {
			int rowIndex = recordIndex + 1;
			createRow(headerMap, record, recordIndex, sheet, rowIndex, datumErrPlaceholder, datumErrors);
			recordIndex++;
		}

		if (shouldSave(datumErrors, stillSaveIfDataError)) {
			writeWorkbook(wb, outputStream);
		}

	}

	/**
	 * the workbook has been generated. Should we write it to the outputstream?
	 * 
	 * @param datumErrors
	 * @param stillSaveIfDataError
	 * @return
	 */
	static boolean shouldSave(List<DatumError> datumErrors, boolean stillSaveIfDataError) {
		if (stillSaveIfDataError) {
			return true;
		}
		if (datumErrors == null || datumErrors.isEmpty()) {
			return true;
		} else {
			return false;
		}
	}

	static void validateRecordClass(Class<?> recordClass) {
		if (recordClass == null) {
			throw new IllegalArgumentException("the recordClass can not be null");
		}
	}

	static void validateReverseHeaderMap(Map<String, String> reverseHeaderMap) {
		if (reverseHeaderMap == null || reverseHeaderMap.isEmpty()) {
			throw new IllegalArgumentException("the reverseHeaderMap can not be null or empty");
		}

		int columnIndex = 0;
		for (Map.Entry<String, String> entry : reverseHeaderMap.entrySet()) {
			String headerText = entry.getKey();
			String propName = entry.getValue();
			if (StringUtils.isBlank(headerText)) {
				throw new IllegalArgumentException("One header defined in the reverseHeaderMap has a blank headerText. Header Index (0-based) = "
						+ columnIndex);
			}

			if (StringUtils.isBlank(propName)) {
				throw new IllegalArgumentException("One header defined in the reverseHeaderMap has a blank propName. Header Index (0-based) = "
						+ columnIndex);
			}
			columnIndex++;
		}
	}

	static void validateHeaderMap(LinkedHashMap<String, String> headerMap) {
		if (headerMap == null || headerMap.isEmpty()) {
			throw new IllegalArgumentException("the headerMap can not be null or empty");
		}
		int columnIndex = 0;
		for (Map.Entry<String, String> entry : headerMap.entrySet()) {
			String propName = entry.getKey();
			if (StringUtils.isBlank(propName)) {
				throw new IllegalArgumentException("One header has a blank propName. Header Index (0-based) = " + columnIndex);
			}
			columnIndex++;
		}
	}

	static <T> void setPropertyWithCellValue(Class<T> recordClass, T record, String propName, Object cellStringOrDate) {
		IllegalArgumentException noSetterException = new IllegalArgumentException(MessageFormat.format(
				"No suitable setter for property \"{0}\" with cellValue \"{1}\" ", propName, cellStringOrDate));
		List<Method> setters = SepReflectionHelper.findSettersByPropName(recordClass, propName);

		//no setter for this prop
		if (setters.isEmpty()) {
			throw noSetterException;
		}

		if (cellStringOrDate == null) {
			// in this case, try all the setters one by one
			for (Method setter : setters) {
				Class<?> propClass = setter.getParameterTypes()[0];
				if (SepBasicTypeConverts.canFromNull(propClass)) {					
					SepReflectionHelper.invokeSetter(setter, record, null);
					return;
				}
			}
			throw noSetterException;
		}

		if (cellStringOrDate instanceof java.util.Date) {
			Method setter = SepReflectionHelper.findSetterByPropNameAndType(recordClass, propName, java.util.Date.class);
			if (setter == null) {
				throw noSetterException;
			} else {
				SepReflectionHelper.invokeSetter(setter, record, cellStringOrDate);
				return;
			}
		}

		// ok, we got a string
		String cellText = (String) cellStringOrDate;

		// try to find a string-type setter first
		Method stringSetter = SepReflectionHelper.findSetterByPropNameAndType(recordClass, propName, String.class);
		if (stringSetter != null) {
			SepReflectionHelper.invokeSetter(stringSetter, record, cellText);
			return;
		}

		// no string-type setter? do a guess!

		for (Method setter : setters) {
			Class<?> propClass = setter.getParameterTypes()[0];
			if (SepBasicTypeConverts.canFromThisString(cellText, propClass)) {
				Object propValue = SepBasicTypeConverts.fromThisString(cellText, propClass);
				SepReflectionHelper.invokeSetter(setter, record, propValue);
				return;
			}
		}

		throw noSetterException;
	}

	static <T> T createRecordInstance(Class<T> recordClass) {
		try {
			Constructor<T> constructor = recordClass.getDeclaredConstructor(new Class[0]);
			constructor.setAccessible(true);
			return constructor.newInstance(new Object[0]);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * read the cell. it only supports: boolean, numeric, date(numeric cell type
	 * + date cell format) and string.
	 * 
	 * @param cell
	 * @return the date if it is a date cell, or else the string value (will be
	 *         trimmed to null) . <br/>
	 * 
	 * 
	 */
	static Object readCellAsStringOrDate(Cell cell) {
		if (cell == null) {
			return null;
		}

		if (cell.getCellType() == Cell.CELL_TYPE_BLANK) {
			return null;
		}

		if (cell.getCellType() == Cell.CELL_TYPE_BOOLEAN) {
			return String.valueOf(cell.getBooleanCellValue());
		}

		if (cell.getCellType() == Cell.CELL_TYPE_ERROR) {
			return null;
		}

		if (cell.getCellType() == Cell.CELL_TYPE_FORMULA) {
			return null;
		}

		if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
			if (DateUtil.isCellDateFormatted(cell)) {
				return cell.getDateCellValue();
			} else {
				double v = cell.getNumericCellValue();
				return String.valueOf(v);
			}
		}

		if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
			String s = cell.getStringCellValue();
			return StringUtils.trimToNull(s);
		}
		return null;

	}

	private static <T> T parseDataRow(Map<Short, ColumnMeta> columnMetaMap, Row row, int rowIndex, Class<T> recordClass, List<CellError> cellErrors) {
		T record = createRecordInstance(recordClass);

		for (short columnIndex = 0; columnIndex < row.getLastCellNum(); columnIndex++) {
			ColumnMeta columnMeta = columnMetaMap.get(columnIndex);
			if (columnMeta == null || columnMeta.propName == null) {
				continue;
			}
			String propName = columnMeta.propName;
			Cell cell = row.getCell(columnIndex);
			Object cellStringOrDate = readCellAsStringOrDate(cell);
			try {
				setPropertyWithCellValue(recordClass, record, propName, cellStringOrDate);
			} catch (Exception e) {
				if (cellErrors != null) {
					CellError ce = new CellError();
					ce.setColumnIndex(columnIndex);
					ce.setHeaderText(columnMeta.headerText);
					ce.setPropName(propName);
					ce.setRowIndex(rowIndex);
					ce.setCause(e);
					cellErrors.add(ce);
				}
			}
		}

		return record;
	}

	/**
	 * meta info about a column
	 * 
	 * 
	 */
	private static class ColumnMeta {
		public String propName;
		public String headerText;

		@Override
		public String toString() {
			return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
		}
	}

	/**
	 * to get <columnIndex, column info>
	 * 
	 * @param reverseHeaderMap
	 * @param row
	 * @return
	 */
	private static Map<Short, ColumnMeta> parseHeader(Map<String, String> reverseHeaderMap, Row row) {
		Map<Short, ColumnMeta> columnMetaMap = new LinkedHashMap<Short, ColumnMeta>();

		// note that row.getLastCellNum() is one-based
		for (short columnIndex = 0; columnIndex < row.getLastCellNum(); columnIndex++) {
			Cell cell = row.getCell(columnIndex);
			Object headerObj = readCellAsStringOrDate(cell);
			String headerText = headerObj == null ? "" : headerObj.toString();
			if (headerText == null) {
				continue;
			}
			String propName = reverseHeaderMap.get(headerText);
			if (propName == null) {
				continue;
			}

			ColumnMeta cm = new ColumnMeta();
			cm.headerText = headerText;
			cm.propName = propName;
			columnMetaMap.put(columnIndex, cm);
		}
		return columnMetaMap;
	}

	private static Row createHeaders(LinkedHashMap<String, String> headerMap, Sheet sheet) {
		CellStyle style = sheet.getWorkbook().createCellStyle();
		style.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
		style.setFillPattern(CellStyle.SOLID_FOREGROUND);
		style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		style.setBorderTop(HSSFCellStyle.BORDER_THIN);
		style.setBorderRight(HSSFCellStyle.BORDER_THIN);
		style.setBorderLeft(HSSFCellStyle.BORDER_THIN);

		Row header = sheet.createRow(0);
		int columnIndex = 0;
		for (Map.Entry<String, String> entry : headerMap.entrySet()) {
			String headerText = StringUtils.defaultString(entry.getValue());
			Cell cell = createCell(header, columnIndex);
			cell.setCellValue(headerText);
			cell.setCellStyle(style);
			sheet.autoSizeColumn(columnIndex);
			columnIndex++;
		}

		return header;
	}

	private static <T> Row createRow(LinkedHashMap<String, String> headerMap, T record, int recordIndex, Sheet sheet, int rowIndex,
			String datumErrPlaceholder, List<DatumError> datumErrors) {
		Row row = sheet.createRow(rowIndex);
		int columnIndex = 0;

		for (Map.Entry<String, String> entry : headerMap.entrySet()) {
			boolean datumErr = false;
			String propName = entry.getKey();
			Object propValue = null;
			try {
				propValue = SepReflectionHelper.getProperty(record, propName);
			} catch (Exception e) {
				if (datumErrors != null) {
					DatumError de = new DatumError();
					de.setPropName(propName);
					de.setRecordIndex(recordIndex);
					de.setCause(e);
					datumErrors.add(de);
				}
				datumErr = true;
				propValue = datumErrPlaceholder;
			}
			String propValueText = (propValue == null ? null : propValue.toString());
			Cell cell = createCell(row, columnIndex);
			cell.setCellValue(StringUtils.defaultString(propValueText));

			if (datumErr) {
				CellStyle errStyle = sheet.getWorkbook().createCellStyle();
				errStyle.setFillForegroundColor(IndexedColors.RED.getIndex());
				errStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
				cell.setCellStyle(errStyle);
			}

			columnIndex++;
		}

		return row;
	}

	private static Cell createCell(Row row, int columnIndex) {
		Cell cell = row.createCell(columnIndex);
		return cell;
	}

	private static void writeWorkbook(Workbook workbook, OutputStream outputStream) {
		try {
			workbook.write(outputStream);
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	private static Workbook toWorkbook(InputStream inputStream) throws InvalidFormatException {
		try {
			return WorkbookFactory.create(inputStream);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
