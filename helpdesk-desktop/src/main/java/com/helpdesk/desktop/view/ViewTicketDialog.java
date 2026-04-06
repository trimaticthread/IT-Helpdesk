package com.helpdesk.desktop.view;

import com.helpdesk.application.dto.CommentDTO;
import com.helpdesk.application.dto.TicketDTO;
import com.helpdesk.desktop.controller.TicketController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

/**
 * Musteri rolune ozel ticket detay ve yorum ekrani.
 *
 * Gosterdiği bilgiler:
 * - Ticket basligi, durumu, onceligi, kategorisi, acilis tarihi
 * - Ticket aciklamasi (tam metin)
 * - Bu ticketa ait herkese acik yorumlar (is_internal = false)
 * - Yorum yazma alani ve "Add Comment" butonu
 *
 * Onemli kisitlamalar:
 * - is_internal = true olan yorumlar musteri tarafinda gizlenir;
 *   sadece agent/supervisor gorebilir (filtre CommentDTO.getIsInternal() ile).
 * - Ticket CLOSED ise bu dialog zaten acilmaz (CustomerDashboardFrame
 *   CLOSED ticketlari listede gostermez).
 */
public class ViewTicketDialog extends JDialog {

    // ─── BAGIMLILIKLAR ───────────────────────────────────────────────────────
    private final TicketDTO ticket;
    private final TicketController ticketController;

    // ─── UI BILESENLERI ──────────────────────────────────────────────────────
    // Yorumlar paneli — yorum eklenince dinamik olarak guncellenir
    private JPanel commentsPanel;
    // Yorum yazma alani — "Add Comment" basilinca buradan okunur
    private JTextArea commentInput;

    public ViewTicketDialog(JFrame parent, TicketDTO ticket, TicketController ticketController) {
        super(parent, "Ticket Detail", true); // modal dialog
        this.ticket = ticket;
        this.ticketController = ticketController;
        initUI();
        loadComments();
    }

    private void initUI() {
        setSize(660, 700);
        setLocationRelativeTo(getParent()); // parent penceresinin ortasinda ac
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // ─── KOK PANEL ───────────────────────────────────────────────────────
        // Dikey BoxLayout: detay bolumu + yorumlar bolumu + yorum yazma bolumu
        JPanel root = new JPanel();
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
        root.setBackground(new Color(245, 247, 250));
        root.setBorder(new EmptyBorder(20, 24, 20, 24));

        // ─── TICKET DETAY KARTI ───────────────────────────────────────────────
        // Basligi, durum/oncelik etiketleri ve aciklamayi gosteren kart
        JPanel detailCard = new JPanel(new BorderLayout());
        detailCard.setBackground(Color.WHITE);
        detailCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 223, 228), 1),
                new EmptyBorder(16, 18, 16, 18)
        ));
        detailCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));

        // Ticket basligi — buyuk ve koyu font
        JLabel titleLabel = new JLabel(ticket.getTitle());
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(new Color(30, 40, 60));

        // Meta bilgi satiri — durum, oncelik, kategori, tarih yan yana
        JPanel metaRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        metaRow.setOpaque(false);
        metaRow.setBorder(new EmptyBorder(8, 0, 10, 0));

        // Durum etiketi — renkli badge
        JLabel statusBadge = makeBadge(ticket.getStatus(), statusColor(ticket.getStatus()));
        // Oncelik etiketi
        JLabel priorityBadge = makeBadge(ticket.getPriority(), priorityColor(ticket.getPriority()));
        // Kategori etiketi
        JLabel categoryLabel = new JLabel("Category: " + nvl(ticket.getCategoryName(), "-"));
        categoryLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        categoryLabel.setForeground(new Color(100, 110, 130));
        // Acilis tarihi
        String dateStr = ticket.getCreatedAt() != null
                ? ticket.getCreatedAt().toLocalDate().toString() : "-";
        JLabel dateLabel = new JLabel("Opened: " + dateStr);
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        dateLabel.setForeground(new Color(100, 110, 130));

        metaRow.add(statusBadge);
        metaRow.add(priorityBadge);
        metaRow.add(categoryLabel);
        metaRow.add(dateLabel);

        // Aciklama alani — salt okunur, word-wrap acik
        JTextArea descArea = new JTextArea(ticket.getDescription());
        descArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        descArea.setForeground(new Color(50, 60, 80));
        descArea.setEditable(false);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setBackground(Color.WHITE);
        descArea.setBorder(BorderFactory.createEmptyBorder());

        JPanel descWrapper = new JPanel(new BorderLayout());
        descWrapper.setOpaque(false);
        descWrapper.add(descArea);

        JPanel detailContent = new JPanel(new BorderLayout());
        detailContent.setOpaque(false);
        detailContent.add(titleLabel, BorderLayout.NORTH);
        detailContent.add(metaRow, BorderLayout.CENTER);
        detailContent.add(descWrapper, BorderLayout.SOUTH);

        detailCard.add(detailContent, BorderLayout.CENTER);

        // ─── YORUMLAR BOLUMU BASLIGI ─────────────────────────────────────────
        JLabel commentsTitle = new JLabel("Comments");
        commentsTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        commentsTitle.setForeground(new Color(30, 40, 60));
        commentsTitle.setBorder(new EmptyBorder(18, 0, 8, 0));
        commentsTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        // ─── YORUMLAR LISTESI PANELI ─────────────────────────────────────────
        // Dinamik panel — loadComments() her cagrildiginda icerigi yenilenir
        commentsPanel = new JPanel();
        commentsPanel.setLayout(new BoxLayout(commentsPanel, BoxLayout.Y_AXIS));
        commentsPanel.setBackground(new Color(245, 247, 250));

        // Scroll destegi — yorumlar uzadikca kaydirilabilir
        JScrollPane commentsScroll = new JScrollPane(commentsPanel);
        commentsScroll.setBorder(BorderFactory.createLineBorder(new Color(220, 223, 228), 1));
        commentsScroll.getViewport().setBackground(new Color(245, 247, 250));
        commentsScroll.setPreferredSize(new Dimension(600, 220));
        commentsScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 220));

        // ─── YORUM YAZMA ALANI ───────────────────────────────────────────────
        // Musteri buraya yorum yazar; "Add Comment" ile kaydedilir
        JLabel inputLabel = new JLabel("Add a comment");
        inputLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        inputLabel.setForeground(new Color(30, 40, 60));
        inputLabel.setBorder(new EmptyBorder(16, 0, 6, 0));
        inputLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Yorum metin alani — cok satirli, word-wrap acik
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

        // "Add Comment" butonu — yorum bos ise uyari verir
        JButton addCommentBtn = new JButton("Add Comment");
        addCommentBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        addCommentBtn.setBackground(new Color(41, 98, 255));
        addCommentBtn.setForeground(Color.WHITE);
        addCommentBtn.setFocusPainted(false);
        addCommentBtn.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        addCommentBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addCommentBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        addCommentBtn.addActionListener(e -> submitComment());

        // ─── PANEL BIRLESTIRME ───────────────────────────────────────────────
        root.add(detailCard);
        root.add(commentsTitle);
        root.add(commentsScroll);
        root.add(inputLabel);
        root.add(inputScroll);
        root.add(Box.createRigidArea(new Dimension(0, 10)));
        root.add(addCommentBtn);

        JScrollPane rootScroll = new JScrollPane(root);
        rootScroll.setBorder(BorderFactory.createEmptyBorder());
        setContentPane(rootScroll);
    }

    /**
     * Yorumlari veritabanindan ceker ve commentsPanel'i yeniden olusturur.
     * is_internal = true olan yorumlar musteri ekraninda gizlenir.
     */
    private void loadComments() {
        commentsPanel.removeAll();

        List<CommentDTO> comments = ticketController.getComments(ticket.getId());

        // is_internal = true olan yorumlari musteri goremez
        List<CommentDTO> visible = comments.stream()
                .filter(c -> c.getIsInternal() == null || !c.getIsInternal())
                .toList();

        if (visible.isEmpty()) {
            // Hic yorum yoksa bilgi mesaji goster
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
     * Tek bir yorum icin kart paneli olusturur.
     * Ust satir: yazar adi + tarih. Alt satir: yorum metni.
     */
    private JPanel buildCommentCard(CommentDTO c) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(225, 228, 235), 1),
                new EmptyBorder(10, 14, 10, 14)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

        // Yorum basligi — yazar adi ve tarih
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        // Yorumu yazan kisinin adi — kalın font
        JLabel authorLabel = new JLabel(nvl(c.getAuthorName(), "Unknown"));
        authorLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        authorLabel.setForeground(new Color(41, 98, 255));

        // Yorum tarihi — sag hizali, kucuk font
        String dateStr = c.getCreatedAt() != null
                ? c.getCreatedAt().toLocalDate().toString() : "";
        JLabel dateLabel = new JLabel(dateStr);
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        dateLabel.setForeground(new Color(160, 170, 185));

        header.add(authorLabel, BorderLayout.WEST);
        header.add(dateLabel, BorderLayout.EAST);

        // Yorum metni — salt okunur, word-wrap acik
        JTextArea contentArea = new JTextArea(c.getContent());
        contentArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        contentArea.setForeground(new Color(50, 60, 80));
        contentArea.setEditable(false);
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        contentArea.setBackground(Color.WHITE);
        contentArea.setBorder(new EmptyBorder(4, 0, 0, 0));

        card.add(header, BorderLayout.NORTH);
        card.add(contentArea, BorderLayout.CENTER);
        return card;
    }

    /**
     * Yorum gonderme islemi.
     * Bos ise kullaniciya uyari gosterir; basarili ise alan temizlenir ve
     * yorumlar yeniden yuklenir.
     */
    private void submitComment() {
        String text = commentInput.getText().trim();
        if (text.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please write a comment before submitting.",
                    "Empty Comment", JOptionPane.WARNING_MESSAGE);
            return;
        }
        ticketController.addComment(ticket.getId(), text);
        commentInput.setText(""); // alani temizle
        loadComments();           // yorumlari yenile
    }

    // ─── YARDIMCI METODLAR ───────────────────────────────────────────────────

    /** Null-safe string donusu */
    private String nvl(String value, String fallback) {
        return value != null ? value : fallback;
    }

    /** Renkli durum/oncelik etiketi olusturur */
    private JLabel makeBadge(String text, Color bg) {
        JLabel badge = new JLabel(nvl(text, "?"));
        badge.setFont(new Font("Segoe UI", Font.BOLD, 11));
        badge.setForeground(Color.WHITE);
        badge.setBackground(bg);
        badge.setOpaque(true);
        badge.setBorder(new EmptyBorder(3, 8, 3, 8));
        return badge;
    }

    /** Ticket durumuna gore badge rengi */
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

    /** Oncelik seviyesine gore badge rengi */
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
