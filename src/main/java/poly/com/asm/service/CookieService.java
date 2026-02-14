package poly.com.asm.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CookieService {
	@Autowired
	HttpServletRequest request;
	@Autowired
	HttpServletResponse response;

	public Cookie get(String name) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equalsIgnoreCase(name))
					return cookie;
			}
		}
		return null;
	}

	public String getValue(String name) {
		Cookie cookie = get(name);
		return (cookie != null) ? cookie.getValue() : "";
	}

	public void add(String name, String value, int days) {
		Cookie cookie = new Cookie(name, value);
		cookie.setMaxAge(days * 24 * 60 * 60);
		cookie.setPath("/");
		response.addCookie(cookie);
	}

	public void remove(String name) {
		add(name, "", 0);
	}
}