package com.helpdesk.web.controller;

import com.helpdesk.application.dto.UserDTO;
import com.helpdesk.application.service.UserService;
import com.helpdesk.web.util.SessionUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ChangePasswordController {

    private final UserService userService;

    public ChangePasswordController(UserService userService) {
        this.userService = userService;
    }

    // ── GET /change-password ──────────────────────────────────────────────────
    @GetMapping("/change-password")
    public String showForm(HttpServletRequest req, Model model) {
        if (!SessionUtil.isLoggedIn(req)) return "redirect:/login";
        model.addAttribute("pageTitle", "Change Password");
        return "auth/change-password";
    }

    // ── POST /change-password ─────────────────────────────────────────────────
    @PostMapping("/change-password")
    public String changePassword(@RequestParam(required = false) String newPassword,
                                 @RequestParam(required = false) String confirmPassword,
                                 HttpServletRequest req,
                                 Model model) {
        UserDTO user = SessionUtil.getUser(req);
        if (user == null) return "redirect:/login";

        // ── Validation ────────────────────────────────────────────────────────
        if (newPassword == null || newPassword.trim().isEmpty()) {
            model.addAttribute("error", "New password is required.");
            return "auth/change-password";
        }
        if (newPassword.length() < 6) {
            model.addAttribute("error", "Password must be at least 6 characters.");
            return "auth/change-password";
        }
        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match.");
            return "auth/change-password";
        }

        try {
            userService.changePassword(user.getId(), newPassword);
            req.getSession().removeAttribute("passwordResetRequired");
            return "redirect:/dashboard?passwordChanged=true";
        } catch (Exception e) {
            model.addAttribute("error", "An error occurred. Please try again.");
            return "auth/change-password";
        }
    }
}
