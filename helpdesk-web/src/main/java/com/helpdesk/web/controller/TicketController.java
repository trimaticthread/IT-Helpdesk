package com.helpdesk.web.controller;

import com.helpdesk.application.dto.CommentDTO;
import com.helpdesk.application.dto.CreateTicketRequest;
import com.helpdesk.application.dto.TicketDTO;
import com.helpdesk.application.dto.UserDTO;
import com.helpdesk.application.service.CategoryService;
import com.helpdesk.application.service.CommentService;
import com.helpdesk.application.service.TicketService;
import com.helpdesk.application.service.UserService;
import com.helpdesk.domain.enums.TicketStatus;
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
    private final UserService userService;

    public TicketController(TicketService ticketService,
                            CategoryService categoryService,
                            CommentService commentService,
                            UserService userService) {
        this.ticketService = ticketService;
        this.categoryService = categoryService;
        this.commentService = commentService;
        this.userService = userService;
    }

    // ── GET /tickets ──────────────────────────────────────────────────────────
    // Customer → kendi ticket'ları | Agent/Supervisor → atanmış/tüm ticket'lar
    @GetMapping("/tickets")
    public String listTickets(@RequestParam(required = false) String showResolved,
                              @RequestParam(required = false) String showClosed,
                              HttpServletRequest req, Model model) {
        UserDTO user = SessionUtil.getUser(req);
        if (user == null) return "redirect:/login";

        try {
            List<TicketDTO> tickets;
            boolean resolvedChecked = "true".equals(showResolved);
            boolean closedChecked   = "true".equals(showClosed);

            switch (user.getRole()) {
                case "CUSTOMER":
                    tickets = ticketService.findByRequesterId(user.getId());
                    break;
                case "AGENT":
                    tickets = ticketService.findByAssigneeId(user.getId());
                    if (!resolvedChecked) {
                        tickets = tickets.stream()
                                .filter(t -> !"RESOLVED".equals(t.getStatus()))
                                .collect(java.util.stream.Collectors.toList());
                    }
                    break;
                case "SUPERVISOR":
                case "ADMIN":
                    tickets = ticketService.findAll();
                    if (!resolvedChecked) {
                        tickets = tickets.stream()
                                .filter(t -> !"RESOLVED".equals(t.getStatus()))
                                .collect(java.util.stream.Collectors.toList());
                    }
                    if (!closedChecked) {
                        tickets = tickets.stream()
                                .filter(t -> !"CLOSED".equals(t.getStatus()))
                                .collect(java.util.stream.Collectors.toList());
                    }
                    break;
                default:
                    return "redirect:/access-denied";
            }

            model.addAttribute("tickets", tickets);
            model.addAttribute("showResolved", resolvedChecked);
            model.addAttribute("showClosed", closedChecked);
            model.addAttribute("pageTitle", "Tickets");
            switch (user.getRole()) {
                case "AGENT":      return "agent/ticket-list";
                case "SUPERVISOR":
                case "ADMIN":      return "supervisor/ticket-list";
                default:           return "customer/my-tickets";
            }

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

    // ── POST /tickets/{id}/status ─────────────────────────────────────────────
    @PostMapping("/tickets/{id}/status")
    public String updateStatus(@PathVariable Long id,
                               @RequestParam String status,
                               HttpServletRequest req) {
        UserDTO user = SessionUtil.getUser(req);
        if (user == null) return "redirect:/login";

        String role = user.getRole();
        if (!"AGENT".equals(role) && !"SUPERVISOR".equals(role) && !"ADMIN".equals(role)) {
            return "redirect:/access-denied";
        }

        Optional<TicketDTO> ticketOpt = ticketService.findById(id);
        if (ticketOpt.isEmpty()) return "redirect:/access-denied";
        TicketDTO ticket = ticketOpt.get();

        // Agent sadece kendisine atanmış ticket'ın status'unu değiştirebilir
        if ("AGENT".equals(role) &&
                (ticket.getAssigneeId() == null || !ticket.getAssigneeId().equals(user.getId()))) {
            return "redirect:/access-denied";
        }

        // Agent CLOSED yapamaz — sadece supervisor/admin kapatabilir
        if ("AGENT".equals(role) && "CLOSED".equals(status)) {
            return "redirect:/access-denied";
        }

        try {
            ticketService.updateStatus(id, TicketStatus.valueOf(status));
        } catch (Exception ignored) {}

        return "redirect:/tickets/" + id;
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

            switch (user.getRole()) {
                case "AGENT":
                    return "agent/ticket-detail";
                case "SUPERVISOR":
                case "ADMIN":
                    model.addAttribute("agents", userService.findByRole("AGENT"));
                    return "supervisor/ticket-detail";
                default:
                    return "customer/ticket-detail";
            }

        } catch (Exception e) {
            return "redirect:/error?message=Ticket+could+not+be+loaded";
        }
    }
}
