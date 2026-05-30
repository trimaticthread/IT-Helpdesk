package com.helpdesk.web.interceptor;

import com.helpdesk.application.service.PasswordResetRequestService;
import com.helpdesk.web.util.SessionUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

public class AdminInterceptor implements HandlerInterceptor {

    private final PasswordResetRequestService resetService;

    public AdminInterceptor(PasswordResetRequestService resetService) {
        this.resetService = resetService;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response,
                           Object handler, ModelAndView mav) {
        if (mav == null) return;
        var user = SessionUtil.getUser(request);
        if (user != null && "ADMIN".equals(user.getRole())) {
            mav.addObject("pendingResetCount", resetService.countPending());
        }
    }
}
