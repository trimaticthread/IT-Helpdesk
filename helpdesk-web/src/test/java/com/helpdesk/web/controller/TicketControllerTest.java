package com.helpdesk.web.controller;

import com.helpdesk.application.dto.TicketDTO;
import com.helpdesk.application.dto.UserDTO;
import com.helpdesk.application.service.CategoryService;
import com.helpdesk.application.service.CommentService;
import com.helpdesk.application.service.GroupService;
import com.helpdesk.application.service.TicketService;
import com.helpdesk.application.service.UserService;
import com.helpdesk.domain.enums.TicketStatus;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * TicketController birim testleri.
 * Role bazli yonlendirme, status guncelleme yetki kontrolleri ve detay erisimi test edilir.
 */
@ExtendWith(MockitoExtension.class)
class TicketControllerTest {

    @Mock private TicketService ticketService;
    @Mock private CategoryService categoryService;
    @Mock private CommentService commentService;
    @Mock private UserService userService;
    @Mock private GroupService groupService;
    @Mock private HttpServletRequest request;
    @Mock private HttpSession session;
    @Mock private Model model;

    @InjectMocks
    private TicketController ticketController;

    private UserDTO customer;
    private UserDTO agent;
    private UserDTO supervisor;
    private TicketDTO ticket;

    @BeforeEach
    void setUp() {
        customer = new UserDTO();
        customer.setId(10L);
        customer.setRole("CUSTOMER");

        agent = new UserDTO();
        agent.setId(20L);
        agent.setRole("AGENT");

        supervisor = new UserDTO();
        supervisor.setId(30L);
        supervisor.setRole("SUPERVISOR");

        ticket = new TicketDTO();
        ticket.setId(100L);
        ticket.setTicketNumber("TKT-1");
        ticket.setStatus("OPEN");
        ticket.setRequesterId(10L);
        ticket.setAssigneeId(20L);
    }

    private void loggedInAs(UserDTO user) {
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("currentUser")).thenReturn(user);
    }

    // ── listTickets ──────────────────────────────────────────────────────────

    @Test
    void list_for_customer_returns_customer_view() {
        loggedInAs(customer);
        when(ticketService.findByRequesterId(10L)).thenReturn(Collections.emptyList());

        String view = ticketController.listTickets(null, null, request, model);

        assertEquals("customer/my-tickets", view);
        verify(ticketService).findByRequesterId(10L);
    }

    @Test
    void list_for_agent_returns_agent_view() {
        loggedInAs(agent);
        when(ticketService.findByAssigneeId(20L)).thenReturn(Collections.emptyList());

        String view = ticketController.listTickets(null, null, request, model);

        assertEquals("agent/ticket-list", view);
    }

    @Test
    void list_for_supervisor_returns_supervisor_view() {
        loggedInAs(supervisor);
        when(ticketService.findAll()).thenReturn(Collections.emptyList());

        String view = ticketController.listTickets(null, null, request, model);

        assertEquals("supervisor/ticket-list", view);
    }

    @Test
    void list_without_login_redirects_to_login() {
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("currentUser")).thenReturn(null);

        String view = ticketController.listTickets(null, null, request, model);

        assertEquals("redirect:/login", view);
    }

    // ── showCreateForm ───────────────────────────────────────────────────────

    @Test
    void create_form_only_for_customer() {
        loggedInAs(agent);

        String view = ticketController.showCreateForm(request, model);

        assertEquals("redirect:/access-denied", view);
    }

    @Test
    void create_form_for_customer_returns_create_jsp() {
        loggedInAs(customer);
        when(categoryService.findAllActive()).thenReturn(Collections.emptyList());

        String view = ticketController.showCreateForm(request, model);

        assertEquals("customer/create-ticket", view);
    }

    // ── createTicket validation ──────────────────────────────────────────────

    @Test
    void create_with_empty_title_returns_form_with_error() {
        loggedInAs(customer);
        when(categoryService.findAllActive()).thenReturn(Collections.emptyList());

        String view = ticketController.createTicket("", "desc", null, "LOW", request, model);

        assertEquals("customer/create-ticket", view);
        verify(ticketService, never()).create(any(), anyLong());
    }

    @Test
    void create_with_empty_description_returns_form_with_error() {
        loggedInAs(customer);
        when(categoryService.findAllActive()).thenReturn(Collections.emptyList());

        String view = ticketController.createTicket("title", "", null, "LOW", request, model);

        assertEquals("customer/create-ticket", view);
        verify(ticketService, never()).create(any(), anyLong());
    }

    @Test
    void create_success_redirects_to_tickets() {
        loggedInAs(customer);

        String view = ticketController.createTicket("title", "desc", 1L, "HIGH", request, model);

        assertEquals("redirect:/tickets", view);
        verify(ticketService).create(any(), any(Long.class));
    }

    // ── updateStatus ─────────────────────────────────────────────────────────

    @Test
    void update_status_without_login_redirects_to_login() {
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("currentUser")).thenReturn(null);

        String view = ticketController.updateStatus(100L, "RESOLVED", request);

        assertEquals("redirect:/login", view);
    }

    @Test
    void customer_cannot_update_status() {
        loggedInAs(customer);

        String view = ticketController.updateStatus(100L, "RESOLVED", request);

        assertEquals("redirect:/access-denied", view);
    }

    @Test
    void agent_can_update_status_of_own_ticket() {
        loggedInAs(agent);
        when(ticketService.findById(100L)).thenReturn(Optional.of(ticket));

        String view = ticketController.updateStatus(100L, "IN_PROGRESS", request);

        assertEquals("redirect:/tickets/100", view);
        verify(ticketService).updateStatus(100L, TicketStatus.IN_PROGRESS);
    }

    @Test
    void agent_cannot_update_status_of_other_ticket() {
        loggedInAs(agent);
        ticket.setAssigneeId(999L); // baska agent'a atanmis
        when(ticketService.findById(100L)).thenReturn(Optional.of(ticket));

        String view = ticketController.updateStatus(100L, "RESOLVED", request);

        assertEquals("redirect:/access-denied", view);
        verify(ticketService, never()).updateStatus(anyLong(), any());
    }

    @Test
    void agent_cannot_close_ticket() {
        loggedInAs(agent);
        when(ticketService.findById(100L)).thenReturn(Optional.of(ticket));

        String view = ticketController.updateStatus(100L, "CLOSED", request);

        assertEquals("redirect:/access-denied", view);
        verify(ticketService, never()).updateStatus(anyLong(), any());
    }

    @Test
    void supervisor_can_close_ticket() {
        loggedInAs(supervisor);
        when(ticketService.findById(100L)).thenReturn(Optional.of(ticket));

        String view = ticketController.updateStatus(100L, "CLOSED", request);

        assertEquals("redirect:/tickets/100", view);
        verify(ticketService).updateStatus(100L, TicketStatus.CLOSED);
    }

    // ── ticketDetail ─────────────────────────────────────────────────────────

    @Test
    void customer_viewing_own_ticket_returns_customer_detail() {
        loggedInAs(customer);
        when(ticketService.findById(100L)).thenReturn(Optional.of(ticket));
        when(commentService.findByTicketId(100L)).thenReturn(Collections.emptyList());

        String view = ticketController.ticketDetail(100L, request, model);

        assertEquals("customer/ticket-detail", view);
    }

    @Test
    void customer_viewing_other_users_ticket_is_denied() {
        loggedInAs(customer);
        ticket.setRequesterId(999L);
        when(ticketService.findById(100L)).thenReturn(Optional.of(ticket));

        String view = ticketController.ticketDetail(100L, request, model);

        assertEquals("redirect:/access-denied", view);
    }

    @Test
    void agent_detail_returns_agent_view() {
        loggedInAs(agent);
        when(ticketService.findById(100L)).thenReturn(Optional.of(ticket));
        when(commentService.findByTicketId(100L)).thenReturn(Collections.emptyList());

        String view = ticketController.ticketDetail(100L, request, model);

        assertEquals("agent/ticket-detail", view);
    }

    @Test
    void supervisor_detail_returns_supervisor_view() {
        loggedInAs(supervisor);
        when(ticketService.findById(100L)).thenReturn(Optional.of(ticket));
        when(commentService.findByTicketId(100L)).thenReturn(Collections.emptyList());
        when(groupService.getAllGroups()).thenReturn(Collections.emptyList());
        when(userService.findByRole("AGENT")).thenReturn(Collections.emptyList());

        String view = ticketController.ticketDetail(100L, request, model);

        assertEquals("supervisor/ticket-detail", view);
    }
}
