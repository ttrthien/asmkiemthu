package poly.com.asm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import poly.com.asm.entity.Product;
import poly.com.asm.service.CategoryService;
import poly.com.asm.service.ProductService;
import poly.com.asm.service.ParamService;

@Controller
@RequestMapping("/admin/product")
public class ProductAController {
    @Autowired ProductService productService;
    @Autowired CategoryService categoryService;
    @Autowired ParamService paramService;

    @RequestMapping("/index")
    public String index(Model model) {
        if (!model.containsAttribute("item")) {
            model.addAttribute("item", new Product());
        }
        model.addAttribute("items", productService.findAll());
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("view", "admin/product.html");
        return "layout/index";
    }

    @PostMapping("/save")
    public String save(Product item, @RequestParam("photo_file") MultipartFile file, RedirectAttributes params) {
        String msg = (item.getId() != null) ? "Cập nhật sản phẩm thành công!" : "Thêm mới sản phẩm thành công!";
        
        if (!file.isEmpty()) {
            paramService.save(file, "/images/");
            item.setImage(file.getOriginalFilename());
        }

        if (item.getId() != null) {
            productService.update(item);
        } else {
            productService.create(item);
        }
        
        params.addFlashAttribute("message", msg);
        return "redirect:/admin/product/index";
    }

    @RequestMapping("/edit/{id}")
    public String edit(Model model, @PathVariable("id") Integer id) {
        model.addAttribute("item", productService.findById(id));
        model.addAttribute("items", productService.findAll());
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("view", "admin/product.html");
        return "layout/index";
    }

    @RequestMapping("/delete/{id}")
    public String delete(@PathVariable("id") Integer id, RedirectAttributes params) {
        productService.delete(id);
        params.addFlashAttribute("message", "Xóa sản phẩm thành công!");
        return "redirect:/admin/product/index";
    }
}