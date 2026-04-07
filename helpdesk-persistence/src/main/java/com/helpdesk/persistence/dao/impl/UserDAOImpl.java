package com.helpdesk.persistence.dao.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.helpdesk.domain.entity.User;
import com.helpdesk.persistence.dao.UserDAO;

/**
 * UserDAO arayuzunun Spring JdbcTemplate tabanli gerceklemesi. Tum kullanici
 * CRUD islemlerini raw SQL ile MySQL veritabaninda yurutur.
 *
 * Ic sinif: - UserRowMapper: ResultSet satirini User entity'sine donusturur.
 *
 * Onemli Notlar: - deleteById : Once user_roles'dan siler, sonra users'dan.
 * MySQL foreign key kisitlamasi bu siralamayi zorunlu kilar. - assignRole :
 * roles tablosunda name ile arama yapip user_roles'a ekler. - getRoleName : Bir
 * kullanicinin ilk rolunu dondurur (LIMIT 1). Coklu rol gerekirse liste donecek
 * sekilde genisletilebilir. - save : GeneratedKeyHolder ile INSERT sonrasi
 * otomatik id alir.
 */
@Repository
public class UserDAOImpl implements UserDAO {

    private final JdbcTemplate jdbcTemplate;

    public UserDAOImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static class UserRowMapper implements RowMapper<User> {

        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User user = new User();
            user.setId(rs.getLong("id"));
            user.setUsername(rs.getString("username"));
            user.setEmail(rs.getString("email"));
            user.setPasswordHash(rs.getString("password_hash"));
            user.setFirstName(rs.getString("first_name"));
            user.setLastName(rs.getString("last_name"));
            user.setPhone(rs.getString("phone"));
            user.setDepartment(rs.getString("department"));
            user.setIsActive(rs.getBoolean("is_active"));
            user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            user.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
            return user;
        }
    }

    @Override
    public Optional<User> findById(Long id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        List<User> result = jdbcTemplate.query(sql, new UserRowMapper(), id);
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    @Override
    public Optional<User> findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        List<User> result = jdbcTemplate.query(sql, new UserRowMapper(), username);
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        List<User> result = jdbcTemplate.query(sql, new UserRowMapper(), email);
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    @Override
    public List<User> findAll() {
        String sql = "SELECT * FROM users";
        return jdbcTemplate.query(sql, new UserRowMapper());
    }

    @Override
    public List<User> findByGroupId(Long groupId) {
        String sql = "SELECT u.* FROM users u JOIN group_users gu ON u.id = gu.user_id WHERE gu.group_id = ?";
        return jdbcTemplate.query(sql, new UserRowMapper(), groupId);
    }

    @Override
    public User save(User user) {
        String sql = "INSERT INTO users (username, email, password_hash, first_name, last_name, phone, department, is_active, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPasswordHash());
            ps.setString(4, user.getFirstName());
            ps.setString(5, user.getLastName());
            ps.setString(6, user.getPhone());
            ps.setString(7, user.getDepartment());
            ps.setBoolean(8, user.getIsActive());
            return ps;
        }, keyHolder);
        Number key = keyHolder.getKey();
        if (key == null) {
            throw new IllegalStateException("User kaydedildi ama id alinamadi.");
        }
        user.setId(key.longValue());
        return user;
    }

    @Override
    public void update(User user) {
        String sql = "UPDATE users SET username=?, email=?, first_name=?, last_name=?, phone=?, department=?, is_active=?, updated_at=NOW() WHERE id=?";
        jdbcTemplate.update(sql,
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhone(),
                user.getDepartment(),
                user.getIsActive(),
                user.getId());
    }

    @Override
    public void deleteById(Long id) {
        // FK silme sırası: comments → tickets (assignee null) → tickets (requester) → user_roles → users
        // 1. Kullanıcının yazdığı yorumları sil
        jdbcTemplate.update("DELETE FROM comments WHERE author_id = ?", id);
        // 2. Kullanıcının açtığı ticketlara ait yorumları sil, sonra ticketları sil
        jdbcTemplate.update("DELETE FROM comments WHERE ticket_id IN (SELECT id FROM tickets WHERE requester_id = ?)", id);
        jdbcTemplate.update("DELETE FROM tickets WHERE requester_id = ?", id);
        // 3. Kullanıcıya atanmış ticketlardan atamasını kaldır (silme değil — ticket korunur)
        jdbcTemplate.update("UPDATE tickets SET assignee_id = NULL WHERE assignee_id = ?", id);
        // 4. Rol bağlantısını sil, ardından kullanıcıyı sil
        jdbcTemplate.update("DELETE FROM user_roles WHERE user_id = ?", id);
        jdbcTemplate.update("DELETE FROM users WHERE id = ?", id);
    }

    @Override
    public boolean existsByUsername(String username) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, username);
        return count != null && count > 0;
    }

    @Override
    public boolean existsByEmail(String email) {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, email);
        return count != null && count > 0;
    }

    @Override
    public void assignRole(Long Id, String roleName) {
        String sql = "INSERT INTO user_roles (user_id, role_id) SELECT ?, id FROM roles WHERE name = ?";
        jdbcTemplate.update(sql, Id, roleName);
    }

    @Override
    public String getRoleName(Long userId) {
        String sql = "SELECT r.name FROM roles r JOIN user_roles ur ON r.id = ur.role_id WHERE ur.user_id = ? LIMIT 1";
        List<String> results = jdbcTemplate.queryForList(sql, String.class, userId);
        return results.isEmpty() ? null : results.get(0);
    }

    @Override
    public List<User> findByRoleName(String roleName) {
        // user_roles ve roles tabloları join edilerek verilen role sahip aktif kullanıcılar döner
        String sql = "SELECT u.* FROM users u " +
                     "JOIN user_roles ur ON u.id = ur.user_id " +
                     "JOIN roles r ON r.id = ur.role_id " +
                     "WHERE r.name = ? AND u.is_active = true";
        return jdbcTemplate.query(sql, new UserRowMapper(), roleName);
    }

}
