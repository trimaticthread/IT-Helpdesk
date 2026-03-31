package com.helpdesk.application.mapper;

import com.helpdesk.application.dto.CommentDTO;
import com.helpdesk.domain.entity.Comment;

public class CommentMapper {

    public static CommentDTO toDTO(Comment comment) {
        if (comment == null) return null;
        CommentDTO dto = new CommentDTO();
        dto.setId(comment.getId());
        dto.setContent(comment.getContent());
        dto.setIsInternal(comment.getIsInternal());
        dto.setCreatedAt(comment.getCreatedAt());

        if (comment.getTicket() != null) dto.setTicketId(comment.getTicket().getId());
        if (comment.getAuthor() != null) {
            dto.setAuthorId(comment.getAuthor().getId());
            dto.setAuthorName(comment.getAuthor().getFirstName() + " " + comment.getAuthor().getLastName());
        }
        return dto;
    }
}
