package com.helpdesk.desktop.view;

import com.helpdesk.desktop.controller.AuthController;
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

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel errorLabel;

    public LoginFrame(AuthController authController, TicketController ticketController, UserController userController) {
        this.authController = authController;
        this.ticketController = ticketController;
        this.userController = userController;
        initUI();
    }

    private void initUI() {
        setTitle("IT Helpdesk");
        setSize(420, 380);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Ekranın ortasında açılır
        setResizable(false);         // Boyut sabitlenir

        // ─── ANA PANEL ───────────────────────────────────────────────────────
        // Açık gri arka plan; içindeki beyaz card'ı ön plana çıkarır
        JPanel root = new JPanel(new GridBagLayout());
        root.setBackground(new Color(245, 247, 250));
        setContentPane(root);

        // ─── LOGIN CARD ──────────────────────────────────────────────────────
        // Beyaz, kenarlıklı kart; tüm form elemanları bu panelin içinde yer alır
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS)); // Elemanlar dikey sıralanır
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 223, 228), 1), // İnce gri çerçeve
                new EmptyBorder(36, 40, 36, 40)                              // İç boşluk
        ));

        // ─── BAŞLIK ──────────────────────────────────────────────────────────
        // Uygulamanın adı — kartın üst ortasında gösterilir
        JLabel titleLabel = new JLabel("IT Helpdesk");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(new Color(30, 40, 60));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Alt başlık — kullanıcıyı yönlendirici küçük metin
        JLabel subLabel = new JLabel("Sign in to your account");
        subLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subLabel.setForeground(new Color(120, 130, 150));
        subLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // ─── KULLANICI ADI ALANI ─────────────────────────────────────────────
        // "Kullanici Adi" etiketi — text field'ın üstünde gösterilir
        JLabel userLabel = new JLabel("Username");
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        userLabel.setForeground(new Color(60, 70, 90));
        userLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Kullanıcı adı metin kutusu — handleLogin() içinde .getText() ile okunur
        usernameField = new JTextField();
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        usernameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        usernameField.setAlignmentX(Component.LEFT_ALIGNMENT);

        // ─── ŞİFRE ALANI ─────────────────────────────────────────────────────
        // "Sifre" etiketi — şifre kutusunun üstünde gösterilir
        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        passLabel.setForeground(new Color(60, 70, 90));
        passLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Şifre kutusu — karakterleri nokta olarak gösterir; Enter tuşu da giriş başlatır
        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);

        // ─── GİRİŞ BUTONU ────────────────────────────────────────────────────
        // Mavi buton — tıklayınca handleLogin() çağrılır ve rol bazlı yönlendirme yapılır
        JButton loginButton = new JButton("Login");
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        loginButton.setBackground(new Color(41, 98, 255));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        loginButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        loginButton.setAlignmentX(Component.LEFT_ALIGNMENT);

        // ─── HATA MESAJI ─────────────────────────────────────────────────────
        // Başta boş görünür; giriş hatası olursa kırmızı mesaj gösterir
        errorLabel = new JLabel(" ");
        errorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        errorLabel.setForeground(new Color(220, 50, 50));
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // ─── KART DÜZENİ ─────────────────────────────────────────────────────
        // Elemanlar aralarındaki boşluklarla birlikte dikey sıraya eklenir
        card.add(titleLabel);
        card.add(Box.createVerticalStrut(4));
        card.add(subLabel);
        card.add(Box.createVerticalStrut(28));
        card.add(userLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(usernameField);
        card.add(Box.createVerticalStrut(14));
        card.add(passLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(passwordField);
        card.add(Box.createVerticalStrut(20));
        card.add(loginButton);
        card.add(Box.createVerticalStrut(10));
        card.add(errorLabel);

        // ─── ROOT'A EKLE ──────────────────────────────────────────────────────
        // GridBagConstraints ile kart hem yatay hem dikey dolduracak şekilde konumlanır
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(20, 30, 20, 30);
        root.add(card, gbc);

        // Buton tıklaması ve Enter tuşu aynı işlemi çalıştırır
        loginButton.addActionListener(e -> handleLogin());
        passwordField.addActionListener(e -> handleLogin());
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Username and password cannot be empty.");
            return;
        }

        boolean success = authController.login(username, password);
        if (success) {
            dispose();
            openDashboardForRole(SessionManager.getCurrentUser().getRole());
        } else {
            errorLabel.setText("Invalid username or password.");
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
            case "CUSTOMER" -> new CustomerDashboardFrame(authController, ticketController, userController).setVisible(true);
            case "AGENT"    -> new AgentDashboardFrame(authController, ticketController, userController).setVisible(true);
            case "SUPERVISOR" -> new SupervisorDashboardFrame(authController, ticketController, userController).setVisible(true);
            default -> new DashboardFrame(authController, ticketController, userController).setVisible(true);
        }
    }
}
