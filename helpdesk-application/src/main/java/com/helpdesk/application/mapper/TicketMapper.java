package com.helpdesk.application.mapper;

import com.helpdesk.application.dto.TicketDTO;
import com.helpdesk.domain.entity.Ticket;

public class TicketMapper {

    public static TicketDTO toDTO(Ticket ticket) {
        if (ticket == null) return null;
        TicketDTO dto = new TicketDTO();
        dto.setId(ticket.getId());
        dto.setTicketNumber(ticket.getTicketNumber());
        dto.setTitle(ticket.getTitle());
        dto.setDescription(ticket.getDescription());
        dto.setStatus(ticket.getStatus().name());
        dto.setPriority(ticket.getPriority().name());
        dto.setCreatedAt(ticket.getCreatedAt());
        dto.setUpdatedAt(ticket.getUpdatedAt());

        if (ticket.getCategory() != null) {
            dto.setCategoryId(ticket.getCategory().getId());
            dto.setCategoryName(ticket.getCategory().getName());
        }
        if (ticket.getRequester() != null) {
            dto.setRequesterId(ticket.getRequester().getId());
            dto.setRequesterName(ticket.getRequester().getFirstName() + " " + ticket.getRequester().getLastName());
        }
        if (ticket.getAssignee() != null) {
            dto.setAssigneeId(ticket.getAssignee().getId());
            dto.setAssigneeName(ticket.getAssignee().getFirstName() + " " + ticket.getAssignee().getLastName());
        }
        if (ticket.getGroup() != null) {
            dto.setGroupId(ticket.getGroup().getId());
            dto.setGroupName(ticket.getGroup().getName());
        }
        return dto;
    }
}
