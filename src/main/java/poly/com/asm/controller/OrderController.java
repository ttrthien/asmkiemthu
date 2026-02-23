package poly.com.asm.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import poly.com.asm.config.Config;
import poly.com.asm.entity.*;
import poly.com.asm.model.CartItem;
import poly.com.asm.service.*;
import java.util.*;

@Controller
public class OrderController {
    @Autowired CartService cartService;
    @Autowired OrderService orderService;
    @Autowired ProductService productService;
    @Autowired VNPayService vnpayService;
    @Autowired HttpSession session;

    @GetMapping("/order/checkout")
    public String checkout(Model model) {
        if (cartService.getCount() == 0) return "redirect:/cart/view";
        Account user = (Account) session.getAttribute("user");
        if (user == null) return "redirect:/auth/login";
        
        model.addAttribute("user", user);
        model.addAttribute("cart", cartService);
        model.addAttribute("view", "order/check-out.html");
        return "layout/index";
    }

    @PostMapping("/order/confirm")
    public String confirm(HttpServletRequest request, 
                          @RequestParam("address") String address, 
                          @RequestParam("paymentMethod") String paymentMethod) {
        Account user = (Account) session.getAttribute("user");
        Order order = new Order();
        order.setAccount(user);
        order.setCreateDate(new Date());
        order.setAddress(address);
        order.setPaymentMethod(paymentMethod);
        order.setStatus(0);

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
            orderService.create(order);
            cartService.clear();
            return "redirect:/order/list";
        } else {
            Order savedOrder = orderService.create(order);
            
            String ipAddress = Config.getIpAddress(request);
            
            String orderInfo = "Thanh toan don hang " + savedOrder.getId();
            
            String vnpayUrl = vnpayService.createPaymentUrl((long) cartService.getAmount(), orderInfo, ipAddress);
            session.setAttribute("pendingOrderId", savedOrder.getId());
            
            return "redirect:" + vnpayUrl;
        }
    }

    @GetMapping("/order/vnpay-return")
    public String vnpayReturn(HttpServletRequest request) {
        String responseCode = request.getParameter("vnp_ResponseCode");
        Long orderId = (Long) session.getAttribute("pendingOrderId");
        
        if ("00".equals(responseCode) && orderId != null) {
            Order order = orderService.findById(orderId);
            order.setStatus(1); 
            orderService.update(order);
            cartService.clear();
            session.removeAttribute("pendingOrderId");
            return "redirect:/order/detail/" + orderId;
        }
        return "redirect:/cart/view";
    }

    @GetMapping("/order/list")
    public String list(Model model) {
        Account user = (Account) session.getAttribute("user");
        model.addAttribute("orders", orderService.findByAccount(user));
        model.addAttribute("view", "order/order-list.html");
        return "layout/index";
    }

    @GetMapping("/order/detail/{id}")
    public String detail(@PathVariable("id") Long id, Model model) {
        Order order = orderService.findById(id);
        model.addAttribute("order", order);
        model.addAttribute("view", "order/order-detail.html");
        return "layout/index";
    }
}