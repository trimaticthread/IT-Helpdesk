package com.helpdesk.persistence.dao.impl;

import com.helpdesk.domain.entity.SlaSettings;
import com.helpdesk.domain.enums.TicketPriority;
import com.helpdesk.persistence.dao.SlaDAO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class SlaDAOImpl implements SlaDAO {

    private final JdbcTemplate jdbcTemplate;

    public SlaDAOImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static class SlaRowMapper implements RowMapper<SlaSettings> {
        @Override
        public SlaSettings mapRow(ResultSet rs, int rowNum) throws SQLException {
            SlaSettings s = new SlaSettings();
            s.setId(rs.getLong("id"));
            s.setPriority(TicketPriority.valueOf(rs.getString("priority")));
            s.setResponseTimeMinutes(rs.getInt("response_time_minutes"));
            s.setResolutionTimeMinutes(rs.getInt("resolution_time_minutes"));
            return s;
        }
    }

    @Override
    public List<SlaSettings> findAll() {
        return jdbcTemplate.query(
            "SELECT * FROM sla_settings ORDER BY FIELD(priority,'CRITICAL','HIGH','MEDIUM','LOW')",
            new SlaRowMapper());
    }

    @Override
    public Optional<SlaSettings> findByPriority(TicketPriority priority) {
        List<SlaSettings> result = jdbcTemplate.query(
            "SELECT * FROM sla_settings WHERE priority = ?", new SlaRowMapper(), priority.name());
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    @Override
    public void update(SlaSettings s) {
        jdbcTemplate.update(
            "UPDATE sla_settings SET response_time_minutes=?, resolution_time_minutes=? WHERE priority=?",
            s.getResponseTimeMinutes(), s.getResolutionTimeMinutes(), s.getPriority().name());
    }
}
