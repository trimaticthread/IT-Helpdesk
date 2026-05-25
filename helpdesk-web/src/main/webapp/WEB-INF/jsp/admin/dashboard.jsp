<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%-- Admin Dashboard — sisteme giriş yapmış ADMIN rolündeki kullanıcıya gösterilir --%>
<!DOCTYPE html>
<html lang="tr">
<head>
    <meta charset="UTF-8">
    <title>Admin Dashboard — IT Helpdesk</title>
</head>
<body>

<h1>Admin Paneli</h1>
<p>Hoşgeldin, <c:out value="${sessionScope.currentUser.username}"/>!</p>
<p>Rol: ADMIN</p>
<hr/>
<p><em>Bu sayfa henüz yapım aşamasındadır.</em></p>
<p><a href="${pageContext.request.contextPath}/logout">Çıkış Yap</a></p>

</body>
</html>
