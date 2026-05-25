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
        jdbcTemplate.execute("DELETE FROM group_users");
        jdbcTemplate.execute("DELETE FROM user_roles");
        jdbcTemplate.execute("DELETE FROM comments");
        jdbcTemplate.execute("DELETE FROM tickets");
        jdbcTemplate.execute("DELETE FROM users");
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
    void save_should_assign_generated_id() {
        User saved = userDAO.save(createTestUser("ahmet"));
        assertNotNull(saved.getId());
        assertTrue(saved.getId() > 0);
    }

    @Test
    void find_by_id_should_return_correct_user() {
        User saved = userDAO.save(createTestUser("mehmet"));
        Optional<User> found = userDAO.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals("mehmet", found.get().getUsername());
    }

    @Test
    void find_by_username_should_return_user() {
        userDAO.save(createTestUser("ayse"));
        Optional<User> found = userDAO.findByUsername("ayse");
        assertTrue(found.isPresent());
    }

    @Test
    void find_by_nonexistent_id_should_return_empty() {
        Optional<User> found = userDAO.findById(999L);
        assertFalse(found.isPresent());
    }

    @Test
    void find_all_should_return_all_saved_users() {
        userDAO.save(createTestUser("user1"));
        userDAO.save(createTestUser("user2"));
        List<User> all = userDAO.findAll();
        assertEquals(2, all.size());
    }

    @Test
    void delete_by_id_should_remove_user() {
        User saved = userDAO.save(createTestUser("to_be_deleted"));
        userDAO.deleteById(saved.getId());
        assertFalse(userDAO.findById(saved.getId()).isPresent());
    }

    @Test
    void exists_by_username_should_return_correct_result() {
        userDAO.save(createTestUser("existing_user"));
        assertTrue(userDAO.existsByUsername("existing_user"));
        assertFalse(userDAO.existsByUsername("nonexistent_user"));
    }
}
