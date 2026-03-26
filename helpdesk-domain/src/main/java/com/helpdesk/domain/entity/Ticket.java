package com.helpdesk.domain.entity;

import com.helpdesk.domain.enums.TicketPriority;
import com.helpdesk.domain.enums.TicketStatus;
import java.time.LocalDateTime;

/**
 * Sistemdeki destek taleplerini (ticket) temsil eder.
 * Bir ticket, bir musterinin olusturdugu sorun veya taleptir.
 * Saf POJO — framework bagimliligi yok.
 *
 * Yasam dongusu: NEW → OPEN → IN_PROGRESS → PENDING → RESOLVED → CLOSED
 *
 * Iliskiler:
 * - Bir ticket bir musteri (requester) tarafindan olusturulur
 * - Bir ticket bir agent'a (assignee) atanabilir, atanmayabilir de
 * - Bir ticket bir kategoriye aittir
 * - Bir ticket bir gruba yonlendirilir
 */
public class Ticket {

    private Long id;
    private String ticketNumber;
    private String title;
    private String description;
    private TicketStatus status = TicketStatus.NEW;
    private TicketPriority priority = TicketPriority.MEDIUM;
    private Category category;
    private User requester;
    private User assignee;
    private Group group;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime resolvedAt;
    private LocalDateTime closedAt;
    private LocalDateTime slaDueDate;

    public Ticket() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTicketNumber() { return ticketNumber; }
    public void setTicketNumber(String ticketNumber) { this.ticketNumber = ticketNumber; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public TicketStatus getStatus() { return status; }
    public void setStatus(TicketStatus status) { this.status = status; }

    public TicketPriority getPriority() { return priority; }
    public void setPriority(TicketPriority priority) { this.priority = priority; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public User getRequester() { return requester; }
    public void setRequester(User requester) { this.requester = requester; }

    public User getAssignee() { return assignee; }
    public void setAssignee(User assignee) { this.assignee = assignee; }

    public Group getGroup() { return group; }
    public void setGroup(Group group) { this.group = group; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public LocalDateTime getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(LocalDateTime resolvedAt) { this.resolvedAt = resolvedAt; }

    public LocalDateTime getClosedAt() { return closedAt; }
    public void setClosedAt(LocalDateTime closedAt) { this.closedAt = closedAt; }

    public LocalDateTime getSlaDueDate() { return slaDueDate; }
    public void setSlaDueDate(LocalDateTime slaDueDate) { this.slaDueDate = slaDueDate; }
}
