package poly.com.asm.service;

import java.util.List;
import poly.com.asm.entity.OrderDetail;

public interface OrderDetailService {
    List<OrderDetail> findByOrderId(Long orderId);
}