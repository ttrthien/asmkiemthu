package poly.com.asm.testsearch_filter;

import java.time.Duration;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select; // Khai báo thêm Select
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

    // Biến price trong file Excel của bạn có thể chứa giá trị sắp xếp (VD: "Giá tăng dần", "Giá giảm dần")
    @Test(dataProvider = "searchFilterData")
    public void testSearchAndFilter(String keyword, String category, String price, String expected, Object rowIdx) throws Exception {
        String actualResult = "";
        String status = "FAIL";
        int rowNum = Integer.parseInt(rowIdx.toString());
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        JavascriptExecutor js = (JavascriptExecutor) driver;

        try {
            // Đợi trang load xong form
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//form[contains(@action, '/home/index')]")));

            // ==========================================
            // BƯỚC 1: ĐIỀN ĐIỀU KIỆN LỌC (Chưa bấm nút)
            // ==========================================
            
            // 1.1 Nhập Keyword
            if (keyword != null && !keyword.isEmpty()) {
                WebElement txtSearch = driver.findElement(By.name("keywords"));
                txtSearch.clear();
                txtSearch.sendKeys(keyword);
            }

            // 1.2 Chọn Dropdown Hãng (name = "cid")
            if (category != null && !category.isEmpty()) {
                WebElement cateElement = driver.findElement(By.name("cid"));
                Select cateSelect = new Select(cateElement);
                // Chọn theo Text hiển thị (VD: "Nike", "Adidas")
                cateSelect.selectByVisibleText(category); 
            }

            // 1.3 Chọn Dropdown Sắp xếp/Giá (name = "sort") 
            // Ở giao diện ta dùng th:name="sort"
            if (price != null && !price.isEmpty()) {
                WebElement sortElement = driver.findElement(By.name("sort"));
                Select sortSelect = new Select(sortElement);
                // Chọn theo Text hiển thị (VD: "Giá tăng dần", "Bán chạy nhất")
                sortSelect.selectByVisibleText(price); 
            }

            // ==========================================
            // BƯỚC 2: BẤM NÚT LỌC VÀ CHỜ KẾT QUẢ
            // ==========================================
            WebElement btnLoc = driver.findElement(By.xpath("//button[contains(text(),'LỌC')]"));
            btnLoc.click();
            
            // Chờ trang load lại sau khi lọc (Chờ thanh phân trang hoặc danh sách sp load xong)
            Thread.sleep(1500); 

            // ==========================================
            // BƯỚC 3: KIỂM TRA KẾT QUẢ (VERIFY)
            // ==========================================
            // CHÚ Ý: Tìm class "product-card" thay vì "card" để không bị đếm nhầm form HTML
            List<WebElement> products = driver.findElements(By.xpath("//div[contains(@class, 'product-card')]")); 
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