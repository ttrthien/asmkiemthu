package poly.com.asm.utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelUtils {
    private static Workbook workbook;
    private static Sheet sheet;

    public static Object[][] getTableArray(String filePath, String sheetName) throws Exception {
        FileInputStream excelFile = new FileInputStream(filePath);
        workbook = new XSSFWorkbook(excelFile);
        sheet = workbook.getSheet(sheetName);
        
        int totalRows = sheet.getPhysicalNumberOfRows();
        
        Object[][] tabArray = new Object[totalRows - 1][4]; 
        for (int i = 1; i < totalRows; i++) {
            Row row = sheet.getRow(i);
            tabArray[i-1][0] = (row.getCell(1) == null) ? "" : row.getCell(1).toString(); 
            tabArray[i-1][1] = (row.getCell(2) == null) ? "" : row.getCell(2).toString(); 
            tabArray[i-1][2] = (row.getCell(3) == null) ? "" : row.getCell(3).toString(); 
            tabArray[i-1][3] = i; 
        }
        return tabArray;
    }

    public static void setCellData(String filePath, String result, String status, int rowNum) throws Exception {
        FileInputStream fileIn = new FileInputStream(filePath);
        workbook = new XSSFWorkbook(fileIn);
        sheet = workbook.getSheetAt(0);
        Row row = sheet.getRow(rowNum);
        
        row.createCell(4).setCellValue(result); // Cột Actual Result
        row.createCell(5).setCellValue(status); // Cột Status
        
        FileOutputStream fileOut = new FileOutputStream(filePath);
        workbook.write(fileOut);
        fileOut.close();
        workbook.close();
    }
}