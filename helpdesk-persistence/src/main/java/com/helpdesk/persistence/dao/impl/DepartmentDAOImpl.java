package com.helpdesk.persistence.dao.impl;

import com.helpdesk.domain.entity.Department;
import com.helpdesk.persistence.dao.DepartmentDAO;
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
public class DepartmentDAOImpl implements DepartmentDAO {

    private final JdbcTemplate jdbcTemplate;

    public DepartmentDAOImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static class DepartmentRowMapper implements RowMapper<Department> {
        @Override
        public Department mapRow(ResultSet rs, int rowNum) throws SQLException {
            Department d = new Department();
            d.setId(rs.getLong("id"));
            d.setName(rs.getString("name"));
            d.setIsActive(rs.getBoolean("is_active"));
            return d;
        }
    }

    @Override
    public List<Department> findAll() {
        return jdbcTemplate.query("SELECT * FROM departments ORDER BY name", new DepartmentRowMapper());
    }

    @Override
    public List<Department> findAllActive() {
        return jdbcTemplate.query("SELECT * FROM departments WHERE is_active = true ORDER BY name", new DepartmentRowMapper());
    }

    @Override
    public Optional<Department> findById(Long id) {
        List<Department> result = jdbcTemplate.query("SELECT * FROM departments WHERE id = ?", new DepartmentRowMapper(), id);
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    @Override
    public Optional<Department> findByName(String name) {
        List<Department> result = jdbcTemplate.query("SELECT * FROM departments WHERE name = ?", new DepartmentRowMapper(), name);
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    @Override
    public Department save(Department department) {
        String sql = "INSERT INTO departments (name, is_active) VALUES (?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, department.getName());
            ps.setBoolean(2, department.getIsActive());
            return ps;
        }, keyHolder);
        Number key = keyHolder.getKey();
        if (key == null) throw new IllegalStateException("Department kaydedildi ama id alinamadi.");
        department.setId(key.longValue());
        return department;
    }

    @Override
    public void update(Department department) {
        jdbcTemplate.update("UPDATE departments SET name=?, is_active=? WHERE id=?",
                department.getName(), department.getIsActive(), department.getId());
    }

    @Override
    public void deleteById(Long id) {
        jdbcTemplate.update("DELETE FROM departments WHERE id = ?", id);
    }

    @Override
    public int countUsersByDepartmentName(String name) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM users WHERE department = ?", Integer.class, name);
        return count != null ? count : 0;
    }
}
