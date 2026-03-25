package com.helpdesk.domain.entity;

import java.time.LocalDateTime;

/**
 * Ticket'lara eklenen yorumlari ve notlari temsil eder.
 * Saf POJO — framework bagimliligi yok.
 *
 * is_internal = true ise sadece agent ve supervisor gorur (dahili not).
 * is_internal = false ise musteri de dahil herkes gorur.
 *
 * Iliskiler:
 * - Her yorum bir ticket'a aittir (N:1)
 * - Her yorumu bir kullanici yazmistir (N:1)
 */
public class Comment {

    private Long id;
    private Ticket ticket;
    private User author;
    private String content;
    private Boolean isInternal = false;
    private LocalDateTime createdAt;

    public Comment() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Ticket getTicket() { return ticket; }
    public void setTicket(Ticket ticket) { this.ticket = ticket; }

    public User getAuthor() { return author; }
    public void setAuthor(User author) { this.author = author; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Boolean getIsInternal() { return isInternal; }
    public void setIsInternal(Boolean isInternal) { this.isInternal = isInternal; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
