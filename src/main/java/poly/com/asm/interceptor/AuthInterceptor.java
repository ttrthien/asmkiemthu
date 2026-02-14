package poly.com.asm.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import poly.com.asm.entity.Account;

@Component
public class AuthInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        Account user = (Account) session.getAttribute("user");
        String uri = request.getRequestURI();

        if (user == null) {
            session.setAttribute("back-url", uri);
            response.sendRedirect("/auth/login?error=Vui long dang nhap!");
            return false;
        }

        if (uri.startsWith("/admin/") && !user.getAdmin()) {
            response.sendRedirect("/auth/login?error=Access-Denied-Admin-Only");
            return false;
        }

        return true;
    }
}