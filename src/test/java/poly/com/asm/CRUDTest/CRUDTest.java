package poly.com.asm.CRUDTest;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.*;
import org.testng.annotations.*;

import poly.com.asm.utils.ExcelUtils;

public class CRUDTest {

    WebDriver driver;
    WebDriverWait wait;

    String filePath = "src/test/resources/CRUDUserData.xlsx";
    String sheetName = "TestCases";

    // ================= SETUP =================
    @BeforeClass
    public void setup() {

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");

        driver = new ChromeDriver(options);
        driver.manage().window().maximize();

        wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        // ===== LOGIN =====
        driver.get("http://localhost:8080/auth/login");

        driver.findElement(By.name("username")).sendKeys("admin");
        driver.findElement(By.name("password")).sendKeys("123");
        driver.findElement(By.xpath("//button[contains(.,'Đăng')]")).click();

        wait.until(ExpectedConditions.urlContains("/home"));
        System.out.println("✅ Login success");
    }

    // ================= DATA PROVIDER =================
    @DataProvider(name = "crudData")
    public Object[][] crudData() throws Exception {

        Object[][] data = ExcelUtils.getTableArray(filePath, sheetName);
        System.out.println("📊 DataProvider loaded rows = " + data.length);
        return data;
    }

    // ================= TEST =================
    @Test(dataProvider = "crudData")
    public void testCategory(Object testNameObj,
                             Object preconditionObj,
                             Object expectedResultObj,
                             Object rowNumObj) {

        String testName = safe(testNameObj);
        int rowNum = parseRow(rowNumObj);

        System.out.println("\n==============================");
        System.out.println("👉 Running: " + testName + " | Row: " + rowNum);
        System.out.println("==============================");

        String actualMsg = "";
        String status = "FAIL";

        try {
            // mở lại page
            driver.get("http://localhost:8080/admin/category/index");
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("id")));
            System.out.println("✅ Category page loaded");

            switch (testName.trim()) {

                // ===== LOAD PAGE =====
                case "Load Category Page":
                    actualMsg = driver.getTitle();
                    status = !actualMsg.isEmpty() ? "PASS" : "FAIL";
                    break;

                // ===== ADD SUCCESS =====
                case "Add New Category – Valid Data":

                    int before = countRows();
                    System.out.println("📊 Rows BEFORE = " + before);

                    addCategory("C999", "Giày test");

                    waitTableReload(before);

                    int after = countRows();
                    System.out.println("📊 Rows AFTER = " + after);

                    boolean found = driver.getPageSource().contains("C999");

                    actualMsg = "Rows before=" + before + " after=" + after;
                    status = (after > before && found) ? "PASS" : "FAIL";
                    break;

                // ===== EMPTY ID =====
                case "Add Category – Empty Category Code":
                    addCategory("", "Tên hợp lệ");

                    actualMsg = getPageText();
                    status = containsValidation(actualMsg) ? "PASS" : "FAIL";
                    break;

                // ===== EMPTY NAME =====
                case "Add Category – Empty Category Name":
                    addCategory("C9993", "");

                    actualMsg = getPageText();
                    status = containsValidation(actualMsg) ? "PASS" : "FAIL";
                    break;

                // ===== DUPLICATE =====
                case "Add Category – Duplicate Code":
                    addCategory("C001", "Trùng mã");

                    actualMsg = getPageText();
                    status = containsValidation(actualMsg) ? "PASS" : "FAIL";
                    break;

                // ===== RESET FORM =====
                case "Refresh Form":
                    driver.findElement(By.name("id")).sendKeys("TEST");

                    clickButtonSmart("Làm mới");

                    wait.until(ExpectedConditions.attributeToBe(
                            By.name("id"), "value", ""));

                    String value = driver.findElement(By.name("id")).getAttribute("value");
                    actualMsg = "Field value=" + value;
                    status = (value == null || value.isEmpty()) ? "PASS" : "FAIL";
                    break;

                default:
                    actualMsg = "NOT IMPLEMENTED: " + testName;
                    status = "SKIP";
            }

        } catch (Exception e) {
            actualMsg = "Exception: " + e.getMessage();
            status = "FAIL";
            e.printStackTrace();
        }

        try {
            ExcelUtils.setCellData(filePath, actualMsg, status, rowNum);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================= HELPERS =================

    private void addCategory(String ma, String ten) {

        WebElement txtMa = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.name("id")));
        WebElement txtTen = driver.findElement(By.name("name"));

        txtMa.clear();
        txtTen.clear();

        if (ma != null) txtMa.sendKeys(ma);
        if (ten != null) txtTen.sendKeys(ten);

        clickButtonSmart("Thêm");
    }
    // 🔥 wait table reload thật sự
    private void waitTableReload(int beforeCount) {
        wait.until(driver -> countRows() != beforeCount);
    }

    private int countRows() {
        List<WebElement> rows = driver.findElements(
                By.xpath("//table//tbody//tr"));
        return rows.size();
    }

    private boolean containsValidation(String text) {
        if (text == null) return false;
        text = text.toLowerCase();
        return text.contains("error")
                || text.contains("required")
                || text.contains("exist")
                || text.contains("trùng");
    }

    // 🔥 CLICK SMART – chống timeout
    private void clickButtonSmart(String text) {

        By locator = By.xpath(
                "//button[contains(.,'" + text + "')]" +
                        " | //a[contains(.,'" + text + "')]" +
                        " | //input[@value='" + text + "']");

        WebElement btn = wait.until(
                ExpectedConditions.presenceOfElementLocated(locator));

        wait.until(ExpectedConditions.elementToBeClickable(btn));

        ((JavascriptExecutor) driver)
                .executeScript("arguments[0].click();", btn);
    }

    private String getPageText() {
        return driver.findElement(By.tagName("body")).getText();
    }

    private String safe(Object o) {
        return o == null ? "" : o.toString().trim();
    }

    private int parseRow(Object o) {
        try {
            return (int) Double.parseDouble(o.toString());
        } catch (Exception e) {
            return 0;
        }
    }

    // ================= TEARDOWN =================
    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
            System.out.println("🧹 Driver closed");
        }
    }
}