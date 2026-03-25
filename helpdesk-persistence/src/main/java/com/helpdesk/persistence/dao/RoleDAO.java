package com.helpdesk.persistence.dao;

import com.helpdesk.domain.entity.Role;
import java.util.List;
import java.util.Optional;

public interface RoleDAO {

    Optional<Role> findById(Long id);
    Optional<Role> findByName(String name);

    List<Role> findAll();
    List<Role> findByUserId(Long userId);

    Role save(Role role);

    void update(Role role);
    void deleteById(Long id);

}
