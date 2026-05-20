package com.helpdesk.application.service;

import com.helpdesk.application.dto.UserDTO;
import java.util.List;
import java.util.Optional;

public interface UserService {

    Optional<UserDTO> findById(Long id);

    Optional<UserDTO> findByUsername(String username);

    List<UserDTO> findAll();

    UserDTO save(UserDTO dto, String rawPassword);

    void update(UserDTO dto);

    void deleteById(Long id);

    boolean existsByUsername(String username);

    UserDTO createUser(UserDTO dto, String rawPassword, String roleName);

    /** Verilen role sahip aktif kullanicilari dondurur. */
    List<UserDTO> findByRole(String roleName);

    /** Şifreyi Welcome@1234 yapar, flag'i true'ya çeker. */
    void resetPassword(Long userId);

    /** Yeni şifreyi kaydeder, flag'i false'a çeker. */
    void changePassword(Long userId, String newPassword);

    boolean isPasswordResetRequired(Long userId);
}
