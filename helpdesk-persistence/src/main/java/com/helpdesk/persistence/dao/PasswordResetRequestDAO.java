package com.helpdesk.persistence.dao;

import com.helpdesk.domain.entity.PasswordResetRequest;
import java.util.List;
import java.util.Optional;

public interface PasswordResetRequestDAO {
    void create(Long userId);
    List<PasswordResetRequest> findPending();
    Optional<PasswordResetRequest> findById(Long id);
    void approve(Long id);
    boolean hasPendingRequest(Long userId);
    int countPending();
}
