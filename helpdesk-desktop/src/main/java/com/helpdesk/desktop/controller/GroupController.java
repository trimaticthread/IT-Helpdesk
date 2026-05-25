package com.helpdesk.desktop.controller;

import com.helpdesk.application.service.GroupService;
import com.helpdesk.domain.entity.Group;
import com.helpdesk.domain.entity.User;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class GroupController {

    private final GroupService groupService;

    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    public List<Group> getAllGroups() {
        return groupService.getAllGroups();
    }

    public Group createGroup(String name, String description, String email) {
        return groupService.createGroup(name, description, email);
    }

    public void addUserToGroup(Long groupId, Long userId) {
        groupService.addUserToGroup(groupId, userId);
    }

    public void removeUserFromGroup(Long groupId, Long userId) {
        groupService.removeUserFromGroup(groupId, userId);
    }

    public void deleteGroup(Long id) {
        groupService.deleteGroup(id);
    }

    public List<User> getUsersInGroup(Long groupId) {
        return groupService.getUsersInGroup(groupId);
    }
}
