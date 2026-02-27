package poly.com.asm.utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelUtilsSearch {
    private static Workbook workbook;
    private static Sheet sheet;
    
    public static Object[][] getTableArray(String filePath, String sheetName) throws Exception {
        FileInputStream excelFile = new FileInputStream(filePath);
        workbook = new XSSFWorkbook(excelFile);
        sheet = workbook.getSheet(sheetName);
        
        int totalRows = sheet.getPhysicalNumberOfRows();
        
        Object[][] tabArray = new Object[totalRows - 1][7];
        for (int i = 1; i < totalRows; i++) {
            Row row = sheet.getRow(i);
            tabArray[i-1][0] = getCellValue(row.getCell(1)); // Scenario
            tabArray[i-1][1] = getCellValue(row.getCell(2)); // Keywords
            tabArray[i-1][2] = getCellValue(row.getCell(3)); // Category
            tabArray[i-1][3] = getCellValue(row.getCell(4)); // Sort
            tabArray[i-1][4] = getCellValue(row.getCell(5)); // ExpectedMin
            tabArray[i-1][5] = getCellValue(row.getCell(6)); // ExpectedContains
            tabArray[i-1][6] = i; // rowIdx
        }
        return tabArray;
    }
    
    private static String getCellValue(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case NUMERIC:
                return String.valueOf((int) cell.getNumericCellValue());
            case STRING:
                return cell.getStringCellValue();
            default:
                return "";
        }
    }
    
    public static void setCellData(String filePath, String actualCount, String status, String detail, int rowNum) throws Exception {
        FileInputStream fileIn = new FileInputStream(filePath);
        workbook = new XSSFWorkbook(fileIn);
        sheet = workbook.getSheetAt(0);
        Row row = sheet.getRow(rowNum);
        
        row.createCell(7).setCellValue(actualCount); // ActualCount
        row.createCell(8).setCellValue(status); // Status
        row.createCell(9).setCellValue(detail); // Detail
        
        FileOutputStream fileOut = new FileOutputStream(filePath);
        workbook.write(fileOut);
        fileOut.close();
        workbook.close();
    }
}