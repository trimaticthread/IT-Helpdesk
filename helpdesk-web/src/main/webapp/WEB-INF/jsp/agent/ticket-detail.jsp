<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ include file="/WEB-INF/jsp/common/layout.jsp" %>

<div class="page-header">
    <h1>Ticket <c:out value="${ticket.ticketNumber}"/></h1>
    <a href="${pageContext.request.contextPath}/tickets" class="btn btn-secondary">← Back</a>
</div>

<div class="card">
    <h2 style="font-size:18px; margin-bottom:16px;"><c:out value="${ticket.title}"/></h2>

    <div class="detail-grid" style="margin-bottom:20px;">
        <div class="detail-row">
            <span class="detail-label">Status</span>
            <span class="detail-value">
                <span class="badge badge-${fn:toLowerCase(ticket.status)}">
                    <c:out value="${ticket.status}"/>
                </span>
            </span>
        </div>
        <div class="detail-row">
            <span class="detail-label">Priority</span>
            <span class="detail-value">
                <span class="badge badge-${fn:toLowerCase(ticket.priority)}">
                    <c:out value="${ticket.priority}"/>
                </span>
            </span>
        </div>
        <div class="detail-row">
            <span class="detail-label">Requester</span>
            <span class="detail-value"><c:out value="${ticket.requesterName}"/></span>
        </div>
        <div class="detail-row">
            <span class="detail-label">Category</span>
            <span class="detail-value">
                <c:out value="${not empty ticket.categoryName ? ticket.categoryName : '—'}"/>
            </span>
        </div>
        <div class="detail-row">
            <span class="detail-label">Created</span>
            <span class="detail-value"><c:out value="${ticket.createdAt}"/></span>
        </div>
    </div>

    <div style="margin-bottom:16px;">
        <span class="detail-label" style="display:block; margin-bottom:6px;">Description</span>
        <div style="background:#f9fafb; border:1px solid #e0e5ec; border-radius:6px; padding:14px; font-size:14px; line-height:1.6; white-space:pre-wrap;">
            <c:out value="${ticket.description}"/>
        </div>
    </div>

    <%-- Status değiştirme — RESOLVED/CLOSED değilse göster --%>
    <c:if test="${ticket.status != 'RESOLVED' && ticket.status != 'CLOSED'}">
        <form method="post" action="${pageContext.request.contextPath}/tickets/${ticket.id}/status"
              style="display:flex;gap:8px;align-items:center;">
            <select name="status" style="padding:.4rem .6rem;border:1px solid #ccc;border-radius:4px;">
                <option value="IN_PROGRESS" ${ticket.status == 'IN_PROGRESS' ? 'selected' : ''}>In Progress</option>
                <option value="PENDING"     ${ticket.status == 'PENDING'     ? 'selected' : ''}>Pending</option>
                <option value="RESOLVED"    ${ticket.status == 'RESOLVED'    ? 'selected' : ''}>Resolved</option>
            </select>
            <button type="submit" class="btn btn-primary btn-sm">Update Status</button>
        </form>
    </c:if>
</div>

<%-- Comments --%>
<div class="card">
    <h3 style="font-size:15px; font-weight:700; margin-bottom:16px;">
        Comments
        <span style="font-size:12px; color:#7a8ea0; font-weight:400;">(${fn:length(comments)})</span>
    </h3>

    <c:choose>
        <c:when test="${empty comments}">
            <p style="color:#7a8ea0; font-size:13px;">No comments yet.</p>
        </c:when>
        <c:otherwise>
            <c:forEach var="comment" items="${comments}">
                <div class="comment ${comment.isInternal ? 'internal' : ''}">
                    <div class="comment-meta">
                        <strong><c:out value="${comment.authorName}"/></strong>
                        &nbsp;·&nbsp;
                        <c:out value="${comment.createdAt}"/>
                        <c:if test="${comment.isInternal}">
                            <span style="color:#e65100; font-size:11px; margin-left:6px;">[INTERNAL]</span>
                        </c:if>
                    </div>
                    <div style="font-size:14px; line-height:1.5; white-space:pre-wrap;">
                        <c:out value="${comment.content}"/>
                    </div>
                </div>
            </c:forEach>
        </c:otherwise>
    </c:choose>

    <c:if test="${ticket.status != 'RESOLVED' && ticket.status != 'CLOSED'}">
        <div style="margin-top:20px; border-top:1px solid #e0e5ec; padding-top:16px;">
            <h4 style="font-size:13px; font-weight:700; margin-bottom:10px;">Add a Comment</h4>
            <form method="post" action="${pageContext.request.contextPath}/tickets/comment">
                <input type="hidden" name="ticketId" value="${ticket.id}">
                <div class="form-group">
                    <textarea name="content" rows="3" placeholder="Write your message here..."
                              style="margin-bottom:8px;"></textarea>
                </div>
                <div class="checkbox-row" style="margin-bottom:8px;">
                    <input type="checkbox" id="isInternal" name="isInternal" value="on">
                    <label for="isInternal" style="margin:0;">Internal note (not visible to customer)</label>
                </div>
                <button type="submit" class="btn btn-primary btn-sm">Send</button>
            </form>
        </div>
    </c:if>
</div>

</div></body></html>
