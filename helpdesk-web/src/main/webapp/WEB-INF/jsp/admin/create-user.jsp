<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="/WEB-INF/jsp/common/layout.jsp" %>

<div class="page-header">
    <h1>New User</h1>
    <a href="${pageContext.request.contextPath}/admin/users" class="btn btn-secondary">← Back</a>
</div>

<div class="card" style="max-width:560px;">
    <c:if test="${not empty error}">
        <div class="alert alert-error"><c:out value="${error}"/></div>
    </c:if>

    <form method="post" action="${pageContext.request.contextPath}/admin/users/new">
        <div class="form-group">
            <label>Username *</label>
            <input type="text" name="username" required value="<c:out value='${param.username}'/>">
        </div>
        <div class="form-group">
            <label>First Name *</label>
            <input type="text" name="firstName" required value="<c:out value='${param.firstName}'/>">
        </div>
        <div class="form-group">
            <label>Last Name *</label>
            <input type="text" name="lastName" required value="<c:out value='${param.lastName}'/>">
        </div>
        <div class="form-group">
            <label>Email *</label>
            <input type="email" name="email" required value="<c:out value='${param.email}'/>">
        </div>
        <div class="form-group">
            <label>Password *</label>
            <input type="password" name="password" required>
        </div>
        <div class="form-group">
            <label>Role *</label>
            <select name="role" required>
                <option value="CUSTOMER">Customer</option>
                <option value="AGENT">Agent</option>
                <option value="SUPERVISOR">Supervisor</option>
                <option value="ADMIN">Admin</option>
            </select>
        </div>
        <div class="form-group">
            <label>Department</label>
            <select name="department">
                <option value="">-- None --</option>
                <c:forEach var="dep" items="${departments}">
                    <option value="${dep.name}"><c:out value="${dep.name}"/></option>
                </c:forEach>
            </select>
        </div>
        <div style="display:flex;gap:8px;">
            <button type="submit" class="btn btn-primary">Create User</button>
            <a href="${pageContext.request.contextPath}/admin/users" class="btn btn-secondary">Cancel</a>
        </div>
    </form>
</div>

</div></body></html>
