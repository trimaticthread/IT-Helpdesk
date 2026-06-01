package com.helpdesk.desktop.view;

import com.helpdesk.desktop.controller.SlaController;
import com.helpdesk.domain.entity.SlaSettings;
import com.helpdesk.domain.enums.TicketPriority;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class SlaManagementPanel extends JPanel {

    private final SlaController slaController;
    private DefaultTableModel tableModel;
    private JTable slaTable;

    public SlaManagementPanel(SlaController slaController) {
        this.slaController = slaController;
        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250));
        initUI();
        loadSla();
    }

    private void initUI() {
        // ─── ARAÇ ÇUBUĞU ─────────────────────────────────────────────────────
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        toolbar.setBackground(new Color(245, 247, 250));
        toolbar.setBorder(new EmptyBorder(4, 12, 0, 12));

        JButton editButton = new JButton("Edit SLA");
        editButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        editButton.setBackground(new Color(41, 98, 255));
        editButton.setForeground(Color.WHITE);
        editButton.setFocusPainted(false);
        editButton.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        editButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        editButton.setEnabled(false);
        editButton.addActionListener(e -> editSelectedSla());

        JButton refreshButton = new JButton("Refresh");
        refreshButton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        refreshButton.setFocusPainted(false);
        refreshButton.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        refreshButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        refreshButton.addActionListener(e -> loadSla());

        toolbar.add(editButton);
        toolbar.add(refreshButton);

        // ─── SLA TABLOSU ─────────────────────────────────────────────────────
        String[] columns = {"Priority", "Response Time (min)", "Resolution Time (min)", "Response", "Resolution"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        slaTable = new JTable(tableModel);
        slaTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        slaTable.setRowHeight(36);
        slaTable.setShowGrid(false);
        slaTable.setIntercellSpacing(new Dimension(0, 0));
        slaTable.setSelectionBackground(new Color(220, 230, 255));
        slaTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        slaTable.getTableHeader().setBackground(new Color(235, 238, 245));
        slaTable.getTableHeader().setForeground(new Color(80, 90, 110));

        slaTable.getSelectionModel().addListSelectionListener(e ->
                editButton.setEnabled(slaTable.getSelectedRow() != -1));

        // Önceliğe göre renk
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            private final Color[] rowColors = {
                new Color(255, 230, 230), // CRITICAL - kırmızımsı
                new Color(255, 243, 220), // HIGH - sarımsı
                new Color(230, 245, 230), // MEDIUM - yeşilimsi
                new Color(235, 240, 255)  // LOW - mavimsi
            };
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                setBorder(new EmptyBorder(0, 12, 0, 12));
                if (!sel) setBackground(row < rowColors.length ? rowColors[row] : Color.WHITE);
                setFont(col == 0
                        ? new Font("Segoe UI", Font.BOLD, 13)
                        : new Font("Segoe UI", Font.PLAIN, 13));
                return this;
            }
        };
        for (int i = 0; i < columns.length; i++) {
            slaTable.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }
        slaTable.getColumnModel().getColumn(0).setPreferredWidth(120);
        slaTable.getColumnModel().getColumn(1).setPreferredWidth(160);
        slaTable.getColumnModel().getColumn(2).setPreferredWidth(180);
        slaTable.getColumnModel().getColumn(3).setPreferredWidth(130);
        slaTable.getColumnModel().getColumn(4).setPreferredWidth(150);

        JScrollPane scroll = new JScrollPane(slaTable);
        scroll.setBorder(BorderFactory.createEmptyBorder());

        JPanel tableCard = new JPanel(new BorderLayout());
        tableCard.setBackground(Color.WHITE);
        tableCard.setBorder(BorderFactory.createCompoundBorder(
                new EmptyBorder(8, 16, 16, 16),
                BorderFactory.createLineBorder(new Color(220, 223, 228), 1)));
        tableCard.add(scroll, BorderLayout.CENTER);

        add(toolbar, BorderLayout.NORTH);
        add(tableCard, BorderLayout.CENTER);
    }

    private void loadSla() {
        List<SlaSettings> list = slaController.getAllSlaSettings();
        tableModel.setRowCount(0);
        for (SlaSettings s : list) {
            int resp = s.getResponseTimeMinutes();
            int resol = s.getResolutionTimeMinutes();
            tableModel.addRow(new Object[]{
                s.getPriority().name(),
                resp,
                resol,
                formatMinutes(resp),
                formatMinutes(resol)
            });
        }
    }

    private String formatMinutes(int minutes) {
        if (minutes < 60) return minutes + " min";
        int h = minutes / 60;
        int m = minutes % 60;
        return m == 0 ? h + " hr" : h + " hr " + m + " min";
    }

    private void editSelectedSla() {
        int row = slaTable.getSelectedRow();
        if (row == -1) return;

        String priorityName = (String) tableModel.getValueAt(row, 0);
        int currentResp = (int) tableModel.getValueAt(row, 1);
        int currentResol = (int) tableModel.getValueAt(row, 2);

        JTextField respField = new JTextField(String.valueOf(currentResp), 8);
        JTextField resolField = new JTextField(String.valueOf(currentResol), 8);

        JPanel panel = new JPanel(new GridLayout(0, 2, 8, 8));
        panel.add(new JLabel("Priority:")); panel.add(new JLabel(priorityName));
        panel.add(new JLabel("Response Time (min):")); panel.add(respField);
        panel.add(new JLabel("Resolution Time (min):")); panel.add(resolField);

        int result = JOptionPane.showConfirmDialog(this, panel,
                "Edit SLA — " + priorityName,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                int resp = Integer.parseInt(respField.getText().trim());
                int resol = Integer.parseInt(resolField.getText().trim());
                TicketPriority priority = TicketPriority.valueOf(priorityName);
                slaController.updateSla(priority, resp, resol);
                loadSla();
                JOptionPane.showMessageDialog(this, "SLA settings updated.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter valid numbers.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
