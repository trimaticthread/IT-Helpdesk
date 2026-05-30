<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>IT Helpdesk — Forgot Password</title>
    <style>
        body { margin:0; font-family:Arial,sans-serif; background:#f0f2f5;
               display:flex; justify-content:center; align-items:center; min-height:100vh; }
        .box { background:#fff; padding:2rem 2.5rem; border-radius:8px;
               box-shadow:0 2px 12px rgba(0,0,0,.12); width:360px; }
        h2 { margin:0 0 .4rem; color:#1a237e; text-align:center; }
        .subtitle { text-align:center; color:#777; font-size:.9rem; margin:0 0 1.4rem; }
        label { display:block; margin-bottom:.3rem; font-size:.9rem; color:#555; }
        input { width:100%; padding:.55rem .7rem; border:1px solid #ccc; border-radius:4px;
                font-size:1rem; box-sizing:border-box; margin-bottom:1rem; }
        button { width:100%; padding:.65rem; background:#1a237e; color:#fff;
                 border:none; border-radius:4px; font-size:1rem; cursor:pointer; }
        button:hover { background:#283593; }
        .error-msg { background:#ffebee; color:#c62828; padding:.6rem .8rem;
                     border-radius:4px; margin-bottom:1rem; font-size:.9rem; }
        .success-box { background:#e8f5e9; color:#2e7d32; padding:1rem;
                       border-radius:6px; text-align:center; font-size:.95rem; }
        .back { display:block; text-align:center; margin-top:1rem;
                font-size:.87rem; color:#1a237e; text-decoration:none; }
        .back:hover { text-decoration:underline; }
    </style>
</head>
<body>
<div class="box">
    <h2>IT Helpdesk</h2>
    <p class="subtitle">Forgot your password?</p>

    <c:choose>
        <c:when test="${success}">
            <div class="success-box">
                <strong>Request submitted.</strong><br><br>
                Your administrator will review your request. Once approved,
                your temporary password will be set to <strong>password</strong>.<br><br>
                Try logging in with that password after a short while.
            </div>
            <a class="back" href="${pageContext.request.contextPath}/login">← Back to Login</a>
        </c:when>
        <c:otherwise>
            <c:if test="${not empty error}">
                <div class="error-msg"><c:out value="${error}"/></div>
            </c:if>
            <form method="post" action="${pageContext.request.contextPath}/forgot-password">
                <label for="usernameOrEmail">Username or E-mail</label>
                <input type="text" id="usernameOrEmail" name="usernameOrEmail"
                       value="<c:out value='${usernameOrEmail}'/>"
                       placeholder="Enter your username or e-mail" autofocus required>
                <button type="submit">Send Reset Request</button>
            </form>
            <a class="back" href="${pageContext.request.contextPath}/login">← Back to Login</a>
        </c:otherwise>
    </c:choose>
</div>
</body>
</html>
