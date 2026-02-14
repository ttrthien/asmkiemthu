package poly.com.asm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import poly.com.asm.service.ProductService;
import poly.com.asm.service.OrderService;
import poly.com.asm.service.AccountService;

@Controller
@RequestMapping("/admin")
public class AdminController {

	@Autowired
	ProductService productService;
	@Autowired
	OrderService orderService;
	@Autowired
	AccountService accountService;

	@RequestMapping("/home/index")
	public String index(Model model) {
		model.addAttribute("totalProducts", productService.findAll().size());
		model.addAttribute("totalOrders", orderService.findAll().size());
		model.addAttribute("totalUsers", accountService.findAll().size());
		model.addAttribute("view", "admin/home/index.html");
		return "layout/index";
	}
}