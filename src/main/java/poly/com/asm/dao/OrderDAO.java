package poly.com.asm.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import poly.com.asm.entity.Account;
import poly.com.asm.entity.Order;

public interface OrderDAO extends JpaRepository<Order, Long> {
	List<Order> findByAccount(Account account);
}