package com.helpdesk.desktop.view;

import com.helpdesk.application.dto.UserDTO;
import com.helpdesk.desktop.controller.DepartmentController;
import com.helpdesk.desktop.controller.UserController;
import com.helpdesk.domain.entity.Department;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class EditUserDialog extends JDialog {

    private final UserController userController;
    private final UserDTO user;
    private boolean updated = false;

    private JTextField emailField;
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JComboBox<String> departmentCombo;
    private JLabel errorLabel;

    public EditUserDialog(Frame parent, UserController userController,
                          DepartmentController departmentController, UserDTO user) {
        super(parent, "Edit User", true);
        this.userController = userController;
        this.user = user;

        // Aktif departmanları DB'den yükle; boşsa fallback listesi kullan
        List<Department> depts = departmentController.getActiveDepartments();
        String[] deptNames = depts.stream().map(Department::getName).toArray(String[]::new);
        if (deptNames.length == 0) deptNames = new String[]{"—"};

        initUI(deptNames);
    }

    private void initUI(String[] deptNames) {
        setSize(440, 400);
        setLocationRelativeTo(getParent());
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.WHITE);
        root.setBorder(new EmptyBorder(28, 32, 24, 32));
        setContentPane(root);

        // ─── BAŞLIK ───────────────────────────────────────────────────────────
        JLabel titleLabel = new JLabel("Edit User — " + user.getUsername());
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(new Color(30, 40, 60));
        titleLabel.setBorder(new EmptyBorder(0, 0, 20, 0));
        root.add(titleLabel, BorderLayout.NORTH);

        // ─── FORM ─────────────────────────────────────────────────────────────
        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(Color.WHITE);

        // Ad – Soyad yan yana
        JPanel nameRow = new JPanel(new GridLayout(1, 2, 12, 0));
        nameRow.setBackground(Color.WHITE);
        nameRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 62));
        nameRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel firstNamePanel = new JPanel(new BorderLayout());
        firstNamePanel.setBackground(Color.WHITE);
        firstNamePanel.add(makeLabel("First Name"), BorderLayout.NORTH);
        firstNameField = new JTextField(user.getFirstName());
        firstNameField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        firstNamePanel.add(firstNameField, BorderLayout.CENTER);

        JPanel lastNamePanel = new JPanel(new BorderLayout());
        lastNamePanel.setBackground(Color.WHITE);
        lastNamePanel.add(makeLabel("Last Name"), BorderLayout.NORTH);
        lastNameField = new JTextField(user.getLastName());
        lastNameField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lastNamePanel.add(lastNameField, BorderLayout.CENTER);

        nameRow.add(firstNamePanel);
        nameRow.add(lastNamePanel);
        form.add(nameRow);
        form.add(Box.createVerticalStrut(12));

        // Email
        form.add(makeLabel("Email"));
        form.add(Box.createVerticalStrut(4));
        emailField = makeTextField(user.getEmail() != null ? user.getEmail() : "");
        form.add(emailField);
        form.add(Box.createVerticalStrut(12));

        // Departman — JComboBox, mevcut departman seçili gelir
        form.add(makeLabel("Department"));
        form.add(Box.createVerticalStrut(4));
        departmentCombo = new JComboBox<>(deptNames);
        departmentCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        departmentCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        departmentCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
        // Kullanıcının mevcut departmanı varsa listede seç
        if (user.getDepartment() != null) {
            for (int i = 0; i < deptNames.length; i++) {
                if (deptNames[i].equals(user.getDepartment())) {
                    departmentCombo.setSelectedIndex(i);
                    break;
                }
            }
        }
        form.add(departmentCombo);
        form.add(Box.createVerticalStrut(8));

        // Hata mesajı — sabit yükseklikte, layout'u bozmaz
        JPanel errorWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        errorWrapper.setBackground(Color.WHITE);
        errorWrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 22));
        errorWrapper.setAlignmentX(Component.LEFT_ALIGNMENT);
        errorLabel = new JLabel(" ");
        errorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        errorLabel.setForeground(new Color(220, 50, 50));
        errorWrapper.add(errorLabel);
        form.add(errorWrapper);

        root.add(form, BorderLayout.CENTER);

        // ─── BUTONLAR ─────────────────────────────────────────────────────────
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(new EmptyBorder(16, 0, 0, 0));

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cancelButton.setFocusPainted(false);
        cancelButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        cancelButton.addActionListener(e -> dispose());

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
        String email     = emailField.getText().trim();
        String firstName = firstNameField.getText().trim();
        String lastName  = lastNameField.getText().trim();
        String department = (String) departmentCombo.getSelectedItem();

        if (firstName.isEmpty()) { errorLabel.setText("First name cannot be empty."); return; }
        if (lastName.isEmpty())  { errorLabel.setText("Last name cannot be empty.");  return; }
        if (email.isEmpty())     { errorLabel.setText("Email cannot be empty.");       return; }
        if (!email.contains("@") || !email.contains(".")) {
            errorLabel.setText("Invalid email format.");
            return;
        }

        try {
            userController.updateUser(user.getId(), user.getUsername(), email,
                    firstName, lastName, department, null);
            updated = true;
            dispose();
        } catch (Exception ex) {
            errorLabel.setText("Error: " + ex.getMessage());
        }
    }

    public boolean isUpdated() { return updated; }
}
