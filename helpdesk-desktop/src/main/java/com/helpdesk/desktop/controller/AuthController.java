package com.helpdesk.desktop.controller;

import com.helpdesk.application.dto.UserDTO;
import com.helpdesk.application.service.AuthService;
import com.helpdesk.desktop.security.SessionManager;
import org.springframework.stereotype.Component;
import java.util.Optional;

/**
 * Kimlik dogrulama islemlerini UI katmanindan servis katmanina aktaran kopru sinif.
 * Swing view'lari AuthService'i dogrudan bilmez; bu controller araci gorevi gorur.
 *
 * Akis:
 * - login()  : AuthService.login() cagirilir. Basarili olursa SessionManager'a
 *              kullanici kaydedilir ve true dondurulur.
 * - logout() : SessionManager.logout() ile aktif oturum temizlenir.
 */
@Component
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    public boolean login(String username, String password) {
        Optional<UserDTO> result = authService.login(username, password);
        result.ifPresent(SessionManager::login);
        return result.isPresent();
    }

    public void logout() {
        SessionManager.logout();
    }
}
