package nyoibo.inkstone.upload.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import nyoibo.inkstone.upload.gui.InkstoneUploadConsole;
import nyoibo.inkstone.upload.web.action.InkstoneUploadMainService;

public class CompareExcelReaderUtils {
	public static final String OFFICE_EXCEL_2003_POSTFIX = "xls";
	public static final String OFFICE_EXCEL_2010_POSTFIX = "xlsx";

	public static final String EMPTY = "";
	public static final String POINT = ".";
	public static final String LIB_PATH = "lib";
	public static final String NOT_EXCEL_FILE = " : Not the Excel file!";
	public static final String PROCESSING = "Processing...";

	private static String getSuffix(File file) {
		String fileName = file.getName();
		String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
		return suffix;
	}

	public static Map<String, String> readExcel(File file) throws Exception {
		if (file.isDirectory() || file == null) {
			throw new Exception("should be a file or not be empty");
		} else {
			String postfix = getSuffix(file);
			if (!EMPTY.equals(postfix)) {
				if (OFFICE_EXCEL_2003_POSTFIX.equals(postfix)) {
					return readXls(file);
				} else if (OFFICE_EXCEL_2010_POSTFIX.equals(postfix)) {
					return readXlsx(file);
				}
			} else {
				throw new Exception(NOT_EXCEL_FILE);
			}
		}
		return null;
	}

	private static String getValue(XSSFCell xssfRow) {
		if (null == xssfRow)
			return null;
		if (xssfRow.getCellType() == xssfRow.CELL_TYPE_BOOLEAN) {
			return String.valueOf(xssfRow.getBooleanCellValue());
		} else if (xssfRow.getCellType() == xssfRow.CELL_TYPE_NUMERIC) {
			return String.valueOf(xssfRow.getNumericCellValue());
		} else {
			return String.valueOf(xssfRow.getStringCellValue());
		}
	}

	private static String getValue(HSSFCell hssfCell) {
		if (null == hssfCell)
			return null;
		if (hssfCell.getCellType() == hssfCell.CELL_TYPE_BOOLEAN) {
			return String.valueOf(hssfCell.getBooleanCellValue());
		} else if (hssfCell.getCellType() == hssfCell.CELL_TYPE_NUMERIC) {
			return String.valueOf(hssfCell.getNumericCellValue());
		} else {
			return String.valueOf(hssfCell.getStringCellValue());
		}
	}

	private static Map<String, String> readXlsx(File file) throws Exception {
		Map<String, String> container = new HashMap<String, String>();
		InkstoneUploadMainService.currentChapterName = "ReadCompareList";
		InkstoneUploadMainService.fileTotal = 0;
		InkstoneUploadMainService.initFileCount = 0;

		InputStream is = new FileInputStream(file);
		@SuppressWarnings("resource")
		XSSFWorkbook xssfWorkbook = new XSSFWorkbook(is);
		int total = xssfWorkbook.getSheetAt(0).getPhysicalNumberOfRows();
		int inited = 0;

		for (int numSheet = 0; numSheet < xssfWorkbook.getNumberOfSheets(); numSheet++) {
			XSSFSheet xssfSheet = xssfWorkbook.getSheetAt(numSheet);
			if (xssfSheet == null) {
				continue;
			}
			for (int rowNum = 0; rowNum <= xssfSheet.getLastRowNum(); rowNum++) {
				XSSFRow xssfRow = xssfSheet.getRow(rowNum);
				if (xssfRow != null) {
					XSSFCell key = xssfRow.getCell(0);
					XSSFCell value = xssfRow.getCell(1);
					container.put(InkstoneRawHeaderUtils.convertRawCNHeader(getValue(key)),
							InkstoneRawHeaderUtils.convertRawENeader(getValue(value)));
				}
			}

			inited++;
			int status = (100 * inited) / total;
			InkstoneUploadMainService.process.put(InkstoneUploadMainService.currentChapterName, status);
		}
		return container;
	}

	private static Map<String, String> readXls(File file) throws Exception {
		InkstoneUploadMainService.currentChapterName = "ReadCompareList";
		Map<String, String> container = new HashMap<String, String>();
		InkstoneUploadMainService.fileTotal = 0;
		InkstoneUploadMainService.initFileCount = 0;

		InputStream is = new FileInputStream(file);
		@SuppressWarnings("resource")
		HSSFWorkbook hssfWorkbook = new HSSFWorkbook(is);
		int total = hssfWorkbook.getSheetAt(0).getPhysicalNumberOfRows();
		int inited = 0;

		for (int numSheet = 0; numSheet < hssfWorkbook.getNumberOfSheets(); numSheet++) {
			HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(numSheet);
			if (hssfSheet == null) {
				continue;
			}

			for (int rowNum = 1; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
				HSSFRow hssfRow = hssfSheet.getRow(rowNum);
				if (hssfRow != null) {
					HSSFCell key = hssfRow.getCell(0);
					HSSFCell value = hssfRow.getCell(1);
					String keyStr = getValue(key);
					String valueStr = getValue(value);
					if (InkstoneUploadConsole.skipReadingExcel) {
						container.put(keyStr, valueStr);
					} else {
						container.put(InkstoneRawHeaderUtils.convertRawCNHeader(getValue(key)),
								InkstoneRawHeaderUtils.convertRawENeader(getValue(value)));
					}
				}
				inited++;
				int status = (100 * inited) / total;
				InkstoneUploadMainService.process.put(InkstoneUploadMainService.currentChapterName, status);
			}
		}
		return container;
	}

	/*
	 * public static void main(String[] args) throws Exception {
	 * System.out.println(InkstoneRawHeaderUtils.convertRawCNHeader("1497(1)"));
	 * }
	 */
}
