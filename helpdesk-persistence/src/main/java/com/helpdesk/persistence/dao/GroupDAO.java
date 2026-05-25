package com.helpdesk.persistence.dao;

import com.helpdesk.domain.entity.Group;
import com.helpdesk.domain.entity.User;
import java.util.List;
import java.util.Optional;

public interface GroupDAO {
    List<Group> findAll();
    Optional<Group> findById(Long id);
    Optional<Group> findByName(String name);
    Group save(Group group);
    void update(Group group);
    void deleteById(Long id);
    List<User> findUsersByGroupId(Long groupId);
    void addUserToGroup(Long groupId, Long userId);
    void removeUserFromGroup(Long groupId, Long userId);
    int countUsersByGroupId(Long groupId);
}
