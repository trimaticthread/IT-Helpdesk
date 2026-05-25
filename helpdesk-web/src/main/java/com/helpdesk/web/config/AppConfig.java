package com.helpdesk.web.config;

import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.File;

/*
 * ── AppConfig ─────────────────────────────────────────────────────────────────
 *
 * Uygulama genelinde kullanılan Spring Bean'lerinin tanımlandığı konfigürasyon sınıfı.
 *
 * @Configuration → Spring, uygulama başlarken bu sınıfı tarar ve içindeki @Bean
 * metodlarını çalıştırarak nesneleri Spring Container'a kaydeder.
 *
 * @Bean nedir?
 *   Spring'in yönettiği bir nesnedir (singleton). Başka sınıflar bu nesneye ihtiyaç
 *   duyduğunda Spring otomatik olarak inject eder — new ile elle oluşturmak gerekmez.
 * ─────────────────────────────────────────────────────────────────────────────
 */
@Configuration
public class AppConfig {

    /*
     * BCryptPasswordEncoder bean'i oluşturur.
     *
     * BCrypt nedir?
     *   Şifreleri düz metin saklamak güvensizdir. BCrypt şifreyi tek yönlü hash'e çevirir.
     *   Giriş sırasında: kullanıcının girdiği şifre → hash'e çevrilir → DB'deki hash ile karşılaştırılır.
     *   Aynı şifre her hash'lendiğinde farklı sonuç üretir (salt) ama doğrulama yine çalışır.
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /*
     * ── Tomcat Document Root Düzeltmesi ───────────────────────────────────────
     *
     * SORUN:
     *   Embedded Tomcat JSP dosyalarını "document root" dizininde arar.
     *   Spring Boot, document root'u çalışma dizinine (working directory) göre belirler.
     *
     *   IntelliJ'den çalıştırınca çalışma dizini: IT-Helpdesk/          ← proje kökü
     *   Maven spring-boot:run ile çalıştırınca:   IT-Helpdesk/helpdesk-web/  ← modül dizini
     *
     *   JSP dosyaları: helpdesk-web/src/main/webapp/WEB-INF/jsp/...
     *
     *   IntelliJ'den çalıştırınca Tomcat, "IT-Helpdesk/src/main/webapp/" arıyor ama
     *   bu dizin yok → JSP bulunamıyor → 404.
     *
     * ÇÖZÜM:
     *   WebServerFactoryCustomizer → Tomcat başlamadan önce document root'u açıkça set ediyoruz.
     *   İki konumu dener:
     *   1. "helpdesk-web/src/main/webapp" → IntelliJ proje kökünden çalıştırınca
     *   2. "src/main/webapp"              → Maven spring-boot:run ile helpdesk-web/ içinden
     */
    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> tomcatDocumentRootCustomizer() {
        return factory -> {

            // IntelliJ genellikle proje kökünü (IT-Helpdesk/) çalışma dizini yapar
            File webappFromRoot = new File("helpdesk-web/src/main/webapp");

            // Maven spring-boot:run, helpdesk-web/ dizininden çalışır
            File webappFromModule = new File("src/main/webapp");

            if (webappFromRoot.exists() && webappFromRoot.isDirectory()) {
                // IntelliJ'den çalıştırma → proje kökünden buldu
                factory.setDocumentRoot(webappFromRoot);
            } else if (webappFromModule.exists() && webappFromModule.isDirectory()) {
                // Maven'dan çalıştırma → modül dizininden buldu
                factory.setDocumentRoot(webappFromModule);
            }
            // Hiçbiri bulunamazsa Spring Boot kendi varsayılan ayarlarını kullanır
        };
    }
}
