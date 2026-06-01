package com.helpdesk.desktop.controller;

import com.helpdesk.application.service.PasswordResetRequestService;
import com.helpdesk.domain.entity.PasswordResetRequest;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PasswordResetRequestController {

    private final PasswordResetRequestService service;

    public PasswordResetRequestController(PasswordResetRequestService service) {
        this.service = service;
    }

    public String submitRequest(String usernameOrEmail) {
        return service.submitRequest(usernameOrEmail);
    }

    public List<PasswordResetRequest> getPendingRequests() {
        return service.getPendingRequests();
    }

    public void approveRequest(Long requestId) {
        service.approveRequest(requestId);
    }

    public int countPending() {
        return service.countPending();
    }
}
