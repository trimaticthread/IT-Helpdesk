package com.helpdesk.application.service.impl;

import com.helpdesk.application.service.SlaService;
import com.helpdesk.domain.entity.SlaSettings;
import com.helpdesk.domain.enums.TicketPriority;
import com.helpdesk.domain.exception.BusinessException;
import com.helpdesk.persistence.dao.SlaDAO;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class SlaServiceImpl implements SlaService {

    private final SlaDAO slaDAO;

    public SlaServiceImpl(SlaDAO slaDAO) {
        this.slaDAO = slaDAO;
    }

    @Override
    public List<SlaSettings> getAllSlaSettings() {
        return slaDAO.findAll();
    }

    @Override
    public SlaSettings getByPriority(TicketPriority priority) {
        return slaDAO.findByPriority(priority)
                .orElseThrow(() -> new BusinessException("SLA not found for priority: " + priority));
    }

    @Override
    public void updateSla(TicketPriority priority, int responseMinutes, int resolutionMinutes) {
        if (responseMinutes <= 0 || resolutionMinutes <= 0) {
            throw new BusinessException("SLA süreleri 0'dan büyük olmalıdır.");
        }
        if (resolutionMinutes <= responseMinutes) {
            throw new BusinessException("Çözüm süresi yanıt süresinden büyük olmalıdır.");
        }
        SlaSettings sla = getByPriority(priority);
        sla.setResponseTimeMinutes(responseMinutes);
        sla.setResolutionTimeMinutes(resolutionMinutes);
        slaDAO.update(sla);
    }
}
