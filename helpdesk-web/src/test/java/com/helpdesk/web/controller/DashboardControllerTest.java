package com.helpdesk.web.controller;

import com.helpdesk.application.dto.UserDTO;
import com.helpdesk.application.service.TicketService;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

/**
 * DashboardController birim testleri.
 * Role bazli dashboard yonlendirme dogrulanir.
 */
@ExtendWith(MockitoExtension.class)
class DashboardControllerTest {

    @Mock private TicketService ticketService;
    @Mock private HttpServletRequest request;
    @Mock private HttpSession session;
    @Mock private Model model;

    @InjectMocks
    private DashboardController dashboardController;

    private UserDTO user;

    @BeforeEach
    void setUp() {
        user = new UserDTO();
        user.setId(1L);
    }

    private void loggedInAs(String role) {
        user.setRole(role);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("currentUser")).thenReturn(user);
    }

    @Test
    void access_denied_page_returns_access_denied_jsp() {
        String view = dashboardController.accessDenied();
        assertEquals("common/access-denied", view);
    }

    @Test
    void dashboard_without_login_redirects_to_login() {
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("currentUser")).thenReturn(null);

        String view = dashboardController.dashboard(request, model);

        assertEquals("redirect:/login", view);
    }

    @Test
    void dashboard_for_admin_returns_admin_dashboard() {
        loggedInAs("ADMIN");
        when(ticketService.findAll()).thenReturn(Collections.emptyList());

        String view = dashboardController.dashboard(request, model);

        assertEquals("admin/dashboard", view);
    }

    @Test
    void dashboard_for_supervisor_returns_supervisor_dashboard() {
        loggedInAs("SUPERVISOR");
        when(ticketService.findAll()).thenReturn(Collections.emptyList());

        String view = dashboardController.dashboard(request, model);

        assertEquals("supervisor/dashboard", view);
    }

    @Test
    void dashboard_for_agent_returns_agent_dashboard() {
        loggedInAs("AGENT");
        when(ticketService.findByAssigneeId(1L)).thenReturn(Collections.emptyList());

        String view = dashboardController.dashboard(request, model);

        assertEquals("agent/dashboard", view);
    }

    @Test
    void dashboard_for_customer_returns_customer_dashboard() {
        loggedInAs("CUSTOMER");
        when(ticketService.findByRequesterId(1L)).thenReturn(Collections.emptyList());

        String view = dashboardController.dashboard(request, model);

        assertEquals("customer/dashboard", view);
    }
}
