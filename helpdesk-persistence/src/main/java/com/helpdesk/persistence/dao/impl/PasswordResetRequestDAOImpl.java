package com.helpdesk.persistence.dao.impl;

import com.helpdesk.domain.entity.PasswordResetRequest;
import com.helpdesk.persistence.dao.PasswordResetRequestDAO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public class PasswordResetRequestDAOImpl implements PasswordResetRequestDAO {

    private final JdbcTemplate jdbcTemplate;

    public PasswordResetRequestDAOImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final String SELECT_WITH_USER =
        "SELECT r.id, r.user_id, r.status, r.created_at, r.resolved_at, " +
        "u.username, u.first_name, u.last_name, u.email " +
        "FROM password_reset_requests r JOIN users u ON r.user_id = u.id ";

    @Override
    public void create(Long userId) {
        jdbcTemplate.update(
            "INSERT INTO password_reset_requests (user_id, status) VALUES (?, 'PENDING')",
            userId
        );
    }

    @Override
    public List<PasswordResetRequest> findPending() {
        return jdbcTemplate.query(
            SELECT_WITH_USER + "WHERE r.status = 'PENDING' ORDER BY r.created_at DESC",
            new RequestRowMapper()
        );
    }

    @Override
    public Optional<PasswordResetRequest> findById(Long id) {
        List<PasswordResetRequest> result = jdbcTemplate.query(
            SELECT_WITH_USER + "WHERE r.id = ?",
            new RequestRowMapper(), id
        );
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    @Override
    public void approve(Long id) {
        jdbcTemplate.update(
            "UPDATE password_reset_requests SET status='APPROVED', resolved_at=NOW() WHERE id=?",
            id
        );
    }

    @Override
    public boolean hasPendingRequest(Long userId) {
        Integer count = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM password_reset_requests WHERE user_id=? AND status='PENDING'",
            Integer.class, userId
        );
        return count != null && count > 0;
    }

    @Override
    public int countPending() {
        Integer count = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM password_reset_requests WHERE status='PENDING'",
            Integer.class
        );
        return count != null ? count : 0;
    }

    private static class RequestRowMapper implements RowMapper<PasswordResetRequest> {
        @Override
        public PasswordResetRequest mapRow(ResultSet rs, int rowNum) throws SQLException {
            PasswordResetRequest r = new PasswordResetRequest();
            r.setId(rs.getLong("id"));
            r.setUserId(rs.getLong("user_id"));
            r.setStatus(rs.getString("status"));
            r.setUsername(rs.getString("username"));
            r.setFirstName(rs.getString("first_name"));
            r.setLastName(rs.getString("last_name"));
            r.setEmail(rs.getString("email"));
            Timestamp created = rs.getTimestamp("created_at");
            if (created != null) r.setCreatedAt(created.toLocalDateTime());
            Timestamp resolved = rs.getTimestamp("resolved_at");
            if (resolved != null) r.setResolvedAt(resolved.toLocalDateTime());
            return r;
        }
    }
}
