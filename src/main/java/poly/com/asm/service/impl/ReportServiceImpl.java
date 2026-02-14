package poly.com.asm.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import poly.com.asm.dao.OrderDetailDAO;
import poly.com.asm.model.ReportItem;
import poly.com.asm.service.ReportService;

@Service
public class ReportServiceImpl implements ReportService {
	@Autowired
	OrderDetailDAO ddao;

	@Override
	public List<ReportItem> getRevenueByCategory() {
		return null;
	}
}