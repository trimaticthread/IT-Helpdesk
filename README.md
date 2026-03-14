# IT Helpdesk Ticket Sistemi

> Universite odevi icin gelistirilen, N-Tier mimariye sahip destek talep yonetim sistemi.
> Spotify mantigi: ayni backend, farkli arayuzler. Hem desktop hem web'den erisim.
> > Günlük hayatta Jira Serverr Management ve Zammad sistemlerinden gördüğüm yapıları uyarlamaya çalıştım.

---

## Ne Bu?

IT departmanina gelen destek taleplerini (ticket) yoneten bir sistem.
Kullanici sorununu bildiriyor, agent cozuyor, supervisor izliyor, admin her seyi kontrol ediyor.
Kisacasi: "internetim calismiyor" diye yaziyorsun, biri geliyor bakiyor.

---

## Teknoloji Stack

| Bilesen | Teknoloji | Versiyon | Neden Bu? |
|---------|-----------|----------|-----------|
| Dil | Java | 21 LTS | Kararlı, her yerde calisiyor |
| Framework | Spring Boot | 3.2.5 | Sormaya gerek yok, standart |
| UI | Thymeleaf | 3.2.x | HTML uzerinden dinamik sayfa |
| Guvenlik | Spring Security + JWT | 6.2.x | Giris yapan adam baska adamin ticket'ini gormesin diye |
| Veri Erisimi | Spring Data JPA | 3.2.x | SQL yazmadan veritabani islemi |
| Veritabani | MySQL | 8.0 | Klasik, saglam, herkes biliyor |
| Build | Maven | 3.9+ | Dependency yonetimi |
| Container | Docker | 24.x | "Bende calisiyor" bahanesini ortadan kaldiriyor |

---

## Mimari Yapi (N-Tier)

```
┌─────────────────────────────────────────┐
│         PRESENTATION TIER               │
│  Desktop (Thymeleaf)   Web (sonra)      │
└─────────────────┬───────────────────────┘
                  │
┌─────────────────▼───────────────────────┐
│         BUSINESS LOGIC TIER             │
│     Service + DTO + Mapper              │
└─────────────────┬───────────────────────┘
                  │
┌─────────────────▼───────────────────────┐
│          DATA ACCESS TIER               │
│       DAO Interface + Impl              │
└─────────────────┬───────────────────────┘
                  │
┌─────────────────▼───────────────────────┐
│           DOMAIN TIER                   │
│      Entity + Enum + Exception          │
└─────────────────┬───────────────────────┘
                  │
┌─────────────────▼───────────────────────┐
│          DATABASE TIER                  │
│             MySQL                       │
└─────────────────────────────────────────┘
```

Kural basit: **her katman sadece bir alt katmanla konusur.** Controller DAO'ya el atamaaz, Service SQL yazamaz. Yazarsa biz yazdirir miyiz? Yazdirmayiz.

---

## Modul Yapisi

Proje multi-module Maven yapisiyla calisir. Her katman kendi modulunde yasader, birbirinin isine burnunu sokamaz (compile-time'da engellenir).

```
IT-Helpdesk/
├── helpdesk-domain/          → Entity, Enum, Exception (saf Java, framework bagimsiz)
├── helpdesk-persistence/     → DAO Interface + Impl (Spring Data JPA)
├── helpdesk-application/     → Service + DTO + Mapper (is mantigi)
├── helpdesk-desktop/         → Controller + Thymeleaf + Security (arayuz)
└── helpdesk-web/             → (henuz yok, sonra eklenecek)
```

### Bagimlillik Zinciri

| Modul | Neye Bagimli | Aciklama |
|-------|-------------|----------|
| `helpdesk-domain` | Hicbir seye | Saf Java, kendi basina ayakta durur |
| `helpdesk-persistence` | domain | Entity'leri alir, veritabanina yazar |
| `helpdesk-application` | domain + persistence | Is kurallarini uygular, DTO donusturur |
| `helpdesk-desktop` | application | Kullaniciya arayuz sunar, gerisi application'in isi |

---

## Veritabani Tablolari

| Tablo | Aciklama | Ne ise Yarar |
|-------|----------|-------------|
| `users` | Kullanicilar | Admin, agent, musteri... sisteme giren herkes |
| `roles` | Roller | ADMIN, SUPERVISOR, AGENT, CUSTOMER |
| `permissions` | Yetkiler | Kim ne yapabilir (ticket.create, user.delete vb.) |
| `groups_` | Gruplar | IT Destek, Ag Yonetimi gibi ekipler. MySQL'de "groups" reserved keyword oldugu icin sonuna _ koyduk |
| `categories` | Kategoriler | Ticket turleri: Ag Sorunu, Yazilim Hatasi, Donanim Arizasi |
| `tickets` | Ticketlar | Asil mesele bu. Kullanicinin actigı destek talebi |
| `comments` | Yorumlar | Ticket'a eklenen notlar. is_internal=true ise musteri gormez |
| `attachments` | Ekler | Ticket'a eklenen dosyalar (ekran goruntusu, log vb.) |
| `user_roles` | Kullanici-Rol | Kimin hangi rolu var (N:M ara tablo) |
| `role_permissions` | Rol-Yetki | Hangi rolun hangi yetkisi var (N:M ara tablo) |
| `group_users` | Grup-Kullanici | Kim hangi grupta (N:M ara tablo) |

---

## Ticket Yasam Dongusu

```
NEW → OPEN → IN_PROGRESS → PENDING → RESOLVED → CLOSED
 |                            |
 |     Musteri bilgi          |
 |     gonderince geri        |
 |     IN_PROGRESS'e doner    |
```

| Durum | Ne Demek |
|-------|----------|
| `NEW` | Ticket yeni acildi, kimse bakmadi henuz |
| `OPEN` | Acildi, agent bekleniyor |
| `IN_PROGRESS` | Agent uzerinde calisiyor |
| `PENDING` | Musteriden bilgi bekleniyor, eli kolu bagli bekliyor |
| `RESOLVED` | Cozuldu, musteri onaylarsa kapanacak |
| `CLOSED` | Bitti, herkes mutlu (umariz) |

---

## Roller (RBAC)

| Rol | Ne Yapar | Yetkisi |
|-----|----------|---------|
| `ADMIN` | Sistem tanrisi | Her seye erisir. Kullanici ekler, siler, sistem ayarlarini degistirir |
| `SUPERVISOR` | Ekip lideri | Agent'lari yonetir, raporlari gorur, ticket'lari izler |
| `AGENT` | Destek elemani | Ticket cozer, gunceller, yorum yazar. Gercek isi yapan adam |
| `CUSTOMER` | Son kullanici | Ticket acar, kendi ticket'ini takip eder. Baskasinin ticket'ini goremez |

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

| Pattern | Nerede Kullaniyoruz | Basitce Ne Yapar |
|---------|--------------------|--------------------|
| MVC | Controller - Service - View | Isleri ayirir, her sey kendi isini yapar |
| DTO | Katmanlar arasi veri tasima | Entity'yi disariya acmak yerine DTO gonderir. Guvenlik icin |
| DAO | Veri erisim soyutlamasi | SQL ile ugrasmak yerine interface uzerinden calisir |
| Factory | Ticket turlerine gore nesne olusturma | Turune gore dogru nesneyi uretir |
| Observer | Ticket durum degisikligi bildirimi | Durum degisince ilgili yerlere haber gider |
| Strategy | Otomatik atama algoritmalari | Farkli atama kurallari arasinda secim yapar |
| Singleton | Spring Bean'ler | Her siniftan tek bir ornek olusturur, hafiza tasarrufu |
| Builder | DTO/Entity olusturma | Cok parametreli nesne olusturmayi okunabilir yapar |

---

## Proje Durumu

- [x] Mimari tasarim ve dokumantasyon
- [x] Docker + MySQL kurulumu
- [x] Multi-module Maven yapisi
- [x] Domain katmani (Entity + Enum + Exception)
- [ ] Persistence katmani (DAO Interface + Impl)
- [ ] Application katmani (Service + DTO + Mapper)
- [ ] Desktop katmani (Controller + Thymeleaf + Security)
- [ ] Web katmani

---

*Universite Odevi Projesi — 2025*
