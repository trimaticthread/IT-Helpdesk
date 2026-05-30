<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Change Password — IT Helpdesk</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/style.css">
</head>
<body>

<div class="auth-wrapper">
    <div class="auth-card">

        <h1>Change Password</h1>
        <c:choose>
            <c:when test="${sessionScope.passwordResetRequired == true}">
                <p class="subtitle" style="color:#c62828;">Your password has been reset by an administrator. Please set a new password to continue.</p>
            </c:when>
            <c:otherwise>
                <p class="subtitle">Update your account password below.</p>
            </c:otherwise>
        </c:choose>

        <%-- Hata mesajı --%>
        <c:if test="${not empty error}">
            <div class="alert alert-error"><c:out value="${error}"/></div>
        </c:if>

        <form method="post" action="${pageContext.request.contextPath}/change-password">

            <div class="form-group">
                <label for="newPassword">New Password</label>
                <input type="password"
                       id="newPassword"
                       name="newPassword"
                       placeholder="At least 6 characters"
                       autocomplete="new-password">
            </div>

            <div class="form-group">
                <label for="confirmPassword">Confirm Password</label>
                <input type="password"
                       id="confirmPassword"
                       name="confirmPassword"
                       placeholder="Repeat your new password"
                       autocomplete="new-password">
            </div>

            <button type="submit" class="btn btn-primary" style="width:100%;">
                Change Password
            </button>

        </form>

    </div>
</div>

</body>
</html>
