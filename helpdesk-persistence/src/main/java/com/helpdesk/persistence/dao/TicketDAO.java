package com.helpdesk.persistence.dao;

import com.helpdesk.domain.entity.Ticket;
import com.helpdesk.domain.enums.TicketStatus;
import java.util.List;
import java.util.Optional;

/*
        Ticketlar üzerindeki bütün sorular bu arayüzden geçiyor.
        Listeleme, filtreleme, durum güncelleme burada tanımlanıyor.
 */



public interface TicketDAO {

    Optional<Ticket> findById(Long id);
    Optional<Ticket> findByTicketNumber(String ticketNumber);

    List<Ticket> findAll();
    List<Ticket> findByRequesterId(Long requesterId);
    List<Ticket> findByAssigneeId(Long assigneeId);
    List<Ticket> findByStatus(TicketStatus status);
    List<Ticket> findByGroupId(Long groupId);
    List<Ticket> findByCategoryId(Long categoryId);

    Ticket save(Ticket ticket);
    
    void update(Ticket ticket);
    void deleteById(Long Id);

}
