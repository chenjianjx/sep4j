package org.sep4j;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * The facade to do records saving and retrieving
 * 
 * @author chenjianjx
 * 
 * 
 */
public class SepExcelUtils {

	// public static <T> void saveSuppressingDatumErr(LinkedHashMap<String,
	// String> headerMap, List<T> records, OutputStream outputStream,
	// String sheetName, String datumErrPlaceholder) throws IOException {
	// try {
	// save(headerMap, records, outputStream, sheetName, true,
	// datumErrPlaceholder);
	// } catch (DatumError e) {
	// throw new
	// IllegalStateException("This should never happen. Something is wrong with this code");
	// }
	// }
	//

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
	 *            the records to save.
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
	 * to the cell. All the datum errors will be saved to dataErrors
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
	 * @param dataErrors
	 *            all data errors in the records
	 * 
	 * @throws IOException
	 */
	public static <T> void save(LinkedHashMap<String, String> headerMap, Collection<T> records, OutputStream outputStream,
			String datumErrPlaceholder, List<DatumError> dataErrors) throws IOException {
		save(headerMap, records, outputStream, datumErrPlaceholder, dataErrors, true);
	}

	/**
	 * save records to a new workbook only if there are no datum errors in the
	 * records. Any datum error will lead to datumErrPlaceholder being written
	 * to the cell. All the datum errors will be saved to dataErrors
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
	 * @param dataErrors
	 *            all data errors in the records
	 * 
	 * @throws IOException
	 */
	public static <T> void saveIfNoDatumError(LinkedHashMap<String, String> headerMap, Collection<T> records, OutputStream outputStream,
			String datumErrPlaceholder, List<DatumError> dataErrors) throws IOException {
		save(headerMap, records, outputStream, datumErrPlaceholder, dataErrors, false);
	}

	/**
	 * save records to a new workbook even if there are datum errors in the
	 * records.
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
	 * @param dataErrors
	 *            all data errors in the records
	 * @param stillSaveIfDataError
	 *            if there are errors in data, should we still save the records
	 *            ?
	 * 
	 * @throws IOException
	 */
	static <T> void save(LinkedHashMap<String, String> headerMap, Collection<T> records, OutputStream outputStream, String datumErrPlaceholder,
			List<DatumError> dataErrors, boolean stillSaveIfDataError) throws IOException {
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
			createRow(headerMap, record, recordIndex, sheet, rowIndex, datumErrPlaceholder, dataErrors);
			recordIndex++;
		}

		if (dataErrors != null && dataErrors.size() > 0 && !stillSaveIfDataError) {
			return;
		}
		wb.write(outputStream);
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
			String datumErrPlaceholder, List<DatumError> dataErrors) {
		Row row = sheet.createRow(rowIndex);
		int columnIndex = 0;

		for (Map.Entry<String, String> entry : headerMap.entrySet()) {
			String propName = entry.getKey();
			Object propValue = null;
			try {

				propValue = PropertyUtils.getProperty(record, propName);
			} catch (Exception e) {
				if (dataErrors != null) {
					DatumError de = new DatumError();
					de.setPropName(propName);
					de.setRecordIndex(recordIndex);
					de.setCause(e);
					dataErrors.add(de);
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
