package com.helpdesk.desktop.view;

import com.helpdesk.application.dto.UserDTO;
import com.helpdesk.desktop.controller.UserController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Admin paneli — kullanici yonetimi ekrani.
 * Sistemdeki tum kullanicilari listeler; yeni kullanici ekleme,
 * mevcut kullanici duzenleme ve silme islemlerine olanak tanir.
 *
 * Ozellikler:
 * - "+ New User"  → CreateUserDialog'u acar (ad, email, sifre, rol secimi).
 * - "Edit"        → EditUserDialog'u acar; sadece secili satirda etkin.
 * - "Delete"      → Onay sonrasi kullaniciyi siler; sadece secili satirda etkin.
 * - "Refresh"     → Tabloyu veritabanindan yeniden yukler.
 *
 * Not: Edit ve Delete butonlari tablo secimi olmadan devre disi kalir.
 *      Bu sayede bos satirda islem yapilmasi engellenir.
 */
public class UserManagementFrame extends JFrame {

    private final UserController userController;
    private DefaultTableModel tableModel;
    private JTable userTable;

    public UserManagementFrame(UserController userController) {
        this.userController = userController;
        initUI();
        loadUsers();
    }

    private void initUI() {
        setTitle("IT Helpdesk - User Management");
        setSize(800, 520);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE); // Ana pencereyi kapatmaz, sadece bu pencereyi kapatır

        // ─── ANA PANEL ───────────────────────────────────────────────────────
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(new Color(245, 247, 250));
        setContentPane(root);

        // ─── ÜST BAR ─────────────────────────────────────────────────────────
        // "User Management" başlığını taşıyan koyu bar
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(30, 40, 60));
        topBar.setBorder(new EmptyBorder(12, 20, 12, 20));

        // Sayfa başlığı — üst barın sol tarafında
        JLabel titleLabel = new JLabel("User Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(Color.WHITE);
        topBar.add(titleLabel, BorderLayout.WEST);

        // ─── ARAÇ ÇUBUĞU ─────────────────────────────────────────────────────
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        toolbar.setBackground(new Color(245, 247, 250));
        toolbar.setBorder(new EmptyBorder(4, 12, 0, 12));

        // Yeni kullanıcı butonu — CreateUserDialog modal penceresini açar
        JButton newUserButton = new JButton("+ New User");
        newUserButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        newUserButton.setBackground(new Color(41, 98, 255));
        newUserButton.setForeground(Color.WHITE);
        newUserButton.setFocusPainted(false);
        newUserButton.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        newUserButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        newUserButton.addActionListener(e -> openCreateUserDialog());

        // Yenile butonu — kullanıcı tablosunu veritabanından tekrar çeker
        JButton refreshButton = new JButton("Refresh");
        refreshButton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        refreshButton.setFocusPainted(false);
        refreshButton.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        refreshButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        refreshButton.addActionListener(e -> loadUsers());

        toolbar.add(newUserButton);
        toolbar.add(refreshButton);

        // Düzenle butonu — tabloda satır seçilmeden devre dışıdır
        // Tıklanınca EditUserDialog'u açar
        JButton editButton = new JButton("Edit");
        editButton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        editButton.setFocusPainted(false);
        editButton.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        editButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        editButton.setEnabled(false); // Başlangıçta pasif
        editButton.addActionListener(e -> openEditUserDialog());

        // Sil butonu — kırmızı metin; tabloda satır seçilmeden devre dışıdır
        // Tıklanınca onay sorar, onaylanırsa kullanıcıyı siler
        JButton deleteButton = new JButton("Delete");
        deleteButton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        deleteButton.setForeground(new Color(200, 50, 50));
        deleteButton.setFocusPainted(false);
        deleteButton.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        deleteButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        deleteButton.setEnabled(false); // Başlangıçta pasif
        deleteButton.addActionListener(e -> deleteSelectedUser());

        toolbar.add(editButton);
        toolbar.add(deleteButton);

        // ─── KULLANICI TABLOSU ────────────────────────────────────────────────
        // ID, Username, Full Name, Email, Department, Active kolonları
        String[] columns = {"ID", "Username", "Full Name", "Email", "Department", "Active"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        // userTable field olarak tutulur — openEditUserDialog/deleteSelectedUser içinde okunur
        userTable = new JTable(tableModel);

        // Seçim dinleyicisi: satır seçilince Edit ve Delete aktif olur
        userTable.getSelectionModel().addListSelectionListener(e -> {
            boolean selected = userTable.getSelectedRow() != -1;
            editButton.setEnabled(selected);
            deleteButton.setEnabled(selected);
        });
        userTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        userTable.setRowHeight(32);
        userTable.setShowGrid(false);
        userTable.setIntercellSpacing(new Dimension(0, 0));
        userTable.setSelectionBackground(new Color(220, 230, 255));
        userTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        userTable.getTableHeader().setBackground(new Color(235, 238, 245));
        userTable.getTableHeader().setForeground(new Color(80, 90, 110));

        // Sütun genişlikleri: ID, Username, Full Name, Email, Department, Active
        int[] widths = {50, 130, 160, 200, 130, 60};
        for (int i = 0; i < widths.length; i++) {
            userTable.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        // Zebra satır renklendirmesi + sol padding
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
            userTable.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
        }

        JScrollPane scrollPane = new JScrollPane(userTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        // Tablo kartı — kenarlık + boşlukla çevrelenmiş
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

    private void openCreateUserDialog() {
        CreateUserDialog dialog = new CreateUserDialog(this, userController);
        dialog.setVisible(true);
        if (dialog.isCreated()) {
            loadUsers();
        }
    }

    private void loadUsers() {
        List<UserDTO> users = userController.getAllUsers();
        tableModel.setRowCount(0);
        for (UserDTO u : users) {
            tableModel.addRow(new Object[]{
                u.getId(),
                u.getUsername(),
                u.getFullName(),
                u.getEmail(),
                u.getDepartment(),
                u.getIsActive() ? "Yes" : "No"
            });
        }
    }

    private void openEditUserDialog() {
        int row = userTable.getSelectedRow();
        if (row == -1) return;
        Long id = (Long) tableModel.getValueAt(row, 0);
        userController.getAllUsers().stream()
                .filter(u -> u.getId().equals(id))
                .findFirst()
                .ifPresent(u -> {
                    EditUserDialog dialog = new EditUserDialog(this, userController, u);
                    dialog.setVisible(true);
                    if (dialog.isUpdated()) loadUsers();
                });
    }

    private void deleteSelectedUser() {
        int row = userTable.getSelectedRow();
        if (row == -1) return;
        Long id = (Long) tableModel.getValueAt(row, 0);
        String username = (String) tableModel.getValueAt(row, 1);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete user '" + username + "'?", "Confirm Delete",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            userController.deleteUser(id);
            loadUsers();
        }
    }

}
