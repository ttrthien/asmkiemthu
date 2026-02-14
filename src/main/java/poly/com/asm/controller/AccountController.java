package poly.com.asm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import poly.com.asm.entity.Account;
import poly.com.asm.service.AccountService;

@Controller
public class AccountController {

	@Autowired
	AccountService accountService;

	@Autowired
	HttpSession session;

	@GetMapping("/account/sign-up")
	public String signUp(Model model) {
		model.addAttribute("item", new Account());
		model.addAttribute("view", "account/sign-up.html");
		return "layout/index";
	}

	@PostMapping("/account/sign-up")
	public String signUp(Model model, @Valid @ModelAttribute("item") Account account, BindingResult result,
			RedirectAttributes params) {
		if (result.hasErrors()) {
			model.addAttribute("view", "account/sign-up.html");
			return "layout/index";
		}
		if (accountService.findById(account.getUsername()) != null) {
			model.addAttribute("message", "Tên đăng nhập đã tồn tại!");
			model.addAttribute("view", "account/sign-up.html");
			return "layout/index";
		}
		try {
			account.setActivated(true);
			account.setAdmin(false);
			account.setPhoto("user.png");
			accountService.create(account);
			params.addFlashAttribute("message", "Đăng ký thành công!");
			return "redirect:/auth/login";
		} catch (Exception e) {
			model.addAttribute("message", "Lỗi: " + e.getMessage());
			model.addAttribute("view", "account/sign-up.html");
			return "layout/index";
		}
	}

	@RequestMapping("/account/edit-profile")
	public String editProfile(Model model) {
		Account sessionUser = (Account) session.getAttribute("user");
		if (sessionUser == null)
			return "redirect:/auth/login";

		Account user = accountService.findById(sessionUser.getUsername());
		model.addAttribute("user", user);
		model.addAttribute("view", "account/edit-profile.html");
		return "layout/index";
	}

	@PostMapping("/account/edit-profile")
	public String updateProfile(Model model, @Valid @ModelAttribute("user") Account formUser, BindingResult result,
			RedirectAttributes params) {
		Account sessionUser = (Account) session.getAttribute("user");
		if (sessionUser == null)
			return "redirect:/auth/login";

		if (result.hasErrors()) {
			model.addAttribute("view", "account/edit-profile.html");
			return "layout/index";
		}

		try {
			Account currentAccount = accountService.findById(sessionUser.getUsername());
			currentAccount.setFullname(formUser.getFullname());
			currentAccount.setEmail(formUser.getEmail());

			accountService.update(currentAccount);
			session.setAttribute("user", currentAccount);

			params.addFlashAttribute("message", "Cập nhật hồ sơ thành công!");
			return "redirect:/account/edit-profile";
		} catch (Exception e) {
			model.addAttribute("message", "Lỗi cập nhật!");
			model.addAttribute("view", "account/edit-profile.html");
			return "layout/index";
		}
	}

	@GetMapping("/account/change-password")
	public String changePasswordForm(Model model) {
		if (session.getAttribute("user") == null)
			return "redirect:/auth/login";
		model.addAttribute("view", "account/change-password.html");
		return "layout/index";
	}

	@PostMapping("/account/change-password")
	public String changePassword(Model model, @RequestParam("password") String password,
			@RequestParam("newPassword") String newPassword, @RequestParam("confirmPassword") String confirmPassword,
			RedirectAttributes params) {
		Account sessionUser = (Account) session.getAttribute("user");
		if (sessionUser == null)
			return "redirect:/auth/login";

		if (!newPassword.equals(confirmPassword)) {
			model.addAttribute("message", "Xác nhận mật khẩu không khớp!");
		} else {
			Account user = accountService.findById(sessionUser.getUsername());
			if (!user.getPassword().equals(password)) {
				model.addAttribute("message", "Mật khẩu hiện tại không đúng!");
			} else {
				user.setPassword(newPassword);
				accountService.update(user);
				session.removeAttribute("user");
				params.addFlashAttribute("message", "Đổi mật khẩu thành công! Hãy đăng nhập lại.");
				return "redirect:/auth/login";
			}
		}
		model.addAttribute("view", "account/change-password.html");
		return "layout/index";
	}

	@GetMapping("/account/forgot-password")
	public String forgotPassword(Model model) {
		model.addAttribute("view", "account/forgot-password.html");
		return "layout/index";
	}
}