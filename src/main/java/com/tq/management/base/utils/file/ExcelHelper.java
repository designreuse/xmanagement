package com.tq.management.base.utils.file;

import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public abstract class ExcelHelper {

	// private static Logger logger =
	// LoggerFactory.getLogger(ExcelHelper.class);

	private static final String EXTENSION_XLS = "xls";

	private static final String EXTENSION_XLSX = "xlsx";

	/**
	 * 取单元格的值
	 * 
	 * @param cell
	 *            单元格对象
	 * @param treatAsStr
	 *            为true时，当做文本来取值 (取到的是文本，不会把“1”取成“1.0”)
	 * @return
	 */
	public static String getValue(Cell cell, boolean treatAsStr) {
		if (cell == null)
			return null;

		if (treatAsStr) {
			// 虽然excel中设置的都是文本，但是数字文本还被读错，如“1”取成“1.0”
			// 加上下面这句，临时把它当做文本来读取
			cell.setCellType(Cell.CELL_TYPE_STRING);
		}

		String value = null;
		switch (cell.getCellType()) {
		case Cell.CELL_TYPE_STRING:
			value = cell.getStringCellValue();
			break;
		case Cell.CELL_TYPE_NUMERIC:
			if (DateUtil.isCellDateFormatted(cell)) {
				value = cell.getDateCellValue().toString();
			} else {
				value = String.valueOf(cell.getNumericCellValue());
			}
			break;
		case Cell.CELL_TYPE_BOOLEAN:
			value = String.valueOf(cell.getBooleanCellValue());
			break;
		case Cell.CELL_TYPE_FORMULA:
			value = String.valueOf(cell.getCellFormula());
			break;
		default:
		}
		if (value != null)
			value = value.trim();
		return value;
	}

	public static Workbook getWorkbook(InputStream is, String filePath) throws IOException {
		Workbook wb = null;
		if (filePath.endsWith(EXTENSION_XLS)) {
			wb = new HSSFWorkbook(is);
		} else if (filePath.endsWith(EXTENSION_XLSX)) {
			wb = new XSSFWorkbook(is);
		}
		return wb;
	}

}
