package com.helpdesk.application.service;

import com.helpdesk.domain.entity.PasswordResetRequest;
import java.util.List;

public interface PasswordResetRequestService {
    /** Kullanıcı adı veya e-posta ile talep oluşturur. Hata varsa mesaj döner, başarıysa null. */
    String submitRequest(String usernameOrEmail);
    List<PasswordResetRequest> getPendingRequests();
    void approveRequest(Long requestId);
    int countPending();
}
