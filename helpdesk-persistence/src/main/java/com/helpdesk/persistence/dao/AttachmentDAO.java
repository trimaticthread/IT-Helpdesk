package com.helpdesk.persistence.dao;

import com.helpdesk.domain.entity.Attachment;
import java.util.List;
import java.util.Optional;


public interface AttachmentDAO {

    Optional<Attachment> findById(Long Id);
    List<Attachment> findByTicketId(Long ticketId);
    List<Attachment> findByUploadedBy(Long userId);

    Attachment save(Attachment attachment);

    void deleteById(Long Id);

}
