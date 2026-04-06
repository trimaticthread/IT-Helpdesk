package com.helpdesk.application.service.impl;

import com.helpdesk.application.dto.UserDTO;
import com.helpdesk.application.mapper.UserMapper;
import com.helpdesk.application.service.AuthService;
import com.helpdesk.domain.entity.User;
import com.helpdesk.persistence.dao.UserDAO;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

/**
 * Kimlik dogrulama servisinin gerceklemesi.
 * Kullanici girisi dogrulamasi ve oturum baslatma islemlerini yonetir.
 *
 * Calisma mantigi:
 * 1. Kullanici adi ile veritabanindan kullanici aranir.
 * 2. Hesabin aktif olup olmadigi kontrol edilir.
 * 3. BCrypt ile ham sifre, veritabanindaki hash ile karsilastirilir.
 * 4. Basarili giriste kullanicinin rolu UserDAO.getRoleName() ile yuklenir.
 * 5. Rol bilgisi dahil UserDTO olusturulup dondurulur.
 *
 * Guvenlik: Ham sifre hicbir zaman loglanmaz veya kismem saklanmaz.
 *           BCryptPasswordEncoder tek yonlu hash kullanir.
 */
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

        UserDTO dto = UserMapper.toDTO(user);
        dto.setRole(userDAO.getRoleName(user.getId()));
        return Optional.of(dto);
    }
}
