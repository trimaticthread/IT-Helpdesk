# IT Helpdesk Ticket System

> A support ticket management system built with N-Tier architecture for a university project.
> Same backend, different frontends — accessible from both desktop and (future) web.
> Inspired by real-world systems like Jira Service Management and Zammad.

---

## Quick Start (Setup Guide)

Follow these steps to get the project running on your machine.

### Prerequisites

| Tool | Version | Download |
|------|---------|----------|
| Java JDK | 21 LTS | [adoptium.net](https://adoptium.net) |
| Maven | 3.9+ | [maven.apache.org](https://maven.apache.org) |
| Docker Desktop | 24.x | [docker.com](https://www.docker.com/products/docker-desktop) |
| IntelliJ IDEA | Any | [jetbrains.com](https://www.jetbrains.com/idea) |

### Step 1 — Clone the repository

```bash
git clone https://github.com/trimaticthread/IT-Helpdesk.git
cd IT-Helpdesk
```

### Step 2 — Start the database

Make sure Docker Desktop is running, then:

```bash
docker-compose up -d
```

This starts a MySQL 8.0 container. On first run, the database schema is **automatically initialized** from `db/init.sql` — no manual SQL execution needed.

**Connection details (for DataGrip / DBeaver):**

| Field | Value |
|-------|-------|
| Host | `localhost` |
| Port | `3306` |
| User | `helpdesk_user` |
| Password | `helpdesk_pass` |
| Database | `helpdesk_db` |

### Step 3 — Open in IntelliJ IDEA

1. Open IntelliJ IDEA → **File → Open** → select the `IT-Helpdesk` folder
2. IntelliJ will detect the multi-module Maven project automatically
3. Wait for Maven to download dependencies (first run may take a few minutes)

### Step 4 — Run the application

1. Navigate to `helpdesk-desktop/src/main/java/com/helpdesk/desktop/DesktopApplication.java`
2. Click the green **Run** button next to `main()`
3. The login window will appear

### Default Test Accounts

| Role | Username | Password |
|------|----------|----------|
| Admin | `trimaticthread` | `Haziran2002` |
| Supervisor | `RequemArcade` | `MuratDugan123` |
| Agent | `sclexx3002` | `Kaancımya123` |
| Customer | `basibozuk` | `bozukbasi123` |

---

## What is This?

A system that manages IT support requests (tickets).
A user reports a problem, an agent resolves it, a supervisor monitors the team, and an admin controls everything.
In short: you write "my internet isn't working", someone shows up and fixes it.

The desktop application is a real native window — written with Swing, no browser required.
A web version (Servlet + JSP) is planned for a future release.
Both share the same backend — same Service, same DAO, same database.

---

## Technology Stack

| Component | Technology | Version | Why? |
|-----------|-----------|---------|------|
| Language | Java | 21 LTS | Stable, runs everywhere |
| Framework | Spring Boot | 3.2.5 | Handles dependency injection and configuration |
| Desktop UI | Swing (FlatLaf) | JDK built-in | Real native window, no extra dependencies |
| Web UI | JSP + JSTL | Servlet 5.x | Browser-accessible interface (planned) |
| Data Access | JDBC / JdbcTemplate | Spring 6.x | Hand-written SQL, no ORM — you know exactly what's happening |
| Database | MySQL | 8.0 | Classic, reliable, widely known |
| Build | Maven | 3.9+ | Multi-module project management |
| Container | Docker | 24.x | Eliminates "works on my machine" excuses |

---

## Architecture (N-Tier)

```
┌──────────────────────────────────────────────┐
│            PRESENTATION TIER                 │
│  helpdesk-desktop       helpdesk-web         │
│  (Swing Window)         (Servlet + JSP)      │
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

The rule is simple: **each layer only communicates with the layer directly below it.**
A Controller cannot touch a DAO. A Service cannot write SQL. The architecture enforces this.

In the desktop app there is no HTTP: the Swing Controller calls the Service directly.
In the web app there is HTTP: Browser → Servlet → Service. But Service and DAO are shared.

---

## Module Structure

Each layer lives in its own Maven module. They cannot reach into each other's internals (enforced at compile time).

```
IT-Helpdesk/
├── helpdesk-domain/          → Entity (POJO), Enum, Exception — no framework dependencies
├── helpdesk-persistence/     → DAO Interface + Impl — SQL via JDBC
├── helpdesk-application/     → Service + DTO + Mapper — business logic lives here
├── helpdesk-desktop/         → Swing View + Controller — native desktop window
└── helpdesk-web/             → Servlet + JSP — browser interface (planned)
```

### Dependency Chain

| Module | Depends On | Technology |
|--------|-----------|-----------|
| `helpdesk-domain` | Nothing | Pure Java POJO |
| `helpdesk-persistence` | domain | JDBC / JdbcTemplate |
| `helpdesk-application` | domain + persistence | Spring (service layer) |
| `helpdesk-desktop` | application | Swing + Spring Boot |
| `helpdesk-web` | application | Servlet + JSP + JSTL |

---

## Database Tables

| Table | Description | Notes |
|-------|-------------|-------|
| `users` | Everyone who logs in | Admin, agent, customer... |
| `roles` | User roles | ADMIN, SUPERVISOR, AGENT, CUSTOMER |
| `groups_` | Teams | Trailing underscore because `groups` is a reserved keyword in MySQL |
| `categories` | Ticket categories | Network Issue, Software Bug, Hardware Failure... |
| `tickets` | Support requests | The main point of the whole system |
| `comments` | Comments on tickets | `is_internal=true` means the customer cannot see it |
| `attachments` | File attachments | Screenshots, log files, etc. |
| `user_roles` | N:M join table | Which user has which role |
| `group_users` | N:M join table | Who belongs to which group |

---

## Ticket Lifecycle

```
NEW → OPEN → IN_PROGRESS → PENDING → RESOLVED → CLOSED
                               |
               Customer sends more info
               → back to IN_PROGRESS
```

| Status | Meaning |
|--------|---------|
| `NEW` | Just opened, no one has looked at it yet |
| `OPEN` | Open, waiting for an agent to pick it up |
| `IN_PROGRESS` | Agent is actively working on it |
| `PENDING` | Waiting for information from the customer |
| `RESOLVED` | Solved — will close once the customer confirms |
| `CLOSED` | Done. Everyone is happy (hopefully) |

---

## Roles (RBAC)

| Role | What They Can Do |
|------|-----------------|
| `ADMIN` | God mode. Full access — create/delete users, view all tickets, assign, generate reports |
| `SUPERVISOR` | Team lead. Assign tickets to agents, monitor all tickets, view reports |
| `AGENT` | Support staff. Resolve tickets, add internal notes. The ones doing the real work |
| `CUSTOMER` | End user. Opens their own ticket and tracks it — cannot see anyone else's |

---

## Design Patterns

| Pattern | Where | What It Does |
|---------|-------|-------------|
| MVC | Swing Controller → Service → View | Each component has a single responsibility |
| DTO | Between layers | Entities never leave the backend; DTOs are passed instead. Security by design |
| DAO | Persistence layer | Abstracts JDBC; the Service layer has no SQL knowledge |
| Factory | Ticket creation | Produces the right object based on type |
| Observer | Status changes | Notifies relevant parties when a ticket status changes |
| Strategy | Auto-assignment | Switches between different assignment rules |
| Singleton | Spring Beans | One instance per class, saves memory |
| Builder | DTO / Entity | Makes constructing objects with many parameters readable |

---

## Project Status

- [x] Architecture design and documentation
- [x] Docker + MySQL setup
- [x] Multi-module Maven structure
- [x] Domain layer (POJO Entity + Enum + Exception)
- [x] Persistence layer (DAO Interface + JDBC Impl)
- [x] Application layer (Service + DTO + Mapper)
- [x] Desktop layer (Swing View + Controller — all 4 role dashboards complete)
- [ ] Web layer (Servlet + JSP — planned)

---

*University Project — 2026*
