package poly.com.asm.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import poly.com.asm.dao.ProductDAO;
import poly.com.asm.entity.Product;
import poly.com.asm.service.ProductService;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    ProductDAO pdao;

    @Override
    public List<Product> findAll() {
        return pdao.findAll();
    }

    @Override
    public Product findById(Integer id) {
        return pdao.findById(id).orElse(null); 
    }

    @Override
    public List<Product> findByCategoryId(String cid) {
        return pdao.findByCategoryId(cid, Pageable.unpaged()).getContent(); 
    }

    @Override
    public Page<Product> findAll(Pageable pageable) {
        return pdao.findAll(pageable);
    }

    @Override
    public Page<Product> findByCategoryId(String cid, Pageable pageable) {
        return pdao.findByCategoryId(cid, pageable);
    }
    
    @Override
    public List<Product> Layspduoi500(Double price) {
        return pdao.TimspDuoiGia(price);
    }


    @Override
    public Product create(Product product) {
        return pdao.save(product); 
    }

    @Override
    public Product update(Product product) {
        return pdao.save(product); 
    }

    @Override
    public void delete(Integer id) {
        pdao.deleteById(id);
    }
    @Override
    public Page<Product> findByKeywords(String keywords, Pageable pageable) {
        return pdao.findByNameContainingIgnoreCase(keywords, pageable);
    }

    @Override
    public Page<Product> findTopSelling(Pageable pageable) {
 
        return pdao.findTopSelling(pageable);
    }
    
    @Override
    public Page<Product> findByPriceRange(Double min, Double max, Pageable pageable) {
        return pdao.findByPriceBetween(min, max, pageable);
    }
    
    @Override
    public Page<Product> filterProducts(String cid, String kw, Pageable pageable) {
        return pdao.filterProducts(cid, kw, pageable);
    }
}