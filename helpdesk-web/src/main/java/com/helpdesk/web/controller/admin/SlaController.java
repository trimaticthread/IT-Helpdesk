package com.helpdesk.web.controller.admin;

import com.helpdesk.application.dto.UserDTO;
import com.helpdesk.application.service.SlaService;
import com.helpdesk.domain.enums.TicketPriority;
import com.helpdesk.web.util.SessionUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/sla")
public class SlaController {

    private final SlaService slaService;

    public SlaController(SlaService slaService) {
        this.slaService = slaService;
    }

    private boolean isAdmin(HttpServletRequest req) {
        UserDTO u = SessionUtil.getUser(req);
        return u != null && "ADMIN".equals(u.getRole());
    }

    @GetMapping
    public String list(HttpServletRequest req, Model model) {
        if (!isAdmin(req)) return "redirect:/access-denied";
        model.addAttribute("slaList", slaService.getAllSlaSettings());
        model.addAttribute("pageTitle", "SLA Settings");
        return "admin/sla";
    }

    @PostMapping
    public String update(
            @RequestParam String priority,
            @RequestParam int responseMinutes,
            @RequestParam int resolutionMinutes,
            HttpServletRequest req) {
        if (!isAdmin(req)) return "redirect:/access-denied";
        slaService.updateSla(TicketPriority.valueOf(priority), responseMinutes, resolutionMinutes);
        return "redirect:/admin/sla";
    }
}
