<%-- ─────────────────────────────────────────────────────────────────────────
  error.jsp — Genel Hata Sayfası

  Bu sayfada ne olur?
    Bir Servlet beklenmedik hatayla karşılaştığında kullanıcıyı buraya
    yönlendirir. Stack trace asla gösterilmez — sadece anlamlı bir mesaj.

  Nasıl kullanılır?
    Servlet'ten redirect ile:
      resp.sendRedirect(req.getContextPath() + "/error?message=Something+went+wrong");

    message parametresi URL'de taşınır → param.message ile JSP'de okunur.
    Parametre gönderilmezse varsayılan mesaj gösterilir.
─────────────────────────────────────────────────────────────────────────── --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<%-- layout.jsp include: HTML head + navbar buradan gelir, <div class="container"> açılır --%>
<%@ include file="/WEB-INF/jsp/common/layout.jsp" %>

    <div style="text-align:center; padding: 4rem 1rem;">

        <h2 style="color: #c62828;">&#9888; Something went wrong</h2>

        <p style="color: #555; font-size: 1.1rem; margin: 1rem 0 2rem;">
            <%-- URL'den gelen message parametresi varsa göster, yoksa genel mesaj.
                 param.message → URL'deki ?message=... değeri --%>
            <c:choose>
                <c:when test="${not empty param.message}">
                    <c:out value="${param.message}"/>
                </c:when>
                <c:otherwise>
                    An unexpected error occurred. Please try again.
                </c:otherwise>
            </c:choose>
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
