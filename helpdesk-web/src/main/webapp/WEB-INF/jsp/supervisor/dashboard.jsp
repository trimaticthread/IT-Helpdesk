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
        <div class="stat-label">Total Tickets</div>
    </div>
    <div class="stat-card">
        <div class="stat-number"><c:out value="${openTickets != null ? openTickets : 0}"/></div>
        <div class="stat-label">Open / In Progress</div>
    </div>
    <div class="stat-card">
        <div class="stat-number"><c:out value="${pendingTickets != null ? pendingTickets : 0}"/></div>
        <div class="stat-label">Pending</div>
    </div>
</div>

<div class="card">
    <p style="color:#7a8ea0; font-size:13px;">
        Use <strong>All Tickets</strong> to manage and assign tickets, or view <strong>Reports</strong> for statistics.
    </p>
    <br/>
    <div style="display:flex;gap:8px;">
        <a href="${pageContext.request.contextPath}/tickets" class="btn btn-primary">All Tickets</a>
        <a href="${pageContext.request.contextPath}/supervisor/reports" class="btn btn-secondary">Reports</a>
    </div>
</div>

</div></body></html>
