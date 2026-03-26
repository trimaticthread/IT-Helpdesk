package com.helpdesk.persistence.dao.impl;

import com.helpdesk.domain.entity.Comment;
import com.helpdesk.domain.entity.Ticket;
import com.helpdesk.domain.entity.User;
import com.helpdesk.persistence.dao.CommentDAO;
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
public class CommentDAOImpl implements CommentDAO {

    private final JdbcTemplate jdbcTemplate;

    public CommentDAOImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static class CommentRowMapper implements RowMapper<Comment> {
        @Override
        public Comment mapRow(ResultSet rs, int rowNum) throws SQLException {
            Comment comment = new Comment();
            comment.setId(rs.getLong("id"));
            comment.setContent(rs.getString("content"));
            comment.setIsInternal(rs.getBoolean("is_internal"));
            comment.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());

            Ticket ticket = new Ticket();
            ticket.setId(rs.getLong("ticket_id"));
            comment.setTicket(ticket);

            User author = new User();
            author.setId(rs.getLong("author_id"));
            comment.setAuthor(author);

            return comment;
        }
    }

    @Override
    public Optional<Comment> findById(Long id) {
        String sql = "SELECT * FROM comments WHERE id = ?";
        List<Comment> result = jdbcTemplate.query(sql, new CommentRowMapper(), id);
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    @Override
    public List<Comment> findByTicketId(Long ticketId) {
        String sql = "SELECT * FROM comments WHERE ticket_id = ? ORDER BY created_at ASC";
        return jdbcTemplate.query(sql, new CommentRowMapper(), ticketId);
    }

    @Override
    public List<Comment> findInternalByTicketId(Long ticketId) {
        String sql = "SELECT * FROM comments WHERE ticket_id = ? AND is_internal = true ORDER BY created_at ASC";
        return jdbcTemplate.query(sql, new CommentRowMapper(), ticketId);
    }

    @Override
    public Comment save(Comment comment) {
        String sql = "INSERT INTO comments (ticket_id, author_id, content, is_internal, created_at) VALUES (?, ?, ?, ?, NOW())";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, comment.getTicket().getId());
            ps.setLong(2, comment.getAuthor().getId());
            ps.setString(3, comment.getContent());
            ps.setBoolean(4, comment.getIsInternal());
            return ps;
        }, keyHolder);
        comment.setId(keyHolder.getKey().longValue());
        return comment;
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM comments WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }
}
