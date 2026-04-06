package com.helpdesk.persistence.dao.impl;

import com.helpdesk.domain.entity.Category;
import com.helpdesk.domain.entity.Group;
import com.helpdesk.domain.entity.Ticket;
import com.helpdesk.domain.entity.User;
import com.helpdesk.domain.enums.TicketPriority;
import com.helpdesk.domain.enums.TicketStatus;
import com.helpdesk.persistence.dao.TicketDAO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

/**
 * {@link TicketDAO} arayüzünün JdbcTemplate tabanlı gerçekleştirimi.
 *
 * Kullanılan tablo: {@code tickets}
 * İlişkili tablolar: {@code users} (requester/assignee), {@code categories}, {@code groups_}
 *
 * Tasarım notları:
 * - RowMapper yalnızca ID'leri set eder (shallow load); tam ad / kategori adı gibi
 *   ilişki alanları {@link com.helpdesk.application.mapper.TicketMapper} tarafından
 *   servis katmanında JOIN veya ayrı sorgu ile doldurulur.
 * - assignee_id ve group_id nullable — rs.wasNull() ile kontrol edilir.
 * - Tüm listeleme sorguları {@code ORDER BY created_at DESC} ile en yeni önce gelir.
 * - {@code save()} metodunda üretilen anahtar {@code GeneratedKeyHolder} ile alınır;
 *   null kontrol edilerek {@link IllegalStateException} fırlatılır.
 */
@Repository
public class TicketDAOImpl implements TicketDAO {

    private final JdbcTemplate jdbcTemplate;

    public TicketDAOImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * ResultSet satırını {@link Ticket} entity'sine dönüştürür.
     * Nullable alanlar (assignee_id, group_id, timestamp'lar) null kontrolü
     * ile set edilir; NPE riski yoktur.
     */
    private static class TicketRowMapper implements RowMapper<Ticket> {
        @Override
        public Ticket mapRow(ResultSet rs, int rowNum) throws SQLException {
            Ticket ticket = new Ticket();
            ticket.setId(rs.getLong("id"));
            ticket.setTicketNumber(rs.getString("ticket_number"));
            ticket.setTitle(rs.getString("title"));
            ticket.setDescription(rs.getString("description"));
            ticket.setStatus(TicketStatus.valueOf(rs.getString("status")));
            ticket.setPriority(TicketPriority.valueOf(rs.getString("priority")));

            if (rs.getTimestamp("created_at") != null)
                ticket.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            if (rs.getTimestamp("updated_at") != null)
                ticket.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
            if (rs.getTimestamp("resolved_at") != null)
                ticket.setResolvedAt(rs.getTimestamp("resolved_at").toLocalDateTime());
            if (rs.getTimestamp("closed_at") != null)
                ticket.setClosedAt(rs.getTimestamp("closed_at").toLocalDateTime());
            if (rs.getTimestamp("sla_due_date") != null)
                ticket.setSlaDueDate(rs.getTimestamp("sla_due_date").toLocalDateTime());

            User requester = new User();
            requester.setId(rs.getLong("requester_id"));
            ticket.setRequester(requester);

            long assigneeId = rs.getLong("assignee_id");
            if (!rs.wasNull()) {
                User assignee = new User();
                assignee.setId(assigneeId);
                ticket.setAssignee(assignee);
            }

            Category category = new Category();
            category.setId(rs.getLong("category_id"));
            ticket.setCategory(category);

            long groupId = rs.getLong("group_id");
            if (!rs.wasNull()) {
                Group group = new Group();
                group.setId(groupId);
                ticket.setGroup(group);
            }

            return ticket;
        }
    }

    @Override
    public Optional<Ticket> findById(Long id) {
        String sql = "SELECT * FROM tickets WHERE id = ?";
        List<Ticket> result = jdbcTemplate.query(sql, new TicketRowMapper(), id);
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    @Override
    public Optional<Ticket> findByTicketNumber(String ticketNumber) {
        String sql = "SELECT * FROM tickets WHERE ticket_number = ?";
        List<Ticket> result = jdbcTemplate.query(sql, new TicketRowMapper(), ticketNumber);
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    @Override
    public List<Ticket> findAll() {
        String sql = "SELECT * FROM tickets ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, new TicketRowMapper());
    }

    @Override
    public List<Ticket> findByRequesterId(Long requesterId) {
        String sql = "SELECT * FROM tickets WHERE requester_id = ? ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, new TicketRowMapper(), requesterId);
    }

    @Override
    public List<Ticket> findByAssigneeId(Long assigneeId) {
        String sql = "SELECT * FROM tickets WHERE assignee_id = ? ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, new TicketRowMapper(), assigneeId);
    }

    @Override
    public List<Ticket> findByStatus(TicketStatus status) {
        String sql = "SELECT * FROM tickets WHERE status = ? ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, new TicketRowMapper(), status.name());
    }

    @Override
    public List<Ticket> findByGroupId(Long groupId) {
        String sql = "SELECT * FROM tickets WHERE group_id = ? ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, new TicketRowMapper(), groupId);
    }

    @Override
    public List<Ticket> findByCategoryId(Long categoryId) {
        String sql = "SELECT * FROM tickets WHERE category_id = ? ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, new TicketRowMapper(), categoryId);
    }

    @Override
    public Ticket save(Ticket ticket) {
        String sql = "INSERT INTO tickets (ticket_number, title, description, status, priority, category_id, requester_id, assignee_id, group_id, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, ticket.getTicketNumber());
            ps.setString(2, ticket.getTitle());
            ps.setString(3, ticket.getDescription());
            ps.setString(4, ticket.getStatus().name());
            ps.setString(5, ticket.getPriority().name());
            ps.setLong(6, ticket.getCategory().getId());
            ps.setLong(7, ticket.getRequester().getId());
            if (ticket.getAssignee() != null) ps.setLong(8, ticket.getAssignee().getId());
            else ps.setNull(8, java.sql.Types.BIGINT);
            if (ticket.getGroup() != null) ps.setLong(9, ticket.getGroup().getId());
            else ps.setNull(9, java.sql.Types.BIGINT);
            return ps;
        }, keyHolder);
        Number key = keyHolder.getKey();
        if (key == null) throw new IllegalStateException("Ticket kaydedildi ama id alinamadi.");
        ticket.setId(key.longValue());
        return ticket;
    }

    @Override
    public void update(Ticket ticket) {
        String sql = "UPDATE tickets SET title=?, description=?, status=?, priority=?, category_id=?, assignee_id=?, group_id=?, updated_at=NOW() WHERE id=?";
        jdbcTemplate.update(sql,
                ticket.getTitle(),
                ticket.getDescription(),
                ticket.getStatus().name(),
                ticket.getPriority().name(),
                ticket.getCategory().getId(),
                ticket.getAssignee() != null ? ticket.getAssignee().getId() : null,
                ticket.getGroup() != null ? ticket.getGroup().getId() : null,
                ticket.getId());
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM tickets WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }
}
