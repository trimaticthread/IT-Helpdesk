package com.helpdesk.desktop.view;

import com.helpdesk.application.dto.UserDTO;
import com.helpdesk.desktop.controller.UserController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Mevcut kullanici bilgilerini duzenleme modal dialog'u.
 * UserManagementFrame'deki "Edit" butonu ile acilir; secili kullanicinin
 * bilgileri form alanlarina onceden doldurulmus olarak gelir.
 *
 * Duzenlenebilir alanlar: First Name, Last Name, Email, Department
 * Degistirilemeyen alanlar: Username (sistemde benzersiz kimlik), Password, Role
 *
 * Kullanim akisi:
 * 1. Admin tablodan bir kullanici secer ve "Edit" e basar.
 * 2. Mevcut bilgiler form alanlarina yuklenir.
 * 3. Degisiklikler kaydedilince UserController.updateUser() cagirilir.
 * 4. isUpdated() == true ise UserManagementFrame tabloyu yeniler.
 */
public class EditUserDialog extends JDialog {

    private final UserController userController;
    private final UserDTO user;
    private boolean updated = false;

    private JTextField emailField;
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField departmentField;
    private JLabel errorLabel;

    public EditUserDialog(Frame parent, UserController userController, UserDTO user) {
        super(parent, "Edit User", true);
        this.userController = userController;
        this.user = user;
        initUI();
    }

    private void initUI() {
        setSize(440, 380);
        setLocationRelativeTo(getParent()); // Ebeveyn pencereye göre ortala
        setResizable(false);

        // ─── ANA PANEL ───────────────────────────────────────────────────────
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.WHITE);
        root.setBorder(new EmptyBorder(28, 32, 24, 32));
        setContentPane(root);

        // ─── DİALOG BAŞLIĞI ──────────────────────────────────────────────────
        // Hangi kullanıcının düzenlendiğini username ile belirtir
        JLabel titleLabel = new JLabel("Edit User — " + user.getUsername());
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(new Color(30, 40, 60));
        titleLabel.setBorder(new EmptyBorder(0, 0, 20, 0));
        root.add(titleLabel, BorderLayout.NORTH);

        // ─── FORM ALANLARI ────────────────────────────────────────────────────
        // Mevcut kullanıcı bilgileri alanlar açılınca doldurulmuş gelir
        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(Color.WHITE);

        // Ad – Soyad yan yana (GridLayout 1x2)
        JPanel nameRow = new JPanel(new GridLayout(1, 2, 12, 0));
        nameRow.setBackground(Color.WHITE);
        nameRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 62));
        nameRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        // İlk ad alanı — mevcut değer (user.getFirstName()) ile doldurulur
        JPanel firstNamePanel = new JPanel();
        firstNamePanel.setLayout(new BoxLayout(firstNamePanel, BoxLayout.Y_AXIS));
        firstNamePanel.setBackground(Color.WHITE);
        firstNamePanel.add(makeLabel("First Name"));
        firstNamePanel.add(Box.createVerticalStrut(4));
        firstNameField = new JTextField(user.getFirstName());
        firstNameField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        firstNamePanel.add(firstNameField);

        // Soyad alanı — mevcut değer (user.getLastName()) ile doldurulur
        JPanel lastNamePanel = new JPanel();
        lastNamePanel.setLayout(new BoxLayout(lastNamePanel, BoxLayout.Y_AXIS));
        lastNamePanel.setBackground(Color.WHITE);
        lastNamePanel.add(makeLabel("Last Name"));
        lastNamePanel.add(Box.createVerticalStrut(4));
        lastNameField = new JTextField(user.getLastName());
        lastNameField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lastNamePanel.add(lastNameField);

        nameRow.add(firstNamePanel);
        nameRow.add(lastNamePanel);
        form.add(nameRow);
        form.add(Box.createVerticalStrut(12));

        // E-posta alanı — mevcut e-posta ile doldurulur; değiştirilebilir
        form.add(makeLabel("Email"));
        form.add(Box.createVerticalStrut(4));
        emailField = makeTextField(user.getEmail());
        form.add(emailField);
        form.add(Box.createVerticalStrut(12));

        // Departman alanı — null ise boş string ile doldurulur
        form.add(makeLabel("Department"));
        form.add(Box.createVerticalStrut(4));
        departmentField = makeTextField(user.getDepartment() != null ? user.getDepartment() : "");
        form.add(departmentField);
        form.add(Box.createVerticalStrut(8));

        // Hata mesajı — boş başlar; zorunlu alan eksikse kırmızı uyarı gösterir
        errorLabel = new JLabel(" ");
        errorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        errorLabel.setForeground(new Color(220, 50, 50));
        errorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(errorLabel);

        root.add(form, BorderLayout.CENTER);

        // ─── BUTONLAR ────────────────────────────────────────────────────────
        // Cancel: değişiklikleri iptal eder | Save Changes: güncellemeyi kaydeder
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(new EmptyBorder(16, 0, 0, 0));

        // İptal butonu — hiçbir değişiklik kaydedilmeden dialog kapanır
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cancelButton.setFocusPainted(false);
        cancelButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        cancelButton.addActionListener(e -> dispose());

        // Kaydet butonu — handleSave() ile doğrulama yapılır ve güncelleme çalıştırılır
        JButton saveButton = new JButton("Save Changes");
        saveButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        saveButton.setBackground(new Color(41, 98, 255));
        saveButton.setForeground(Color.WHITE);
        saveButton.setFocusPainted(false);
        saveButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        saveButton.addActionListener(e -> handleSave());

        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);
        root.add(buttonPanel, BorderLayout.SOUTH);
    }

    private JLabel makeLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        label.setForeground(new Color(60, 70, 90));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JTextField makeTextField(String value) {
        JTextField field = new JTextField(value);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        return field;
    }

    private void handleSave() {
        String email = emailField.getText().trim();
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String department = departmentField.getText().trim();

        if (email.isEmpty() || firstName.isEmpty() || lastName.isEmpty()) {
            errorLabel.setText("Required fields cannot be empty.");
            return;
        }

        try {
            userController.updateUser(user.getId(), user.getUsername(), email, firstName, lastName, department, null);
            updated = true;
            dispose();
        } catch (Exception ex) {
            errorLabel.setText("Error: " + ex.getMessage());
        }
    }

    public boolean isUpdated() {
        return updated;
    }
}