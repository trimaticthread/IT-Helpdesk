package com.helpdesk.desktop.controller;

import com.helpdesk.application.dto.CommentDTO;
import com.helpdesk.application.dto.CreateTicketRequest;
import com.helpdesk.application.dto.TicketDTO;
import com.helpdesk.application.service.CategoryService;
import com.helpdesk.application.service.CommentService;
import com.helpdesk.application.service.TicketService;
import com.helpdesk.desktop.security.SessionManager;
import com.helpdesk.domain.entity.Category;
import com.helpdesk.domain.enums.TicketStatus;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Ticket islemlerini UI katmanindan servis katmanina aktaran kopru sinif.
 *
 * Gorevler: - getAllTickets : Tum ticketlari getirir. Admin ve Supervisor icin
 * kullanilir. - getMyTickets : Aktif kullanicinin olusturdugu ticketlari
 * getirir (Customer icin). - getAssignedTickets : Aktif kullaniciya atanan
 * ticketlari getirir (Agent icin). - createTicket : Yeni ticket olusturur;
 * requester olarak mevcut kullaniciyi atar. - updateStatus : Seçilen ticketin
 * durumunu gunceller. - getCategories : Aktif kategorileri getirir; ticket
 * formu dropdown'u icin kullanilir.
 */
@Component
public class TicketController {

    private final TicketService ticketService;
    private final CategoryService categoryService;
    private final CommentService commentService;

    public TicketController(TicketService ticketService, CategoryService categoryService, CommentService commentService) {
        this.ticketService = ticketService;
        this.categoryService = categoryService;
        this.commentService = commentService;
    }

    public List<Category> getCategories() {
        return categoryService.findAllActive();
    }

    public List<TicketDTO> getAllTickets() {
        return ticketService.findAll();
    }

    public List<TicketDTO> getMyTickets() {
        Long userId = SessionManager.getCurrentUser().getId();
        return ticketService.findByRequesterId(userId)
                .stream()
                .filter(t -> !"CLOSED".equals(t.getStatus()))
                .collect(Collectors.toList());
    }

    public List<TicketDTO> getAssignedTickets() {
        Long userId = SessionManager.getCurrentUser().getId();
        return ticketService.findByAssigneeId(userId)
                .stream()
                .filter(t -> !"CLOSED".equals(t.getStatus()))
                .collect(java.util.stream.Collectors.toList());
    }

    public List<CommentDTO> getComments(Long ticketId) {
        return commentService.findByTicketId(ticketId);
    }

    public TicketDTO createTicket(String title, String description, Long categoryId, String priority) {
        CreateTicketRequest request = new CreateTicketRequest();
        request.setTitle(title);
        request.setDescription(description);
        request.setCategoryId(categoryId);
        request.setPriority(priority);
        return ticketService.create(request, SessionManager.getCurrentUser().getId());
    }

    public TicketDTO updateStatus(Long ticketId, TicketStatus status) {
        return ticketService.updateStatus(ticketId, status);
    }

    public CommentDTO addComment(Long ticketId, String content) {
        Long authorId = SessionManager.getCurrentUser().getId();
        return commentService.addComment(ticketId, authorId, content, false);
    }
}
