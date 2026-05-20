package com.helpdesk.desktop.view;

import com.helpdesk.desktop.controller.CategoryController;
import com.helpdesk.domain.entity.Category;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class CategoryManagementPanel extends JPanel {

    private final CategoryController categoryController;
    private DefaultTableModel tableModel;
    private JTable categoryTable;

    public CategoryManagementPanel(CategoryController categoryController) {
        this.categoryController = categoryController;
        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250));
        initUI();
        loadCategories();
    }

    private void initUI() {
        // ─── ARAÇ ÇUBUĞU ─────────────────────────────────────────────────────
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        toolbar.setBackground(new Color(245, 247, 250));
        toolbar.setBorder(new EmptyBorder(4, 12, 0, 12));

        JButton addButton = new JButton("+ Add Category");
        addButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        addButton.setBackground(new Color(41, 98, 255));
        addButton.setForeground(Color.WHITE);
        addButton.setFocusPainted(false);
        addButton.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        addButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addButton.addActionListener(e -> openAddCategoryDialog());

        JButton toggleButton = new JButton("Activate / Deactivate");
        toggleButton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        toggleButton.setFocusPainted(false);
        toggleButton.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        toggleButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        toggleButton.setEnabled(false);
        toggleButton.addActionListener(e -> toggleSelectedCategory());

        JButton deleteButton = new JButton("Delete");
        deleteButton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        deleteButton.setForeground(new Color(200, 50, 50));
        deleteButton.setFocusPainted(false);
        deleteButton.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        deleteButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        deleteButton.setEnabled(false);
        deleteButton.addActionListener(e -> deleteSelectedCategory());

        JButton refreshButton = new JButton("Refresh");
        refreshButton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        refreshButton.setFocusPainted(false);
        refreshButton.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        refreshButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        refreshButton.addActionListener(e -> loadCategories());

        toolbar.add(addButton);
        toolbar.add(toggleButton);
        toolbar.add(deleteButton);
        toolbar.add(refreshButton);

        // ─── KATEGORİ TABLOSU ─────────────────────────────────────────────────
        String[] columns = {"ID", "Name", "Description", "Active"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        categoryTable = new JTable(tableModel);
        categoryTable.getSelectionModel().addListSelectionListener(e -> {
            int row = categoryTable.getSelectedRow();
            boolean selected = row != -1;
            toggleButton.setEnabled(selected);
            deleteButton.setEnabled(selected);
            if (selected) {
                String active = (String) tableModel.getValueAt(row, 3);
                toggleButton.setText("Yes".equals(active) ? "Deactivate" : "Activate");
            } else {
                toggleButton.setText("Activate / Deactivate");
            }
        });
        categoryTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        categoryTable.setRowHeight(32);
        categoryTable.setShowGrid(false);
        categoryTable.setIntercellSpacing(new Dimension(0, 0));
        categoryTable.setSelectionBackground(new Color(220, 230, 255));
        categoryTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        categoryTable.getTableHeader().setBackground(new Color(235, 238, 245));
        categoryTable.getTableHeader().setForeground(new Color(80, 90, 110));

        int[] widths = {50, 180, 360, 80};
        for (int i = 0; i < widths.length; i++) {
            categoryTable.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBorder(new EmptyBorder(0, 10, 0, 10));
                if (!isSelected) {
                    // Pasif kategoriler gri gösterilir
                    String active = (String) tableModel.getValueAt(row, 3);
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
            categoryTable.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
        }

        JScrollPane scrollPane = new JScrollPane(categoryTable);
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

    private void loadCategories() {
        List<Category> categories = categoryController.getAllCategories();
        tableModel.setRowCount(0);
        for (Category c : categories) {
            tableModel.addRow(new Object[]{
                c.getId(), c.getName(),
                c.getDescription() != null ? c.getDescription() : "",
                c.getIsActive() ? "Yes" : "No"
            });
        }
    }

    private void openAddCategoryDialog() {
        JTextField nameField = new JTextField(24);
        JTextField descField = new JTextField(24);

        JPanel panel = new JPanel(new GridLayout(0, 1, 4, 4));
        panel.add(new JLabel("Category Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Description (optional):"));
        panel.add(descField);

        int result = JOptionPane.showConfirmDialog(this, panel,
                "Add Category", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            try {
                categoryController.createCategory(nameField.getText(), descField.getText());
                loadCategories();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void toggleSelectedCategory() {
        int row = categoryTable.getSelectedRow();
        if (row == -1) return;
        Long id = (Long) tableModel.getValueAt(row, 0);
        try {
            categoryController.toggleActive(id);
            loadCategories();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelectedCategory() {
        int row = categoryTable.getSelectedRow();
        if (row == -1) return;
        Long id = (Long) tableModel.getValueAt(row, 0);
        String name = (String) tableModel.getValueAt(row, 1);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete category '" + name + "'?", "Confirm Delete",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                categoryController.deleteCategory(id);
                loadCategories();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
