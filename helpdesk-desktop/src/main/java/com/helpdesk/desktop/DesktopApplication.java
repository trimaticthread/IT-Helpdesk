package com.helpdesk.desktop;

import javax.swing.SwingUtilities;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.formdev.flatlaf.FlatIntelliJLaf;
import com.helpdesk.desktop.controller.AuthController;
import com.helpdesk.desktop.controller.CategoryController;
import com.helpdesk.desktop.controller.DepartmentController;
import com.helpdesk.desktop.controller.GroupController;
import com.helpdesk.desktop.controller.SlaController;
import com.helpdesk.desktop.controller.TicketController;
import com.helpdesk.desktop.controller.UserController;
import com.helpdesk.desktop.view.LoginFrame;

/**
 * Masaustu uygulamasinin giris noktasi.
 * Spring Boot context'i baslatir ve Swing UI'i EDT (Event Dispatch Thread) uzerinde acar.
 *
 * Baslatma sirasi:
 * 1. FlatIntelliJLaf.setup() — modern Swing Look & Feel aktif edilir.
 *    Bu, Spring context baslamadan ONCE yapilmalidir.
 * 2. SpringApplication.run() — tum Spring bean'leri olusturulur (DAO, Service, Controller).
 * 3. ApplicationRunner.run() — Spring hazir oldugunda SwingUtilities.invokeLater ile
 *    LoginFrame EDT uzerinde gosterilir.
 *
 * Not: java.awt.headless=false olmadan Swing ekrani acilmaz.
 */
@SpringBootApplication(scanBasePackages = "com.helpdesk")
public class DesktopApplication implements ApplicationRunner {

    private final AuthController authController;
    private final TicketController ticketController;
    private final UserController userController;
    private final CategoryController categoryController;
    private final DepartmentController departmentController;
    private final GroupController groupController;
    private final SlaController slaController;

    public DesktopApplication(AuthController authController, TicketController ticketController,
                               UserController userController, CategoryController categoryController,
                               DepartmentController departmentController, GroupController groupController,
                               SlaController slaController) {
        this.authController = authController;
        this.ticketController = ticketController;
        this.userController = userController;
        this.categoryController = categoryController;
        this.departmentController = departmentController;
        this.groupController = groupController;
        this.slaController = slaController;
    }

    public static void main(String[] args) {
        System.setProperty("java.awt.headless", "false");
        FlatIntelliJLaf.setup();
        SpringApplication.run(DesktopApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) {
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame(authController, ticketController, userController, categoryController, departmentController, groupController, slaController);
            loginFrame.setVisible(true);
        });
    }
}
