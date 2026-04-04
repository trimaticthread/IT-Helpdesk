package com.helpdesk.persistence.dao;

import com.helpdesk.domain.entity.User;
import java.util.List;
import java.util.Optional;

public interface UserDAO {

    Optional<User> findById(Long id);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);

    List<User> findAll();
    List<User> findByGroupId(Long groupId);

    User save(User user);

    void update(User user);
    void deleteById(Long id);

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    void assignRole(Long Id, String roleName);
}
