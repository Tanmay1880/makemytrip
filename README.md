# MakeMyTrip Backend

Backend REST API for a MakeMyTrip-inspired flight booking application built with Java and Spring Boot.

The project focuses on building a realistic backend with layered architecture, JPA relationships, validation, exception handling, transactions, payment processing, and flight seat inventory management.

## Tech Stack

- Java 21
- Spring Boot
- Spring Data JPA
- Hibernate
- MySQL
- Maven
- MapStruct
- Bean Validation
- REST API

## Features

### User
- User registration
- DTO-based API design
- Input validation
- Role-based user model
- Active/inactive user support
- Exception handling

### Airline & Airport
- Create, read, update and delete operations
- Validation
- Duplicate handling
- DTO separation
- Exception handling

### Flight
- Flight CRUD operations
- Airline and airport relationships
- Flight schedule validation
- Flight number uniqueness
- Soft delete
- Active/inactive flight support
- Economy, Premium Economy and Business seat inventory
- Seat pricing
- Optimistic locking using JPA `@Version`

### Booking
- Create and retrieve bookings
- PNR generation
- User and flight relationships
- Seat class selection
- Booking status management
- Pending booking validation
- Booking expiration timestamp
- Active user and flight validation

### Passenger
- Add passengers to bookings
- Retrieve passenger by ID
- Retrieve passengers by booking
- Adult, Child and Infant passenger types
- Gender and date-of-birth support
- Passenger validation

### Payment
- Payment initiation
- Server-side amount calculation
- Payment reference generation
- `INITIATED` and `SUCCESS` payment states
- Transactional payment processing
- Booking confirmation after successful payment
- Duplicate payment protection
- Payment timestamp tracking

### Seat Inventory
- Economy seat reservation
- Premium Economy seat reservation
- Business seat reservation
- Seat availability validation
- Transactional inventory updates
- Optimistic locking for concurrent updates

## Booking Flow

```text
Create Booking
      ↓
Add Passenger(s)
      ↓
Create Payment
      ↓
Payment INITIATED
      ↓
Process Payment
      ↓
Reserve Seats
      ↓
Payment SUCCESS
      ↓
Booking CONFIRMED
```

Payment processing, seat reservation, and booking confirmation are handled as a transactional workflow.

## Architecture

The project follows a layered architecture:

```text
Controller
    ↓
Service
    ↓
Repository
    ↓
Database
```

DTOs are used to separate the API layer from persistence entities, while MapStruct handles entity-to-DTO mapping.

## Project Structure

```text
src/main/java/com/tanmay/makemytrip_backend
│
├── common
│   └── exception
│
├── user
├── airline
├── airport
├── flight
├── booking
├── passenger
└── payment
```

Each business module follows a similar structure:

```text
module/
├── controller
├── dto
├── entity
├── exception
├── mapper
├── repository
└── service
```

## Exception Handling

The application uses:

- Module-specific business exceptions
- Global validation exception handling
- Consistent API error responses

Example:

```json
{
    "status": 400,
    "message": "Validation failed",
    "timestamp": "2026-08-16T21:00:00",
    "errors": {
        "firstName": "must not be blank"
    }
}
```

## Database

The application uses MySQL with Spring Data JPA and Hibernate.

Main entities:

```text
User
Airline
Airport
Flight
Booking
Passenger
Payment
```

Key relationships include:

```text
Airline  1 ─── N Flight
Airport  1 ─── N Flight
User     1 ─── N Booking
Flight   1 ─── N Booking
Booking  1 ─── N Passenger
Booking  1 ─── N Payment
```

## Concurrency

Flight inventory uses JPA optimistic locking:

```java
@Version
private Long version;
```

This helps prevent concurrent transactions from overwriting flight seat inventory changes.

## Current Status

### Completed

- User module
- Airline module
- Airport module
- Flight module
- Booking module
- Passenger module
- Payment initiation and processing
- Booking confirmation
- Seat inventory reservation
- Validation and exception handling
- Transaction management
- Optimistic locking foundation

### Planned

- Booking cancellation
- Seat restoration
- Booking expiration handling
- Payment failure and refund flows
- Authentication and JWT authorization
- Search, filtering and pagination
- Automated unit and integration testing
- API documentation
- Final production-oriented cleanup

## Getting Started

### Prerequisites

- Java 21
- MySQL
- Maven

### Clone the repository

```bash
git clone https://github.com/Tanmay1880/makemytrip-backend.git
cd makemytrip-backend
```

### Configure Database

Update the database configuration in:

```text
src/main/resources/application.properties
```

with your MySQL credentials.

### Run the application

Using Maven:

```bash
./mvnw spring-boot:run
```

Or on Windows:

```bash
mvnw.cmd spring-boot:run
```

The API will be available at:

```text
http://localhost:8080
```

## Project Goal

This project is being developed as a practical Java backend project to demonstrate:

- Spring Boot
- REST API development
- JPA and Hibernate
- Relational database design
- Layered architecture
- DTOs and MapStruct
- Validation and exception handling
- Transaction management
- Concurrency control
- Business workflow implementation
- Payment processing
- Inventory management
