package com.helpdesk.desktop.view;

import com.helpdesk.desktop.controller.AuthController;
import com.helpdesk.desktop.controller.CategoryController;
import com.helpdesk.desktop.controller.DepartmentController;
import com.helpdesk.desktop.controller.GroupController;
import com.helpdesk.desktop.controller.TicketController;
import com.helpdesk.desktop.controller.UserController;
import com.helpdesk.desktop.security.SessionManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Uygulamanin giris ekrani.
 * Kullanici adi ve sifre alinir, AuthController uzerinden dogrulama yapilir.
 *
 * Giris akisi:
 * 1. Kullanici bilgilerini girer ve "Giris Yap" a basar.
 * 2. AuthController.login() cagirilir; basarili olursa SessionManager dolar.
 * 3. SessionManager'daki kullanicinin rolune gore dogru dashboard acilir:
 *    - CUSTOMER    → CustomerDashboardFrame  (sadece kendi ticket'lari)
 *    - AGENT       → AgentDashboardFrame     (atanan ticket'lar + status degistirme)
 *    - SUPERVISOR  → SupervisorDashboardFrame (tum ticket'lar + atama)
 *    - ADMIN       → DashboardFrame          (tam yetki + kullanici yonetimi)
 * 4. Bu frame dispose edilir, yeni dashboard gosterilir.
 */
public class LoginFrame extends JFrame {

    private final AuthController authController;
    private final TicketController ticketController;
    private final UserController userController;
    private final CategoryController categoryController;
    private final DepartmentController departmentController;
    private final GroupController groupController;

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel errorLabel;

    public LoginFrame(AuthController authController, TicketController ticketController,
                      UserController userController, CategoryController categoryController,
                      DepartmentController departmentController, GroupController groupController) {
        this.authController = authController;
        this.ticketController = ticketController;
        this.userController = userController;
        this.categoryController = categoryController;
        this.departmentController = departmentController;
        this.groupController = groupController;
        initUI();
    }

    private void initUI() {
        setTitle("IT Helpdesk");
        setSize(420, 460);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout());
        setContentPane(root);

        // ─── ÜST BANNER ──────────────────────────────────────────────────────
        JPanel banner = new JPanel();
        banner.setBackground(new Color(30, 40, 60));
        banner.setLayout(new BoxLayout(banner, BoxLayout.Y_AXIS));
        banner.setBorder(new EmptyBorder(32, 40, 28, 40));

        JLabel titleLabel = new JLabel("IT Helpdesk");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subLabel = new JLabel("Destek Talep Sistemi");
        subLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subLabel.setForeground(new Color(160, 175, 200));
        subLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        banner.add(titleLabel);
        banner.add(Box.createVerticalStrut(6));
        banner.add(subLabel);

        root.add(banner, BorderLayout.NORTH);

        // ─── FORM PANEL ───────────────────────────────────────────────────────
        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(Color.WHITE);
        form.setBorder(new EmptyBorder(32, 40, 32, 40));

        form.add(makeFieldLabel("Kullanici Adi"));
        form.add(Box.createVerticalStrut(5));
        usernameField = new JTextField();
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        usernameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        usernameField.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(usernameField);

        form.add(Box.createVerticalStrut(16));

        form.add(makeFieldLabel("Sifre"));
        form.add(Box.createVerticalStrut(5));
        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(passwordField);

        form.add(Box.createVerticalStrut(24));

        JButton loginButton = new JButton("Giris Yap");
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginButton.setBackground(new Color(41, 98, 255));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        loginButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        loginButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(loginButton);

        form.add(Box.createVerticalStrut(12));

        // Sabit yükseklik — metin değişince layout bozulmasın
        errorLabel = new JLabel(" ");
        errorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        errorLabel.setForeground(new Color(220, 50, 50));
        JPanel errorWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        errorWrapper.setBackground(Color.WHITE);
        errorWrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 22));
        errorWrapper.setAlignmentX(Component.LEFT_ALIGNMENT);
        errorWrapper.add(errorLabel);
        form.add(errorWrapper);

        root.add(form, BorderLayout.CENTER);

        // ─── ALT BİLGİ ───────────────────────────────────────────────────────
        JPanel footer = new JPanel();
        footer.setBackground(new Color(245, 247, 250));
        footer.setBorder(new EmptyBorder(10, 0, 10, 0));
        JLabel footerLabel = new JLabel("IT Helpdesk v1.0");
        footerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        footerLabel.setForeground(new Color(160, 170, 185));
        footer.add(footerLabel);
        root.add(footer, BorderLayout.SOUTH);

        loginButton.addActionListener(e -> handleLogin());
        passwordField.addActionListener(e -> handleLogin());
    }

    private JLabel makeFieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        label.setForeground(new Color(60, 70, 90));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() && password.isEmpty()) {
            errorLabel.setText("Kullanici adi ve sifre bos birakilamaz.");
            return;
        }
        if (username.isEmpty()) {
            errorLabel.setText("Kullanici adi bos birakilamaz.");
            return;
        }
        if (password.isEmpty()) {
            errorLabel.setText("Sifre bos birakilamaz.");
            return;
        }

        errorLabel.setText(" ");
        boolean success = authController.login(username, password);
        if (success) {
            dispose();
            openDashboardForRole(SessionManager.getCurrentUser().getRole());
        } else {
            errorLabel.setText("Kullanici adi veya sifre yanlis.");
            passwordField.setText("");
        }
    }

    /**
     * Giris yapan kullanicinin rolune gore ilgili dashboard ekranini acar.
     * Tanimsiz veya null rol Admin dashboard'una yonlendirir (fallback).
     */
    private void openDashboardForRole(String role) {
        if (role == null) role = "ADMIN";
        switch (role) {
            case "CUSTOMER"   -> new CustomerDashboardFrame(authController, ticketController, userController, categoryController, departmentController, groupController).setVisible(true);
            case "AGENT"      -> new AgentDashboardFrame(authController, ticketController, userController, categoryController, departmentController, groupController).setVisible(true);
            case "SUPERVISOR" -> new SupervisorDashboardFrame(authController, ticketController, userController, categoryController, departmentController, groupController).setVisible(true);
            default           -> new DashboardFrame(authController, ticketController, userController, categoryController, departmentController, groupController).setVisible(true);
        }
    }
}
