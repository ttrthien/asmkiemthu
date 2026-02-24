package poly.com.asm.controller;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import poly.com.asm.entity.Product;
import poly.com.asm.service.CategoryService;
import poly.com.asm.service.ProductService;

@Controller
public class HomeController {
    @Autowired
    ProductService productService;

    @Autowired
    CategoryService categoryService;

    @RequestMapping({ "/", "/home/index" })
    public String index(Model model, 
            @RequestParam("keywords") Optional<String> kw,
            @RequestParam("cid") Optional<String> cid, 
            @RequestParam("sort") Optional<String> sortType,
            @RequestParam("p") Optional<Integer> p) {

        String sType = sortType.orElse("");
        String keyword = kw.orElse("");
        String categoryId = cid.orElse("");
        
        Page<Product> page;

        if (sType.equals("top-selling")) {
            Pageable pageable = PageRequest.of(p.orElse(0), 8);
            page = productService.findTopSelling(pageable);
        } else {
            String sortField = "price";
            Direction direction = Direction.ASC;
            
            if (sType.equals("price-desc")) {
                direction = Direction.DESC;
            }
            
            Pageable pageable = PageRequest.of(p.orElse(0), 8, Sort.by(direction, sortField));
            
            page = productService.filterProducts(categoryId, keyword, pageable);
        }

        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("page", page);
        model.addAttribute("view", "product/product-list.html");
        
        return "layout/index";
    }
}