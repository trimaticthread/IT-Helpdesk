package com.helpdesk.web.controller;

import com.helpdesk.application.dto.UserDTO;
import com.helpdesk.application.service.AuthService;
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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * AuthController birim testleri.
 * Login akisinin tum dallari mock'lar ile dogrulanir.
 */
@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock private AuthService authService;
    @Mock private UserService userService;
    @Mock private HttpServletRequest request;
    @Mock private HttpSession session;
    @Mock private Model model;

    @InjectMocks
    private AuthController authController;

    private UserDTO activeUser;

    @BeforeEach
    void setUp() {
        activeUser = new UserDTO();
        activeUser.setId(1L);
        activeUser.setUsername("john");
        activeUser.setIsActive(true);
        activeUser.setRole("CUSTOMER");
    }

    @Test
    void loginPage_when_already_logged_in_should_redirect_to_dashboard() {
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("currentUser")).thenReturn(activeUser);

        String view = authController.loginPage(request);

        assertEquals("redirect:/dashboard", view);
    }

    @Test
    void loginPage_when_not_logged_in_should_show_login() {
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("currentUser")).thenReturn(null);

        String view = authController.loginPage(request);

        assertEquals("auth/login", view);
    }

    @Test
    void login_with_empty_username_should_show_error() {
        String view = authController.login("", "password", request, model);

        assertEquals("auth/login", view);
        verify(model).addAttribute(eq("error"), any());
        verify(authService, never()).login(any(), any());
    }

    @Test
    void login_with_empty_password_should_show_error() {
        String view = authController.login("john", "", request, model);

        assertEquals("auth/login", view);
        verify(model).addAttribute(eq("error"), any());
        verify(authService, never()).login(any(), any());
    }

    @Test
    void login_with_invalid_credentials_should_show_error() {
        when(authService.login("john", "wrong")).thenReturn(Optional.empty());

        String view = authController.login("john", "wrong", request, model);

        assertEquals("auth/login", view);
        verify(model).addAttribute(eq("error"), any());
    }

    @Test
    void login_with_inactive_account_should_show_disabled_error() {
        activeUser.setIsActive(false);
        when(authService.login("john", "secret")).thenReturn(Optional.of(activeUser));

        String view = authController.login("john", "secret", request, model);

        assertEquals("auth/login", view);
        verify(model).addAttribute(eq("error"), any());
    }

    @Test
    void login_success_without_reset_should_redirect_to_dashboard() {
        when(authService.login("john", "secret")).thenReturn(Optional.of(activeUser));
        when(userService.isPasswordResetRequired(1L)).thenReturn(false);
        when(request.getSession()).thenReturn(session);

        String view = authController.login("john", "secret", request, model);

        assertEquals("redirect:/dashboard", view);
        verify(session).setAttribute("currentUser", activeUser);
    }

    @Test
    void login_success_with_reset_required_should_redirect_to_change_password() {
        when(authService.login("john", "secret")).thenReturn(Optional.of(activeUser));
        when(userService.isPasswordResetRequired(1L)).thenReturn(true);
        when(request.getSession()).thenReturn(session);

        String view = authController.login("john", "secret", request, model);

        assertEquals("redirect:/change-password", view);
    }

    @Test
    void logout_should_invalidate_session_and_redirect_to_login() {
        when(request.getSession(false)).thenReturn(session);

        String view = authController.logout(request);

        assertEquals("redirect:/login", view);
        verify(session).invalidate();
    }
}
