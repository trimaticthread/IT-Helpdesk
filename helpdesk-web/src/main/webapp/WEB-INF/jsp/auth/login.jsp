<%-- ─────────────────────────────────────────────────────────────────────────
  login.jsp — Giriş Sayfası

  Bu sayfada ne olur?
    Kullanıcı kullanıcı adı ve şifresini girer, "Sign In" butonuna basar.
    Form verileri POST /login adresine gönderilir → AuthServlet karşılar.

  Buraya nasıl gelinir?
    - Giriş yapılmamış kullanıcı herhangi bir sayfaya gitmeye çalışırsa
      AuthFilter bu sayfaya yönlendirir.
    - Veya doğrudan /login URL'i yazılırsa.

  Hata mesajı nereden gelir?
    AuthServlet giriş başarısız olunca req.setAttribute("error", "...") ile
    mesajı request'e koyar ve bu sayfaya forward eder.
    JSP'de ${error} ile o mesaj ekrana çıkar.

  JSTL nedir?
    JSP'de Java kodu yazmadan koşul, döngü gibi işlemleri yapan etiket kütüphanesi.
    <%@ taglib prefix="c" uri="jakarta.tags.core" %> ile kullanıma açılır.
    <c:if>, <c:out>, <c:choose> gibi etiketler buradan gelir.
─────────────────────────────────────────────────────────────────────────── --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>IT Helpdesk — Login</title>
    <style>
        /* Sayfanın tam ortasında beyaz bir kutu göstermek için */
        body {
            margin: 0;
            font-family: Arial, sans-serif;
            background: #f0f2f5;
            display: flex;
            justify-content: center;
            align-items: center;
            min-height: 100vh;
        }
        .login-box {
            background: #fff;
            padding: 2rem 2.5rem;
            border-radius: 8px;
            box-shadow: 0 2px 12px rgba(0,0,0,.12);
            width: 340px;
        }
        .login-box h2 {
            margin: 0 0 1.5rem;
            text-align: center;
            color: #1a237e;
        }
        .login-box label {
            display: block;
            margin-bottom: .3rem;
            font-size: .9rem;
            color: #555;
        }
        .login-box input {
            width: 100%;
            padding: .55rem .7rem;
            border: 1px solid #ccc;
            border-radius: 4px;
            font-size: 1rem;
            box-sizing: border-box;
            margin-bottom: 1rem;
        }
        .login-box button {
            width: 100%;
            padding: .65rem;
            margin-top: .5rem;
            background: #1a237e;
            color: #fff;
            border: none;
            border-radius: 4px;
            font-size: 1rem;
            cursor: pointer;
        }
        .login-box button:hover { background: #283593; }
        /* Hata mesajı — kırmızı arka plan ile dikkat çeker */
        .error-msg {
            background: #ffebee;
            color: #c62828;
            padding: .6rem .8rem;
            border-radius: 4px;
            margin-bottom: 1rem;
            font-size: .9rem;
        }
    </style>
</head>
<body>

<div class="login-box">
    <h2>IT Helpdesk</h2>

    <%-- Hata mesajı varsa göster.
         "not empty" → null veya boş string DEĞİLSE demek.
         <c:out> ile yazıyoruz — XSS koruması: kullanıcı verisi direkt ekrana yazılmaz,
         özel karakterler (<, >, " gibi) HTML'e dönüştürülür. --%>
    <c:if test="${not empty error}">
        <div class="error-msg">
            <c:out value="${error}"/>
        </div>
    </c:if>

    <%-- Form POST /login → AuthServlet.doPost() çalışır
         contextPath: uygulama kök yolu (genellikle boş string, bazen /helpdesk gibi prefix olabilir) --%>
    <form method="post" action="${pageContext.request.contextPath}/login">

        <label for="username">Username</label>
        <%-- Hata sonrası formu tekrar açınca kullanıcı adını doldurulmuş göster.
             param.username → son POST isteğinde gönderilen username değeri. --%>
        <input type="text"
               id="username"
               name="username"
               value="<c:out value='${param.username}'/>"
               placeholder="Enter your username"
               autofocus
               required>

        <label for="password">Password</label>
        <%-- Şifre alanı — güvenlik gereği value hiçbir zaman doldurulmaz --%>
        <input type="password"
               id="password"
               name="password"
               placeholder="Enter your password"
               required>

        <button type="submit">Sign In</button>

    </form>

    <p style="margin-top:1.2rem;text-align:center;font-size:.85rem;">
        <a href="forgot-password" style="color:#1a237e;text-decoration:none;">Forgot your password?</a>
    </p>
</div>

</body>
</html>
