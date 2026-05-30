<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="New Ticket" scope="request"/>
<%@ include file="/WEB-INF/jsp/common/layout.jsp" %>

<div class="page-header">
    <h1>New Support Ticket</h1>
    <a href="${pageContext.request.contextPath}/tickets" class="btn btn-secondary">← Back to My Tickets</a>
</div>

<div class="card" style="max-width:680px;">

    <%-- Hata mesajı --%>
    <c:if test="${not empty error}">
        <div class="alert alert-error"><c:out value="${error}"/></div>
    </c:if>

    <form method="post" action="${pageContext.request.contextPath}/tickets/new">

        <div class="form-group">
            <label for="title">Title <span style="color:#e53e3e;">*</span></label>
            <input type="text"
                   id="title"
                   name="title"
                   maxlength="255"
                   required
                   placeholder="Briefly describe your issue"
                   value="<c:out value='${param.title != null ? param.title : title}'/>">
        </div>

        <div class="form-group">
            <label for="description">Description <span style="color:#e53e3e;">*</span></label>
            <textarea id="description"
                      name="description"
                      rows="5"
                      required
                      placeholder="Describe the problem in detail..."><c:out value="${param.description != null ? param.description : description}"/></textarea>
        </div>

        <div style="display:flex; gap:16px;">
            <div class="form-group" style="flex:1;">
                <label for="categoryId">Category</label>
                <select id="categoryId" name="categoryId">
                    <option value="">— Select category —</option>
                    <c:forEach var="cat" items="${categories}">
                        <option value="${cat.id}"><c:out value="${cat.name}"/></option>
                    </c:forEach>
                </select>
            </div>

            <div class="form-group" style="flex:1;">
                <label for="priority">Priority</label>
                <select id="priority" name="priority">
                    <option value="LOW">Low</option>
                    <option value="MEDIUM" selected>Medium</option>
                    <option value="HIGH">High</option>
                    <option value="CRITICAL">Critical</option>
                </select>
            </div>
        </div>

        <div style="display:flex; gap:10px; margin-top:8px;">
            <button type="submit" class="btn btn-primary">Submit Ticket</button>
            <a href="${pageContext.request.contextPath}/tickets" class="btn btn-secondary">Cancel</a>
        </div>

    </form>
</div>

</div>
</body>
</html>
