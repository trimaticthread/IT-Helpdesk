package com.helpdesk.desktop.view;

import com.helpdesk.desktop.controller.TicketController;
import com.helpdesk.domain.entity.Category;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

/**
 * Yeni destek talebi (ticket) olusturma modal dialog'u.
 * Dashboard ekranlarindan acilir; kullanici baslik, aciklama, oncelik
 * ve kategori girerek yeni bir ticket olusturabilir.
 *
 * Form alanlari: Baslik (zorunlu), Aciklama, Oncelik (LOW/MEDIUM/HIGH/CRITICAL), Kategori
 *
 * Kullanim akisi:
 * 1. Kullanici "+ Yeni Ticket" butonuna basar → bu dialog acilir.
 * 2. Kategoriler veritabanindan otomatik yuklenir (loadCategories).
 * 3. "Talebi Olustur" butonuna basilinca TicketController.createTicket() cagirilir.
 *    Requester olarak SessionManager'daki aktif kullanici kullanilir.
 * 4. isSubmitted() == true ise parent dashboard tabloyu yeniler.
 */
public class CreateTicketDialog extends JDialog {

    private final TicketController ticketController;
    private boolean submitted = false;

    private JTextField titleField;
    private JTextArea descriptionArea;
    private JComboBox<String> priorityCombo;
    private JComboBox<Category> categoryCombo;
    private JLabel errorLabel;

    public CreateTicketDialog(Frame parent, TicketController ticketController) {
        super(parent, "Yeni Destek Talebi", true);
        this.ticketController = ticketController;
        initUI();
        loadCategories();
    }

    private void initUI() {
        setSize(500, 460);
        setLocationRelativeTo(getParent()); // Ebeveyn dashboard'a göre ortala
        setResizable(false);

        // ─── ANA PANEL ───────────────────────────────────────────────────────
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.WHITE);
        root.setBorder(new EmptyBorder(28, 32, 24, 32));
        setContentPane(root);

        // ─── DİALOG BAŞLIĞI ──────────────────────────────────────────────────
        // Formun amacını belirtir
        JLabel titleLabel = new JLabel("Yeni Destek Talebi");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(new Color(30, 40, 60));
        titleLabel.setBorder(new EmptyBorder(0, 0, 20, 0));
        root.add(titleLabel, BorderLayout.NORTH);

        // ─── FORM ALANLARI ────────────────────────────────────────────────────
        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(Color.WHITE);

        // Ticket başlık alanı — zorunlu alan; boş bırakılamaz
        form.add(makeLabel("Baslik"));
        form.add(Box.createVerticalStrut(5));
        titleField = new JTextField();
        titleField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        titleField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        titleField.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(titleField);
        form.add(Box.createVerticalStrut(14));

        // Açıklama alanı — çok satırlı; sorunun detayını içerir
        // Kelime bazlı satır kaydırma (wrapStyleWord) aktif
        form.add(makeLabel("Aciklama"));
        form.add(Box.createVerticalStrut(5));
        descriptionArea = new JTextArea(4, 0);
        descriptionArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane descScroll = new JScrollPane(descriptionArea); // Scroll desteği
        descScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        descScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(descScroll);
        form.add(Box.createVerticalStrut(14));

        // Öncelik ve Kategori yan yana (GridLayout 1x2)
        JPanel row = new JPanel(new GridLayout(1, 2, 16, 0));
        row.setBackground(Color.WHITE);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Öncelik seçici — LOW / MEDIUM / HIGH / CRITICAL; varsayılan MEDIUM
        JPanel prioPanel = new JPanel();
        prioPanel.setLayout(new BoxLayout(prioPanel, BoxLayout.Y_AXIS));
        prioPanel.setBackground(Color.WHITE);
        prioPanel.add(makeLabel("Oncelik"));
        prioPanel.add(Box.createVerticalStrut(5));
        priorityCombo = new JComboBox<>(new String[]{"LOW", "MEDIUM", "HIGH", "CRITICAL"});
        priorityCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        priorityCombo.setSelectedItem("MEDIUM"); // Varsayılan öncelik
        prioPanel.add(priorityCombo);

        // Kategori seçici — veritabanından aktif kategoriler yüklenir (loadCategories)
        // Category.toString() override sayesinde isim görüntülenir
        JPanel catPanel = new JPanel();
        catPanel.setLayout(new BoxLayout(catPanel, BoxLayout.Y_AXIS));
        catPanel.setBackground(Color.WHITE);
        catPanel.add(makeLabel("Kategori"));
        catPanel.add(Box.createVerticalStrut(5));
        categoryCombo = new JComboBox<>(); // Öğeler loadCategories() ile doldurulur
        categoryCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        catPanel.add(categoryCombo);

        row.add(prioPanel);
        row.add(catPanel);
        form.add(row);
        form.add(Box.createVerticalStrut(6));

        // Hata mesajı — başta boş; doğrulama hatası varsa kırmızı metin gösterir
        errorLabel = new JLabel(" ");
        errorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        errorLabel.setForeground(new Color(220, 50, 50));
        errorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(errorLabel);

        root.add(form, BorderLayout.CENTER);

        // ─── BUTONLAR ────────────────────────────────────────────────────────
        // Iptal: dialog kapanır | Talebi Olustur: handleSubmit() çalışır
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(new EmptyBorder(16, 0, 0, 0));

        // İptal butonu — formu temizlemeden kapatır, ticket oluşmaz
        JButton cancelButton = new JButton("Iptal");
        cancelButton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cancelButton.setFocusPainted(false);
        cancelButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        cancelButton.addActionListener(e -> dispose());

        // Gönder butonu — başlık ve kategori kontrolü sonrası ticket oluşturur
        JButton submitButton = new JButton("Talebi Olustur");
        submitButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        submitButton.setBackground(new Color(41, 98, 255));
        submitButton.setForeground(Color.WHITE);
        submitButton.setFocusPainted(false);
        submitButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        submitButton.addActionListener(e -> handleSubmit());

        buttonPanel.add(cancelButton);
        buttonPanel.add(submitButton);
        root.add(buttonPanel, BorderLayout.SOUTH);
    }

    private JLabel makeLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        label.setForeground(new Color(60, 70, 90));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private void loadCategories() {
        List<Category> categories = ticketController.getCategories();
        categoryCombo.removeAllItems();
        for (Category c : categories) {
            categoryCombo.addItem(c);
        }
    }

    private void handleSubmit() {
        String title = titleField.getText().trim();
        String description = descriptionArea.getText().trim();
        String priority = (String) priorityCombo.getSelectedItem();
        Category category = (Category) categoryCombo.getSelectedItem();

        if (title.isEmpty()) {
            errorLabel.setText("Baslik bos birakilamaz.");
            return;
        }
        if (category == null) {
            errorLabel.setText("Lutfen bir kategori secin.");
            return;
        }

        try {
            ticketController.createTicket(title, description, category.getId(), priority);
            submitted = true;
            dispose();
        } catch (Exception ex) {
            errorLabel.setText("Hata: " + ex.getMessage());
        }
    }

    public boolean isSubmitted() {
        return submitted;
    }
}
