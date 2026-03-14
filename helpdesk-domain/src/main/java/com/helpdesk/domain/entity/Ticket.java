package com.helpdesk.domain.entity;

import com.helpdesk.domain.enums.TicketPriority;
import com.helpdesk.domain.enums.TicketStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Sistemdeki destek taleplerini temsil eder.
 * Bir ticket, bir musterinin olusturdugu sorun veya taleptir.
 * Ticket bir agent'a  atanir, bir kategoriye ve bir gruba aittir.
 *
 * Yasam dongusu: NEW → OPEN → IN_PROGRESS → PENDING → RESOLVED → CLOSED
 *
 * Iliskiler:
 * - Bir ticket bir musteri tarafindan olusturulur
 * - Bir ticket bir agent'a atanabilir
 * - Bir ticket bir kategoriye aittir
 * - Bir ticket bir gruba yonlendirilir
 * - Bir ticket'a birden fazla yorum eklenebilir
 * - Bir ticket'a birden fazla dosya eklenebilir
 */
@Entity
@Table(name = "tickets")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ticket_number", nullable = false, unique = true, length = 20)
    private String ticketNumber;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private TicketStatus status = TicketStatus.NEW;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority")
    private TicketPriority priority = TicketPriority.MEDIUM;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "requester_id", nullable = false)
    private User requester;

    @ManyToOne
    @JoinColumn(name = "assignee_id")
    private User assignee;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group group;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    @Column(name = "closed_at")
    private LocalDateTime closedAt;

    @Column(name = "sla_due_date")
    private LocalDateTime slaDueDate;

    public Ticket() {}

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Getter ve Setter, Encapsulation

    public Long getId() {
        return id;
    }public void setId(Long id) {
        this.id = id;
    }

    public String getTicketNumber() {
        return ticketNumber;
    }public void setTicketNumber(String ticketNumber) {
        this.ticketNumber = ticketNumber;
    }

    public String getTitle() {
        return title;
    }public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }public void setDescription(String description) {
        this.description = description;
    }

    public TicketStatus getStatus() {
        return status;
    }public void setStatus(TicketStatus status) {
        this.status = status;
    }

    public TicketPriority getPriority() {
        return priority;
    }public void setPriority(TicketPriority priority) {
        this.priority = priority;
    }

    public Category getCategory() {
        return category;
    } public void setCategory(Category category) {
        this.category = category;
    }

    public User getRequester() {
        return requester;
    }public void setRequester(User requester) {
        this.requester = requester;
    }

    public User getAssignee() {
        return assignee;
    }public void setAssignee(User assignee) {
        this.assignee = assignee;
    }

    public Group getGroup() {
        return group;
    } public void setGroup(Group group) {
        this.group = group;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    } public void setResolvedAt(LocalDateTime resolvedAt) {
        this.resolvedAt = resolvedAt;
    }

    public LocalDateTime getClosedAt() {
        return closedAt;
    }public void setClosedAt(LocalDateTime closedAt) {
        this.closedAt = closedAt;
    }

    public LocalDateTime getSlaDueDate() {
        return slaDueDate;
    }public void setSlaDueDate(LocalDateTime slaDueDate) {
        this.slaDueDate = slaDueDate;
    }
}