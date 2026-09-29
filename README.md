# OrderCraft — Manufacturing Order Management System

> Web-based Order Management System (OMS) automating order processing, bill-of-materials generation, purchase orders, invoicing and payment tracking for general-purpose manufacturing.

---

## Tech Stack

| Layer | Technology |
|-------|------------|
| **Backend** | Java 17 + Spring Boot 3.3.4 |
| **Frontend** | Angular 17+ |
| **Database** | Oracle DB 21c XE |
| **Security** | Spring Security + JWT |
| **Build** | Maven 3.9+ / Angular CLI |

---

## Prerequisites

| Software | Version | Install |
|----------|---------|---------|
| **JDK** | 17 or 21 LTS | `sudo apt install openjdk-17-jdk` |
| **Maven** | 3.9+ | `sudo apt install maven` |
| **Node.js** | 18+ LTS | [nodejs.org](https://nodejs.org) |
| **npm** | 9+ | Comes with Node.js |
| **Angular CLI** | 17+ | `npm install -g @angular/cli` |
| **Docker** | 24+ | [docs.docker.com/get-docker](https://docs.docker.com/get-docker/) |
| **Git** | 2.x | `sudo apt install git` |

---

## Getting Started

### 1. Clone the Repository

```bash
git clone <repository-url>
cd OrderCraft
```

### 2. Database Setup

#### Option A: Docker (Recommended)

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

# Verify the container is running
docker ps

# Check logs (wait for "DATABASE IS READY TO USE")
docker logs -f ordercraft-db
```

#### Option B: Native Oracle Installation

```bash
# After installing Oracle DB, connect as SYSDBA and run:
sqlplus / as sysdba
```

```sql
CREATE USER ordercraft IDENTIFIED BY ordercraft123
  DEFAULT TABLESPACE users
  TEMPORARY TABLESPACE temp
  QUOTA UNLIMITED ON users;

GRANT CONNECT, RESOURCE, CREATE VIEW, CREATE SEQUENCE TO ordercraft;
GRANT CREATE SESSION TO ordercraft;
```

---

### 3. Backend Setup

```bash
# Navigate to backend directory
cd ordercraft-backend

# Build the project (compile + run tests)
mvn clean install

# Build without running tests
mvn clean install -DskipTests

# Run the application
mvn spring-boot:run
```

The backend starts at **http://localhost:8080**

#### Useful Backend Commands

```bash
# Compile only (quick check)
mvn compile

# Run tests only
mvn test

# Package as JAR
mvn clean package

# Run the packaged JAR directly
java -jar target/ordercraft-backend-0.0.1-SNAPSHOT.jar

# Run with a custom JWT secret
JWT_SECRET=my-super-secret-256-bit-key-here mvn spring-boot:run

# Run in debug mode (remote debug on port 5005)
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005"
```

---

### 4. Frontend Setup

```bash
# Navigate to frontend directory
cd ordercraft-frontend

# Install dependencies
npm install

# Start development server
ng serve

# Start on a custom port
ng serve --port 4201

# Build for production
ng build --configuration production
```

The frontend starts at **http://localhost:4200**

---

### 5. Full Stack with Docker Compose

```bash
# Start all services (DB + Backend + Frontend)
docker-compose up -d

# View logs
docker-compose logs -f

# View logs for a specific service
docker-compose logs -f backend

# Stop all services
docker-compose down

# Stop and remove volumes (clears DB data)
docker-compose down -v

# Rebuild and restart
docker-compose up -d --build
```

---

## API Access

### Swagger UI

Once the backend is running, access API docs at:

```
http://localhost:8080/swagger-ui.html
```

### Default Login Credentials

| Username | Password | Role |
|----------|----------|------|
| `admin` | `Admin@123` | Admin (full access) |

### Authentication

```bash
# Login — get JWT token
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "Admin@123"}'

# Use the token in subsequent requests
curl http://localhost:8080/api/users \
  -H "Authorization: Bearer <your-jwt-token>"

# Refresh token
curl -X POST http://localhost:8080/api/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{"token": "<your-jwt-token>"}'

# Logout
curl -X POST http://localhost:8080/api/auth/logout \
  -H "Authorization: Bearer <your-jwt-token>"
```

---

## Database Management

```bash
# Connect to Oracle DB via Docker
docker exec -it ordercraft-db sqlplus ordercraft/ordercraft123@//localhost:1521/XEPDB1

# View Flyway migration status
mvn flyway:info

# Manually run pending migrations
mvn flyway:migrate
```

Flyway migrations run automatically on application startup. Scripts are located at:

```
ordercraft-backend/src/main/resources/db/migration/
├── V1__create_user_role_permission_tables.sql
├── V2__create_token_blacklist_table.sql
└── V3__seed_roles_permissions_admin.sql
```

---

## Configuration

Key settings are in `ordercraft-backend/src/main/resources/application.yml`:

| Property | Default | Description |
|----------|---------|-------------|
| `server.port` | `8080` | Backend server port |
| `spring.datasource.url` | `jdbc:oracle:thin:@localhost:1521/XEPDB1` | Oracle connection URL |
| `spring.datasource.username` | `ordercraft` | DB username |
| `spring.datasource.password` | `ordercraft123` | DB password |
| `app.jwt.secret` | (hex string) | JWT signing key — **override in production** |
| `app.jwt.expiration-ms` | `86400000` | Token expiry (24 hours) |
| `app.cors.allowed-origins` | `http://localhost:4200` | CORS allowed origins |

Override any property via environment variables:

```bash
# Examples
export SPRING_DATASOURCE_URL=jdbc:oracle:thin:@dbhost:1521/XEPDB1
export SPRING_DATASOURCE_USERNAME=myuser
export SPRING_DATASOURCE_PASSWORD=mypassword
export JWT_SECRET=my-production-secret-key

mvn spring-boot:run
```

---

## Project Structure

```
OrderCraft/
├── ordercraft-backend/              # Spring Boot application
│   ├── pom.xml                      # Maven build config
│   ├── src/main/java/com/ordercraft/
│   │   ├── auth/                    # Authentication & JWT
│   │   ├── user/                    # User & Role management
│   │   ├── common/                  # Shared DTOs, exceptions, entities
│   │   └── OrderCraftApplication.java
│   └── src/main/resources/
│       ├── application.yml          # App config
│       └── db/migration/            # Flyway SQL scripts
├── ordercraft-frontend/             # Angular application (upcoming)
├── docker-compose.yml               # Full-stack Docker setup
├── PROJECT_MODULES.md               # Detailed module specs
└── README.md                        # This file
```

---

## Testing the Backend (Current Build)

The following endpoints are available right now (**Module 1 — Auth & Authorization**). No frontend is needed — test everything from the terminal.

### Step 1: Start the Database

```bash
# Start Oracle in Docker (skip if already running)
docker run -d \
  --name ordercraft-db \
  -p 1521:1521 \
  -e ORACLE_PASSWORD=ordercraft_admin \
  -e APP_USER=ordercraft \
  -e APP_USER_PASSWORD=ordercraft123 \
  -v ordercraft-oracle-data:/opt/oracle/oradata \
  gvenzl/oracle-xe:21-slim

# Wait until you see "DATABASE IS READY TO USE" (~30-60 seconds)
docker logs -f ordercraft-db
```

### Step 2: Start the Backend

```bash
cd ordercraft-backend

# Build and run
mvn clean install -DskipTests
mvn spring-boot:run
```

Wait for: `Started OrderCraftApplication in X seconds`

### Step 3: Test the Auth Endpoints

> All examples use `curl`. You can also use [Postman](https://www.postman.com/) or open **http://localhost:8080/swagger-ui.html** in your browser.

#### 3a. Login

```bash
curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "Admin@123"
  }' | json_pp
```

**Expected response (200 OK):**

```json
{
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "tokenType": "Bearer",
    "expiresIn": 86400,
    "user": {
      "id": 1,
      "username": "admin",
      "email": "admin@ordercraft.com",
      "fullName": "System Administrator",
      "roles": ["Admin"],
      "permissions": ["APPROVAL_CONFIG", "AUDIT_VIEW", "CONFIG_MANAGE", "..."]
    }
  },
  "message": "Login successful"
}
```

**Save the token** — you'll need it for the next steps:

```bash
# Quick way: login and extract token in one command
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"Admin@123"}' \
  | grep -o '"token":"[^"]*"' | cut -d'"' -f4)

echo $TOKEN
```

#### 3b. Login with Wrong Credentials (expect 401)

```bash
curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "wrongpassword"
  }' | json_pp
```

**Expected response (401 Unauthorized):**

```json
{
  "error": "UNAUTHORIZED",
  "message": "Invalid username or password",
  "timestamp": "2026-09-29T..."
}
```

#### 3c. Access a Protected Endpoint (with token)

```bash
# This should succeed (200 OK)
curl -s http://localhost:8080/api/auth/logout \
  -X POST \
  -H "Authorization: Bearer $TOKEN" | json_pp
```

#### 3d. Access a Protected Endpoint (without token — expect 401)

```bash
curl -s -X POST http://localhost:8080/api/auth/logout | json_pp
```

**Expected:** `401 Unauthorized` — no token provided.

#### 3e. Refresh Token

```bash
# First, login to get a fresh token
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"Admin@123"}' \
  | grep -o '"token":"[^"]*"' | cut -d'"' -f4)

# Refresh it
curl -s -X POST http://localhost:8080/api/auth/refresh \
  -H "Content-Type: application/json" \
  -d "{\"token\": \"$TOKEN\"}" | json_pp
```

**Expected response (200 OK):** New token issued, old token blacklisted.

#### 3f. Admin Reset Password

```bash
# Login first
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"Admin@123"}' \
  | grep -o '"token":"[^"]*"' | cut -d'"' -f4)

# Reset the admin's own password (userId=1)
curl -s -X POST http://localhost:8080/api/auth/reset-password \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "userId": 1,
    "newPassword": "NewAdmin@456"
  }' | json_pp
```

**Expected response (200 OK):**

```json
{
  "message": "Password reset successfully"
}
```

Now login with the new password:

```bash
curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"NewAdmin@456"}' | json_pp
```

#### 3g. Validation Errors (expect 400)

```bash
# Empty username and password
curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"","password":""}' | json_pp
```

**Expected response (400 Bad Request):**

```json
{
  "error": "VALIDATION_ERROR",
  "message": "Validation failed",
  "details": [
    { "field": "username", "message": "Username is required" },
    { "field": "password", "message": "Password is required" }
  ]
}
```

#### 3h. Logout and Verify Token is Blacklisted

```bash
# Login
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"Admin@123"}' \
  | grep -o '"token":"[^"]*"' | cut -d'"' -f4)

# Logout
curl -s -X POST http://localhost:8080/api/auth/logout \
  -H "Authorization: Bearer $TOKEN" | json_pp

# Try using the same token again — should fail
curl -s -X POST http://localhost:8080/api/auth/logout \
  -H "Authorization: Bearer $TOKEN" | json_pp
```

**Expected:** First logout succeeds, second call fails because the token is now blacklisted.

### Step 4: Swagger UI (Browser)

Open **http://localhost:8080/swagger-ui.html** in your browser to see all available endpoints with interactive forms — no curl needed.

### Quick Reference: All Available Endpoints

| Method | Endpoint | Auth | Permission | What it does |
|--------|----------|:----:|:----------:|-------------|
| `POST` | `/api/auth/login` | ❌ | — | Login → JWT |
| `POST` | `/api/auth/refresh` | ❌ | — | Get new token |
| `POST` | `/api/auth/logout` | ✅ | — | Blacklist token |
| `POST` | `/api/auth/reset-password` | ✅ | `USER_MANAGE` | Reset a user's password |

---

## Troubleshooting

| Problem | Solution |
|---------|----------|
| `mvn: command not found` | Install Maven: `sudo apt install maven` |
| `ORA-01017: invalid username/password` | Verify Oracle user is created and container is ready |
| `Connection refused on port 1521` | Wait for Oracle to fully start: `docker logs -f ordercraft-db` |
| `Port 8080 already in use` | Kill the process: `lsof -i :8080` then `kill <PID>` |
| `Flyway migration failed` | Check SQL syntax; run `mvn flyway:repair` then `mvn flyway:migrate` |
| `CORS errors in browser` | Ensure frontend URL matches `app.cors.allowed-origins` |
