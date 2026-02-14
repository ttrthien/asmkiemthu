package poly.com.asm.service;

import java.util.List;

import poly.com.asm.entity.Account;
import poly.com.asm.entity.Order;

public interface OrderService {
    Order create(Order order);
    Order findById(Long id);
    List<Order> findAll();
    List<Order> findByAccount(Account account);
}