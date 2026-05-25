<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%-- Supervisor Dashboard — SUPERVISOR rolündeki kullanıcıya gösterilir --%>
<!DOCTYPE html>
<html lang="tr">
<head>
    <meta charset="UTF-8">
    <title>Supervisor Dashboard — IT Helpdesk</title>
</head>
<body>

<h1>Supervisor Paneli</h1>
<p>Hoşgeldin, <c:out value="${sessionScope.currentUser.username}"/>!</p>
<p>Rol: SUPERVISOR</p>
<hr/>
<p><em>Bu sayfa henüz yapım aşamasındadır.</em></p>
<p><a href="${pageContext.request.contextPath}/logout">Çıkış Yap</a></p>

</body>
</html>
