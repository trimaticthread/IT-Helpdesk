package com.helpdesk.desktop.view;

import com.helpdesk.application.dto.TicketDTO;
import com.helpdesk.desktop.controller.AuthController;
import com.helpdesk.desktop.controller.TicketController;
import com.helpdesk.desktop.controller.UserController;
import com.helpdesk.desktop.security.SessionManager;
import com.helpdesk.domain.enums.TicketStatus;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * SUPERVISOR rolune ozel dashboard ekrani.
 * Supervisor sistemdeki tum ticket'lari goruntular ve yonetir;
 * kullanici olusturamaz/silemez, sistem ayarlarini degistiremez.
 *
 * Sekmeler:
 * - Tickets: Tum ticket'lari listeler. Secili ticket'in durumu degistirilebilir.
 * - Reports : Ticket istatistiklerini ozetler (toplam, duruma gore, onceliğe gore).
 *
 * Ozellikler:
 * - "Change Status" → Secili ticket'in durumunu gunceller.
 * - "Refresh"       → Tabloyu veritabanindan yeniden yukler.
 * - Reports sekmesi her acildiginda anlik istatistikleri hesaplar.
 */
public class SupervisorDashboardFrame extends JFrame {

    private final AuthController authController;
    private final TicketController ticketController;
    private final UserController userController;
    private DefaultTableModel tableModel;
    private JTable ticketTable;
    private JPanel reportsPanel; // Reports sekmesi içeriği buraya eklenir/sıfırlanır
    // ViewTicketDialog ve Assign Ticket için DTO listesi hafızada tutulur
    private java.util.List<TicketDTO> currentTickets = new java.util.ArrayList<>();

    public SupervisorDashboardFrame(AuthController authController,
                                    TicketController ticketController,
                                    UserController userController) {
        this.authController = authController;
        this.ticketController = ticketController;
        this.userController = userController;
        initUI();
        loadTickets();
    }

    private void initUI() {
        setTitle("IT Helpdesk — Supervisor");
        setSize(1050, 660);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // ─── ANA PANEL ───────────────────────────────────────────────────────
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(new Color(245, 247, 250));
        setContentPane(root);

        // ─── ÜST BAR ─────────────────────────────────────────────────────────
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(30, 40, 60));
        topBar.setBorder(new EmptyBorder(12, 20, 12, 20));

        // Panel başlığı — rol etiketi içerir
        JLabel appTitle = new JLabel("IT Helpdesk — Supervisor Panel");
        appTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        appTitle.setForeground(Color.WHITE);

        // Sağ üst: kullanıcı adı + çıkış
        JPanel rightTop = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        rightTop.setOpaque(false);

        String name = SessionManager.getCurrentUser() != null
                ? SessionManager.getCurrentUser().getFullName() : "Supervisor";
        JLabel welcomeLabel = new JLabel("Welcome, " + name);
        welcomeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        welcomeLabel.setForeground(new Color(180, 190, 210));

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
            new LoginFrame(authController, ticketController, userController).setVisible(true);
        });

        rightTop.add(welcomeLabel);
        rightTop.add(logoutButton);
        topBar.add(appTitle, BorderLayout.WEST);
        topBar.add(rightTop, BorderLayout.EAST);

        root.add(topBar, BorderLayout.NORTH);

        // ─── SEKMELER ────────────────────────────────────────────────────────
        // Tickets: tablo görünümü | Reports: istatistik özeti
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabs.addTab("Tickets", buildTicketsPanel());
        tabs.addTab("Reports", buildReportsPanel());

        // Reports sekmesine her geçildiğinde istatistikler yeniden hesaplanır
        tabs.addChangeListener(e -> {
            if (tabs.getSelectedIndex() == 1) refreshReports();
        });

        root.add(tabs, BorderLayout.CENTER);
    }

    /**
     * "Tickets" sekmesinin içeriğini oluşturur.
     * Toolbar (Refresh + Change Status) + ticket tablosu içerir.
     */
    private JPanel buildTicketsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 247, 250));

        // ─── ARAÇ ÇUBUĞU ─────────────────────────────────────────────────────
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        toolbar.setBackground(new Color(245, 247, 250));
        toolbar.setBorder(new EmptyBorder(4, 12, 0, 12));

        // Yenile — tabloyu veritabanından tekrar çeker
        JButton refreshButton = new JButton("Refresh");
        refreshButton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        refreshButton.setFocusPainted(false);
        refreshButton.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        refreshButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        refreshButton.addActionListener(e -> loadTickets());

        // Durum değiştir — sadece satır seçilince aktif olur
        JButton changeStatusButton = new JButton("Change Status");
        changeStatusButton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        changeStatusButton.setFocusPainted(false);
        changeStatusButton.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        changeStatusButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        changeStatusButton.setEnabled(false); // Başta pasif
        changeStatusButton.addActionListener(e -> openChangeStatusDialog());

        // Ticket atama butonu — seçili ticket'ı bir agent'a atar
        JButton assignButton = new JButton("Assign Ticket");
        assignButton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        assignButton.setFocusPainted(false);
        assignButton.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        assignButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        assignButton.setEnabled(false); // Satır seçilmeden pasif kalır
        assignButton.addActionListener(e -> openAssignDialog());

        toolbar.add(refreshButton);
        toolbar.add(changeStatusButton);
        toolbar.add(assignButton);
        panel.add(toolbar, BorderLayout.NORTH);

        // ─── TİCKET TABLOSU ──────────────────────────────────────────────────
        // ID kolonu gizli; Requester kolonu supervisor'a talep sahibini gösterir
        String[] columns = {"ID", "Ticket No", "Title", "Status", "Priority", "Requester", "Category", "Date"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
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

        // ID kolonu (index 0) gizlenir
        ticketTable.getColumnModel().getColumn(0).setMinWidth(0);
        ticketTable.getColumnModel().getColumn(0).setMaxWidth(0);
        ticketTable.getColumnModel().getColumn(0).setWidth(0);

        // Sütun genişlikleri: Ticket No, Başlık, Durum, Öncelik, Talep Eden, Kategori, Tarih
        int[] widths = {0, 110, 220, 100, 80, 130, 120, 100};
        for (int i = 1; i < widths.length; i++) {
            ticketTable.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        // Satır seçilince "Change Status" ve "Assign Ticket" aktif olur
        ticketTable.getSelectionModel().addListSelectionListener(e -> {
            boolean selected = ticketTable.getSelectedRow() != -1;
            changeStatusButton.setEnabled(selected);
            assignButton.setEnabled(selected);
        });

        // Çift tıklamada ViewTicketDialog açılır — supervisor agent gibi dahili yorumları görebilir
        ticketTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = ticketTable.getSelectedRow();
                    if (row >= 0 && row < currentTickets.size()) {
                        new ViewTicketDialog(
                                SupervisorDashboardFrame.this,
                                currentTickets.get(row),
                                ticketController,
                                true  // isAgent=true: dahili yorumlar görünür
                        ).setVisible(true);
                        loadTickets();
                    }
                }
            }
        });

        // Zebra satır renklendirmesi
        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int col) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                setBorder(new EmptyBorder(0, 10, 0, 10));
                if (!isSelected) setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 250, 253));
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
        panel.add(tableCard, BorderLayout.CENTER);

        return panel;
    }

    /**
     * "Reports" sekmesinin boş container'ını oluşturur.
     * İçerik, sekme açıldığında refreshReports() ile dinamik olarak doldurulur.
     */
    private JPanel buildReportsPanel() {
        reportsPanel = new JPanel();
        reportsPanel.setLayout(new BoxLayout(reportsPanel, BoxLayout.Y_AXIS));
        reportsPanel.setBackground(new Color(245, 247, 250));
        reportsPanel.setBorder(new EmptyBorder(24, 32, 24, 32));
        return reportsPanel;
    }

    /**
     * Reports sekmesini güncel verilerle yeniden doldurur.
     * Tüm ticket'lar çekilir; duruma ve önceliğe göre gruplandırılır.
     */
    private void refreshReports() {
        reportsPanel.removeAll(); // Önceki içeriği temizle

        List<TicketDTO> all = ticketController.getAllTickets();
        int total = all.size();

        // Ticket'ları duruma göre say: {NEW: 3, OPEN: 7, ...}
        Map<String, Long> byStatus = all.stream()
                .collect(Collectors.groupingBy(TicketDTO::getStatus, Collectors.counting()));

        // Ticket'ları önceliğe göre say: {HIGH: 5, MEDIUM: 10, ...}
        Map<String, Long> byPriority = all.stream()
                .collect(Collectors.groupingBy(TicketDTO::getPriority, Collectors.counting()));

        // ─── RAPOR BAŞLIĞI ────────────────────────────────────────────────────
        JLabel titleLabel = new JLabel("Ticket Reports");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(30, 40, 60));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        reportsPanel.add(titleLabel);
        reportsPanel.add(Box.createVerticalStrut(20));

        // ─── ÖZET BÖLÜMÜ ─────────────────────────────────────────────────────
        reportsPanel.add(makeSectionLabel("Summary"));
        reportsPanel.add(makeStatRow("Total Tickets", String.valueOf(total)));
        reportsPanel.add(Box.createVerticalStrut(16));

        // ─── DURUMA GÖRE ─────────────────────────────────────────────────────
        reportsPanel.add(makeSectionLabel("By Status"));
        for (Map.Entry<String, Long> e : byStatus.entrySet()) {
            reportsPanel.add(makeStatRow(e.getKey(), String.valueOf(e.getValue())));
        }
        reportsPanel.add(Box.createVerticalStrut(16));

        // ─── ÖNCELİĞE GÖRE ───────────────────────────────────────────────────
        reportsPanel.add(makeSectionLabel("By Priority"));
        for (Map.Entry<String, Long> e : byPriority.entrySet()) {
            reportsPanel.add(makeStatRow(e.getKey(), String.valueOf(e.getValue())));
        }

        reportsPanel.revalidate();
        reportsPanel.repaint();
    }

    /** Rapor bölüm başlığı — kalın, mavi-gri renk */
    private JLabel makeSectionLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(new Color(60, 80, 120));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    /** Tek bir istatistik satırı — sol=etiket, sağ=mavi değer */
    private JPanel makeStatRow(String label, String value) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 4));
        row.setBackground(new Color(245, 247, 250));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(400, 30));

        JLabel lbl = new JLabel(label + ":  ");
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lbl.setForeground(new Color(80, 90, 110));
        lbl.setPreferredSize(new Dimension(180, 24)); // Hizalı görünüm için sabit genişlik

        JLabel val = new JLabel(value);
        val.setFont(new Font("Segoe UI", Font.BOLD, 13));
        val.setForeground(new Color(41, 98, 255)); // Mavi rakam

        row.add(lbl);
        row.add(val);
        return row;
    }

    /**
     * Seçili ticket'ın durumunu değiştirmek için JOptionPane açar.
     * Supervisor tüm durumları seçebilir (OPEN dahil).
     */
    private void openChangeStatusDialog() {
        int row = ticketTable.getSelectedRow();
        if (row == -1) return;
        Long ticketId = (Long) tableModel.getValueAt(row, 0); // Gizli ID kolonu

        TicketStatus[] options = {
            TicketStatus.OPEN, TicketStatus.IN_PROGRESS, TicketStatus.PENDING,
            TicketStatus.RESOLVED, TicketStatus.CLOSED
        };
        TicketStatus selected = (TicketStatus) JOptionPane.showInputDialog(
                this, "Select new status:", "Change Status",
                JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

        if (selected != null) {
            try {
                ticketController.updateStatus(ticketId, selected);
                loadTickets();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Seçili ticket'ı bir agent'a atamak için dialog açar.
     * Agent listesi TicketController.getAgents() üzerinden çekilir.
     * Seçilen agent TicketController.assignTicket() ile kaydedilir.
     */
    private void openAssignDialog() {
        int row = ticketTable.getSelectedRow();
        if (row == -1) return;
        Long ticketId = (Long) tableModel.getValueAt(row, 0); // Gizli ID kolonu

        // AGENT rolündeki aktif kullanıcılar dropdown'a yüklenir
        List<com.helpdesk.application.dto.UserDTO> agents = ticketController.getAgents();
        if (agents.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No active agents found in the system.",
                    "No Agents", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Agent seçim dropdown'u — UserDTO.getFullName() gösterilir
        com.helpdesk.application.dto.UserDTO selected =
                (com.helpdesk.application.dto.UserDTO) JOptionPane.showInputDialog(
                        this,
                        "Select agent to assign:",
                        "Assign Ticket",
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        agents.toArray(),
                        agents.get(0));

        if (selected != null) {
            try {
                ticketController.assignTicket(ticketId, selected.getId());
                loadTickets();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void loadTickets() {
        // Sistemdeki tüm ticket'lar çekilir (supervisor tümünü görebilir)
        currentTickets = ticketController.getAllTickets();
        tableModel.setRowCount(0);
        for (TicketDTO t : currentTickets) {
            tableModel.addRow(new Object[]{
                t.getId(),            // Gizli ID kolonu — Change Status ve Assign Ticket için
                t.getTicketNumber(), t.getTitle(), t.getStatus(), t.getPriority(),
                t.getRequesterName(), // Kimin talep açtığını gösterir
                t.getCategoryName(),
                t.getCreatedAt() != null ? t.getCreatedAt().toLocalDate().toString() : ""
            });
        }
    }
}
