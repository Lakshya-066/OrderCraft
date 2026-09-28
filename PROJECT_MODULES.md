# OrderCraft — Manufacturing Order Management System

> **Web-based Order Management System (OMS)** automating order processing, bill-of-materials generation, purchase orders, invoicing and payment tracking for general-purpose manufacturing.

---

## Table of Contents

1. [Project Overview](#1-project-overview)
2. [Tech Stack & Architecture](#2-tech-stack--architecture)
3. [Environment Setup & Installation](#3-environment-setup--installation)
4. [Module Overview Map](#4-module-overview-map)
5. [Module 1 — Authentication & Authorization](#5-module-1--authentication--authorization)
6. [Module 2 — User & Role Management](#6-module-2--user--role-management)
7. [Module 3 — Customer Management](#7-module-3--customer-management)
8. [Module 4 — Vendor Management](#8-module-4--vendor-management)
9. [Module 5 — Product & Item Master](#9-module-5--product--item-master)
10. [Module 6 — Bill of Materials (BOM)](#10-module-6--bill-of-materials-bom)
11. [Module 7 — Sales Order Management](#11-module-7--sales-order-management)
12. [Module 8 — Purchase Order Management](#12-module-8--purchase-order-management)
13. [Module 9 — Inventory Management (Lightweight)](#13-module-9--inventory-management-lightweight)
14. [Module 10 — Invoicing](#14-module-10--invoicing)
15. [Module 11 — Payment Tracking](#15-module-11--payment-tracking)
16. [Module 12 — Approval Workflow Engine](#16-module-12--approval-workflow-engine)
17. [Module 13 — Dashboard & Reporting](#17-module-13--dashboard--reporting)
18. [Module 14 — Audit Trail](#18-module-14--audit-trail)
19. [Module 15 — System Configuration](#19-module-15--system-configuration)
20. [Order-to-Cash Workflow](#20-order-to-cash-workflow)
21. [Database Schema Overview](#21-database-schema-overview)
22. [API Design Conventions](#22-api-design-conventions)
23. [Non-Functional Requirements](#23-non-functional-requirements)

---

## 1. Project Overview

**OrderCraft** is a web-based Manufacturing Order Management System designed to digitize and automate the entire **order-to-cash lifecycle** in a manufacturing environment. It covers the commercial side of manufacturing — from receiving a sales order, generating bills of materials, creating purchase orders for raw materials, invoicing customers, and tracking payments from both customers and to vendors.

### Key Business Goals

| Goal | Description |
|------|-------------|
| **Streamline Order Processing** | Replace manual/spreadsheet-based order tracking with a centralized digital system |
| **Automate BOM Generation** | Auto-generate flat bills of materials from product definitions when orders are placed |
| **Procurement Automation** | Auto-create purchase orders for raw materials based on BOM requirements and current stock |
| **Financial Visibility** | Full accounts receivable (customer invoices) and accounts payable (vendor payments) tracking |
| **Approval Controls** | Configurable multi-step approval workflows for sales orders, purchase orders, and invoices |
| **Real-Time Dashboards** | Visual dashboards for order pipeline, revenue, overdue payments, and stock status |

### What OrderCraft Does NOT Cover

- **Production/Work Order Tracking** — Manufacturing floor operations are tracked externally
- **Warehouse Management** — No bin/zone/location tracking; only aggregate stock levels
- **Shipping & Logistics** — Dispatch tracking is out of scope
- **Email/Push Notifications** — Users rely on dashboards; notifications may be added later

---

## 2. Tech Stack & Architecture

### Technology Stack

| Layer | Technology | Version (Recommended) |
|-------|------------|-----------------------|
| **Backend** | Java + Spring Boot | Java 17+, Spring Boot 3.x |
| **Frontend** | Angular | Angular 17+ (standalone components) |
| **Database** | Oracle DB | Oracle 19c / 21c XE (dev) |
| **ORM** | Spring Data JPA + Hibernate | — |
| **Security** | Spring Security + JWT | — |
| **Build (Backend)** | Maven | 3.9+ |
| **Build (Frontend)** | Angular CLI + npm | Node 18+ |
| **API Docs** | Springdoc OpenAPI (Swagger) | — |
| **Charting** | Chart.js / ngx-charts (Angular) | — |
| **PDF Generation** | iText / JasperReports | — |
| **Containerization** | Docker + Docker Compose | Optional |

### High-Level Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                     Angular Frontend                         │
│  (SPA — Components, Services, Guards, Interceptors)          │
└──────────────────────┬──────────────────────────────────────┘
                       │ HTTP/REST (JSON)
                       │ JWT Bearer Token
┌──────────────────────▼──────────────────────────────────────┐
│                   Spring Boot Backend                         │
│  ┌────────────┐  ┌────────────┐  ┌────────────────────────┐ │
│  │ Controllers│  │  Services  │  │  Repositories (JPA)    │ │
│  │  (REST)    │→ │ (Business  │→ │  (Spring Data)         │ │
│  │            │  │  Logic)    │  │                        │ │
│  └────────────┘  └────────────┘  └───────────┬────────────┘ │
│  ┌────────────┐  ┌────────────┐              │              │
│  │  Security  │  │  Approval  │              │              │
│  │  Filters   │  │  Engine    │              │              │
│  └────────────┘  └────────────┘              │              │
└──────────────────────────────────────────────┼──────────────┘
                                               │ JDBC
                                    ┌──────────▼──────────┐
                                    │     Oracle DB        │
                                    │  (Tables, Sequences, │
                                    │   Views, Indexes)    │
                                    └─────────────────────┘
```

### Project Structure

```
OrderCraft/
├── ordercraft-backend/                 # Spring Boot application
│   ├── src/main/java/com/ordercraft/
│   │   ├── auth/                       # Authentication & JWT
│   │   ├── user/                       # User & Role management
│   │   ├── customer/                   # Customer module
│   │   ├── vendor/                     # Vendor module
│   │   ├── product/                    # Product & Item master
│   │   ├── bom/                        # Bill of Materials
│   │   ├── salesorder/                 # Sales Order management
│   │   ├── purchaseorder/              # Purchase Order management
│   │   ├── inventory/                  # Inventory tracking
│   │   ├── invoice/                    # Invoicing (AR + AP)
│   │   ├── payment/                    # Payment tracking
│   │   ├── approval/                   # Approval workflow engine
│   │   ├── dashboard/                  # Dashboard & reporting
│   │   ├── audit/                      # Audit trail
│   │   ├── config/                     # System configuration
│   │   └── common/                     # Shared utilities, DTOs, exceptions
│   ├── src/main/resources/
│   │   ├── application.yml
│   │   └── db/migration/              # Flyway/Liquibase scripts
│   └── pom.xml
├── ordercraft-frontend/                # Angular application
│   ├── src/app/
│   │   ├── core/                       # Guards, interceptors, services
│   │   ├── shared/                     # Shared components, pipes, directives
│   │   ├── features/
│   │   │   ├── auth/
│   │   │   ├── dashboard/
│   │   │   ├── customers/
│   │   │   ├── vendors/
│   │   │   ├── products/
│   │   │   ├── bom/
│   │   │   ├── sales-orders/
│   │   │   ├── purchase-orders/
│   │   │   ├── inventory/
│   │   │   ├── invoices/
│   │   │   ├── payments/
│   │   │   ├── approvals/
│   │   │   ├── reports/
│   │   │   └── settings/
│   │   └── app.routes.ts
│   ├── angular.json
│   └── package.json
├── docker-compose.yml                  # Oracle DB + app containers
├── PROJECT_MODULES.md                  # This file
└── README.md
```

---

## 3. Environment Setup & Installation

### Prerequisites

| Software | Version | Purpose |
|----------|---------|---------|
| **JDK** | 17 or 21 (LTS) | Backend runtime |
| **Maven** | 3.9+ | Backend build |
| **Node.js** | 18+ (LTS) | Frontend runtime |
| **npm** | 9+ | Frontend package manager |
| **Angular CLI** | 17+ | `npm install -g @angular/cli` |
| **Oracle DB** | 19c / 21c XE | Database |
| **Docker** | 24+ | Optional — for containerized Oracle |
| **Git** | 2.x | Version control |

### Step 1 — Oracle Database Setup

**Option A: Docker (Recommended for Development)**

```bash
# Pull Oracle XE image
docker pull gvenzl/oracle-xe:21-slim

# Run Oracle container
docker run -d \
  --name ordercraft-db \
  -p 1521:1521 \
  -e ORACLE_PASSWORD=ordercraft_admin \
  -e APP_USER=ordercraft \
  -e APP_USER_PASSWORD=ordercraft123 \
  -v ordercraft-oracle-data:/opt/oracle/oradata \
  gvenzl/oracle-xe:21-slim
```

**Option B: Native Oracle Installation**

1. Download Oracle Database 21c XE from [oracle.com/database/technologies/xe-downloads.html](https://www.oracle.com/database/technologies/xe-downloads.html)
2. Follow the platform-specific installation guide
3. Create the application user:

```sql
-- Connect as SYSDBA
CREATE USER ordercraft IDENTIFIED BY ordercraft123
  DEFAULT TABLESPACE users
  TEMPORARY TABLESPACE temp
  QUOTA UNLIMITED ON users;

GRANT CONNECT, RESOURCE, CREATE VIEW, CREATE SEQUENCE TO ordercraft;
GRANT CREATE SESSION TO ordercraft;
```

### Step 2 — Backend Setup

```bash
# Clone the repository
git clone <repository-url>
cd OrderCraft/ordercraft-backend

# Configure database connection in src/main/resources/application.yml
# (See configuration section below)

# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

**application.yml — Key Configuration:**

```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:oracle:thin:@localhost:1521/XEPDB1
    username: ordercraft
    password: ordercraft123
    driver-class-name: oracle.jdbc.OracleDriver
  jpa:
    hibernate:
      ddl-auto: validate    # Use Flyway for schema management
    database-platform: org.hibernate.dialect.OracleDialect
    show-sql: false

  flyway:
    enabled: true
    locations: classpath:db/migration
    baseline-on-migrate: true

app:
  jwt:
    secret: ${JWT_SECRET:your-256-bit-secret-key-here-change-in-production}
    expiration-ms: 86400000   # 24 hours
  cors:
    allowed-origins: http://localhost:4200
```

**Maven Dependencies (pom.xml essentials):**

```xml
<dependencies>
    <!-- Spring Boot Starters -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>

    <!-- Oracle JDBC Driver -->
    <dependency>
        <groupId>com.oracle.database.jdbc</groupId>
        <artifactId>ojdbc11</artifactId>
        <scope>runtime</scope>
    </dependency>

    <!-- JWT -->
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-api</artifactId>
        <version>0.12.5</version>
    </dependency>

    <!-- Flyway for Oracle -->
    <dependency>
        <groupId>org.flywaydb</groupId>
        <artifactId>flyway-database-oracle</artifactId>
    </dependency>

    <!-- OpenAPI / Swagger -->
    <dependency>
        <groupId>org.springdoc</groupId>
        <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
        <version>2.5.0</version>
    </dependency>

    <!-- Lombok -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>
</dependencies>
```

### Step 3 — Frontend Setup

```bash
cd OrderCraft/ordercraft-frontend

# Install dependencies
npm install

# Start development server
ng serve
# App runs at http://localhost:4200
```

### Step 4 — Docker Compose (Full Stack)

```yaml
# docker-compose.yml
version: '3.8'
services:
  oracle-db:
    image: gvenzl/oracle-xe:21-slim
    ports:
      - "1521:1521"
    environment:
      ORACLE_PASSWORD: ordercraft_admin
      APP_USER: ordercraft
      APP_USER_PASSWORD: ordercraft123
    volumes:
      - oracle-data:/opt/oracle/oradata

  backend:
    build: ./ordercraft-backend
    ports:
      - "8080:8080"
    depends_on:
      - oracle-db
    environment:
      SPRING_DATASOURCE_URL: jdbc:oracle:thin:@oracle-db:1521/XEPDB1
      SPRING_DATASOURCE_USERNAME: ordercraft
      SPRING_DATASOURCE_PASSWORD: ordercraft123

  frontend:
    build: ./ordercraft-frontend
    ports:
      - "4200:80"
    depends_on:
      - backend

volumes:
  oracle-data:
```

```bash
docker-compose up -d
```

---

## 4. Module Overview Map

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                              OrderCraft Modules                                 │
├─────────────────────────────────────────────────────────────────────────────────┤
│                                                                                 │
│  ┌──────────────────┐   ┌──────────────────┐   ┌──────────────────────────┐    │
│  │  Authentication  │   │  User & Role     │   │  System Configuration    │    │
│  │  & Authorization │   │  Management      │   │                          │    │
│  └──────────────────┘   └──────────────────┘   └──────────────────────────┘    │
│                                                                                 │
│  ┌──────────────────┐   ┌──────────────────┐   ┌──────────────────────────┐    │
│  │  Customer        │   │  Vendor          │   │  Product & Item          │    │
│  │  Management      │   │  Management      │   │  Master                  │    │
│  └────────┬─────────┘   └────────┬─────────┘   └─────────┬──────────────┘    │
│           │                      │                        │                    │
│           ▼                      │                        ▼                    │
│  ┌──────────────────┐            │              ┌──────────────────────────┐    │
│  │  Sales Order     │            │              │  Bill of Materials       │    │
│  │  Management      │────────────┼──────────────│  (BOM)                   │    │
│  └────────┬─────────┘            │              └─────────┬──────────────┘    │
│           │                      │                        │                    │
│           │                      ▼                        │                    │
│           │             ┌──────────────────┐              │                    │
│           │             │  Purchase Order  │◄─────────────┘                    │
│           │             │  Management      │                                   │
│           │             └────────┬─────────┘                                   │
│           │                      │                                             │
│           ▼                      ▼                                             │
│  ┌──────────────────┐   ┌──────────────────┐                                  │
│  │  Invoicing       │   │  Inventory       │                                  │
│  │  (AR + AP)       │   │  (Lightweight)   │                                  │
│  └────────┬─────────┘   └──────────────────┘                                  │
│           │                                                                    │
│           ▼                                                                    │
│  ┌──────────────────┐   ┌──────────────────┐   ┌──────────────────────────┐    │
│  │  Payment         │   │  Approval        │   │  Dashboard &             │    │
│  │  Tracking        │   │  Workflow Engine  │   │  Reporting               │    │
│  └──────────────────┘   └──────────────────┘   └──────────────────────────┘    │
│                                                                                 │
│  ┌──────────────────┐                                                          │
│  │  Audit Trail     │                                                          │
│  └──────────────────┘                                                          │
│                                                                                 │
└─────────────────────────────────────────────────────────────────────────────────┘
```

---

## 5. Module 1 — Authentication & Authorization

### Purpose

Secures the application by handling user login, JWT token issuance, and route/API protection based on user permissions.

### Functionality

| Feature | Description |
|---------|-------------|
| **User Login** | Authenticate via username + password; returns a JWT access token |
| **JWT Token Management** | Issue, validate, and refresh JWT tokens with configurable expiry |
| **Password Hashing** | Bcrypt-based password hashing; no plain-text storage |
| **Route Protection (Frontend)** | Angular route guards prevent unauthorized navigation |
| **API Protection (Backend)** | Spring Security filters validate JWT on every API request |
| **Permission-Based Access** | Each API endpoint is guarded by specific permissions (e.g., `ORDER_CREATE`, `INVOICE_VIEW`) |
| **Session Logout** | Client-side token removal; optional server-side token blacklist |
| **Password Reset** | Admin-initiated password reset for users |

### Use Cases

| ID | Use Case | Actor | Flow |
|----|----------|-------|------|
| UC-1.1 | **Login** | Any User | User enters credentials → System validates → Returns JWT + user profile with permissions |
| UC-1.2 | **Access Protected Page** | Any User | User navigates to a route → Angular guard checks token validity + required permission → Allows or redirects to login |
| UC-1.3 | **Token Refresh** | Any User | Frontend detects token nearing expiry → Calls refresh endpoint → Receives new token |
| UC-1.4 | **Unauthorized API Call** | Any User | User calls API without valid token or without required permission → System returns `401`/`403` |
| UC-1.5 | **Admin Resets Password** | Admin | Admin selects user → Triggers password reset → User must set new password on next login |

### Key API Endpoints

```
POST   /api/auth/login          — Authenticate and receive JWT
POST   /api/auth/refresh        — Refresh an expiring token
POST   /api/auth/logout         — Invalidate token (optional blacklist)
POST   /api/auth/reset-password — Admin-initiated password reset
```

### Data Model

```
TABLE: oc_users (see Module 2)
TABLE: oc_token_blacklist
  - id              NUMBER          PK
  - token_hash      VARCHAR2(512)   NOT NULL
  - expiry_date     TIMESTAMP       NOT NULL
  - created_at      TIMESTAMP       DEFAULT SYSTIMESTAMP
```

---

## 6. Module 2 — User & Role Management

### Purpose

Manages system users and their access through a flexible permission-based role system. The system uses two base roles — **Admin** and **General User** — with granular permissions assigned to each role.

### Functionality

| Feature | Description |
|---------|-------------|
| **User CRUD** | Create, view, update, deactivate/reactivate user accounts |
| **Role Assignment** | Assign one or more roles to a user |
| **Permission Management** | Define granular permissions (e.g., `SALES_ORDER_CREATE`, `PO_APPROVE`); assign permissions to roles |
| **Role CRUD** | Admin can create custom roles beyond the default Admin/General User |
| **User Profile** | Users can view and update their own profile (name, email, password) |
| **User Status** | Active / Inactive toggle; inactive users cannot log in |
| **Bulk Operations** | Activate/deactivate multiple users at once |

### Default Roles & Permissions

| Role | Permissions |
|------|-------------|
| **Admin** | All permissions — full system access including user management, configuration, and all modules |
| **General User** | Configurable subset — typically: view/create/edit sales orders, view products, view inventory, view own invoices. No user management or system config access. |

### Granular Permissions Matrix

| Permission Key | Description | Admin | General User (Default) |
|----------------|-------------|:-----:|:----------------------:|
| `USER_MANAGE` | Create/edit/deactivate users | ✅ | ❌ |
| `ROLE_MANAGE` | Create/edit roles and permissions | ✅ | ❌ |
| `CUSTOMER_VIEW` | View customer list and details | ✅ | ✅ |
| `CUSTOMER_MANAGE` | Create/edit/delete customers | ✅ | ❌ |
| `VENDOR_VIEW` | View vendor list and details | ✅ | ✅ |
| `VENDOR_MANAGE` | Create/edit/delete vendors | ✅ | ❌ |
| `PRODUCT_VIEW` | View product catalog | ✅ | ✅ |
| `PRODUCT_MANAGE` | Create/edit/delete products & BOMs | ✅ | ❌ |
| `SO_VIEW` | View sales orders | ✅ | ✅ |
| `SO_CREATE` | Create new sales orders | ✅ | ✅ |
| `SO_EDIT` | Edit draft/pending sales orders | ✅ | ✅ |
| `SO_APPROVE` | Approve/reject sales orders | ✅ | ❌ |
| `PO_VIEW` | View purchase orders | ✅ | ✅ |
| `PO_CREATE` | Create purchase orders | ✅ | ❌ |
| `PO_APPROVE` | Approve/reject purchase orders | ✅ | ❌ |
| `INVENTORY_VIEW` | View stock levels | ✅ | ✅ |
| `INVENTORY_ADJUST` | Manually adjust stock | ✅ | ❌ |
| `INVOICE_VIEW` | View invoices | ✅ | ✅ |
| `INVOICE_CREATE` | Generate invoices | ✅ | ❌ |
| `PAYMENT_VIEW` | View payment records | ✅ | ✅ |
| `PAYMENT_RECORD` | Record incoming/outgoing payments | ✅ | ❌ |
| `APPROVAL_CONFIG` | Configure approval workflows | ✅ | ❌ |
| `DASHBOARD_VIEW` | Access dashboards | ✅ | ✅ |
| `REPORT_EXPORT` | Export reports (PDF/CSV) | ✅ | ✅ |
| `CONFIG_MANAGE` | System configuration settings | ✅ | ❌ |
| `AUDIT_VIEW` | View audit trail logs | ✅ | ❌ |

### Use Cases

| ID | Use Case | Actor | Flow |
|----|----------|-------|------|
| UC-2.1 | **Create User** | Admin | Admin fills user form (username, email, name, role) → System creates user with temporary password |
| UC-2.2 | **Deactivate User** | Admin | Admin selects user → Clicks deactivate → User can no longer log in; all active sessions invalidated |
| UC-2.3 | **Create Custom Role** | Admin | Admin defines new role name → Selects permissions from checklist → Saves role |
| UC-2.4 | **Assign Role to User** | Admin | Admin selects user → Assigns one or more roles → User's effective permissions update immediately |
| UC-2.5 | **Update Own Profile** | Any User | User navigates to profile → Updates name/email/password → System validates and saves |
| UC-2.6 | **View User List** | Admin | Admin views paginated, searchable list of all users with their roles and status |

### Key API Endpoints

```
GET    /api/users                — List all users (paginated, filterable)
POST   /api/users                — Create a new user
GET    /api/users/{id}           — Get user by ID
PUT    /api/users/{id}           — Update user
PATCH  /api/users/{id}/status    — Activate/deactivate user
GET    /api/users/me             — Get current user's profile
PUT    /api/users/me             — Update current user's profile

GET    /api/roles                — List all roles
POST   /api/roles                — Create a new role
PUT    /api/roles/{id}           — Update role (name + permissions)
DELETE /api/roles/{id}           — Delete role (if not assigned to any user)
GET    /api/permissions          — List all available permissions
```

### Data Model

```
TABLE: oc_users
  - id              NUMBER          PK (sequence: oc_users_seq)
  - username        VARCHAR2(50)    UNIQUE, NOT NULL
  - email           VARCHAR2(100)   UNIQUE, NOT NULL
  - full_name       VARCHAR2(100)   NOT NULL
  - password_hash   VARCHAR2(255)   NOT NULL
  - is_active       NUMBER(1)       DEFAULT 1
  - created_by      NUMBER          FK → oc_users(id)
  - created_at      TIMESTAMP       DEFAULT SYSTIMESTAMP
  - updated_by      NUMBER          FK → oc_users(id)
  - updated_at      TIMESTAMP

TABLE: oc_roles
  - id              NUMBER          PK (sequence: oc_roles_seq)
  - role_name       VARCHAR2(50)    UNIQUE, NOT NULL
  - description     VARCHAR2(255)
  - is_system       NUMBER(1)       DEFAULT 0  -- 1 = cannot be deleted
  - created_at      TIMESTAMP       DEFAULT SYSTIMESTAMP

TABLE: oc_permissions
  - id              NUMBER          PK (sequence: oc_permissions_seq)
  - permission_key  VARCHAR2(50)    UNIQUE, NOT NULL
  - description     VARCHAR2(255)

TABLE: oc_role_permissions
  - role_id         NUMBER          FK → oc_roles(id)
  - permission_id   NUMBER          FK → oc_permissions(id)
  - PRIMARY KEY (role_id, permission_id)

TABLE: oc_user_roles
  - user_id         NUMBER          FK → oc_users(id)
  - role_id         NUMBER          FK → oc_roles(id)
  - PRIMARY KEY (user_id, role_id)
```

---

## 7. Module 3 — Customer Management

### Purpose

Maintains a master list of customers (buyers) who place sales orders. Stores basic contact information and links to associated orders and invoices.

### Functionality

| Feature | Description |
|---------|-------------|
| **Customer CRUD** | Create, view, update, and soft-delete customer records |
| **Search & Filter** | Search by name, code, city; filter by status (active/inactive) |
| **Customer Code** | Auto-generated unique customer code (e.g., `CUST-0001`) |
| **Contact Details** | Name, phone, email, billing address |
| **Linked Records** | View all sales orders, invoices, and payments linked to a customer |
| **Status Management** | Active/Inactive toggle; inactive customers cannot be selected for new orders |
| **Duplicate Detection** | Warn on duplicate email or phone number |

### Use Cases

| ID | Use Case | Actor | Flow |
|----|----------|-------|------|
| UC-3.1 | **Add New Customer** | Admin / Authorized User | User fills customer form → System auto-generates customer code → Validates for duplicates → Saves |
| UC-3.2 | **View Customer Details** | Any User (with `CUSTOMER_VIEW`) | User clicks customer from list → System shows profile + linked sales orders, invoices, and payments |
| UC-3.3 | **Edit Customer Info** | Admin / Authorized User | User updates contact info or address → System validates and saves → Audit trail logged |
| UC-3.4 | **Deactivate Customer** | Admin | Admin marks customer inactive → Customer no longer appears in new order dropdowns |
| UC-3.5 | **Search Customers** | Any User | User types in search bar → System filters customer list by name/code/city in real-time |

### Key API Endpoints

```
GET    /api/customers              — List all customers (paginated, searchable)
POST   /api/customers              — Create a new customer
GET    /api/customers/{id}         — Get customer by ID
PUT    /api/customers/{id}         — Update customer
PATCH  /api/customers/{id}/status  — Activate/deactivate
GET    /api/customers/{id}/orders  — List sales orders for this customer
GET    /api/customers/search?q=    — Quick search by name/code
```

### Data Model

```
TABLE: oc_customers
  - id              NUMBER          PK (sequence: oc_customers_seq)
  - customer_code   VARCHAR2(20)    UNIQUE, NOT NULL    -- Auto: CUST-0001
  - name            VARCHAR2(150)   NOT NULL
  - email           VARCHAR2(100)
  - phone           VARCHAR2(20)
  - address_line1   VARCHAR2(255)
  - address_line2   VARCHAR2(255)
  - city            VARCHAR2(100)
  - state           VARCHAR2(100)
  - postal_code     VARCHAR2(20)
  - country         VARCHAR2(100)   DEFAULT 'India'
  - is_active       NUMBER(1)       DEFAULT 1
  - created_by      NUMBER          FK → oc_users(id)
  - created_at      TIMESTAMP       DEFAULT SYSTIMESTAMP
  - updated_by      NUMBER          FK → oc_users(id)
  - updated_at      TIMESTAMP
```

---

## 8. Module 4 — Vendor Management

### Purpose

Maintains a master list of vendors (suppliers) who supply raw materials. Stores basic contact information and links to associated purchase orders and vendor payments.

### Functionality

| Feature | Description |
|---------|-------------|
| **Vendor CRUD** | Create, view, update, and soft-delete vendor records |
| **Vendor Code** | Auto-generated unique vendor code (e.g., `VEND-0001`) |
| **Contact Details** | Name, phone, email, address |
| **Linked Records** | View all purchase orders, invoices (AP), and payments linked to a vendor |
| **Status Management** | Active/Inactive toggle |
| **Material Association** | View which raw materials a vendor supplies (derived from PO history) |

### Use Cases

| ID | Use Case | Actor | Flow |
|----|----------|-------|------|
| UC-4.1 | **Add New Vendor** | Admin / Authorized User | User fills vendor form → System auto-generates vendor code → Saves |
| UC-4.2 | **View Vendor Details** | Any User (with `VENDOR_VIEW`) | User clicks vendor → System shows profile + linked POs and payments |
| UC-4.3 | **Edit Vendor Info** | Admin / Authorized User | User updates contact info → System saves with audit log |
| UC-4.4 | **Deactivate Vendor** | Admin | Admin marks vendor inactive → Vendor no longer available for new POs |
| UC-4.5 | **View Vendor's PO History** | Any User | User opens vendor detail → Sees all historical and active purchase orders |

### Key API Endpoints

```
GET    /api/vendors                       — List all vendors (paginated)
POST   /api/vendors                       — Create a new vendor
GET    /api/vendors/{id}                  — Get vendor by ID
PUT    /api/vendors/{id}                  — Update vendor
PATCH  /api/vendors/{id}/status           — Activate/deactivate
GET    /api/vendors/{id}/purchase-orders  — List POs for this vendor
```

### Data Model

```
TABLE: oc_vendors
  - id              NUMBER          PK (sequence: oc_vendors_seq)
  - vendor_code     VARCHAR2(20)    UNIQUE, NOT NULL    -- Auto: VEND-0001
  - name            VARCHAR2(150)   NOT NULL
  - email           VARCHAR2(100)
  - phone           VARCHAR2(20)
  - address_line1   VARCHAR2(255)
  - address_line2   VARCHAR2(255)
  - city            VARCHAR2(100)
  - state           VARCHAR2(100)
  - postal_code     VARCHAR2(20)
  - country         VARCHAR2(100)   DEFAULT 'India'
  - is_active       NUMBER(1)       DEFAULT 1
  - created_by      NUMBER          FK → oc_users(id)
  - created_at      TIMESTAMP       DEFAULT SYSTIMESTAMP
  - updated_by      NUMBER          FK → oc_users(id)
  - updated_at      TIMESTAMP
```

---

## 9. Module 5 — Product & Item Master

### Purpose

Central catalog of all items in the system — both **finished goods** (products sold to customers) and **raw materials** (components purchased from vendors and consumed in manufacturing). This module is the foundation for BOM, sales orders, and purchase orders.

### Functionality

| Feature | Description |
|---------|-------------|
| **Item CRUD** | Create, view, update, and soft-delete items |
| **Item Types** | Categorize as `FINISHED_GOOD` or `RAW_MATERIAL` |
| **Item Code** | Auto-generated unique item code (e.g., `FG-0001`, `RM-0001`) |
| **Unit of Measure (UOM)** | Each item has a UOM (e.g., PCS, KG, LTR, MTR, SET) |
| **Pricing** | Selling price (for finished goods), standard cost (for raw materials) |
| **Category/Group** | Optional categorization for filtering (e.g., "Electronics", "Fasteners") |
| **Search & Filter** | Search by name/code; filter by type, category, status |
| **Linked Records** | View BOMs using this item, sales orders, purchase orders |
| **Low Stock Flag** | Visual indicator when current stock is below reorder level |

### Use Cases

| ID | Use Case | Actor | Flow |
|----|----------|-------|------|
| UC-5.1 | **Add Finished Good** | Admin / Product Manager | User fills product form with type=FINISHED_GOOD, selling price, UOM → System auto-generates code → Saves |
| UC-5.2 | **Add Raw Material** | Admin / Product Manager | User fills item form with type=RAW_MATERIAL, standard cost, UOM → System saves |
| UC-5.3 | **View Product Catalog** | Any User | User browses paginated list with filters for type, category, status → Clicks item for details |
| UC-5.4 | **Edit Item Details** | Admin / Product Manager | User updates price, description, reorder level → System saves |
| UC-5.5 | **View Where Used** | Any User | User opens a raw material → System shows all BOMs that include this material |
| UC-5.6 | **Deactivate Item** | Admin | Admin deactivates item → Item can no longer be added to new orders or BOMs |

### Key API Endpoints

```
GET    /api/items                    — List all items (paginated, filterable by type/category/status)
POST   /api/items                    — Create a new item
GET    /api/items/{id}               — Get item by ID
PUT    /api/items/{id}               — Update item
PATCH  /api/items/{id}/status        — Activate/deactivate
GET    /api/items/finished-goods     — List only finished goods
GET    /api/items/raw-materials      — List only raw materials
GET    /api/items/{id}/where-used    — List BOMs containing this item
GET    /api/items/search?q=          — Quick search by name/code
```

### Data Model

```
TABLE: oc_items
  - id              NUMBER          PK (sequence: oc_items_seq)
  - item_code       VARCHAR2(20)    UNIQUE, NOT NULL    -- Auto: FG-0001 or RM-0001
  - name            VARCHAR2(150)   NOT NULL
  - description     VARCHAR2(500)
  - item_type       VARCHAR2(20)    NOT NULL CHECK (item_type IN ('FINISHED_GOOD', 'RAW_MATERIAL'))
  - category        VARCHAR2(50)                        -- Optional grouping
  - uom             VARCHAR2(10)    NOT NULL             -- PCS, KG, LTR, MTR, SET
  - selling_price   NUMBER(12,2)                         -- For FINISHED_GOOD
  - standard_cost   NUMBER(12,2)                         -- For RAW_MATERIAL
  - reorder_level   NUMBER(10)      DEFAULT 0            -- Low stock threshold
  - is_active       NUMBER(1)       DEFAULT 1
  - created_by      NUMBER          FK → oc_users(id)
  - created_at      TIMESTAMP       DEFAULT SYSTIMESTAMP
  - updated_by      NUMBER          FK → oc_users(id)
  - updated_at      TIMESTAMP
```

---

## 10. Module 6 — Bill of Materials (BOM)

### Purpose

Defines the **recipe** for manufacturing a finished good — a flat (single-level) list of raw materials required to produce one unit of a finished product, along with the quantity of each material needed. When a sales order is placed, the BOM is used to calculate total material requirements and can trigger purchase orders for shortfalls.

### Functionality

| Feature | Description |
|---------|-------------|
| **BOM Creation** | Create a BOM for a finished good by selecting raw materials and specifying quantities per unit |
| **BOM Editing** | Add/remove/update raw material lines in an existing BOM |
| **BOM Versioning** | Each BOM has a version number; creating a new version archives the old one |
| **BOM Cost Rollup** | Auto-calculate total material cost per unit based on raw material standard costs × quantities |
| **BOM Explosion** | Given a sales order quantity, calculate total raw materials needed (qty × BOM quantities) |
| **BOM Copy** | Clone an existing BOM to create a new product's BOM |
| **BOM Status** | Draft → Active → Archived lifecycle |
| **Where-Used Query** | For any raw material, show all BOMs that reference it |

### BOM Structure (Single-Level / Flat)

```
Finished Good: "Widget Assembly" (FG-0001)
├── Raw Material: Steel Plate (RM-0001)     × 2.5 KG
├── Raw Material: Copper Wire (RM-0002)     × 0.3 MTR
├── Raw Material: Screw M5 (RM-0003)        × 8 PCS
└── Raw Material: Paint Blue (RM-0004)      × 0.1 LTR
```

### Use Cases

| ID | Use Case | Actor | Flow |
|----|----------|-------|------|
| UC-6.1 | **Create BOM** | Admin / Product Manager | Select a finished good → Add raw material lines (item + qty per unit) → System calculates total unit cost → Save as Draft |
| UC-6.2 | **Activate BOM** | Admin / Product Manager | Open draft BOM → Review → Mark as Active → This becomes the current BOM for the finished good |
| UC-6.3 | **Edit BOM** | Admin / Product Manager | Open active BOM → System auto-increments version → User edits material lines → Save as new active version; old version archived |
| UC-6.4 | **Explode BOM for Order** | System (Auto) | Sales order created for 100 units of FG-0001 → System multiplies BOM quantities by 100 → Returns total raw material requirements |
| UC-6.5 | **Calculate BOM Cost** | Any User | User views BOM → System sums (qty × standard_cost) for all lines → Displays total material cost per unit |
| UC-6.6 | **Copy BOM** | Admin / Product Manager | User selects existing BOM → Clicks "Copy to new product" → Selects target finished good → BOM lines cloned |
| UC-6.7 | **View BOM History** | Any User | User opens finished good → Views all BOM versions with dates and changes |
| UC-6.8 | **Where-Used Report** | Any User | User selects raw material → System lists all active BOMs containing it |

### Key API Endpoints

```
GET    /api/boms                          — List all BOMs (paginated, filterable by product/status)
POST   /api/boms                          — Create a new BOM
GET    /api/boms/{id}                     — Get BOM by ID (includes lines)
PUT    /api/boms/{id}                     — Update BOM (auto-versions)
PATCH  /api/boms/{id}/status              — Change status (DRAFT → ACTIVE → ARCHIVED)
POST   /api/boms/{id}/copy                — Copy BOM to a new product
GET    /api/boms/product/{productId}      — Get active BOM for a finished good
POST   /api/boms/{id}/explode             — Explode BOM for a given quantity
GET    /api/boms/{id}/versions            — List all versions of this BOM
```

### Data Model

```
TABLE: oc_boms
  - id              NUMBER          PK (sequence: oc_boms_seq)
  - product_id      NUMBER          FK → oc_items(id), NOT NULL   -- Must be FINISHED_GOOD
  - version         NUMBER          NOT NULL DEFAULT 1
  - status          VARCHAR2(20)    DEFAULT 'DRAFT' CHECK (status IN ('DRAFT', 'ACTIVE', 'ARCHIVED'))
  - total_unit_cost NUMBER(12,2)    -- Auto-calculated
  - notes           VARCHAR2(500)
  - created_by      NUMBER          FK → oc_users(id)
  - created_at      TIMESTAMP       DEFAULT SYSTIMESTAMP
  - updated_by      NUMBER          FK → oc_users(id)
  - updated_at      TIMESTAMP
  - UNIQUE (product_id, version)

TABLE: oc_bom_lines
  - id              NUMBER          PK (sequence: oc_bom_lines_seq)
  - bom_id          NUMBER          FK → oc_boms(id), NOT NULL
  - raw_material_id NUMBER          FK → oc_items(id), NOT NULL   -- Must be RAW_MATERIAL
  - quantity        NUMBER(10,4)    NOT NULL                       -- Qty per 1 unit of finished good
  - uom             VARCHAR2(10)    NOT NULL
  - notes           VARCHAR2(255)
  - UNIQUE (bom_id, raw_material_id)
```

---

## 11. Module 7 — Sales Order Management

### Purpose

Core module for capturing, tracking, and managing customer orders for finished goods. A sales order represents a customer's intent to purchase products and drives the downstream processes — BOM explosion, purchase order generation, invoicing, and payment tracking.

### Functionality

| Feature | Description |
|---------|-------------|
| **SO Creation** | Create a sales order with customer, order date, expected delivery date, and line items (finished goods + quantities) |
| **SO Number** | Auto-generated unique SO number (e.g., `SO-2026-0001`) with year prefix |
| **Line Items** | Multiple finished goods per order; each line has product, quantity, unit price, line total |
| **Auto-Price** | Unit price auto-populated from product's selling price; can be overridden |
| **Order Total** | Auto-calculated sum of all line totals + applicable taxes |
| **Tax Calculation** | Configurable tax rate (GST/VAT) applied to order total |
| **SO Status Lifecycle** | `DRAFT` → `PENDING_APPROVAL` → `APPROVED` → `IN_PROGRESS` → `COMPLETED` → `CANCELLED` |
| **BOM Explosion on Approve** | When SO is approved, system auto-explodes BOMs and calculates total raw material requirements |
| **PO Suggestion** | After BOM explosion, system identifies raw material shortfalls (required vs. available stock) and suggests purchase orders |
| **SO Edit** | Editable while in DRAFT or PENDING_APPROVAL status; locked once APPROVED |
| **SO Cancel** | Cancel an order with mandatory reason; reverses any stock reservations |
| **SO Search & Filter** | Filter by status, customer, date range; search by SO number |
| **SO Print / PDF** | Generate printable PDF of the sales order |

### Status Lifecycle

```
DRAFT → PENDING_APPROVAL → APPROVED → IN_PROGRESS → COMPLETED
                 ↓                                      
              REJECTED                                  
    ↓                              ↓
 CANCELLED                     CANCELLED
```

### Use Cases

| ID | Use Case | Actor | Flow |
|----|----------|-------|------|
| UC-7.1 | **Create Sales Order** | Sales User | Select customer → Add line items (finished good + qty) → Prices auto-fill → Review totals → Save as DRAFT |
| UC-7.2 | **Submit for Approval** | Sales User | Open draft SO → Click "Submit" → Status changes to PENDING_APPROVAL → Appears in approver's queue |
| UC-7.3 | **Approve Sales Order** | Approver | Open pending SO → Review details → Approve → Status = APPROVED → System triggers BOM explosion |
| UC-7.4 | **Reject Sales Order** | Approver | Open pending SO → Reject with comments → Status = REJECTED → Returns to creator for revision |
| UC-7.5 | **BOM Explosion** | System (Auto) | On approval → For each line item, fetch active BOM → Multiply quantities → Aggregate all raw material requirements |
| UC-7.6 | **View Material Shortfall** | Procurement User | After BOM explosion → System compares required quantities vs. current inventory → Displays shortfall report |
| UC-7.7 | **Generate PO from SO** | Procurement User | From shortfall report → Select materials → Click "Create Purchase Order" → PO auto-populated with quantities and suggested vendors |
| UC-7.8 | **Mark In Progress** | Operations User | Approved SO → Manufacturing begins → User marks SO as IN_PROGRESS |
| UC-7.9 | **Complete Sales Order** | Operations User | All items manufactured and ready → User marks SO as COMPLETED → Eligible for invoicing |
| UC-7.10 | **Cancel Sales Order** | Admin / Sales User | Open SO → Cancel with reason → Status = CANCELLED → Stock reservations reversed |
| UC-7.11 | **Print Sales Order** | Any User | Open SO → Click "Print/PDF" → System generates formatted PDF |

### Key API Endpoints

```
GET    /api/sales-orders                              — List all SOs (paginated, filterable)
POST   /api/sales-orders                              — Create a new SO
GET    /api/sales-orders/{id}                         — Get SO by ID (includes lines)
PUT    /api/sales-orders/{id}                         — Update SO (DRAFT/PENDING only)
PATCH  /api/sales-orders/{id}/status                  — Change SO status
POST   /api/sales-orders/{id}/submit                  — Submit for approval
GET    /api/sales-orders/{id}/material-requirements   — Get BOM explosion results
GET    /api/sales-orders/{id}/shortfall               — Get material shortfall report
POST   /api/sales-orders/{id}/generate-po             — Auto-generate PO from shortfall
GET    /api/sales-orders/{id}/pdf                     — Download SO as PDF
GET    /api/sales-orders/search?q=                    — Search by SO number
```

### Data Model

```
TABLE: oc_sales_orders
  - id                  NUMBER          PK (sequence: oc_sales_orders_seq)
  - so_number           VARCHAR2(20)    UNIQUE, NOT NULL    -- Auto: SO-2026-0001
  - customer_id         NUMBER          FK → oc_customers(id), NOT NULL
  - order_date          DATE            NOT NULL DEFAULT SYSDATE
  - expected_delivery   DATE
  - status              VARCHAR2(20)    DEFAULT 'DRAFT'
                                        CHECK (status IN ('DRAFT','PENDING_APPROVAL',
                                        'APPROVED','REJECTED','IN_PROGRESS','COMPLETED','CANCELLED'))
  - subtotal            NUMBER(14,2)    -- Sum of line totals
  - tax_rate            NUMBER(5,2)     DEFAULT 18.00       -- GST %
  - tax_amount          NUMBER(14,2)    -- Calculated
  - total_amount        NUMBER(14,2)    -- subtotal + tax
  - cancel_reason       VARCHAR2(500)
  - notes               VARCHAR2(500)
  - created_by          NUMBER          FK → oc_users(id)
  - created_at          TIMESTAMP       DEFAULT SYSTIMESTAMP
  - updated_by          NUMBER          FK → oc_users(id)
  - updated_at          TIMESTAMP

TABLE: oc_sales_order_lines
  - id                  NUMBER          PK (sequence: oc_so_lines_seq)
  - sales_order_id      NUMBER          FK → oc_sales_orders(id), NOT NULL
  - product_id          NUMBER          FK → oc_items(id), NOT NULL   -- FINISHED_GOOD
  - quantity            NUMBER(10,2)    NOT NULL
  - unit_price          NUMBER(12,2)    NOT NULL
  - line_total          NUMBER(14,2)    NOT NULL    -- quantity × unit_price
  - notes               VARCHAR2(255)
  - UNIQUE (sales_order_id, product_id)
```

---

## 12. Module 8 — Purchase Order Management

### Purpose

Manages the procurement of raw materials from vendors. Purchase orders can be created manually or auto-generated from sales order BOM explosions when material shortfalls are detected.

### Functionality

| Feature | Description |
|---------|-------------|
| **PO Creation (Manual)** | Create a PO by selecting a vendor and adding raw material line items with quantities and unit costs |
| **PO Creation (Auto)** | System auto-generates POs from sales order material shortfall — groups items by suggested vendor |
| **PO Number** | Auto-generated unique PO number (e.g., `PO-2026-0001`) |
| **PO Status Lifecycle** | `DRAFT` → `PENDING_APPROVAL` → `APPROVED` → `ORDERED` → `PARTIALLY_RECEIVED` → `RECEIVED` → `CANCELLED` |
| **Goods Receipt** | Record receipt of materials against a PO — partial or full; updates inventory |
| **PO Matching** | Match received quantities against ordered quantities; flag discrepancies |
| **Multi-Step Approval** | POs above a configurable threshold require additional approval levels |
| **PO Search & Filter** | Filter by status, vendor, date range; search by PO number |
| **PO Print / PDF** | Generate printable PDF of the purchase order |
| **PO to Invoice Link** | Link received POs to vendor invoices (AP) |

### Status Lifecycle

```
DRAFT → PENDING_APPROVAL → APPROVED → ORDERED → PARTIALLY_RECEIVED → RECEIVED
                 ↓                                                        
              REJECTED                                                    
    ↓                                          ↓
 CANCELLED                                  CANCELLED
```

### Use Cases

| ID | Use Case | Actor | Flow |
|----|----------|-------|------|
| UC-8.1 | **Create Manual PO** | Procurement User | Select vendor → Add raw material lines (item + qty + unit cost) → Review total → Save as DRAFT |
| UC-8.2 | **Auto-Generate PO** | System / Procurement User | From SO shortfall report → System groups required materials by preferred vendor → Creates PO drafts → User reviews and submits |
| UC-8.3 | **Submit PO for Approval** | Procurement User | Open draft PO → Submit → Status = PENDING_APPROVAL → Enters approval workflow |
| UC-8.4 | **Approve PO** | Approver | Review PO details → Approve → Status = APPROVED |
| UC-8.5 | **Send PO to Vendor** | Procurement User | Mark approved PO as ORDERED → PO is now "live" and awaiting delivery |
| UC-8.6 | **Record Goods Receipt** | Warehouse / Procurement User | PO marked ORDERED → Goods arrive → User records received quantities per line item → Inventory updated |
| UC-8.7 | **Partial Receipt** | Warehouse User | Only some items received → Record partial quantities → Status = PARTIALLY_RECEIVED → Remaining items tracked |
| UC-8.8 | **Complete Receipt** | Warehouse User | All items received → Status = RECEIVED → PO is complete |
| UC-8.9 | **Cancel PO** | Admin / Procurement | Cancel PO with reason → If goods partially received, those remain in inventory |
| UC-8.10 | **Generate PDF** | Any User | Open PO → Click "Print/PDF" → System generates formatted purchase order PDF |

### Key API Endpoints

```
GET    /api/purchase-orders                              — List all POs (paginated, filterable)
POST   /api/purchase-orders                              — Create a new PO
GET    /api/purchase-orders/{id}                         — Get PO by ID (includes lines)
PUT    /api/purchase-orders/{id}                         — Update PO (DRAFT only)
PATCH  /api/purchase-orders/{id}/status                  — Change PO status
POST   /api/purchase-orders/{id}/submit                  — Submit for approval
POST   /api/purchase-orders/{id}/receive                 — Record goods receipt
GET    /api/purchase-orders/{id}/receipts                — View receipt history
GET    /api/purchase-orders/{id}/pdf                     — Download PO as PDF
```

### Data Model

```
TABLE: oc_purchase_orders
  - id                  NUMBER          PK (sequence: oc_purchase_orders_seq)
  - po_number           VARCHAR2(20)    UNIQUE, NOT NULL    -- Auto: PO-2026-0001
  - vendor_id           NUMBER          FK → oc_vendors(id), NOT NULL
  - sales_order_id      NUMBER          FK → oc_sales_orders(id)  -- NULL if manual PO
  - order_date          DATE            NOT NULL DEFAULT SYSDATE
  - expected_delivery   DATE
  - status              VARCHAR2(25)    DEFAULT 'DRAFT'
                                        CHECK (status IN ('DRAFT','PENDING_APPROVAL',
                                        'APPROVED','REJECTED','ORDERED',
                                        'PARTIALLY_RECEIVED','RECEIVED','CANCELLED'))
  - subtotal            NUMBER(14,2)
  - tax_rate            NUMBER(5,2)     DEFAULT 18.00
  - tax_amount          NUMBER(14,2)
  - total_amount        NUMBER(14,2)
  - cancel_reason       VARCHAR2(500)
  - notes               VARCHAR2(500)
  - created_by          NUMBER          FK → oc_users(id)
  - created_at          TIMESTAMP       DEFAULT SYSTIMESTAMP
  - updated_by          NUMBER          FK → oc_users(id)
  - updated_at          TIMESTAMP

TABLE: oc_purchase_order_lines
  - id                  NUMBER          PK (sequence: oc_po_lines_seq)
  - purchase_order_id   NUMBER          FK → oc_purchase_orders(id), NOT NULL
  - raw_material_id     NUMBER          FK → oc_items(id), NOT NULL   -- RAW_MATERIAL
  - ordered_quantity    NUMBER(10,2)    NOT NULL
  - received_quantity   NUMBER(10,2)    DEFAULT 0
  - unit_cost           NUMBER(12,2)    NOT NULL
  - line_total          NUMBER(14,2)    NOT NULL
  - notes               VARCHAR2(255)
  - UNIQUE (purchase_order_id, raw_material_id)

TABLE: oc_goods_receipts
  - id                  NUMBER          PK (sequence: oc_goods_receipts_seq)
  - purchase_order_id   NUMBER          FK → oc_purchase_orders(id), NOT NULL
  - receipt_date        DATE            NOT NULL DEFAULT SYSDATE
  - received_by         NUMBER          FK → oc_users(id)
  - notes               VARCHAR2(500)
  - created_at          TIMESTAMP       DEFAULT SYSTIMESTAMP

TABLE: oc_goods_receipt_lines
  - id                  NUMBER          PK (sequence: oc_gr_lines_seq)
  - goods_receipt_id    NUMBER          FK → oc_goods_receipts(id), NOT NULL
  - po_line_id          NUMBER          FK → oc_purchase_order_lines(id), NOT NULL
  - quantity_received   NUMBER(10,2)    NOT NULL
```

---

## 13. Module 9 — Inventory Management (Lightweight)

### Purpose

Tracks aggregate stock levels for all items (finished goods and raw materials). No warehouse zone/bin management — just a single stock quantity per item. Updated automatically by goods receipts (PO) and can be manually adjusted.

### Functionality

| Feature | Description |
|---------|-------------|
| **Stock Level View** | View current stock quantity for every item in a paginated list |
| **Auto-Update on Receipt** | When goods are received against a PO, inventory for those raw materials automatically increases |
| **Manual Adjustment** | Admin can manually adjust stock (increase/decrease) with a reason (e.g., damage, count correction) |
| **Low Stock Alerts** | Dashboard widget and list filter for items below their reorder level |
| **Stock History** | View a log of all stock movements (receipts, adjustments) for any item |
| **Stock Valuation** | Total inventory value = Σ (stock_quantity × standard_cost) for raw materials |
| **Availability Check** | API to check if sufficient stock exists for a BOM explosion (used by SO module) |

### Use Cases

| ID | Use Case | Actor | Flow |
|----|----------|-------|------|
| UC-9.1 | **View All Stock** | Any User (with `INVENTORY_VIEW`) | User navigates to Inventory page → Sees paginated list: Item Code, Name, Type, Current Stock, UOM, Reorder Level, Status |
| UC-9.2 | **Low Stock Filter** | Any User | User clicks "Low Stock" filter → System shows only items where current stock < reorder level |
| UC-9.3 | **Auto Stock Increase** | System (Auto) | Goods receipt recorded for PO → System increases inventory quantity for received raw materials |
| UC-9.4 | **Manual Stock Adjustment** | Admin | Admin selects item → Enters adjustment qty (+/-) and reason → System updates stock and logs the adjustment |
| UC-9.5 | **View Stock History** | Any User | User opens item → Clicks "Stock History" tab → Sees chronological list of all movements with source (PO receipt, adjustment) |
| UC-9.6 | **Check Availability** | System (Auto) | During BOM explosion → System queries current stock for each raw material → Returns available vs. required comparison |

### Key API Endpoints

```
GET    /api/inventory                       — List all stock levels (paginated, filterable)
GET    /api/inventory/{itemId}              — Get stock for a specific item
POST   /api/inventory/{itemId}/adjust       — Manual stock adjustment (+/- with reason)
GET    /api/inventory/{itemId}/history      — View stock movement history
GET    /api/inventory/low-stock             — List items below reorder level
GET    /api/inventory/valuation             — Total stock valuation summary
POST   /api/inventory/check-availability    — Check stock availability for a list of items + quantities
```

### Data Model

```
TABLE: oc_inventory
  - id              NUMBER          PK (sequence: oc_inventory_seq)
  - item_id         NUMBER          FK → oc_items(id), UNIQUE, NOT NULL
  - quantity        NUMBER(12,2)    NOT NULL DEFAULT 0
  - last_updated    TIMESTAMP       DEFAULT SYSTIMESTAMP

TABLE: oc_stock_movements
  - id              NUMBER          PK (sequence: oc_stock_movements_seq)
  - item_id         NUMBER          FK → oc_items(id), NOT NULL
  - movement_type   VARCHAR2(20)    NOT NULL CHECK (movement_type IN
                                    ('PO_RECEIPT', 'MANUAL_ADJUSTMENT', 'SO_CONSUMPTION'))
  - quantity_change  NUMBER(12,2)   NOT NULL    -- Positive = increase, Negative = decrease
  - quantity_after   NUMBER(12,2)   NOT NULL    -- Stock level after this movement
  - reference_type   VARCHAR2(20)               -- 'GOODS_RECEIPT', 'ADJUSTMENT', 'SALES_ORDER'
  - reference_id     NUMBER                     -- ID of the source document
  - reason           VARCHAR2(500)              -- Required for manual adjustments
  - created_by       NUMBER         FK → oc_users(id)
  - created_at       TIMESTAMP      DEFAULT SYSTIMESTAMP
```

---

## 14. Module 10 — Invoicing

### Purpose

Handles both **Accounts Receivable (AR)** — invoices sent to customers for sales orders — and **Accounts Payable (AP)** — invoices received from vendors for purchase orders. Supports full invoice lifecycle from creation to payment linkage.

### Functionality

| Feature | Description |
|---------|-------------|
| **Customer Invoice (AR)** | Generate invoices for completed (or approved) sales orders |
| **Vendor Invoice (AP)** | Record vendor invoices against received purchase orders |
| **Invoice Number** | Auto-generated: `INV-2026-0001` (AR) or `VINV-2026-0001` (AP) |
| **Invoice Status** | `DRAFT` → `PENDING_APPROVAL` → `APPROVED` → `SENT` → `PARTIALLY_PAID` → `PAID` → `OVERDUE` → `CANCELLED` |
| **Auto-Generate from SO** | One-click invoice generation from a completed sales order; lines auto-populated |
| **Manual Invoice** | Create standalone invoices not linked to a specific order |
| **Tax Calculation** | Auto-apply configurable tax rates (GST/VAT) |
| **Due Date** | Configurable payment terms (Net 30, Net 60, etc.); due date auto-calculated |
| **Overdue Detection** | System automatically marks invoices past due date as OVERDUE |
| **Invoice PDF** | Generate and download professional invoice PDFs |
| **Payment Linkage** | Track which payments are applied against which invoices |
| **Credit Notes** | Issue credit notes for returns or adjustments |

### Use Cases

| ID | Use Case | Actor | Flow |
|----|----------|-------|------|
| UC-10.1 | **Generate AR Invoice from SO** | Finance User | Open completed SO → Click "Generate Invoice" → System creates invoice with lines from SO → Review → Save as DRAFT |
| UC-10.2 | **Create Manual AR Invoice** | Finance User | New invoice → Select customer → Add line items manually → Set payment terms → Save |
| UC-10.3 | **Record Vendor Invoice (AP)** | Finance User | Vendor sends invoice → User creates AP invoice linked to received PO → Enters vendor invoice number, amount, due date |
| UC-10.4 | **Submit Invoice for Approval** | Finance User | Open draft invoice → Submit → Enters approval workflow |
| UC-10.5 | **Approve Invoice** | Approver | Review invoice → Approve → Status = APPROVED; for AR, ready to send to customer |
| UC-10.6 | **Mark as Sent** | Finance User | AR invoice approved → Mark as SENT → Starts the payment clock (due date) |
| UC-10.7 | **View Overdue Invoices** | Finance / Admin | Navigate to Invoices → Filter by OVERDUE → See all past-due invoices with days overdue |
| UC-10.8 | **Print Invoice PDF** | Any User | Open invoice → Click "Download PDF" → System generates formatted invoice document |
| UC-10.9 | **Issue Credit Note** | Finance User | Open an AR invoice → Issue credit note with reason and amount → Reduces outstanding balance |
| UC-10.10 | **View Invoice Aging** | Finance / Admin | Dashboard widget shows aging buckets: Current, 1-30 days, 31-60 days, 61-90 days, 90+ days |

### Key API Endpoints

```
GET    /api/invoices                           — List all invoices (filterable by type AR/AP, status)
POST   /api/invoices                           — Create a new invoice
GET    /api/invoices/{id}                      — Get invoice by ID (includes lines + payments)
PUT    /api/invoices/{id}                      — Update invoice (DRAFT only)
PATCH  /api/invoices/{id}/status               — Change invoice status
POST   /api/invoices/{id}/submit               — Submit for approval
POST   /api/sales-orders/{soId}/generate-invoice  — Auto-generate AR invoice from SO
GET    /api/invoices/{id}/pdf                  — Download invoice PDF
POST   /api/invoices/{id}/credit-note          — Issue credit note
GET    /api/invoices/overdue                   — List overdue invoices
GET    /api/invoices/aging                     — Aging report data
```

### Data Model

```
TABLE: oc_invoices
  - id                  NUMBER          PK (sequence: oc_invoices_seq)
  - invoice_number      VARCHAR2(25)    UNIQUE, NOT NULL    -- INV-2026-0001 or VINV-2026-0001
  - invoice_type        VARCHAR2(5)     NOT NULL CHECK (invoice_type IN ('AR', 'AP'))
  - customer_id         NUMBER          FK → oc_customers(id)   -- For AR
  - vendor_id           NUMBER          FK → oc_vendors(id)     -- For AP
  - sales_order_id      NUMBER          FK → oc_sales_orders(id)        -- Linked SO (AR)
  - purchase_order_id   NUMBER          FK → oc_purchase_orders(id)     -- Linked PO (AP)
  - vendor_invoice_ref  VARCHAR2(50)    -- Vendor's own invoice number (AP)
  - invoice_date        DATE            NOT NULL DEFAULT SYSDATE
  - due_date            DATE            NOT NULL
  - payment_terms       VARCHAR2(20)    DEFAULT 'NET_30'    -- NET_15, NET_30, NET_45, NET_60
  - status              VARCHAR2(20)    DEFAULT 'DRAFT'
                                        CHECK (status IN ('DRAFT','PENDING_APPROVAL',
                                        'APPROVED','SENT','PARTIALLY_PAID','PAID',
                                        'OVERDUE','CANCELLED'))
  - subtotal            NUMBER(14,2)
  - tax_rate            NUMBER(5,2)     DEFAULT 18.00
  - tax_amount          NUMBER(14,2)
  - total_amount        NUMBER(14,2)
  - paid_amount         NUMBER(14,2)    DEFAULT 0
  - outstanding         NUMBER(14,2)    -- total_amount - paid_amount
  - notes               VARCHAR2(500)
  - created_by          NUMBER          FK → oc_users(id)
  - created_at          TIMESTAMP       DEFAULT SYSTIMESTAMP
  - updated_by          NUMBER          FK → oc_users(id)
  - updated_at          TIMESTAMP

TABLE: oc_invoice_lines
  - id                  NUMBER          PK (sequence: oc_inv_lines_seq)
  - invoice_id          NUMBER          FK → oc_invoices(id), NOT NULL
  - item_id             NUMBER          FK → oc_items(id)
  - description         VARCHAR2(255)   NOT NULL
  - quantity            NUMBER(10,2)    NOT NULL
  - unit_price          NUMBER(12,2)    NOT NULL
  - line_total          NUMBER(14,2)    NOT NULL

TABLE: oc_credit_notes
  - id                  NUMBER          PK (sequence: oc_credit_notes_seq)
  - credit_note_number  VARCHAR2(25)    UNIQUE, NOT NULL    -- CN-2026-0001
  - invoice_id          NUMBER          FK → oc_invoices(id), NOT NULL
  - amount              NUMBER(14,2)    NOT NULL
  - reason              VARCHAR2(500)   NOT NULL
  - created_by          NUMBER          FK → oc_users(id)
  - created_at          TIMESTAMP       DEFAULT SYSTIMESTAMP
```

---

## 15. Module 11 — Payment Tracking

### Purpose

Records and tracks all monetary transactions — both **incoming payments** from customers (against AR invoices) and **outgoing payments** to vendors (against AP invoices). Supports partial payments, multiple payment methods, and automatic invoice status updates.

### Functionality

| Feature | Description |
|---------|-------------|
| **Record Customer Payment** | Record a payment received from a customer against one or more AR invoices |
| **Record Vendor Payment** | Record a payment made to a vendor against one or more AP invoices |
| **Payment Methods** | Cash, Bank Transfer, Cheque, UPI, Credit Card |
| **Partial Payments** | Apply a payment partially to an invoice; invoice status updates to PARTIALLY_PAID |
| **Full Payment** | When outstanding = 0, invoice auto-updates to PAID |
| **Payment Reference** | Transaction reference number (bank ref, cheque number, UPI ID) |
| **Payment History** | View all payments linked to an invoice, customer, or vendor |
| **Unapplied Payments** | Record payments not yet linked to a specific invoice (advance payments) |
| **Payment Reversal** | Reverse a payment (bounced cheque, chargeback) with reason |
| **AR/AP Summary** | Dashboard views for total receivable, total payable, and cash flow |

### Use Cases

| ID | Use Case | Actor | Flow |
|----|----------|-------|------|
| UC-11.1 | **Record Customer Payment** | Finance User | Select customer → Select AR invoices → Enter payment amount, method, reference → System applies payment → Invoice status updates |
| UC-11.2 | **Record Vendor Payment** | Finance User | Select vendor → Select AP invoices → Enter payment amount, method, reference → System records outgoing payment |
| UC-11.3 | **Partial Payment** | Finance User | Customer pays ₹5,000 against ₹10,000 invoice → System records payment → Invoice status = PARTIALLY_PAID → Outstanding = ₹5,000 |
| UC-11.4 | **View Payment History** | Finance User | Open invoice → Payment tab shows all payments applied with dates, amounts, methods |
| UC-11.5 | **Record Advance Payment** | Finance User | Customer pays before invoice → Record as unapplied payment → Later link to invoice when generated |
| UC-11.6 | **Reverse Payment** | Admin / Finance | Cheque bounces → User selects payment → Reverses it with reason → Invoice outstanding restored |
| UC-11.7 | **AR/AP Dashboard** | Finance / Admin | Dashboard shows: Total Receivable, Total Payable, Overdue Receivable, Payments This Month |

### Key API Endpoints

```
GET    /api/payments                            — List all payments (filterable by type, date range)
POST   /api/payments                            — Record a new payment
GET    /api/payments/{id}                       — Get payment details
POST   /api/payments/{id}/reverse               — Reverse a payment
GET    /api/payments/customer/{customerId}      — Payment history for a customer
GET    /api/payments/vendor/{vendorId}           — Payment history for a vendor
GET    /api/payments/invoice/{invoiceId}        — Payments applied to an invoice
GET    /api/payments/summary                    — AR/AP summary (totals, overdue)
```

### Data Model

```
TABLE: oc_payments
  - id                  NUMBER          PK (sequence: oc_payments_seq)
  - payment_number      VARCHAR2(25)    UNIQUE, NOT NULL    -- PAY-2026-0001
  - payment_type        VARCHAR2(10)    NOT NULL CHECK (payment_type IN ('INCOMING', 'OUTGOING'))
  - customer_id         NUMBER          FK → oc_customers(id)   -- For INCOMING
  - vendor_id           NUMBER          FK → oc_vendors(id)     -- For OUTGOING
  - payment_date        DATE            NOT NULL DEFAULT SYSDATE
  - amount              NUMBER(14,2)    NOT NULL
  - payment_method      VARCHAR2(20)    NOT NULL
                                        CHECK (payment_method IN ('CASH','BANK_TRANSFER',
                                        'CHEQUE','UPI','CREDIT_CARD'))
  - reference_number    VARCHAR2(100)                       -- Bank ref / cheque no / UPI ID
  - is_reversed         NUMBER(1)       DEFAULT 0
  - reversal_reason     VARCHAR2(500)
  - reversal_date       DATE
  - notes               VARCHAR2(500)
  - created_by          NUMBER          FK → oc_users(id)
  - created_at          TIMESTAMP       DEFAULT SYSTIMESTAMP

TABLE: oc_payment_applications
  - id                  NUMBER          PK (sequence: oc_payment_apps_seq)
  - payment_id          NUMBER          FK → oc_payments(id), NOT NULL
  - invoice_id          NUMBER          FK → oc_invoices(id), NOT NULL
  - applied_amount      NUMBER(14,2)    NOT NULL
  - created_at          TIMESTAMP       DEFAULT SYSTIMESTAMP
```

---

## 16. Module 12 — Approval Workflow Engine

### Purpose

Provides a configurable, multi-step approval system that controls the progression of key business documents — sales orders, purchase orders, and invoices — through defined approval chains before they become actionable.

### Functionality

| Feature | Description |
|---------|-------------|
| **Workflow Definition** | Admin defines approval workflows per document type (SO, PO, Invoice) |
| **Multi-Step Chains** | Each workflow has ordered steps; each step has an approver (user or role) |
| **Threshold-Based Routing** | Different approval chains based on document amount (e.g., PO < ₹50K = 1 approver; PO ≥ ₹50K = 2 approvers) |
| **Approval Queue** | Each approver sees their pending approvals in a unified queue |
| **Approve / Reject** | Approver can approve (advances to next step or completes) or reject (returns to submitter) with comments |
| **Approval History** | Full log of who approved/rejected and when, with comments |
| **Skip/Override** | Admin can override and force-approve a stuck workflow |
| **Auto-Approve** | Optional: documents below a minimum threshold auto-approve |

### Workflow Configuration Model

```
Example: Purchase Order Approval Workflow

Workflow: "PO Approval"
  Condition: PO.total_amount >= 10000
  Steps:
    Step 1: Role = "PROCUREMENT_MANAGER"  (approve/reject)
    Step 2: Role = "FINANCE_HEAD"         (approve/reject, only if amount >= 50000)

Workflow: "PO Auto-Approve"
  Condition: PO.total_amount < 10000
  Steps: (none — auto-approved)
```

### Use Cases

| ID | Use Case | Actor | Flow |
|----|----------|-------|------|
| UC-12.1 | **Configure Workflow** | Admin | Admin goes to Settings → Approval Workflows → Creates workflow for document type → Defines steps with approver roles and optional amount thresholds |
| UC-12.2 | **Submit for Approval** | Any User | User submits document (SO/PO/Invoice) → System evaluates which workflow applies based on document type and amount → Creates approval request at Step 1 |
| UC-12.3 | **View Approval Queue** | Approver | Approver opens "My Approvals" page → Sees all pending items across all document types → Can sort/filter |
| UC-12.4 | **Approve Step** | Approver | Approver opens pending item → Reviews document → Clicks Approve with optional comment → If more steps, advances to next; if last step, document status = APPROVED |
| UC-12.5 | **Reject** | Approver | Approver rejects with mandatory comment → Document status = REJECTED → Returns to submitter |
| UC-12.6 | **View Approval History** | Any User | Open any document → Approval History tab shows full chain: who submitted, each step's approver, action, timestamp, comments |
| UC-12.7 | **Admin Override** | Admin | Document stuck in approval → Admin force-approves → Logged as override in audit trail |

### Key API Endpoints

```
GET    /api/approvals/pending                — List pending approvals for current user
GET    /api/approvals/history/{docType}/{id} — Approval history for a document
POST   /api/approvals/{id}/approve           — Approve a pending step
POST   /api/approvals/{id}/reject            — Reject a pending step
POST   /api/approvals/{id}/override          — Admin force-approve

GET    /api/approval-workflows               — List all configured workflows
POST   /api/approval-workflows               — Create a workflow
PUT    /api/approval-workflows/{id}          — Update a workflow
DELETE /api/approval-workflows/{id}          — Delete a workflow
```

### Data Model

```
TABLE: oc_approval_workflows
  - id                  NUMBER          PK (sequence: oc_approval_wf_seq)
  - name                VARCHAR2(100)   NOT NULL
  - document_type       VARCHAR2(20)    NOT NULL CHECK (document_type IN
                                        ('SALES_ORDER', 'PURCHASE_ORDER', 'INVOICE'))
  - min_amount          NUMBER(14,2)    DEFAULT 0       -- Workflow applies when amount >= this
  - max_amount          NUMBER(14,2)                    -- NULL = no upper limit
  - is_active           NUMBER(1)       DEFAULT 1
  - created_at          TIMESTAMP       DEFAULT SYSTIMESTAMP

TABLE: oc_approval_workflow_steps
  - id                  NUMBER          PK (sequence: oc_approval_ws_seq)
  - workflow_id         NUMBER          FK → oc_approval_workflows(id), NOT NULL
  - step_order          NUMBER          NOT NULL        -- 1, 2, 3...
  - approver_role_id    NUMBER          FK → oc_roles(id)       -- Role-based
  - approver_user_id    NUMBER          FK → oc_users(id)       -- Or specific user
  - UNIQUE (workflow_id, step_order)

TABLE: oc_approval_requests
  - id                  NUMBER          PK (sequence: oc_approval_req_seq)
  - document_type       VARCHAR2(20)    NOT NULL
  - document_id         NUMBER          NOT NULL        -- FK to SO/PO/Invoice
  - workflow_id         NUMBER          FK → oc_approval_workflows(id)
  - current_step        NUMBER          NOT NULL DEFAULT 1
  - status              VARCHAR2(20)    DEFAULT 'PENDING'
                                        CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED', 'OVERRIDDEN'))
  - submitted_by        NUMBER          FK → oc_users(id)
  - submitted_at        TIMESTAMP       DEFAULT SYSTIMESTAMP

TABLE: oc_approval_actions
  - id                  NUMBER          PK (sequence: oc_approval_act_seq)
  - request_id          NUMBER          FK → oc_approval_requests(id), NOT NULL
  - step_order          NUMBER          NOT NULL
  - action              VARCHAR2(20)    NOT NULL CHECK (action IN ('APPROVED', 'REJECTED', 'OVERRIDDEN'))
  - acted_by            NUMBER          FK → oc_users(id), NOT NULL
  - comments            VARCHAR2(500)
  - acted_at            TIMESTAMP       DEFAULT SYSTIMESTAMP
```

---

## 17. Module 13 — Dashboard & Reporting

### Purpose

Provides real-time visual dashboards and exportable reports giving stakeholders a bird's-eye view of the business — order pipeline, revenue, procurement status, payment health, and inventory status.

### Functionality

#### Dashboard Widgets

| Widget | Description | Chart Type |
|--------|-------------|------------|
| **Order Pipeline** | Count of sales orders by status (Draft, Pending, Approved, In Progress, Completed) | Horizontal bar chart |
| **Revenue Overview** | Monthly revenue (invoiced amount) trend over the past 12 months | Line chart |
| **Top 5 Customers** | Customers with highest order value in selected period | Bar chart |
| **Purchase Order Status** | PO count by status | Doughnut chart |
| **Accounts Receivable** | Total outstanding AR amount + aging breakdown | Stacked bar chart |
| **Accounts Payable** | Total outstanding AP amount + aging breakdown | Stacked bar chart |
| **Overdue Payments** | Count and total value of overdue invoices (AR + AP) | KPI cards |
| **Low Stock Items** | Items below reorder level | Data table widget |
| **Monthly Cash Flow** | Incoming vs. outgoing payments per month | Grouped bar chart |
| **Pending Approvals** | Count of items awaiting current user's approval | KPI card with link |
| **Recent Activity** | Latest 10 actions across the system | Activity feed |

#### Reports

| Report | Description | Export Formats |
|--------|-------------|---------------|
| **Sales Order Report** | All SOs in date range with status, customer, amounts | PDF, CSV |
| **Purchase Order Report** | All POs in date range with status, vendor, amounts | PDF, CSV |
| **Invoice Aging Report** | AR/AP invoices grouped by aging buckets (Current, 30, 60, 90+) | PDF, CSV |
| **Payment Report** | All payments in date range with method, reference, linked invoices | PDF, CSV |
| **Inventory Stock Report** | Current stock levels, valuation, low stock flags | PDF, CSV |
| **BOM Cost Report** | Product-wise BOM cost breakdown | PDF, CSV |
| **Customer Statement** | All invoices and payments for a customer in date range | PDF |
| **Vendor Statement** | All POs, invoices, and payments for a vendor in date range | PDF |

### Use Cases

| ID | Use Case | Actor | Flow |
|----|----------|-------|------|
| UC-13.1 | **View Dashboard** | Any User (with `DASHBOARD_VIEW`) | User logs in → Dashboard is the landing page → Widgets load with real-time data |
| UC-13.2 | **Filter Dashboard by Date** | Any User | User selects date range from date picker → All widgets refresh with filtered data |
| UC-13.3 | **Drill Down from Widget** | Any User | User clicks a bar in "Order Pipeline" chart → Navigates to Sales Orders list pre-filtered by that status |
| UC-13.4 | **Export Report to PDF** | Any User (with `REPORT_EXPORT`) | User navigates to Reports → Selects report type → Sets filters → Clicks "Export PDF" → Downloads |
| UC-13.5 | **Export Report to CSV** | Any User (with `REPORT_EXPORT`) | Same as above but clicks "Export CSV" → Downloads spreadsheet-compatible file |
| UC-13.6 | **View Customer Statement** | Finance User | Select customer → Set date range → Generate statement showing all invoices, payments, outstanding balance |

### Key API Endpoints

```
GET    /api/dashboard/order-pipeline        — SO status counts
GET    /api/dashboard/revenue-trend         — Monthly revenue data
GET    /api/dashboard/top-customers         — Top customers by value
GET    /api/dashboard/po-status             — PO status counts
GET    /api/dashboard/ar-aging              — AR aging buckets
GET    /api/dashboard/ap-aging              — AP aging buckets
GET    /api/dashboard/overdue-summary       — Overdue KPIs
GET    /api/dashboard/cash-flow             — Monthly cash flow data
GET    /api/dashboard/low-stock             — Low stock items
GET    /api/dashboard/pending-approvals     — Current user's pending approvals count
GET    /api/dashboard/recent-activity       — Latest system activity

GET    /api/reports/sales-orders            — Generate SO report (params: dateFrom, dateTo, format)
GET    /api/reports/purchase-orders         — Generate PO report
GET    /api/reports/invoice-aging           — Generate aging report
GET    /api/reports/payments                — Generate payment report
GET    /api/reports/inventory               — Generate stock report
GET    /api/reports/bom-cost                — Generate BOM cost report
GET    /api/reports/customer-statement/{id} — Generate customer statement
GET    /api/reports/vendor-statement/{id}   — Generate vendor statement
```

---

## 18. Module 14 — Audit Trail

### Purpose

Provides basic traceability by recording who created and last modified every record in the system. Uses a standardized `created_by`, `created_at`, `updated_by`, `updated_at` pattern across all tables.

### Functionality

| Feature | Description |
|---------|-------------|
| **Created By / At** | Every record stores the user ID and timestamp of creation |
| **Updated By / At** | Every record stores the user ID and timestamp of last modification |
| **JPA Auditing** | Spring Data JPA's `@CreatedBy`, `@CreatedDate`, `@LastModifiedBy`, `@LastModifiedDate` annotations auto-populate these fields |
| **Auditable Base Entity** | All entities extend a common `AuditableEntity` base class |
| **Display in UI** | Every detail page shows "Created by [user] on [date]" and "Last updated by [user] on [date]" |

### Implementation

```java
// Base entity that all auditable entities extend
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AuditableEntity {

    @CreatedBy
    @Column(name = "created_by", updatable = false)
    private Long createdBy;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @LastModifiedBy
    @Column(name = "updated_by")
    private Long updatedBy;

    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt;
}
```

### Use Cases

| ID | Use Case | Actor | Flow |
|----|----------|-------|------|
| UC-14.1 | **View Record Metadata** | Any User | User opens any record (SO, PO, Invoice, etc.) → Footer shows "Created by Admin on 15 Sep 2026, Last updated by John on 20 Sep 2026" |
| UC-14.2 | **Audit Check** | Admin | Admin can filter records by created_by or updated_by to see what a specific user has done |

---

## 19. Module 15 — System Configuration

### Purpose

Central settings module for system-wide configurations that affect the behavior of other modules. Admin-only access.

### Functionality

| Feature | Description |
|---------|-------------|
| **Tax Configuration** | Set default tax rate (GST %), tax name, and whether tax is inclusive or exclusive |
| **Payment Terms** | Define available payment terms (NET_15, NET_30, NET_45, NET_60, IMMEDIATE) |
| **Number Sequences** | Configure prefix and starting number for document numbers (SO, PO, INV, etc.) |
| **Company Profile** | Company name, logo, address, GSTIN — displayed on invoices and PDFs |
| **Currency Settings** | Default currency, symbol, decimal places |
| **UOM Management** | Add/edit/deactivate units of measure |
| **Category Management** | Add/edit/deactivate item categories |
| **Approval Thresholds** | Configure amount thresholds for approval routing (linked to Approval Workflow module) |

### Use Cases

| ID | Use Case | Actor | Flow |
|----|----------|-------|------|
| UC-15.1 | **Update Tax Rate** | Admin | Admin goes to Settings → Tax → Updates GST rate from 18% to 12% → All new documents use the new rate |
| UC-15.2 | **Set Company Profile** | Admin | Admin enters company name, address, GSTIN, uploads logo → This info appears on all generated PDFs |
| UC-15.3 | **Add Payment Term** | Admin | Admin adds "NET_90" as a new payment term → Available in invoice creation dropdown |
| UC-15.4 | **Configure Number Sequence** | Admin | Admin changes SO prefix from "SO-" to "SLO-" and next number to 5000 → Next SO will be SLO-2026-5000 |

### Key API Endpoints

```
GET    /api/config                   — Get all system configuration
PUT    /api/config/tax               — Update tax settings
PUT    /api/config/company           — Update company profile
GET    /api/config/payment-terms     — List payment terms
POST   /api/config/payment-terms     — Add payment term
GET    /api/config/uoms              — List UOMs
POST   /api/config/uoms              — Add UOM
GET    /api/config/categories        — List item categories
POST   /api/config/categories        — Add category
PUT    /api/config/sequences         — Update number sequences
```

### Data Model

```
TABLE: oc_system_config
  - id              NUMBER          PK
  - config_key      VARCHAR2(50)    UNIQUE, NOT NULL
  - config_value    VARCHAR2(500)   NOT NULL
  - description     VARCHAR2(255)
  - updated_by      NUMBER          FK → oc_users(id)
  - updated_at      TIMESTAMP       DEFAULT SYSTIMESTAMP

TABLE: oc_payment_terms
  - id              NUMBER          PK (sequence: oc_payment_terms_seq)
  - code            VARCHAR2(20)    UNIQUE, NOT NULL    -- NET_30
  - label           VARCHAR2(50)    NOT NULL             -- "Net 30 Days"
  - days            NUMBER          NOT NULL             -- 30
  - is_active       NUMBER(1)       DEFAULT 1

TABLE: oc_uom
  - id              NUMBER          PK (sequence: oc_uom_seq)
  - code            VARCHAR2(10)    UNIQUE, NOT NULL    -- PCS, KG
  - name            VARCHAR2(50)    NOT NULL             -- "Pieces", "Kilograms"
  - is_active       NUMBER(1)       DEFAULT 1

TABLE: oc_categories
  - id              NUMBER          PK (sequence: oc_categories_seq)
  - name            VARCHAR2(50)    UNIQUE, NOT NULL
  - is_active       NUMBER(1)       DEFAULT 1

TABLE: oc_number_sequences
  - id              NUMBER          PK
  - document_type   VARCHAR2(20)    UNIQUE, NOT NULL    -- SALES_ORDER, PURCHASE_ORDER, etc.
  - prefix          VARCHAR2(10)    NOT NULL             -- SO-, PO-, INV-
  - include_year    NUMBER(1)       DEFAULT 1            -- Include year in number
  - next_number     NUMBER          NOT NULL DEFAULT 1
```

---

## 20. Order-to-Cash Workflow

The complete end-to-end business process automated by OrderCraft:

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                     ORDER-TO-CASH WORKFLOW                                   │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  1. CUSTOMER PLACES ORDER                                                   │
│     └── Sales user creates Sales Order (SO) with finished goods             │
│         └── SO saved as DRAFT                                               │
│                                                                             │
│  2. ORDER APPROVAL                                                          │
│     └── SO submitted for approval                                           │
│         └── Approval workflow evaluates (amount-based routing)              │
│             ├── Approved → SO status = APPROVED                             │
│             └── Rejected → Returns to sales user for revision               │
│                                                                             │
│  3. BOM EXPLOSION                                                           │
│     └── System auto-explodes BOMs for all SO line items                     │
│         └── Calculates total raw material requirements                      │
│             └── Compares against current inventory                          │
│                 └── Identifies material shortfalls                          │
│                                                                             │
│  4. PROCUREMENT                                                             │
│     └── Purchase Orders auto-generated for shortfall quantities             │
│         └── POs grouped by vendor → Submitted for approval                  │
│             └── Approved POs sent to vendors (status = ORDERED)             │
│                                                                             │
│  5. GOODS RECEIPT                                                           │
│     └── Raw materials arrive from vendors                                   │
│         └── Goods receipt recorded against PO                               │
│             └── Inventory automatically updated                             │
│                 └── PO status → PARTIALLY_RECEIVED / RECEIVED               │
│                                                                             │
│  6. MANUFACTURING (External)                                                │
│     └── Production happens outside OrderCraft                               │
│         └── SO marked as IN_PROGRESS → then COMPLETED                       │
│                                                                             │
│  7. INVOICING                                                               │
│     ├── AR: Customer invoice generated from completed SO                    │
│     │   └── Invoice submitted for approval → Approved → Sent to customer    │
│     └── AP: Vendor invoices recorded against received POs                   │
│         └── Vendor invoice submitted for approval → Approved                │
│                                                                             │
│  8. PAYMENT                                                                 │
│     ├── INCOMING: Customer pays against AR invoices                         │
│     │   └── Payment recorded → Invoice status updates                       │
│     │       (PARTIALLY_PAID → PAID)                                         │
│     └── OUTGOING: Company pays vendors against AP invoices                  │
│         └── Payment recorded → Vendor invoice status updates                │
│                                                                             │
│  9. COMPLETE                                                                │
│     └── SO fully delivered, invoiced, and paid                              │
│         └── Revenue recognized → Reflected in dashboard                     │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 21. Database Schema Overview

### Entity Relationship Summary

```
oc_users ──────────── oc_user_roles ──────────── oc_roles
                                                    │
                                            oc_role_permissions
                                                    │
                                              oc_permissions

oc_customers ─────── oc_sales_orders ─────── oc_sales_order_lines ─────── oc_items
                           │                                                  │
                           │                                            oc_boms ── oc_bom_lines
                           │                                                  │
oc_vendors ──────── oc_purchase_orders ──── oc_purchase_order_lines ──────────┘
                           │
                    oc_goods_receipts ── oc_goods_receipt_lines
                                               │
                                         oc_inventory ── oc_stock_movements

oc_invoices ─────── oc_invoice_lines
     │
oc_payment_applications ─── oc_payments

oc_approval_workflows ── oc_approval_workflow_steps
         │
oc_approval_requests ── oc_approval_actions
```

### All Tables

| # | Table | Module | Records |
|---|-------|--------|---------|
| 1 | `oc_users` | User Management | System users |
| 2 | `oc_roles` | User Management | Roles (Admin, General User, custom) |
| 3 | `oc_permissions` | User Management | Granular permissions |
| 4 | `oc_role_permissions` | User Management | Role ↔ Permission mapping |
| 5 | `oc_user_roles` | User Management | User ↔ Role mapping |
| 6 | `oc_token_blacklist` | Authentication | Invalidated JWT tokens |
| 7 | `oc_customers` | Customer Management | Customer master |
| 8 | `oc_vendors` | Vendor Management | Vendor master |
| 9 | `oc_items` | Product & Item Master | Finished goods + raw materials |
| 10 | `oc_boms` | Bill of Materials | BOM headers |
| 11 | `oc_bom_lines` | Bill of Materials | BOM line items |
| 12 | `oc_sales_orders` | Sales Order | SO headers |
| 13 | `oc_sales_order_lines` | Sales Order | SO line items |
| 14 | `oc_purchase_orders` | Purchase Order | PO headers |
| 15 | `oc_purchase_order_lines` | Purchase Order | PO line items |
| 16 | `oc_goods_receipts` | Purchase Order | Goods receipt headers |
| 17 | `oc_goods_receipt_lines` | Purchase Order | Goods receipt line items |
| 18 | `oc_inventory` | Inventory | Current stock levels |
| 19 | `oc_stock_movements` | Inventory | Stock movement history |
| 20 | `oc_invoices` | Invoicing | Invoice headers (AR + AP) |
| 21 | `oc_invoice_lines` | Invoicing | Invoice line items |
| 22 | `oc_credit_notes` | Invoicing | Credit notes |
| 23 | `oc_payments` | Payment Tracking | Payment records |
| 24 | `oc_payment_applications` | Payment Tracking | Payment ↔ Invoice linkage |
| 25 | `oc_approval_workflows` | Approval Engine | Workflow definitions |
| 26 | `oc_approval_workflow_steps` | Approval Engine | Workflow steps |
| 27 | `oc_approval_requests` | Approval Engine | Active approval requests |
| 28 | `oc_approval_actions` | Approval Engine | Approval/rejection actions |
| 29 | `oc_system_config` | System Config | Key-value config store |
| 30 | `oc_payment_terms` | System Config | Payment term definitions |
| 31 | `oc_uom` | System Config | Units of measure |
| 32 | `oc_categories` | System Config | Item categories |
| 33 | `oc_number_sequences` | System Config | Document number sequences |

---

## 22. API Design Conventions

### General Rules

| Convention | Standard |
|------------|----------|
| **Base Path** | `/api/` |
| **Naming** | Kebab-case for URLs: `/api/sales-orders` |
| **HTTP Methods** | `GET` (read), `POST` (create), `PUT` (full update), `PATCH` (partial update), `DELETE` (remove) |
| **Pagination** | `?page=0&size=20&sort=createdAt,desc` |
| **Filtering** | Query params: `?status=APPROVED&customerId=5&dateFrom=2026-01-01` |
| **Search** | `?q=search-term` for text search |
| **Versioning** | No API versioning for v1; future: `/api/v2/` |
| **Auth Header** | `Authorization: Bearer <jwt-token>` |
| **Content Type** | `application/json` |

### Standard Response Format

```json
// Success (single object)
{
  "data": { ... },
  "message": "Sales order created successfully"
}

// Success (paginated list)
{
  "data": [ ... ],
  "page": 0,
  "size": 20,
  "totalElements": 150,
  "totalPages": 8
}

// Error
{
  "error": "VALIDATION_ERROR",
  "message": "Validation failed",
  "details": [
    { "field": "quantity", "message": "must be greater than 0" }
  ],
  "timestamp": "2026-09-28T14:00:00Z"
}
```

### HTTP Status Codes

| Code | Usage |
|------|-------|
| `200` | Successful GET, PUT, PATCH |
| `201` | Successful POST (created) |
| `204` | Successful DELETE (no content) |
| `400` | Validation error, bad request |
| `401` | Unauthorized (missing/invalid token) |
| `403` | Forbidden (insufficient permissions) |
| `404` | Resource not found |
| `409` | Conflict (duplicate, invalid state transition) |
| `500` | Internal server error |

---

## 23. Non-Functional Requirements

| Category | Requirement |
|----------|-------------|
| **Performance** | API response time < 500ms for CRUD operations; dashboard loads in < 2 seconds |
| **Scalability** | Support up to 100 concurrent users; 10,000+ orders per year |
| **Security** | JWT-based auth; bcrypt password hashing; HTTPS in production; CORS configured; SQL injection prevention via parameterized queries (JPA) |
| **Data Integrity** | Foreign key constraints; database transactions for multi-table operations; optimistic locking for concurrent edits |
| **Availability** | 99.5% uptime target; graceful error handling; no data loss on failure |
| **Browser Support** | Chrome, Firefox, Edge (latest 2 versions); responsive design for desktop |
| **Code Quality** | Layered architecture (Controller → Service → Repository); DTOs for API contracts; global exception handler; input validation |
| **Testing** | Unit tests (JUnit 5 + Mockito) for services; integration tests for repositories; Angular component tests (Jasmine/Karma) |
| **Documentation** | Swagger UI at `/swagger-ui.html`; this project document; inline code comments |

---

> **Document Version:** 1.0  
> **Created:** September 28, 2026  
> **Last Updated:** September 28, 2026  
> **Author:** OrderCraft Team
