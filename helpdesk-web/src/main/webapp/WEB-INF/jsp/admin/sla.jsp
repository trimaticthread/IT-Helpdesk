<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="/WEB-INF/jsp/common/layout.jsp" %>

<div class="page-header">
    <h1>SLA Settings</h1>
</div>

<div class="table-wrapper">
    <table>
        <thead>
            <tr>
                <th>Priority</th>
                <th>Response Time (min)</th>
                <th>Resolution Time (min)</th>
                <th>Actions</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="sla" items="${slaList}">
                <tr>
                    <td>
                        <span class="badge badge-${sla.priority.toString().toLowerCase()}">
                            <c:out value="${sla.priority}"/>
                        </span>
                    </td>
                    <td><c:out value="${sla.responseTimeMinutes}"/></td>
                    <td><c:out value="${sla.resolutionTimeMinutes}"/></td>
                    <td>
                        <form method="post" action="${pageContext.request.contextPath}/admin/sla"
                              style="display:flex;gap:8px;align-items:center;">
                            <input type="hidden" name="priority" value="${sla.priority}">
                            <input type="number" name="responseMinutes" value="${sla.responseTimeMinutes}" min="1"
                                   style="width:80px;padding:5px 8px;border:1px solid #c8d0da;border-radius:4px;">
                            <input type="number" name="resolutionMinutes" value="${sla.resolutionTimeMinutes}" min="1"
                                   style="width:80px;padding:5px 8px;border:1px solid #c8d0da;border-radius:4px;">
                            <button type="submit" class="btn btn-primary btn-sm">Save</button>
                        </form>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</div>

</div></body></html>
