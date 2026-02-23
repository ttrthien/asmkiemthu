package poly.com.asm.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import poly.com.asm.entity.Account;
import poly.com.asm.entity.Order;
import poly.com.asm.entity.OrderDetail;
import poly.com.asm.model.CartItem;
import poly.com.asm.service.CartService;
import poly.com.asm.service.OrderService;
import poly.com.asm.service.ProductService;
import poly.com.asm.service.VNPayService; // Đảm bảo đã tạo file này

@Controller
public class OrderController {
    @Autowired CartService cartService;
    @Autowired OrderService orderService;
    @Autowired ProductService productService;
    @Autowired HttpSession session;
    @Autowired VNPayService vnpayService; // Inject Service thanh toán thật

    @RequestMapping("/order/checkout")
    public String checkout(Model model) {
        if (cartService.getCount() == 0) return "redirect:/cart/view";
        Account user = (Account) session.getAttribute("user");
        model.addAttribute("user", user);
        model.addAttribute("cart", cartService);
        model.addAttribute("view", "order/check-out.html");
        return "layout/index";
    }

    @PostMapping("/order/confirm")
    public String confirm(@RequestParam("address") String address, 
                          @RequestParam("paymentMethod") String paymentMethod) {
        Account user = (Account) session.getAttribute("user");
        
        // 1. Tạo đối tượng Order và gán thông tin
        Order order = new Order();
        order.setAccount(user);
        order.setCreateDate(new Date());
        order.setAddress(address);
        order.setPaymentMethod(paymentMethod);
        order.setStatus(0); // Trạng thái: Mới

        // 2. Chuyển dữ liệu từ giỏ hàng sang danh sách chi tiết đơn hàng
        List<OrderDetail> details = new ArrayList<>();
        for (CartItem item : cartService.getItems()) {
            OrderDetail detail = new OrderDetail();
            detail.setOrder(order);
            detail.setProduct(productService.findById(item.getId()));
            detail.setPrice(item.getPrice());
            detail.setQuantity(item.getQty());
            details.add(detail);
        }
        order.setOrderDetails(details);

        if ("COD".equals(paymentMethod)) {
            // Thanh toán tiền mặt: Lưu luôn vào Database
            orderService.create(order); 
            cartService.clear();
            return "redirect:/order/list";
        } else {
            // THANH TOÁN THẬT: Lưu đơn hàng vào session và chuyển sang VNPay
            session.setAttribute("pendingOrder", order);
            long amount = (long) cartService.getAmount();
            String vnpayUrl = vnpayService.createPaymentUrl(amount, "Thanh toan don hang CAMEDO");
            return "redirect:" + vnpayUrl; // Chuyển sang trang mã QR thật của VNPay
        }
    }

    // Hàm nhận kết quả thật từ VNPay trả về
    @RequestMapping("/order/vnpay-return")
    public String vnpayReturn(HttpServletRequest request, Model model) {
        String responseCode = request.getParameter("vnp_ResponseCode");
        
        // "00" là mã thanh toán thành công từ VNPay
        if ("00".equals(responseCode)) {
            Order order = (Order) session.getAttribute("pendingOrder");
            if (order != null) {
                order.setStatus(1); // Trạng thái: Đã xác nhận/Đã trả tiền
                orderService.create(order);
                cartService.clear();
                session.removeAttribute("pendingOrder");
                model.addAttribute("message", "Thanh toán Online thành công!");
            }
            return "redirect:/order/list";
        } else {
            // Nếu khách hủy hoặc lỗi, quay về giỏ hàng
            return "redirect:/cart/view";
        }
    }

    @RequestMapping("/order/list")
    public String list(Model model) {
        Account user = (Account) session.getAttribute("user");
        model.addAttribute("orders", orderService.findByAccount(user));
        model.addAttribute("view", "order/order-list.html");
        return "layout/index";
    }

    @RequestMapping("/order/detail/{id}")
    public String detail(@PathVariable("id") Long id, Model model) {
        Order order = orderService.findById(id);
        model.addAttribute("order", order);
        model.addAttribute("view", "order/order-detail.html");
        return "layout/index";
    }
}