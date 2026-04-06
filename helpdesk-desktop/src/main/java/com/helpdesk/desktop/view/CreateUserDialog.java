package com.helpdesk.desktop.view;

import com.helpdesk.desktop.controller.UserController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Yeni kullanici olusturma modal dialog'u.
 * UserManagementFrame tarafindan acilir; admin bir kullanicinin tum bilgilerini
 * ve rolunu girerek sisteme kayit yapabilir.
 *
 * Form alanlari: First Name, Last Name, Username, Email, Password, Department, Role
 * Rol secenekleri: CUSTOMER, AGENT, SUPERVISOR, ADMIN
 *
 * Kullanim akisi:
 * 1. Admin "+ New User" a basar → bu dialog acilir.
 * 2. Bilgiler girilir, "Create User" butonuna basilir.
 * 3. UserController.createUser() cagirilir; sifre BCrypt ile hashlenir, rol atanir.
 * 4. isCreated() == true ise UserManagementFrame tabloyu yeniler.
 */
public class CreateUserDialog extends JDialog {

    private final UserController userController;
    private boolean created = false;

    private JTextField usernameField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField departmentField;
    private JComboBox<String> roleCombo;
    private JLabel errorLabel;

    public CreateUserDialog(Frame parent, UserController userController) {
        super(parent, "New User", true);
        this.userController = userController;
        initUI();
    }

    private void initUI() {
        setSize(480, 520);
        setLocationRelativeTo(getParent()); // Ebeveyn pencereye göre ortala
        setResizable(false);

        // ─── ANA PANEL ───────────────────────────────────────────────────────
        // Beyaz arka plan; iç boşlukla form elemanları çerçevelenir
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.WHITE);
        root.setBorder(new EmptyBorder(28, 32, 24, 32));
        setContentPane(root);

        // ─── DİALOG BAŞLIĞI ──────────────────────────────────────────────────
        // "Create New User" — formun amacını belirtir
        JLabel titleLabel = new JLabel("Create New User");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(new Color(30, 40, 60));
        titleLabel.setBorder(new EmptyBorder(0, 0, 20, 0));
        root.add(titleLabel, BorderLayout.NORTH);

        // ─── FORM ALANLARI ────────────────────────────────────────────────────
        // Tüm giriş alanları dikey BoxLayout ile sıralanır
        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(Color.WHITE);

        // Ad – Soyad yan yana (GridLayout 1x2)
        JPanel nameRow = new JPanel(new GridLayout(1, 2, 12, 0));
        nameRow.setBackground(Color.WHITE);
        nameRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 62));
        nameRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        // İlk ad alanı — solda
        JPanel firstNamePanel = new JPanel();
        firstNamePanel.setLayout(new BoxLayout(firstNamePanel, BoxLayout.Y_AXIS));
        firstNamePanel.setBackground(Color.WHITE);
        firstNamePanel.add(makeLabel("First Name"));
        firstNamePanel.add(Box.createVerticalStrut(4));
        firstNameField = new JTextField(); // Kullanıcının adı
        firstNameField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        firstNamePanel.add(firstNameField);

        // Soyad alanı — sağda
        JPanel lastNamePanel = new JPanel();
        lastNamePanel.setLayout(new BoxLayout(lastNamePanel, BoxLayout.Y_AXIS));
        lastNamePanel.setBackground(Color.WHITE);
        lastNamePanel.add(makeLabel("Last Name"));
        lastNamePanel.add(Box.createVerticalStrut(4));
        lastNameField = new JTextField(); // Kullanıcının soyadı
        lastNameField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lastNamePanel.add(lastNameField);

        nameRow.add(firstNamePanel);
        nameRow.add(lastNamePanel);
        form.add(nameRow);
        form.add(Box.createVerticalStrut(12));

        // Kullanıcı adı — sistemde benzersiz olmalı; login için kullanılır
        form.add(makeLabel("Username"));
        form.add(Box.createVerticalStrut(4));
        usernameField = makeTextField();
        form.add(usernameField);
        form.add(Box.createVerticalStrut(12));

        // E-posta alanı — bildirim ve kimlik için
        form.add(makeLabel("Email"));
        form.add(Box.createVerticalStrut(4));
        emailField = makeTextField();
        form.add(emailField);
        form.add(Box.createVerticalStrut(12));

        // Şifre alanı — JPasswordField; karakterler nokta olarak gösterilir
        // Kaydedilirken BCrypt ile hashlenir, ham şifre DB'ye yazılmaz
        form.add(makeLabel("Password"));
        form.add(Box.createVerticalStrut(4));
        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(passwordField);
        form.add(Box.createVerticalStrut(12));

        // Departman alanı — opsiyonel; boş bırakılabilir
        form.add(makeLabel("Department"));
        form.add(Box.createVerticalStrut(4));
        departmentField = makeTextField();
        form.add(departmentField);
        form.add(Box.createVerticalStrut(12));

        // Rol seçici — CUSTOMER / AGENT / SUPERVISOR / ADMIN
        // Seçilen rol user_roles tablosuna atanır
        form.add(makeLabel("Role"));
        form.add(Box.createVerticalStrut(4));
        roleCombo = new JComboBox<>(new String[]{"CUSTOMER", "AGENT", "SUPERVISOR", "ADMIN"});
        roleCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        roleCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        roleCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(roleCombo);
        form.add(Box.createVerticalStrut(8));

        // Hata mesajı — başta boş; doğrulama hatası varsa kırmızı metin gösterir
        errorLabel = new JLabel(" ");
        errorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        errorLabel.setForeground(new Color(220, 50, 50));
        errorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(errorLabel);

        root.add(form, BorderLayout.CENTER);

        // ─── BUTONLAR ────────────────────────────────────────────────────────
        // Sağa hizalı; Cancel (iptal) ve Create User (kaydet) yan yana
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(new EmptyBorder(16, 0, 0, 0));

        // İptal butonu — dialog'u kapatır, hiçbir şey kaydetmez
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cancelButton.setFocusPainted(false);
        cancelButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        cancelButton.addActionListener(e -> dispose());

        // Kaydet butonu — handleCreate() ile doğrulama yapılır ve kullanıcı oluşturulur
        JButton createButton = new JButton("Create User");
        createButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        createButton.setBackground(new Color(41, 98, 255));
        createButton.setForeground(Color.WHITE);
        createButton.setFocusPainted(false);
        createButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        createButton.addActionListener(e -> handleCreate());

        buttonPanel.add(cancelButton);
        buttonPanel.add(createButton);
        root.add(buttonPanel, BorderLayout.SOUTH);
    }

    private JLabel makeLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        label.setForeground(new Color(60, 70, 90));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JTextField makeTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        return field;
    }

    private void handleCreate() {
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String department = departmentField.getText().trim();
        String role = (String) roleCombo.getSelectedItem();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty()
                || firstName.isEmpty() || lastName.isEmpty()) {
            errorLabel.setText("Required fields cannot be empty.");
            return;
        }

        try {
            userController.createUser(username, email, password, firstName, lastName, department, role);
            created = true;
            dispose();
        } catch (Exception ex) {
            errorLabel.setText("Error: " + ex.getMessage());
        }
    }

    public boolean isCreated() {
        return created;
    }
}
