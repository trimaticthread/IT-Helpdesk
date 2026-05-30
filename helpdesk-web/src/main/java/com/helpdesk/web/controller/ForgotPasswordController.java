package com.helpdesk.web.controller;

import com.helpdesk.application.service.PasswordResetRequestService;
import com.helpdesk.web.util.SessionUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ForgotPasswordController {

    private final PasswordResetRequestService resetService;

    public ForgotPasswordController(PasswordResetRequestService resetService) {
        this.resetService = resetService;
    }

    @GetMapping("/forgot-password")
    public String showForm(HttpServletRequest req) {
        if (SessionUtil.isLoggedIn(req)) return "redirect:/dashboard";
        return "auth/forgot-password";
    }

    @PostMapping("/forgot-password")
    public String submit(@RequestParam(required = false) String usernameOrEmail,
                         Model model) {
        String error = resetService.submitRequest(usernameOrEmail);
        if (error != null) {
            model.addAttribute("error", error);
            model.addAttribute("usernameOrEmail", usernameOrEmail);
            return "auth/forgot-password";
        }
        model.addAttribute("success", true);
        return "auth/forgot-password";
    }
}
