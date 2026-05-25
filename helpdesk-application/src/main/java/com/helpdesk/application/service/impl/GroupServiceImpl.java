package com.helpdesk.application.service.impl;

import com.helpdesk.application.service.GroupService;
import com.helpdesk.domain.entity.Group;
import com.helpdesk.domain.entity.User;
import com.helpdesk.domain.exception.BusinessException;
import com.helpdesk.persistence.dao.GroupDAO;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class GroupServiceImpl implements GroupService {

    private final GroupDAO groupDAO;

    public GroupServiceImpl(GroupDAO groupDAO) {
        this.groupDAO = groupDAO;
    }

    @Override
    public List<Group> getAllGroups() {
        return groupDAO.findAll();
    }

    @Override
    public Group createGroup(String name, String description, String email) {
        if (name == null || name.isBlank()) {
            throw new BusinessException("Group name cannot be empty.");
        }
        if (groupDAO.findByName(name.trim()).isPresent()) {
            throw new BusinessException("A group with this name already exists.");
        }
        Group group = new Group();
        group.setName(name.trim());
        group.setDescription(description != null ? description.trim() : "");
        group.setEmail(email != null ? email.trim() : "");
        group.setIsActive(true);
        return groupDAO.save(group);
    }

    @Override
    public void addUserToGroup(Long groupId, Long userId) {
        groupDAO.findById(groupId)
                .orElseThrow(() -> new BusinessException("Group not found: " + groupId));
        groupDAO.addUserToGroup(groupId, userId);
    }

    @Override
    public void removeUserFromGroup(Long groupId, Long userId) {
        groupDAO.removeUserFromGroup(groupId, userId);
    }

    @Override
    public void deleteGroup(Long id) {
        if (groupDAO.countUsersByGroupId(id) > 0) {
            throw new BusinessException("Cannot delete: this group has members. Remove all users first.");
        }
        groupDAO.deleteById(id);
    }

    @Override
    public List<User> getUsersInGroup(Long groupId) {
        return groupDAO.findUsersByGroupId(groupId);
    }
}
