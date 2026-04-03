package com.helpdesk.persistence.dao;

import com.helpdesk.domain.entity.User;
import com.helpdesk.persistence.dao.impl.UserDAOImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class UserDAOImplTest {

    @Autowired
    JdbcTemplate jdbcTemplate;

    UserDAOImpl userDAO;

    @BeforeEach
    void setUp() {
        userDAO = new UserDAOImpl(jdbcTemplate);
    }

    private User createTestUser(String username) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(username + "@test.com");
        user.setPasswordHash("hash123");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setIsActive(true);
        return user;
    }

    @Test
    void kaydetme_ve_id_atama_calismali() {
        User saved = userDAO.save(createTestUser("ahmet"));
        assertNotNull(saved.getId());
        assertTrue(saved.getId() > 0);
    }

    @Test
    void id_ile_kullanici_bulunmali() {
        User saved = userDAO.save(createTestUser("mehmet"));
        Optional<User> found = userDAO.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals("mehmet", found.get().getUsername());
    }

    @Test
    void username_ile_kullanici_bulunmali() {
        userDAO.save(createTestUser("ayse"));
        Optional<User> found = userDAO.findByUsername("ayse");
        assertTrue(found.isPresent());
    }

    @Test
    void olmayan_kullanici_bos_donmeli() {
        Optional<User> found = userDAO.findById(999L);
        assertFalse(found.isPresent());
    }

    @Test
    void tum_kullanicilar_listelenmeli() {
        userDAO.save(createTestUser("user1"));
        userDAO.save(createTestUser("user2"));
        List<User> all = userDAO.findAll();
        assertEquals(2, all.size());
    }

    @Test
    void kullanici_silinmeli() {
        User saved = userDAO.save(createTestUser("silinecek"));
        userDAO.deleteById(saved.getId());
        assertFalse(userDAO.findById(saved.getId()).isPresent());
    }

    @Test
    void username_varlik_kontrolu_calismali() {
        userDAO.save(createTestUser("varolan"));
        assertTrue(userDAO.existsByUsername("varolan"));
        assertFalse(userDAO.existsByUsername("olmayan"));
    }
}