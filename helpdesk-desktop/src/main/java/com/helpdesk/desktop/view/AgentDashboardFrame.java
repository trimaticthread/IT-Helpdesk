package com.helpdesk.desktop.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import com.helpdesk.application.dto.TicketDTO;
import com.helpdesk.desktop.controller.AuthController;
import com.helpdesk.desktop.controller.TicketController;
import com.helpdesk.desktop.controller.UserController;
import com.helpdesk.desktop.security.SessionManager;
import com.helpdesk.domain.enums.TicketStatus;

/**
 * AGENT rolune ozel dashboard ekrani. Agent yalnizca kendisine atanmis
 * ticket'lari gorebilir. Ticket durumunu guncelleyebilir; kullanici yonetimine
 * veya raporlara erisemez.
 *
 * Ozellikler: - Yalnizca aktif agent'a atanmis ticket'lari listeler
 * (findByAssigneeId). - "Change Status" butonu ile secili ticket'in durumu
 * degistirilebilir. - Durum secenekleri: IN_PROGRESS, PENDING, RESOLVED,
 * CLOSED. - Ust barda hosgeldin mesaji ve cikis butonu bulunur.
 */
public class AgentDashboardFrame extends JFrame {

    private final AuthController authController;
    private final TicketController ticketController;
    private final UserController userController;
    private final com.helpdesk.desktop.controller.CategoryController categoryController;
    private final com.helpdesk.desktop.controller.DepartmentController departmentController;
    private final com.helpdesk.desktop.controller.GroupController groupController;
    private final com.helpdesk.desktop.controller.SlaController slaController;
    private DefaultTableModel tableModel;
    private JTable ticketTable;
    private java.util.List<com.helpdesk.application.dto.TicketDTO> currentTickets = new java.util.ArrayList<>();
    private boolean showResolved = false;

    public AgentDashboardFrame(AuthController authController, TicketController ticketController,
                               UserController userController,
                               com.helpdesk.desktop.controller.CategoryController categoryController,
                               com.helpdesk.desktop.controller.DepartmentController departmentController,
                               com.helpdesk.desktop.controller.GroupController groupController,
                               com.helpdesk.desktop.controller.SlaController slaController) {
        this.authController = authController;
        this.ticketController = ticketController;
        this.userController = userController;
        this.categoryController = categoryController;
        this.departmentController = departmentController;
        this.groupController = groupController;
        this.slaController = slaController;
        initUI();
        loadTickets();
    }

    private void initUI() {
        setTitle("IT Helpdesk — Agent");
        setSize(960, 620);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // ─── ANA PANEL ───────────────────────────────────────────────────────
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(new Color(245, 247, 250));
        setContentPane(root);

        // ─── ÜST BAR ─────────────────────────────────────────────────────────
        // Koyu bar; sol=başlık, sağ=kullanıcı adı + çıkış
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(30, 40, 60));
        topBar.setBorder(new EmptyBorder(12, 20, 12, 20));

        // Panel başlığı — hangi role ait olduğunu belirtir
        JLabel appTitle = new JLabel("IT Helpdesk — Agent Panel");
        appTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        appTitle.setForeground(Color.WHITE);

        // Sağ üst: kullanıcı adı + çıkış butonu
        JPanel rightTop = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        rightTop.setOpaque(false);

        // Oturum açmış agent'ın tam adı
        String name = SessionManager.getCurrentUser() != null
                ? SessionManager.getCurrentUser().getFullName() : "Agent";
        JLabel welcomeLabel = new JLabel("Welcome, " + name);
        welcomeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        welcomeLabel.setForeground(new Color(180, 190, 210));

        // Çıkış butonu — oturumu sona erdirir
        JButton logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        logoutButton.setBackground(new Color(60, 70, 95));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        logoutButton.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));
        logoutButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logoutButton.addActionListener(e -> {
            authController.logout();
            dispose();
            new LoginFrame(authController, ticketController, userController, categoryController, departmentController, groupController, slaController).setVisible(true);
        });

        rightTop.add(welcomeLabel);
        rightTop.add(logoutButton);
        topBar.add(appTitle, BorderLayout.WEST);
        topBar.add(rightTop, BorderLayout.EAST);

        // ─── ARAÇ ÇUBUĞU ─────────────────────────────────────────────────────
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        toolbar.setBackground(new Color(245, 247, 250));
        toolbar.setBorder(new EmptyBorder(4, 12, 0, 12));

        // Yeni ticket butonu — agent da ticket oluşturabilir (CreateTicketDialog)
        JButton newTicketButton = new JButton("+ New Ticket");
        newTicketButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        newTicketButton.setBackground(new Color(41, 98, 255));
        newTicketButton.setForeground(Color.WHITE);
        newTicketButton.setFocusPainted(false);
        newTicketButton.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        newTicketButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        newTicketButton.addActionListener(e -> {
            CreateTicketDialog dialog = new CreateTicketDialog(this, ticketController);
            dialog.setVisible(true);
            if (dialog.isSubmitted()) {
                loadTickets();
            }
        });

        // Yenile butonu — atanan ticket listesini tekrar yükler
        JButton refreshButton = new JButton("Refresh");
        refreshButton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        refreshButton.setFocusPainted(false);
        refreshButton.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        refreshButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        refreshButton.addActionListener(e -> loadTickets());

        // Durum değiştir butonu — tabloda satır seçilmeden devre dışı kalır
        // Tıklanınca JOptionPane ile yeni durum seçilir
        JButton changeStatusButton = new JButton("Change Status");
        changeStatusButton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        changeStatusButton.setFocusPainted(false);
        changeStatusButton.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        changeStatusButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        changeStatusButton.setEnabled(false); // Başlangıçta pasif
        changeStatusButton.addActionListener(e -> openChangeStatusDialog());

        JCheckBox showResolvedCheck = new JCheckBox("Show Resolved");
        showResolvedCheck.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        showResolvedCheck.setOpaque(false);
        showResolvedCheck.addActionListener(e -> { showResolved = showResolvedCheck.isSelected(); loadTickets(); });

        toolbar.add(newTicketButton);
        toolbar.add(refreshButton);
        toolbar.add(changeStatusButton);
        toolbar.add(showResolvedCheck);

        // ─── TİCKET TABLOSU ──────────────────────────────────────────────────
        // ID kolonu gizlidir; sadece seçili satırın ticket ID'sini almak için saklanır
        String[] columns = {"ID", "Ticket No", "Title", "Status", "Priority", "Category", "Date"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        ticketTable = new JTable(tableModel);
        ticketTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        ticketTable.setRowHeight(32);
        ticketTable.setShowGrid(false);
        ticketTable.setIntercellSpacing(new Dimension(0, 0));
        ticketTable.setSelectionBackground(new Color(220, 230, 255));
        ticketTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        ticketTable.getTableHeader().setBackground(new Color(235, 238, 245));
        ticketTable.getTableHeader().setForeground(new Color(80, 90, 110));

        // ID kolonu (index 0) gizlenir — kullanıcı görmez ama kod okuyabilir
        ticketTable.getColumnModel().getColumn(0).setMinWidth(0);
        ticketTable.getColumnModel().getColumn(0).setMaxWidth(0);
        ticketTable.getColumnModel().getColumn(0).setWidth(0);

        // Görünen sütun genişlikleri: Ticket No, Başlık, Durum, Öncelik, Kategori, Tarih
        int[] widths = {0, 110, 240, 100, 80, 130, 100};
        for (int i = 1; i < widths.length; i++) {
            ticketTable.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        // Tablo seçim dinleyicisi — satır seçilirse "Change Status" aktif olur
        ticketTable.getSelectionModel().addListSelectionListener(e
                -> changeStatusButton.setEnabled(ticketTable.getSelectedRow() != -1));

        // Çift tıklamada ViewTicketDialog açılır — agent modu (isAgent=true)
        // Dahili yorumlar görünür + "Internal note" checkbox aktif
        ticketTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = ticketTable.getSelectedRow();
                    if (row >= 0 && row < currentTickets.size()) {
                        new ViewTicketDialog(
                                AgentDashboardFrame.this,
                                currentTickets.get(row),
                                ticketController,
                                true // isAgent=true: dahili yorumlar + internal checkbox
                        ).setVisible(true);
                        loadTickets();
                    }
                }
            }
        });

        // Zebra + RESOLVED/CLOSED satırları gri
        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int col) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                setBorder(new EmptyBorder(0, 10, 0, 10));
                if (!isSelected) {
                    Object status = tableModel.getValueAt(row, 3);
                    if ("RESOLVED".equals(status) || "CLOSED".equals(status)) {
                        setBackground(new Color(240, 240, 240));
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

        // ─── PANEL BİRLEŞTİRME ───────────────────────────────────────────────
        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.setOpaque(false);
        northPanel.add(topBar, BorderLayout.NORTH);
        northPanel.add(toolbar, BorderLayout.SOUTH);

        root.add(northPanel, BorderLayout.NORTH);
        root.add(tableCard, BorderLayout.CENTER);
    }

    /**
     * Seçili ticket'ın durumunu değiştirmek için JOptionPane açar. Seçilen
     * status TicketController.updateStatus() ile kaydedilir.
     */
    private void openChangeStatusDialog() {
        int row = ticketTable.getSelectedRow();
        if (row == -1) return;
        Long ticketId = (Long) tableModel.getValueAt(row, 0);

        TicketStatus[] options = {
            TicketStatus.IN_PROGRESS, TicketStatus.PENDING, TicketStatus.RESOLVED
        };

        // Mevcut status'u bul, default olarak göster
        TicketStatus currentStatus = null;
        try { currentStatus = TicketStatus.valueOf(currentTickets.get(row).getStatus()); } catch (Exception ignored) {}
        final TicketStatus defaultOption = java.util.Arrays.asList(options).contains(currentStatus)
                ? currentStatus : options[0];

        TicketStatus selected = (TicketStatus) JOptionPane.showInputDialog(
                this,
                "Current status: " + (currentStatus != null ? currentStatus : "—") + "\nSelect new status:",
                "Change Status",
                JOptionPane.PLAIN_MESSAGE, null, options, defaultOption);

        if (selected != null) {
            try {
                ticketController.updateStatus(ticketId, selected);
                loadTickets(); // Güncelleme sonrası tabloyu yenile
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void loadTickets() {
        if (showResolved) {
            // CLOSED hariç tümü — RESOLVED dahil
            currentTickets = ticketController.getAllAssignedTickets();
        } else {
            // Sadece aktif ticket'lar
            currentTickets = ticketController.getAssignedTickets();
        }
        tableModel.setRowCount(0);
        for (TicketDTO t : currentTickets) {
            tableModel.addRow(new Object[]{
                t.getId(), // Gizli ID kolonu
                t.getTicketNumber(), t.getTitle(), t.getStatus(), t.getPriority(),
                t.getCategoryName(),
                t.getCreatedAt() != null ? t.getCreatedAt().toLocalDate().toString() : ""
            });
        }
    }
}
