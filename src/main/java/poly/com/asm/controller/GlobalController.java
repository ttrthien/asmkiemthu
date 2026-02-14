package poly.com.asm.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import poly.com.asm.entity.Category;
import poly.com.asm.service.CartService;
import poly.com.asm.service.CategoryService; // Dùng Service thay vì DAO cho chuẩn

@ControllerAdvice
public class GlobalController {

	@Autowired
	CartService cart;

	@Autowired
	CategoryService categoryService;

	@ModelAttribute("cart")
	public CartService getCart() {
		return cart;
	}

	@ModelAttribute("cates")
	public List<Category> getCategories() {
		return categoryService.findAll();
	}
}