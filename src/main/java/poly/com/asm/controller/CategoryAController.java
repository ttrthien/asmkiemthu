package poly.com.asm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import poly.com.asm.entity.Category;
import poly.com.asm.service.CategoryService;

@Controller
@RequestMapping("/admin/category")
public class CategoryAController {

	@Autowired
	CategoryService categoryService;

	@RequestMapping("/index")
	public String index(Model model) {
		model.addAttribute("item", new Category());
		model.addAttribute("items", categoryService.findAll());
		model.addAttribute("view", "/admin/category.html");
		return "layout/index";
	}

	@RequestMapping("/edit/{id}")
	public String edit(@PathVariable("id") String id, Model model) {
		model.addAttribute("item", categoryService.findById(id));
		model.addAttribute("items", categoryService.findAll());
		model.addAttribute("view", "/admin/category.html");
		return "layout/index";
	}

	@PostMapping("/create")
	public String create(Category item, RedirectAttributes params) {
		if (categoryService.findById(item.getId()) != null) {
			params.addFlashAttribute("message", "Mã loại đã tồn tại!");
		} else {
			categoryService.create(item);
			params.addFlashAttribute("message", "Thêm mới thành công!");
		}
		return "redirect:/admin/category/index";
	}

	@PostMapping("/update")
	public String update(Category item, RedirectAttributes params) {
		if (categoryService.findById(item.getId()) == null) {
			params.addFlashAttribute("message", "Mã loại không tồn tại!");
		} else {
			categoryService.update(item);
			params.addFlashAttribute("message", "Cập nhật thành công!");
		}
		return "redirect:/admin/category/edit/" + item.getId();
	}

	@RequestMapping("/delete/{id}")
	public String delete(@PathVariable("id") String id, RedirectAttributes params) {
		try {
			categoryService.delete(id);
			params.addFlashAttribute("message", "Xóa thành công!");
		} catch (Exception e) {
			params.addFlashAttribute("message", "Không thể xóa loại đang có sản phẩm!");
		}
		return "redirect:/admin/category/index";
	}
}