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

    /** Kullanicinin ilk giriste sifre degistirmesi gerekip gerekmedigini dondurur. */
    boolean isPasswordResetRequired(Long userId);

    /** Kullanicinin sifresini yeni deger ile gunceller. */
    void changePassword(Long userId, String newRawPassword);

    /**
     * Kullanicinin sifresini Welcome@1234 olarak sifirlar ve
     * bir sonraki giriste degistirme zorunlulugunu aktiflestirir.
     */
    void resetPassword(Long userId);
}
