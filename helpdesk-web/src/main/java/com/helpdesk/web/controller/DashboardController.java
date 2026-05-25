package com.helpdesk.web.controller;

import com.helpdesk.application.dto.UserDTO;
import com.helpdesk.web.util.SessionUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * ── DashboardController ──────────────────────────────────────────────────────
 *
 * /dashboard URL'ini yöneten Spring MVC Controller.
 *
 * Kullanıcının rolüne göre doğru dashboard view'ini döner:
 *   ADMIN      → "admin/dashboard"      →  /WEB-INF/jsp/admin/dashboard.jsp
 *   SUPERVISOR → "supervisor/dashboard" →  /WEB-INF/jsp/supervisor/dashboard.jsp
 *   AGENT      → "agent/dashboard"      →  /WEB-INF/jsp/agent/dashboard.jsp
 *   CUSTOMER   → "customer/dashboard"   →  /WEB-INF/jsp/customer/dashboard.jsp
 *
 * AuthFilter zaten giriş yapılmamış kullanıcıları /login'e gönderiyor.
 * Yine de null kontrolü var — savunmacı programlama (defensive programming).
 * ─────────────────────────────────────────────────────────────────────────────
 */
@Controller // Spring bu sınıfı HTTP controller olarak tanır
public class DashboardController {

    /**
     * GET /dashboard → Kullanıcının rolüne göre uygun JSP'yi döndür.
     *
     * return "admin/dashboard" demek:
     *   /WEB-INF/jsp/ + admin/dashboard + .jsp = /WEB-INF/jsp/admin/dashboard.jsp
     *
     * return "redirect:/login" demek:
     *   302 yönlendirme → tarayıcı /login'e gider
     */
    @GetMapping("/dashboard") // GET /dashboard → bu metodu çalıştır
    public String dashboard(HttpServletRequest req) {

        // Session'dan giriş yapmış kullanıcıyı al
        UserDTO user = SessionUtil.getUser(req);

        // Kullanıcı yoksa (session süresi dolmuşsa) login'e at
        if (user == null) {
            return "redirect:/login";
        }

        // Kullanıcının rolüne göre doğru JSP'yi seç
        switch (user.getRole()) {
            case "ADMIN":
                return "admin/dashboard";       // /WEB-INF/jsp/admin/dashboard.jsp

            case "SUPERVISOR":
                return "supervisor/dashboard";  // /WEB-INF/jsp/supervisor/dashboard.jsp

            case "AGENT":
                return "agent/dashboard";       // /WEB-INF/jsp/agent/dashboard.jsp

            case "CUSTOMER":
                return "customer/dashboard";    // /WEB-INF/jsp/customer/dashboard.jsp

            default:
                // Tanımlanmamış rol — erişimi engelle
                return "redirect:/access-denied";
        }
    }
}
