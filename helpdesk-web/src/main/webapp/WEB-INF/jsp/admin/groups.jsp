<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="/WEB-INF/jsp/common/layout.jsp" %>

<div class="page-header">
    <h1>Groups</h1>
</div>

<div class="card" style="max-width:560px;">
    <div class="card-title" style="font-weight:700;margin-bottom:12px;">Create Group</div>
    <form method="post" action="${pageContext.request.contextPath}/admin/groups/new">
        <div style="display:flex;gap:8px;flex-wrap:wrap;">
            <input type="text" name="name" placeholder="Group name" required
                   style="flex:1;min-width:160px;padding:8px 12px;border:1px solid #c8d0da;border-radius:5px;font-size:14px;">
            <input type="text" name="description" placeholder="Description"
                   style="flex:1;min-width:160px;padding:8px 12px;border:1px solid #c8d0da;border-radius:5px;font-size:14px;">
            <input type="email" name="email" placeholder="Group email"
                   style="flex:1;min-width:160px;padding:8px 12px;border:1px solid #c8d0da;border-radius:5px;font-size:14px;">
            <button type="submit" class="btn btn-primary">Create</button>
        </div>
    </form>
</div>

<c:forEach var="group" items="${groups}">
    <div class="card">
        <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:12px;">
            <strong><c:out value="${group.name}"/></strong>
            <form method="post" action="${pageContext.request.contextPath}/admin/groups/${group.id}/delete"
                  onsubmit="return confirm('Delete group ${group.name}?')">
                <button type="submit" class="btn btn-danger btn-sm">Delete Group</button>
            </form>
        </div>

        <%-- Gruba agent ekle --%>
        <form method="post" action="${pageContext.request.contextPath}/admin/groups/${group.id}/users/add"
              style="display:flex;gap:8px;margin-bottom:12px;">
            <select name="userId" style="flex:1;padding:7px 10px;border:1px solid #c8d0da;border-radius:5px;font-size:13px;">
                <option value="">-- Add Agent --</option>
                <c:forEach var="agent" items="${agents}">
                    <option value="${agent.id}"><c:out value="${agent.firstName}"/> <c:out value="${agent.lastName}"/></option>
                </c:forEach>
            </select>
            <button type="submit" class="btn btn-primary btn-sm">Add</button>
        </form>

        <%-- Gruptaki üyeler --%>
        <c:forEach var="member" items="${groupMembers[group.id]}">
            <div style="display:flex;justify-content:space-between;align-items:center;padding:6px 0;border-bottom:1px solid #f0f3f8;">
                <span><c:out value="${member.firstName}"/> <c:out value="${member.lastName}"/></span>
                <form method="post" action="${pageContext.request.contextPath}/admin/groups/${group.id}/users/remove">
                    <input type="hidden" name="userId" value="${member.id}">
                    <button type="submit" class="btn btn-secondary btn-sm">Remove</button>
                </form>
            </div>
        </c:forEach>
    </div>
</c:forEach>

</div></body></html>
