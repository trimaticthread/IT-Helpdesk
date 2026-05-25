package com.helpdesk.application.service;

import java.util.List;
import java.util.Optional;

import com.helpdesk.application.dto.CreateTicketRequest;
import com.helpdesk.application.dto.TicketDTO;
import com.helpdesk.domain.enums.TicketStatus;

public interface TicketService {

    Optional<TicketDTO> findById(Long id);

    List<TicketDTO> findAll();

    List<TicketDTO> findByRequesterId(Long requesterId);

    List<TicketDTO> findByAssigneeId(Long assigneeId);

    List<TicketDTO> findByStatus(TicketStatus status);

    TicketDTO create(CreateTicketRequest request, Long requesterId);

    TicketDTO updateStatus(Long ticketId, TicketStatus newStatus);

    TicketDTO assignTicket(Long ticketId, Long agentId);

    void deleteById(Long id);
}
