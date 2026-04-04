package com.helpdesk.desktop.view;

import com.helpdesk.desktop.controller.UserController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

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
        setLocationRelativeTo(getParent());
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.WHITE);
        root.setBorder(new EmptyBorder(28, 32, 24, 32));
        setContentPane(root);

        JLabel titleLabel = new JLabel("Create New User");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(new Color(30, 40, 60));
        titleLabel.setBorder(new EmptyBorder(0, 0, 20, 0));
        root.add(titleLabel, BorderLayout.NORTH);

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(Color.WHITE);

        JPanel nameRow = new JPanel(new GridLayout(1, 2, 12, 0));
        nameRow.setBackground(Color.WHITE);
        nameRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 62));
        nameRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel firstNamePanel = new JPanel();
        firstNamePanel.setLayout(new BoxLayout(firstNamePanel, BoxLayout.Y_AXIS));
        firstNamePanel.setBackground(Color.WHITE);
        firstNamePanel.add(makeLabel("First Name"));
        firstNamePanel.add(Box.createVerticalStrut(4));
        firstNameField = new JTextField();
        firstNameField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        firstNamePanel.add(firstNameField);

        JPanel lastNamePanel = new JPanel();
        lastNamePanel.setLayout(new BoxLayout(lastNamePanel, BoxLayout.Y_AXIS));
        lastNamePanel.setBackground(Color.WHITE);
        lastNamePanel.add(makeLabel("Last Name"));
        lastNamePanel.add(Box.createVerticalStrut(4));
        lastNameField = new JTextField();
        lastNameField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lastNamePanel.add(lastNameField);

        nameRow.add(firstNamePanel);
        nameRow.add(lastNamePanel);
        form.add(nameRow);
        form.add(Box.createVerticalStrut(12));

        form.add(makeLabel("Username"));
        form.add(Box.createVerticalStrut(4));
        usernameField = makeTextField();
        form.add(usernameField);
        form.add(Box.createVerticalStrut(12));

        form.add(makeLabel("Email"));
        form.add(Box.createVerticalStrut(4));
        emailField = makeTextField();
        form.add(emailField);
        form.add(Box.createVerticalStrut(12));

        form.add(makeLabel("Password"));
        form.add(Box.createVerticalStrut(4));
        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(passwordField);
        form.add(Box.createVerticalStrut(12));

        form.add(makeLabel("Department"));
        form.add(Box.createVerticalStrut(4));
        departmentField = makeTextField();
        form.add(departmentField);
        form.add(Box.createVerticalStrut(12));

        form.add(makeLabel("Role"));
        form.add(Box.createVerticalStrut(4));
        roleCombo = new JComboBox<>(new String[]{"CUSTOMER", "AGENT", "SUPERVISOR", "ADMIN"});
        roleCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        roleCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        roleCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(roleCombo);
        form.add(Box.createVerticalStrut(8));

        errorLabel = new JLabel(" ");
        errorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        errorLabel.setForeground(new Color(220, 50, 50));
        errorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(errorLabel);

        root.add(form, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(new EmptyBorder(16, 0, 0, 0));

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cancelButton.setFocusPainted(false);
        cancelButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        cancelButton.addActionListener(e -> dispose());

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
