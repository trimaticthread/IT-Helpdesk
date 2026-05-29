package com.helpdesk.web.controller;

import com.helpdesk.application.dto.TicketDTO;
import com.helpdesk.application.dto.UserDTO;
import com.helpdesk.application.service.TicketService;
import com.helpdesk.web.util.SessionUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class DashboardController {

    private final TicketService ticketService;

    public DashboardController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @GetMapping("/access-denied")
    public String accessDenied() {
        return "common/access-denied";
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpServletRequest req, Model model) {
        UserDTO user = SessionUtil.getUser(req);
        if (user == null) return "redirect:/login";

        switch (user.getRole()) {
            case "ADMIN": {
                List<TicketDTO> all = ticketService.findAll();
                model.addAttribute("totalTickets", all.size());
                model.addAttribute("openTickets",  all.stream().filter(t -> "NEW".equals(t.getStatus()) || "OPEN".equals(t.getStatus()) || "IN_PROGRESS".equals(t.getStatus())).count());
                model.addAttribute("resolvedTickets", all.stream().filter(t -> "RESOLVED".equals(t.getStatus()) || "CLOSED".equals(t.getStatus())).count());
                return "admin/dashboard";
            }

            case "SUPERVISOR": {
                List<TicketDTO> all = ticketService.findAll();
                model.addAttribute("totalTickets", all.size());
                model.addAttribute("openTickets",  all.stream().filter(t -> "NEW".equals(t.getStatus()) || "OPEN".equals(t.getStatus()) || "IN_PROGRESS".equals(t.getStatus())).count());
                model.addAttribute("pendingTickets", all.stream().filter(t -> "PENDING".equals(t.getStatus())).count());
                return "supervisor/dashboard";
            }

            case "AGENT": {
                List<TicketDTO> mine = ticketService.findByAssigneeId(user.getId());
                model.addAttribute("totalTickets", mine.size());
                model.addAttribute("openTickets",  mine.stream().filter(t -> "OPEN".equals(t.getStatus()) || "IN_PROGRESS".equals(t.getStatus())).count());
                model.addAttribute("resolvedTickets", mine.stream().filter(t -> "RESOLVED".equals(t.getStatus())).count());
                return "agent/dashboard";
            }

            case "CUSTOMER":
                // Customer dashboard'a ticket istatistiklerini geçir
                try {
                    List<TicketDTO> tickets = ticketService.findByRequesterId(user.getId());
                    long open     = tickets.stream().filter(t -> !"CLOSED".equals(t.getStatus()) && !"RESOLVED".equals(t.getStatus())).count();
                    long resolved = tickets.stream().filter(t -> "RESOLVED".equals(t.getStatus()) || "CLOSED".equals(t.getStatus())).count();
                    model.addAttribute("totalTickets",    tickets.size());
                    model.addAttribute("openTickets",     open);
                    model.addAttribute("resolvedTickets", resolved);
                } catch (Exception ignored) {}
                return "customer/dashboard";

            default:
                return "redirect:/access-denied";
        }
    }
}
