package com.helpdesk.web.controller;

import com.helpdesk.application.dto.CommentDTO;
import com.helpdesk.application.dto.CreateTicketRequest;
import com.helpdesk.application.dto.TicketDTO;
import com.helpdesk.application.dto.UserDTO;
import com.helpdesk.application.service.CategoryService;
import com.helpdesk.application.service.CommentService;
import com.helpdesk.application.service.TicketService;
import com.helpdesk.web.util.SessionUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Controller
public class TicketController {

    private final TicketService ticketService;
    private final CategoryService categoryService;
    private final CommentService commentService;

    public TicketController(TicketService ticketService,
                            CategoryService categoryService,
                            CommentService commentService) {
        this.ticketService = ticketService;
        this.categoryService = categoryService;
        this.commentService = commentService;
    }

    // ── GET /tickets ──────────────────────────────────────────────────────────
    // Customer → kendi ticket'ları | Agent/Supervisor → atanmış/tüm ticket'lar
    @GetMapping("/tickets")
    public String listTickets(HttpServletRequest req, Model model) {
        UserDTO user = SessionUtil.getUser(req);
        if (user == null) return "redirect:/login";

        try {
            List<TicketDTO> tickets;

            switch (user.getRole()) {
                case "CUSTOMER":
                    tickets = ticketService.findByRequesterId(user.getId());
                    break;
                case "AGENT":
                    tickets = ticketService.findByAssigneeId(user.getId());
                    break;
                case "SUPERVISOR":
                case "ADMIN":
                    tickets = ticketService.findAll();
                    break;
                default:
                    return "redirect:/access-denied";
            }

            model.addAttribute("tickets", tickets);
            model.addAttribute("pageTitle", "My Tickets");
            return "customer/my-tickets";

        } catch (Exception e) {
            return "redirect:/error?message=Tickets+could+not+be+loaded";
        }
    }

    // ── GET /tickets/new ──────────────────────────────────────────────────────
    @GetMapping("/tickets/new")
    public String showCreateForm(HttpServletRequest req, Model model) {
        if (!SessionUtil.hasRole(req, "CUSTOMER")) return "redirect:/access-denied";

        try {
            model.addAttribute("categories", categoryService.findAllActive());
            model.addAttribute("pageTitle", "New Ticket");
            return "customer/create-ticket";
        } catch (Exception e) {
            return "redirect:/error?message=Page+could+not+be+loaded";
        }
    }

    // ── POST /tickets/new ─────────────────────────────────────────────────────
    @PostMapping("/tickets/new")
    public String createTicket(@RequestParam(required = false) String title,
                               @RequestParam(required = false) String description,
                               @RequestParam(required = false) Long categoryId,
                               @RequestParam(required = false) String priority,
                               HttpServletRequest req,
                               Model model) {
        if (!SessionUtil.hasRole(req, "CUSTOMER")) return "redirect:/access-denied";

        // ── Validation ────────────────────────────────────────────────────────
        if (title == null || title.trim().isEmpty()) {
            model.addAttribute("error", "Title is required.");
            model.addAttribute("categories", categoryService.findAllActive());
            model.addAttribute("description", description);
            return "customer/create-ticket";
        }
        if (title.trim().length() > 255) {
            model.addAttribute("error", "Title must be less than 255 characters.");
            model.addAttribute("categories", categoryService.findAllActive());
            model.addAttribute("title", title);
            model.addAttribute("description", description);
            return "customer/create-ticket";
        }
        if (description == null || description.trim().isEmpty()) {
            model.addAttribute("error", "Description is required.");
            model.addAttribute("categories", categoryService.findAllActive());
            model.addAttribute("title", title);
            return "customer/create-ticket";
        }

        try {
            UserDTO user = SessionUtil.getUser(req);
            CreateTicketRequest request = new CreateTicketRequest();
            request.setTitle(title.trim());
            request.setDescription(description.trim());
            request.setCategoryId(categoryId);
            request.setPriority(priority != null ? priority : "MEDIUM");

            ticketService.create(request, user.getId());
            return "redirect:/tickets"; // PRG pattern
        } catch (Exception e) {
            model.addAttribute("error", "An error occurred. Please try again.");
            model.addAttribute("categories", categoryService.findAllActive());
            model.addAttribute("title", title);
            model.addAttribute("description", description);
            return "customer/create-ticket";
        }
    }

    // ── GET /tickets/{id} ─────────────────────────────────────────────────────
    @GetMapping("/tickets/{id}")
    public String ticketDetail(@PathVariable Long id,
                               HttpServletRequest req,
                               Model model) {
        UserDTO user = SessionUtil.getUser(req);
        if (user == null) return "redirect:/login";

        try {
            Optional<TicketDTO> result = ticketService.findById(id);
            if (result.isEmpty()) {
                return "redirect:/error?message=Ticket+not+found";
            }

            TicketDTO ticket = result.get();

            // Customer sadece kendi ticket'ını görebilir
            if ("CUSTOMER".equals(user.getRole()) &&
                    !ticket.getRequesterId().equals(user.getId())) {
                return "redirect:/access-denied";
            }

            List<CommentDTO> comments = commentService.findByTicketId(id);

            // Customer iç notları göremez (isInternal = true)
            if ("CUSTOMER".equals(user.getRole())) {
                comments = comments.stream()
                        .filter(c -> !Boolean.TRUE.equals(c.getIsInternal()))
                        .collect(java.util.stream.Collectors.toList());
            }

            model.addAttribute("ticket", ticket);
            model.addAttribute("comments", comments);
            model.addAttribute("pageTitle", "Ticket #" + ticket.getTicketNumber());
            return "customer/ticket-detail";

        } catch (Exception e) {
            return "redirect:/error?message=Ticket+could+not+be+loaded";
        }
    }
}
