package com.helpdesk.application.service;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class BcryptTest {
    @Test
    void printHash() {
        BCryptPasswordEncoder enc = new BCryptPasswordEncoder();
        String hash = enc.encode("password");
        System.out.println("SPRING_HASH=" + hash);
        System.out.println("VERIFY=" + enc.matches("password", hash));
        // Mevcut hash'i de test et
        String existing = "$2a$10$AGRjXSaA5DS/NTnPGr20yeCmxjD/DM.1ZFYl7O4aqCIFD.oAnk0h6";
        System.out.println("EXISTING_MATCHES=" + enc.matches("password", existing));
    }
}
