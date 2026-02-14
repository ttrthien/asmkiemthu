package poly.com.asm.service;

import java.util.List;
import poly.com.asm.model.ReportItem; // Bạn cần tạo thêm Model này để chứa kết quả thống kê

public interface ReportService {
    List<ReportItem> getRevenueByCategory();
}