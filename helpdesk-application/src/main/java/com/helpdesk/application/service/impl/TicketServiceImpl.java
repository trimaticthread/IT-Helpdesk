package com.helpdesk.application.service.impl;

import com.helpdesk.application.dto.CreateTicketRequest;
import com.helpdesk.application.dto.TicketDTO;
import com.helpdesk.application.mapper.TicketMapper;
import com.helpdesk.application.service.TicketService;
import com.helpdesk.domain.entity.Category;
import com.helpdesk.domain.entity.Ticket;
import com.helpdesk.domain.entity.User;
import com.helpdesk.domain.enums.TicketPriority;
import com.helpdesk.domain.enums.TicketStatus;
import com.helpdesk.domain.exception.ResourceNotFoundException;
import com.helpdesk.persistence.dao.TicketDAO;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TicketServiceImpl implements TicketService {

    private final TicketDAO ticketDAO;

    public TicketServiceImpl(TicketDAO ticketDAO) {
        this.ticketDAO = ticketDAO;
    }

    @Override
    public Optional<TicketDTO> findById(Long id) {
        return ticketDAO.findById(id).map(TicketMapper::toDTO);
    }

    @Override
    public List<TicketDTO> findAll() {
        return ticketDAO.findAll().stream()
                .map(TicketMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<TicketDTO> findByRequesterId(Long requesterId) {
        return ticketDAO.findByRequesterId(requesterId).stream()
                .map(TicketMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<TicketDTO> findByAssigneeId(Long assigneeId) {
        return ticketDAO.findByAssigneeId(assigneeId).stream()
                .map(TicketMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<TicketDTO> findByStatus(TicketStatus status) {
        return ticketDAO.findByStatus(status).stream()
                .map(TicketMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public TicketDTO create(CreateTicketRequest request, Long requesterId) {
        Ticket ticket = new Ticket();
        ticket.setTitle(request.getTitle());
        ticket.setDescription(request.getDescription());
        ticket.setStatus(TicketStatus.NEW);
        ticket.setPriority(TicketPriority.valueOf(request.getPriority()));
        ticket.setTicketNumber(generateTicketNumber());

        Category category = new Category();
        category.setId(request.getCategoryId());
        ticket.setCategory(category);

        User requester = new User();
        requester.setId(requesterId);
        ticket.setRequester(requester);

        Ticket saved = ticketDAO.save(ticket);
        return TicketMapper.toDTO(saved);
    }

    @Override
    public TicketDTO updateStatus(Long ticketId, TicketStatus newStatus) {
        Ticket ticket = ticketDAO.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket bulunamadi: " + ticketId));
        ticket.setStatus(newStatus);
        if (newStatus == TicketStatus.RESOLVED) ticket.setResolvedAt(LocalDateTime.now());
        if (newStatus == TicketStatus.CLOSED) ticket.setClosedAt(LocalDateTime.now());
        ticketDAO.update(ticket);
        return TicketMapper.toDTO(ticket);
    }

    @Override
    public void deleteById(Long id) {
        ticketDAO.deleteById(id);
    }

    private String generateTicketNumber() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        return "TKT-" + timestamp;
    }
}
