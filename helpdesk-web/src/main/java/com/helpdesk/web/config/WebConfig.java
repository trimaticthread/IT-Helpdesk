package com.helpdesk.web.config;

import com.helpdesk.application.service.PasswordResetRequestService;
import com.helpdesk.web.filter.AuthFilter;
import com.helpdesk.web.filter.EncodingFilter;
import com.helpdesk.web.interceptor.AdminInterceptor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * ── WebConfig ────────────────────────────────────────────────────────────────
 *
 * Filter'ların Spring Boot'a tanıtıldığı konfigürasyon sınıfı.
 *
 * NEDEN BURADA SADECE FILTER VAR, SERVLET YOK?
 *   Servlet'ler (@WebServlet anotasyonuyla) doğrudan Tomcat'e kaydediliyor.
 *   WebApplication.java'daki @ServletComponentScan bu işi halleder.
 *   Filter'lar ise FilterRegistrationBean ile kaydedilir — bu yaklaşım
 *   Filter'lara çalışma sırası (order) vermemizi sağlar.
 *
 * NEDEN @Component YOK Filter sınıflarında?
 *   @Component olan bir sınıf Spring tarafından bean olarak yönetilir.
 *   Aynı sınıfı bir de FilterRegistrationBean ile kaydetmeye çalışırsak
 *   Spring iki farklı yerde aynı nesneyi kaydetmeye çalışır → çakışma.
 *   Çözüm: Filter'ları burada new ile oluştur, tam kontrol burada olsun.
 *
 * NASIL ÇALIŞIR?
 *   Spring Boot başlarken bu @Bean metodlarını çalıştırır.
 *   FilterRegistrationBean → Tomcat'e "her istekte bu Filter'ı çalıştır" der.
 * ─────────────────────────────────────────────────────────────────────────────
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final PasswordResetRequestService resetService;

    public WebConfig(PasswordResetRequestService resetService) {
        this.resetService = resetService;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AdminInterceptor(resetService));
    }

    // ── FİLTER KAYITLARI ─────────────────────────────────────────────────────

    /**
     * EncodingFilter → tüm URL'lere ("/*") uygulanır, order=1 (ilk çalışan).
     *
     * Spring bağımlılığı yok → new ile oluşturulur.
     * Her istekte UTF-8 encoding garantilenir.
     */
    @Bean // Spring bu metodu çalıştırır ve dönen nesneyi yönetir
    public FilterRegistrationBean<EncodingFilter> encodingFilterRegistration() {
        FilterRegistrationBean<EncodingFilter> reg = new FilterRegistrationBean<>(new EncodingFilter());
        reg.addUrlPatterns("/*"); // Tüm URL'lere uygula
        reg.setOrder(1);          // İlk çalışan filter — encoding önce ayarlanmalı
        return reg;
    }

    /**
     * AuthFilter → tüm URL'lere ("/*") uygulanır, order=2 (encoding'den sonra).
     *
     * Spring bağımlılığı yok → new ile oluşturulur.
     * Giriş yapılmamış kullanıcıları /login'e yönlendirir.
     * /login, /logout, /static URL'lerine uygulanmaz (PUBLIC_URLS).
     */
    @Bean
    public FilterRegistrationBean<AuthFilter> authFilterRegistration() {
        FilterRegistrationBean<AuthFilter> reg = new FilterRegistrationBean<>(new AuthFilter());
        reg.addUrlPatterns("/*"); // Tüm URL'lere uygula
        reg.setOrder(2);          // EncodingFilter'dan sonra çalışır
        return reg;
    }
}
