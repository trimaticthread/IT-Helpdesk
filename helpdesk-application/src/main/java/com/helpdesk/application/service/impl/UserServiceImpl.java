package com.helpdesk.application.service.impl;

import com.helpdesk.application.dto.UserDTO;
import com.helpdesk.application.mapper.UserMapper;
import com.helpdesk.application.service.UserService;
import com.helpdesk.domain.entity.User;
import com.helpdesk.domain.exception.ResourceNotFoundException;
import com.helpdesk.persistence.dao.UserDAO;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserDAO userDAO;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserServiceImpl(UserDAO userDAO, BCryptPasswordEncoder passwordEncoder) {
        this.userDAO = userDAO;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Optional<UserDTO> findById(Long id) {
        return userDAO.findById(id).map(UserMapper::toDTO);
    }

    @Override
    public Optional<UserDTO> findByUsername(String username) {
        return userDAO.findByUsername(username).map(UserMapper::toDTO);
    }

    @Override
    public List<UserDTO> findAll() {
        return userDAO.findAll().stream()
                .map(UserMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UserDTO save(UserDTO dto, String rawPassword) {
        User user = UserMapper.toEntity(dto);
        user.setPasswordHash(passwordEncoder.encode(rawPassword));
        user.setIsActive(true);
        User saved = userDAO.save(user);
        return UserMapper.toDTO(saved);
    }

    @Override
    public void update(UserDTO dto) {
        userDAO.findById(dto.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Kullanici bulunamadi: " + dto.getId()));
        userDAO.update(UserMapper.toEntity(dto));
    }

    @Override
    public void deleteById(Long id) {
        userDAO.deleteById(id);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userDAO.existsByUsername(username);
    }
}
