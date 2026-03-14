package com.helpdesk.domain.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Ticket'lara eklenen yorumlari ve notlari temsil eder.
 * Agent'lar ve musteriler ticket'a yorum ekleyebilir.

 * Iliskiler:
 * - Her yorum bir ticket'a aittir
 * - Her yorumu bir kullanici yazmistir
 */

@Entity
@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ticket_id", nullable = false)
    private Ticket ticket;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "is_internal")
    private Boolean isInternal = false;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public Comment() {}

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // Getter ve Setter, Encapsulation

    public Long getId() {
        return id;
    }public void setId(Long id) {
        this.id = id;
    }

    public Ticket getTicket() {
        return ticket;
    } public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    public User getAuthor() {
        return author;
    }public void setAuthor(User author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    } public void setContent(String content) {
        this.content = content;
    }

    public Boolean getIsInternal() {
        return isInternal;
    }public void setIsInternal(Boolean isInternal) {
        this.isInternal = isInternal;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

}