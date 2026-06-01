package com.helpdesk.desktop.view;

import com.helpdesk.desktop.controller.PasswordResetRequestController;
import com.helpdesk.domain.entity.PasswordResetRequest;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ResetRequestsPanel extends JPanel {

    private final PasswordResetRequestController prrController;
    private DefaultTableModel tableModel;
    private JTable table;
    private List<PasswordResetRequest> currentRequests;

    public ResetRequestsPanel(PasswordResetRequestController prrController) {
        this.prrController = prrController;
        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250));
        initUI();
        loadRequests();
    }

    private void initUI() {
        // ─── TOOLBAR ──────────────────────────────────────────────────────────
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        toolbar.setBackground(new Color(245, 247, 250));
        toolbar.setBorder(new EmptyBorder(4, 12, 0, 12));

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        refreshBtn.setFocusPainted(false);
        refreshBtn.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        refreshBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        refreshBtn.addActionListener(e -> loadRequests());

        JButton approveBtn = new JButton("Approve");
        approveBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        approveBtn.setBackground(new Color(41, 98, 255));
        approveBtn.setForeground(Color.WHITE);
        approveBtn.setFocusPainted(false);
        approveBtn.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        approveBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        approveBtn.setEnabled(false);
        approveBtn.addActionListener(e -> approveSelected());

        toolbar.add(refreshBtn);
        toolbar.add(approveBtn);
        add(toolbar, BorderLayout.NORTH);

        // ─── TABLO ────────────────────────────────────────────────────────────
        String[] cols = {"ID", "Username", "Full Name", "E-mail", "Requested At"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(tableModel);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(32);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(220, 230, 255));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(235, 238, 245));

        // ID kolonu gizle
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setWidth(0);

        int[] widths = {0, 140, 160, 220, 140};
        for (int i = 1; i < widths.length; i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        table.getSelectionModel().addListSelectionListener(e ->
                approveBtn.setEnabled(table.getSelectedRow() != -1));

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(Color.WHITE);

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                new EmptyBorder(8, 16, 16, 16),
                BorderFactory.createLineBorder(new Color(220, 223, 228), 1)
        ));
        card.add(scroll, BorderLayout.CENTER);
        add(card, BorderLayout.CENTER);
    }

    private void loadRequests() {
        currentRequests = prrController.getPendingRequests();
        tableModel.setRowCount(0);
        for (PasswordResetRequest r : currentRequests) {
            tableModel.addRow(new Object[]{
                r.getId(),
                r.getUsername(),
                r.getFirstName() + " " + r.getLastName(),
                r.getEmail(),
                r.getCreatedAt() != null ? r.getCreatedAt().toString().replace("T", " ").substring(0, 16) : ""
            });
        }
        if (currentRequests.isEmpty()) {
            tableModel.addRow(new Object[]{null, "—", "No pending requests", "", ""});
        }
    }

    private void approveSelected() {
        int row = table.getSelectedRow();
        if (row == -1) return;
        Long id = (Long) tableModel.getValueAt(row, 0);
        if (id == null) return;

        String username = (String) tableModel.getValueAt(row, 1);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Approve password reset for " + username + "?\nTheir password will be set to 'password'.",
                "Approve Reset", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            prrController.approveRequest(id);
            JOptionPane.showMessageDialog(this,
                    username + "'s password has been reset to 'password'.\nThey will be required to change it on next login.",
                    "Approved", JOptionPane.INFORMATION_MESSAGE);
            loadRequests();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
