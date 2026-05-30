<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ include file="/WEB-INF/jsp/common/layout.jsp" %>

<div class="page-header">
    <h1>Ticket <c:out value="${ticket.ticketNumber}"/></h1>
    <a href="${pageContext.request.contextPath}/tickets" class="btn btn-secondary">← Back to My Tickets</a>
</div>

<%-- URL'den gelen hata mesajı (yorum eklenemedi vb.) --%>
<c:if test="${not empty param.error}">
    <div class="alert alert-error"><c:out value="${param.error}"/></div>
</c:if>

<%-- ── Ticket Detayları ───────────────────────────────────────────────────── --%>
<div class="card">
    <h2 style="font-size:18px; margin-bottom:16px;"><c:out value="${ticket.title}"/></h2>

    <div class="detail-grid" style="margin-bottom:20px;">
        <div class="detail-row">
            <span class="detail-label">Status</span>
            <span class="detail-value">
                <span class="badge ${ticket.statusBadgeClass}">
                    <c:out value="${ticket.statusDisplay}"/>
                </span>
            </span>
        </div>
        <div class="detail-row">
            <span class="detail-label">Priority</span>
            <span class="detail-value">
                <span class="badge ${ticket.priorityBadgeClass}">
                    <c:out value="${ticket.priorityDisplay}"/>
                </span>
            </span>
        </div>
        <div class="detail-row">
            <span class="detail-label">Category</span>
            <span class="detail-value">
                <c:out value="${not empty ticket.categoryName ? ticket.categoryName : '—'}"/>
            </span>
        </div>
        <div class="detail-row">
            <span class="detail-label">Assigned To</span>
            <span class="detail-value">
                <c:out value="${not empty ticket.assigneeName ? ticket.assigneeName : 'Unassigned'}"/>
            </span>
        </div>
        <div class="detail-row">
            <span class="detail-label">Created</span>
            <span class="detail-value"><c:out value="${ticket.createdAtFormatted}"/></span>
        </div>
        <div class="detail-row">
            <span class="detail-label">Last Updated</span>
            <span class="detail-value"><c:out value="${ticket.updatedAt}"/></span>
        </div>
    </div>

    <div>
        <span class="detail-label" style="display:block; margin-bottom:6px;">Description</span>
        <div style="background:#f9fafb; border:1px solid #e0e5ec; border-radius:6px; padding:14px; font-size:14px; line-height:1.6; white-space:pre-wrap;">
            <c:out value="${ticket.description}"/>
        </div>
    </div>
</div>

<%-- ── Yorumlar ───────────────────────────────────────────────────────────── --%>
<div class="card">
    <h3 style="font-size:15px; font-weight:700; margin-bottom:16px;">
        Comments
        <span style="font-size:12px; color:#7a8ea0; font-weight:400;">
            (${fn:length(comments)})
        </span>
    </h3>

    <c:choose>
        <c:when test="${empty comments}">
            <p style="color:#7a8ea0; font-size:13px;">No comments yet.</p>
        </c:when>
        <c:otherwise>
            <c:forEach var="comment" items="${comments}">
                <div class="comment">
                    <div class="comment-meta">
                        <strong><c:out value="${comment.authorName}"/></strong>
                        &nbsp;·&nbsp;
                        <c:out value="${comment.createdAtFormatted}"/>
                    </div>
                    <div style="font-size:14px; line-height:1.5; white-space:pre-wrap;">
                        <c:out value="${comment.content}"/>
                    </div>
                </div>
            </c:forEach>
        </c:otherwise>
    </c:choose>

    <%-- Kapalı ticket'a yorum eklenemez --%>
    <c:if test="${ticket.status != 'CLOSED' && ticket.status != 'RESOLVED'}">
        <div style="margin-top:20px; border-top:1px solid #e0e5ec; padding-top:16px;">
            <h4 style="font-size:13px; font-weight:700; margin-bottom:10px;">Add a Comment</h4>
            <form method="post" action="${pageContext.request.contextPath}/tickets/comment">
                <input type="hidden" name="ticketId" value="${ticket.id}">
                <div class="form-group">
                    <textarea name="content"
                              rows="3"
                              placeholder="Write your message here..."
                              style="margin-bottom:8px;"></textarea>
                </div>
                <button type="submit" class="btn btn-primary">Send</button>
            </form>
        </div>
    </c:if>

    <c:if test="${ticket.status == 'RESOLVED'}">
        <div class="alert alert-info" style="margin-top:16px;">
            This ticket has been resolved. If your issue is not fixed, please open a new ticket.
        </div>
    </c:if>
    <c:if test="${ticket.status == 'CLOSED'}">
        <div class="alert alert-info" style="margin-top:16px;">
            This ticket is closed and no longer accepts comments.
        </div>
    </c:if>
</div>

</div>
</body>
</html>
