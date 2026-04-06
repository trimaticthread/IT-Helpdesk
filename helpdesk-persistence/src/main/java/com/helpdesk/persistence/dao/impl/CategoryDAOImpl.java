package com.helpdesk.persistence.dao.impl;

import com.helpdesk.domain.entity.Category;
import com.helpdesk.persistence.dao.CategoryDAO;
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
 * {@link CategoryDAO} arayüzünün JdbcTemplate tabanlı gerçekleştirimi.
 *
 * Kullanılan tablo: {@code categories}
 *
 * Tasarım notları:
 * - Tüm sorgular parametreli PreparedStatement kullanır; SQL injection riski yoktur.
 * - {@code save()} metodunda üretilen anahtar {@code GeneratedKeyHolder} ile alınır;
 *   null kontrol edilerek {@link IllegalStateException} fırlatılır.
 * - {@code findAllActive()} yalnızca {@code is_active = true} kayıtları getirir;
 *   ticket formu dropdown'larında kullanılır.
 */
@Repository
public class CategoryDAOImpl implements CategoryDAO {

    private final JdbcTemplate jdbcTemplate;

    public CategoryDAOImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * ResultSet satırını {@link Category} entity'sine dönüştürür.
     */
    private static class CategoryRowMapper implements RowMapper<Category> {
        @Override
        public Category mapRow(ResultSet rs, int rowNum) throws SQLException {
            Category category = new Category();
            category.setId(rs.getLong("id"));
            category.setName(rs.getString("name"));
            category.setDescription(rs.getString("description"));
            category.setIsActive(rs.getBoolean("is_active"));
            return category;
        }
    }

    @Override
    public Optional<Category> findById(Long id) {
        String sql = "SELECT * FROM categories WHERE id = ?";
        List<Category> result = jdbcTemplate.query(sql, new CategoryRowMapper(), id);
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    @Override
    public Optional<Category> findByName(String name) {
        String sql = "SELECT * FROM categories WHERE name = ?";
        List<Category> result = jdbcTemplate.query(sql, new CategoryRowMapper(), name);
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    @Override
    public List<Category> findAll() {
        String sql = "SELECT * FROM categories";
        return jdbcTemplate.query(sql, new CategoryRowMapper());
    }

    @Override
    public List<Category> findAllActive() {
        String sql = "SELECT * FROM categories WHERE is_active = true";
        return jdbcTemplate.query(sql, new CategoryRowMapper());
    }

    @Override
    public Category save(Category category) {
        String sql = "INSERT INTO categories (name, description, is_active) VALUES (?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, category.getName());
            ps.setString(2, category.getDescription());
            ps.setBoolean(3, category.getIsActive());
            return ps;
        }, keyHolder);
        Number key = keyHolder.getKey();
        if (key == null) throw new IllegalStateException("Category kaydedildi ama id alinamadi.");
        category.setId(key.longValue());
        return category;
    }

    @Override
    public void update(Category category) {
        String sql = "UPDATE categories SET name=?, description=?, is_active=? WHERE id=?";
        jdbcTemplate.update(sql,
                category.getName(),
                category.getDescription(),
                category.getIsActive(),
                category.getId());
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM categories WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }
}
