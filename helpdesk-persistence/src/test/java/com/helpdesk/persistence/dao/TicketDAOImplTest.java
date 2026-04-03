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
        category.setName("Test Kategori");
        category.setIsActive(true);
        testCategory = categoryDAO.save(category);
    }

    private Ticket createTestTicket(String ticketNumber) {
        Ticket ticket = new Ticket();
        ticket.setTicketNumber(ticketNumber);
        ticket.setTitle("Test Ticket");
        ticket.setDescription("Test açıklaması");
        ticket.setStatus(TicketStatus.NEW);
        ticket.setPriority(TicketPriority.MEDIUM);
        ticket.setRequester(testUser);
        ticket.setCategory(testCategory);
        return ticket;
    }

    @Test
    void kaydetme_ve_id_atama_calismali() {
        Ticket saved = ticketDAO.save(createTestTicket("TKT-001"));
        assertNotNull(saved.getId());
        assertTrue(saved.getId() > 0);
    }

    @Test
    void id_ile_ticket_bulunmali() {
        Ticket saved = ticketDAO.save(createTestTicket("TKT-002"));
        Optional<Ticket> found = ticketDAO.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals("TKT-002", found.get().getTicketNumber());
    }

    @Test
    void ticket_number_ile_ticket_bulunmali() {
        ticketDAO.save(createTestTicket("TKT-003"));
        Optional<Ticket> found = ticketDAO.findByTicketNumber("TKT-003");
        assertTrue(found.isPresent());
        assertEquals("Test Ticket", found.get().getTitle());
    }

    @Test
    void olmayan_ticket_bos_donmeli() {
        Optional<Ticket> found = ticketDAO.findById(999L);
        assertFalse(found.isPresent());
    }

    @Test
    void requester_id_ile_ticketlar_listelenmeli() {
        ticketDAO.save(createTestTicket("TKT-004"));
        ticketDAO.save(createTestTicket("TKT-005"));
        List<Ticket> tickets = ticketDAO.findByRequesterId(testUser.getId());
        assertEquals(2, tickets.size());
    }

    @Test
    void status_ile_ticketlar_filtrelenmeli() {
        ticketDAO.save(createTestTicket("TKT-006"));
        List<Ticket> newTickets = ticketDAO.findByStatus(TicketStatus.NEW);
        assertFalse(newTickets.isEmpty());
        newTickets.forEach(t -> assertEquals(TicketStatus.NEW, t.getStatus()));
    }

    @Test
    void ticket_guncellenmeli() {
        Ticket saved = ticketDAO.save(createTestTicket("TKT-007"));
        saved.setTitle("Guncellenmis Baslik");
        saved.setStatus(TicketStatus.IN_PROGRESS);
        ticketDAO.update(saved);
        Optional<Ticket> updated = ticketDAO.findById(saved.getId());
        assertTrue(updated.isPresent());
        assertEquals("Guncellenmis Baslik", updated.get().getTitle());
        assertEquals(TicketStatus.IN_PROGRESS, updated.get().getStatus());
    }

    @Test
    void ticket_silinmeli() {
        Ticket saved = ticketDAO.save(createTestTicket("TKT-008"));
        ticketDAO.deleteById(saved.getId());
        assertFalse(ticketDAO.findById(saved.getId()).isPresent());
    }
}
