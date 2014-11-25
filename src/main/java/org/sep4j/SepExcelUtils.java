package org.sep4j;

import java.io.IOException;
import java.io.OutputStream;
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

	public static <T> void saveSuppressingDatumErr(LinkedHashMap<String, String> headerMap, List<T> records, OutputStream outputStream,
			String sheetName, String datumErrPlaceholder) throws IOException {
		try {
			save(headerMap, records, outputStream, sheetName, true, datumErrPlaceholder);
		} catch (DatumException e) {
			throw new IllegalStateException("This should never happen. Something is wrong with this code");
		}
	}

	public static <T> void saveFailingOnDatumErr(LinkedHashMap<String, String> headerMap, List<T> records, OutputStream outputStream, String sheetName)
			throws IOException, DatumException {
		save(headerMap, records, outputStream, sheetName, true, null);
	}

	private static <T> void save(LinkedHashMap<String, String> headerMap, List<T> records, OutputStream outputStream, String sheetName,
			boolean suppressDatumErr, String datumErrPlaceholder) throws IOException, DatumException {
		validateHeaderMap(headerMap);

		if (records == null || records.isEmpty()) {
			throw new IllegalArgumentException("the records can not be null or empty");
		}
		if (outputStream == null) {
			throw new IllegalArgumentException("the outputStream can not be null");
		}

		Workbook wb = new XSSFWorkbook();
		Sheet sheet = wb.createSheet(sheetName);

		createHeaders(headerMap, sheet);

		for (int recordIndex = 0; recordIndex < records.size(); recordIndex++) {
			T record = records.get(recordIndex);
			int rowIndex = recordIndex + 1;
			createRow(headerMap, record, recordIndex, sheet, rowIndex, suppressDatumErr, datumErrPlaceholder);
		}
		wb.write(outputStream);

	}

	static void validateHeaderMap(LinkedHashMap<String, String> headerMap) {
		if (headerMap == null || headerMap.isEmpty()) {
			throw new IllegalArgumentException("the headerMap cant not be null or empty");
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
			boolean suppressDatumErr, String errPlaceholder) throws DatumException {
		Row row = sheet.createRow(rowIndex);
		int columnIndex = 0;

		for (Map.Entry<String, String> entry : headerMap.entrySet()) {
			String propName = entry.getKey();
			Object propValue = null;
			try {
				propValue = PropertyUtils.getProperty(record, propName);
			} catch (Exception e) {
				if (!suppressDatumErr) {
					DatumException de = new DatumException(e);
					de.setPropName(propName);
					de.setRecordIndex(recordIndex);
					throw de;
				}
				propValue = errPlaceholder;
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
