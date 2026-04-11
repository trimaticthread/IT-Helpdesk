package com.helpdesk.application.service;

import com.helpdesk.application.dto.UserDTO;
import com.helpdesk.application.service.impl.AuthServiceImpl;
import com.helpdesk.domain.entity.User;
import com.helpdesk.persistence.dao.UserDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * AuthServiceImpl icin birim testleri.
 * Gercek veritabani veya Spring context kullanilmaz; tum bagimliliklar Mockito ile taklit edilir.
 *
 * Test senaryolari:
 * - Dogru sifre ile giris basarili olmali
 * - Yanlis sifre ile giris bos donmeli
 * - Olmayan kullanici ile giris bos donmeli
 * - Pasif hesap ile giris bos donmeli
 * - Basarili giriste role DTO'ya yuklenmeli
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserDAO userDAO;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthServiceImpl authService;

    private User activeUser;

    @BeforeEach
    void setUp() {
        activeUser = new User();
        activeUser.setId(1L);
        activeUser.setUsername("john");
        activeUser.setPasswordHash("$2a$hashed");
        activeUser.setFirstName("John");
        activeUser.setLastName("Doe");
        activeUser.setEmail("john@test.com");
        activeUser.setIsActive(true);
    }

    @Test
    void login_with_correct_password_should_succeed() {
        when(userDAO.findByUsername("john")).thenReturn(Optional.of(activeUser));
        when(passwordEncoder.matches("secret", "$2a$hashed")).thenReturn(true);
        when(userDAO.getRoleName(1L)).thenReturn("CUSTOMER");

        Optional<UserDTO> result = authService.login("john", "secret");

        assertTrue(result.isPresent());
        assertEquals("john", result.get().getUsername());
    }

    @Test
    void login_with_wrong_password_should_return_empty() {
        when(userDAO.findByUsername("john")).thenReturn(Optional.of(activeUser));
        when(passwordEncoder.matches("yanlis", "$2a$hashed")).thenReturn(false);

        Optional<UserDTO> result = authService.login("john", "yanlis");

        assertFalse(result.isPresent());
    }

    @Test
    void find_by_nonexistent_id_should_return_empty() {
        when(userDAO.findByUsername("yok")).thenReturn(Optional.empty());

        Optional<UserDTO> result = authService.login("yok", "sifre");

        assertFalse(result.isPresent());
    }

    @Test
    void login_with_inactive_account_should_return_empty() {
        activeUser.setIsActive(false);
        when(userDAO.findByUsername("john")).thenReturn(Optional.of(activeUser));

        Optional<UserDTO> result = authService.login("john", "secret");

        assertFalse(result.isPresent());
        // Sifre kontrolu bile yapilmamali
        verify(passwordEncoder, never()).matches(any(), any());
    }

    @Test
    void login_should_load_role_into_dto() {
        when(userDAO.findByUsername("john")).thenReturn(Optional.of(activeUser));
        when(passwordEncoder.matches("secret", "$2a$hashed")).thenReturn(true);
        when(userDAO.getRoleName(1L)).thenReturn("ADMIN");

        Optional<UserDTO> result = authService.login("john", "secret");

        assertTrue(result.isPresent());
        assertEquals("ADMIN", result.get().getRole());
    }

    @Test
    void login_should_return_correct_full_name() {
        when(userDAO.findByUsername("john")).thenReturn(Optional.of(activeUser));
        when(passwordEncoder.matches("secret", "$2a$hashed")).thenReturn(true);
        when(userDAO.getRoleName(1L)).thenReturn("CUSTOMER");

        Optional<UserDTO> result = authService.login("john", "secret");

        assertTrue(result.isPresent());
        assertEquals("John Doe", result.get().getFullName());
    }
}
