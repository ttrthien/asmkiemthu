package poly.com.asm.testlogin_authen;

import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.*;
import poly.com.asm.utils.ExcelUtils;

public class LoginTest {
    WebDriver driver;
    String excelPath = "src/test/resources/TestLoginData.xlsx";

    @BeforeMethod
    public void setup() {
        driver = new ChromeDriver();
        driver.get("http://localhost:8080/auth/login"); 
        driver.manage().window().maximize();
    }

    @DataProvider(name = "loginData")
    public Object[][] dataProvider() throws Exception {
        return ExcelUtils.getTableArray(excelPath, "Sheet1");
    }

    @Test(dataProvider = "loginData")
    public void testLogin(String user, String pass, String expected, Object rowIdx) throws Exception {
        String actualResult = "";
        String status = "FAIL";
        int rowNum = Integer.parseInt(rowIdx.toString());
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

        try {
            WebElement txtUser = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("username")));
            txtUser.clear();
            txtUser.sendKeys(user.trim());

            WebElement txtPass = driver.findElement(By.name("password"));
            txtPass.clear();
            String formattedPass = pass.endsWith(".0") ? pass.replace(".0", "") : pass;
            txtPass.sendKeys(formattedPass.trim());

            WebElement btnSubmit = driver.findElement(By.xpath("//button[contains(text(),'Đăng Nhập')]"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btnSubmit);

            try {
                wait.until(ExpectedConditions.not(ExpectedConditions.urlToBe("http://localhost:8080/auth/login")));
            } catch (Exception e) { }

            String currentUrl = driver.getCurrentUrl();
            actualResult = currentUrl;

            if (expected.equalsIgnoreCase("Admin")) {
                if (currentUrl.contains("/home/index")) {
                    driver.get("http://localhost:8080/admin/product/index");
                    Thread.sleep(1500);
                    if (driver.getCurrentUrl().contains("/admin/")) {
                        status = "PASS";
                        actualResult = "Admin Access Verified: " + driver.getCurrentUrl();
                    }
                }
            } 
            else if (expected.equalsIgnoreCase("User") && currentUrl.contains("/home/index")) {
                status = "PASS";
            } 
            else if (expected.equalsIgnoreCase("Fail") || expected.equalsIgnoreCase("Blocked")) {
                if (user.equals("ttrthien") && currentUrl.contains("/home/index")) {
                    driver.get("http://localhost:8080/admin/product/index");
                    Thread.sleep(1500);
                    if (driver.getCurrentUrl().contains("/auth/login") || !driver.getCurrentUrl().contains("/admin/")) {
                        status = "PASS";
                        actualResult = "Security Active: Redirected to " + driver.getCurrentUrl();
                    }
                } else if (currentUrl.contains("error") || currentUrl.contains("login")) {
                    status = "PASS";
                }
            }

        } catch (Exception e) {
            actualResult = "Lỗi: " + e.getMessage();
        } finally {
            ExcelUtils.setCellData(excelPath, actualResult, status, rowNum);
        }
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) driver.quit();
    }
}