package com.helpdesk.web.controller;

import com.helpdesk.application.dto.UserDTO;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * ChangePasswordController birim testleri.
 * Yeni sifre validasyonu ve guncelleme akisi test edilir.
 */
@ExtendWith(MockitoExtension.class)
class ChangePasswordControllerTest {

    @Mock private UserService userService;
    @Mock private HttpServletRequest request;
    @Mock private HttpSession session;
    @Mock private Model model;

    @InjectMocks
    private ChangePasswordController controller;

    private UserDTO user;

    @BeforeEach
    void setUp() {
        user = new UserDTO();
        user.setId(1L);
        user.setRole("CUSTOMER");
    }

    private void loggedIn() {
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("currentUser")).thenReturn(user);
    }

    @Test
    void show_form_without_login_redirects() {
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("currentUser")).thenReturn(null);

        String view = controller.showForm(request, model);

        assertEquals("redirect:/login", view);
    }

    @Test
    void show_form_with_login_returns_change_password_jsp() {
        loggedIn();

        String view = controller.showForm(request, model);

        assertEquals("auth/change-password", view);
    }

    @Test
    void empty_new_password_should_show_error() {
        loggedIn();

        String view = controller.changePassword("", "", request, model);

        assertEquals("auth/change-password", view);
        verify(model).addAttribute(eq("error"), any());
        verify(userService, never()).changePassword(anyLong(), anyString());
    }

    @Test
    void short_password_should_show_error() {
        loggedIn();

        String view = controller.changePassword("123", "123", request, model);

        assertEquals("auth/change-password", view);
        verify(model).addAttribute(eq("error"), any());
        verify(userService, never()).changePassword(anyLong(), anyString());
    }

    @Test
    void mismatched_passwords_should_show_error() {
        loggedIn();

        String view = controller.changePassword("password1", "password2", request, model);

        assertEquals("auth/change-password", view);
        verify(model).addAttribute(eq("error"), any());
        verify(userService, never()).changePassword(anyLong(), anyString());
    }

    @Test
    void valid_password_should_redirect_to_dashboard() {
        loggedIn();

        String view = controller.changePassword("newpass123", "newpass123", request, model);

        assertEquals("redirect:/dashboard?passwordChanged=true", view);
        verify(userService).changePassword(1L, "newpass123");
    }
}
