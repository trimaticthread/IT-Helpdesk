package com.helpdesk.web.controller.admin;

import com.helpdesk.application.dto.UserDTO;
import com.helpdesk.application.service.DepartmentService;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * DepartmentController birim testleri.
 * Sadece ADMIN departman yonetebilmeli.
 */
@ExtendWith(MockitoExtension.class)
class DepartmentControllerTest {

    @Mock private DepartmentService departmentService;
    @Mock private HttpServletRequest request;
    @Mock private HttpSession session;
    @Mock private Model model;

    @InjectMocks
    private DepartmentController controller;

    private UserDTO admin;
    private UserDTO agent;

    @BeforeEach
    void setUp() {
        admin = new UserDTO();
        admin.setRole("ADMIN");

        agent = new UserDTO();
        agent.setRole("AGENT");
    }

    private void loggedInAs(UserDTO user) {
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("currentUser")).thenReturn(user);
    }

    @Test
    void non_admin_cannot_list_departments() {
        loggedInAs(agent);

        String view = controller.list(request, model);

        assertEquals("redirect:/access-denied", view);
    }

    @Test
    void admin_lists_departments() {
        loggedInAs(admin);
        when(departmentService.getAllDepartments()).thenReturn(Collections.emptyList());

        String view = controller.list(request, model);

        assertEquals("admin/departments", view);
    }

    @Test
    void non_admin_cannot_create() {
        loggedInAs(agent);

        String view = controller.create("IT", request);

        assertEquals("redirect:/access-denied", view);
        verify(departmentService, never()).createDepartment(anyString());
    }

    @Test
    void admin_creates_department() {
        loggedInAs(admin);

        String view = controller.create("Finance", request);

        assertEquals("redirect:/admin/departments", view);
        verify(departmentService).createDepartment("Finance");
    }

    @Test
    void non_admin_cannot_toggle() {
        loggedInAs(agent);

        String view = controller.toggle(5L, request);

        assertEquals("redirect:/access-denied", view);
        verify(departmentService, never()).toggleActive(anyLong());
    }

    @Test
    void admin_toggles_department() {
        loggedInAs(admin);

        String view = controller.toggle(5L, request);

        assertEquals("redirect:/admin/departments", view);
        verify(departmentService).toggleActive(5L);
    }

    @Test
    void admin_deletes_department() {
        loggedInAs(admin);

        String view = controller.delete(5L, request);

        assertEquals("redirect:/admin/departments", view);
        verify(departmentService).deleteDepartment(5L);
    }
}
