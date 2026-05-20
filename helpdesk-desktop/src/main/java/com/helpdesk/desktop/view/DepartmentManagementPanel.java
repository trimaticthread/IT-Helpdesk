package com.helpdesk.desktop.view;

import com.helpdesk.desktop.controller.DepartmentController;
import com.helpdesk.domain.entity.Department;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class DepartmentManagementPanel extends JPanel {

    private final DepartmentController departmentController;
    private DefaultTableModel tableModel;
    private JTable departmentTable;

    public DepartmentManagementPanel(DepartmentController departmentController) {
        this.departmentController = departmentController;
        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250));
        initUI();
        loadDepartments();
    }

    private void initUI() {
        // ─── ARAÇ ÇUBUĞU ─────────────────────────────────────────────────────
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        toolbar.setBackground(new Color(245, 247, 250));
        toolbar.setBorder(new EmptyBorder(4, 12, 0, 12));

        JButton addButton = new JButton("+ Add Department");
        addButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        addButton.setBackground(new Color(41, 98, 255));
        addButton.setForeground(Color.WHITE);
        addButton.setFocusPainted(false);
        addButton.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        addButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addButton.addActionListener(e -> openAddDialog());

        JButton toggleButton = new JButton("Activate / Deactivate");
        toggleButton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        toggleButton.setFocusPainted(false);
        toggleButton.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        toggleButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        toggleButton.setEnabled(false);
        toggleButton.addActionListener(e -> toggleSelected());

        JButton deleteButton = new JButton("Delete");
        deleteButton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        deleteButton.setForeground(new Color(200, 50, 50));
        deleteButton.setFocusPainted(false);
        deleteButton.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        deleteButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        deleteButton.setEnabled(false);
        deleteButton.addActionListener(e -> deleteSelected());

        JButton refreshButton = new JButton("Refresh");
        refreshButton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        refreshButton.setFocusPainted(false);
        refreshButton.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        refreshButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        refreshButton.addActionListener(e -> loadDepartments());

        toolbar.add(addButton);
        toolbar.add(toggleButton);
        toolbar.add(deleteButton);
        toolbar.add(refreshButton);

        // ─── DEPARTMAN TABLOSU ────────────────────────────────────────────────
        String[] columns = {"ID", "Department Name", "Active"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        departmentTable = new JTable(tableModel);
        departmentTable.getSelectionModel().addListSelectionListener(e -> {
            int row = departmentTable.getSelectedRow();
            boolean selected = row != -1;
            toggleButton.setEnabled(selected);
            deleteButton.setEnabled(selected);
            if (selected) {
                String active = (String) tableModel.getValueAt(row, 2);
                toggleButton.setText("Yes".equals(active) ? "Deactivate" : "Activate");
            } else {
                toggleButton.setText("Activate / Deactivate");
            }
        });

        departmentTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        departmentTable.setRowHeight(32);
        departmentTable.setShowGrid(false);
        departmentTable.setIntercellSpacing(new Dimension(0, 0));
        departmentTable.setSelectionBackground(new Color(220, 230, 255));
        departmentTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        departmentTable.getTableHeader().setBackground(new Color(235, 238, 245));
        departmentTable.getTableHeader().setForeground(new Color(80, 90, 110));

        departmentTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        departmentTable.getColumnModel().getColumn(1).setPreferredWidth(400);
        departmentTable.getColumnModel().getColumn(2).setPreferredWidth(80);

        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBorder(new EmptyBorder(0, 10, 0, 10));
                if (!isSelected) {
                    String active = (String) tableModel.getValueAt(row, 2);
                    if ("No".equals(active)) {
                        setBackground(new Color(245, 245, 245));
                        setForeground(Color.GRAY);
                    } else {
                        setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 250, 253));
                        setForeground(Color.BLACK);
                    }
                }
                return this;
            }
        };
        for (int i = 0; i < columns.length; i++) {
            departmentTable.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
        }

        JScrollPane scrollPane = new JScrollPane(departmentTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        JPanel tableCard = new JPanel(new BorderLayout());
        tableCard.setBackground(Color.WHITE);
        tableCard.setBorder(BorderFactory.createCompoundBorder(
                new EmptyBorder(8, 16, 16, 16),
                BorderFactory.createLineBorder(new Color(220, 223, 228), 1)
        ));
        tableCard.add(scrollPane, BorderLayout.CENTER);

        add(toolbar, BorderLayout.NORTH);
        add(tableCard, BorderLayout.CENTER);
    }

    private void loadDepartments() {
        List<Department> departments = departmentController.getAllDepartments();
        tableModel.setRowCount(0);
        for (Department d : departments) {
            tableModel.addRow(new Object[]{d.getId(), d.getName(), d.getIsActive() ? "Yes" : "No"});
        }
    }

    private void openAddDialog() {
        JTextField nameField = new JTextField(24);
        JPanel panel = new JPanel(new GridLayout(0, 1, 4, 4));
        panel.add(new JLabel("Department Name:"));
        panel.add(nameField);

        int result = JOptionPane.showConfirmDialog(this, panel,
                "Add Department", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            try {
                departmentController.createDepartment(nameField.getText());
                loadDepartments();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void toggleSelected() {
        int row = departmentTable.getSelectedRow();
        if (row == -1) return;
        Long id = (Long) tableModel.getValueAt(row, 0);
        try {
            departmentController.toggleActive(id);
            loadDepartments();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelected() {
        int row = departmentTable.getSelectedRow();
        if (row == -1) return;
        Long id = (Long) tableModel.getValueAt(row, 0);
        String name = (String) tableModel.getValueAt(row, 1);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete department '" + name + "'?", "Confirm Delete",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                departmentController.deleteDepartment(id);
                loadDepartments();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
