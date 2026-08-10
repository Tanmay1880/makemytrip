# MakeMyTrip Backend

Backend REST API for a MakeMyTrip-inspired travel booking application.

## Tech Stack

- Java 21
- Spring Boot
- Spring Data JPA
- MySQL
- Maven
- MapStruct
- Bean Validation

## Current Features

### User

- User registration
- Request/response DTO separation
- Input validation
- User-specific exception handling
- Global validation exception handling
- Role-based user model
- MySQL persistence

## Project Structure

```text
src/main/java/com/tanmay/makemytrip_backend
│
├── common
│   └── exception
│
└── user
    ├── controller
    ├── dto
    ├── entity
    ├── exception
    ├── mapper
    ├── repository
    └── service