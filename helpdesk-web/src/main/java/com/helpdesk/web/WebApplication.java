package com.helpdesk.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * ── WebApplication ───────────────────────────────────────────────────────────
 *
 * Spring Boot uygulamasının başlangıç noktası (entry point).
 *
 * @SpringBootApplication → üç anotasyonu birden taşır:
 *   @Configuration      : Bu sınıf da bir konfigürasyon kaynağıdır
 *   @EnableAutoConfiguration : Spring Boot otomatik ayarları açar (Tomcat, DB vs.)
 *   @ComponentScan      : "com.helpdesk" altındaki tüm @Component, @Service, @Repository bean'lerini tarar
 *
 * scanBasePackages = "com.helpdesk" → sadece helpdesk-web değil,
 *   helpdesk-application ve helpdesk-persistence modüllerindeki @Service, @Repository vs. de taranır.
 *   Yoksa AuthService, UserService gibi bean'ler bulunamaz → uygulama başlamaz.
 *
 * MİMARİ NOT:
 *   HTTP istekleri DispatcherServlet tarafından yakalanır ("/" pattern).
 *   @Controller sınıfları DispatcherServlet içinde çalışır → sorunsuz yönlendirme.
 *   View çözümlemesi application.properties'teki prefix/suffix ile yapılır:
 *   /WEB-INF/jsp/  +  view-adı  +  .jsp
 * ─────────────────────────────────────────────────────────────────────────────
 */
@SpringBootApplication(scanBasePackages = "com.helpdesk")
public class WebApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebApplication.class, args);
    }
}
