package com.helpdesk.desktop;

import com.formdev.flatlaf.FlatIntelliJLaf;
import com.helpdesk.desktop.controller.AuthController;
import com.helpdesk.desktop.controller.TicketController;
import com.helpdesk.desktop.view.LoginFrame;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.swing.*;

@SpringBootApplication(scanBasePackages = "com.helpdesk")
public class DesktopApplication implements ApplicationRunner {

    private final AuthController authController;
    private final TicketController ticketController;

    public DesktopApplication(AuthController authController, TicketController ticketController) {
        this.authController = authController;
        this.ticketController = ticketController;
    }

    public static void main(String[] args) {
        System.setProperty("java.awt.headless", "false");
        FlatIntelliJLaf.setup();
        SpringApplication.run(DesktopApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) {
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame(authController, ticketController);
            loginFrame.setVisible(true);
        });
    }
}
