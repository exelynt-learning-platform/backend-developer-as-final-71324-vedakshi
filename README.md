# backend-developer-as-final-71324-vedakshi

Final Project Assignment – This repository contains the complete final project code and documentation.

# Resource Booking System

A RESTful Resource Booking System built with **Java 21, Spring Boot, Spring Security, JWT, JPA/Hibernate, and MySQL**.

## Features

* JWT authentication with BCrypt
* ADMIN / USER role-based authorization
* Resource CRUD
* Reservation management
* Reservation ownership
* Reservation status: PENDING, CONFIRMED, CANCELLED
* Filtering, pagination and sorting
* Validation and exception handling
* Swagger/OpenAPI documentation
* JUnit 5 and Mockito tests

## Setup

Create the MySQL database:

```sql
CREATE DATABASE resource_booking_db;
```

Configure `application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/resource_booking_db
spring.datasource.username=root
spring.datasource.password=password
spring.jpa.hibernate.ddl-auto=update
```

Run the application:

```powershell
.\mvnw.cmd spring-boot:run
```

Application:

```text
http://localhost:8080
```

## API

### Authentication

```text
POST /auth/login
```

### Resources

```text
GET    /resources
GET    /resources/{id}
POST   /resources        ADMIN
PUT    /resources/{id}  ADMIN
DELETE /resources/{id}  ADMIN
```

### Reservations

```text
POST   /reservations
GET    /reservations/my
GET    /reservations             ADMIN
GET    /reservations/{id}
PUT    /reservations/{id}       ADMIN
DELETE /reservations/{id}       ADMIN
```

Reservations support status/price filtering, pagination and sorting.

## Swagger

After starting the application, open:

```text
http://localhost:8080/swagger-ui.html
```

Use **Authorize** in Swagger and enter the JWT token received from `/auth/login`.

## Testing

Run:

```powershell
.\mvnw.cmd test
```

Current result:

```text
Tests run: 25
Failures: 0
Errors: 0
Skipped: 0
```

## Security

* JWT-based stateless authentication
* BCrypt password hashing
* ADMIN / USER RBAC
* Users can access only their own reservations
