<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="/WEB-INF/jsp/common/layout.jsp" %>

<div class="page-header">
    <h1>Reports</h1>
</div>

<div style="display:grid;grid-template-columns:1fr 1fr;gap:20px;">

    <%-- Status dağılımı --%>
    <div class="card">
        <div class="card-title" style="font-weight:700;margin-bottom:12px;">By Status</div>
        <table>
            <thead><tr><th>Status</th><th>Count</th></tr></thead>
            <tbody>
                <c:forEach var="entry" items="${byStatus}">
                    <tr>
                        <td><span class="badge badge-${entry.key.toLowerCase().replace('_','-')}"><c:out value="${entry.key}"/></span></td>
                        <td><c:out value="${entry.value}"/></td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </div>

    <%-- Öncelik dağılımı --%>
    <div class="card">
        <div class="card-title" style="font-weight:700;margin-bottom:12px;">By Priority</div>
        <table>
            <thead><tr><th>Priority</th><th>Count</th></tr></thead>
            <tbody>
                <c:forEach var="entry" items="${byPriority}">
                    <tr>
                        <td><span class="badge badge-${entry.key.toLowerCase()}"><c:out value="${entry.key}"/></span></td>
                        <td><c:out value="${entry.value}"/></td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </div>

    <%-- Kategori dağılımı --%>
    <div class="card" style="grid-column:1/-1;">
        <div class="card-title" style="font-weight:700;margin-bottom:12px;">By Category</div>
        <table>
            <thead><tr><th>Category</th><th>Count</th></tr></thead>
            <tbody>
                <c:forEach var="entry" items="${byCategory}">
                    <tr>
                        <td><c:out value="${entry.key}"/></td>
                        <td><c:out value="${entry.value}"/></td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </div>

</div>

</div></body></html>
