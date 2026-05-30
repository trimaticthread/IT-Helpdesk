<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ include file="/WEB-INF/jsp/common/layout.jsp" %>

<div class="page-header">
    <h1>My Tickets</h1>
    <c:if test="${sessionScope.currentUser.role == 'CUSTOMER'}">
        <a href="${pageContext.request.contextPath}/tickets/new" class="btn btn-primary">+ New Ticket</a>
    </c:if>
</div>

<c:choose>
    <c:when test="${empty tickets}">
        <div class="empty-state">
            <h3>No tickets yet</h3>
            <p>You haven't submitted any support requests yet.</p>
            <c:if test="${sessionScope.currentUser.role == 'CUSTOMER'}">
                <a href="${pageContext.request.contextPath}/tickets/new" class="btn btn-primary">Create your first ticket</a>
            </c:if>
        </div>
    </c:when>
    <c:otherwise>
        <div class="table-wrapper">
            <table>
                <thead>
                    <tr>
                        <th>#</th>
                        <th>Title</th>
                        <th>Category</th>
                        <th>Priority</th>
                        <th>Status</th>
                        <th>Created</th>
                        <th></th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="ticket" items="${tickets}">
                        <tr>
                            <td style="color:#7a8ea0; font-size:12px;">
                                <c:out value="${ticket.ticketNumber}"/>
                            </td>
                            <td>
                                <a href="${pageContext.request.contextPath}/tickets/${ticket.id}"
                                   style="color:#2962ff; text-decoration:none; font-weight:600;">
                                    <c:out value="${ticket.title}"/>
                                </a>
                            </td>
                            <td>
                                <c:out value="${not empty ticket.categoryName ? ticket.categoryName : '—'}"/>
                            </td>
                            <td>
                                <%-- Priority badge --%>
                                <span class="badge ${ticket.priorityBadgeClass}">
                                    <c:out value="${ticket.priorityDisplay}"/>
                                </span>
                            </td>
                            <td>
                                <span class="badge ${ticket.statusBadgeClass}">
                                    <c:out value="${ticket.statusDisplay}"/>
                                </span>
                            </td>
                            <td style="color:#7a8ea0; font-size:12px;">
                                <c:out value="${ticket.createdAtFormatted}"/>
                            </td>
                            <td>
                                <a href="${pageContext.request.contextPath}/tickets/${ticket.id}"
                                   class="btn btn-secondary" style="padding:4px 12px; font-size:12px;">
                                    View
                                </a>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </c:otherwise>
</c:choose>

</div>
</body>
</html>
