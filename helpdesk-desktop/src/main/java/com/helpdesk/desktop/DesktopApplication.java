package com.helpdesk.desktop;

import javax.swing.SwingUtilities;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.formdev.flatlaf.FlatIntelliJLaf;
import com.helpdesk.desktop.controller.AuthController;
import com.helpdesk.desktop.controller.TicketController;
import com.helpdesk.desktop.controller.UserController;
import com.helpdesk.desktop.view.LoginFrame;

@SpringBootApplication(scanBasePackages = "com.helpdesk")
public class DesktopApplication implements ApplicationRunner {

    private final AuthController authController;
    private final TicketController ticketController;
    private final UserController userController;

    public DesktopApplication(AuthController authController, TicketController ticketController, UserController userController) {
        this.authController = authController;
        this.ticketController = ticketController;
        this.userController = userController;
    }

    public static void main(String[] args) {
        System.setProperty("java.awt.headless", "false");
        FlatIntelliJLaf.setup();
        SpringApplication.run(DesktopApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) {
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame(authController, ticketController, userController);
            loginFrame.setVisible(true);
        });
    }
}
