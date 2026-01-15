# ReservationAppServer

Backend server for a sports court reservation system. Enables management of training bookings, players, coaches, and pricing rules.

## Technologies

- **Java 17**
- **Spring Boot 3.5.7** (Web, Data JPA, Security)
- **PostgreSQL** - database
- **Liquibase** - database migrations
- **JWT** - authentication
- **MapStruct** - DTO/Entity mapping
- **iText** - PDF generation
- **ZXing** - QR code generation

## Requirements

- Java 17+
- Maven 3.6+
- PostgreSQL 12+

## Installation and Setup

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
- **Notifications** - SMS and email notifications to coaches

## Project Structure

```
src/main/java/cz/reservation/
├── configuration/     # Configuration (security, app)
├── constant/          # Enums and constants
├── controller/        # REST API endpoints
├── dto/               # Data Transfer Objects
├── entity/            # JPA entities
├── filter/            # JWT filter
└── service/           # Business logic
```

## API Endpoints

| Endpoint | Description |
|----------|-------------|
| `/auth` | Authentication (register, login) |
| `/api/booking` | Booking management |
| `/api/player` | Player management |
| `/api/coach` | Coach management |
| `/api/group` | Group management |
| `/api/training-slot` | Training slots |
| `/api/enrollment` | Group enrollments |
| `/api/venue` | Venue management |
| `/api/court` | Court management |
| `/api/pricing-rule` | Pricing rules |
| `/api/package` | Packages |

## Security

The application uses JWT authentication. To access protected endpoints, include the header:

```
Authorization: Bearer <token>
```

Obtain a token by logging in at `/auth/login`.

## License

Proprietary software.
