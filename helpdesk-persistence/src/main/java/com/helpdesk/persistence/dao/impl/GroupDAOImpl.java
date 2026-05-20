package com.helpdesk.persistence.dao.impl;

import com.helpdesk.domain.entity.Group;
import com.helpdesk.domain.entity.User;
import com.helpdesk.persistence.dao.GroupDAO;
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
public class GroupDAOImpl implements GroupDAO {

    private final JdbcTemplate jdbcTemplate;

    public GroupDAOImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static class GroupRowMapper implements RowMapper<Group> {
        @Override
        public Group mapRow(ResultSet rs, int rowNum) throws SQLException {
            Group g = new Group();
            g.setId(rs.getLong("id"));
            g.setName(rs.getString("name"));
            g.setDescription(rs.getString("description"));
            g.setEmail(rs.getString("email"));
            g.setIsActive(rs.getBoolean("is_active"));
            return g;
        }
    }

    private static class UserRowMapper implements RowMapper<User> {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User u = new User();
            u.setId(rs.getLong("id"));
            u.setUsername(rs.getString("username"));
            u.setFirstName(rs.getString("first_name"));
            u.setLastName(rs.getString("last_name"));
            u.setEmail(rs.getString("email"));
            u.setDepartment(rs.getString("department"));
            u.setIsActive(rs.getBoolean("is_active"));
            return u;
        }
    }

    @Override
    public List<Group> findAll() {
        return jdbcTemplate.query("SELECT * FROM groups_ ORDER BY name", new GroupRowMapper());
    }

    @Override
    public Optional<Group> findById(Long id) {
        List<Group> result = jdbcTemplate.query("SELECT * FROM groups_ WHERE id = ?", new GroupRowMapper(), id);
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    @Override
    public Optional<Group> findByName(String name) {
        List<Group> result = jdbcTemplate.query("SELECT * FROM groups_ WHERE name = ?", new GroupRowMapper(), name);
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    @Override
    public Group save(Group group) {
        String sql = "INSERT INTO groups_ (name, description, email, is_active) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, group.getName());
            ps.setString(2, group.getDescription());
            ps.setString(3, group.getEmail());
            ps.setBoolean(4, group.getIsActive() != null ? group.getIsActive() : true);
            return ps;
        }, keyHolder);
        Number key = keyHolder.getKey();
        if (key == null) throw new IllegalStateException("Group kaydedildi ama id alinamadi.");
        group.setId(key.longValue());
        return group;
    }

    @Override
    public void update(Group group) {
        jdbcTemplate.update("UPDATE groups_ SET name=?, description=?, email=?, is_active=? WHERE id=?",
                group.getName(), group.getDescription(), group.getEmail(), group.getIsActive(), group.getId());
    }

    @Override
    public void deleteById(Long id) {
        jdbcTemplate.update("DELETE FROM groups_ WHERE id = ?", id);
    }

    @Override
    public List<User> findUsersByGroupId(Long groupId) {
        String sql = "SELECT u.* FROM users u JOIN group_users gu ON u.id = gu.user_id WHERE gu.group_id = ?";
        return jdbcTemplate.query(sql, new UserRowMapper(), groupId);
    }

    @Override
    public void addUserToGroup(Long groupId, Long userId) {
        jdbcTemplate.update("INSERT IGNORE INTO group_users (group_id, user_id) VALUES (?, ?)", groupId, userId);
    }

    @Override
    public void removeUserFromGroup(Long groupId, Long userId) {
        jdbcTemplate.update("DELETE FROM group_users WHERE group_id = ? AND user_id = ?", groupId, userId);
    }

    @Override
    public int countUsersByGroupId(Long groupId) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM group_users WHERE group_id = ?", Integer.class, groupId);
        return count != null ? count : 0;
    }
}
