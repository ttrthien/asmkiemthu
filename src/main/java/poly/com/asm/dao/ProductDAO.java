package poly.com.asm.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import poly.com.asm.entity.Product;

public interface ProductDAO extends JpaRepository<Product, Integer> {
	@Query("SELECT p FROM Product p WHERE p.category.id=?1")
	Page<Product> findByCategoryId(String cid, Pageable pageable);

	@Query("SELECT p FROM Product p WHERE p.price < ?1")
	List<Product> TimspDuoiGia(Double price);

	Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);

	Page<Product> findByPriceBetween(Double min, Double max, Pageable pageable);

	@Query("SELECT p FROM Product p LEFT JOIN p.orderDetails od GROUP BY p ORDER BY COUNT(od) DESC")
	Page<Product> findTopSelling(Pageable pageable);
	
	@Query("SELECT p FROM Product p WHERE " +
		       "(:cid IS NULL OR :cid = '' OR p.category.id = :cid) AND " +
		       "(:kw IS NULL OR :kw = '' OR p.name LIKE %:kw%)")
		Page<Product> filterProducts(String cid, String kw, Pageable pageable);
}