package com.helpdesk.application.service;

import com.helpdesk.application.dto.UserDTO;
import java.util.Optional;

public interface AuthService {

    Optional<UserDTO> login(String username, String rawPassword);
}
