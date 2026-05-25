package com.helpdesk.persistence.dao;

import com.helpdesk.domain.entity.SlaSettings;
import com.helpdesk.domain.enums.TicketPriority;
import java.util.List;
import java.util.Optional;

public interface SlaDAO {
    List<SlaSettings> findAll();
    Optional<SlaSettings> findByPriority(TicketPriority priority);
    void update(SlaSettings slaSettings);
}
