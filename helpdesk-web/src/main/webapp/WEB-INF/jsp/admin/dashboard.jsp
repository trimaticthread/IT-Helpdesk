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
        <div class="stat-label">Open</div>
    </div>
    <div class="stat-card">
        <div class="stat-number"><c:out value="${resolvedTickets != null ? resolvedTickets : 0}"/></div>
        <div class="stat-label">Resolved / Closed</div>
    </div>
</div>

<div class="card">
    <div class="card-title" style="font-weight:700;margin-bottom:12px;">Administration</div>
    <div style="display:flex;gap:8px;flex-wrap:wrap;">
        <a href="${pageContext.request.contextPath}/admin/users"       class="btn btn-secondary">Users</a>
        <a href="${pageContext.request.contextPath}/admin/categories"  class="btn btn-secondary">Categories</a>
        <a href="${pageContext.request.contextPath}/admin/departments" class="btn btn-secondary">Departments</a>
        <a href="${pageContext.request.contextPath}/admin/groups"      class="btn btn-secondary">Groups</a>
        <a href="${pageContext.request.contextPath}/admin/sla"         class="btn btn-secondary">SLA</a>
    </div>
</div>

</div></body></html>
