package com.helpdesk.desktop.controller;

import com.helpdesk.application.service.SlaService;
import com.helpdesk.domain.entity.SlaSettings;
import com.helpdesk.domain.enums.TicketPriority;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class SlaController {

    private final SlaService slaService;

    public SlaController(SlaService slaService) {
        this.slaService = slaService;
    }

    public List<SlaSettings> getAllSlaSettings() {
        return slaService.getAllSlaSettings();
    }

    public void updateSla(TicketPriority priority, int responseMinutes, int resolutionMinutes) {
        slaService.updateSla(priority, responseMinutes, resolutionMinutes);
    }
}
