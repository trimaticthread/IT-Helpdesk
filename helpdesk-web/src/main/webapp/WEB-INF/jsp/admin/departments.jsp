<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="/WEB-INF/jsp/common/layout.jsp" %>

<div class="page-header">
    <h1>Departments</h1>
</div>

<div class="card" style="max-width:400px;">
    <div class="card-title" style="font-weight:700;margin-bottom:12px;">Add Department</div>
    <form method="post" action="${pageContext.request.contextPath}/admin/departments/new" style="display:flex;gap:8px;">
        <input type="text" name="name" placeholder="Department name" required
               style="flex:1;padding:8px 12px;border:1px solid #c8d0da;border-radius:5px;font-size:14px;">
        <button type="submit" class="btn btn-primary">Add</button>
    </form>
</div>

<div class="table-wrapper">
    <table>
        <thead>
            <tr>
                <th>Name</th>
                <th>Status</th>
                <th>Actions</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="dep" items="${departments}">
                <tr>
                    <td><c:out value="${dep.name}"/></td>
                    <td>
                        <span class="badge ${dep.isActive ? 'badge-open' : 'badge-closed'}">
                            ${dep.isActive ? 'Active' : 'Inactive'}
                        </span>
                    </td>
                    <td style="display:flex;gap:6px;">
                        <form method="post" action="${pageContext.request.contextPath}/admin/departments/${dep.id}/toggle">
                            <button type="submit" class="btn btn-secondary btn-sm">
                                ${dep.isActive ? 'Deactivate' : 'Activate'}
                            </button>
                        </form>
                        <form method="post" action="${pageContext.request.contextPath}/admin/departments/${dep.id}/delete"
                              onsubmit="return confirm('Delete this department?')">
                            <button type="submit" class="btn btn-danger btn-sm">Delete</button>
                        </form>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</div>

</div></body></html>
