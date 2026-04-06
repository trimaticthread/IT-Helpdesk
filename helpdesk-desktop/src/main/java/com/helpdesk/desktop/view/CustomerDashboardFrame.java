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

/**
 * CUSTOMER rolune ozel dashboard ekrani.
 * Musteri sadece kendi olusturdugu ticket'lari gorebilir;
 * baskasinin ticket'larina, kullanici yonetimine veya raporlara erisemez.
 *
 * Ozellikler:
 * - Yalnizca aktif kullanicinin ticket'larini listeler (findByRequesterId).
 * - "+ New Ticket" butonu ile yeni talep olusturulabilir.
 * - Ust barda hosgeldin mesaji ve cikis butonu bulunur.
 */
public class CustomerDashboardFrame extends JFrame {

    private final AuthController authController;
    private final TicketController ticketController;
    private DefaultTableModel tableModel;

    public CustomerDashboardFrame(AuthController authController, TicketController ticketController) {
        this.authController = authController;
        this.ticketController = ticketController;
        initUI();
        loadTickets();
    }

    private void initUI() {
        setTitle("IT Helpdesk");
        setSize(900, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Ekran ortasında aç

        // ─── ANA PANEL ───────────────────────────────────────────────────────
        // Tüm ekranı dolduran kök panel; kuzey=üst bar+toolbar, merkez=tablo
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(new Color(245, 247, 250));
        setContentPane(root);

        // ─── ÜST BAR ─────────────────────────────────────────────────────────
        // Koyu lacivert bar; sol=uygulama adı, sağ=kullanıcı adı + çıkış butonu
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(30, 40, 60));
        topBar.setBorder(new EmptyBorder(12, 20, 12, 20));

        // Uygulama adı — üst barın sol köşesinde sabit
        JLabel appTitle = new JLabel("IT Helpdesk");
        appTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        appTitle.setForeground(Color.WHITE);

        // Sağ üst köşe paneli — kullanıcı adı ve çıkış butonu yan yana
        JPanel rightTop = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        rightTop.setOpaque(false);

        // Oturumu açık olan kullanıcının tam adı (SessionManager'dan okunur)
        String name = SessionManager.getCurrentUser() != null
                ? SessionManager.getCurrentUser().getFullName() : "User";
        JLabel welcomeLabel = new JLabel("Welcome, " + name);
        welcomeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        welcomeLabel.setForeground(new Color(180, 190, 210));

        // Çıkış butonu — SessionManager temizlenir, LoginFrame açılır
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
            new LoginFrame(authController, ticketController, null).setVisible(true);
        });

        rightTop.add(welcomeLabel);
        rightTop.add(logoutButton);
        topBar.add(appTitle, BorderLayout.WEST);
        topBar.add(rightTop, BorderLayout.EAST);

        // ─── ARAÇ ÇUBUĞU ─────────────────────────────────────────────────────
        // Üst barın hemen altı; yeni ticket ve yenile butonları soldan sıralanır
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        toolbar.setBackground(new Color(245, 247, 250));
        toolbar.setBorder(new EmptyBorder(4, 12, 0, 12));

        // Yeni ticket butonu — CreateTicketDialog modal penceresini açar
        JButton newTicketButton = new JButton("+ New Ticket");
        newTicketButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        newTicketButton.setBackground(new Color(41, 98, 255));
        newTicketButton.setForeground(Color.WHITE);
        newTicketButton.setFocusPainted(false);
        newTicketButton.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        newTicketButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        newTicketButton.addActionListener(e -> openCreateTicketDialog());

        // Yenile butonu — tabloyu veritabanından tekrar çeker
        JButton refreshButton = new JButton("Refresh");
        refreshButton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        refreshButton.setFocusPainted(false);
        refreshButton.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        refreshButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        refreshButton.addActionListener(e -> loadTickets());

        toolbar.add(newTicketButton);
        toolbar.add(refreshButton);

        // ─── TİCKET TABLOSU ──────────────────────────────────────────────────
        // Sadece bu kullanıcının ticket'larını gösterir (loadTickets → getMyTickets)
        String[] columns = {"Ticket No", "Title", "Status", "Priority", "Category", "Date"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };

        JTable ticketTable = new JTable(tableModel);
        ticketTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        ticketTable.setRowHeight(32);
        ticketTable.setShowGrid(false);
        ticketTable.setIntercellSpacing(new Dimension(0, 0));
        ticketTable.setSelectionBackground(new Color(220, 230, 255));
        ticketTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        ticketTable.getTableHeader().setBackground(new Color(235, 238, 245));
        ticketTable.getTableHeader().setForeground(new Color(80, 90, 110));

        // Sütun genişlikleri: Ticket No, Başlık, Durum, Öncelik, Kategori, Tarih
        int[] widths = {120, 260, 90, 80, 130, 100};
        for (int i = 0; i < widths.length; i++) {
            ticketTable.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        // Zebra satır renklendirmesi + sol padding
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

        // Scroll desteği — liste büyüdükçe kaydırılabilir
        JScrollPane scrollPane = new JScrollPane(ticketTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);

        // Tablo kartı — kenarlık + boşlukla çevrelenmiş beyaz panel
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

    private void openCreateTicketDialog() {
        CreateTicketDialog dialog = new CreateTicketDialog(this, ticketController);
        dialog.setVisible(true);
        if (dialog.isSubmitted()) loadTickets(); // Dialog kapandıktan sonra tabloyu yenile
    }

    private void loadTickets() {
        // Sadece bu müşteriye ait ticket'lar çekilir (requesterId = mevcut kullanıcı)
        List<TicketDTO> tickets = ticketController.getMyTickets();
        tableModel.setRowCount(0); // Tabloyu sıfırla
        for (TicketDTO t : tickets) {
            tableModel.addRow(new Object[]{
                t.getTicketNumber(), t.getTitle(), t.getStatus(), t.getPriority(),
                t.getCategoryName(),
                t.getCreatedAt() != null ? t.getCreatedAt().toLocalDate().toString() : ""
            });
        }
    }
}
