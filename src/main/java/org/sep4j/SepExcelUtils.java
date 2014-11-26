package org.sep4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.sep4j.support.SepReflectionHelper;

/**
 * The facade to do records saving and retrieving
 * 
 * @author chenjianjx
 * 
 * 
 */
public class SepExcelUtils {

	/**
	 * save records to a new workbook even if there are datum errors in the
	 * records. Any datum error will lead to an empty cell.
	 * 
	 * @param headerMap
	 *            <propName, headerText>, for example <"username" field of User
	 *            class, "User Name" as the excel header text>.
	 * @param records
	 *            the records to save. Note the element type cannot be a private
	 *            class
	 * @param outputStream
	 *            the output stream for the excel
	 * @throws IOException
	 */
	public static <T> void save(LinkedHashMap<String, String> headerMap, Collection<T> records, OutputStream outputStream) throws IOException {
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
	 *            the records to save. Note the element type cannot be a private
	 *            class
	 * @param outputStream
	 *            the output stream for the excel
	 * @param datumErrPlaceholder
	 *            if some datum is wrong, write this place holder to the cell
	 *            (stillSaveIfDataError should be set true)
	 * 
	 * @throws IOException
	 */
	public static <T> void save(LinkedHashMap<String, String> headerMap, Collection<T> records, OutputStream outputStream, String datumErrPlaceholder)
			throws IOException {
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
	 *            the records to save. Note the element type cannot be a private
	 *            class
	 * @param outputStream
	 *            the output stream for the excel
	 * @param datumErrPlaceholder
	 *            if some datum is wrong, write this place holder to the cell
	 *            (stillSaveIfDataError should be set true)
	 * @param datumErrors
	 *            all data errors in the records
	 * 
	 * @throws IOException
	 */
	public static <T> void save(LinkedHashMap<String, String> headerMap, Collection<T> records, OutputStream outputStream,
			String datumErrPlaceholder, List<DatumError> datumErrors) throws IOException {
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
	 *            the records to save. Note the element type cannot be a private
	 *            class
	 * @param outputStream
	 *            the output stream for the excel
	 * @param datumErrPlaceholder
	 *            if some datum is wrong, write this place holder to the cell
	 *            (stillSaveIfDataError should be set true)
	 * @param datumErrors
	 *            all data errors in the records
	 * 
	 * @throws IOException
	 */
	public static <T> void saveIfNoDatumError(LinkedHashMap<String, String> headerMap, Collection<T> records, OutputStream outputStream,
			String datumErrPlaceholder, List<DatumError> datumErrors) throws IOException {
		save(headerMap, records, outputStream, datumErrPlaceholder, datumErrors, false);
	}

	/**
	 * save records to a new workbook even if there are datum errors in the
	 * records.
	 * 
	 * @param headerMap
	 *            <propName, headerText>, for example <"username" of User class,
	 *            "User Name" as the excel header>.
	 * @param records
	 *            the records to save. Note the element type cannot be a private
	 *            class
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
	 * @throws IOException
	 */
	static <T> void save(LinkedHashMap<String, String> headerMap, Collection<T> records, OutputStream outputStream, String datumErrPlaceholder,
			List<DatumError> datumErrors, boolean stillSaveIfDataError) throws IOException {
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

		if (datumErrors != null && datumErrors.size() > 0 && !stillSaveIfDataError) {
			return;
		}
		wb.write(outputStream);
	}

	static <T> List<T> parse(Map<String, String> reverseHeaderMap, InputStream inputStream, List<CellError> cellErrors, Class<T> recordClass)
			throws InvalidFormatException, InvalidHeaderRowException, IOException {
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

		Workbook workbook = WorkbookFactory.create(inputStream);
		if (workbook.getNumberOfSheets() <= 0) {
			return new ArrayList<T>();
		}

		Sheet sheet = workbook.getSheetAt(0);
		// less than two rows, meaning no data rows or nothing
		if (sheet.getLastRowNum() < 1) {
			return new ArrayList<T>();
		}

		// key = columnIndex, value= propName
		Map<Short, ColumnMeta> columnMetaMap = parseHeader(reverseHeaderMap, sheet.getRow(0));
		if (columnMetaMap.isEmpty()) {
			throw new InvalidHeaderRowException();
		}

		// now do the data rows
		List<T> records = new ArrayList<T>();
		for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
			Row row = sheet.getRow(rowIndex);
			T record = parseDataRow(columnMetaMap, row, rowIndex, recordClass, cellErrors);
			records.add(record);
		}
		return records;
	}

	private static <T> T parseDataRow(Map<Short, ColumnMeta> columnMetaMap, Row row, int rowIndex, Class<T> recordClass, List<CellError> cellErrors) {
		T record = newInstance(recordClass);

		for (short columnIndex = 0; columnIndex < row.getLastCellNum(); columnIndex++) {
			ColumnMeta columnMeta = columnMetaMap.get(columnIndex);
			if (columnMeta == null || columnMeta.propName == null) {
				continue;
			}

			Cell cell = row.getCell(columnIndex);
			String text = readCellAsString(cell);
			try {
				Method setter = SepReflectionHelper.findSetterByPropNameAndType(recordClass, columnMeta.propName, String.class);
				if (setter != null) {
					SepReflectionHelper.invokeSetter(setter, record, text);
				}
			} catch (Exception e) {
				if (cellErrors != null) {
					CellError ce = new CellError();
					ce.setColumnIndex(columnIndex);
					ce.setHeaderText(columnMeta.headerText);
					ce.setPropName(columnMeta.propName);
					ce.setRowIndex(rowIndex);
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

	private static <T> T newInstance(Class<T> recordClass) {
		try {
			return recordClass.newInstance();
		} catch (InstantiationException e) {
			throw new IllegalArgumentException(e);
		} catch (IllegalAccessException e) {
			throw new IllegalArgumentException(e);
		}
	}

	private static Map<Short, ColumnMeta> parseHeader(Map<String, String> reverseHeaderMap, Row row) {
		Map<Short, ColumnMeta> columnMetaMap = new LinkedHashMap<Short, ColumnMeta>();

		// note that row.getLastCellNum() is one-based
		for (short columnIndex = 0; columnIndex < row.getLastCellNum(); columnIndex++) {
			Cell cell = row.getCell(columnIndex);
			String headerText = readCellAsString(cell);
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

	/**
	 * read the cell's string value no matter what type the cell is of. Actually
	 * it only supports 3 types: string, numeric and boolean.
	 * 
	 * @param cell
	 * @return the string representation of the cell (will be trimmed to null)
	 */
	private static String readCellAsString(Cell cell) {
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
			double v = cell.getNumericCellValue();
			return String.valueOf(v);
		}

		if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
			String s = cell.getStringCellValue();
			return StringUtils.trimToNull(s);
		}
		return null;

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

	private static Row createHeaders(LinkedHashMap<String, String> headerMap, Sheet sheet) {
		Row header = sheet.createRow(0);
		int columnIndex = 0;
		for (Map.Entry<String, String> entry : headerMap.entrySet()) {
			String headerText = StringUtils.defaultString(entry.getValue());
			createCell(header, columnIndex).setCellValue(headerText);
			columnIndex++;
		}
		return header;
	}

	private static <T> Row createRow(LinkedHashMap<String, String> headerMap, T record, int recordIndex, Sheet sheet, int rowIndex,
			String datumErrPlaceholder, List<DatumError> datumErrors) {
		Row row = sheet.createRow(rowIndex);
		int columnIndex = 0;

		for (Map.Entry<String, String> entry : headerMap.entrySet()) {
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
				propValue = datumErrPlaceholder;
			}
			String propValueText = (propValue == null ? null : propValue.toString());
			createCell(row, columnIndex).setCellValue(StringUtils.defaultString(propValueText));
			columnIndex++;
		}

		return row;
	}

	private static Cell createCell(Row row, int columnIndex) {
		Cell cell = row.createCell(columnIndex);
		return cell;
	}
}
