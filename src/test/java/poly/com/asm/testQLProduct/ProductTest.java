package poly.com.asm.testQLProduct;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
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
		org.openqa.selenium.chrome.ChromeOptions options = new org.openqa.selenium.chrome.ChromeOptions();
		options.addArguments("--remote-allow-origins=*");
		driver = new ChromeDriver(options);
		driver.manage().window().maximize();

		driver.get("http://localhost:8080/auth/login");
		driver.findElement(By.name("username")).sendKeys("admin");
		driver.findElement(By.name("password")).sendKeys("123");
		driver.findElement(By.xpath("//button[contains(text(),'Đăng Nhập')]")).click();

		try {
			Thread.sleep(2000);
		} catch (Exception e) {
		}
	}

	@DataProvider(name = "productData")
	public Object[][] dataProvider() throws Exception {
		return ExcelUtils.getTableArray(filePath, sheetName);
	}

	@Test(dataProvider = "productData")
	public void testAddProduct(String name, String price, String expectedMsg, int rowNum) {

		String testName = name;
		if (!name.isEmpty()) {
			testName = "[TEST] " + name;
		}

		System.out.println("Đang test dòng số: " + rowNum + " | Sản phẩm: " + testName);

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

			Thread.sleep(1000);
			String actualMsg = "";
			try {
				actualMsg = driver.findElement(By.id("systemAlert")).getText();
			} catch (Exception e) {
				actualMsg = "Không tìm thấy thông báo";
			}

			String status = actualMsg.contains(expectedMsg) ? "PASS" : "FAIL";
			ExcelUtils.setCellData(filePath, actualMsg, status, rowNum);

		} catch (Exception e) {
			try {
				ExcelUtils.setCellData(filePath, "Lỗi Exception: " + e.getMessage(), "FAIL", rowNum);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	@AfterClass
	public void tearDown() {
		if (driver != null) {
			driver.quit();
		}

		try {
			System.out.println("Đang kết nối SQL Server để dọn rác của Bot...");
			String dbUrl = "jdbc:sqlserver://localhost:1433;databaseName=ASM_BanGiay;encrypt=true;trustServerCertificate=true";
			Connection conn = DriverManager.getConnection(dbUrl, "asmjava5", "123");
			Statement stmt = conn.createStatement();
			int rowsDeleted = stmt.executeUpdate("DELETE FROM Products WHERE name LIKE '[TEST] %'");
			System.out.println("Tuyệt vời! Đã dọn sạch " + rowsDeleted
					+ " sản phẩm rác do test tạo ra. Dữ liệu thật vẫn an toàn!");
			conn.close();
		} catch (Exception e) {
			System.out.println("Lỗi khi dọn rác: " + e.getMessage());
		}
	}
}