<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Dashboard" scope="request"/>
<%@ include file="/WEB-INF/jsp/common/layout.jsp" %>

<div class="page-header">
    <h1>Welcome, <c:out value="${sessionScope.currentUser.firstName}"/>!</h1>
</div>

<div class="stats-grid">
    <div class="stat-card">
        <div class="stat-number"><c:out value="${totalTickets != null ? totalTickets : 0}"/></div>
        <div class="stat-label">Assigned Tickets</div>
    </div>
    <div class="stat-card">
        <div class="stat-number"><c:out value="${openTickets != null ? openTickets : 0}"/></div>
        <div class="stat-label">In Progress</div>
    </div>
    <div class="stat-card">
        <div class="stat-number"><c:out value="${resolvedTickets != null ? resolvedTickets : 0}"/></div>
        <div class="stat-label">Resolved</div>
    </div>
</div>

<div class="card">
    <p style="color:#7a8ea0; font-size:13px;">
        Use the <strong>My Tickets</strong> link to view tickets assigned to you.
    </p>
    <br/>
    <a href="${pageContext.request.contextPath}/tickets" class="btn btn-secondary">View My Tickets</a>
</div>

</div></body></html>
