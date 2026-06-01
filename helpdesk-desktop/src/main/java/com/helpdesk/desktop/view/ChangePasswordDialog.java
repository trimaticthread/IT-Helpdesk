package com.helpdesk.desktop.view;

import com.helpdesk.desktop.controller.UserController;
import com.helpdesk.desktop.security.SessionManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Admin şifre sıfırladıktan sonra kullanıcı bu dialog ile yeni şifresini belirler.
 * Dashboard açılmadan önce gösterilir; kapatılamaz (modal + dispose engeli).
 */
public class ChangePasswordDialog extends JDialog {

    private final UserController userController;
    private boolean passwordChanged = false;

    private JPasswordField newPasswordField;
    private JPasswordField confirmPasswordField;
    private JLabel errorLabel;

    public ChangePasswordDialog(Frame parent, UserController userController) {
        super(parent, "Password Change Required", true);
        this.userController = userController;
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE); // kapatılamaz
        initUI();
    }

    private void initUI() {
        setSize(420, 360);
        setLocationRelativeTo(getParent());
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout());
        setContentPane(root);

        // ─── ÜST BANNER ──────────────────────────────────────────────────────
        JPanel banner = new JPanel();
        banner.setBackground(new Color(180, 60, 60));
        banner.setLayout(new BoxLayout(banner, BoxLayout.Y_AXIS));
        banner.setBorder(new EmptyBorder(20, 30, 18, 30));

        JLabel title = new JLabel("You Must Change Your Password");
        title.setFont(new Font("Segoe UI", Font.BOLD, 15));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sub = new JLabel("Your password has been reset. Please set a new password to continue.");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sub.setForeground(new Color(255, 210, 210));
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);

        banner.add(title);
        banner.add(Box.createVerticalStrut(6));
        banner.add(sub);
        root.add(banner, BorderLayout.NORTH);

        // ─── FORM ─────────────────────────────────────────────────────────────
        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(Color.WHITE);
        form.setBorder(new EmptyBorder(24, 32, 16, 32));

        form.add(makeLabel("New Password"));
        form.add(Box.createVerticalStrut(5));
        newPasswordField = new JPasswordField();
        newPasswordField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        newPasswordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        newPasswordField.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(newPasswordField);

        form.add(Box.createVerticalStrut(14));

        form.add(makeLabel("Confirm New Password"));
        form.add(Box.createVerticalStrut(5));
        confirmPasswordField = new JPasswordField();
        confirmPasswordField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        confirmPasswordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        confirmPasswordField.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(confirmPasswordField);

        form.add(Box.createVerticalStrut(14));

        errorLabel = new JLabel(" ");
        errorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        errorLabel.setForeground(new Color(200, 50, 50));
        errorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(errorLabel);

        root.add(form, BorderLayout.CENTER);

        // ─── BUTON ───────────────────────────────────────────────────────────
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 10));
        bottom.setBackground(Color.WHITE);
        bottom.setBorder(new EmptyBorder(0, 0, 8, 8));

        JButton saveButton = new JButton("Save Password");
        saveButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        saveButton.setBackground(new Color(41, 98, 255));
        saveButton.setForeground(Color.WHITE);
        saveButton.setFocusPainted(false);
        saveButton.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        saveButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        saveButton.addActionListener(e -> handleSave());
        bottom.add(saveButton);

        root.add(bottom, BorderLayout.SOUTH);

        confirmPasswordField.addActionListener(e -> handleSave());
    }

    private JLabel makeLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        label.setForeground(new Color(60, 70, 90));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private void handleSave() {
        String newPw = new String(newPasswordField.getPassword());
        String confirm = new String(confirmPasswordField.getPassword());

        if (newPw.isEmpty()) {
            errorLabel.setText("Password is required.");
            return;
        }
        if (newPw.length() < 6) {
            errorLabel.setText("Password must be at least 6 characters.");
            return;
        }
        if (!newPw.equals(confirm)) {
            errorLabel.setText("Passwords do not match.");
            confirmPasswordField.setText("");
            return;
        }

        try {
            Long userId = SessionManager.getCurrentUser().getId();
            userController.changePassword(userId, newPw);
            passwordChanged = true;
            dispose();
        } catch (Exception ex) {
            errorLabel.setText("Error:" + ex.getMessage());
        }
    }

    public boolean isPasswordChanged() {
        return passwordChanged;
    }
}
