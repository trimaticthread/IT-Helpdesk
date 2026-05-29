package com.helpdesk.web.controller;

import com.helpdesk.application.dto.TicketDTO;
import com.helpdesk.application.dto.UserDTO;
import com.helpdesk.application.service.TicketService;
import com.helpdesk.web.util.SessionUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class ReportsController {

    private final TicketService ticketService;

    public ReportsController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @GetMapping("/supervisor/reports")
    public String reports(HttpServletRequest req, Model model) {
        UserDTO user = SessionUtil.getUser(req);
        if (user == null) return "redirect:/login";
        if (!"SUPERVISOR".equals(user.getRole()) && !"ADMIN".equals(user.getRole())) {
            return "redirect:/access-denied";
        }

        List<TicketDTO> all = ticketService.findAll();

        Map<String, Long> byStatus = all.stream()
                .collect(Collectors.groupingBy(TicketDTO::getStatus, Collectors.counting()));

        Map<String, Long> byPriority = all.stream()
                .collect(Collectors.groupingBy(TicketDTO::getPriority, Collectors.counting()));

        Map<String, Long> byCategory = all.stream()
                .filter(t -> t.getCategoryName() != null)
                .collect(Collectors.groupingBy(TicketDTO::getCategoryName, Collectors.counting()));

        model.addAttribute("byStatus", byStatus);
        model.addAttribute("byPriority", byPriority);
        model.addAttribute("byCategory", byCategory);
        model.addAttribute("pageTitle", "Reports");
        return "supervisor/reports";
    }
}
