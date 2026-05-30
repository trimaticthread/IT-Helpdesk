<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="/WEB-INF/jsp/common/layout.jsp" %>

<div class="page-header">
    <h1>Password Reset Requests</h1>
</div>

<div class="card">
    <c:choose>
        <c:when test="${empty requests}">
            <p class="text-muted">No pending password reset requests.</p>
        </c:when>
        <c:otherwise>
            <div class="table-wrap">
                <table>
                    <thead>
                        <tr>
                            <th>Username</th>
                            <th>Full Name</th>
                            <th>E-mail</th>
                            <th>Requested At</th>
                            <th>Action</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="r" items="${requests}">
                            <tr>
                                <td><c:out value="${r.username}"/></td>
                                <td><c:out value="${r.firstName}"/> <c:out value="${r.lastName}"/></td>
                                <td><c:out value="${r.email}"/></td>
                                <td><c:out value="${r.createdAt}"/></td>
                                <td>
                                    <form method="post"
                                          action="${pageContext.request.contextPath}/admin/reset-requests/${r.id}/approve"
                                          onsubmit="return confirm('Approve reset for ${r.username}? Their password will be set to &quot;password&quot;.')">
                                        <button type="submit" class="btn btn-primary btn-sm">Approve</button>
                                    </form>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </c:otherwise>
    </c:choose>
</div>

</div></body></html>
