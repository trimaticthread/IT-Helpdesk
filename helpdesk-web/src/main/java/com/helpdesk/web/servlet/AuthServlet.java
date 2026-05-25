package com.helpdesk.web.servlet;

import com.helpdesk.application.dto.UserDTO;
import com.helpdesk.application.service.AuthService;
import com.helpdesk.application.service.UserService;
import com.helpdesk.web.util.SessionUtil;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import java.io.IOException;
import java.util.Optional;

/**
 * ── AuthServlet ──────────────────────────────────────────────────────────────
 *
 * /login ve /logout URL'lerini yöneten Servlet.
 *
 * GET  /login  → Kullanıcı tarayıcıda /login açtı → login.jsp'yi göster
 * POST /login  → Kullanıcı formu doldurdu ve gönderdi → kimlik doğrula, yönlendir
 * GET  /logout → Kullanıcı çıkış yaptı → session sil, /login'e gönder
 *
 * Login başarılı akış:
 *   Form → AuthService.login() → UserDTO → SessionUtil.setUser() → /dashboard
 *
 * Login başarısız akış:
 *   Form → AuthService.login() → boş → hata mesajı → login.jsp tekrar gösterilir
 *
 * @WebServlet → Bu sınıfı Tomcat'e "servlet" olarak kaydeder.
 *   WebApplication'daki @ServletComponentScan bu anotasyonu tarar ve işler.
 *   urlPatterns = {"/login", "/logout"} → bu iki URL bu Servlet'e gelir.
 *
 * NEDEN init() metodu var?
 *   @WebServlet sınıfları Spring bean'i değildir — Spring @Autowired yapamaz.
 *   Spring bean'lerine erişmek için init() içinde WebApplicationContext'ten
 *   manuel olarak alıyoruz.
 * ─────────────────────────────────────────────────────────────────────────────
 */
// NOT: Bu sınıf artık kullanılmıyor. Aktif implementasyon: AuthController.java
// @WebServlet anotasyonu kaldırıldı — Spring Boot'ta DispatcherServlet'i geçemiyordu.
public class AuthServlet extends HttpServlet {

    // Kullanıcı adı + şifreyi doğrular, UserDTO döner
    private AuthService authService;

    // Giriş sonrası şifre sıfırlama zorunluluğunu kontrol eder
    private UserService userService;

    /**
     * Servlet ilk kez çağrılmadan önce Tomcat bu metodu çalıştırır.
     *
     * @WebServlet, constructor injection desteklemez — Spring yönetimli bir bean değil.
     * Bu yüzden Spring Application Context'e manuel erişip gerekli servisleri çekiyoruz.
     *
     * WebApplicationContextUtils → Spring'in uygulama bağlamını (context) ServletContext'ten bulur.
     * getBean(AuthService.class) → "AuthService tipinde bir bean var mı, ver bana" der.
     */
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config); // Tomcat'in kendi init işlemlerini yaptır, sonra biz ekleriz

        // Spring Application Context'e eriş — Tomcat'in ServletContext'i üzerinden bulunur
        WebApplicationContext ctx =
                WebApplicationContextUtils.getRequiredWebApplicationContext(config.getServletContext());

        // Spring context'ten servisleri al ve field'lara ata
        this.authService = ctx.getBean(AuthService.class);
        this.userService = ctx.getBean(UserService.class);
    }

    /**
     * GET isteği → URL'e bakarak /login veya /logout işlemini ayırt eder.
     *
     * /login  → login sayfasını göster (zaten giriş yapılmışsa dashboard'a at)
     * /logout → session'ı sil, login'e yönlendir
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String uri = req.getRequestURI();

        // ── /logout ──────────────────────────────────────────────────────────
        if (uri.endsWith("/logout")) {
            SessionUtil.invalidate(req); // session'ı ve içindeki kullanıcı bilgisini sil
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        // ── /login ───────────────────────────────────────────────────────────
        // Zaten giriş yapılmışsa login sayfasını tekrar gösterme
        if (SessionUtil.isLoggedIn(req)) {
            resp.sendRedirect(req.getContextPath() + "/dashboard");
            return;
        }

        // Giriş yapılmamış → login.jsp'yi göster
        // forward: URL değişmez, aynı istek içinde JSP'ye geçilir
        req.getRequestDispatcher("/WEB-INF/jsp/auth/login.jsp").forward(req, resp);
    }

    /**
     * POST /login → Kullanıcı formu doldurup gönderdi, kimlik doğrula.
     *
     * Adımlar:
     *   1. Formdan kullanıcı adı + şifre al
     *   2. Boş alan kontrolü
     *   3. AuthService ile doğrula (BCrypt hash karşılaştırması burada)
     *   4. Hesap aktif mi kontrolü
     *   5. Session'a kaydet
     *   6. Şifre sıfırlama zorunluysa /change-password'a, değilse /dashboard'a yönlendir
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // Formdan gelen değerleri al
        String username = req.getParameter("username");
        String password = req.getParameter("password");

        // ── Boş alan kontrolü ────────────────────────────────────────────────
        if (username == null || username.trim().isEmpty() ||
                password == null || password.trim().isEmpty()) {
            req.setAttribute("error", "Username and password are required.");
            req.getRequestDispatcher("/WEB-INF/jsp/auth/login.jsp").forward(req, resp);
            return; // devam etme, aşağıdaki kodu çalıştırma
        }

        try {
            // ── Kimlik doğrulama ─────────────────────────────────────────────
            // authService içeride DB'den kullanıcıyı bulur, BCrypt ile şifre karşılaştırır
            // Bulunamazsa veya şifre yanlışsa Optional.empty() döner
            Optional<UserDTO> result = authService.login(username.trim(), password);

            if (result.isEmpty()) {
                // Kullanıcı yok veya şifre yanlış — hangisi olduğunu söyleme (güvenlik)
                req.setAttribute("error", "Invalid username or password.");
                req.getRequestDispatcher("/WEB-INF/jsp/auth/login.jsp").forward(req, resp);
                return;
            }

            UserDTO user = result.get(); // Optional'dan gerçek UserDTO'yu çıkar

            // ── Hesap aktif mi? ──────────────────────────────────────────────
            // Admin hesabı devre dışı bırakmışsa giriş engellenir
            if (!Boolean.TRUE.equals(user.getIsActive())) {
                req.setAttribute("error", "Your account is disabled. Contact your administrator.");
                req.getRequestDispatcher("/WEB-INF/jsp/auth/login.jsp").forward(req, resp);
                return;
            }

            // ── Session'a kaydet ─────────────────────────────────────────────
            // Artık bu kullanıcı giriş yapmış sayılır
            SessionUtil.setUser(req, user);

            // ── Şifre sıfırlama zorunlu mu? ──────────────────────────────────
            // Admin "Reset Password" yapmışsa kullanıcı önce şifresini değiştirmeli
            if (userService.isPasswordResetRequired(user.getId())) {
                resp.sendRedirect(req.getContextPath() + "/change-password");
            } else {
                resp.sendRedirect(req.getContextPath() + "/dashboard");
            }

        } catch (Exception e) {
            // Beklenmedik bir hata oldu — stack trace kullanıcıya gösterilmez
            req.setAttribute("error", "An error occurred. Please try again.");
            req.getRequestDispatcher("/WEB-INF/jsp/auth/login.jsp").forward(req, resp);
        }
    }
}
