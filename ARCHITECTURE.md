# IT Helpdesk — Mimari Tasarım Dokümanı

**Proje:** IT Helpdesk Ticket Management System  
**Geliştiriciler:** Sina Toprak Güleç · Ahmet Furkan Poyraz  
**Yıl:** 2026

---

## 1. Genel Mimari Model

Aynı backend, iki farklı arayüz. Tüm iş mantığı ve veri erişimi tek bir yerde (servis katmanı) toplanmış; hem masaüstü hem web uygulaması bu katmanı paylaşır.

- **Desktop:** Spring Boot + Java Swing — tarayıcı gerektirmez, gerçek masaüstü penceresi
- **Web:** Spring Boot + Spring MVC + JSP/JSTL — tarayıcıdan erişilir, port 8080

---

## 2. N-Tier Katmanlı Mimari

```
┌────────────────────────────────────────────────────┐
│                  PRESENTATION TIER                 │
│   helpdesk-desktop            helpdesk-web         │
│   Swing View + Controller     Spring MVC + JSP     │
└──────────────────────┬─────────────────────────────┘
                       │  (Method call / HTTP)
┌──────────────────────▼─────────────────────────────┐
│               BUSINESS LOGIC TIER                  │
│               helpdesk-application                 │
│          Service Interface + Impl                  │
│          DTO (Data Transfer Object)                │
│          Mapper (Entity ↔ DTO)                     │
└──────────────────────┬─────────────────────────────┘
                       │
┌──────────────────────▼─────────────────────────────┐
│                DATA ACCESS TIER                    │
│               helpdesk-persistence                 │
│          DAO Interface + JdbcTemplate Impl         │
└──────────────────────┬─────────────────────────────┘
                       │
┌──────────────────────▼─────────────────────────────┐
│                  DOMAIN TIER                       │
│                helpdesk-domain                     │
│      Entity (POJO) · Enum · Exception              │
│      Sıfır framework bağımlılığı                   │
└──────────────────────┬─────────────────────────────┘
                       │
┌──────────────────────▼─────────────────────────────┐
│                   DATABASE                         │
│              MySQL 8.0 (Docker)                   │
└────────────────────────────────────────────────────┘
```

**Temel kural:** Her katman yalnızca bir alt katmanla konuşur. Controller → Service → DAO → DB. Bir Controller hiçbir zaman DAO'ya doğrudan erişemez.

---

## 3. Maven Multi-Module Yapısı

```
IT-Helpdesk/                       ← Parent POM (root)
├── helpdesk-domain/               ← 1. Katman: Entity, Enum, Exception
├── helpdesk-persistence/          ← 2. Katman: DAO Interface + JDBC Impl
├── helpdesk-application/          ← 3. Katman: Service + DTO + Mapper
├── helpdesk-desktop/              ← 4a. Sunum: Swing + Desktop Controller
├── helpdesk-web/                  ← 4b. Sunum: Spring MVC Controller + JSP
└── db/
    └── init.sql                   ← Schema + seed data
```

### Modül Bağımlılık Zinciri

```
helpdesk-desktop ──→ helpdesk-application ──→ helpdesk-persistence ──→ helpdesk-domain
helpdesk-web ──────→ helpdesk-application ──→ helpdesk-persistence ──→ helpdesk-domain
```

Maven bağımlılık yönü tek yönlüdür; döngüsel bağımlılık mümkün değildir.

---

## 4. Katman Detayları

### 4.1 helpdesk-domain

Framework bağımlılığı sıfır. Saf Java POJO'ları.

```
domain/
├── entity/          User, Ticket, Comment, Category, Group, Department,
│                    SlaSettings, PasswordResetRequest, Attachment
├── enums/           TicketStatus (NEW/OPEN/IN_PROGRESS/PENDING/RESOLVED/CLOSED)
│                    TicketPriority (LOW/MEDIUM/HIGH/CRITICAL)
│                    RoleType (ADMIN/SUPERVISOR/AGENT/CUSTOMER)
└── exception/       BusinessException, ResourceNotFoundException, UnauthorizedException
```

### 4.2 helpdesk-persistence

DAO (Data Access Object) pattern. Tüm SQL bu katmanda yaşar; üst katmanlar SQL bilmez.

```
persistence/
└── dao/
    ├── TicketDAO / TicketDAOImpl        (JdbcTemplate + JOIN queries)
    ├── UserDAO / UserDAOImpl
    ├── CommentDAO / CommentDAOImpl
    ├── CategoryDAO / CategoryDAOImpl
    ├── GroupDAO / GroupDAOImpl
    ├── DepartmentDAO / DepartmentDAOImpl
    ├── SlaDAO / SlaDAOImpl
    ├── RoleDAO / RoleDAOImpl
    └── PasswordResetRequestDAO / Impl
```

**Tasarım kararları:**
- ORM yok — JdbcTemplate ile elle yazılmış SQL
- JOIN sorgular için özel RowMapper sınıfları
- Interface üzerinden bağımlılık (DIP prensibi)

### 4.3 helpdesk-application

Tüm iş mantığı burada. Service katmanı hem desktop hem web tarafından kullanılır.

```
application/
├── service/
│   ├── AuthService / AuthServiceImpl      (BCrypt ile login doğrulama)
│   ├── TicketService / TicketServiceImpl  (CRUD, SLA hesaplama, atama)
│   ├── UserService / UserServiceImpl      (CRUD, şifre yönetimi, rol atama)
│   ├── GroupService / GroupServiceImpl    (Grup CRUD, üye yönetimi)
│   ├── SlaService / SlaServiceImpl        (SLA ayarları)
│   ├── CategoryService / CategoryServiceImpl
│   ├── DepartmentService / DepartmentServiceImpl
│   ├── CommentService / CommentServiceImpl
│   └── PasswordResetRequestService / Impl (Şifre sıfırlama talep akışı)
├── dto/                   TicketDTO, UserDTO, CommentDTO  (Entity'ler katmanlar arası taşınmaz)
└── mapper/                TicketMapper, UserMapper, CommentMapper
```

**SLA Hesaplama:**  
Ticket oluşturulurken `TicketServiceImpl.create()` → `SlaService.getByPriority()` → `sla_due_date = now + resolutionMinutes`

### 4.4 helpdesk-desktop

Swing tabanlı native masaüstü uygulaması. Spring Boot uygulama context'i üzerinden bean injection çalışır.

```
desktop/
├── DesktopApplication.java          (Spring Boot main — Swing thread'inde başlar)
├── config/AppConfig.java            (Spring bean tanımları)
├── security/SessionManager.java     (Aktif kullanıcıyı hafızada tutar)
├── controller/                      (AuthController, TicketController, UserController,
│                                     GroupController, CategoryController,
│                                     DepartmentController, SlaController)
└── view/
    ├── LoginFrame.java
    ├── DashboardFrame.java          (Admin — 5 yönetim sekmesi)
    ├── SupervisorDashboardFrame.java (Ticket listesi + SLA kolonu + grup→agent atama)
    ├── AgentDashboardFrame.java
    ├── CustomerDashboardFrame.java
    ├── ViewTicketDialog.java         (Ticket detay + yorum + SLA/group bilgisi)
    ├── CreateTicketDialog.java
    ├── ChangePasswordDialog.java
    └── panel/  UserManagementPanel, CategoryManagementPanel,
                DepartmentManagementPanel, GroupManagementPanel, SlaManagementPanel
```

**HTTP yok.** Swing Controller → Service method call → DAO → MySQL.

### 4.5 helpdesk-web

Spring MVC + JSP/JSTL tabanlı web uygulaması. Port 8080'de çalışır.

```
web/
├── WebApplication.java
├── config/
│   ├── AppConfig.java
│   └── WebConfig.java              (Filter kayıtları + AdminInterceptor)
├── filter/
│   ├── AuthFilter.java             (Oturum kontrolü — Spring Security kullanılmadı)
│   └── EncodingFilter.java
├── interceptor/
│   └── AdminInterceptor.java       (Her admin sayfasına pendingResetCount inject eder)
├── controller/
│   ├── AuthController.java         (/login, /logout, şifre reset forced-change)
│   ├── DashboardController.java    (/dashboard, /error, /access-denied)
│   ├── TicketController.java       (/tickets, /tickets/{id}, /tickets/new)
│   ├── CommentController.java      (/tickets/comment)
│   ├── AssignController.java       (/supervisor/assign, /supervisor/assign-group)
│   ├── ReportsController.java      (/supervisor/reports)
│   ├── ChangePasswordController.java
│   ├── ForgotPasswordController.java
│   └── admin/  UserController, CategoryController, DepartmentController,
│               GroupController, SlaController, ResetRequestController
└── webapp/WEB-INF/jsp/
    ├── auth/     login.jsp, forgot-password.jsp, change-password.jsp
    ├── common/   layout.jsp (navbar + custom modal), error.jsp, access-denied.jsp
    ├── admin/    users, categories, departments, groups, sla, reset-requests
    ├── supervisor/ ticket-list, ticket-detail, reports
    ├── agent/    ticket-list, ticket-detail
    └── customer/ dashboard, my-tickets, create-ticket, ticket-detail
```

**HTTP akışı:** Browser → AuthFilter → Spring MVC DispatcherServlet → Controller → Service → DAO → MySQL → JSP render.

---

## 5. Güvenlik Tasarımı

### Desktop
- `SessionManager` singleton: giriş yapan kullanıcıyı bellekte tutar
- Her kritik işlemde rol kontrolü yapılır
- CLOSED/RESOLVED ticket kilitleri servis katmanında uygulanır

### Web
- `AuthFilter`: tüm istekleri yakalar; oturum yoksa `/login`'e yönlendirir
- `passwordResetRequired` session flag: zorunlu şifre değişimi bypass'ı engeller
- Rol bazlı erişim her controller metodunda manuel olarak kontrol edilir
- Internal comment'lar customer'dan gizlenir (servis katmanında filtrelenir)
- Spring Security kasıtlı olarak kullanılmadı — filter zinciri elle yazıldı

---

## 6. Veritabanı Şeması

```
users ──< user_roles >── roles
  │
  ├──< tickets >── categories
  │      │
  │      ├──< comments (author_id → users)
  │      ├── assignee_id → users
  │      └── group_id → groups_
  │
  └──< group_users >── groups_

sla_settings          (priority bazlı SLA süreleri)
departments           (organizasyon birimleri)
password_reset_requests (şifre sıfırlama talep kuyruğu)
```

**Not:** MySQL'de `groups` reserved keyword olduğu için tablo adı `groups_` olarak tanımlandı.

---

## 7. Ticket Yaşam Döngüsü

```
     [Customer]                [Supervisor]              [Agent]
         │                          │                       │
    Ticket açar                     │                       │
         │──── NEW ────────────────►│                       │
                                    │  Grup + Agent atar    │
                                    │──── OPEN ────────────►│
                                                    Agent çalışmaya başlar
                                                       IN_PROGRESS
                                                    Müşteri bilgisi bekler
                                                        PENDING
                                                    Çözüm uygulandı
                                                       RESOLVED
                                    │◄── doğrula ──────────│
                            Supervisor kapatır
                                  CLOSED
```

**Kilitler:**
- Agent: RESOLVED veya CLOSED ticket'ı değiştiremez; CLOSED yapamaz
- Supervisor: CLOSED ticket'ı değiştiremez / agent atayamaz

---

## 8. Tasarım Desenleri

| Desen | Nerede Kullanıldı |
|-------|------------------|
| **MVC** | Tüm katmanlar — View, Controller, Model (Service) ayrımı |
| **DAO (Repository)** | Persistence katmanı — SQL'i iş mantığından ayırır |
| **DTO** | Katmanlar arası veri taşıma — Entity'ler dışarı çıkmaz |
| **Mapper** | Entity ↔ DTO dönüşümü — her iki yön için static metodlar |
| **PRG (Post-Redirect-Get)** | Web form submit'lerinde duplicate POST önleme |
| **Session** | Web auth — HttpSession üzerinden kullanıcı state tutma |
| **Filter Chain** | Web güvenlik — AuthFilter → EncodingFilter → Controller |
| **Interceptor** | Admin sayfalarına cross-cutting veri inject etme |
| **Singleton** | Spring Bean'ler — uygulama başına bir instance |

---

## 9. Teknoloji Seçim Gerekçeleri

| Karar | Neden |
|-------|-------|
| JdbcTemplate (ORM yok) | SQL sorguları tam kontrol altında; JOIN davranışı şeffaf |
| Spring Security yok | Filter zinciri elle yazılarak auth mekanizması tam anlaşılır kılındı |
| JSP + JSTL (React/Angular yok) | Sunucu taraflı render; Java ekosistemine uygun |
| Docker | "Bende çalışıyor" sorununu ortadan kaldırır; tek komutla DB ayağa kalkar |
| Multi-module Maven | Derleme zamanında katman ihlalleri tespit edilir |

---

*Sina Toprak Güleç · Ahmet Furkan Poyraz — 2026*
