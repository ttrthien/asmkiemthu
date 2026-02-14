package poly.com.asm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import poly.com.asm.entity.Order;
import poly.com.asm.service.OrderService;

@Controller
@RequestMapping("/admin/order")
public class OrderAController {

	@Autowired
	OrderService orderService;

	@RequestMapping("/index")
	public String index(Model model) {
		model.addAttribute("orders", orderService.findAll());
		model.addAttribute("view", "admin/order.html");
		return "layout/index";
	}

	@RequestMapping("/detail/{id}")
	public String detail(@PathVariable("id") Long id, Model model) {
		model.addAttribute("order", orderService.findById(id));
		model.addAttribute("view", "admin/order-detail.html");
		return "layout/index";
	}

	@PostMapping("/update-status")
	public String updateStatus(@RequestParam("id") Long id, @RequestParam("status") Integer status,
			RedirectAttributes params) {
		try {
			Order order = orderService.findById(id);
			if (order != null) {
				order.setStatus(status);
				orderService.create(order);
				String sttText = switch (status) {
				case 0 -> "Mới";
				case 1 -> "Đã xác nhận";
				case 2 -> "Đang giao";
				case 3 -> "Hoàn tất";
				default -> "Khác";
				};
				params.addFlashAttribute("message", "Đơn hàng #" + id + " -> " + sttText);
			}
		} catch (Exception e) {
			params.addFlashAttribute("message", "Lỗi cập nhật đơn hàng!");
		}
		return "redirect:/admin/order/index";
	}
}