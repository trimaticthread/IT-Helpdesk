package com.helpdesk.web.controller;

import com.helpdesk.application.dto.TicketDTO;
import com.helpdesk.application.dto.UserDTO;
import com.helpdesk.application.service.CommentService;
import com.helpdesk.application.service.TicketService;
import com.helpdesk.web.util.SessionUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class CommentController {

    private final CommentService commentService;
    private final TicketService ticketService;

    public CommentController(CommentService commentService, TicketService ticketService) {
        this.commentService = commentService;
        this.ticketService = ticketService;
    }

    // ── POST /tickets/comment ─────────────────────────────────────────────────
    @PostMapping("/tickets/comment")
    public String addComment(@RequestParam Long ticketId,
                             @RequestParam(required = false) String content,
                             @RequestParam(required = false) String isInternal,
                             HttpServletRequest req) {
        UserDTO user = SessionUtil.getUser(req);
        if (user == null) return "redirect:/login";

        // Validation
        if (content == null || content.trim().isEmpty()) {
            return "redirect:/tickets/" + ticketId + "?error=Comment+cannot+be+empty";
        }
        if (content.trim().length() > 2000) {
            return "redirect:/tickets/" + ticketId + "?error=Comment+is+too+long";
        }

        // Ticket var mı kontrolü
        Optional<TicketDTO> ticketOpt = ticketService.findById(ticketId);
        if (ticketOpt.isEmpty()) return "redirect:/access-denied";
        TicketDTO ticket = ticketOpt.get();

        String role = user.getRole();

        // Customer sadece kendi ticket'ına yorum yazabilir
        if ("CUSTOMER".equals(role) && !ticket.getRequesterId().equals(user.getId())) {
            return "redirect:/access-denied";
        }

        // RESOLVED/CLOSED ticket'a yorum yazılamaz
        if ("RESOLVED".equals(ticket.getStatus()) || "CLOSED".equals(ticket.getStatus())) {
            return "redirect:/tickets/" + ticketId + "?error=Comment+cannot+be+added+to+closed+ticket";
        }

        // isInternal sadece AGENT/SUPERVISOR/ADMIN için geçerli, customer her zaman public
        boolean internal = false;
        if (!"CUSTOMER".equals(role) && "on".equals(isInternal)) {
            internal = true;
        }

        try {
            commentService.addComment(ticketId, user.getId(), content.trim(), internal);
            return "redirect:/tickets/" + ticketId;
        } catch (Exception e) {
            return "redirect:/tickets/" + ticketId + "?error=Comment+could+not+be+added";
        }
    }
}
