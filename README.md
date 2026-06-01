# IT Helpdesk Ticket Management System

> A full-stack IT support ticket management system built with a clean N-Tier architecture.
> Same backend, two frontends — a native desktop application (Swing) and a browser-based web application (Spring MVC + JSP).

**Developers:** Sina Toprak Güleç · Ahmet Furkan Poyraz

---

## Quick Start

### Prerequisites

| Tool | Version | Download |
|------|---------|----------|
| Java JDK | 21 LTS | [adoptium.net](https://adoptium.net) |
| Maven | 3.9+ | [maven.apache.org](https://maven.apache.org) |
| Docker Desktop | 24.x | [docker.com](https://www.docker.com/products/docker-desktop) |
| IntelliJ IDEA | Any | [jetbrains.com](https://www.jetbrains.com/idea) |

### Step 1 — Clone

```bash
git clone https://github.com/trimaticthread/IT-Helpdesk.git
cd IT-Helpdesk
```

### Step 2 — Start the database

```bash
docker-compose up -d
```

The database schema and initial data are automatically loaded from `db/init.sql` on first run.

| Field | Value |
|-------|-------|
| Host | `localhost` |
| Port | `3306` |
| Database | `helpdesk_db` |
| User | `helpdesk_user` |
| Password | `helpdesk_pass` |

### Step 3 — Run the Desktop App

1. Open the project in IntelliJ IDEA
2. Navigate to `helpdesk-desktop/.../DesktopApplication.java`
3. Click the green **Run** button

### Step 4 — Run the Web App

1. Navigate to `helpdesk-web/.../WebApplication.java`
2. Click the green **Run** button
3. Open [http://localhost:8080](http://localhost:8080) in your browser

---

## Test Accounts

All accounts use the password: **`password`**

| Role | Username | Access |
|------|----------|--------|
| Admin | `trimaticthread` | User management, system settings, SLA, groups |
| Supervisor | `muro` | All tickets, assign agents/groups, reports |
| Agent (IT) | `aliyildiz` | Assigned tickets, status updates, internal notes |
| Agent (Software) | `sclexx` | Assigned tickets, status updates, internal notes |
| Agent (Hardware) | `emredemir` | Assigned tickets, status updates, internal notes |
| Customer | `basibozuk` | Open tickets, track status, add comments |

> On first login after a password reset, users are forced to change their password.

---

## What Does It Do?

An IT helpdesk is where employees report technical problems and get them resolved. This system manages that workflow end-to-end:

- **Customer** opens a ticket describing their problem
- **Supervisor** reviews it, assigns it to a group and an agent
- **Agent** works on it, updates status, adds internal notes
- **Supervisor** monitors SLA compliance, views reports, closes resolved tickets
- **Admin** manages users, departments, categories, groups, and SLA settings

---

## Technology Stack

| Layer | Technology | Notes |
|-------|-----------|-------|
| Language | Java 21 LTS | |
| Framework | Spring Boot 3.2.5 | DI, auto-configuration |
| Desktop UI | Java Swing | Native window, no browser required |
| Web UI | Spring MVC + JSP + JSTL | Browser-based interface |
| Auth (Web) | Session + Filter | Custom AuthFilter, no Spring Security |
| Data Access | JdbcTemplate | Hand-written SQL, no ORM |
| Database | MySQL 8.0 | Runs in Docker |
| Build | Maven (multi-module) | 5 modules, enforced layering |
| Container | Docker | Portable, reproducible DB setup |

---

## Architecture (N-Tier)

```
┌────────────────────────────────────────────────┐
│               PRESENTATION TIER                │
│   helpdesk-desktop          helpdesk-web       │
│   (Swing + Controllers)     (Spring MVC + JSP) │
└─────────────────────┬──────────────────────────┘
                      │
┌─────────────────────▼──────────────────────────┐
│              BUSINESS LOGIC TIER               │
│              helpdesk-application              │
│          Service + DTO + Mapper                │
└─────────────────────┬──────────────────────────┘
                      │
┌─────────────────────▼──────────────────────────┐
│               DATA ACCESS TIER                 │
│              helpdesk-persistence              │
│           DAO Interface + JDBC Impl            │
└─────────────────────┬──────────────────────────┘
                      │
┌─────────────────────▼──────────────────────────┐
│                DOMAIN TIER                     │
│               helpdesk-domain                  │
│        Entity (POJO) + Enum + Exception        │
└─────────────────────┬──────────────────────────┘
                      │
┌─────────────────────▼──────────────────────────┐
│                  DATABASE                      │
│               MySQL 8.0 (Docker)              │
└────────────────────────────────────────────────┘
```

**Rule:** Each layer only talks to the layer directly below it. A Controller cannot touch a DAO. A Service never writes SQL.

In the desktop app there is no HTTP — the Swing Controller calls the Service directly in-process.
In the web app there is HTTP — Browser → Spring MVC Controller → Service. Both share the exact same Service, DAO, and database.

---

## Module Structure

```
IT-Helpdesk/
├── helpdesk-domain/        Entity, Enum, Exception — zero framework dependencies
├── helpdesk-persistence/   DAO interface + JDBC implementation
├── helpdesk-application/   Service layer, DTO, Mapper — all business logic lives here
├── helpdesk-desktop/       Swing UI + Desktop controllers
├── helpdesk-web/           Spring MVC controllers + JSP views
└── db/
    └── init.sql            Database schema + seed data
```

---

## Key Features

### Ticket Management
- Full ticket lifecycle: NEW → OPEN → IN_PROGRESS → PENDING → RESOLVED → CLOSED
- Status locking: CLOSED tickets cannot be modified; agents cannot close tickets directly
- Internal comments: visible only to agents and supervisors, hidden from customers

### SLA (Service Level Agreement)
- Resolution time targets configured per priority (CRITICAL / HIGH / MEDIUM / LOW) by admin
- Automatically calculated on ticket creation
- Visual indicators on ticket lists: **On Track** / **Due Soon** / **SLA Breached** / **SLA Met**

### Group-Based Assignment
- Admin creates groups and assigns agents to them
- Supervisor assigns a ticket to a group → agent list filters to that group's members → agent is selected
- Enables team-based routing (IT Support, Software, Hardware, etc.)

### Role-Based Access Control (RBAC)
| Role | Capabilities |
|------|-------------|
| ADMIN | Manage users, categories, departments, groups, SLA settings |
| SUPERVISOR | View all tickets, change status, assign groups and agents, view reports |
| AGENT | View assigned tickets, update status, add public/internal comments |
| CUSTOMER | Open tickets, track their own tickets, add comments |

### Password Reset Flow
- Admin resets a user's password → temporary password set to `password`
- Users can self-request a reset via "Forgot Password" on the login page
- Admin approves requests from the admin panel → temporary password applied
- Forced password change on first login after any reset

### Web Interface
- Login with session-based authentication and AuthFilter protection
- Role-specific dashboards and navigation
- Ticket creation, detail view, commenting, status updates
- Admin panel: full CRUD for users, categories, departments, groups, SLA
- Supervisor: ticket list with SLA column, group + agent assignment, reports

---

## Database Tables

| Table | Description |
|-------|-------------|
| `users` | All system users |
| `roles` | ADMIN, SUPERVISOR, AGENT, CUSTOMER |
| `user_roles` | N:M — user to role mapping |
| `departments` | Organizational departments |
| `groups_` | Agent teams (underscore because `groups` is a MySQL reserved word) |
| `group_users` | N:M — agent to group mapping |
| `categories` | Ticket categories (Network Issue, Software Bug, etc.) |
| `tickets` | Support requests — the core of the system |
| `comments` | Ticket comments; `is_internal=true` hides from customers |
| `sla_settings` | Resolution time targets per priority |
| `password_reset_requests` | Self-service password reset requests pending admin approval |
| `attachments` | File attachments (schema ready) |

---

## Project Status

- [x] Multi-module Maven project structure
- [x] Docker + MySQL setup with automatic schema initialization
- [x] Domain layer (Entity, Enum, Exception)
- [x] Persistence layer (DAO + JDBC)
- [x] Application layer (Service + DTO + Mapper)
- [x] Desktop application — all 4 role dashboards complete
- [x] Web application — all role flows complete (Customer, Agent, Supervisor, Admin)
- [x] SLA system — auto-calculation + visual indicators
- [x] Group-based ticket assignment
- [x] Password reset flow (admin-initiated + self-service)
- [x] Role-based access control on both desktop and web

---

*University Project — 2026 · Sina Toprak Güleç · Ahmet Furkan Poyraz*
