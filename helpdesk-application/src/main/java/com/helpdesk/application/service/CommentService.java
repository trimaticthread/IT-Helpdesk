package com.helpdesk.application.service;

import com.helpdesk.application.dto.CommentDTO;
import java.util.List;

public interface CommentService {

    List<CommentDTO> findByTicketId(Long ticketId);

    CommentDTO addComment(Long ticketId, Long authorId, String content, boolean isInternal);

    void deleteById(Long id);
}
