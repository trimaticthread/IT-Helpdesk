![](media/image1.png){width="8.26771653543307in" height="11.692913385826772in"}

**IT HELPDESK**

**TICKET SISTEMI**

Kapsamli Proje Raporu ve Java Taslagi

Universite Odevi Projesi

2025

**ICINDEKILER**

(Ipucu: Sayfa numaralarini guncellemek icin icerik tablosuna sag tiklayip \"Alani Guncelle\" secenegini secin)

[1. Giris ve IT Helpdesk Kavrami](#giris-ve-it-helpdesk-kavrami)

[1.1 IT Helpdesk\'in Temel Fonksiyonlari](#it-helpdeskin-temel-fonksiyonlari)

[1.2 Zammad ve Acik Kaynak Helpdesk Sistemleri](#zammad-ve-acik-kaynak-helpdesk-sistemleri)

[2. Is Akislari (Workflows)](#is-akislari-workflows)

[2.1 Ticket Olusturma Akisi](#ticket-olusturma-akisi)

[2.2 Atama ve Yonlendirme Akisi](#atama-ve-yonlendirme-akisi)

[Otomatik Atama Kurallari](#otomatik-atama-kurallari)

[Yukseltme (Escalation) Mekanizmalari](#yukseltme-escalation-mekanizmalari)

[2.3 Cozum ve Kapatma Akisi](#cozum-ve-kapatma-akisi)

[3. Yetki Prensipleri ve RBAC](#yetki-prensipleri-ve-rbac)

[3.1 Rol Tanimlari](#rol-tanimlari)

[3.2 Yetki Matrisi](#yetki-matrisi)

[3.3 Grup Bazli Yetkilendirme](#grup-bazli-yetkilendirme)

[4. Ticket Yasam Dongusu](#ticket-yasam-dongusu)

[4.1 Ticket Durumlari](#ticket-durumlari)

[4.2 Onceliklendirme ve SLA Yonetimi](#onceliklendirme-ve-sla-yonetimi)

[5. Veritabani Tasarimi ve ER Diyagramlari](#veritabani-tasarimi-ve-er-diyagramlari)

[5.1 Temel Varliklar (Entities)](#temel-varliklar-entities)

[Users (Kullanicilar)](#users-kullanicilar)

[Tickets](#tickets)

[Comments (Yorumlar)](#comments-yorumlar)

[Attachments (Ekler)](#attachments-ekler)

[Roles (Roller)](#roles-roller)

[Permissions (Yetkiler)](#permissions-yetkiler)

[Groups (Gruplar)](#groups-gruplar)

[5.2 Iliskiler (Relationships)](#iliskiler-relationships)

[5.3 ER Diyagram Yapisi](#er-diyagram-yapisi)

[6. Java Proje Taslagi](#java-proje-taslagi)

[6.1 Teknoloji Stack\'i](#teknoloji-stacki)

[6.2 Proje Yapisi](#proje-yapisi)

[6.2 N-Tier Mimari Yapisi](#n-tier-mimari-yapisi)

[Mimari Katmanlar](#mimari-katmanlar)

[Proje Dizin Yapisi](#proje-dizin-yapisi)

[6.3 Temel Siniflar ve Paketler](#temel-siniflar-ve-paketler)

[Entity Siniflari](#entity-siniflari)

[Repository Katmani](#repository-katmani)

[Service Katmani](#service-katmani)

[Controller Katmani](#controller-katmani)

[6.4 Web URL Yapisi ve Controller Mapping](#web-url-yapisi-ve-controller-mapping)

[6.5 Guvenlik Yapilandirmasi](#guvenlik-yapilandirmasi)

[6.6 Docker Containerizasyon](#docker-containerizasyon)

[Docker Compose Yapisi](#docker-compose-yapisi)

[Dockerfile - Web Uygulamasi](#dockerfile---web-uygulamasi)

[Dockerfile - Desktop Uygulamasi](#dockerfile---desktop-uygulamasi)

[Docker Kullanim Komutlari](#docker-kullanim-komutlari)

[7. Sonuc](#sonuc)

[Temel Cikarimlar](#temel-cikarimlar)

[Gelecek Gelisimler](#gelecek-gelisimler)

# 1. Giris ve IT Helpdesk Kavrami

IT Helpdesk, bir organizasyonun bilgi teknolojileri altyapisini destekleyen, kullanici sorunlarini cozen ve teknik hizmetler sunan merkezi bir destek sistemidir. Modern isletmelerde IT Helpdesk, sadece teknik sorunlari cozmekle kalmayip, ayni zamanda is surekliligi, kullanici memnuniyeti ve operasyonel verimlilik icin kritik bir rol oynamaktadir.

## 1.1 IT Helpdesk\'in Temel Fonksiyonlari

-   Incident Management: Teknik arızalarin ve sorunlarin kaydedilmesi, siniflandirilmasi ve cozulmesi

-   Service Request Management: Kullanici taleplerinin (sifre sifirlama, yazilim kurulumu vb.) karsilanmasi

-   Problem Management: Tekrarlayan sorunlarin kok neden analizi ve kalici cozumlerin uretilmesi

-   Change Management: IT sistemlerindeki degisikliklerin kontrollu sekilde yonetilmesi

-   Knowledge Management: Cozumlerin ve prosedurlerin dokumante edilmesi

## 1.2 Zammad ve Acik Kaynak Helpdesk Sistemleri

Zammad, modern ve acik kaynakli bir helpdesk ve ticket yonetim sistemidir. Web tabanli arayuzu, coklu kanal destegi (e-posta, chat, telefon, sosyal medya) ve guclu otomasyon ozellikleriyle dikkat cekmektedir. Zammad\'in temel ozellikleri:

-   Coklu Kanal Destegi: E-posta, web formu, chat, telefon entegrasyonu

-   Otomasyon: Tetikleyiciler, makrolar ve zamanlayicilar ile is akisi otomasyonu

-   Raporlama: Kapsamli performans raporlari ve analizler

-   Esneklik: Ozellestirilebilir formlar, alanlar ve is akislari

-   Guvenlik: Rol tabanli erisim kontrolu ve audit loglari

# 2. Is Akislari (Workflows)

IT Helpdesk sistemlerinde is akislari, ticket\'larin olusturulmasindan kapatilmasina kadar gecen surecteki adimlari ve bu adimlar arasindaki gecisleri tanimlar. Etkin is akislari, hizli yanit sureleri, tutarli hizmet kalitesi ve yuksek kullanici memnuniyeti saglar.

## 2.1 Ticket Olusturma Akisi

Ticket olusturma sureci, kullanicinin bir sorun bildirmesiyle baslar ve ticket\'in sistemde kayitlanmasiyla devam eder. Bu surec asagidaki adimlari icerir:

1.  **Basvuru Kanali**: Kullanici ticket\'i web portal, e-posta, chat veya telefon ile olusturur

2.  **Bilgi Toplama**: Sistem otomatik olarak kullanici bilgileri, tarih/saat ve basvuru kanalini kaydeder

3.  **Kategorizasyon**: Ticket konusu, tipi (incident/request) ve etkilenen servis belirlenir

4.  **Onceliklendirme**: Etki (Impact) ve Aciliyet (Urgency) degerlerine gore oncelik atanir

5.  **Kayit Olusturma**: Ticket sisteme kaydedilir ve benzersiz ticket numarasi atanir

## 2.2 Atama ve Yonlendirme Akisi

Ticket\'in dogru personele hizli bir sekilde atanmasi, cozum suresini dogrudan etkiler. Otomatik atama kurallari ve yonlendirme mekanizmalari su sekillerde calisir:

### Otomatik Atama Kurallari

-   Kategori Bazli: Yazilim sorunlari yazilim ekibine, donanim sorunlari donanim ekibine

-   Yuk Dengeleme: Mevcut ticket yuku en dusuk personele otomatik atama

-   Uzmanlik Eşleştirmesi: Konu uzmanligi gerektiren ticket\'lar ilgili uzmana

-   Round-Robin: Sirayla dagitim yaparak adil yuk paylasimi

### Yukseltme (Escalation) Mekanizmalari

-   Fonksiyonel Yukseltme: L1 -\> L2 -\> L3 seviyeleri arasinda teknik uzmanlik transferi

-   Hiyerarsik Yukseltme: Surec yoneticisi veya ust yonetime bildirim

-   SLA Bazli: Yanit/Cozum suresi asildiginda otomatik yukseltme

## 2.3 Cozum ve Kapatma Akisi

Ticket cozum sureci, atanmis personelin sorunu cozmesi ve ticket\'i kapatmasiyla sonuclanir. Bu surec asagidaki durumlari icerir:

  --------------------------------------------------------------------------
  **Mevcut Durum**       **Eylem**                       **Sonraki Durum**
  ---------------------- ------------------------------- -------------------
  NEW                    Atama yapildi                   OPEN

  OPEN                   Cozum basladi                   IN_PROGRESS

  IN_PROGRESS            Bilgi bekleme                   PENDING

  PENDING                Bilgi geldi                     IN_PROGRESS

  IN_PROGRESS            Cozum uygulandi                 RESOLVED

  RESOLVED               Onay (7 gun)                    CLOSED

  RESOLVED               Tekrar acildi                   IN_PROGRESS
  --------------------------------------------------------------------------

Tablo 1: Ticket Durum Gecisleri

# 3. Yetki Prensipleri ve RBAC

Rol Tabanli Erisim Kontrolu (Role-Based Access Control - RBAC), IT Helpdesk sistemlerinde guvenlik ve veri mahremiyetini saglamak icin kullanilan temel bir mekanizmadir. RBAC, kullanicilara roller atanmasi ve bu rollere belirli yetkilerin tanimlanmasi prensibine dayanir.

## 3.1 Rol Tanimlari

Tipik bir IT Helpdesk sisteminde asagidaki temel roller bulunur:

  -----------------------------------------------------------------------
  **Rol**           **Aciklama**
  ----------------- -----------------------------------------------------
  ADMIN             Sistem uzerinde tam yetki, kullanici/grup yonetimi

  SUPERVISOR        Ekibi yonetir, raporlari gorur, ticket atayabilir

  AGENT             Ticket cozer, yorum ekler, durum gunceller

  CUSTOMER          Kendi ticket\'larini olusturur ve takip eder
  -----------------------------------------------------------------------

Tablo 2: Temel Roller ve Aciklamalari

## 3.2 Yetki Matrisi

Asagidaki matris, roller ve yetkiler arasindaki iliskiyi gostermektedir:

  ------------------------------------------------------------------------------
  **Yetki**          **ADMIN**     **SUPERVISOR**   **AGENT**     **CUSTOMER**
  ------------------ ------------- ---------------- ------------- --------------
  Ticket Olustur     Evet          Evet             Evet          Evet

  Ticket Goruntule   Tumu          Tumu             Atanan        Kendi

  Ticket Guncelle    Tumu          Tumu             Atanan        Kendi

  Ticket Sil         Evet          Hayir            Hayir         Hayir

  Kullanici Yonet    Evet          Hayir            Hayir         Hayir

  Rapor Goruntule    Evet          Evet             Hayir         Hayir

  Ayar Yonet         Evet          Hayir            Hayir         Hayir
  ------------------------------------------------------------------------------

Tablo 3: Rol-Yetki Matrisi

## 3.3 Grup Bazli Yetkilendirme

Zammad ve benzeri sistemlerde, rollerin yaninda grup (group) bazli yetkilendirme de onemlidir. Her grup belirli bir departmani veya ekibi temsil eder (ornegin: IT Destek, Yazilim Gelistirme, Sistem Yonetimi). Kullanicilara gruplar uzerinde asagidaki yetkiler atanabilir:

-   FULL: Grup uzerinde tam yetki (okuma, yazma, silme)

-   READ: Sadece okuma yetkisi

-   OVERVIEW: Ticket listelerini gorme yetkisi

-   CREATE: Yeni ticket olusturma yetkisi

-   CHANGE: Mevcut ticket\'leri guncelleme yetkisi

# 4. Ticket Yasam Dongusu

Ticket yasam dongusu, bir ticket\'in sistemdeki tum durumlarini ve bu durumlar arasindaki gecisleri tanimlar. ITIL cercevesinde standart ticket durumlari ve gecisleri asagida aciklanmistir:

## 4.1 Ticket Durumlari

  -----------------------------------------------------------------------
  **Durum**          **Aciklama**
  ------------------ ----------------------------------------------------
  NEW                Ticket olusturuldu, henuz atanmadi

  OPEN               Ticket atandi, cozum bekleniyor

  IN_PROGRESS        Aktif olarak uzerinde calisiliyor

  PENDING            Musteri veya 3. parti bilgi bekleniyor

  RESOLVED           Cozum uygulandi, onay bekleniyor

  CLOSED             Ticket kapatildi, islem tamam
  -----------------------------------------------------------------------

Tablo 4: Ticket Durumlari ve Aciklamalari

## 4.2 Onceliklendirme ve SLA Yonetimi

Ticket onceliklendirme, etki (impact) ve aciliyet (urgency) degerlerinin birlesiminden olusur. Bu degerler, Service Level Agreement (SLA) hedeflerinin belirlenmesinde kullanilir:

  ---------------------------------------------------------------------------
  **Oncelik**    **Yanit Suresi**   **Cozum Suresi**   **Ornek**
  -------------- ------------------ ------------------ ----------------------
  CRITICAL       15 dk              2 saat             Sistem cokusu

  HIGH           1 saat             8 saat             Kritik is fonksiyonu

  MEDIUM         4 saat             24 saat            Kismi etki

  LOW            8 saat             72 saat            Dusuk etki
  ---------------------------------------------------------------------------

Tablo 5: Oncelik Seviyeleri ve SLA Hedefleri

# 5. Veritabani Tasarimi ve ER Diyagramlari

IT Helpdesk sisteminin veritabani tasarimi, sistemin performansi, olceklenebilirligi ve islevselligi acisindan kritik oneme sahiptir. Asagida, kapsamli bir helpdesk sistemi icin gerekli temel varliklar ve iliskiler aciklanmistir.

## 5.1 Temel Varliklar (Entities)

### Users (Kullanicilar)

Sisteme erisen tum kullanicilari (musteriler, ajanlar, yoneticiler) temsil eder.

  ------------------------------------------------------------------------
  **Alan**               **Tip**           **Aciklama**
  ---------------------- ----------------- -------------------------------
  id                     BIGINT            PK, Auto Increment

  username               VARCHAR(50)       Unique, Not Null

  email                  VARCHAR(100)      Unique, Not Null

  password_hash          VARCHAR(255)      Not Null

  first_name             VARCHAR(50)       Not Null

  last_name              VARCHAR(50)       Not Null

  phone                  VARCHAR(20)       Nullable

  department             VARCHAR(100)      Nullable

  is_active              BOOLEAN           Default: true

  created_at             TIMESTAMP         Default: CURRENT_TIMESTAMP

  updated_at             TIMESTAMP         On Update
  ------------------------------------------------------------------------

### Tickets

Sistemdeki tum ticket\'lari ve ozelliklerini icerir.

  -------------------------------------------------------------------------------
  **Alan**               **Tip**           **Aciklama**
  ---------------------- ----------------- --------------------------------------
  id                     BIGINT            PK, Auto Increment

  ticket_number          VARCHAR(20)       Unique, Not Null

  title                  VARCHAR(255)      Not Null

  description            TEXT              Not Null

  status                 ENUM              NEW, OPEN, PENDING, RESOLVED, CLOSED

  priority               ENUM              LOW, MEDIUM, HIGH, CRITICAL

  category_id            BIGINT            FK -\> categories

  requester_id           BIGINT            FK -\> users

  assignee_id            BIGINT            FK -\> users, Nullable

  group_id               BIGINT            FK -\> groups

  created_at             TIMESTAMP         Default: CURRENT_TIMESTAMP

  updated_at             TIMESTAMP         On Update

  resolved_at            TIMESTAMP         Nullable

  closed_at              TIMESTAMP         Nullable

  sla_due_date           TIMESTAMP         Nullable
  -------------------------------------------------------------------------------

### Comments (Yorumlar)

Ticket\'lara eklenen yorumlar ve notlari saklar.

  --------------------------------------------------------------------------
  **Alan**               **Tip**           **Aciklama**
  ---------------------- ----------------- ---------------------------------
  id                     BIGINT            PK, Auto Increment

  ticket_id              BIGINT            FK -\> tickets

  author_id              BIGINT            FK -\> users

  content                TEXT              Not Null

  is_internal            BOOLEAN           Default: false (musteri gormez)

  created_at             TIMESTAMP         Default: CURRENT_TIMESTAMP
  --------------------------------------------------------------------------

### Attachments (Ekler)

Ticket\'lara eklenen dosyalari saklar.

  ------------------------------------------------------------------------
  **Alan**               **Tip**           **Aciklama**
  ---------------------- ----------------- -------------------------------
  id                     BIGINT            PK, Auto Increment

  ticket_id              BIGINT            FK -\> tickets

  filename               VARCHAR(255)      Not Null

  file_path              VARCHAR(500)      Not Null

  file_size              BIGINT            Not Null

  mime_type              VARCHAR(100)      Not Null

  uploaded_by            BIGINT            FK -\> users

  created_at             TIMESTAMP         Default: CURRENT_TIMESTAMP
  ------------------------------------------------------------------------

### Roles (Roller)

Sistemdeki rolleri tanimlar.

  ------------------------------------------------------------------------
  **Alan**               **Tip**           **Aciklama**
  ---------------------- ----------------- -------------------------------
  id                     BIGINT            PK, Auto Increment

  name                   VARCHAR(50)       Unique, Not Null

  description            VARCHAR(255)      Nullable

  is_active              BOOLEAN           Default: true
  ------------------------------------------------------------------------

### Permissions (Yetkiler)

Sistemdeki tum yetkileri listeler.

  ------------------------------------------------------------------------------
  **Alan**               **Tip**           **Aciklama**
  ---------------------- ----------------- -------------------------------------
  id                     BIGINT            PK, Auto Increment

  name                   VARCHAR(50)       Unique, Not Null

  resource               VARCHAR(50)       Ornek: ticket, user, report

  action                 VARCHAR(50)       Ornek: create, read, update, delete

  description            VARCHAR(255)      Nullable
  ------------------------------------------------------------------------------

### Groups (Gruplar)

Ticket\'larin atanabilecegi departman/ekip gruplarini tanimlar.

  ------------------------------------------------------------------------
  **Alan**               **Tip**           **Aciklama**
  ---------------------- ----------------- -------------------------------
  id                     BIGINT            PK, Auto Increment

  name                   VARCHAR(100)      Unique, Not Null

  description            VARCHAR(255)      Nullable

  email                  VARCHAR(100)      Grup e-posta adresi

  is_active              BOOLEAN           Default: true
  ------------------------------------------------------------------------

## 5.2 Iliskiler (Relationships)

Veritabani varliklari arasindaki iliskiler asagida aciklanmistir:

  --------------------------------------------------------------------------------------------------
  **Iliski**                 **Tip**     **Kaynak**                 **Hedef**
  -------------------------- ----------- -------------------------- --------------------------------
  User - Ticket              1:N         users.id                   tickets.requester_id

  User - Ticket (Assignee)   1:N         users.id                   tickets.assignee_id

  Ticket - Comment           1:N         tickets.id                 comments.ticket_id

  Ticket - Attachment        1:N         tickets.id                 attachments.ticket_id

  User - Role                N:M         user_roles.user_id         user_roles.role_id

  Role - Permission          N:M         role_permissions.role_id   role_permissions.permission_id

  Group - User               N:M         group_users.group_id       group_users.user_id

  Group - Ticket             1:N         groups.id                  tickets.group_id
  --------------------------------------------------------------------------------------------------

Tablo 6: Varliklar Arasi Iliskiler

## 5.3 ER Diyagram Yapisi

Asagida sistemin kavramsal ER diyagrami metin olarak tanimlanmistir:

> \[Users\] \|\|\--o{ \[Tickets\] : creates \[Users\] \|\|\--o{ \[Tickets\] : assigned_to \[Users\] }o\--o{ \[Roles\] : has \[Users\] }o\--o{ \[Groups\] : member_of \[Tickets\] \|\|\--o{ \[Comments\] : contains \[Tickets\] \|\|\--o{ \[Attachments\] : has \[Tickets\] }o\--\|\| \[Categories\] : categorized_as \[Tickets\] }o\--\|\| \[Groups\] : belongs_to \[Roles\] }o\--o{ \[Permissions\] : includes Aciklama: - \|\|\--o{ : Bir-kadar (One-to-Many) - }o\--o{ : Cok-cok (Many-to-Many) - }o\--\|\| : Cok-bir (Many-to-One)

# 6. Java Proje Taslagi

Bu bolumde, IT Helpdesk sisteminin Java ve Spring Boot kullanilarak nasil gelistirilebilecegi kapsamli bir sekilde aciklanmistir. Web uygulamasi Spring Boot + Spring Web MVC, Desktop uygulamasi ise Spring Boot ile gelistirilmistir. Her iki uygulama da ortak bir Spring Boot altyapisi uzerinde calisir. Proje, modern yazilim mimarisi prensiplerine uygun olarak tasarlanmistir.

## 6.1 Teknoloji Stack\'i

  ----------------------------------------------------------------------------
  **Bilesen**               **Teknoloji**                       **Versiyon**
  ------------------------- ----------------------------------- --------------
  **Web - Framework**       Spring Boot + Spring Web MVC        6.1.x

  Web - View                Thymeleaf                           3.2.x

  Web - Server              Spring Boot Embedded Tomcat         10.x

  **Desktop - Framework**   Spring Boot                         3.2.x

  Desktop - UI              Spring Boot                         17+

  **Ortak - Guvenlik**      Spring Security + JWT               6.2.x

  Ortak - Veri Erisimi      Spring Data JPA                     3.2.x

  Ortak - Veritabani        MySQL                               8.0+

  Ortak - Build Tool        Maven                               3.9+

  **Container**             Docker + Docker Compose             24.x
  ----------------------------------------------------------------------------

Tablo 7: Web ve Desktop Uygulamalari Teknoloji Stacki

## 6.2 Proje Yapisi

## 6.2 N-Tier Mimari Yapisi

Proje, N-Tier (Cok Katmanli) mimari prensiplerine uygun olarak tasarlanmistir. Hem Web hem de Desktop uygulamalari ayni katmanli mimariyi paylasmakta ve ortak bir veritabani (MySQL) kullanmaktadir.

### Mimari Katmanlar

6.  **Presentation Layer (Sunum Katmani):**Web uygulamasi icin Spring Boot + Spring Web MVC (Thymeleaf template), Desktop uygulamasi icin Spring Boot

7.  **Controller Layer (Kontrol Katmani):**Spring Boot + Spring Web MVC Controller\'lar (Web) ve Spring Boot Desktop Controller\'lar

8.  **Service Layer (Is Mantigi Katmani):**Ortak is mantigi, validasyon ve is kurallari

9.  **Repository Layer (Veri Erisim Katmani):**Spring Data JPA Repository\'ler

10. **Database Layer (Veritabani Katmani):**MySQL veritabani

11. 

### Proje Dizin Yapisi

> helpdesk-system/ ├── helpdesk-common/ \# Ortak katmanlar (shared) │ ├── src/main/java/com/helpdesk/common/ │ │ ├── entity/ \# JPA Entity\'ler │ │ ├── repository/ \# JPA Repository\'ler │ │ ├── service/ \# Service Interface\'leri │ │ ├── service/impl/ \# Service Implementasyonlari │ │ ├── dto/ \# Data Transfer Objects │ │ ├── exception/ \# Custom Exception\'lar │ │ └── util/ \# Yardimci siniflar │ └── pom.xml │ ├── helpdesk-web/ \# Web Uygulamasi (Spring Boot + Spring Web MVC) │ ├── src/main/java/com/helpdesk/web/ │ │ ├── config/ \# Web yapilandirmasi │ │ ├── controller/ \# Spring Web MVC Controllers │ │ ├── security/ \# Web Security Config │ │ └── WebApplication.java │ ├── src/main/resources/ │ │ ├── templates/ \# Thymeleaf template\'leri │ │ ├── static/ \# CSS, JS, images │ │ └── application.properties │ └── pom.xml │ ├── helpdesk-desktop/ \# Desktop Uygulamasi (Spring Boot) │ ├── src/main/java/com/helpdesk/desktop/ │ │ ├── controller/ \# Desktop Controllers │ │ ├── ui/ \# UI siniflari │ │ ├── security/ \# Desktop Security │ │ └── DesktopApplication.java │ ├── src/main/resources/ │ │ └── application.properties │ └── pom.xml │ ├── helpdesk-database/ \# Database migrations │ └── src/main/resources/db/migration/ │ ├── docker-compose.yml \# Docker Compose konfigurasyonu ├── Dockerfile.web \# Web uygulamasi Dockerfile ├── Dockerfile.desktop \# Desktop uygulamasi Dockerfile └── pom.xml \# Root Maven POM

## 6.3 Temel Siniflar ve Paketler

### Entity Siniflari

JPA entity siniflari, veritabani tablolarini temsil eder:

> \@Entity \@Table(name = \"tickets\") public class Ticket { \@Id \@GeneratedValue(strategy = GenerationType.IDENTITY) private Long id; \@Column(name = \"ticket_number\", unique = true, nullable = false) private String ticketNumber; \@Column(nullable = false) private String title; \@Column(columnDefinition = \"TEXT\", nullable = false) private String description; \@Enumerated(EnumType.STRING) \@Column(nullable = false) private TicketStatus status; \@Enumerated(EnumType.STRING) \@Column(nullable = false) private Priority priority; \@ManyToOne \@JoinColumn(name = \"requester_id\") private User requester; \@ManyToOne \@JoinColumn(name = \"assignee_id\") private User assignee; \@ManyToOne \@JoinColumn(name = \"group_id\") private Group group; \@OneToMany(mappedBy = \"ticket\", cascade = CascadeType.ALL) private List\<Comment\> comments = new ArrayList\<\>(); // Getters, Setters, Constructors }

### Repository Katmani

Spring Data JPA repository arayuzleri:

> \@Repository public interface TicketRepository extends JpaRepository\<Ticket, Long\> { Optional\<Ticket\> findByTicketNumber(String ticketNumber); List\<Ticket\> findByRequesterId(Long requesterId); List\<Ticket\> findByAssigneeId(Long assigneeId); List\<Ticket\> findByStatus(TicketStatus status); List\<Ticket\> findByGroupId(Long groupId); \@Query(\"SELECT t FROM Ticket t WHERE t.status IN :statuses\") List\<Ticket\> findByStatuses(@Param(\"statuses\") List\<TicketStatus\> statuses); \@Query(\"SELECT t FROM Ticket t WHERE t.slaDueDate \< :now AND t.status NOT IN (\'CLOSED\', \'RESOLVED\')\") List\<Ticket\> findOverdueTickets(@Param(\"now\") LocalDateTime now); }

### Service Katmani

Is mantiginin bulundugu service siniflari:

> public interface TicketService { TicketDTO createTicket(CreateTicketRequest request); TicketDTO getTicketById(Long id); TicketDTO getTicketByNumber(String ticketNumber); List\<TicketDTO\> getAllTickets(Pageable pageable); List\<TicketDTO\> getTicketsByRequester(Long requesterId); List\<TicketDTO\> getTicketsByAssignee(Long assigneeId); TicketDTO assignTicket(Long ticketId, Long assigneeId); TicketDTO updateStatus(Long ticketId, TicketStatus status); TicketDTO addComment(Long ticketId, AddCommentRequest request); void closeTicket(Long ticketId, String resolution); } \@Service \@Transactional public class TicketServiceImpl implements TicketService { // Implementation }

### Controller Katmani

Spring Web MVC Controller siniflari (Web) ve Spring Boot Controller siniflari (Desktop):

> \@Controller \@RequestMapping(\"/tickets\") public class TicketController { private final TicketService ticketService; public TicketController(TicketService ticketService) { this.ticketService = ticketService; } \@GetMapping \@PreAuthorize(\"hasRole(\'AGENT\') or hasRole(\'ADMIN\')\") public String listTickets(Model model) { model.addAttribute(\"tickets\", ticketService.getAllTickets()); return \"tickets/list\"; } \@GetMapping(\"/{id}\") \@PreAuthorize(\"hasRole(\'AGENT\') or hasRole(\'ADMIN\') or \@securityService.isTicketOwner(#id, authentication)\") public String getTicket(@PathVariable Long id, Model model) { model.addAttribute(\"ticket\", ticketService.getTicketById(id)); return \"tickets/detail\"; } \@GetMapping(\"/new\") \@PreAuthorize(\"hasRole(\'CUSTOMER\') or hasRole(\'AGENT\')\") public String newTicketForm(Model model) { model.addAttribute(\"ticket\", new CreateTicketRequest()); return \"tickets/form\"; } \@PostMapping \@PreAuthorize(\"hasRole(\'CUSTOMER\') or hasRole(\'AGENT\')\") public String createTicket(@Valid \@ModelAttribute CreateTicketRequest request, BindingResult result) { if (result.hasErrors()) return \"tickets/form\"; ticketService.createTicket(request); return \"redirect:/tickets\"; } \@PostMapping(\"/{id}/assign\") \@PreAuthorize(\"hasRole(\'AGENT\') or hasRole(\'ADMIN\')\") public String assignTicket(@PathVariable Long id, \@RequestParam Long assigneeId) { ticketService.assignTicket(id, assigneeId); return \"redirect:/tickets/\" + id; } \@PostMapping(\"/{id}/status\") \@PreAuthorize(\"hasRole(\'AGENT\') or hasRole(\'ADMIN\')\") public String updateStatus(@PathVariable Long id, \@RequestParam TicketStatus status) { ticketService.updateStatus(id, status); return \"redirect:/tickets/\" + id; } }

## 6.4 Web URL Yapisi ve Controller Mapping

  ----------------------------------------------------------------------------
  **Method**    **Endpoint**             **Aciklama**        **Yetki**
  ------------- ------------------------ ------------------- -----------------
  POST          /tickets                 Ticket olustur      CUSTOMER+

  GET           /tickets/{id}            Ticket detayi       AUTHENTICATED

  GET           /tickets                 Ticket listesi      AGENT+

  PUT           /tickets/{id}            Ticket guncelle     AGENT+

  PUT           /tickets/{id}/assign     Ticket ata          AGENT+

  POST          /tickets/{id}/comments   Yorum ekle          AUTHENTICATED

  GET           /users                   Kullanici listesi   ADMIN

  GET           /reports/sla             SLA raporu          SUPERVISOR+
  ----------------------------------------------------------------------------

Tablo 8: Spring Web MVC URL Mapping ve Yetki Tablosu

## 6.5 Guvenlik Yapilandirmasi

Spring Security ile form tabanli oturum yonetimi yapilandirmasi (Spring Web MVC icin):

> \@Configuration \@EnableWebSecurity \@EnableMethodSecurity public class SecurityConfig { \@Bean public SecurityFilterChain filterChain(HttpSecurity http) throws Exception { http .csrf(Customizer.withDefaults()) .authorizeHttpRequests(auth -\> auth .requestMatchers(\"/auth/\*\*\", \"/css/\*\*\", \"/js/\*\*\").permitAll() .requestMatchers(\"/admin/\*\*\").hasRole(\"ADMIN\") .requestMatchers(\"/reports/\*\*\").hasAnyRole(\"ADMIN\", \"SUPERVISOR\") .anyRequest().authenticated() ) .formLogin(form -\> form .loginPage(\"/auth/login\") .defaultSuccessUrl(\"/tickets\", true) .failureUrl(\"/auth/login?error\") .permitAll() ) .logout(logout -\> logout .logoutUrl(\"/auth/logout\") .logoutSuccessUrl(\"/auth/login?logout\") .permitAll() ) .sessionManagement(sess -\> sess .maximumSessions(1) ); return http.build(); } \@Bean public PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); } }

## 6.6 Docker Containerizasyon

Proje, Docker container teknolojisi kullanilarak containerize edilmistir. Bu sayede uygulamalarin farkli ortamlarda tutarli calismasi saglanir ve deployment sureci kolaylastirilir.

### Docker Compose Yapisi

> version: \'3.8\' services: \# MySQL Veritabani mysql: image: mysql:8.0 container_name: helpdesk-mysql environment: MYSQL_ROOT_PASSWORD: rootpass MYSQL_DATABASE: helpdesk_db MYSQL_USER: helpdesk_user MYSQL_PASSWORD: helpdesk_pass ports: - \"3306:3306\" volumes: - mysql_data:/var/lib/mysql networks: - helpdesk-network \# Web Uygulamasi (Spring Boot + Spring Web MVC) helpdesk-web: build: context: . dockerfile: Dockerfile.web container_name: helpdesk-web ports: - \"8080:8080\" environment: SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/helpdesk_db SPRING_DATASOURCE_USERNAME: helpdesk_user SPRING_DATASOURCE_PASSWORD: helpdesk_pass depends_on: - mysql networks: - helpdesk-network \# Desktop Uygulamasi Backend (Spring Boot) helpdesk-desktop: build: context: . dockerfile: Dockerfile.desktop container_name: helpdesk-desktop ports: - \"8081:8081\" environment: SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/helpdesk_db SPRING_DATASOURCE_USERNAME: helpdesk_user SPRING_DATASOURCE_PASSWORD: helpdesk_pass depends_on: - mysql networks: - helpdesk-network volumes: mysql_data: networks: helpdesk-network: driver: bridge

### Dockerfile - Web Uygulamasi

> \# Web Uygulamasi Dockerfile FROM eclipse-temurin:17-jdk-alpine as build WORKDIR /app COPY . /app RUN ./mvnw clean package -pl helpdesk-common,helpdesk-web -am -DskipTests FROM eclipse-temurin:17-jre-alpine WORKDIR /app COPY \--from=build /app/helpdesk-web/target/\*.jar app.jar EXPOSE 8080 ENTRYPOINT \[\"java\", \"-jar\", \"app.jar\"\]

### Dockerfile - Desktop Uygulamasi

> \# Desktop Uygulamasi Dockerfile FROM eclipse-temurin:17-jdk-alpine as build WORKDIR /app COPY . /app RUN ./mvnw clean package -pl helpdesk-common,helpdesk-desktop -am -DskipTests FROM eclipse-temurin:17-jre-alpine WORKDIR /app COPY \--from=build /app/helpdesk-desktop/target/\*.jar app.jar EXPOSE 8081 ENTRYPOINT \[\"java\", \"-jar\", \"app.jar\"\]

### Docker Kullanim Komutlari

12. **Baslatma:**docker-compose up -d

13. **Durdurma:**docker-compose down

14. **Loglari Gorme:**docker-compose logs -f

15. **Rebuild:**docker-compose up -d \--build

# 7. Design Patterns

Bu projede yazilim kalitesini artirmak, kodun surdurulebilirligini saglamak ve mimariyi guclendirmek amaciyla asagidaki tasarim desenleri (design patterns) uygulanmaktadir.

## 7.1 MVC (Model-View-Controller)

MVC deseni, uygulamayi uc katmana ayirir: Model (is mantigi ve veri), View (kullanici arayuzu) ve Controller (kullanici isteklerini yoneten katman). Bu projede Spring Web MVC, web uygulamasinin tum request-response dongusu boyunca MVC desenini dogal olarak uygulamaktadir. Thymeleaf template\'leri View katmanini, \@Controller siniflari Controller katmanini, Entity ve Service siniflari ise Model katmanini olusturur. Bu ayrim sayesinde her katman bagimsiz olarak gelistirilebilir ve test edilebilir.

## 7.2 DTO (Data Transfer Object) Pattern

DTO deseni, katmanlar arasinda veri tasimak icin kullanilan ozel nesneleri tanimlar. Bu projede Entity siniflari (Ticket, User, vb.) dogrudan kullaniciya gosterilmez; bunun yerine TicketDTO, UserDTO gibi DTO siniflari araciligiyla veri aktarimi yapilir. Bu yaklasim; hassas alanlarin (sifre hash\'i gibi) ifsa edilmesini onler, API sozlesmesini veritabanindan bagimsiz kilar ve her use-case icin optimize edilmis veri yapilarinin kullanilmasina olanak tanir.

## 7.3 Factory Pattern

Factory deseni, nesne olusturma sorumluluğunu merkezi bir noktaya tasir. Bu projede TicketFactory sinifi, farkli ticket turlerine (Incident, ServiceRequest) gore dogru Ticket nesnelerini olusturmaktan sorumludur. Bu desen olmasaydi, her Controller veya Service sinifi kendi icinde nesne olusturma mantigi tasimak zorunda kalir; bu da kod tekrarına ve bagimliliklarin artmasina yol acardi. Factory sayesinde ticket olusturma kurallari tek bir yerde yonetilir.

## 7.4 Observer Pattern

Observer deseni, bir nesnedeki degisikliklerin bagli diger nesnelere otomatik olarak bildirilmesini saglar. Bu projede bir ticket\'in durumu degistiginde (ornegin OPEN\'dan IN_PROGRESS\'e gecis), ilgili taraflarin (atanan ajan, ticket sahibi, supervisor) bilgilendirilmesi gerekmektedir. Spring\'in ApplicationEvent ve ApplicationListener mekanizmasi kullanilarak TicketStatusChangedEvent olayi yayinlanir; e-posta bildirimi, SLA sayaci guncelleme ve log kaydetme gibi islemler bagimsiz Observer\'lar araciligiyla gerceklestirilir.

## 7.5 Strategy Pattern

Strategy deseni, bir algoritmayı veya davranisi ailesini tanimlar ve bunlari birbirinin yerine kullanilabilir hale getirir. Bu projede ticket oncelik hesaplama ve otomatik atama mantigi Strategy deseniyle modellenmistir. Ornegin TicketAssignmentStrategy arayuzu; RoundRobinStrategy, LeastLoadStrategy ve ExpertMatchStrategy gibi farkli atama stratejilerini icerir. Hangi stratejinin kullanilacagi runtime\'da sistemin konfigurasyonuna gore belirlenir; bu sayede yeni bir atama algoritmasi eklemek mevcut kodu degistirmeyi gerektirmez.

# 8. Sonuc

Bu rapor, kapsamli bir IT Helpdesk Ticket Sistemi\'nin tasarimi ve gelistirilmesi icin gerekli tum temel bilesenleri detayli bir sekilde ele almistir. Zammad ornegi uzerinden aciklanan kavramlar, modern ITSM prensiplerine dayali bir sistem icin temel olusturmaktadir.

## Temel Cikarimlar

-   Is Akislari: Etkin is akislari, hizli yanit sureleri ve yuksek kullanici memnuniyeti saglar

-   RBAC: Rol tabanli erisim kontrolu, guvenlik ve veri mahremiyeti icin kritiktir

-   Veritabani Tasarimi: Normalizasyon ve iliski yonetimi, sistemin olceklenebilirligini belirler

-   Java/Spring Boot: Enterprise duzeyde guclu ve guvenli uygulamalar gelistirmek icin idealdir

-   SLA Yonetimi: Service Level Agreement takibi, hizmet kalitesinin olculmesini saglar

**IT Helpdesk Ticket Sistemi**

Kapsamli Proje Raporu

Universite Odevi - 2025

Bu rapor IT Helpdesk sistemleri hakkinda kapsamli bir kaynak olarak hazirlanmistir.
