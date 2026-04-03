package com.helpdesk.desktop.view;

import com.helpdesk.application.dto.TicketDTO;
import com.helpdesk.desktop.controller.AuthController;
import com.helpdesk.desktop.controller.TicketController;
import com.helpdesk.desktop.security.SessionManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class DashboardFrame extends JFrame {

    private final AuthController authController;
    private final TicketController ticketController;
    private JTable ticketTable;
    private DefaultTableModel tableModel;

    public DashboardFrame(AuthController authController, TicketController ticketController) {
        this.authController = authController;
        this.ticketController = ticketController;
        initUI();
        loadTickets();
    }

    private void initUI() {
        setTitle("IT Helpdesk");
        setSize(1000, 640);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Ana container
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(new Color(245, 247, 250));
        setContentPane(root);

        // Üst bar
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(30, 40, 60));
        topBar.setBorder(new EmptyBorder(12, 20, 12, 20));

        JLabel appTitle = new JLabel("IT Helpdesk");
        appTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        appTitle.setForeground(Color.WHITE);

        JPanel rightTop = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        rightTop.setOpaque(false);

        String name = SessionManager.getCurrentUser() != null
                ? SessionManager.getCurrentUser().getFullName() : "Kullanici";
        JLabel welcomeLabel = new JLabel("Hosgeldin, " + name);
        welcomeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        welcomeLabel.setForeground(new Color(180, 190, 210));

        JButton logoutButton = new JButton("Cikis");
        logoutButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        logoutButton.setBackground(new Color(60, 70, 95));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        logoutButton.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));
        logoutButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logoutButton.addActionListener(e -> {
            authController.logout();
            dispose();
            new LoginFrame(authController, ticketController).setVisible(true);
        });

        rightTop.add(welcomeLabel);
        rightTop.add(logoutButton);
        topBar.add(appTitle, BorderLayout.WEST);
        topBar.add(rightTop, BorderLayout.EAST);

        // Araç çubuğu
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        toolbar.setBackground(new Color(245, 247, 250));
        toolbar.setBorder(new EmptyBorder(4, 12, 0, 12));

        JButton newTicketButton = new JButton("+ Yeni Ticket");
        newTicketButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        newTicketButton.setBackground(new Color(41, 98, 255));
        newTicketButton.setForeground(Color.WHITE);
        newTicketButton.setFocusPainted(false);
        newTicketButton.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        newTicketButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        newTicketButton.addActionListener(e -> openCreateTicketDialog());

        JButton refreshButton = new JButton("Yenile");
        refreshButton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        refreshButton.setFocusPainted(false);
        refreshButton.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        refreshButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        refreshButton.addActionListener(e -> loadTickets());

        toolbar.add(newTicketButton);
        toolbar.add(refreshButton);

        // Tablo
        String[] columns = {"Ticket No", "Baslik", "Durum", "Oncelik", "Kategori", "Tarih"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        ticketTable = new JTable(tableModel);
        ticketTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        ticketTable.setRowHeight(32);
        ticketTable.setShowGrid(false);
        ticketTable.setIntercellSpacing(new Dimension(0, 0));
        ticketTable.setSelectionBackground(new Color(220, 230, 255));
        ticketTable.setSelectionForeground(new Color(30, 40, 60));
        ticketTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        ticketTable.getTableHeader().setBackground(new Color(235, 238, 245));
        ticketTable.getTableHeader().setForeground(new Color(80, 90, 110));
        ticketTable.getTableHeader().setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));

        // Sütun genişlikleri
        int[] widths = {120, 260, 90, 80, 130, 100};
        for (int i = 0; i < widths.length; i++) {
            ticketTable.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        // Hücre padding
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
            ticketTable.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
        }

        JScrollPane scrollPane = new JScrollPane(ticketTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);

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

    private void openCreateTicketDialog() {
        CreateTicketDialog dialog = new CreateTicketDialog(this, ticketController);
        dialog.setVisible(true);
        if (dialog.isSubmitted()) {
            loadTickets();
        }
    }

    private void loadTickets() {
        List<TicketDTO> tickets = ticketController.getAllTickets();
        tableModel.setRowCount(0);
        for (TicketDTO t : tickets) {
            tableModel.addRow(new Object[]{
                t.getTicketNumber(),
                t.getTitle(),
                t.getStatus(),
                t.getPriority(),
                t.getCategoryName(),
                t.getCreatedAt() != null ? t.getCreatedAt().toLocalDate().toString() : ""
            });
        }
    }
}
