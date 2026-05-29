<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ include file="/WEB-INF/jsp/common/layout.jsp" %>

<div class="page-header">
    <h1>All Tickets</h1>
</div>

<div class="card">
    <form method="get" action="${pageContext.request.contextPath}/tickets" style="display:flex;gap:1.5rem;margin-bottom:.75rem;">
        <label class="checkbox-row">
            <input type="checkbox" name="showResolved" value="true"
                   <c:if test="${showResolved}">checked</c:if>
                   onchange="this.form.submit()">
            Show Resolved
        </label>
        <label class="checkbox-row">
            <input type="checkbox" name="showClosed" value="true"
                   <c:if test="${showClosed}">checked</c:if>
                   onchange="this.form.submit()">
            Show Closed
        </label>
    </form>

    <c:choose>
        <c:when test="${empty tickets}">
            <p class="text-muted">No tickets found.</p>
        </c:when>
        <c:otherwise>
            <div class="table-wrap">
                <table>
                    <thead>
                        <tr>
                            <th>#</th>
                            <th>Title</th>
                            <th>Status</th>
                            <th>Priority</th>
                            <th>Requester</th>
                            <th>Assigned To</th>
                            <th>Category</th>
                            <th>Created</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="t" items="${tickets}">
                            <tr>
                                <td><a href="${pageContext.request.contextPath}/tickets/${t.id}"><c:out value="${t.ticketNumber}"/></a></td>
                                <td><a href="${pageContext.request.contextPath}/tickets/${t.id}"><c:out value="${t.title}"/></a></td>
                                <td><span class="badge badge-${fn:toLowerCase(t.status)}"><c:out value="${t.status}"/></span></td>
                                <td><span class="badge badge-${fn:toLowerCase(t.priority)}"><c:out value="${t.priority}"/></span></td>
                                <td><c:out value="${t.requesterName}"/></td>
                                <td><c:out value="${empty t.assigneeName ? 'Unassigned' : t.assigneeName}"/></td>
                                <td><c:out value="${t.categoryName}"/></td>
                                <td><c:out value="${t.createdAt}"/></td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </c:otherwise>
    </c:choose>
</div>

</div></body></html>
