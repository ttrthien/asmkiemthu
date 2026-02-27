package poly.com.asm.testQLProduct;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.time.Duration; // Import thư viện thời gian

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import poly.com.asm.utils.ExcelUtils;

public class ProductTest {
    WebDriver driver;
    String filePath = "src/test/resources/TestQlProduct.xlsx";
    String sheetName = "Sheet1";

    @BeforeClass
    public void setup() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        driver = new ChromeDriver(options);
        driver.manage().window().maximize();

        // Đăng nhập 1 lần đầu tiên
        driver.get("http://localhost:8080/auth/login");
        driver.findElement(By.name("username")).sendKeys("admin");
        driver.findElement(By.name("password")).sendKeys("123");
        driver.findElement(By.xpath("//button[contains(text(),'Đăng Nhập')]")).click();

        try { Thread.sleep(2000); } catch (Exception e) {}
    }

    @DataProvider(name = "productData")
    public Object[][] dataProvider() throws Exception {
        return ExcelUtils.getTableArray(filePath, sheetName);
    }

    @Test(dataProvider = "productData")
    public void testAddProduct(String name, String price, String expectedMsg, int rowNum) {
        
        // --- 1. CHUẨN BỊ DỮ LIỆU ---
        String testName = name;
        if (name != null && !name.isEmpty()) {
            testName = "[TEST] " + name; // Gắn mác [TEST] để lát dễ dọn rác
        }

        // Xóa đuôi ".0" lỡ ExcelUtils có tự động thêm vào
        if (price != null && price.endsWith(".0")) {
            price = price.substring(0, price.length() - 2); 
        }

        System.out.println("Đang test dòng: " + rowNum + " | Tên: " + testName + " | Giá: " + price);

        // --- 2. THAO TÁC TRÊN WEB ---
        driver.get("http://localhost:8080/admin/product/index");
        try {
            WebElement txtName = driver.findElement(By.name("name"));
            txtName.clear();
            txtName.sendKeys(testName);
            
            WebElement txtPrice = driver.findElement(By.name("price"));
            txtPrice.clear();
            txtPrice.sendKeys(price);

            WebElement btnSave = driver.findElement(By.xpath("//button[contains(text(),'LƯU THAY ĐỔI')]"));
            JavascriptExecutor executor = (JavascriptExecutor) driver;
            executor.executeScript("arguments[0].click();", btnSave);

            // --- 3. ĐỢI VÀ LẤY THÔNG BÁO THÔNG MINH ---
            String actualMsg = "";
            try {
                // Nhờ Robot canh cái thông báo trong tối đa 5 giây
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
                WebElement alertBox = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("systemAlert")));
                actualMsg = alertBox.getText();
            } catch (Exception e) {
                // Nếu 5 giây mà web vẫn im lìm không báo gì
                actualMsg = "Web không hiển thị thông báo lỗi"; 
            }

            // --- 4. GHI KẾT QUẢ VÀO EXCEL ---
            String status = actualMsg.contains(expectedMsg) ? "PASS" : "FAIL";
            ExcelUtils.setCellData(filePath, actualMsg, status, rowNum);

        } catch (Exception e) {
            try {
                ExcelUtils.setCellData(filePath, "Lỗi Web: " + e.getMessage(), "FAIL", rowNum);
            } catch (Exception ex) {}
        }
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
        try {
            System.out.println("Đang kết nối SQL Server để dọn rác...");
            String dbUrl = "jdbc:sqlserver://localhost:1433;databaseName=ASM_BanGiay;encrypt=true;trustServerCertificate=true";
            Connection conn = DriverManager.getConnection(dbUrl, "asmjava5", "123");
            Statement stmt = conn.createStatement();
            int rowsDeleted = stmt.executeUpdate("DELETE FROM Products WHERE name LIKE '%[TEST]%'");
            System.out.println("Tuyệt vời! Đã dọn sạch " + rowsDeleted + " sản phẩm rác!");
            conn.close();
        } catch (Exception e) {
            System.out.println("Lỗi khi dọn rác: " + e.getMessage());
        }
    }
}