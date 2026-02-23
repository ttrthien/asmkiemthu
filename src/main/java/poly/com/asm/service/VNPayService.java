package poly.com.asm.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import poly.com.asm.config.VNPayConfig;

@Service
public class VNPayService {
	public String createPaymentUrl(long amount, String orderInfo) {
	    Map<String, String> vnp_Params = new HashMap<>();
	    vnp_Params.put("vnp_Version", "2.1.0");
	    vnp_Params.put("vnp_Command", "pay");
	    vnp_Params.put("vnp_TmnCode", VNPayConfig.vnp_TmnCode);
	    vnp_Params.put("vnp_Amount", String.valueOf(amount * 100));
	    vnp_Params.put("vnp_CurrCode", "VND");
	    vnp_Params.put("vnp_TxnRef", String.valueOf(System.currentTimeMillis()));
	    vnp_Params.put("vnp_OrderInfo", orderInfo);
	    vnp_Params.put("vnp_OrderType", "other");
	    vnp_Params.put("vnp_Locale", "vn");
	    vnp_Params.put("vnp_ReturnUrl", VNPayConfig.vnp_ReturnUrl);
	    vnp_Params.put("vnp_IpAddr", "127.0.0.1");
	    vnp_Params.put("vnp_CreateDate", new java.text.SimpleDateFormat("yyyyMMddHHmmss").format(new java.util.Date()));

	    // 1. Sắp xếp tham số (Cực kỳ quan trọng để sửa lỗi 72)
	    List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
	    Collections.sort(fieldNames);

	    StringBuilder hashData = new StringBuilder();
	    StringBuilder query = new StringBuilder();
	    for (String fieldName : fieldNames) {
	        String fieldValue = vnp_Params.get(fieldName);
	        if (fieldValue != null && fieldValue.length() > 0) {
	            // Build dữ liệu băm và query string
	            hashData.append(fieldName).append('=').append(java.net.URLEncoder.encode(fieldValue, java.nio.charset.StandardCharsets.US_ASCII)).append('&');
	            query.append(java.net.URLEncoder.encode(fieldName, java.nio.charset.StandardCharsets.US_ASCII)).append('=').append(java.net.URLEncoder.encode(fieldValue, java.nio.charset.StandardCharsets.US_ASCII)).append('&');
	        }
	    }
	    hashData.deleteCharAt(hashData.length() - 1);
	    query.deleteCharAt(query.length() - 1);

	    // 2. Tạo mã Hash và nối vào URL
	    String vnp_SecureHash = VNPayConfig.hmacSHA512(VNPayConfig.vnp_HashSecret, hashData.toString());
	    return VNPayConfig.vnp_PayUrl + "?" + query.toString() + "&vnp_SecureHash=" + vnp_SecureHash;
	}
}
