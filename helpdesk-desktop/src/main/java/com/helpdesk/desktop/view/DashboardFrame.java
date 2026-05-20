package com.helpdesk.desktop.view;

import com.helpdesk.desktop.controller.AuthController;
import com.helpdesk.desktop.controller.TicketController;
import com.helpdesk.desktop.controller.UserController;
import com.helpdesk.desktop.security.SessionManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * ADMIN rolune ozel ana dashboard ekrani.
 * Kullanici yonetimi (UserManagementFrame) icin giris noktasidir.
 * Ticket islemleri bu ekranda yer almaz; sadece sistem yonetimi yapilir.
 */
public class DashboardFrame extends JFrame {

    private final AuthController authController;
    private final TicketController ticketController;
    private final UserController userController;

    public DashboardFrame(AuthController authController, TicketController ticketController,
                          UserController userController) {
        this.authController = authController;
        this.ticketController = ticketController;
        this.userController = userController;
        initUI();
    }

    private void initUI() {
        setTitle("IT Helpdesk — Admin");
        setSize(1100, 680);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // ─── ANA PANEL ───────────────────────────────────────────────────────
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(new Color(245, 247, 250));
        setContentPane(root);

        // ─── ÜST BAR ─────────────────────────────────────────────────────────
        // Koyu lacivert bar; sol=uygulama adı + rol, sağ=kullanıcı adı + çıkış
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(30, 40, 60));
        topBar.setBorder(new EmptyBorder(12, 20, 12, 20));

        JLabel appTitle = new JLabel("IT Helpdesk — Admin Panel");
        appTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        appTitle.setForeground(Color.WHITE);

        JPanel rightTop = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        rightTop.setOpaque(false);

        String name = SessionManager.getCurrentUser() != null
                ? SessionManager.getCurrentUser().getFullName() : "Admin";
        JLabel welcomeLabel = new JLabel("Welcome, " + name);
        welcomeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        welcomeLabel.setForeground(new Color(180, 190, 210));

        // Çıkış butonu — oturumu kapatır ve LoginFrame'e döner
        JButton logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        logoutButton.setBackground(new Color(60, 70, 95));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        logoutButton.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));
        logoutButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logoutButton.addActionListener(e -> {
            authController.logout();
            dispose();
            new LoginFrame(authController, ticketController, userController).setVisible(true);
        });

        rightTop.add(welcomeLabel);
        rightTop.add(logoutButton);
        topBar.add(appTitle, BorderLayout.WEST);
        topBar.add(rightTop, BorderLayout.EAST);

        root.add(topBar, BorderLayout.NORTH);

        // ─── ANA İÇERİK ──────────────────────────────────────────────────────
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(new Color(245, 247, 250));

        // ─── ARAÇ ÇUBUĞU ─────────────────────────────────────────────────────
        // Sadece kullanıcı yönetimi butonu — ticket işlemleri bu panelde yok
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        toolbar.setBackground(new Color(245, 247, 250));
        toolbar.setBorder(new EmptyBorder(4, 12, 0, 12));

        // Kullanıcı yönetimi — UserManagementFrame'i yeni pencerede açar
        JButton usersButton = new JButton("Users");
        usersButton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        usersButton.setFocusPainted(false);
        usersButton.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        usersButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        usersButton.addActionListener(e -> new UserManagementFrame(userController).setVisible(true));

        toolbar.add(usersButton);
        contentPanel.add(toolbar, BorderLayout.NORTH);

        // ─── MERKEZİ BAŞLIK ──────────────────────────────────────────────────
        // Admin ekranı ticket değil sistem yönetimi odaklıdır
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(new Color(245, 247, 250));

        JLabel adminLabel = new JLabel("System Administration Panel");
        adminLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        adminLabel.setForeground(new Color(30, 40, 60));
        centerPanel.add(adminLabel);

        contentPanel.add(centerPanel, BorderLayout.CENTER);
        root.add(contentPanel, BorderLayout.CENTER);
    }
}