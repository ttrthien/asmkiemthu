package poly.com.asm.utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelUtilsSearch {
    private static Workbook workbook;
    private static Sheet sheet;

    public static Object[][] getSearchData(String filePath, String sheetName) throws Exception {
        FileInputStream excelFile = new FileInputStream(filePath);
        workbook = new XSSFWorkbook(excelFile);
        sheet = workbook.getSheet(sheetName);
        
        int totalRows = sheet.getLastRowNum(); // Sử dụng LastRowNum để chính xác hơn
        List<Object[]> dataList = new ArrayList<>();

        for (int i = 1; i <= totalRows; i++) {
            Row row = sheet.getRow(i);
            // Kiểm tra: Nếu dòng rỗng hoặc ô STT (cột 0) rỗng thì bỏ qua
            if (row == null || getCellValue(row.getCell(0)).isEmpty()) {
                continue;
            }
            
            Object[] rowData = new Object[5];
            rowData[0] = getCellValue(row.getCell(1)); // Keyword
            rowData[1] = getCellValue(row.getCell(2)); // Category
            rowData[2] = getCellValue(row.getCell(3)); // PriceRange
            rowData[3] = getCellValue(row.getCell(4)); // Expected
            rowData[4] = i; // Row Index để ghi kết quả
            dataList.add(rowData);
        }
        workbook.close();
        
        // Chuyển List sang Object[][] để DataProvider sử dụng
        return dataList.toArray(new Object[0][0]);
    }

    private static String getCellValue(Cell cell) {
        if (cell == null) return "";
        cell.setCellType(CellType.STRING); // Ép kiểu về String để tránh lỗi định dạng
        return cell.getStringCellValue().trim();
    }

    public static void setSearchResults(String filePath, String sheetName, String actual, String status, int rowNum) throws Exception {
        FileInputStream fileIn = new FileInputStream(filePath);
        workbook = new XSSFWorkbook(fileIn);
        sheet = workbook.getSheet(sheetName);
        
        Row row = sheet.getRow(rowNum);
        if (row == null) row = sheet.createRow(rowNum);
        
        row.createCell(6).setCellValue(actual); 
        row.createCell(7).setCellValue(status); 
        
        FileOutputStream fileOut = new FileOutputStream(filePath);
        workbook.write(fileOut);
        fileOut.close();
        workbook.close();
    }
}