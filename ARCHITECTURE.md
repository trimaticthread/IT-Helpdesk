# IT Helpdesk - Mimari Tasarim Dokumani

## 1. Proje Modeli

Spotify modeli: Ayni backend, farkli arayuzler. Kullanici uygulamayi hem desktop hem web uzerinden kullanabilir.

- **Desktop App:** Spring Boot + Swing (gercek masaustu uygulamasi, kendi penceresi var)
- **Web App:** Spring Boot + JSP + JSTL (sunucuda calisan, tarayicidan erisilen) — sonra eklenecek

---

## 2. Buyuk Resim (N-Tier Architecture)

```
┌─────────────────────────────────────────┐
│         PRESENTATION TIER               │
│  helpdesk-desktop    helpdesk-web       │
│  (Swing)             (Servlet +         │
│                       JSP/JSTL)        │
└─────────────────┬───────────────────────┘
                  │
┌─────────────────▼───────────────────────┐
│           BUSINESS LOGIC TIER           │
│          helpdesk-application/          │
│     (service, dto, mapper)              │
└─────────────────┬───────────────────────┘
                  │
┌─────────────────▼───────────────────────┐
│            DATA ACCESS TIER             │
│          helpdesk-persistence/          │
│        (dao interface + impl)           │
└─────────────────┬───────────────────────┘
                  │
┌─────────────────▼───────────────────────┐
│             DOMAIN TIER                 │
│           helpdesk-domain/              │
│     (entity, enum, exception)           │
└─────────────────┬───────────────────────┘
                  │
┌─────────────────▼───────────────────────┐
│            DATABASE TIER                │
│               MySQL                     │
└─────────────────────────────────────────┘
```

### Modul Bagimlilik Zinciri

```
helpdesk-desktop ──→ helpdesk-application ──→ helpdesk-persistence ──→ helpdesk-domain
helpdesk-web ──────→ helpdesk-application ──→ helpdesk-persistence ──→ helpdesk-domain
```

- Her modul SADECE bir alt katmani dependency olarak alir
- desktop/web ayni application'i kullanir (Spotify modeli)

---

## 3. Multi-Module Maven Yapisi

```
helpdesk-system/                        (parent POM)
│
├── helpdesk-domain/                    # Domain katmani (saf Java)
│   └── src/main/java/com/helpdesk/domain/
│       ├── entity/                     # Entity siniflari (JPA annotations yok, saf POJO)
│       ├── enums/                      # Enum tipleri (Status, Role, Priority...)
│       └── exception/                  # Domain-specific exception siniflari
│
├── helpdesk-persistence/               # Veri erisim katmani (JDBC)
│   └── src/main/java/com/helpdesk/persistence/
│       ├── dao/                        # DAO interface'leri
│       └── dao/impl/                   # DAO implementasyonlari (JDBC/JdbcTemplate)
│
├── helpdesk-application/               # Is mantigi katmani
│   └── src/main/java/com/helpdesk/application/
│       ├── service/                    # Service interface'leri
│       ├── service/impl/              # Service implementasyonlari
│       ├── dto/                        # Data Transfer Object'ler
│       └── mapper/                     # Entity <-> DTO donusturuculer
│
├── helpdesk-desktop/                   # Desktop uygulamasi (oncelikli)
│   └── src/main/java/com/helpdesk/desktop/
│       ├── controller/                 # Swing Controller siniflari (UI olaylarini yakalar)
│       ├── view/                       # Swing UI siniflari (JFrame, JPanel vb.)
│       ├── config/                     # Spring konfigurasyonu
│       └── security/                   # Giris/oturum yonetimi
│
└── helpdesk-web/                       # Web uygulamasi (sonra eklenecek)
    └── src/main/java/com/helpdesk/web/
        ├── servlet/                    # HttpServlet siniflari
        ├── filter/                     # Guvenlik/session filtreleri
        └── config/                     # Web konfigurasyonu
    └── src/main/resources/
        └── webapp/
            ├── WEB-INF/jsp/            # JSP dosyalari
            └── WEB-INF/web.xml         # Servlet konfigurasyonu
```

### Modul Bagimliliklari (pom.xml)

| Modul | Bagimliliklar | Teknoloji |
|-------|---------------|-----------|
| helpdesk-domain | YOK | Saf Java POJO |
| helpdesk-persistence | helpdesk-domain | JDBC / JdbcTemplate |
| helpdesk-application | helpdesk-domain, helpdesk-persistence | - |
| helpdesk-desktop | helpdesk-application | Swing, Spring Boot |
| helpdesk-web | helpdesk-application | Servlet, JSP, JSTL |

---

## 4. N-Tier Katman Detaylari (Desktop)

```
┌─────────────────────────────────┐
│  1. PRESENTATION LAYER          │  Swing (JFrame, JPanel, JTable...)
│     Kullanicinin gordugu arayuz │
└──────────────┬──────────────────┘
               │ Kullanici Aksiyonu (buton tiklama vb.)
┌──────────────▼──────────────────┐
│  2. CONTROLLER LAYER            │  Swing Controller siniflari
│     Arayuz olaylarini yakalar   │
└──────────────┬──────────────────┘
               │ DTO
┌──────────────▼──────────────────┐
│  3. SERVICE LAYER               │  Interface + Impl
│     Is mantigi, validasyon      │
└──────────────┬──────────────────┘
               │ Entity
┌──────────────▼──────────────────┐
│  4. DAO LAYER                   │  Interface + Impl (JDBC)
│     Veri erisim soyutlamasi     │
└──────────────┬──────────────────┘
               │ JDBC / JdbcTemplate
┌──────────────▼──────────────────┐
│  5. DATABASE LAYER              │  MySQL
│     Veri depolama               │
└─────────────────────────────────┘
```

### Katmanlar Arasi Iletisim Kurallari

- Swing Controller → ASLA dogrudan DAO'ya erismez
- Swing Controller → ASLA dogrudan Entity dondurmez (DTO kullanir)
- Service → ASLA dogrudan SQL yazmaz (DAO kullanir)
- DAO → ASLA is mantigi icermez (sadece CRUD, SQL burada)
- Swing View → ASLA Service/DAO bilmez (sadece Controller ile konusur)

---

## 5. N-Tier Katman Detaylari (Web)

```
┌─────────────────────────────────┐
│  1. PRESENTATION LAYER          │  JSP + JSTL
│     Kullanicinin gordugu sayfa  │
└──────────────┬──────────────────┘
               │ HTTP Request/Response
┌──────────────▼──────────────────┐
│  2. SERVLET LAYER               │  HttpServlet siniflari
│     HTTP isteklerini yakalar    │
└──────────────┬──────────────────┘
               │ DTO
┌──────────────▼──────────────────┐
│  3. SERVICE LAYER               │  Ayni service (Desktop ile ortak!)
│     Is mantigi, validasyon      │
└──────────────┬──────────────────┘
               │ Entity
┌──────────────▼──────────────────┐
│  4. DAO LAYER                   │  Ayni DAO (Desktop ile ortak!)
│     Veri erisim soyutlamasi     │
└──────────────┬──────────────────┘
               │ JDBC / JdbcTemplate
┌──────────────▼──────────────────┐
│  5. DATABASE LAYER              │  Ayni MySQL (Desktop ile ortak!)
│     Veri depolama               │
└─────────────────────────────────┘
```

---

## 6. Paket Yapisi (Detayli)

### helpdesk-domain (saf Java — framework bagimsiz)
```
com.helpdesk.domain/
│
├── entity/
│   ├── Ticket.java
│   ├── User.java
│   ├── Comment.java
│   ├── Attachment.java
│   ├── Role.java
│   ├── Permission.java
│   ├── Group.java
│   └── Category.java
│
├── enums/
│   ├── TicketStatus.java          (NEW, OPEN, IN_PROGRESS, PENDING, RESOLVED, CLOSED)
│   ├── TicketPriority.java        (CRITICAL, HIGH, MEDIUM, LOW)
│   └── RoleType.java              (ADMIN, SUPERVISOR, AGENT, CUSTOMER)
│
└── exception/
    ├── ResourceNotFoundException.java
    ├── UnauthorizedException.java
    └── BusinessException.java
```

### helpdesk-persistence (JDBC)
```
com.helpdesk.persistence/
│
├── dao/
│   ├── TicketDAO.java              (interface)
│   ├── UserDAO.java                (interface)
│   ├── CommentDAO.java             (interface)
│   ├── AttachmentDAO.java          (interface)
│   ├── RoleDAO.java                (interface)
│   └── CategoryDAO.java            (interface)
│
└── dao/impl/
    ├── TicketDAOImpl.java          (JDBC/JdbcTemplate ile SQL)
    ├── UserDAOImpl.java
    ├── CommentDAOImpl.java
    ├── AttachmentDAOImpl.java
    ├── RoleDAOImpl.java
    └── CategoryDAOImpl.java
```

### helpdesk-application (is mantigi)
```
com.helpdesk.application/
│
├── service/
│   ├── TicketService.java          (interface)
│   ├── UserService.java            (interface)
│   ├── CommentService.java         (interface)
│   └── AuthService.java            (interface)
│
├── service/impl/
│   ├── TicketServiceImpl.java
│   ├── UserServiceImpl.java
│   ├── CommentServiceImpl.java
│   └── AuthServiceImpl.java
│
├── dto/
│   ├── TicketDTO.java
│   ├── UserDTO.java
│   ├── CreateTicketRequest.java
│   ├── UpdateTicketRequest.java
│   └── LoginRequest.java
│
└── mapper/
    ├── TicketMapper.java
    └── UserMapper.java
```

### helpdesk-desktop (Swing masaustu uygulamasi)
```
com.helpdesk.desktop/
│
├── view/
│   ├── LoginFrame.java
│   ├── DashboardFrame.java
│   ├── TicketListPanel.java
│   └── TicketDetailPanel.java
│
├── controller/
│   ├── TicketController.java
│   ├── UserController.java
│   ├── AuthController.java
│   └── DashboardController.java
│
├── config/
│   └── AppConfig.java
│
├── security/
│   └── SessionManager.java
│
└── DesktopApplication.java           (main class)
```

### helpdesk-web (Servlet + JSP)
```
com.helpdesk.web/
│
├── servlet/
│   ├── TicketServlet.java
│   ├── UserServlet.java
│   ├── AuthServlet.java
│   └── DashboardServlet.java
│
├── filter/
│   ├── AuthFilter.java               (session kontrolu)
│   └── EncodingFilter.java
│
└── config/
    └── AppConfig.java

webapp/
└── WEB-INF/
    ├── web.xml
    └── jsp/
        ├── login.jsp
        ├── dashboard.jsp
        ├── ticket-list.jsp
        └── ticket-detail.jsp
```

---

## 7. Veri Akisi Ornegi

### Desktop (Ticket Olusturma)
```
Kullanici Swing formunu doldurur
        ↓
Swing Controller: Formdaki verileri CreateTicketRequest (DTO) olarak toplar
        ↓
Service: DTO → Entity donusturur, is kurallarini uygular
        ↓
DAO: JDBC ile Entity'yi veritabanina kaydeder (SQL burada)
        ↓
Service: Entity → DTO donusturur
        ↓
Swing Controller: DTO'yu arayuzde gosterir (JTable, JLabel vb.)
        ↓
Kullanici sonucu pencerede gorur
```

### Web (Ticket Olusturma)
```
Kullanici JSP formunu doldurur (HTTP POST)
        ↓
Servlet: HttpServletRequest'ten parametreleri alir, DTO olusturur
        ↓
Service: Ayni service! DTO → Entity, is kurallari
        ↓
DAO: Ayni DAO! JDBC ile veritabanina kaydeder
        ↓
Service: Entity → DTO
        ↓
Servlet: DTO'yu request attribute olarak set eder, JSP'ye yonlendirir
        ↓
JSP + JSTL: DTO'yu sayfada gosterir
```

NOT: Desktop'ta HTTP yok. Swing Controller, Service'i dogrudan Java method call ile cagirir.
Web'de HTTP Request/Response akisi var. Ama Service ve DAO katmanlari her ikisinde ORTAKTIR.

---

## 8. Teknoloji Stack

| Bilesen | Teknoloji | Versiyon |
|---------|-----------|----------|
| Java | Eclipse Temurin | 21 LTS |
| Framework | Spring Boot | 3.2.5 |
| UI (Desktop) | Swing | JDK ile gelir |
| UI (Web) | JSP + JSTL | Servlet 5.x |
| Veri Erisimi | JDBC / JdbcTemplate | Spring 6.x |
| Veritabani | MySQL | 8.0+ |
| Build Tool | Maven | 3.9+ |
| Container | Docker + Docker Compose | 24.x |

---

## 9. Design Patterns

| Pattern | Kullanim Alani |
|---------|---------------|
| MVC | Controller/Servlet - Service - View akisi |
| DTO | Katmanlar arasi veri tasima |
| DAO | Veri erisim soyutlamasi (JDBC implementasyonu) |
| Factory | Ticket turlerine gore nesne olusturma |
| Observer | Ticket durum degisikligi bildirimleri |
| Strategy | Otomatik atama algoritmalari |
| Singleton | Spring Bean'ler (default scope) |
| Builder | DTO/Entity olusturma |

---

## 10. RBAC (Rol Tabanli Erisim Kontrolu)

| Rol | Aciklama |
|-----|----------|
| ADMIN | Sistem uzerinde tam yetki |
| SUPERVISOR | Ekip yonetimi, raporlama |
| AGENT | Ticket cozme, guncelleme |
| CUSTOMER | Kendi ticketlarini olusturma/takip |

---

## 11. Gelistirme Ortami ve Araclar

| Arac | Kullanim |
|------|----------|
| **Docker** | MySQL container'da calisir |
| **DataGrip** | Docker'daki MySQL'e baglanip veritabani yonetimi |
| **IntelliJ IDEA** | Proje implementasyonu |
| **Claude (Agent)** | Asistan — yonlendirme, kod yazmaz |

### Workflow

1. Docker ile MySQL container ayaga kalkar
2. DataGrip ile Docker MySQL'e baglanilir, veritabani/tablolar olusturulur
3. IntelliJ'de Maven multi-module proje olusturulur
4. Once domain → persistence → application → desktop sirasinda implemente edilir
5. Desktop tamamlaninca web modulu eklenir (ayni application kullanilir)

---

## 12. Gelistirme Onceligi

1. **Simdi (Vize):** helpdesk-domain → helpdesk-persistence → helpdesk-application → helpdesk-desktop
2. **Sonra (Final):** helpdesk-web (ayni application'i kullanarak, sadece Servlet + JSP eklenir)