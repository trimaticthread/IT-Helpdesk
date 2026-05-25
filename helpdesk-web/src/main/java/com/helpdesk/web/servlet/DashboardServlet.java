package com.helpdesk.web.servlet;

import com.helpdesk.application.dto.UserDTO;
import com.helpdesk.web.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * ── DashboardServlet ─────────────────────────────────────────────────────────
 *
 * NOT: Bu sınıf artık kullanılmıyor.
 * Aktif implementasyon: DashboardController.java (@Controller)
 *
 * @WebServlet kaldırıldı — Spring Boot'ta DispatcherServlet tüm istekleri
 * yakaladığı için custom Servlet'lar 404 döndürüyordu.
 * Çözüm: @Controller kullanmak.
 * ─────────────────────────────────────────────────────────────────────────────
 */
public class DashboardServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        UserDTO user = SessionUtil.getUser(req);

        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String role = user.getRole();
        String jspPath;

        switch (role) {
            case "ADMIN":      jspPath = "/WEB-INF/jsp/admin/dashboard.jsp";      break;
            case "SUPERVISOR": jspPath = "/WEB-INF/jsp/supervisor/dashboard.jsp"; break;
            case "AGENT":      jspPath = "/WEB-INF/jsp/agent/dashboard.jsp";      break;
            case "CUSTOMER":   jspPath = "/WEB-INF/jsp/customer/dashboard.jsp";   break;
            default:
                resp.sendRedirect(req.getContextPath() + "/access-denied");
                return;
        }

        req.getRequestDispatcher(jspPath).forward(req, resp);
    }
}
