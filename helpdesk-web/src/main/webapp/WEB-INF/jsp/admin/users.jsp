<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="/WEB-INF/jsp/common/layout.jsp" %>

<div class="page-header">
    <h1>Users</h1>
    <a href="${pageContext.request.contextPath}/admin/users/new" class="btn btn-primary">+ New User</a>
</div>

<%-- Search / Filter bar --%>
<div class="card" style="margin-bottom:12px;padding:12px 16px;">
    <div style="display:flex;gap:10px;align-items:center;">
        <input type="text" id="searchInput" placeholder="Search by username or name..."
               style="flex:1;padding:7px 12px;border:1px solid #c8d0da;border-radius:5px;font-size:13px;"
               oninput="filterTable()">
        <select id="roleFilter" onchange="filterTable()"
                style="padding:7px 12px;border:1px solid #c8d0da;border-radius:5px;font-size:13px;">
            <option value="">All Roles</option>
            <option value="ADMIN">ADMIN</option>
            <option value="SUPERVISOR">SUPERVISOR</option>
            <option value="AGENT">AGENT</option>
            <option value="CUSTOMER">CUSTOMER</option>
        </select>
    </div>
</div>

<div class="card">
    <c:choose>
        <c:when test="${empty users}">
            <p class="text-muted">No users found.</p>
        </c:when>
        <c:otherwise>
            <div class="table-wrap">
                <table id="userTable">
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
                                <td>
                                    <span class="badge ${u.role == 'ADMIN' ? 'badge-closed' :
                                                         u.role == 'SUPERVISOR' ? 'badge-progress' :
                                                         u.role == 'AGENT' ? 'badge-open' : 'badge-new'}">
                                        <c:out value="${u.role}"/>
                                    </span>
                                </td>
                                <td><c:out value="${u.department}"/></td>
                                <td>${u.isActive ? 'Yes' : 'No'}</td>
                                <td>
                                    <form method="post" action="${pageContext.request.contextPath}/admin/users/${u.id}/reset-password"
                                          style="display:inline;"
                                          data-confirm="Reset password for ${u.username}? Their temporary password will be set to 'password'."
                                          data-title="Reset Password"
                                          data-confirm-label="Reset">
                                        <button type="submit" class="btn btn-secondary btn-sm">Reset PW</button>
                                    </form>
                                    <form method="post" action="${pageContext.request.contextPath}/admin/users/${u.id}/delete"
                                          style="display:inline;"
                                          data-confirm="Delete ${u.username}? This action cannot be undone."
                                          data-title="Delete User"
                                          data-confirm-label="Delete">
                                        <button type="submit" class="btn btn-danger btn-sm">Delete</button>
                                    </form>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
            <p id="noResults" style="display:none;color:#888;padding:12px;">No users match your search.</p>
        </c:otherwise>
    </c:choose>
</div>

<script>
function filterTable() {
    var search = document.getElementById('searchInput').value.toLowerCase();
    var role   = document.getElementById('roleFilter').value.toUpperCase();
    var rows   = document.querySelectorAll('#userTable tbody tr');
    var visible = 0;
    rows.forEach(function(row) {
        var cells   = row.querySelectorAll('td');
        var username = cells[0].textContent.toLowerCase();
        var name     = cells[1].textContent.toLowerCase();
        var rowRole  = cells[3].textContent.trim().toUpperCase();
        var matchSearch = !search || username.includes(search) || name.includes(search);
        var matchRole   = !role   || rowRole === role;
        var show = matchSearch && matchRole;
        row.style.display = show ? '' : 'none';
        if (show) visible++;
    });
    document.getElementById('noResults').style.display = visible === 0 ? 'block' : 'none';
}
</script>

</div></body></html>
