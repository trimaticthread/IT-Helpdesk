package com.helpdesk.persistence.dao;

import com.helpdesk.domain.entity.Comment;
import java.util.List;
import java.util.Optional;

public interface CommentDAO {

    Optional<Comment> findById(Long id);

    List<Comment> findByTicketId(Long ticketId);
    List<Comment> findInternalByTicketId(Long ticketId);

    Comment save(Comment comment);

    void deleteById(Long Id);
}
