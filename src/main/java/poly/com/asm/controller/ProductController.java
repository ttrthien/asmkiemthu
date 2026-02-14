package poly.com.asm.controller;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import poly.com.asm.entity.Product;
import poly.com.asm.service.ProductService;

@Controller
public class ProductController {
    @Autowired
    ProductService productService;

    @RequestMapping("/product/list-by-category/{cid}")
    public String list(Model model, @PathVariable("cid") String cid, 
                       @RequestParam("p") Optional<Integer> p) {
        Pageable pageable = PageRequest.of(p.orElse(0), 20);
        Page<Product> page = productService.findByCategoryId(cid, pageable);
        
        model.addAttribute("page", page);
        model.addAttribute("view", "/product/product-list.html"); 
        return "layout/index";
    }

    @RequestMapping("/product/detail/{id}")
    public String detail(Model model, @PathVariable("id") Integer id) {
        Product item = productService.findById(id);
        model.addAttribute("item", item);
        model.addAttribute("view", "/product/product-detail.html");
        return "layout/index";
    }
    
    
}