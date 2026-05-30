package com.helpdesk.web.controller;

import com.helpdesk.application.dto.UserDTO;
import com.helpdesk.application.service.AuthService;
import com.helpdesk.application.service.UserService;
import com.helpdesk.web.util.SessionUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

/**
 * ── AuthController ───────────────────────────────────────────────────────────
 *
 * /login ve /logout URL'lerini yöneten Spring MVC Controller.
 *
 * @Controller → Spring bu sınıfı bulur, DispatcherServlet'e "bu sınıfı kullan" der.
 *   DispatcherServlet zaten tüm HTTP isteklerini yakalıyor.
 *   @GetMapping("/login") → GET /login gelince bu metodu çağır, demektir.
 *
 * NEDEN SERVLET DEĞİL DE CONTROLLER?
 *   Spring Boot'ta DispatcherServlet HER şeyi yakalar ("/").
 *   Custom Servlet oluşturup "beni de kaydet" demek hem karmaşık hem hatalı.
 *   @Controller kullanırsak DispatcherServlet'in içinde çalışırız → sorunsuz.
 *
 * VIEW DÖNÜŞÜ NASIL ÇALIŞIR?
 *   return "auth/login"  →  application.properties'teki prefix+suffix eklenir:
 *   /WEB-INF/jsp/  +  auth/login  +  .jsp  =  /WEB-INF/jsp/auth/login.jsp
 *
 *   return "redirect:/dashboard"  →  tarayıcıya "302 Found, git /dashboard'a" yanıtı döner.
 *
 * MODEL NASIL ÇALIŞIR?
 *   Model.addAttribute("error", "...")  →  JSP'de ${error} ile okunur.
 *   Servlet'teki req.setAttribute() ile aynı şey, sadece daha temiz.
 * ─────────────────────────────────────────────────────────────────────────────
 */
@Controller // Spring bu sınıfı HTTP controller olarak tanır
public class AuthController {

    // Kullanıcı adı + şifreyi doğrular, UserDTO döner
    private final AuthService authService;

    // Giriş sonrası şifre sıfırlama zorunluluğunu kontrol eder
    private final UserService userService;

    /**
     * Spring constructor injection — new yazmaya gerek yok, Spring otomatik inject eder.
     * @Controller Spring bean'idir, bu yüzden @Autowired/constructor injection çalışır.
     */
    public AuthController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    // ── GET /login ────────────────────────────────────────────────────────────

    /**
     * Kullanıcı tarayıcıdan /login adresini açtı.
     *
     * Zaten giriş yapmışsa dashboard'a yönlendir.
     * Yapmamışsa login sayfasını göster.
     *
     * @return "auth/login"  →  /WEB-INF/jsp/auth/login.jsp gösterilir
     *         "redirect:/dashboard"  →  tarayıcı /dashboard'a yönlendirilir
     */
    @GetMapping("/login") // GET isteği + /login URL'i → bu metodu çalıştır
    public String loginPage(HttpServletRequest req) {

        // Zaten giriş yapmışsa login sayfasını tekrar gösterme
        if (SessionUtil.isLoggedIn(req)) {
            return "redirect:/dashboard"; // 302 yönlendirme
        }

        // Giriş yapılmamış → login sayfasını göster
        return "auth/login"; // /WEB-INF/jsp/auth/login.jsp
    }

    // ── POST /login ───────────────────────────────────────────────────────────

    /**
     * Kullanıcı login formunu doldurup "Giriş Yap" butonuna bastı.
     *
     * @param username  Formdan gelen kullanıcı adı (@RequestParam → form alanından otomatik alır)
     * @param password  Formdan gelen şifre
     * @param req       Session'a kullanıcıyı kaydetmek için
     * @param model     JSP'ye veri göndermek için (hata mesajı, prefill vs.)
     * @return          Başarılıysa redirect, başarısızsa login sayfası tekrar
     */
    @PostMapping("/login") // POST isteği + /login URL'i → bu metodu çalıştır
    public String login(
            @RequestParam(required = false) String username, // form field'dan otomatik alınır
            @RequestParam(required = false) String password,
            HttpServletRequest req,
            Model model) { // Model → JSP'ye veri taşır (req.setAttribute() gibi)

        // ── Boş alan kontrolü ────────────────────────────────────────────────
        if (username == null || username.trim().isEmpty() ||
                password == null || password.trim().isEmpty()) {
            model.addAttribute("error", "Username and password are required.");
            model.addAttribute("username", username); // formu prefill et
            return "auth/login"; // login sayfasını tekrar göster
        }

        try {
            // ── Kimlik doğrulama ─────────────────────────────────────────────
            // authService DB'den kullanıcıyı bulur, BCrypt ile şifre karşılaştırır
            // Bulunamazsa veya şifre yanlışsa Optional.empty() döner
            Optional<UserDTO> result = authService.login(username.trim(), password);

            if (result.isEmpty()) {
                // Kullanıcı yok veya şifre yanlış — hangisini söyleme (güvenlik)
                model.addAttribute("error", "Invalid username or password.");
                model.addAttribute("username", username);
                return "auth/login";
            }

            UserDTO user = result.get(); // Optional'dan gerçek UserDTO'yu çıkar

            // ── Hesap aktif mi? ──────────────────────────────────────────────
            if (!Boolean.TRUE.equals(user.getIsActive())) {
                model.addAttribute("error", "Your account is disabled. Contact your administrator.");
                model.addAttribute("username", username);
                return "auth/login";
            }

            // ── Session'a kaydet ─────────────────────────────────────────────
            SessionUtil.setUser(req, user);

            // ── Şifre sıfırlama zorunlu mu? ──────────────────────────────────
            if (userService.isPasswordResetRequired(user.getId())) {
                req.getSession().setAttribute("passwordResetRequired", true);
                return "redirect:/change-password";
            } else {
                req.getSession().removeAttribute("passwordResetRequired");
                return "redirect:/dashboard";
            }

        } catch (Exception e) {
            // Beklenmedik hata — stack trace kullanıcıya gösterilmez
            model.addAttribute("error", "An error occurred. Please try again.");
            return "auth/login";
        }
    }

    // ── GET /logout ───────────────────────────────────────────────────────────

    /**
     * Kullanıcı "Çıkış Yap" butonuna bastı.
     * Session'ı sil, login sayfasına yönlendir.
     */
    @GetMapping("/logout") // GET /logout → bu metodu çalıştır
    public String logout(HttpServletRequest req) {
        SessionUtil.invalidate(req); // session'ı ve içindeki kullanıcı bilgisini sil
        return "redirect:/login";    // 302 → tarayıcı /login'e gider
    }
}
