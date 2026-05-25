package com.helpdesk.desktop.view;

import com.helpdesk.application.dto.TicketDTO;
import com.helpdesk.application.dto.UserDTO;
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
 * ADMIN rolune ozel ana dashboard ekrani.
 * Sistemin tam yetkili panelidir; ticket'lar ve kullanicilar uzerinde
 * tam CRUD yetkisi bulunur.
 *
 * Sekmeler:
 * - Tickets : Tum ticket'lari listeler. Durum degistirme, atama, silme ve
 *             goruntuleme butonlari bulunur. Cift tiklama ile detay acilir.
 * - Reports : Duruma, onceliğe, kategoriye ve atanan agente gore istatistikler.
 *
 * Toolbar butonlari:
 * - "+ New Ticket"   → CreateTicketDialog'u acar.
 * - "Change Status"  → Secili ticket'in durumunu gunceller (pasif basta).
 * - "Assign Ticket"  → Secili ticket'i bir agente atar (pasif basta).
 * - "Delete Ticket"  → Onay sonrasi secili ticket'i siler (pasif basta).
 * - "Refresh"        → Tabloyu veritabanindan yeniden yukler.
 * - "Users"          → UserManagementFrame'i acar.
 */
public class DashboardFrame extends JFrame {

    private final AuthController authController;
    private final TicketController ticketController;
    private final UserController userController;
    private DefaultTableModel tableModel;
    private JTable ticketTable;
    // Reports sekmesi icerigi dinamik olarak buraya eklenir
    private JPanel reportsPanel;
    // ViewTicketDialog ve islem butonlari icin DTO listesi hafizada tutulur
    private List<TicketDTO> currentTickets = new java.util.ArrayList<>();

    public DashboardFrame(AuthController authController, TicketController ticketController,
                          UserController userController) {
        this.authController = authController;
        this.ticketController = ticketController;
        this.userController = userController;
        initUI();
        loadTickets();
    }

    private void initUI() {
        setTitle("IT Helpdesk — Admin");
        setSize(1100, 680);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // ─── ANA PANEL ───────────────────────────────────────────────────────
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(new Color(245, 247, 250));
        setContentPane(root);

        // ─── ÜST BAR ─────────────────────────────────────────────────────────
        // Koyu lacivert bar; sol=uygulama adı + rol, sağ=kullanıcı adı + çıkış
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(30, 40, 60));
        topBar.setBorder(new EmptyBorder(12, 20, 12, 20));

        JLabel appTitle = new JLabel("IT Helpdesk — Admin Panel");
        appTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        appTitle.setForeground(Color.WHITE);

        JPanel rightTop = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        rightTop.setOpaque(false);

        String name = SessionManager.getCurrentUser() != null
                ? SessionManager.getCurrentUser().getFullName() : "Admin";
        JLabel welcomeLabel = new JLabel("Welcome, " + name);
        welcomeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        welcomeLabel.setForeground(new Color(180, 190, 210));

        // Çıkış butonu — oturumu kapatır ve LoginFrame'e döner
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
        // Tickets: tablo yönetimi | Reports: istatistik özeti
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabs.addTab("Tickets", buildTicketsPanel());
        tabs.addTab("Reports", buildReportsPanel());

        // Reports sekmesine geçildiğinde istatistikler anında yenilenir
        tabs.addChangeListener(e -> {
            if (tabs.getSelectedIndex() == 1) refreshReports();
        });

        root.add(tabs, BorderLayout.CENTER);
    }

    /**
     * "Tickets" sekmesinin icerigini olusturur.
     * Toolbar butonlari + ticket tablosunu icerir.
     * Durum degistirme, atama ve silme butonlari satir secilince etkinlesir.
     */
    private JPanel buildTicketsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 247, 250));

        // ─── ARAÇ ÇUBUĞU ─────────────────────────────────────────────────────
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        toolbar.setBackground(new Color(245, 247, 250));
        toolbar.setBorder(new EmptyBorder(4, 12, 0, 12));

        // Yeni ticket butonu — her zaman aktif
        JButton newTicketButton = new JButton("+ New Ticket");
        newTicketButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        newTicketButton.setBackground(new Color(41, 98, 255));
        newTicketButton.setForeground(Color.WHITE);
        newTicketButton.setFocusPainted(false);
        newTicketButton.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        newTicketButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        newTicketButton.addActionListener(e -> openCreateTicketDialog());

        // Durum değiştir — satır seçilmeden pasif kalır
        JButton changeStatusButton = new JButton("Change Status");
        changeStatusButton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        changeStatusButton.setFocusPainted(false);
        changeStatusButton.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        changeStatusButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        changeStatusButton.setEnabled(false);
        changeStatusButton.addActionListener(e -> openChangeStatusDialog());

        // Ticket atama — seçili ticket'ı bir agente atar; satır seçilmeden pasif
        JButton assignButton = new JButton("Assign Ticket");
        assignButton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        assignButton.setFocusPainted(false);
        assignButton.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        assignButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        assignButton.setEnabled(false);
        assignButton.addActionListener(e -> openAssignDialog());

        // Ticket silme — kırmızı, onay gerektirir; satır seçilmeden pasif
        JButton deleteButton = new JButton("Delete Ticket");
        deleteButton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        deleteButton.setForeground(new Color(200, 50, 50));
        deleteButton.setFocusPainted(false);
        deleteButton.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        deleteButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        deleteButton.setEnabled(false);
        deleteButton.addActionListener(e -> deleteSelectedTicket());

        // Yenile — tabloyu veritabanından tekrar çeker
        JButton refreshButton = new JButton("Refresh");
        refreshButton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        refreshButton.setFocusPainted(false);
        refreshButton.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        refreshButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        refreshButton.addActionListener(e -> loadTickets());

        // Kullanıcı yönetimi — UserManagementFrame'i yeni pencerede açar
        JButton usersButton = new JButton("Users");
        usersButton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        usersButton.setFocusPainted(false);
        usersButton.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        usersButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        usersButton.addActionListener(e -> new UserManagementFrame(userController).setVisible(true));

        toolbar.add(newTicketButton);
        toolbar.add(changeStatusButton);
        toolbar.add(assignButton);
        toolbar.add(deleteButton);
        toolbar.add(refreshButton);
        toolbar.add(usersButton);
        panel.add(toolbar, BorderLayout.NORTH);

        // ─── TİCKET TABLOSU ──────────────────────────────────────────────────
        // ID kolonu gizli (index=0); Requester kolonu talep sahibini gösterir
        String[] columns = {"ID", "Ticket No", "Title", "Status", "Priority", "Requester", "Category", "Date"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
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

        // ID kolonu (index 0) gizlenir — DB id'si butonlar için saklanır
        ticketTable.getColumnModel().getColumn(0).setMinWidth(0);
        ticketTable.getColumnModel().getColumn(0).setMaxWidth(0);
        ticketTable.getColumnModel().getColumn(0).setWidth(0);

        // Sütun genişlikleri: Ticket No, Title, Status, Priority, Requester, Category, Date
        int[] widths = {0, 110, 220, 100, 80, 140, 120, 100};
        for (int i = 1; i < widths.length; i++) {
            ticketTable.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        // Satır seçilince ilgili butonlar etkinleşir
        ticketTable.getSelectionModel().addListSelectionListener(e -> {
            boolean selected = ticketTable.getSelectedRow() != -1;
            changeStatusButton.setEnabled(selected);
            assignButton.setEnabled(selected);
            deleteButton.setEnabled(selected);
        });

        // Çift tıklamada ViewTicketDialog açılır — admin dahili yorumları görebilir
        ticketTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = ticketTable.getSelectedRow();
                    if (row >= 0 && row < currentTickets.size()) {
                        new ViewTicketDialog(
                                DashboardFrame.this,
                                currentTickets.get(row),
                                ticketController,
                                true  // isAgent=true: admin dahili yorumları görebilir
                        ).setVisible(true);
                        loadTickets(); // Dialog kapandıktan sonra tabloyu yenile
                    }
                }
            }
        });

        // Zebra satır renklendirmesi + sol padding
        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int col) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
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
        panel.add(tableCard, BorderLayout.CENTER);

        return panel;
    }

    /**
     * "Reports" sekmesinin bos container'ini olusturur.
     * Icerik, sekme acildiginda refreshReports() ile dinamik doldurulur.
     */
    private JPanel buildReportsPanel() {
        reportsPanel = new JPanel();
        reportsPanel.setLayout(new BoxLayout(reportsPanel, BoxLayout.Y_AXIS));
        reportsPanel.setBackground(new Color(245, 247, 250));
        reportsPanel.setBorder(new EmptyBorder(24, 32, 24, 32));

        JScrollPane scroll = new JScrollPane(reportsPanel);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(new Color(245, 247, 250));

        // JTabbedPane addTab() bileşen beklediği için bir wrapper kullanıyoruz
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(scroll, BorderLayout.CENTER);
        return wrapper;
    }

    /**
     * Reports sekmesini guncel verilerle yeniden doldurur.
     * Duruma, onceliğe, kategoriye ve atanan agente gore gruplandırma yapar.
     */
    private void refreshReports() {
        reportsPanel.removeAll();

        List<TicketDTO> all = ticketController.getAllTickets();
        int total = all.size();

        // Gruplamalar — client-side hesaplama, ek backend metodu gerekmez
        Map<String, Long> byStatus = all.stream()
                .collect(Collectors.groupingBy(TicketDTO::getStatus, Collectors.counting()));

        Map<String, Long> byPriority = all.stream()
                .collect(Collectors.groupingBy(TicketDTO::getPriority, Collectors.counting()));

        Map<String, Long> byCategory = all.stream()
                .filter(t -> t.getCategoryName() != null)
                .collect(Collectors.groupingBy(TicketDTO::getCategoryName, Collectors.counting()));

        Map<String, Long> byAssignee = all.stream()
                .filter(t -> t.getAssigneeName() != null)
                .collect(Collectors.groupingBy(TicketDTO::getAssigneeName, Collectors.counting()));

        // ─── RAPOR BAŞLIĞI ────────────────────────────────────────────────────
        JLabel titleLabel = new JLabel("Ticket Reports");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(30, 40, 60));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        reportsPanel.add(titleLabel);
        reportsPanel.add(Box.createVerticalStrut(20));

        // ─── ÖZET ─────────────────────────────────────────────────────────────
        reportsPanel.add(makeSectionLabel("Summary"));
        reportsPanel.add(makeStatRow("Total Tickets", String.valueOf(total)));
        reportsPanel.add(makeStatRow("Unassigned",
                String.valueOf(all.stream().filter(t -> t.getAssigneeName() == null).count())));
        reportsPanel.add(Box.createVerticalStrut(20));

        // ─── DURUMA GÖRE ─────────────────────────────────────────────────────
        reportsPanel.add(makeSectionLabel("By Status"));
        for (Map.Entry<String, Long> e : byStatus.entrySet()) {
            reportsPanel.add(makeStatRow(e.getKey(), String.valueOf(e.getValue())));
        }
        reportsPanel.add(Box.createVerticalStrut(20));

        // ─── ÖNCELİĞE GÖRE ───────────────────────────────────────────────────
        reportsPanel.add(makeSectionLabel("By Priority"));
        for (Map.Entry<String, Long> e : byPriority.entrySet()) {
            reportsPanel.add(makeStatRow(e.getKey(), String.valueOf(e.getValue())));
        }
        reportsPanel.add(Box.createVerticalStrut(20));

        // ─── KATEGORİYE GÖRE ─────────────────────────────────────────────────
        reportsPanel.add(makeSectionLabel("By Category"));
        if (byCategory.isEmpty()) {
            reportsPanel.add(makeStatRow("No category data", "-"));
        } else {
            for (Map.Entry<String, Long> e : byCategory.entrySet()) {
                reportsPanel.add(makeStatRow(e.getKey(), String.valueOf(e.getValue())));
            }
        }
        reportsPanel.add(Box.createVerticalStrut(20));

        // ─── ATANAN AGENTE GÖRE ───────────────────────────────────────────────
        reportsPanel.add(makeSectionLabel("By Assignee"));
        if (byAssignee.isEmpty()) {
            reportsPanel.add(makeStatRow("No assignments yet", "-"));
        } else {
            for (Map.Entry<String, Long> e : byAssignee.entrySet()) {
                reportsPanel.add(makeStatRow(e.getKey(), String.valueOf(e.getValue())));
            }
        }

        reportsPanel.revalidate();
        reportsPanel.repaint();
    }

    /** Rapor bölüm başlığı — kalın, mavi-gri */
    private JLabel makeSectionLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(new Color(60, 80, 120));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    /** Tek istatistik satırı — sol=etiket (sabit genişlik), sağ=mavi değer */
    private JPanel makeStatRow(String label, String value) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 4));
        row.setBackground(new Color(245, 247, 250));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(500, 30));

        JLabel lbl = new JLabel(label + ":  ");
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lbl.setForeground(new Color(80, 90, 110));
        lbl.setPreferredSize(new Dimension(200, 24));

        JLabel val = new JLabel(value);
        val.setFont(new Font("Segoe UI", Font.BOLD, 13));
        val.setForeground(new Color(41, 98, 255));

        row.add(lbl);
        row.add(val);
        return row;
    }

    private void openCreateTicketDialog() {
        CreateTicketDialog dialog = new CreateTicketDialog(this, ticketController);
        dialog.setVisible(true);
        if (dialog.isSubmitted()) {
            loadTickets();
        }
    }

    /**
     * Secili ticket'in durumunu degistirmek icin JOptionPane dialog'u acar.
     * Tum TicketStatus degerlerinden secim yapilabilir.
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
     * Secili ticket'i bir agente atamak icin dialog acar.
     * Agent listesi TicketController.getAgents() ile cekilir.
     */
    private void openAssignDialog() {
        int row = ticketTable.getSelectedRow();
        if (row == -1) return;
        Long ticketId = (Long) tableModel.getValueAt(row, 0);

        List<UserDTO> agents = ticketController.getAgents();
        if (agents.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No active agents found in the system.",
                    "No Agents", JOptionPane.WARNING_MESSAGE);
            return;
        }

        UserDTO selected = (UserDTO) JOptionPane.showInputDialog(
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

    /**
     * Secili ticket'i onay sonrasi kalici olarak siler.
     * Silme islemi geri alinamaz; onay dialogu zorunludur.
     */
    private void deleteSelectedTicket() {
        int row = ticketTable.getSelectedRow();
        if (row == -1) return;
        Long ticketId = (Long) tableModel.getValueAt(row, 0);
        String ticketNo = (String) tableModel.getValueAt(row, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Permanently delete ticket '" + ticketNo + "'?\nThis action cannot be undone.",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                ticketController.deleteTicket(ticketId);
                loadTickets();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void loadTickets() {
        currentTickets = ticketController.getAllTickets();
        tableModel.setRowCount(0);
        for (TicketDTO t : currentTickets) {
            tableModel.addRow(new Object[]{
                t.getId(),             // Gizli ID — Change Status, Assign, Delete icin
                t.getTicketNumber(),
                t.getTitle(),
                t.getStatus(),
                t.getPriority(),
                t.getRequesterName(),
                t.getCategoryName(),
                t.getCreatedAt() != null ? t.getCreatedAt().toLocalDate().toString() : ""
            });
        }
    }
}