# ReservationAppServer

Backend server for a tennis court reservation system. Enables management of tennis training bookings, players, coaches, and pricing rules.

## Technologies

- **Java 17** (compile target) / **Java 21** (Docker runtime)
- **Spring Boot 3.5.7** (Web, Data JPA, Security, Validation)
- **PostgreSQL 14** - database
- **Liquibase 4.31** - database migrations
- **JWT (jjwt 0.11.5)** - authentication
- **MapStruct 1.6.3** - DTO/Entity mapping
- **Lombok** - boilerplate reduction
- **Hibernate JPA Modelgen 6.6** - type-safe JPA criteria queries
- **Hypersistence Utils** - advanced Hibernate types (JSONB support)
- **iText 9.4** - PDF generation
- **ZXing 3.5.4** - QR code generation
- **Pingram** - SMS and email notifications
- **Docker** - containerization

## Requirements

- Java 17+
- Maven 3.6+
- PostgreSQL 14+ (or Docker)

## Installation and Setup

### Option 1: Docker (Recommended)

```bash
# Start backend + PostgreSQL
docker-compose up -d

# View logs
docker-compose logs -f backend

# Stop containers
docker-compose down
```

The server will be available at `http://localhost:8080`.

### Option 2: Manual Setup

1. Clone the repository:
```bash
git clone <repository-url>
cd ReservationAppServer
```

2. Create a PostgreSQL database:
```sql
CREATE DATABASE reservation;
```

3. The default active profile is `dev` (set in `application.yaml`). Update credentials in `src/main/resources/application-dev.yaml` if your local PostgreSQL differs from the defaults:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/reservation
    username: <your-username>
    password: <your-password>
```

4. Run the application:
```bash
mvn spring-boot:run
```

The server will be available at `http://localhost:8080`.

## Configuration Profiles

The application uses Spring profiles for environment-specific configuration. The active profile is set in `src/main/resources/application.yaml`:

```yaml
spring:
  profiles.active: dev
```

| Profile | File | Purpose |
|---------|------|---------|
| `dev` | `application-dev.yaml` | Local development with hardcoded credentials |
| `staging` | `application-staging.yaml` | CI/CD staging environment using GitHub Actions secrets |

### Switching profiles

Override the active profile at runtime:
```bash
# Via Maven
mvn spring-boot:run -Dspring-boot.run.profiles=staging

# Via environment variable
SPRING_PROFILES_ACTIVE=staging mvn spring-boot:run

# Via Docker / docker-compose
environment:
  SPRING_PROFILES_ACTIVE: staging
```

### Staging profile and GitHub Actions secrets

`application-staging.yaml` uses Spring Boot placeholder syntax for sensitive values:

```yaml
spring:
  datasource:
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
notification-api:  # Pingram — SMS and email notification service
  client-id: ${NOTIFICATION_CLIENT_ID}
  client-secret: ${NOTIFICATION_CLIENT_SECRET}
```

Spring Boot resolves these from environment variables at runtime. In a GitHub Actions workflow, inject them from repository secrets:

```yaml
- name: Deploy
  env:
    SPRING_PROFILES_ACTIVE: staging
    DB_USERNAME: ${{ secrets.DB_USERNAME }}
    DB_PASSWORD: ${{ secrets.DB_PASSWORD }}
    NOTIFICATION_CLIENT_ID: ${{ secrets.NOTIFICATION_CLIENT_ID }}
    NOTIFICATION_CLIENT_SECRET: ${{ secrets.NOTIFICATION_CLIENT_SECRET }}
```

## Main Features

- **User Management** - registration, login, roles (ADMIN, USER, COACH)
- **Bookings** - create and manage tennis training slot reservations
- **Training Slots** - tennis training time slots with capacity and pricing
- **Groups** - tennis training groups managed by coaches
- **Players** - player profiles (children) under parent accounts
- **Coaches** - tennis coach profiles with certifications
- **Pricing Rules** - flexible pricing system for tennis lessons and packages
- **Packages** - prepaid tennis training packages
- **Court Management** - tennis venues, courts, and court blocking
- **Invoicing** - invoice summaries for players
- **Attendance** - tennis training attendance tracking
- **Notifications** - SMS and email notifications via Pingram
- **Seasons** - tennis season management for scheduling
- **Weather Notes** - weather condition tracking for outdoor tennis courts
- **Company Info** - tennis club/organization settings

## Project Structure

```
src/main/java/cz/reservation/
├── configuration/     # Configuration (security, app, static resources)
├── constant/          # Enums and constants
│   └── converter/     # JPA attribute converters
├── controller/        # REST API endpoints (19 controllers)
│   └── advice/        # Global exception handler
├── dto/               # Data Transfer Objects
│   └── mapper/        # MapStruct mappers (16 mappers)
├── entity/            # JPA entities (16 entities)
│   ├── filter/        # Query filter objects (12 filters)
│   ├── repository/    # Spring Data repositories
│   │   └── specification/  # JPA Specifications (11 specs)
│   └── userdetails/   # Spring Security user details
├── filter/            # JWT authentication filter
└── service/           # Business logic
    ├── exception/     # Custom exceptions
    ├── invoice/       # Invoice generation engine
    ├── listener/      # Event listeners (3 listeners)
    ├── message/       # Message handling
    ├── pricing/       # Pricing strategy pattern
    │   ├── pricinginterface/  # PricingEngine interface
    │   └── resolver/  # Strategy resolver
    └── serviceinterface/  # Service interfaces (19 interfaces)
```

## API Endpoints

| Endpoint | Description |
|----------|-------------|
| `/` | Home/health check |
| `/auth` | Authentication (register, login, token refresh) |
| `/user` | User management |
| `/api/booking` | Booking management |
| `/api/player` | Player management |
| `/api/coach` | Coach management |
| `/api/group` | Group management |
| `/api/training-slot` | Tennis training slots |
| `/api/enrollment` | Group enrollments |
| `/api/attendance` | Attendance tracking |
| `/api/venue` | Venue management |
| `/api/court` | Tennis court management |
| `/api/court-block` | Tennis court blocking |
| `/api/pricing-rules` | Pricing rules |
| `/api/package` | Packages |
| `/api/invoice-summary` | Invoice summaries |
| `/api/season` | Season management |
| `/api/weather-notes` | Weather notes |
| `/api/company-info` | Company information |

## Security

The application uses stateless JWT authentication with an access/refresh token pair.

### Authentication flow

1. **Login** — `POST /auth/generateToken` returns an access token and its expiration time.
2. **Authenticated requests** — include the access token in the header:
   ```
   Authorization: Bearer <access-token>
   ```
3. **Token refresh** — when the access token expires, the client explicitly calls `POST /auth/refresh` with the expired access token in the request body:
   ```json
   { "accessToken": "<expired-access-token>" }
   ```
   - **Refresh token valid** → returns `200` with a new access token and expiration:
     ```json
     { "accessToken": "...", "expiresIn": 3600000 }
     ```
     The client stores the new token and retries the original request.
   - **Refresh token expired** → returns `401`. The client should redirect to the login page.

Refresh tokens are persisted in the `refresh_tokens` table and marked as revoked after each use (rotation on every refresh).

**Roles:** ADMIN, COACH, PARENT, PLAYER

## Build Commands

```bash
# Compile
mvn clean compile

# Run tests
mvn test

# Run single test class
mvn test -Dtest=BookingServiceTest

# Package JAR
mvn package

# Build Docker image
docker build -t reservationapp .
```

## Docker Volumes

When using Docker, the following directories are mounted:
- `./files` → `/app/files` - general files
- `./qr-codes` → `/app/qr-codes` - QR code images
- `./pdf` → `/app/pdf` - generated PDF invoices

## License

Proprietary software.
