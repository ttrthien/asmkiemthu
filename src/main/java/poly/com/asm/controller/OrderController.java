package poly.com.asm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import poly.com.asm.entity.Account;
import poly.com.asm.entity.Order;
import poly.com.asm.entity.OrderDetail;
import poly.com.asm.entity.Product;
import poly.com.asm.model.CartItem;
import poly.com.asm.service.CartService;
import poly.com.asm.service.OrderService;
import poly.com.asm.service.ProductService;
import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class OrderController {
	@Autowired
	CartService cartService;
	@Autowired
	OrderService orderService;
	@Autowired
	ProductService productService;
	@Autowired
	HttpSession session;

	@RequestMapping("/order/checkout")
	public String checkout(Model model) {
		if (cartService.getCount() == 0) {
			return "redirect:/cart/view";
		}
		Account user = (Account) session.getAttribute("user");
		model.addAttribute("user", user);
		model.addAttribute("cart", cartService);
		model.addAttribute("view", "order/check-out.html");
		return "layout/index";
	}

	@RequestMapping("/order/confirm")
	public String confirm(@RequestParam("address") String address) {
		Account user = (Account) session.getAttribute("user");
		Order order = new Order();
		order.setAccount(user);
		order.setCreateDate(new Date());
		order.setAddress(address);
		List<OrderDetail> details = new ArrayList<>();
		for (CartItem item : cartService.getItems()) {
			OrderDetail detail = new OrderDetail();
			detail.setOrder(order);
			Product product = productService.findById(item.getId());
			detail.setProduct(product);
			detail.setPrice(item.getPrice());
			detail.setQuantity(item.getQty());
			details.add(detail);
		}
		order.setOrderDetails(details);
		orderService.create(order);
		cartService.clear();
		return "redirect:/order/list";
	}

	@RequestMapping("/order/list")
	public String list(Model model) {
		Account user = (Account) session.getAttribute("user");
		model.addAttribute("orders", orderService.findByAccount(user));
		model.addAttribute("view", "order/order-list.html");
		return "layout/index";
	}

	@RequestMapping("/order/detail/{id}")
	public String detail(@PathVariable("id") Long id, Model model) {
		Order order = orderService.findById(id);
		model.addAttribute("order", order);
		model.addAttribute("view", "order/order-detail.html");
		return "layout/index";
	}
}