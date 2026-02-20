# ReservationAppServer

Backend server for a sports court reservation system. Enables management of training bookings, players, coaches, and pricing rules.

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
- **NotificationAPI** - SMS and email notifications
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

3. Update configuration in `src/main/resources/application.yaml`:
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

## Main Features

- **User Management** - registration, login, roles (ADMIN, USER, COACH)
- **Bookings** - create and manage training slot reservations
- **Training Slots** - time slots with capacity and pricing
- **Groups** - training groups managed by coaches
- **Players** - player profiles (children) under parent accounts
- **Coaches** - coach profiles with certifications
- **Pricing Rules** - flexible pricing system
- **Packages** - prepaid training packages
- **Court Management** - venues, courts, court blocking
- **Invoicing** - invoice summaries for players
- **Attendance** - training attendance tracking
- **Notifications** - SMS and email notifications via NotificationAPI
- **Seasons** - season management for scheduling
- **Weather Notes** - weather condition tracking for outdoor courts
- **Company Info** - company/organization settings

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
| `/auth` | Authentication (register, login) |
| `/user` | User management |
| `/api/booking` | Booking management |
| `/api/player` | Player management |
| `/api/coach` | Coach management |
| `/api/group` | Group management |
| `/api/training-slot` | Training slots |
| `/api/enrollment` | Group enrollments |
| `/api/attendance` | Attendance tracking |
| `/api/venue` | Venue management |
| `/api/court` | Court management |
| `/api/court-block` | Court blocking |
| `/api/pricing-rules` | Pricing rules |
| `/api/package` | Packages |
| `/api/invoice-summary` | Invoice summaries |
| `/api/season` | Season management |
| `/api/weather-notes` | Weather notes |
| `/api/company-info` | Company information |

## Security

The application uses JWT authentication. To access protected endpoints, include the header:

```
Authorization: Bearer <token>
```

Obtain a token by logging in at `/auth/login`.

**Roles:** ADMIN, USER, COACH

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
- `./qrcodes` → `/app/qrcodes` - QR code images
- `./pdf` → `/app/pdf` - generated PDF invoices

## License

Proprietary software.
