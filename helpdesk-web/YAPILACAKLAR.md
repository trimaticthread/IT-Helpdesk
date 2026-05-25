# helpdesk-web — Yapılacaklar Listesi

Modül iskeleti hazır. Aşağıdaki her madde ya sen ya da takım arkadaşın tarafından doldurulacak.
Kılavuz için: `C:\Users\Toprak\IdeaProjects\HELPDESK_WEB_KILAVUZU.md`

---

## BEN yapacağım

- [ ] `util/SessionUtil.java` — HttpSession yardımcısı (getUser, setUser, isLoggedIn, hasRole)
- [ ] `filter/AuthFilter.java` — her istekte session kontrolü, /login hariç
- [ ] `filter/EncodingFilter.java` — UTF-8 zorla
- [ ] `config/WebConfig.java` — Servlet'leri ve Filter'ları Spring bean olarak kaydet
- [ ] `servlet/AuthServlet.java` — GET/POST /login, GET /logout
- [ ] `config/AppConfig.java` — BCryptPasswordEncoder bean (desktop'takinin aynısı)
- [ ] `webapp/WEB-INF/jsp/auth/login.jsp` — giriş formu
- [ ] `webapp/WEB-INF/jsp/common/layout.jsp` — ortak header/nav/footer (include edilecek)
- [ ] `webapp/WEB-INF/jsp/common/access-denied.jsp`
- [ ] `webapp/WEB-INF/jsp/common/error.jsp`
- [ ] `servlet/DashboardServlet.java` — rol bazlı yönlendirme (/ → role'e göre jsp)

---

## TAKIM ARKADAŞI yapacak

### Servlet'ler
- [ ] `servlet/TicketServlet.java`
  - GET /tickets → rol bazlı ticket listesi (showResolved, showClosed param)
  - GET /tickets/new → create-ticket.jsp
  - POST /tickets/new → TicketService.create()
  - GET /tickets/{id} → ticket-detail.jsp
  - POST /tickets/{id}/status → TicketService.updateStatus()
- [ ] `servlet/CommentServlet.java`
  - POST /tickets/comment → CommentService.addComment() (isInternal parametresi)
- [ ] `servlet/ChangePasswordServlet.java`
  - GET /change-password → change-password.jsp
  - POST /change-password → UserService.changePassword() + redirect dashboard
- [ ] `servlet/supervisor/AssignServlet.java`
  - POST /supervisor/assign → önce grup seç, sonra agent, TicketService.assignTicket() + assignTicketToGroup()
- [ ] `servlet/admin/UserServlet.java`
  - GET /admin/users → tüm kullanıcılar
  - GET /admin/users/new + POST → UserService.createUser()
  - POST /admin/users/{id}/delete → UserService.deleteById()
  - POST /admin/users/{id}/reset-password → UserService.resetPassword()
- [ ] `servlet/admin/CategoryServlet.java`
  - GET/POST /admin/categories → CategoryService (list, create, toggleActive, delete)
- [ ] `servlet/admin/DepartmentServlet.java`
  - GET/POST /admin/departments → DepartmentService (list, create, toggleActive, delete)
- [ ] `servlet/admin/GroupServlet.java`
  - GET /admin/groups → list
  - POST /admin/groups/new → GroupService.createGroup()
  - POST /admin/groups/{id}/users/add → GroupService.addUserToGroup()
  - POST /admin/groups/{id}/users/remove → GroupService.removeUserFromGroup()
  - POST /admin/groups/{id}/delete → GroupService.deleteGroup()
- [ ] `servlet/admin/SlaServlet.java`
  - GET /admin/sla → SlaService.getAllSlaSettings()
  - POST /admin/sla → SlaService.updateSla()

### JSP Sayfaları
- [ ] `jsp/auth/change-password.jsp` — zorunlu şifre değiştirme
- [ ] `jsp/customer/dashboard.jsp`
- [ ] `jsp/customer/my-tickets.jsp` — tablo + Show Resolved checkbox
- [ ] `jsp/customer/create-ticket.jsp` — başlık, açıklama, kategori dropdown, öncelik
- [ ] `jsp/customer/ticket-detail.jsp` — detay + yorum listesi + yorum formu (RESOLVED/CLOSED'da form kapalı)
- [ ] `jsp/agent/dashboard.jsp`
- [ ] `jsp/agent/ticket-list.jsp` — atanan ticket'lar + Show Resolved
- [ ] `jsp/agent/ticket-detail.jsp` — status değiştirme + internal note checkbox
- [ ] `jsp/supervisor/dashboard.jsp`
- [ ] `jsp/supervisor/ticket-list.jsp` — tüm ticket'lar + Show Resolved + Show Closed
- [ ] `jsp/supervisor/ticket-detail.jsp` — Assign butonu (grup + agent seçimi)
- [ ] `jsp/supervisor/reports.jsp` — status/öncelik/kategori bazlı istatistik
- [ ] `jsp/admin/dashboard.jsp` — 5 sekme linki (Users, Categories, Departments, Groups, SLA)
- [ ] `jsp/admin/users.jsp` — tablo + Reset Password / Delete butonları
- [ ] `jsp/admin/create-user.jsp` — form (departman dropdown DB'den)
- [ ] `jsp/admin/categories.jsp` — list + toggle + delete
- [ ] `jsp/admin/departments.jsp`
- [ ] `jsp/admin/groups.jsp` — sol grup listesi / sağ üyeler
- [ ] `jsp/admin/sla.jsp` — 4 satır tablo + Edit form

### Stil
- [ ] `static/css/style.css` — temel stil (navbar, tablo, form, badge'ler)
- [ ] `static/js/main.js` — varsa minimal JS (confirm dialog vs.)

---

## Önemli Kurallar (Koda Başlamadan Oku)

1. **Servlet → Service → DAO** zinciri. Controller yok, Servlet direkt Service'i çağırır.
2. **Session'a sadece `UserDTO` konur.** Entity asla session'a girmez.
3. **POST sonrası redirect** (PRG Pattern). `response.sendRedirect()` kullan, form çift gönderimi önle.
4. **XSS koruması.** JSP'de `<c:out value="${data}"/>` kullan, `${data}` ile direkt yazma.
5. **JSP'ler /WEB-INF altında.** URL ile doğrudan erişilemez, Servlet `request.getRequestDispatcher()` ile forward eder.
6. **Role kontrolü.** AuthFilter URL bazlı koruma yapar. Servlet içinde de `SessionUtil.hasRole()` ile kontrol et.
7. **RESOLVED/CLOSED ticket** → yorum formu JSP'de disabled gösterilecek (`${ticket.status == 'RESOLVED'}` ile kontrol).

---

## Servislerin Nereden Geldiği

Tüm servisler Spring tarafından inject edilir — import edilecek paket:
```
com.helpdesk.application.service.AuthService
com.helpdesk.application.service.TicketService
com.helpdesk.application.service.UserService
com.helpdesk.application.service.CommentService
com.helpdesk.application.service.CategoryService
com.helpdesk.application.service.DepartmentService
com.helpdesk.application.service.GroupService
com.helpdesk.application.service.SlaService
```

DTO'lar:
```
com.helpdesk.application.dto.TicketDTO
com.helpdesk.application.dto.UserDTO
com.helpdesk.application.dto.CommentDTO
```

Entity'ler (Servislerin döndürdükleri):
```
com.helpdesk.domain.entity.Category
com.helpdesk.domain.entity.Department
com.helpdesk.domain.entity.Group
com.helpdesk.domain.entity.SlaSettings
```

Enum'lar:
```
com.helpdesk.domain.enums.TicketStatus   (NEW, OPEN, IN_PROGRESS, PENDING, RESOLVED, CLOSED)
com.helpdesk.domain.enums.TicketPriority (CRITICAL, HIGH, MEDIUM, LOW)
```
