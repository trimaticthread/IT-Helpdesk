package com.helpdesk.application.service.impl;

import com.helpdesk.application.service.PasswordResetRequestService;
import com.helpdesk.application.service.UserService;
import com.helpdesk.application.dto.UserDTO;
import com.helpdesk.domain.entity.PasswordResetRequest;
import com.helpdesk.persistence.dao.PasswordResetRequestDAO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PasswordResetRequestServiceImpl implements PasswordResetRequestService {

    private final PasswordResetRequestDAO requestDAO;
    private final UserService userService;

    public PasswordResetRequestServiceImpl(PasswordResetRequestDAO requestDAO, UserService userService) {
        this.requestDAO = requestDAO;
        this.userService = userService;
    }

    @Override
    public String submitRequest(String usernameOrEmail) {
        if (usernameOrEmail == null || usernameOrEmail.trim().isEmpty()) {
            return "Please enter your username or e-mail address.";
        }

        // Önce username, bulamazsa email ile ara
        Optional<UserDTO> found = userService.findByUsername(usernameOrEmail.trim());
        if (found.isEmpty()) {
            found = userService.findByEmail(usernameOrEmail.trim());
        }
        if (found.isEmpty()) {
            return "No account found with that username or e-mail.";
        }

        UserDTO user = found.get();
        if (!Boolean.TRUE.equals(user.getIsActive())) {
            return "This account is disabled. Contact your administrator.";
        }
        if (requestDAO.hasPendingRequest(user.getId())) {
            return "A reset request is already pending for this account. Please wait for administrator approval.";
        }

        requestDAO.create(user.getId());
        return null; // null = başarılı
    }

    @Override
    public List<PasswordResetRequest> getPendingRequests() {
        return requestDAO.findPending();
    }

    @Override
    public void approveRequest(Long requestId) {
        requestDAO.findById(requestId).ifPresent(req -> {
            userService.resetPasswordTo(req.getUserId(), "password");
            requestDAO.approve(requestId);
        });
    }

    @Override
    public int countPending() {
        return requestDAO.countPending();
    }
}
