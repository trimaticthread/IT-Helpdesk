package com.helpdesk.persistence.dao.impl;

import com.helpdesk.domain.entity.Role;
import com.helpdesk.persistence.dao.RoleDAO;
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
public class RoleDAOImpl implements RoleDAO {

    private final JdbcTemplate jdbcTemplate;

    public RoleDAOImpl(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    private static class RoleRowMapper implements RowMapper<Role>{
        @Override
        public Role mapRow(ResultSet rs, int rowNum) throws SQLException{
            Role role = new Role();
            role.setId(rs.getLong("id"));
            role.setName(rs.getString("name"));
            role.setDescription(rs.getString("description"));
            role.setIsActive(rs.getBoolean("is_active"));
            return role;
        }
    }

    @Override
    public Optional<Role> findById(Long id){
        String sql = "SELECT * FROM roles WHERE id = ?";
        List<Role> result = jdbcTemplate.query(sql, new RoleRowMapper(), id);
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    @Override
    public Optional<Role> findByName(String name){
        String sql = "SELECT * FROM roles WHERE name = ?";
        List<Role> result = jdbcTemplate.query(sql, new RoleRowMapper(), name);
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    @Override
    public List<Role> findAll(){
        String sql = "SELECT * FROM roles";
        return jdbcTemplate.query(sql, new RoleRowMapper());
    }

    @Override
    public List<Role> findByUserId(Long userId) {
        String sql = "SELECT r.* FROM roles r JOIN user_roles ur ON r.id = ur.role_id WHERE ur.user_id = ?";
        return jdbcTemplate.query(sql, new RoleRowMapper(), userId);
    }

    @Override
    public Role save(Role role) {
        String sql = "INSERT INTO roles (name, description, is_active) VALUES (?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, role.getName());
            ps.setString(2, role.getDescription());
            ps.setBoolean(3, role.getIsActive());
            return ps;
        }, keyHolder);
        role.setId(keyHolder.getKey().longValue());
        return role;
    }

    @Override
    public void update(Role role){
        String sql = "UPDATE roles SET name=?, description = ?, is_active = ? WHERE id = ?";
        jdbcTemplate.update(sql, role.getName(), role.getDescription(), role.getIsActive(), role.getId());
    }

    @Override
    public void deleteById(Long Id){
        String sql = "DELETE FROM roles WHERE id = ?";
        jdbcTemplate.update(sql, Id);
    }
}
