package com.helpdesk.desktop.view;

import com.helpdesk.application.dto.TicketDTO;
import com.helpdesk.desktop.controller.AuthController;
import com.helpdesk.desktop.controller.TicketController;
import com.helpdesk.desktop.security.SessionManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class DashboardFrame extends JFrame {

    private final AuthController authController;
    private final TicketController ticketController;
    private JTable ticketTable;
    private DefaultTableModel tableModel;
    private JLabel welcomeLabel;

    public DashboardFrame(AuthController authController, TicketController ticketController) {
        this.authController = authController;
        this.ticketController = ticketController;
        initUI();
        loadTickets();
    }

    private void initUI() {
        setTitle("IT Helpdesk - Dashboard");
        setSize(900, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel topPanel = new JPanel(new BorderLayout());
        welcomeLabel = new JLabel("Hosgeldin, " + SessionManager.getCurrentUser().getFullName());
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        welcomeLabel.setFont(welcomeLabel.getFont().deriveFont(14f));

        JButton logoutButton = new JButton("Cikis");
        logoutButton.addActionListener(e -> {
            authController.logout();
            dispose();
            LoginFrame loginFrame = new LoginFrame(authController, ticketController);
            loginFrame.setVisible(true);
        });

        topPanel.add(welcomeLabel, BorderLayout.WEST);
        topPanel.add(logoutButton, BorderLayout.EAST);

        String[] columns = {"Ticket No", "Baslik", "Durum", "Oncelik", "Kategori", "Tarih"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        ticketTable = new JTable(tableModel);
        ticketTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton refreshButton = new JButton("Yenile");
        refreshButton.addActionListener(e -> loadTickets());
        bottomPanel.add(refreshButton);

        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(ticketTable), BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
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
