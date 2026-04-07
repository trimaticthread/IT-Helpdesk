package com.helpdesk.persistence.dao;

import com.helpdesk.domain.entity.Category;
import com.helpdesk.domain.entity.Ticket;
import com.helpdesk.domain.entity.User;
import com.helpdesk.domain.enums.TicketPriority;
import com.helpdesk.domain.enums.TicketStatus;
import com.helpdesk.persistence.dao.impl.CategoryDAOImpl;
import com.helpdesk.persistence.dao.impl.TicketDAOImpl;
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
class TicketDAOImplTest {

    @Autowired
    JdbcTemplate jdbcTemplate;

    TicketDAOImpl ticketDAO;
    UserDAOImpl userDAO;
    CategoryDAOImpl categoryDAO;

    User testUser;
    Category testCategory;

    @BeforeEach
    void setUp() {
        ticketDAO = new TicketDAOImpl(jdbcTemplate);
        userDAO = new UserDAOImpl(jdbcTemplate);
        categoryDAO = new CategoryDAOImpl(jdbcTemplate);

        User user = new User();
        user.setUsername("test_requester");
        user.setEmail("requester@test.com");
        user.setPasswordHash("hash123");
        user.setFirstName("Test");
        user.setLastName("Requester");
        user.setIsActive(true);
        testUser = userDAO.save(user);

        Category category = new Category();
        category.setName("Test Category");
        category.setIsActive(true);
        testCategory = categoryDAO.save(category);
    }

    private Ticket createTestTicket(String ticketNumber) {
        Ticket ticket = new Ticket();
        ticket.setTicketNumber(ticketNumber);
        ticket.setTitle("Test Ticket");
        ticket.setDescription("Test description");
        ticket.setStatus(TicketStatus.NEW);
        ticket.setPriority(TicketPriority.MEDIUM);
        ticket.setRequester(testUser);
        ticket.setCategory(testCategory);
        return ticket;
    }

    @Test
    void save_should_assign_generated_id() {
        Ticket saved = ticketDAO.save(createTestTicket("TKT-001"));
        assertNotNull(saved.getId());
        assertTrue(saved.getId() > 0);
    }

    @Test
    void find_by_id_should_return_correct_ticket() {
        Ticket saved = ticketDAO.save(createTestTicket("TKT-002"));
        Optional<Ticket> found = ticketDAO.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals("TKT-002", found.get().getTicketNumber());
    }

    @Test
    void find_by_ticket_number_should_return_correct_ticket() {
        ticketDAO.save(createTestTicket("TKT-003"));
        Optional<Ticket> found = ticketDAO.findByTicketNumber("TKT-003");
        assertTrue(found.isPresent());
        assertEquals("Test Ticket", found.get().getTitle());
    }

    @Test
    void find_by_nonexistent_id_should_return_empty() {
        Optional<Ticket> found = ticketDAO.findById(999L);
        assertFalse(found.isPresent());
    }

    @Test
    void find_by_requester_id_should_return_all_requester_tickets() {
        ticketDAO.save(createTestTicket("TKT-004"));
        ticketDAO.save(createTestTicket("TKT-005"));
        List<Ticket> tickets = ticketDAO.findByRequesterId(testUser.getId());
        assertEquals(2, tickets.size());
    }

    @Test
    void find_by_status_should_return_only_matching_tickets() {
        ticketDAO.save(createTestTicket("TKT-006"));
        List<Ticket> newTickets = ticketDAO.findByStatus(TicketStatus.NEW);
        assertFalse(newTickets.isEmpty());
        newTickets.forEach(t -> assertEquals(TicketStatus.NEW, t.getStatus()));
    }

    @Test
    void update_should_persist_changed_fields() {
        Ticket saved = ticketDAO.save(createTestTicket("TKT-007"));
        saved.setTitle("Updated Title");
        saved.setStatus(TicketStatus.IN_PROGRESS);
        ticketDAO.update(saved);
        Optional<Ticket> updated = ticketDAO.findById(saved.getId());
        assertTrue(updated.isPresent());
        assertEquals("Updated Title", updated.get().getTitle());
        assertEquals(TicketStatus.IN_PROGRESS, updated.get().getStatus());
    }

    @Test
    void delete_by_id_should_remove_ticket() {
        Ticket saved = ticketDAO.save(createTestTicket("TKT-008"));
        ticketDAO.deleteById(saved.getId());
        assertFalse(ticketDAO.findById(saved.getId()).isPresent());
    }
}
