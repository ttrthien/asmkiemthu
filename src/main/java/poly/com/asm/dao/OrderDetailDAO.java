package poly.com.asm.dao;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import poly.com.asm.entity.OrderDetail;
import poly.com.asm.model.ReportItem;
import java.util.List;

public interface OrderDetailDAO extends JpaRepository<OrderDetail, Long> {
	@Query("SELECT new ReportItem(d.product.category.name, SUM(d.price * d.quantity), SUM(d.quantity)) "
			+ "FROM OrderDetail d " + "GROUP BY d.product.category.name")
	List<ReportItem> getRevenueByCategory();

	@Query("SELECT new ReportItem(d.order.account.fullname, SUM(d.price * d.quantity), COUNT(DISTINCT d.order.id)) "
			+ "FROM OrderDetail d " + "GROUP BY d.order.account.fullname " + "ORDER BY SUM(d.price * d.quantity) DESC")
	List<ReportItem> getTop10VIP(Pageable pageable);
}