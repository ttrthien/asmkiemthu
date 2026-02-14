package poly.com.asm.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import poly.com.asm.dao.OrderDetailDAO;
import poly.com.asm.entity.OrderDetail;
import poly.com.asm.service.OrderDetailService;

@Service
public class OrderDetailServiceImpl implements OrderDetailService {
	@Autowired
	OrderDetailDAO ddao;

	@Override
	public List<OrderDetail> findByOrderId(Long orderId) {
		return ddao.findAll();
	}
}