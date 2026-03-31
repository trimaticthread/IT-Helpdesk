package com.helpdesk.application.service.impl;

import com.helpdesk.application.dto.UserDTO;
import com.helpdesk.application.mapper.UserMapper;
import com.helpdesk.application.service.AuthService;
import com.helpdesk.domain.entity.User;
import com.helpdesk.persistence.dao.UserDAO;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserDAO userDAO;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthServiceImpl(UserDAO userDAO, BCryptPasswordEncoder passwordEncoder) {
        this.userDAO = userDAO;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Optional<UserDTO> login(String username, String rawPassword) {
        Optional<User> userOpt = userDAO.findByUsername(username);
        if (userOpt.isEmpty()) return Optional.empty();

        User user = userOpt.get();
        if (!user.getIsActive()) return Optional.empty();
        if (!passwordEncoder.matches(rawPassword, user.getPasswordHash())) return Optional.empty();

        return Optional.of(UserMapper.toDTO(user));
    }
}
