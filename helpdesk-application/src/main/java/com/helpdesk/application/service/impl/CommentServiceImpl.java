package com.helpdesk.application.service.impl;

import com.helpdesk.application.dto.CommentDTO;
import com.helpdesk.application.mapper.CommentMapper;
import com.helpdesk.application.service.CommentService;
import com.helpdesk.domain.entity.Comment;
import com.helpdesk.domain.entity.Ticket;
import com.helpdesk.domain.entity.User;
import com.helpdesk.persistence.dao.CommentDAO;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentDAO commentDAO;

    public CommentServiceImpl(CommentDAO commentDAO) {
        this.commentDAO = commentDAO;
    }

    @Override
    public List<CommentDTO> findByTicketId(Long ticketId) {
        return commentDAO.findByTicketId(ticketId).stream()
                .map(CommentMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDTO addComment(Long ticketId, Long authorId, String content, boolean isInternal) {
        Comment comment = new Comment();
        comment.setContent(content);
        comment.setIsInternal(isInternal);

        Ticket ticket = new Ticket();
        ticket.setId(ticketId);
        comment.setTicket(ticket);

        User author = new User();
        author.setId(authorId);
        comment.setAuthor(author);

        Comment saved = commentDAO.save(comment);
        return CommentMapper.toDTO(saved);
    }

    @Override
    public void deleteById(Long id) {
        commentDAO.deleteById(id);
    }
}
