package poly.com.asm.testsearch_filter;

import java.io.File;
import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.io.FileHandler;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.*;
import poly.com.asm.utils.ExcelUtilsSearch;
import java.util.List;

public class SearchFilterTest {
    WebDriver driver;
    String excelPath = "src/test/resources/TestSearchData.xlsx";
    WebDriverWait wait;

    @BeforeMethod
    public void setup() {
        driver = new ChromeDriver();
        driver.get("http://localhost:8080/home/index");
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    @DataProvider(name = "searchData")
    public Object[][] dataProvider() throws Exception {
        return ExcelUtilsSearch.getTableArray(excelPath, "Sheet1");
    }

    @Test(dataProvider = "searchData")
    public void testSearch(String scenario, String keywords, String category, String sort, String expectedMin, String expectedContains, Object rowIdx) throws Exception {
        String actualCount = "0";
        String status = "FAIL";
        String detail = "";
        int rowNum = Integer.parseInt(rowIdx.toString());
        long startTime = System.currentTimeMillis();

        try {
            if (!keywords.trim().isEmpty()) {
                WebElement inputKeywords = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("keywords")));
                inputKeywords.clear();
                inputKeywords.sendKeys(keywords.trim());
            }

            if (!category.trim().isEmpty()) {
                WebElement selectCategory = wait.until(ExpectedConditions.elementToBeClickable(By.name("cid")));
                Select selCat = new Select(selectCategory);
                selCat.selectByValue(category.trim());
            }

            if (!sort.trim().isEmpty()) {
                try {
                    WebElement selectSort = wait.until(ExpectedConditions.elementToBeClickable(By.name("sort")));
                    Select selSort = new Select(selectSort);
                    selSort.selectByValue(sort.trim());
                } catch (Exception e) {
                    detail += "Không tìm thấy select sort hoặc value không hợp lệ: " + sort + ". ";
                }
            }

            WebElement btnSubmit = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[@type='submit'] | //button[contains(text(),'Tìm')] | //button[contains(@class,'search')]")
            ));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btnSubmit);

            int expMin = Integer.parseInt(expectedMin);

            if (expMin > 0) {
                By productLocator = By.cssSelector(".product-item, .card, .product-card, .item, .col-md-3, .col-3, .shoe-item"); // Linh hoạt
                wait.until(ExpectedConditions.presenceOfElementLocated(productLocator));

                List<WebElement> products = driver.findElements(productLocator);
                int count = products.size();
                actualCount = String.valueOf(count);

                boolean containsMatch = true;
                if (!expectedContains.trim().isEmpty()) {
                    containsMatch = products.stream()
                        .anyMatch(p -> p.getText().toLowerCase().contains(expectedContains.toLowerCase()));
                }

                if (count >= expMin && containsMatch) {
                    status = "PASS";
                    detail = "Tìm thấy " + count + " sản phẩm phù hợp";
                } else {
                    detail = "Số lượng hoặc nội dung không khớp: " + count + " < " + expMin + " hoặc thiếu '" + expectedContains + "'";
                }
            } else {
                try {
                    WebElement noResult = wait.until(ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//*[contains(text(),'Không tìm thấy') or contains(text(),'No products') or contains(@class,'no-result')]")
                    ));
                    if (noResult.isDisplayed()) {
                        actualCount = "0";
                        status = "PASS";
                        detail = "Không tìm thấy sản phẩm (như mong đợi)";
                    }
                } catch (Exception e) {
                    detail = "Không hiển thị thông báo không tìm thấy khi expected 0";
                }
            }

        } catch (Exception e) {
            actualCount = "0";
            detail = "LỖI: " + e.getClass().getSimpleName() + " - " + e.getMessage() + " | URL: " + driver.getCurrentUrl();
            try {
                File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
                FileHandler.copy(srcFile, new File("screenshots/fail_" + rowNum + "_" + System.currentTimeMillis() + ".png"));
                detail += " | Đã chụp ảnh lỗi";
            } catch (Exception ignored) {}
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            detail += " | Thời gian: " + duration + "ms";
            ExcelUtilsSearch.setCellData(excelPath, actualCount, status, detail, rowNum);
        }
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}