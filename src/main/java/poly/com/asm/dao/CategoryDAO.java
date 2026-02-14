package poly.com.asm.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import poly.com.asm.entity.Category;

public interface CategoryDAO extends JpaRepository<Category, String> {
}