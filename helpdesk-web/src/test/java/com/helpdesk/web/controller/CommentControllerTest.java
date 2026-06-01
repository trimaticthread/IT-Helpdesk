package com.helpdesk.web.controller;

import com.helpdesk.application.dto.TicketDTO;
import com.helpdesk.application.dto.UserDTO;
import com.helpdesk.application.service.CommentService;
import com.helpdesk.application.service.TicketService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * CommentController birim testleri.
 * Yorum ekleme akisinin guvenlik kontrolleri dogrulanir:
 * - Customer baska kullanicinin ticket'ina yorum yazamaz
 * - CLOSED/RESOLVED ticket'a yorum yazilamaz
 * - isInternal sadece AGENT/SUPERVISOR/ADMIN icin gecerli
 */
@ExtendWith(MockitoExtension.class)
class CommentControllerTest {

    @Mock private CommentService commentService;
    @Mock private TicketService ticketService;
    @Mock private HttpServletRequest request;
    @Mock private HttpSession session;

    @InjectMocks
    private CommentController commentController;

    private UserDTO customer;
    private UserDTO agent;
    private TicketDTO openTicket;

    @BeforeEach
    void setUp() {
        customer = new UserDTO();
        customer.setId(10L);
        customer.setRole("CUSTOMER");

        agent = new UserDTO();
        agent.setId(20L);
        agent.setRole("AGENT");

        openTicket = new TicketDTO();
        openTicket.setId(100L);
        openTicket.setRequesterId(10L);
        openTicket.setStatus("OPEN");
    }

    private void loggedInAs(UserDTO user) {
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("currentUser")).thenReturn(user);
    }

    @Test
    void empty_content_should_redirect_with_error() {
        loggedInAs(customer);

        String view = commentController.addComment(100L, "", null, request);

        assertTrue(view.contains("error="));
        verify(commentService, never()).addComment(anyLong(), anyLong(), anyString(), any(Boolean.class));
    }

    @Test
    void unauthenticated_user_should_redirect_to_login() {
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("currentUser")).thenReturn(null);

        String view = commentController.addComment(100L, "test", null, request);

        assertEquals("redirect:/login", view);
    }

    @Test
    void customer_commenting_on_other_users_ticket_should_be_denied() {
        loggedInAs(customer);
        openTicket.setRequesterId(99L); // baskasinin ticket'i
        when(ticketService.findById(100L)).thenReturn(Optional.of(openTicket));

        String view = commentController.addComment(100L, "hack", null, request);

        assertEquals("redirect:/access-denied", view);
        verify(commentService, never()).addComment(anyLong(), anyLong(), anyString(), any(Boolean.class));
    }

    @Test
    void comment_on_closed_ticket_should_be_rejected() {
        loggedInAs(agent);
        openTicket.setStatus("CLOSED");
        when(ticketService.findById(100L)).thenReturn(Optional.of(openTicket));

        String view = commentController.addComment(100L, "test", null, request);

        assertTrue(view.contains("error="));
        verify(commentService, never()).addComment(anyLong(), anyLong(), anyString(), any(Boolean.class));
    }

    @Test
    void agent_can_post_internal_note() {
        loggedInAs(agent);
        when(ticketService.findById(100L)).thenReturn(Optional.of(openTicket));

        commentController.addComment(100L, "internal", "on", request);

        verify(commentService).addComment(eq(100L), eq(20L), eq("internal"), eq(true));
    }

    @Test
    void customer_cannot_post_internal_note() {
        loggedInAs(customer);
        when(ticketService.findById(100L)).thenReturn(Optional.of(openTicket));

        commentController.addComment(100L, "test", "on", request);

        // isInternal=on gonderse bile customer icin false olmali
        verify(commentService).addComment(eq(100L), eq(10L), eq("test"), eq(false));
    }

    @Test
    void successful_comment_should_redirect_to_ticket_detail() {
        loggedInAs(customer);
        when(ticketService.findById(100L)).thenReturn(Optional.of(openTicket));

        String view = commentController.addComment(100L, "hello", null, request);

        assertEquals("redirect:/tickets/100", view);
    }
}
