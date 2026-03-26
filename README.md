# IT Helpdesk Ticket Sistemi

> Universite odevi icin gelistirilen, N-Tier mimariye sahip destek talep yonetim sistemi.
> Spotify mantigi: ayni backend, farkli arayuzler. Hem desktop hem web'den erisim.
> Gunluk hayatta Jira Server Management ve Zammad sistemlerinden gorduğum yapilari uyarlamaya calistim.

---

## Ne Bu?

IT departmanina gelen destek taleplerini (ticket) yoneten bir sistem.
Kullanici sorununu bildiriyor, agent cozuyor, supervisor izliyor, admin her seyi kontrol ediyor.
Kisacasi: "internetim calismiyor" diye yaziyorsun, biri geliyor bakiyor.

Desktop uygulama gercek bir masaustu penceresi — Swing ile yazildi, tarayici gerektirmiyor.
Web uygulamasi tarayicidan erisilen versiyon (Servlet + JSP, sonra eklenecek).
Ikisi de ayni backend'i kullanıyor — ayni Service, ayni DAO, ayni veritabani.

---

## Teknoloji Stack

| Bilesen | Teknoloji | Versiyon | Neden Bu? |
|---------|-----------|----------|-----------|
| Dil | Java | 21 LTS | Kararli, her yerde calisiyor |
| Framework | Spring Boot | 3.2.5 | Dependency injection ve konfigurasyonu yonetiyor |
| Desktop UI | Swing | JDK ile gelir | Gercek masaustu penceresi, ekstra bagimlilik yok |
| Web UI | JSP + JSTL | Servlet 5.x | Tarayicidan erisilen arayuz (sonra eklenecek) |
| Veri Erisimi | JDBC / JdbcTemplate | Spring 6.x | SQL elle yazilir, ORM yok — ne yaptigini biliyorsun |
| Veritabani | MySQL | 8.0 | Klasik, saglam, herkes biliyor |
| Build | Maven | 3.9+ | Multi-module proje yonetimi |
| Container | Docker | 24.x | "Bende calisiyor" bahanesini ortadan kaldiriyor |

---

## Mimari Yapi (N-Tier)

```
┌──────────────────────────────────────────────┐
│            PRESENTATION TIER                 │
│  helpdesk-desktop       helpdesk-web         │
│  (Swing Penceresi)      (Servlet + JSP)      │
└──────────────────┬───────────────────────────┘
                   │
┌──────────────────▼───────────────────────────┐
│           BUSINESS LOGIC TIER                │
│           helpdesk-application               │
│        Service + DTO + Mapper                │
└──────────────────┬───────────────────────────┘
                   │
┌──────────────────▼───────────────────────────┐
│            DATA ACCESS TIER                  │
│           helpdesk-persistence               │
│         DAO Interface + Impl (JDBC)          │
└──────────────────┬───────────────────────────┘
                   │
┌──────────────────▼───────────────────────────┐
│              DOMAIN TIER                     │
│            helpdesk-domain                   │
│     Entity (POJO) + Enum + Exception         │
└──────────────────┬───────────────────────────┘
                   │
┌──────────────────▼───────────────────────────┐
│            DATABASE TIER                     │
│                MySQL                         │
└──────────────────────────────────────────────┘
```

Kural basit: **her katman sadece bir alt katmanla konusur.**
Controller DAO'ya el atamaz. Service SQL yazamaz. Yazarsa biz yazdirir miyiz? Yazdirmayiz.

Desktop'ta HTTP yok: Swing Controller dogrudan Service'i cagirir.
Web'de HTTP var: Tarayici → Servlet → Service seklinde gider. Ama Service ve DAO ortaktir.

---

## Modul Yapisi

Her katman kendi Maven modulunde yasar. Birbirinin isine burnunu sokamaz (compile-time'da engellenir).

```
IT-Helpdesk/
├── helpdesk-domain/          → Entity (POJO), Enum, Exception — hicbir framework bagimliligi yok
├── helpdesk-persistence/     → DAO Interface + Impl — JDBC ile SQL
├── helpdesk-application/     → Service + DTO + Mapper — is mantigi burada
├── helpdesk-desktop/         → Swing View + Controller — masaustu penceresi
└── helpdesk-web/             → Servlet + JSP — tarayici arayuzu (sonra eklenecek)
```

### Bagimlilik Zinciri

| Modul | Neye Bagimli | Teknoloji |
|-------|-------------|-----------|
| `helpdesk-domain` | Hicbir seye | Saf Java POJO |
| `helpdesk-persistence` | domain | JDBC / JdbcTemplate |
| `helpdesk-application` | domain + persistence | Spring (service katmani) |
| `helpdesk-desktop` | application | Swing + Spring Boot |
| `helpdesk-web` | application | Servlet + JSP + JSTL |

---

## Veritabani Tablolari

| Tablo | Aciklama | Not |
|-------|----------|-----|
| `users` | Sisteme giren herkes | Admin, agent, musteri... |
| `roles` | Roller | ADMIN, SUPERVISOR, AGENT, CUSTOMER |
| `permissions` | Yetkiler | Kim ne yapabilir (ticket.create vb.) |
| `groups_` | Ekipler | MySQL'de "groups" reserved keyword oldugu icin sona _ koyduk |
| `categories` | Ticket kategorileri | Ag Sorunu, Yazilim Hatasi, Donanim Arizasi... |
| `tickets` | Destek talepleri | Asil mesele bu |
| `comments` | Yorumlar | is_internal=true ise musteri gormez |
| `attachments` | Dosya ekleri | Ekran goruntusu, log dosyasi vb. |
| `user_roles` | N:M ara tablo | Kimin hangi rolu var |
| `role_permissions` | N:M ara tablo | Hangi rolun hangi yetkisi var |
| `group_users` | N:M ara tablo | Kim hangi grupta |

---

## Ticket Yasam Dongusu

```
NEW → OPEN → IN_PROGRESS → PENDING → RESOLVED → CLOSED
                               |
               Musteri bilgi gonderince
               geri IN_PROGRESS'e doner
```

| Durum | Ne Demek |
|-------|----------|
| `NEW` | Yeni acildi, kimse bakmadi henuz |
| `OPEN` | Acildi, agent bekleniyor |
| `IN_PROGRESS` | Agent uzerinde calisiyor |
| `PENDING` | Musteriden bilgi bekleniyor, eli kolu bagli bekliyor |
| `RESOLVED` | Cozuldu, musteri onaylarsa kapanacak |
| `CLOSED` | Bitti, herkes mutlu (umariz) |

---

## Roller (RBAC)

| Rol | Ne Yapar |
|-----|----------|
| `ADMIN` | Sistem tanrisi. Her seye erisir, kullanici ekler/siler |
| `SUPERVISOR` | Ekip lideri. Agent'lari yonetir, raporlari gorur |
| `AGENT` | Destek elemani. Ticket cozer, yorum yazar. Gercek isi yapan adam |
| `CUSTOMER` | Son kullanici. Kendi ticket'ini acar ve takip eder, baskasinkini goremez |

---

## Calistirma

### 1. Docker ile MySQL'i Ayaga Kaldir

```bash
docker-compose up -d
```

### 2. Veritabani Baglantisi (DataGrip)

| Alan | Deger |
|------|-------|
| Host | `localhost` |
| Port | `3306` |
| User | `helpdesk_user` |
| Password | `helpdesk_pass` |
| Database | `helpdesk_db` |

### 3. Projeyi Ac

IntelliJ IDEA ile `IT-Helpdesk` klasorunu ac. Maven otomatik taniyacak.

---

## Design Patterns

| Pattern | Nerede | Ne Yapar |
|---------|--------|----------|
| MVC | Swing Controller - Service - View | Her sey kendi isini yapar, birbiriyle karismiyor |
| DTO | Katmanlar arasi | Entity disariya cikmaz, yerine DTO gider. Guvenlik icin |
| DAO | Persistence katmani | JDBC'yi soyutlar, Service SQL bilmez |
| Factory | Ticket olusturma | Ture gore dogru nesneyi uretir |
| Observer | Durum degisikligi | Ticket durumu degisince ilgili yerlere haber gider |
| Strategy | Otomatik atama | Farkli atama kurallari arasinda secim yapar |
| Singleton | Spring Bean'ler | Her siniftan bir tane olusur, hafiza tasarrufu |
| Builder | DTO/Entity | Cok parametreli nesne olusturmayi okunabilir yapar |

---

## Proje Durumu

- [x] Mimari tasarim ve dokumantasyon
- [x] Docker + MySQL kurulumu
- [x] Multi-module Maven yapisi
- [x] Domain katmani (POJO Entity + Enum + Exception)
- [ ] Persistence katmani (DAO Interface + JDBC Impl)
- [ ] Application katmani (Service + DTO + Mapper)
- [ ] Desktop katmani (Swing View + Controller)
- [ ] Web katmani (Servlet + JSP, sonra)

---

*Universite Odevi Projesi — 2025*
