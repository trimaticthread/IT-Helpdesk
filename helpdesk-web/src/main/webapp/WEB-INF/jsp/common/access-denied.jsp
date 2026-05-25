<%-- ─────────────────────────────────────────────────────────────────────────
  access-denied.jsp — Yetkisiz Erişim Sayfası

  Bu sayfada ne olur?
    Kullanıcı kendi rolünün yetkisi dışında bir sayfaya erişmeye çalışırsa
    buraya düşer.
    Örnek: CUSTOMER rolündeki biri /admin/users URL'ine giderse.

  Nasıl kullanılır?
    Servlet'in en üstünde rol kontrolü yapılır:
      if (!SessionUtil.hasRole(req, "ADMIN")) {
          resp.sendRedirect(req.getContextPath() + "/access-denied");
          return;  ← return ŞART, yoksa kod çalışmaya devam eder
      }
─────────────────────────────────────────────────────────────────────────── --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<%-- layout.jsp include: HTML head + navbar buradan gelir, <div class="container"> açılır --%>
<%@ include file="/WEB-INF/jsp/common/layout.jsp" %>

    <div style="text-align:center; padding: 4rem 1rem;">

        <h2 style="color: #e65100;">&#128683; Access Denied</h2>

        <p style="color: #555; font-size: 1.1rem; margin: 1rem 0 2rem;">
            You don't have permission to access this page.
        </p>

        <%-- Kullanıcıyı dashboard'a geri götüren buton --%>
        <a href="${pageContext.request.contextPath}/dashboard"
           style="display:inline-block; padding:.65rem 1.6rem; background:#1a237e;
                  color:#fff; border-radius:4px; text-decoration:none; font-size:1rem;">
            Return to Dashboard
        </a>

    </div>

</div><%-- layout.jsp'nin açtığı .container kapanıyor --%>
</body>
</html>
