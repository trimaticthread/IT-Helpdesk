package com.helpdesk.web.util;

import com.helpdesk.application.dto.UserDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/**
 * ── SessionUtil ──────────────────────────────────────────────────────────────
 *
 * HTTP Session işlemlerini tek yerden yöneten yardımcı sınıftır.
 *
 * Session nedir?
 *   Tarayıcı her isteği bağımsız gönderir — sunucu "bu istek kim?" diye bilemez.
 *   Session bu sorunu çözer: sunucu kullanıcıya gizli bir kimlik (JSESSIONID cookie)
 *   verir ve bu kimliğe bağlı hafıza alanında veri saklar.
 *   Biz bu hafıza alanına kullanıcının UserDTO'sunu koyuyoruz.
 *
 * Neden bu sınıf var?
 *   Her Servlet'te session.getAttribute("currentUser") yazmak yerine
 *   SessionUtil.getUser(req) diyerek tek satırda bitiriyoruz.
 *   "currentUser" key'i tek yerde (USER_KEY sabiti) tanımlı — değişmesi gerekirse
 *   sadece buraya dokunmak yeterli.
 *
 * ÖNEMLİ KURAL: Session'a SADECE UserDTO girer. User entity'si asla girmez.
 * ────────────────────────────────────────────────────────────────────────────
 */
public class SessionUtil {

    // Session'da kullanıcı bilgisini sakladığımız anahtar kelime.
    // Tüm sınıflar bu sabit üzerinden erişir — string'i elle yazmak hata kaynağıdır.
    private static final String USER_KEY = "currentUser";

    /**
     * Giriş yapan kullanıcıyı session'a kaydeder.
     * AuthServlet login başarılı olduğunda bu metodu çağırır.
     *
     * @param request HTTP isteği — session buradan alınır
     * @param user    Kaydedilecek kullanıcı bilgisi (DTO)
     */
    public static void setUser(HttpServletRequest request, UserDTO user) {
        request.getSession().setAttribute(USER_KEY, user);
    }

    /**
     * Session'daki kullanıcıyı döndürür.
     * Giriş yapılmamışsa null döner.
     *
     * @param request HTTP isteği
     * @return Oturumdaki UserDTO, giriş yoksa null
     */
    public static UserDTO getUser(HttpServletRequest request) {
        // getAttribute her zaman Object döner — UserDTO'ya cast ediyoruz
        return (UserDTO) request.getSession().getAttribute(USER_KEY);
    }

    /**
     * Kullanıcının giriş yapıp yapmadığını kontrol eder.
     * AuthFilter bu metodu kullanarak korumalı sayfalara erişimi denetler.
     *
     * @param request HTTP isteği
     * @return true → giriş yapılmış | false → giriş yapılmamış
     */
    public static boolean isLoggedIn(HttpServletRequest request) {
        // Kullanıcı session'da varsa giriş yapılmış demektir
        return getUser(request) != null;
    }

    /**
     * Session'ı tamamen geçersiz kılar (logout işlemi).
     * Session ve içindeki tüm veriler silinir.
     *
     * getSession(false) → session yoksa null döner, yeni oluşturmaz.
     * getSession(true)  → session yoksa yeni oluşturur. Logout'ta istemiyoruz.
     *
     * @param request HTTP isteği
     */
    public static void invalidate(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate(); // session'ı ve içindeki her şeyi sil
        }
    }

    /**
     * Oturumdaki kullanıcının belirtilen role sahip olup olmadığını kontrol eder.
     * Servlet'ler yetkisiz erişimi engellemek için bunu kullanır.
     *
     * Örnek: SessionUtil.hasRole(req, "ADMIN") → sadece admin true alır
     *
     * @param request HTTP isteği
     * @param role    Kontrol edilecek rol ("ADMIN", "AGENT", "SUPERVISOR", "CUSTOMER")
     * @return true → kullanıcı bu role sahip | false → değil veya giriş yapılmamış
     */
    public static boolean hasRole(HttpServletRequest request, String role) {
        UserDTO user = getUser(request);
        // Kullanıcı yoksa (null) → false. Varsa rolü verilen rolle karşılaştır.
        return user != null && role.equals(user.getRole());
    }
}
