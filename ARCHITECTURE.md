# IT Helpdesk - Mimari Tasarim Dokumani

## 1. Proje Modeli

Spotify modeli: Ayni backend, farkli arayuzler. Kullanici uygulamayi hem desktop hem web uzerinden kullanabilir.

- **Desktop App:** Spring Boot + Thymeleaf (lokalde calisan, tarayicida acilan)
- **Web App:** Spring Boot + Thymeleaf (sunucuda calisan, tarayicidan erisilen) — sonra eklenecek

---

## 2. Buyuk Resim (N-Tier Architecture)

```
┌─────────────────────────────────────────┐
│         PRESENTATION TIER               │
│  helpdesk-desktop    helpdesk-web       │
│  (controller +       (controller +      │
│   Thymeleaf)          Thymeleaf)        │
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
│       ├── entity/                     # JPA entity siniflari
│       ├── enums/                      # Enum tipleri (Status, Role, Priority...)
│       └── exception/                  # Domain-specific exception siniflari
│
├── helpdesk-persistence/               # Veri erisim katmani (Spring Data JPA)
│   └── src/main/java/com/helpdesk/persistence/
│       ├── dao/                        # DAO interface'leri
│       └── dao/impl/                   # DAO implementasyonlari
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
│       ├── controller/                 # @Controller siniflari
│       ├── config/                     # Konfigurasyonlar
│       └── security/                   # Security ayarlari
│   └── src/main/resources/
│       └── templates/                  # Thymeleaf sablonlari
│
└── helpdesk-web/                       # Web uygulamasi (sonra eklenecek)
    └── src/main/java/com/helpdesk/web/
        ├── controller/
        ├── config/
        └── security/
    └── src/main/resources/
        └── templates/
```

### Modul Bagimliliklari (pom.xml)

| Modul | Bagimliliklar | Spring Bagimliligi |
|-------|---------------|-------------------|
| helpdesk-domain | YOK | Sadece JPA annotations |
| helpdesk-persistence | helpdesk-domain | Spring Data JPA |
| helpdesk-application | helpdesk-domain, helpdesk-persistence | - |
| helpdesk-desktop | helpdesk-application | Spring Web, Thymeleaf, Security |
| helpdesk-web | helpdesk-application | Spring Web, Thymeleaf, Security |

---

## 4. N-Tier Katman Detaylari (Desktop)

```
┌─────────────────────────────────┐
│  1. PRESENTATION LAYER          │  Thymeleaf (.html)
│     Kullanicinin gordugu arayuz │
└──────────────┬──────────────────┘
               │ HTTP Request/Response
┌──────────────▼──────────────────┐
│  2. CONTROLLER LAYER            │  @Controller siniflari
│     Istek yonlendirme           │
└──────────────┬──────────────────┘
               │ DTO
┌──────────────▼──────────────────┐
│  3. SERVICE LAYER               │  Interface + Impl
│     Is mantigi, validasyon      │
└──────────────┬──────────────────┘
               │ Entity
┌──────────────▼──────────────────┐
│  4. DAO LAYER                   │  Interface + Impl
│     Veri erisim soyutlamasi     │
└──────────────┬──────────────────┘
               │ JDBC/JPA
┌──────────────▼──────────────────┐
│  5. DATABASE LAYER              │  MySQL
│     Veri depolama               │
└─────────────────────────────────┘
```

### Katmanlar Arasi Iletisim Kurallari

- Controller → ASLA dogrudan DAO'ya erismez
- Controller → ASLA dogrudan Entity dondurmez (DTO kullanir)
- Service → ASLA dogrudan SQL yazmaz (DAO kullanir)
- DAO → ASLA is mantigi icermez (sadece CRUD)
- Thymeleaf → ASLA Service/DAO bilmez (sadece DTO gorur)

---

## 5. Paket Yapisi (Detayli)

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
│   └── Group.java
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

### helpdesk-persistence (Spring Data JPA)
```
com.helpdesk.persistence/
│
├── dao/
│   ├── TicketDAO.java              (interface)
│   ├── UserDAO.java                (interface)
│   ├── CommentDAO.java             (interface)
│   ├── AttachmentDAO.java          (interface)
│   └── RoleDAO.java                (interface)
│
└── dao/impl/
    ├── TicketDAOImpl.java
    ├── UserDAOImpl.java
    ├── CommentDAOImpl.java
    ├── AttachmentDAOImpl.java
    └── RoleDAOImpl.java
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

### helpdesk-desktop (presentation)
```
com.helpdesk.desktop/
│
├── controller/
│   ├── TicketController.java
│   ├── UserController.java
│   ├── AuthController.java
│   └── DashboardController.java
│
├── config/
│   ├── SecurityConfig.java
│   └── AppConfig.java
│
├── security/
│   └── ...
│
└── DesktopApplication.java         (main class)
```

---

## 6. Veri Akisi Ornegi (Ticket Olusturma)

```
Kullanici formu doldurur (Thymeleaf)
        ↓
Controller: CreateTicketRequest (DTO) alir
        ↓
Service: DTO → Entity donusturur, is kurallarini uygular
        ↓
DAO: Entity'yi veritabanina kaydeder
        ↓
Service: Entity → DTO donusturur
        ↓
Controller: DTO'yu Thymeleaf'e gonderir
        ↓
Kullanici sonucu gorur
```

---

## 7. Teknoloji Stack (Desktop)

| Bilesen | Teknoloji | Versiyon |
|---------|-----------|----------|
| Java | Eclipse Temurin | 21 LTS |
| Framework | Spring Boot | 3.2.5 |
| UI | Thymeleaf | 3.2.x |
| Guvenlik | Spring Security + JWT | 6.2.x |
| Veri Erisimi | Spring Data JPA | 3.2.x |
| Veritabani | MySQL | 8.0+ |
| Build Tool | Maven | 3.9+ |
| Container | Docker + Docker Compose | 24.x |

---

## 8. Design Patterns

| Pattern | Kullanim Alani |
|---------|---------------|
| MVC | Controller-Service-View akisi |
| DTO | Katmanlar arasi veri tasima |
| DAO | Veri erisim soyutlamasi |
| Factory | Ticket turlerine gore nesne olusturma |
| Observer | Ticket durum degisikligi bildirimleri |
| Strategy | Otomatik atama algoritmalari |
| Singleton | Spring Bean'ler (default scope) |
| Builder | DTO/Entity olusturma |
| Repository | DAO implementasyonu icinde |

---

## 9. RBAC (Rol Tabanli Erisim Kontrolu)

| Rol | Aciklama |
|-----|----------|
| ADMIN | Sistem uzerinde tam yetki |
| SUPERVISOR | Ekip yonetimi, raporlama |
| AGENT | Ticket cozme, guncelleme |
| CUSTOMER | Kendi ticketlarini olusturma/takip |

---

## 10. Gelistirme Ortami ve Araclar

| Arac | Kullanim |
|------|----------|
| **Docker** | MySQL ve tum servisler container'da calisir |
| **DataGrip** | Docker'daki MySQL'e baglanip veritabani yonetimi |
| **IntelliJ IDEA** | Proje implementasyonu |
| **Claude (Agent)** | Asistan — ne yapilacagini ve nasil yazilacagini yonlendirir, kod yazmaz |

### Workflow

1. Docker ile MySQL container ayaga kalkar
2. DataGrip ile Docker MySQL'e baglanilir, veritabani/tablolar olusturulur
3. IntelliJ'de Spring Boot projesi olusturulur ve implemente edilir
4. Claude adim adim yonlendirir, kullanici kodu yazar

---

## 11. Gelistirme Onceligi

1. **Simdi:** helpdesk-domain + helpdesk-persistence + helpdesk-application + helpdesk-desktop
2. **Sonra:** helpdesk-web (ayni application'i kullanarak)
