package com.helpdesk.desktop.controller;

import com.helpdesk.application.dto.UserDTO;
import com.helpdesk.application.service.UserService;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    public List<UserDTO> getAllUsers() {
        return userService.findAll();
    }

    public UserDTO createUser(String username, String email, String password,
                              String firstName, String lastName, String department, String roleName) {
        UserDTO dto = new UserDTO();
        dto.setUsername(username);
        dto.setEmail(email);
        dto.setFirstName(firstName);
        dto.setLastName(lastName);
        dto.setDepartment(department);
        return userService.createUser(dto, password, roleName);
    }
}
