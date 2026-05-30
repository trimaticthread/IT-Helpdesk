package com.helpdesk.web.controller.admin;

import com.helpdesk.application.dto.UserDTO;
import com.helpdesk.application.service.PasswordResetRequestService;
import com.helpdesk.web.util.SessionUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/reset-requests")
public class ResetRequestController {

    private final PasswordResetRequestService resetService;

    public ResetRequestController(PasswordResetRequestService resetService) {
        this.resetService = resetService;
    }

    private boolean isAdmin(HttpServletRequest req) {
        UserDTO u = SessionUtil.getUser(req);
        return u != null && "ADMIN".equals(u.getRole());
    }

    @GetMapping
    public String list(HttpServletRequest req, Model model) {
        if (!isAdmin(req)) return "redirect:/access-denied";
        model.addAttribute("requests", resetService.getPendingRequests());
        model.addAttribute("pageTitle", "Password Reset Requests");
        return "admin/reset-requests";
    }

    @PostMapping("/{id}/approve")
    public String approve(@PathVariable Long id, HttpServletRequest req) {
        if (!isAdmin(req)) return "redirect:/access-denied";
        resetService.approveRequest(id);
        return "redirect:/admin/reset-requests";
    }
}
