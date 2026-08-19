# MakeMyTrip Backend

Backend REST API for a MakeMyTrip-inspired flight booking application built with Java and Spring Boot.

The project focuses on building a realistic backend with layered architecture, JPA relationships, validation, exception handling, transactional booking and payment workflows, flight search, seat inventory management, and concurrency control.

---

## Tech Stack

- Java 21
- Spring Boot
- Spring Data JPA
- Hibernate
- MySQL
- Maven
- MapStruct
- Bean Validation
- Spring Security
- REST API
- JUnit 5
- Mockito

---

## Features

### User

- User registration
- DTO-based API design
- Input validation
- Role-based user model
- Active/inactive user support
- Password storage foundation for authentication
- Module-specific business exceptions
- Global exception handling

> Authentication and JWT authorization are planned for the next phase.

### Airline & Airport

- Create, read, update and delete operations
- DTO separation
- MapStruct mapping
- Validation
- Duplicate handling
- Active/inactive support
- Business exception handling

### Flight

- Flight CRUD operations
- Airline and airport relationships
- Flight schedule validation
- Flight number uniqueness
- Soft delete
- Active/inactive flight support
- Flight search by route and departure date
- Results ordered by departure time
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
- Booking cancellation
- Seat restoration after cancellation
- Booking expiration handling

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
- `INITIATED`, `SUCCESS`, `FAILED` and `REFUNDED` states
- Payment processing
- Payment failure handling
- Booking confirmation after successful payment
- Duplicate successful-payment protection
- Payment timestamp tracking
- Refund flow

### Seat Inventory

- Economy seat reservation
- Premium Economy seat reservation
- Business seat reservation
- Seat availability validation
- Transactional inventory updates
- Optimistic locking for concurrent updates
- Seat restoration when a booking is cancelled

---

## Booking & Payment Flow

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

Cancellation:

Booking CONFIRMED
↓
Cancel Booking
↓
Booking CANCELLED
↓
Restore Reserved Seats

Payment processing, seat reservation, and booking confirmation are implemented as transactional business workflows.

---

## Architecture

The project follows a layered architecture:

Controller
↓
Service
↓
Repository
↓
Database

DTOs separate the API layer from persistence entities.

MapStruct is used for entity-to-DTO mapping.

Business exceptions are kept inside their respective modules, while common/global exception handling provides consistent API error responses.

---

## Project Structure

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

Each business module follows a similar structure:

module/
├── controller
├── dto
├── entity
├── exception
├── mapper
├── repository
└── service

---

## Exception Handling

The application uses two levels of exception organization:

### Module-specific exceptions

Business exceptions are defined inside their respective modules.

Examples:

- flight/exception
- booking/exception
- payment/exception
- airline/exception
- airport/exception
- user/exception

### Common exception handling

A common global exception handler provides consistent HTTP error responses.

Typical responses include:

- 400 Bad Request
- 404 Not Found
- 409 Conflict

Example:

{
"status": 400,
"message": "Validation failed",
"timestamp": "2026-08-16T21:00:00",
"errors": {
"firstName": "must not be blank"
}
}

---

## Database

The application uses MySQL with Spring Data JPA and Hibernate.

Main entities:

- User
- Airline
- Airport
- Flight
- Booking
- Passenger
- Payment

Key relationships:

Airline  1 ─── N Flight
Airport  1 ─── N Flight
User     1 ─── N Booking
Flight   1 ─── N Booking
Booking  1 ─── N Passenger
Booking  1 ─── N Payment

---

## Concurrency Control

Flight seat inventory uses JPA optimistic locking:

@Version
private Long version;

Seat reservation is performed transactionally so that concurrent updates cannot silently overwrite each other.

A dedicated concurrency test verifies the seat reservation behavior.

---

## Validation

Input validation is implemented using Jakarta Bean Validation.

Examples include:

- Required fields
- String length
- Future departure/arrival dates
- Non-negative seat inventory
- Non-negative prices
- Valid passenger information
- Booking business rules

Business-level validation is handled inside the service layer.

---

## Testing

The backend currently contains unit and application-context tests covering the major business modules.

Current test status:

Tests run: 36
Failures: 0
Errors: 0
Skipped: 0

BUILD SUCCESS

The test suite currently covers areas including:

- Flight search
- Flight validation
- Flight concurrency
- Booking business rules
- Passenger behavior
- Payment initiation
- Payment processing
- Payment failure
- Refund
- Application context loading

---

## Current Status

### Backend Foundation — COMPLETE

- User module
- Airline module
- Airport module
- Flight module
- Flight search
- Booking module
- Passenger module
- Payment module
- Booking confirmation
- Booking cancellation
- Seat reservation
- Seat restoration
- Payment failure handling
- Refund flow
- Validation
- Module-specific exceptions
- Global exception handling
- Transaction management
- Optimistic locking
- MapStruct
- Unit testing
- Application context testing

### Next Phase

- Authentication
- Password authentication flow
- JWT authorization
- Spring Security configuration
- Protected endpoints
- User authorization

### Later

- Admin role
- Staff role
- Admin/staff management operations
- Search/filter/pagination improvements
- API documentation
- Integration testing
- Production-oriented cleanup

> Admin and Staff are intentionally deferred from the current Authentication MVP. The current authentication phase focuses on the USER role.

---

## Getting Started

### Prerequisites

- Java 21
- MySQL
- Maven

### Clone the repository

git clone https://github.com/Tanmay1880/makemytrip-backend.git
cd makemytrip-backend

### Configure Database

The application reads database configuration from environment variables:

spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}

Set these variables before running the application.

### Run Tests

mvn clean test

Expected result:

Tests run: 36
Failures: 0
Errors: 0
Skipped: 0

BUILD SUCCESS

### Run the Application

mvn spring-boot:run

The API will be available at:

http://localhost:8080

---

## Project Goal

This project is being developed as a practical Java backend project to demonstrate:

- Spring Boot
- REST API development
- JPA and Hibernate
- Relational database design
- Layered architecture
- DTOs and MapStruct
- Validation
- Exception handling
- Transaction management
- Concurrency control
- Booking workflows
- Payment workflows
- Flight search
- Inventory management
- Automated testing
- Authentication and authorization
