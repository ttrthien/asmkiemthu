package poly.com.asm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes; // Import mới

import jakarta.servlet.http.HttpSession;
import poly.com.asm.entity.Account;
import poly.com.asm.service.AccountService;
import poly.com.asm.service.CookieService;

@Controller
public class AuthController {
	@Autowired
	AccountService accountService;
	@Autowired
	CookieService cookieService;
	@Autowired
	HttpSession session;

	@RequestMapping("/auth/login")
	public String login(Model model) {
		String username = cookieService.getValue("user");
		model.addAttribute("username", username);
		model.addAttribute("view", "account/login.html");
		return "layout/index";
	}

	@PostMapping("/auth/login")
	public String login(Model model, @RequestParam("username") String username,
			@RequestParam("password") String password,
			@RequestParam(value = "remember", defaultValue = "false") boolean remember, RedirectAttributes params) {
		try {
			Account user = accountService.findById(username);
			if (!user.getPassword().equals(password)) {
				model.addAttribute("message", "Sai mật khẩu!");
			} else {
				session.setAttribute("user", user);

				if (remember) {
					cookieService.add("user", username, 10 * 24);
				} else {
					cookieService.remove("user");
				}
				params.addFlashAttribute("message", "Đăng nhập thành công! Chào mừng " + user.getFullname());
				return "redirect:/home/index";
			}
		} catch (Exception e) {
			model.addAttribute("message", "Tài khoản không tồn tại!");
		}
		model.addAttribute("view", "account/login.html");
		return "layout/index";
	}

	@RequestMapping("/auth/logoff")
	public String logoff(RedirectAttributes params) {
		session.removeAttribute("user");
		params.addFlashAttribute("message", "Đã đăng xuất thành công!");
		return "redirect:/home/index";
	}
}