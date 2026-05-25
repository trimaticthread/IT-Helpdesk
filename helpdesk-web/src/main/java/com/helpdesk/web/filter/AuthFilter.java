package com.helpdesk.web.filter;

import com.helpdesk.web.util.SessionUtil;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * ── AuthFilter ───────────────────────────────────────────────────────────────
 *
 * Kimlik doğrulama filtresi.
 * Giriş yapılmadan korumalı sayfalara erişimi engeller, /login'e yönlendirir.
 *
 * Nasıl çalışır?
 *   Her HTTP isteği bu filter'dan geçer (WebConfig'de "/*" pattern'ine atandı).
 *   1. İstenen URL herkese açık bir URL mi? (/login, /logout, /static)
 *      → Evet: isteği geçir, kontrol yapma.
 *   2. Kullanıcı giriş yapmış mı? (session'da UserDTO var mı?)
 *      → Evet: geçir.
 *      → Hayır: /login'e yönlendir.
 *
 * WebConfig'de order=2 ile tanımlandı → EncodingFilter'dan sonra çalışır.
 * ─────────────────────────────────────────────────────────────────────────────
 */
// Spring bean DEĞİL — WebConfig içinde new AuthFilter() ile oluşturuluyor
public class AuthFilter implements Filter { // implements Filter ŞART — Spring bunu Filter olarak tanır

    /**
     * Giriş yapılmadan erişilebilecek URL'ler.
     * Bu listedeki URL'lere session kontrolü uygulanmaz.
     */
    private static final List<String> PUBLIC_URLS = List.of(
            "/login",   // giriş sayfası ve formu
            "/logout",  // çıkış işlemi
            "/static"   // CSS, JS, resim gibi statik dosyalar
    );

    /**
     * Her HTTP isteğinde otomatik çalışır.
     *
     * Parametre isimleri kasıtlı: servletRequest ve servletResponse genel tiplerdir.
     * HTTP'ye özgü metodlara (getRequestURI, sendRedirect) erişmek için
     * bunları HttpServletRequest / HttpServletResponse'a cast etmek gerekir.
     *
     * @param servletRequest  Gelen istek (genel Servlet tipi)
     * @param servletResponse Gönderilecek yanıt (genel Servlet tipi)
     * @param chain           Sonraki filter veya Servlet'e geçişi sağlar
     */
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                         FilterChain chain) throws IOException, ServletException {

        // HTTP'ye özgü metodları kullanabilmek için cast ediyoruz
        HttpServletRequest  request  = (HttpServletRequest)  servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse; // sendRedirect burada

        // Tarayıcının istediği tam URL yolu (örn: "/dashboard", "/login")
        String uri = request.getRequestURI();

        // İstenen URL, herkese açık listede var mı?
        boolean isPublic = PUBLIC_URLS.stream().anyMatch(uri::startsWith);

        if (isPublic || SessionUtil.isLoggedIn(request)) {
            // Herkese açık URL veya giriş yapılmış → zinciri devam ettir
            chain.doFilter(request, response);
        } else {
            // Giriş yapılmamış ve korumalı sayfa → login'e yönlendir
            // sendRedirect sadece HttpServletResponse'da var — bu yüzden cast şarttı
            response.sendRedirect(request.getContextPath() + "/login");
        }
    }
}
