package com.helpdesk.persistence.dao.impl;

import com.helpdesk.domain.entity.Attachment;
import com.helpdesk.domain.entity.Ticket;
import com.helpdesk.domain.entity.User;
import com.helpdesk.persistence.dao.AttachmentDAO;
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

@Repository
public class AttachmentDAOImpl implements AttachmentDAO {

    private final JdbcTemplate jdbcTemplate;

    public AttachmentDAOImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static class AttachmentRowMapper implements RowMapper<Attachment> {
        @Override
        public Attachment mapRow(ResultSet rs, int rowNum) throws SQLException {
            Attachment attachment = new Attachment();
            attachment.setId(rs.getLong("id"));
            attachment.setFilename(rs.getString("filename"));
            attachment.setFilePath(rs.getString("file_path"));
            attachment.setFileSize(rs.getLong("file_size"));
            attachment.setMimeType(rs.getString("mime_type"));
            attachment.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());

            Ticket ticket = new Ticket();
            ticket.setId(rs.getLong("ticket_id"));
            attachment.setTicket(ticket);

            User uploadedBy = new User();
            uploadedBy.setId(rs.getLong("uploaded_by"));
            attachment.setUploadedBy(uploadedBy);

            return attachment;
        }
    }

    @Override
    public Optional<Attachment> findById(Long id) {
        String sql = "SELECT * FROM attachments WHERE id = ?";
        List<Attachment> result = jdbcTemplate.query(sql, new AttachmentRowMapper(), id);
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    @Override
    public List<Attachment> findByTicketId(Long ticketId) {
        String sql = "SELECT * FROM attachments WHERE ticket_id = ?";
        return jdbcTemplate.query(sql, new AttachmentRowMapper(), ticketId);
    }

    @Override
    public List<Attachment> findByUploadedBy(Long userId) {
        String sql = "SELECT * FROM attachments WHERE uploaded_by = ?";
        return jdbcTemplate.query(sql, new AttachmentRowMapper(), userId);
    }

    @Override
    public Attachment save(Attachment attachment) {
        String sql = "INSERT INTO attachments (ticket_id, filename, file_path, file_size, mime_type, uploaded_by, created_at) VALUES (?, ?, ?, ?, ?, ?, NOW())";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, attachment.getTicket().getId());
            ps.setString(2, attachment.getFilename());
            ps.setString(3, attachment.getFilePath());
            ps.setLong(4, attachment.getFileSize());
            ps.setString(5, attachment.getMimeType());
            ps.setLong(6, attachment.getUploadedBy().getId());
            return ps;
        }, keyHolder);
        attachment.setId(keyHolder.getKey().longValue());
        return attachment;
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM attachments WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }
}
