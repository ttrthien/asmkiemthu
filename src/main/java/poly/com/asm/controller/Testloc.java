package poly.com.asm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import poly.com.asm.entity.Product;
import poly.com.asm.service.ProductService;
import java.util.List;

@Controller
public class Testloc {
	@Autowired
	ProductService productService;

	@GetMapping("/product/duoi500")
	public String testtimsp(Model model) {
		List<Product> list = productService.Layspduoi500(500.0);
		model.addAttribute("items", list);
		return "product/50sp";
	}
}