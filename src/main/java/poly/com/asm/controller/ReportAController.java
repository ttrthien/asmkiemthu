package poly.com.asm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import poly.com.asm.dao.OrderDetailDAO; // Gọi trực tiếp DAO hoặc qua ReportService

@Controller
@RequestMapping("/admin/report")
public class ReportAController {
	@Autowired
	OrderDetailDAO dao;

	@RequestMapping("/revenue")
	public String revenue(Model model) {
		model.addAttribute("items", dao.getRevenueByCategory());
		model.addAttribute("view", "admin/report/revenue.html");
		return "layout/index";
	}

	@RequestMapping("/vip")
	public String vip(Model model) {
		model.addAttribute("items", dao.getTop10VIP(PageRequest.of(0, 10)));
		model.addAttribute("view", "admin/report/vip.html");
		return "layout/index";
	}
}