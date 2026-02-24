package poly.com.asm.service;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import poly.com.asm.entity.Product;

public interface ProductService {
    List<Product> findAll();
    Product findById(Integer id);
    List<Product> findByCategoryId(String cid);

    Page<Product> findAll(Pageable pageable);
    Page<Product> findByCategoryId(String cid, Pageable pageable);
    
    Page<Product> findByKeywords(String keywords, Pageable pageable);

    Page<Product> findByPriceRange(Double min, Double max, Pageable pageable);

    Page<Product> findTopSelling(Pageable pageable);
    
    Page<Product> filterProducts(String cid, String kw, Pageable pageable);

    List<Product> Layspduoi500(Double price);
    Product create(Product product);
    Product update(Product product);
    void delete(Integer id);
}