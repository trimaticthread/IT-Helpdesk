package com.helpdesk.application.service;

import com.helpdesk.domain.entity.Group;
import com.helpdesk.domain.entity.User;
import java.util.List;

public interface GroupService {
    List<Group> getAllGroups();
    Group createGroup(String name, String description, String email);
    void addUserToGroup(Long groupId, Long userId);
    void removeUserFromGroup(Long groupId, Long userId);
    void deleteGroup(Long id);
    List<User> getUsersInGroup(Long groupId);
}
