<%-- ─────────────────────────────────────────────────────────────────────────
  layout.jsp — Ortak Sayfa Şablonu (Navbar)

  Bu dosya ne yapar?
    Her sayfanın üstüne include edilir. HTML <head> bölümünü ve navbar'ı
    tek yerden yönetir — değişiklik gerekirse sadece buraya dokunulur.

  Nasıl kullanılır?
    Her JSP'nin EN ÜSTÜNE şunu yaz:
      <%@ include file="/WEB-INF/jsp/common/layout.jsp" %>
    Bu satır, o JSP compile edilirken layout.jsp içeriğini fiziksel olarak
    o noktaya yapıştırır (statik include).

  pageTitle nasıl set edilir?
    Servlet içinde:
      req.setAttribute("pageTitle", "My Tickets");

  Navbar linkleri nasıl çalışır?
    sessionScope.currentUser → session'daki UserDTO nesnesi.
    SessionUtil.setUser() ile oraya koyduk, JSP buradan okur.
    Kullanıcının rolüne göre farklı linkler gösterilir.

  ÖNEMLİ: Bu dosya </div></body></html> KAPATMAZ!
    Include eden her sayfanın en sonuna şunu ekle:
      </div>  (container kapanışı)
      </body>
      </html>
─────────────────────────────────────────────────────────────────────────── --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>
        <%-- pageTitle set edilmişse "Tickets — IT Helpdesk", yoksa sadece "IT Helpdesk" --%>
        <c:choose>
            <c:when test="${not empty pageTitle}">
                <c:out value="${pageTitle}"/> — IT Helpdesk
            </c:when>
            <c:otherwise>IT Helpdesk</c:otherwise>
        </c:choose>
    </title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/style.css">
</head>
<body>

<%-- ── NAVBAR ──────────────────────────────────────────────────────────── --%>
<nav class="navbar">

    <%-- Sol: Uygulama adı — tıklayınca dashboard'a gider --%>
    <a class="navbar-brand" href="${pageContext.request.contextPath}/dashboard">
        IT Helpdesk
    </a>

    <%-- Orta: Role göre navigasyon linkleri
         sessionScope.currentUser.role → UserDTO'nun getRole() metodu --%>
    <div class="navbar-links">
        <c:choose>

            <c:when test="${sessionScope.currentUser.role == 'CUSTOMER'}">
                <a href="${pageContext.request.contextPath}/tickets">My Tickets</a>
                <a href="${pageContext.request.contextPath}/tickets/new">+ New Ticket</a>
            </c:when>

            <c:when test="${sessionScope.currentUser.role == 'AGENT'}">
                <a href="${pageContext.request.contextPath}/tickets">My Tickets</a>
            </c:when>

            <c:when test="${sessionScope.currentUser.role == 'SUPERVISOR'}">
                <a href="${pageContext.request.contextPath}/tickets">All Tickets</a>
                <a href="${pageContext.request.contextPath}/supervisor/reports">Reports</a>
            </c:when>

            <c:when test="${sessionScope.currentUser.role == 'ADMIN'}">
                <a href="${pageContext.request.contextPath}/admin/users">Users</a>
                <a href="${pageContext.request.contextPath}/admin/categories">Categories</a>
                <a href="${pageContext.request.contextPath}/admin/departments">Departments</a>
                <a href="${pageContext.request.contextPath}/admin/groups">Groups</a>
                <a href="${pageContext.request.contextPath}/admin/sla">SLA</a>
                <a href="${pageContext.request.contextPath}/admin/reset-requests"
                   style="position:relative;">
                    Reset Requests
                    <c:if test="${pendingResetCount > 0}">
                        <span style="background:#e53935;color:#fff;border-radius:10px;
                                     padding:1px 6px;font-size:.72rem;margin-left:4px;">
                            <c:out value="${pendingResetCount}"/>
                        </span>
                    </c:if>
                </a>
            </c:when>

        </c:choose>
    </div>

    <%-- Sağ: Kullanıcı adı + rolü + logout butonu --%>
    <div class="navbar-user">
        <span class="navbar-username">
            <%-- firstName alanını XSS güvenli yaz --%>
            <c:out value="${sessionScope.currentUser.firstName}"/>
            <small class="navbar-role">
                (<c:out value="${sessionScope.currentUser.role}"/>)
            </small>
        </span>
        <%-- Logout: GET /logout → AuthServlet session'ı siler, /login'e yönlendirir --%>
        <a href="${pageContext.request.contextPath}/logout" class="btn-logout">Logout</a>
    </div>

</nav>
<%-- ── SAYFA İÇERİĞİ BURADAN BAŞLAR ──────────────────────────────────── --%>
<div class="container">
