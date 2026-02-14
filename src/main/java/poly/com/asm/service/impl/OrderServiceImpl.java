package poly.com.asm.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import poly.com.asm.dao.OrderDAO;
import poly.com.asm.dao.OrderDetailDAO;
import poly.com.asm.entity.Account;
import poly.com.asm.entity.Order;
import poly.com.asm.entity.OrderDetail;
import poly.com.asm.service.OrderService;

@Service
public class OrderServiceImpl implements OrderService {
	@Autowired
	OrderDAO odao;

	@Autowired
	OrderDetailDAO ddao;

	@Override
	public List<Order> findAll() {
		return odao.findAll();
	}

	@Transactional
	@Override
	public Order create(Order order) {
		Order savedOrder = odao.save(order);

		if (order.getOrderDetails() != null && !order.getOrderDetails().isEmpty()) {
			for (OrderDetail detail : order.getOrderDetails()) {
				detail.setOrder(savedOrder);
				ddao.save(detail);
			}
		}
		return savedOrder;
	}

	@Override
	public Order findById(Long id) {
		return odao.findById(id).orElse(null);
	}

	@Override
	public List<Order> findByAccount(Account account) {
		return odao.findByAccount(account);
	}
}