package com.helpdesk.application.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TicketDTO {

    private Long id;
    private String ticketNumber;
    private String title;
    private String description;
    private String status;
    private String priority;
    private String categoryName;
    private Long categoryId;
    private String requesterName;
    private Long requesterId;
    private String assigneeName;
    private Long assigneeId;
    private String groupName;
    private Long groupId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public TicketDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTicketNumber() { return ticketNumber; }
    public void setTicketNumber(String ticketNumber) { this.ticketNumber = ticketNumber; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    public String getRequesterName() { return requesterName; }
    public void setRequesterName(String requesterName) { this.requesterName = requesterName; }

    public Long getRequesterId() { return requesterId; }
    public void setRequesterId(Long requesterId) { this.requesterId = requesterId; }

    public String getAssigneeName() { return assigneeName; }
    public void setAssigneeName(String assigneeName) { this.assigneeName = assigneeName; }

    public Long getAssigneeId() { return assigneeId; }
    public void setAssigneeId(Long assigneeId) { this.assigneeId = assigneeId; }

    public String getGroupName() { return groupName; }
    public void setGroupName(String groupName) { this.groupName = groupName; }

    public Long getGroupId() { return groupId; }
    public void setGroupId(Long groupId) { this.groupId = groupId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    private LocalDateTime slaDueDate;
    public LocalDateTime getSlaDueDate() { return slaDueDate; }
    public void setSlaDueDate(LocalDateTime slaDueDate) { this.slaDueDate = slaDueDate; }

    public String getSlaDueDateFormatted() {
        if (slaDueDate == null) return "—";
        return slaDueDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    public String getSlaStatus() {
        if (slaDueDate == null) return "unknown";
        if ("RESOLVED".equals(status) || "CLOSED".equals(status)) return "met";
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        if (now.isAfter(slaDueDate)) return "breached";
        if (now.isAfter(slaDueDate.minusHours(2))) return "warning";
        return "ok";
    }

    public String getSlaLabel() {
        switch (getSlaStatus()) {
            case "breached": return "SLA Breached";
            case "warning":  return "Due Soon";
            case "met":      return "SLA Met";
            case "ok":       return "On Track";
            default:         return "—";
        }
    }

    public String getSlaBadgeStyle() {
        switch (getSlaStatus()) {
            case "breached": return "background:#ffebee;color:#c62828;border-radius:12px;padding:3px 9px;font-size:11px;font-weight:700;";
            case "warning":  return "background:#fffde7;color:#f57f17;border-radius:12px;padding:3px 9px;font-size:11px;font-weight:700;";
            case "met":      return "background:#e8f5e9;color:#2e7d32;border-radius:12px;padding:3px 9px;font-size:11px;font-weight:700;";
            case "ok":       return "background:#e3f2fd;color:#1565c0;border-radius:12px;padding:3px 9px;font-size:11px;font-weight:700;";
            default:         return "background:#f5f5f5;color:#757575;border-radius:12px;padding:3px 9px;font-size:11px;font-weight:700;";
        }
    }

    public String getCreatedAtFormatted() {
        if (createdAt == null) return "";
        return createdAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    public String getStatusDisplay() {
        if (status == null) return "";
        switch (status) {
            case "NEW":         return "New";
            case "OPEN":        return "Open";
            case "IN_PROGRESS": return "In Progress";
            case "PENDING":     return "Pending";
            case "RESOLVED":    return "Resolved";
            case "CLOSED":      return "Closed";
            default:            return status;
        }
    }

    public String getStatusBadgeClass() {
        if (status == null) return "badge-new";
        switch (status) {
            case "NEW":         return "badge-new";
            case "OPEN":        return "badge-open";
            case "IN_PROGRESS": return "badge-in-progress";
            case "PENDING":     return "badge-pending";
            case "RESOLVED":    return "badge-resolved";
            case "CLOSED":      return "badge-closed";
            default:            return "badge-new";
        }
    }

    public String getPriorityDisplay() {
        if (priority == null) return "";
        switch (priority) {
            case "LOW":      return "Low";
            case "MEDIUM":   return "Medium";
            case "HIGH":     return "High";
            case "CRITICAL": return "Critical";
            default:         return priority;
        }
    }

    public String getPriorityBadgeClass() {
        if (priority == null) return "";
        switch (priority) {
            case "LOW":      return "badge-low";
            case "MEDIUM":   return "badge-medium";
            case "HIGH":     return "badge-high";
            case "CRITICAL": return "badge-critical";
            default:         return "";
        }
    }
}
