package com.helpdesk.desktop.view;

import com.helpdesk.application.dto.CommentDTO;
import com.helpdesk.application.dto.TicketDTO;
import com.helpdesk.desktop.controller.TicketController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

/**
 * Ticket detay ve yorum ekrani — CUSTOMER ve AGENT rolleri tarafından kullanılır.
 *
 * Mod farkları (isAgent parametresiyle kontrol edilir):
 * - CUSTOMER modu (isAgent=false):
 *     is_internal=true yorumlar gizlenir; "Internal note" checkbox gösterilmez.
 * - AGENT modu (isAgent=true):
 *     Tüm yorumlar görünür; dahili yorumlar [INTERNAL] etiketi ile ayırt edilir.
 *     Yorum yazarken "Mark as internal" checkbox gösterilir.
 *
 * Gosterilen bilgiler:
 * - Ticket başlığı, durum badge, öncelik badge, kategori, açılış tarihi
 * - Ticket açıklaması (tam metin)
 * - Yorum listesi (role göre filtrelenmiş)
 * - Yorum yazma alanı ve "Add Comment" butonu
 */
public class ViewTicketDialog extends JDialog {

    // ─── BAĞIMLILIKLAR ───────────────────────────────────────────────────────
    private final TicketDTO ticket;
    private final TicketController ticketController;
    // isAgent=true: dahili yorumlar görünür + internal checkbox aktif
    private final boolean isAgent;

    // ─── UI BİLEŞENLERİ ─────────────────────────────────────────────────────
    // Yorumlar paneli — loadComments() her çağrıldığında yenilenir
    private JPanel commentsPanel;
    // Yorum yazma alanı
    private JTextArea commentInput;
    // Sadece agent modunda gösterilir — dahili not işaretlemek için
    private JCheckBox internalCheckBox;

    public ViewTicketDialog(JFrame parent, TicketDTO ticket, TicketController ticketController) {
        this(parent, ticket, ticketController, false);
    }

    public ViewTicketDialog(JFrame parent, TicketDTO ticket, TicketController ticketController, boolean isAgent) {
        super(parent, "Ticket Detail", true);
        this.ticket = ticket;
        this.ticketController = ticketController;
        this.isAgent = isAgent;
        initUI();
        loadComments();
    }

    private void initUI() {
        setSize(660, 720);
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // ─── KÖK PANEL ───────────────────────────────────────────────────────
        JPanel root = new JPanel();
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
        root.setBackground(new Color(245, 247, 250));
        root.setBorder(new EmptyBorder(20, 24, 20, 24));

        // ─── TİCKET DETAY KARTI ──────────────────────────────────────────────
        JPanel detailCard = new JPanel(new BorderLayout());
        detailCard.setBackground(Color.WHITE);
        detailCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 223, 228), 1),
                new EmptyBorder(16, 18, 16, 18)
        ));
        detailCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 210));

        // Ticket başlığı — büyük ve koyu font
        JLabel titleLabel = new JLabel(ticket.getTitle());
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(new Color(30, 40, 60));

        // Meta bilgi satırı — durum, öncelik, kategori, tarih
        JPanel metaRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        metaRow.setOpaque(false);
        metaRow.setBorder(new EmptyBorder(8, 0, 10, 0));

        metaRow.add(makeBadge(ticket.getStatus(), statusColor(ticket.getStatus())));
        metaRow.add(makeBadge(ticket.getPriority(), priorityColor(ticket.getPriority())));

        // Kategori etiketi
        JLabel categoryLabel = new JLabel("Category: " + nvl(ticket.getCategoryName(), "-"));
        categoryLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        categoryLabel.setForeground(new Color(100, 110, 130));

        // Açılış tarihi
        String dateStr = ticket.getCreatedAt() != null
                ? ticket.getCreatedAt().toLocalDate().toString() : "-";
        JLabel dateLabel = new JLabel("Opened: " + dateStr);
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        dateLabel.setForeground(new Color(100, 110, 130));

        metaRow.add(categoryLabel);
        metaRow.add(dateLabel);

        // Açıklama alanı — salt okunur, word-wrap açık
        JTextArea descArea = new JTextArea(ticket.getDescription());
        descArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        descArea.setForeground(new Color(50, 60, 80));
        descArea.setEditable(false);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setBackground(Color.WHITE);
        descArea.setBorder(BorderFactory.createEmptyBorder());

        JPanel detailContent = new JPanel(new BorderLayout());
        detailContent.setOpaque(false);
        detailContent.add(titleLabel, BorderLayout.NORTH);
        detailContent.add(metaRow, BorderLayout.CENTER);
        detailContent.add(descArea, BorderLayout.SOUTH);
        detailCard.add(detailContent, BorderLayout.CENTER);

        // ─── YORUMLAR BÖLÜM BAŞLIĞI ──────────────────────────────────────────
        JLabel commentsTitle = new JLabel("Comments");
        commentsTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        commentsTitle.setForeground(new Color(30, 40, 60));
        commentsTitle.setBorder(new EmptyBorder(18, 0, 8, 0));
        commentsTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        // ─── YORUMLAR LİSTESİ PANELİ ─────────────────────────────────────────
        // Dinamik panel — loadComments() her çağrıldığında içeriği yenilenir
        commentsPanel = new JPanel();
        commentsPanel.setLayout(new BoxLayout(commentsPanel, BoxLayout.Y_AXIS));
        commentsPanel.setBackground(new Color(245, 247, 250));

        JScrollPane commentsScroll = new JScrollPane(commentsPanel);
        commentsScroll.setBorder(BorderFactory.createLineBorder(new Color(220, 223, 228), 1));
        commentsScroll.getViewport().setBackground(new Color(245, 247, 250));
        commentsScroll.setPreferredSize(new Dimension(600, 200));
        commentsScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));

        // ─── YORUM YAZMA ALANI ───────────────────────────────────────────────
        JLabel inputLabel = new JLabel("Add a comment");
        inputLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        inputLabel.setForeground(new Color(30, 40, 60));
        inputLabel.setBorder(new EmptyBorder(16, 0, 6, 0));
        inputLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Yorum metin alanı — çok satırlı, word-wrap açık
        commentInput = new JTextArea(4, 40);
        commentInput.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        commentInput.setLineWrap(true);
        commentInput.setWrapStyleWord(true);
        commentInput.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 205, 215), 1),
                new EmptyBorder(8, 10, 8, 10)
        ));

        JScrollPane inputScroll = new JScrollPane(commentInput);
        inputScroll.setBorder(BorderFactory.createEmptyBorder());
        inputScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        // ─── ALT BUTON SATIRI ────────────────────────────────────────────────
        // "Add Comment" butonu + agent modunda "Internal note" checkbox yan yana
        JPanel bottomRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        bottomRow.setOpaque(false);
        bottomRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        // "Add Comment" butonu — boş girişte uyarı verir
        JButton addCommentBtn = new JButton("Add Comment");
        addCommentBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        addCommentBtn.setBackground(new Color(41, 98, 255));
        addCommentBtn.setForeground(Color.WHITE);
        addCommentBtn.setFocusPainted(false);
        addCommentBtn.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        addCommentBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addCommentBtn.addActionListener(e -> submitComment());
        bottomRow.add(addCommentBtn);

        // "Internal note" checkbox — sadece agent modunda gösterilir
        // İşaretlenirse is_internal=true kaydedilir, müşteri göremez
        if (isAgent) {
            internalCheckBox = new JCheckBox("Internal note");
            internalCheckBox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            internalCheckBox.setForeground(new Color(150, 60, 60));
            internalCheckBox.setOpaque(false);
            internalCheckBox.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            bottomRow.add(internalCheckBox);
        }

        // ─── PANEL BİRLEŞTİRME ───────────────────────────────────────────────
        root.add(detailCard);
        root.add(commentsTitle);
        root.add(commentsScroll);
        root.add(inputLabel);
        root.add(inputScroll);
        root.add(Box.createRigidArea(new Dimension(0, 10)));
        root.add(bottomRow);

        JScrollPane rootScroll = new JScrollPane(root);
        rootScroll.setBorder(BorderFactory.createEmptyBorder());
        setContentPane(rootScroll);
    }

    /**
     * Yorumları veritabanından çeker ve commentsPanel'i yeniden oluşturur.
     * - CUSTOMER modu: is_internal=true yorumlar gizlenir.
     * - AGENT modu: tüm yorumlar gösterilir; dahili olanlar [INTERNAL] etiketiyle ayırt edilir.
     */
    private void loadComments() {
        commentsPanel.removeAll();

        List<CommentDTO> comments = ticketController.getComments(ticket.getId());

        List<CommentDTO> visible = isAgent
                ? comments  // Agent tüm yorumları görür
                : comments.stream()
                        .filter(c -> c.getIsInternal() == null || !c.getIsInternal())
                        .toList();

        if (visible.isEmpty()) {
            JLabel empty = new JLabel("No comments yet.");
            empty.setFont(new Font("Segoe UI", Font.ITALIC, 13));
            empty.setForeground(new Color(150, 160, 175));
            empty.setBorder(new EmptyBorder(14, 14, 14, 14));
            commentsPanel.add(empty);
        } else {
            for (CommentDTO c : visible) {
                commentsPanel.add(buildCommentCard(c));
                commentsPanel.add(Box.createRigidArea(new Dimension(0, 6)));
            }
        }

        commentsPanel.revalidate();
        commentsPanel.repaint();
    }

    /**
     * Tek bir yorum için kart paneli oluşturur.
     * Dahili yorumlar (is_internal=true) sarı arka plan + [INTERNAL] etiketi alır.
     */
    private JPanel buildCommentCard(CommentDTO c) {
        boolean internal = Boolean.TRUE.equals(c.getIsInternal());

        JPanel card = new JPanel(new BorderLayout());
        // Dahili yorumlar için farklı arka plan rengi — görsel ayırt edici
        card.setBackground(internal ? new Color(255, 252, 235) : Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                        internal ? new Color(240, 200, 100) : new Color(225, 228, 235), 1),
                new EmptyBorder(10, 14, 10, 14)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

        // Yorum başlığı — yazar adı, tarih ve dahili etiket
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        // Yorum yazarının adı
        JLabel authorLabel = new JLabel(nvl(c.getAuthorName(), "Unknown"));
        authorLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        authorLabel.setForeground(internal ? new Color(160, 100, 20) : new Color(41, 98, 255));

        // Sağ üst: tarih + [INTERNAL] etiketi (varsa)
        JPanel rightHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        rightHeader.setOpaque(false);

        if (internal) {
            // Dahili yorum etiketi — müşterinin göremediğini belirtir
            JLabel internalTag = new JLabel("[INTERNAL]");
            internalTag.setFont(new Font("Segoe UI", Font.BOLD, 10));
            internalTag.setForeground(new Color(160, 100, 20));
            rightHeader.add(internalTag);
        }

        String dateStr = c.getCreatedAt() != null
                ? c.getCreatedAt().toLocalDate().toString() : "";
        JLabel dateLabel = new JLabel(dateStr);
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        dateLabel.setForeground(new Color(160, 170, 185));
        rightHeader.add(dateLabel);

        header.add(authorLabel, BorderLayout.WEST);
        header.add(rightHeader, BorderLayout.EAST);

        // Yorum metni — salt okunur, word-wrap açık
        JTextArea contentArea = new JTextArea(c.getContent());
        contentArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        contentArea.setForeground(new Color(50, 60, 80));
        contentArea.setEditable(false);
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        contentArea.setBackground(card.getBackground());
        contentArea.setBorder(new EmptyBorder(4, 0, 0, 0));

        card.add(header, BorderLayout.NORTH);
        card.add(contentArea, BorderLayout.CENTER);
        return card;
    }

    /**
     * Yorum gönderme işlemi.
     * Boş ise uyarı gösterir; agent modunda is_internal değeri checkbox'tan okunur.
     */
    private void submitComment() {
        String text = commentInput.getText().trim();
        if (text.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please write a comment before submitting.",
                    "Empty Comment", JOptionPane.WARNING_MESSAGE);
            return;
        }
        boolean internal = isAgent && internalCheckBox != null && internalCheckBox.isSelected();
        ticketController.addComment(ticket.getId(), text, internal);
        commentInput.setText("");
        if (internalCheckBox != null) internalCheckBox.setSelected(false);
        loadComments();
    }

    // ─── YARDIMCI METODLAR ───────────────────────────────────────────────────

    private String nvl(String value, String fallback) {
        return value != null ? value : fallback;
    }

    private JLabel makeBadge(String text, Color bg) {
        JLabel badge = new JLabel(nvl(text, "?"));
        badge.setFont(new Font("Segoe UI", Font.BOLD, 11));
        badge.setForeground(Color.WHITE);
        badge.setBackground(bg);
        badge.setOpaque(true);
        badge.setBorder(new EmptyBorder(3, 8, 3, 8));
        return badge;
    }

    private Color statusColor(String status) {
        if (status == null) return Color.GRAY;
        return switch (status) {
            case "OPEN"        -> new Color(41, 98, 255);
            case "IN_PROGRESS" -> new Color(230, 130, 30);
            case "PENDING"     -> new Color(130, 100, 200);
            case "RESOLVED"    -> new Color(34, 160, 90);
            default            -> new Color(120, 130, 150);
        };
    }

    private Color priorityColor(String priority) {
        if (priority == null) return Color.GRAY;
        return switch (priority) {
            case "CRITICAL" -> new Color(200, 40, 40);
            case "HIGH"     -> new Color(220, 90, 30);
            case "MEDIUM"   -> new Color(200, 160, 20);
            default         -> new Color(100, 160, 100);
        };
    }
}
