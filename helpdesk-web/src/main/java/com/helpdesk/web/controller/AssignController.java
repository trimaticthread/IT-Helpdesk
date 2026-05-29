package com.helpdesk.web.controller;

import com.helpdesk.application.dto.UserDTO;
import com.helpdesk.application.service.TicketService;
import com.helpdesk.application.service.UserService;
import com.helpdesk.web.util.SessionUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AssignController {

    private final TicketService ticketService;
    private final UserService userService;

    public AssignController(TicketService ticketService, UserService userService) {
        this.ticketService = ticketService;
        this.userService = userService;
    }

    @PostMapping("/supervisor/assign")
    public String assign(
            @RequestParam Long ticketId,
            @RequestParam(required = false) Long agentId,
            HttpServletRequest req) {

        UserDTO user = SessionUtil.getUser(req);
        if (user == null) return "redirect:/login";
        if (!"SUPERVISOR".equals(user.getRole()) && !"ADMIN".equals(user.getRole())) {
            return "redirect:/access-denied";
        }

        if (agentId != null) {
            // agentId'nin gerçekten AGENT rolünde olduğunu doğrula
            boolean isAgent = userService.findByRole("AGENT").stream()
                    .anyMatch(u -> agentId.equals(u.getId()));
            if (!isAgent) {
                return "redirect:/tickets/" + ticketId + "?error=Selected+user+is+not+an+agent";
            }
            ticketService.assignTicket(ticketId, agentId);
        }
        return "redirect:/tickets/" + ticketId;
    }
}
