package poly.com.asm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import poly.com.asm.entity.Account;
import poly.com.asm.service.AccountService;

@Controller
@RequestMapping("/admin/account")
public class AccountAController {

	@Autowired
	AccountService accountService;

	@RequestMapping("/index")
	public String index(Model model) {
		model.addAttribute("items", accountService.findAll());
		model.addAttribute("view", "admin/account.html");
		return "layout/index";
	}

	@PostMapping("/update-role")
	public String updateRole(@RequestParam("username") String username, @RequestParam("admin") boolean admin,
			RedirectAttributes params) {
		try {
			Account acc = accountService.findById(username);
			if (acc != null) {
				acc.setAdmin(admin);
				accountService.update(acc);

				String role = admin ? "Quản trị viên" : "Khách hàng";
				params.addFlashAttribute("message", "Cập nhật quyền thành công cho " + username + " thành " + role);
			}
		} catch (Exception e) {
			params.addFlashAttribute("message", "Lỗi cập nhật quyền!");
		}
		return "redirect:/admin/account/index";
	}
}