package com.helpdesk.application.service;

import com.helpdesk.domain.entity.SlaSettings;
import com.helpdesk.domain.enums.TicketPriority;
import java.util.List;

public interface SlaService {
    List<SlaSettings> getAllSlaSettings();
    SlaSettings getByPriority(TicketPriority priority);
    void updateSla(TicketPriority priority, int responseMinutes, int resolutionMinutes);
}
