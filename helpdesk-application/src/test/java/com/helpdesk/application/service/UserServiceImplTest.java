package com.helpdesk.application.service;

import com.helpdesk.application.dto.UserDTO;
import com.helpdesk.application.service.impl.UserServiceImpl;
import com.helpdesk.domain.entity.User;
import com.helpdesk.domain.exception.ResourceNotFoundException;
import com.helpdesk.persistence.dao.UserDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * UserServiceImpl icin birim testleri.
 * Gercek veritabani veya Spring context kullanilmaz; tum bagimliliklar Mockito ile taklit edilir.
 *
 * Test senaryolari:
 * - Yeni kullanici olusturulabilmeli ve rol atanmali
 * - Kullanici id ile bulunabilmeli
 * - Olmayan kullanici update'te exception firlatmali
 * - deleteById UserDAO'ya devredilmeli
 * - Username varlik kontrolu dogru cevap vermeli
 */
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserDAO userDAO;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User savedUser;

    @BeforeEach
    void setUp() {
        savedUser = new User();
        savedUser.setId(10L);
        savedUser.setUsername("testuser");
        savedUser.setEmail("test@test.com");
        savedUser.setFirstName("Test");
        savedUser.setLastName("User");
        savedUser.setIsActive(true);
    }

    @Test
    void create_user_should_assign_role() {
        when(passwordEncoder.encode("rawPass")).thenReturn("$hashed");
        when(userDAO.save(any(User.class))).thenReturn(savedUser);

        UserDTO dto = new UserDTO();
        dto.setUsername("testuser");
        dto.setEmail("test@test.com");
        dto.setFirstName("Test");
        dto.setLastName("User");

        UserDTO result = userService.createUser(dto, "rawPass", "CUSTOMER");

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        // Rol atamasi yapilmali
        verify(userDAO).assignRole(10L, "CUSTOMER");
    }

    @Test
    void save_should_hash_password_before_persisting() {
        when(passwordEncoder.encode("mypassword")).thenReturn("$hashed");
        when(userDAO.save(any(User.class))).thenReturn(savedUser);

        UserDTO dto = new UserDTO();
        dto.setUsername("testuser");
        dto.setEmail("test@test.com");
        dto.setFirstName("Test");
        dto.setLastName("User");

        userService.save(dto, "mypassword");

        // Ham sifre asla kaydedilmemeli
        verify(passwordEncoder).encode("mypassword");
        verify(userDAO).save(argThat(u -> "$hashed".equals(u.getPasswordHash())));
    }

    @Test
    void find_by_id_should_return_correct_user() {
        when(userDAO.findById(10L)).thenReturn(Optional.of(savedUser));

        Optional<UserDTO> result = userService.findById(10L);

        assertTrue(result.isPresent());
        assertEquals("testuser", result.get().getUsername());
    }

    @Test
    void find_by_nonexistent_id_should_return_empty() {
        when(userDAO.findById(999L)).thenReturn(Optional.empty());

        Optional<UserDTO> result = userService.findById(999L);

        assertFalse(result.isPresent());
    }

    @Test
    void update_nonexistent_user_should_throw_exception() {
        when(userDAO.findById(999L)).thenReturn(Optional.empty());

        UserDTO dto = new UserDTO();
        dto.setId(999L);

        assertThrows(ResourceNotFoundException.class, () -> userService.update(dto));
    }

    @Test
    void delete_by_id_should_delegate_to_dao() {
        userService.deleteById(5L);
        verify(userDAO).deleteById(5L);
    }

    @Test
    void exists_by_username_should_return_true_when_found() {
        when(userDAO.existsByUsername("varolan")).thenReturn(true);
        assertTrue(userService.existsByUsername("varolan"));
    }

    @Test
    void exists_by_username_should_return_false_when_not_found() {
        when(userDAO.existsByUsername("yok")).thenReturn(false);
        assertFalse(userService.existsByUsername("yok"));
    }

    @Test
    void find_all_should_return_mapped_dtos() {
        when(userDAO.findAll()).thenReturn(List.of(savedUser));

        List<UserDTO> result = userService.findAll();

        assertEquals(1, result.size());
        assertEquals("testuser", result.get(0).getUsername());
    }
}
