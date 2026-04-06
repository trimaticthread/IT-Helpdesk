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
    void yeni_kullanici_olusturulabilmeli_ve_rol_atanmali() {
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
    void sifre_hashlenip_kaydedilmeli() {
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
    void kullanici_id_ile_bulunabilmeli() {
        when(userDAO.findById(10L)).thenReturn(Optional.of(savedUser));

        Optional<UserDTO> result = userService.findById(10L);

        assertTrue(result.isPresent());
        assertEquals("testuser", result.get().getUsername());
    }

    @Test
    void olmayan_kullanici_bos_donmeli() {
        when(userDAO.findById(999L)).thenReturn(Optional.empty());

        Optional<UserDTO> result = userService.findById(999L);

        assertFalse(result.isPresent());
    }

    @Test
    void olmayan_kullanici_update_te_exception_firlatmali() {
        when(userDAO.findById(999L)).thenReturn(Optional.empty());

        UserDTO dto = new UserDTO();
        dto.setId(999L);

        assertThrows(ResourceNotFoundException.class, () -> userService.update(dto));
    }

    @Test
    void deleteById_dao_ya_devredilmeli() {
        userService.deleteById(5L);
        verify(userDAO).deleteById(5L);
    }

    @Test
    void username_varsa_true_donmeli() {
        when(userDAO.existsByUsername("varolan")).thenReturn(true);
        assertTrue(userService.existsByUsername("varolan"));
    }

    @Test
    void username_yoksa_false_donmeli() {
        when(userDAO.existsByUsername("yok")).thenReturn(false);
        assertFalse(userService.existsByUsername("yok"));
    }

    @Test
    void tum_kullanicilar_dto_ye_donusturulmeli() {
        when(userDAO.findAll()).thenReturn(List.of(savedUser));

        List<UserDTO> result = userService.findAll();

        assertEquals(1, result.size());
        assertEquals("testuser", result.get(0).getUsername());
    }
}
