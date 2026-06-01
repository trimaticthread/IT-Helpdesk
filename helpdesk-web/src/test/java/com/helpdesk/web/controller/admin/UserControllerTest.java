package com.helpdesk.web.controller.admin;

import com.helpdesk.application.dto.UserDTO;
import com.helpdesk.application.service.DepartmentService;
import com.helpdesk.application.service.UserService;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * UserController (admin) birim testleri.
 * Sadece ADMIN rolu kullanici yonetebilmeli.
 */
@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock private UserService userService;
    @Mock private DepartmentService departmentService;
    @Mock private HttpServletRequest request;
    @Mock private HttpSession session;
    @Mock private Model model;

    @InjectMocks
    private UserController userController;

    private UserDTO admin;
    private UserDTO supervisor;

    @BeforeEach
    void setUp() {
        admin = new UserDTO();
        admin.setId(1L);
        admin.setRole("ADMIN");

        supervisor = new UserDTO();
        supervisor.setId(2L);
        supervisor.setRole("SUPERVISOR");
    }

    private void loggedInAs(UserDTO user) {
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("currentUser")).thenReturn(user);
    }

    @Test
    void list_users_requires_admin() {
        loggedInAs(supervisor);

        String view = userController.list(request, model);

        assertEquals("redirect:/access-denied", view);
        verify(userService, never()).findAll();
    }

    @Test
    void admin_can_list_users() {
        loggedInAs(admin);
        when(userService.findAll()).thenReturn(Collections.emptyList());

        String view = userController.list(request, model);

        assertEquals("admin/users", view);
    }

    @Test
    void create_form_requires_admin() {
        loggedInAs(supervisor);

        String view = userController.newUserForm(request, model);

        assertEquals("redirect:/access-denied", view);
    }

    @Test
    void admin_can_open_create_form() {
        loggedInAs(admin);
        when(departmentService.getAllDepartments()).thenReturn(Collections.emptyList());

        String view = userController.newUserForm(request, model);

        assertEquals("admin/create-user", view);
    }

    @Test
    void create_user_with_duplicate_username_shows_error() {
        loggedInAs(admin);
        when(userService.existsByUsername("john")).thenReturn(true);
        when(departmentService.getAllDepartments()).thenReturn(Collections.emptyList());

        String view = userController.createUser(
                "john", "John", "Doe", "john@test.com",
                "secret", "CUSTOMER", null, request, model);

        assertEquals("admin/create-user", view);
        verify(userService, never()).createUser(any(), anyString(), anyString());
    }

    @Test
    void admin_creates_user_successfully() {
        loggedInAs(admin);
        when(userService.existsByUsername("newuser")).thenReturn(false);

        String view = userController.createUser(
                "newuser", "New", "User", "new@test.com",
                "secret", "AGENT", "IT", request, model);

        assertEquals("redirect:/admin/users", view);
        verify(userService).createUser(any(), any(String.class), any(String.class));
    }

    @Test
    void delete_user_requires_admin() {
        loggedInAs(supervisor);

        String view = userController.deleteUser(5L, request);

        assertEquals("redirect:/access-denied", view);
        verify(userService, never()).deleteById(anyLong());
    }

    @Test
    void admin_can_delete_user() {
        loggedInAs(admin);

        String view = userController.deleteUser(5L, request);

        assertEquals("redirect:/admin/users", view);
        verify(userService).deleteById(5L);
    }

    @Test
    void admin_can_reset_password() {
        loggedInAs(admin);

        String view = userController.resetPassword(5L, request);

        assertEquals("redirect:/admin/users", view);
        verify(userService).resetPassword(5L);
    }
}
