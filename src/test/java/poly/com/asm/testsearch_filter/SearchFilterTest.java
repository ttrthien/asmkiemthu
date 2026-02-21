package poly.com.asm.testsearch_filter;

import java.time.Duration;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.*;
import poly.com.asm.utils.ExcelUtilsSearch;

public class SearchFilterTest {
    WebDriver driver;
    String excelPath = "src/test/resources/TestSearchFilterData.xlsx"; 
    String sheetName = "test_case"; 

    @BeforeMethod
    public void setup() {
        driver = new ChromeDriver();
        driver.get("http://localhost:8080/home/index"); 
        driver.manage().window().maximize();
    }

    @DataProvider(name = "searchFilterData")
    public Object[][] dataProvider() throws Exception {
        return ExcelUtilsSearch.getSearchData(excelPath, sheetName);
    }

    @Test(dataProvider = "searchFilterData")
    public void testSearchAndFilter(String keyword, String category, String price, String expected, Object rowIdx) throws Exception {
        String actualResult = "";
        String status = "FAIL";
        int rowNum = Integer.parseInt(rowIdx.toString());
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        JavascriptExecutor js = (JavascriptExecutor) driver;

        try {
            // 1. Logic Tìm kiếm
            if (!keyword.isEmpty()) {
                WebElement txtSearch = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@placeholder='Tìm tên giày...']")));
                txtSearch.clear();
                txtSearch.sendKeys(keyword);
                
                WebElement btnLoc = driver.findElement(By.xpath("//button[contains(text(),'LỌC')]"));
                btnLoc.click();
                Thread.sleep(1000);
            }

            // 2. Logic Danh mục (Sửa lỗi Timeout)
            if (!category.isEmpty()) {
                // XPath tìm thẻ a chứa text danh mục (Adidas, Nike...) trong phần sidebar
                WebElement cateLink = wait.until(ExpectedConditions.presenceOfElementLocated(
                        By.xpath("//a[contains(text(),'" + category + "')] | //span[contains(text(),'" + category + "')]")));
                
                // Cuộn tới phần tử và Click bằng JS để tránh bị che khuất
                js.executeScript("arguments[0].scrollIntoView(true);", cateLink);
                Thread.sleep(500);
                js.executeScript("arguments[0].click();", cateLink);
            }

            // 3. Logic Dropdown Hãng
            if (!price.isEmpty()) {
                WebElement dropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//select")));
                dropdown.sendKeys(price); 
                Thread.sleep(1000);
            }

            // Chờ kết quả hiển thị
            Thread.sleep(1500);

            // 4. Verify kết quả: Đếm các khung card sản phẩm
            List<WebElement> products = driver.findElements(By.xpath("//div[contains(@class, 'card')] | //div[@class='product-item']")); 
            int count = products.size();

            if (expected.equalsIgnoreCase("Found") || expected.equalsIgnoreCase("Filtered")) {
                if (count > 0) {
                    status = "PASS";
                    actualResult = "Thành công: " + count + " SP";
                } else {
                    actualResult = "Lỗi: Không tìm thấy sản phẩm";
                }
            } else if (expected.equalsIgnoreCase("No Result")) {
                if (count == 0) {
                    status = "PASS";
                    actualResult = "Đúng: 0 kết quả";
                } else {
                    actualResult = "Lỗi: Vẫn hiển thị " + count + " SP";
                }
            } else { 
                status = "PASS";
                actualResult = "Hiển thị: " + count + " SP";
            }

        } catch (Exception e) {
            actualResult = "Lỗi hệ thống: " + e.getMessage();
        } finally {
            ExcelUtilsSearch.setSearchResults(excelPath, sheetName, actualResult, status, rowNum);
        }
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) driver.quit();
    }
}