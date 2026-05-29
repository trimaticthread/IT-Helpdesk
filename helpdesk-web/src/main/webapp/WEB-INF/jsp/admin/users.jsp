<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="/WEB-INF/jsp/common/layout.jsp" %>

<div class="page-header">
    <h1>Users</h1>
    <a href="${pageContext.request.contextPath}/admin/users/new" class="btn btn-primary">+ New User</a>
</div>

<div class="card">
    <c:choose>
        <c:when test="${empty users}">
            <p class="text-muted">No users found.</p>
        </c:when>
        <c:otherwise>
            <div class="table-wrap">
                <table>
                    <thead>
                        <tr>
                            <th>Username</th>
                            <th>Full Name</th>
                            <th>Email</th>
                            <th>Role</th>
                            <th>Department</th>
                            <th>Active</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="u" items="${users}">
                            <tr>
                                <td><c:out value="${u.username}"/></td>
                                <td><c:out value="${u.firstName}"/> <c:out value="${u.lastName}"/></td>
                                <td><c:out value="${u.email}"/></td>
                                <td><c:out value="${u.role}"/></td>
                                <td><c:out value="${u.department}"/></td>
                                <td>${u.isActive ? 'Yes' : 'No'}</td>
                                <td>
                                    <form method="post" action="${pageContext.request.contextPath}/admin/users/${u.id}/reset-password"
                                          style="display:inline;" onsubmit="return confirm('Reset password for ${u.username}?')">
                                        <button type="submit" class="btn btn-secondary btn-sm">Reset PW</button>
                                    </form>
                                    <form method="post" action="${pageContext.request.contextPath}/admin/users/${u.id}/delete"
                                          style="display:inline;" onsubmit="return confirm('Delete ${u.username}?')">
                                        <button type="submit" class="btn btn-danger btn-sm">Delete</button>
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
