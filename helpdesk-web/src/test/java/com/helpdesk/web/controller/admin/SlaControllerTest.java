package com.helpdesk.web.controller.admin;

import com.helpdesk.application.dto.UserDTO;
import com.helpdesk.application.service.SlaService;
import com.helpdesk.domain.enums.TicketPriority;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * SlaController birim testleri.
 * Sadece ADMIN SLA degerlerini guncelleyebilmeli.
 */
@ExtendWith(MockitoExtension.class)
class SlaControllerTest {

    @Mock private SlaService slaService;
    @Mock private HttpServletRequest request;
    @Mock private HttpSession session;
    @Mock private Model model;

    @InjectMocks
    private SlaController controller;

    private UserDTO admin;
    private UserDTO supervisor;

    @BeforeEach
    void setUp() {
        admin = new UserDTO();
        admin.setRole("ADMIN");

        supervisor = new UserDTO();
        supervisor.setRole("SUPERVISOR");
    }

    private void loggedInAs(UserDTO user) {
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("currentUser")).thenReturn(user);
    }

    @Test
    void non_admin_cannot_view_sla() {
        loggedInAs(supervisor);

        String view = controller.list(request, model);

        assertEquals("redirect:/access-denied", view);
    }

    @Test
    void admin_views_sla_list() {
        loggedInAs(admin);
        when(slaService.getAllSlaSettings()).thenReturn(Collections.emptyList());

        String view = controller.list(request, model);

        assertEquals("admin/sla", view);
    }

    @Test
    void non_admin_cannot_update_sla() {
        loggedInAs(supervisor);

        String view = controller.update("HIGH", 60, 240, request);

        assertEquals("redirect:/access-denied", view);
        verify(slaService, never()).updateSla(any(), anyInt(), anyInt());
    }

    @Test
    void admin_updates_sla() {
        loggedInAs(admin);

        String view = controller.update("HIGH", 60, 240, request);

        assertEquals("redirect:/admin/sla", view);
        verify(slaService).updateSla(TicketPriority.HIGH, 60, 240);
    }
}
