package com.helpdesk.desktop.view;

import com.helpdesk.application.dto.UserDTO;
import com.helpdesk.desktop.controller.UserController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class UserManagementFrame extends JFrame {

    private final UserController userController;
    private DefaultTableModel tableModel;

    public UserManagementFrame(UserController userController) {
        this.userController = userController;
        initUI();
        loadUsers();
    }

    private void initUI() {
        setTitle("IT Helpdesk - User Management");
        setSize(800, 520);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(new Color(245, 247, 250));
        setContentPane(root);

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(30, 40, 60));
        topBar.setBorder(new EmptyBorder(12, 20, 12, 20));

        JLabel titleLabel = new JLabel("User Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(Color.WHITE);
        topBar.add(titleLabel, BorderLayout.WEST);

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        toolbar.setBackground(new Color(245, 247, 250));
        toolbar.setBorder(new EmptyBorder(4, 12, 0, 12));

        JButton newUserButton = new JButton("+ New User");
        newUserButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        newUserButton.setBackground(new Color(41, 98, 255));
        newUserButton.setForeground(Color.WHITE);
        newUserButton.setFocusPainted(false);
        newUserButton.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        newUserButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        newUserButton.addActionListener(e -> openCreateUserDialog());

        JButton refreshButton = new JButton("Refresh");
        refreshButton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        refreshButton.setFocusPainted(false);
        refreshButton.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        refreshButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        refreshButton.addActionListener(e -> loadUsers());

        toolbar.add(newUserButton);
        toolbar.add(refreshButton);

        String[] columns = {"ID", "Username", "Full Name", "Email", "Department", "Active"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        JTable userTable = new JTable(tableModel);
        userTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        userTable.setRowHeight(32);
        userTable.setShowGrid(false);
        userTable.setIntercellSpacing(new Dimension(0, 0));
        userTable.setSelectionBackground(new Color(220, 230, 255));
        userTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        userTable.getTableHeader().setBackground(new Color(235, 238, 245));
        userTable.getTableHeader().setForeground(new Color(80, 90, 110));

        int[] widths = {50, 130, 160, 200, 130, 60};
        for (int i = 0; i < widths.length; i++) {
            userTable.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBorder(new EmptyBorder(0, 10, 0, 10));
                if (!isSelected) {
                    setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 250, 253));
                }
                return this;
            }
        };
        for (int i = 0; i < columns.length; i++) {
            userTable.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
        }

        JScrollPane scrollPane = new JScrollPane(userTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        JPanel tableCard = new JPanel(new BorderLayout());
        tableCard.setBackground(Color.WHITE);
        tableCard.setBorder(BorderFactory.createCompoundBorder(
                new EmptyBorder(8, 16, 16, 16),
                BorderFactory.createLineBorder(new Color(220, 223, 228), 1)
        ));
        tableCard.add(scrollPane, BorderLayout.CENTER);

        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.setOpaque(false);
        northPanel.add(topBar, BorderLayout.NORTH);
        northPanel.add(toolbar, BorderLayout.SOUTH);

        root.add(northPanel, BorderLayout.NORTH);
        root.add(tableCard, BorderLayout.CENTER);
    }

    private void openCreateUserDialog() {
        CreateUserDialog dialog = new CreateUserDialog(this, userController);
        dialog.setVisible(true);
        if (dialog.isCreated()) {
            loadUsers();
        }
    }

    private void loadUsers() {
        List<UserDTO> users = userController.getAllUsers();
        tableModel.setRowCount(0);
        for (UserDTO u : users) {
            tableModel.addRow(new Object[]{
                u.getId(),
                u.getUsername(),
                u.getFullName(),
                u.getEmail(),
                u.getDepartment(),
                u.getIsActive() ? "Yes" : "No"
            });
        }
    }
}
