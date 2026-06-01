package com.helpdesk.web.controller;

import com.helpdesk.application.dto.UserDTO;
import com.helpdesk.application.service.TicketService;
import com.helpdesk.application.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * AssignController birim testleri.
 * Atama islemlerinin yetki kontrolleri ve agent dogrulamasi test edilir.
 */
@ExtendWith(MockitoExtension.class)
class AssignControllerTest {

    @Mock private TicketService ticketService;
    @Mock private UserService userService;
    @Mock private HttpServletRequest request;
    @Mock private HttpSession session;

    @InjectMocks
    private AssignController assignController;

    private UserDTO supervisor;
    private UserDTO customer;
    private UserDTO agent;

    @BeforeEach
    void setUp() {
        supervisor = new UserDTO();
        supervisor.setId(1L);
        supervisor.setRole("SUPERVISOR");

        customer = new UserDTO();
        customer.setId(2L);
        customer.setRole("CUSTOMER");

        agent = new UserDTO();
        agent.setId(50L);
        agent.setRole("AGENT");
    }

    private void loggedInAs(UserDTO user) {
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("currentUser")).thenReturn(user);
    }

    @Test
    void assign_without_login_should_redirect_to_login() {
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("currentUser")).thenReturn(null);

        String view = assignController.assign(100L, 50L, request);

        assertEquals("redirect:/login", view);
    }

    @Test
    void customer_cannot_assign_tickets() {
        loggedInAs(customer);

        String view = assignController.assign(100L, 50L, request);

        assertEquals("redirect:/access-denied", view);
        verify(ticketService, never()).assignTicket(anyLong(), anyLong());
    }

    @Test
    void supervisor_assigning_real_agent_should_succeed() {
        loggedInAs(supervisor);
        List<UserDTO> agents = Collections.singletonList(agent);
        when(userService.findByRole("AGENT")).thenReturn(agents);

        String view = assignController.assign(100L, 50L, request);

        assertEquals("redirect:/tickets/100", view);
        verify(ticketService).assignTicket(100L, 50L);
    }

    @Test
    void supervisor_assigning_non_agent_should_fail() {
        loggedInAs(supervisor);
        when(userService.findByRole("AGENT")).thenReturn(Collections.emptyList());

        String view = assignController.assign(100L, 999L, request);

        assertTrue(view.contains("error="));
        verify(ticketService, never()).assignTicket(anyLong(), anyLong());
    }

    @Test
    void assign_with_null_agent_id_should_skip_assignment() {
        loggedInAs(supervisor);

        String view = assignController.assign(100L, null, request);

        assertEquals("redirect:/tickets/100", view);
        verify(ticketService, never()).assignTicket(anyLong(), anyLong());
    }

    @Test
    void assign_group_by_customer_should_be_denied() {
        loggedInAs(customer);

        String view = assignController.assignGroup(100L, 5L, request);

        assertEquals("redirect:/access-denied", view);
        verify(ticketService, never()).assignGroup(anyLong(), anyLong());
    }

    @Test
    void assign_group_by_supervisor_should_succeed() {
        loggedInAs(supervisor);

        String view = assignController.assignGroup(100L, 5L, request);

        assertEquals("redirect:/tickets/100", view);
        verify(ticketService).assignGroup(100L, 5L);
    }
}
