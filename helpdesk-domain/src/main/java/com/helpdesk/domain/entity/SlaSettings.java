package com.helpdesk.domain.entity;

import com.helpdesk.domain.enums.TicketPriority;

public class SlaSettings {

    private Long id;
    private TicketPriority priority;
    private int responseTimeMinutes;
    private int resolutionTimeMinutes;

    public SlaSettings() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public TicketPriority getPriority() { return priority; }
    public void setPriority(TicketPriority priority) { this.priority = priority; }

    public int getResponseTimeMinutes() { return responseTimeMinutes; }
    public void setResponseTimeMinutes(int responseTimeMinutes) { this.responseTimeMinutes = responseTimeMinutes; }

    public int getResolutionTimeMinutes() { return resolutionTimeMinutes; }
    public void setResolutionTimeMinutes(int resolutionTimeMinutes) { this.resolutionTimeMinutes = resolutionTimeMinutes; }
}
