<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Dashboard" scope="request"/>
<%@ include file="/WEB-INF/jsp/common/layout.jsp" %>

<div class="page-header">
    <h1>Welcome, <c:out value="${sessionScope.currentUser.firstName}"/>!</h1>
    <a href="${pageContext.request.contextPath}/tickets/new" class="btn btn-primary">+ New Ticket</a>
</div>

<%-- Şifre değiştirildi bildirimi --%>
<c:if test="${param.passwordChanged == 'true'}">
    <div class="alert alert-success">Your password has been changed successfully.</div>
</c:if>

<div class="stats-grid">
    <div class="stat-card">
        <div class="stat-number"><c:out value="${totalTickets != null ? totalTickets : 0}"/></div>
        <div class="stat-label">Total Tickets</div>
    </div>
    <div class="stat-card">
        <div class="stat-number"><c:out value="${openTickets != null ? openTickets : 0}"/></div>
        <div class="stat-label">Open</div>
    </div>
    <div class="stat-card">
        <div class="stat-number"><c:out value="${resolvedTickets != null ? resolvedTickets : 0}"/></div>
        <div class="stat-label">Resolved</div>
    </div>
</div>

<div class="card">
    <p style="color:#7a8ea0; font-size:13px;">
        Use the <strong>My Tickets</strong> link in the navigation to view all your tickets,
        or click <strong>+ New Ticket</strong> to submit a support request.
    </p>
    <br/>
    <a href="${pageContext.request.contextPath}/tickets" class="btn btn-secondary">View My Tickets</a>
</div>

</div>
</body>
</html>
