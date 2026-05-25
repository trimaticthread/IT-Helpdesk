package com.helpdesk.web.controller;

import com.helpdesk.application.dto.UserDTO;
import com.helpdesk.application.service.CommentService;
import com.helpdesk.web.util.SessionUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    // ── POST /tickets/comment ─────────────────────────────────────────────────
    // Ticket'a yorum ekler, sonra ticket detay sayfasına yönlendirir (PRG)
    @PostMapping("/tickets/comment")
    public String addComment(@RequestParam Long ticketId,
                             @RequestParam(required = false) String content,
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

        try {
            // Customer her zaman public yorum yazar (isInternal = false)
            boolean isInternal = false;
            commentService.addComment(ticketId, user.getId(), content.trim(), isInternal);
            return "redirect:/tickets/" + ticketId;
        } catch (Exception e) {
            return "redirect:/tickets/" + ticketId + "?error=Comment+could+not+be+added";
        }
    }
}
