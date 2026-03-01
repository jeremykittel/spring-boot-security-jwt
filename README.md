# Spring Boot Security (JWT + Keycloak)

Demo Spring Boot app showing two authentication modes:

- **Custom JWT mode**: register/login issues JWT + refresh token.
- **Keycloak mode**: login via **OAuth2/OIDC** (Keycloak), with role mapping.

## What the app currently offers

### Security
- User registration and authentication (custom JWT mode)
- Access token + refresh token flow
- Logout / token invalidation (token repository)
- Role-based authorization (controllers for demo/admin/management)
- Optional Keycloak integration:
    - OAuth2 Login (browser redirect / session)
    - Role mapping from Keycloak token claims to Spring Security authorities

### API / Modules
- Auth endpoints under `/api/v1/auth/**` (JWT mode)
- Example domain endpoints (e.g., books, demo/admin/management)
- OpenAPI/Swagger UI enabled (see endpoints below)

### Persistence / Auditing
- PostgreSQL backing store
- Spring Data JPA auditing enabled (created/modified auditing)

## Tech stack
- Java 17
- Spring Boot 4.x
- Spring Security
- Spring Data JPA (PostgreSQL)
- Maven
- **Angular (frontend)**
- Node.js + npm (frontend tooling)
- Docker Compose (Postgres, Keycloak, nginx, pgAdmin, MailDev)

## Running with Docker (recommended)

1) Create/update `.env` at the project root (used by Docker Compose).
2) Start infra:
   ```bash
   docker compose up -d
   ```
3) Run the Spring Boot app (locally via IntelliJ or Maven).

### Services started by Docker Compose
- **PostgreSQL (app DB)**: `localhost:5432`
- **Keycloak PostgreSQL (Keycloak DB)**: `localhost:5433`
- **Keycloak** (behind nginx): `http://app.local/auth`
- **pgAdmin**: `http://localhost:5050`
- **MailDev**: `http://localhost:1080`

> Note: `app.local` is provided as a Docker network alias for nginx.  
> If you’re calling it from your host machine, you may need to add to your hosts file:
> `127.0.0.1 app.local`

## Running the application

### Backend (Spring Boot)

#### JWT mode (default)
- Ensure your Spring profile/config sets:
    - `security.auth-mode=custom-jwt`

Run:
```bash
./mvnw spring-boot:run
```

#### Keycloak mode
- Switch auth mode to Keycloak:
    - `security.auth-mode=keycloak`
- Ensure your Keycloak/OIDC settings (client id/issuer/redirect) are configured for your environment.

Run:
```bash
./mvnw spring-boot:run
```

### Frontend (Angular)

The Angular app lives in `./angular-app`.

1) Install dependencies:
   ```bash
   cd angular-app
   npm ci
   ```
2) Start the dev server:
   ```bash
   npm start
   ```
3) Open the app (typically):
    - `http://localhost:4200`

> If the frontend calls the backend API, configure either:
> - an Angular dev-server **proxy** (recommended for local dev), or
> - the backend base URL via Angular environment configuration.

Build:
```bash
cd angular-app
npm run build
```

## API docs (OpenAPI / Swagger)
Once the app is running, open Swagger UI (path depends on Springdoc config). Common defaults:
- `/swagger-ui.html`
- `/swagger-ui/index.html`

## Development notes
- Docker Compose `.env` is for containers; the Spring Boot app reads **environment variables** or `application*.yml` properties.
- If you want `application.yml` to use env vars, use placeholders like `${POSTGRES_DB:app_db}`.

## Build
```bash
./mvnw clean verify
```