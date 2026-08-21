
# MakeMyTrip - Flight Booking System

A full-stack flight booking application inspired by the core workflow of online travel booking platforms. The project provides separate user and admin experiences for searching flights, making bookings, processing simulated payments, cancelling bookings, handling refunds, and managing flight-related data.

## Features

### User Features
- User registration and login
- JWT-based authentication
- Role-based access
- Browse and search available flights
- Search by source and destination
- View flight details
- Book available flights
- Passenger details during booking
- Simulated payment flow
- View personal bookings
- Cancel eligible bookings
- Refund processing for cancelled paid bookings
- View profile information
- Logout
- Seat availability updates during booking/cancellation

### Admin Features
- Admin authentication
- Admin dashboard
- Manage airlines
- Manage airports
- Manage flights
- View bookings
- Manage flight-related data through backend APIs
- Role-based authorization for admin operations

## Tech Stack

### Backend
- Java
- Spring Boot
- Spring MVC / REST API
- Spring Security
- JWT authentication
- Spring Data JPA
- Hibernate
- MySQL
- Maven
- Jakarta Validation

### Frontend
- React
- TypeScript
- Vite
- Tailwind CSS
- React Router
- Axios
- Lucide React

## Architecture

```text
React UI (TypeScript + Vite)
            |
         HTTP/REST
            |
            v
Spring Boot REST API
Controllers -> Services -> Repositories
            |
            v
          MySQL
```

The backend is organized by business domain, including Airline, Airport, Authentication, Booking, Flight, Payment, and User.

## Project Structure

```text
makemytrip/
|
├── backend/
│   ├── .env.example
│   ├── pom.xml
│   ├── mvnw
│   ├── mvnw.cmd
│   ├── README.md
│   └── src/main/java/com/tanmay/makemytrip_backend/
│       ├── airline/
│       ├── airport/
│       ├── auth/
│       ├── booking/
│       ├── flight/
│       ├── payment/
│       ├── user/
│       └── ...
|
├── frontend/
│   ├── package.json
│   ├── package-lock.json
│   ├── index.html
│   ├── vite.config.ts
│   ├── tailwind.config.js
│   ├── postcss.config.js
│   ├── eslint.config.js
│   └── src/
│       ├── api/
│       ├── components/
│       ├── context/
│       ├── pages/
│       └── main.tsx
|
└── .gitignore
```

## Authentication and Authorization

The application uses JWT-based authentication.

```text
Login
  ↓
Authentication API
  ↓
JWT Access Token
  ↓
Frontend authentication state
  ↓
JWT sent with protected requests
  ↓
Spring Security validates token
  ↓
Authorization based on role
```

Supported roles include:
- USER
- ADMIN

Admin-only operations are protected by backend authorization.

## Flight Search Workflow

```text
Select source
    ↓
Select destination
    ↓
Request flight data
    ↓
Backend searches flights
    ↓
Available flights returned
    ↓
Select flight
    ↓
Begin booking
```

Flight, airport, and airline information is stored in the backend database.

## Booking Workflow

```text
Login
  ↓
Search Flight
  ↓
Select Flight
  ↓
Enter Passenger Details
  ↓
Create Booking
  ↓
Payment
  ↓
Booking Confirmed
  ↓
My Bookings
```

The booking process validates the relevant flight and booking information before confirmation.

## Payment and Refund Workflow

Payment is simulated for demonstration purposes.

```text
Booking
   ↓
Payment
   ↓
Payment Successful
   ↓
Booking Confirmed
```

For eligible cancelled paid bookings:

```text
Confirmed Booking
       ↓
Cancellation
       ↓
Refund Processing
       ↓
Payment Refunded
```

This project does not connect to a real payment gateway.

## Booking Cancellation

Users can cancel eligible bookings from the booking section.

The cancellation workflow also handles the related payment/refund state when applicable. Invalid operations, such as cancelling an expired booking, are rejected by the backend.

## Admin Management

### Airlines
- View airlines
- Add airlines
- Manage airline information

### Airports
- View airports
- Add/manage airport information

### Flights
- View flights
- Add flights
- Manage flight information
- Manage availability data

### Bookings
- View booking information
- Manage booking-related administrative operations

Backend authorization prevents normal users from performing administrator operations.

## Database

The backend uses MySQL with JPA/Hibernate.

The main business areas represented in the database include:

```text
User
  |
  └── Booking
          |
          ├── Flight
          |      ├── Airline
          |      ├── Source Airport
          |      └── Destination Airport
          |
          └── Payment
```

## API Structure

The backend exposes REST APIs grouped by business domain.

Examples include:

```text
/api/auth
/api/users
/api/airlines
/api/airports
/api/flights
/api/bookings
/api/payments
```

The exact endpoints and HTTP methods are implemented in the corresponding Spring Boot controllers.

## Validation and Error Handling

The backend uses request validation and domain-specific exception handling.

Handled situations include:
- Invalid user input
- Duplicate airline data
- Missing resources
- Invalid booking operations
- Unauthorized operations
- Forbidden admin operations
- Invalid cancellation attempts
- Payment/refund-related errors

## Frontend Structure

```text
src/
├── api/
│   └── Backend API communication
├── components/
│   └── Reusable UI components
├── context/
│   └── Authentication and application state
├── pages/
│   └── Application screens
└── main.tsx
    └── React application entry point
```

Axios is used for frontend-backend communication, and React Context manages authentication state.

## Running the Project Locally

### Prerequisites

Install:
- Java JDK
- Maven or use the Maven Wrapper
- Node.js and npm
- MySQL
- Git

### 1. Clone the Repository

```bash
git clone https://github.com/Tanmay1880/makemytrip.git
cd makemytrip
```

### 2. Configure the Database

Create a MySQL database:

```sql
CREATE DATABASE makemytrip;
```

Configure the backend database credentials using the project's environment/configuration setup.

The backend contains `.env.example` as a reference for required environment configuration.

Do not commit real database passwords, JWT secrets, or private credentials.

### 3. Start the Backend

```bash
cd backend
```

Windows:

```bash
mvnw.cmd spring-boot:run
```

Linux/macOS:

```bash
./mvnw spring-boot:run
```

Alternatively, run the Spring Boot application from IntelliJ IDEA.

The backend typically runs at:

```text
http://localhost:8080
```

### 4. Start the Frontend

Open another terminal:

```bash
cd frontend
npm install
npm run dev
```

Vite typically starts at:

```text
http://localhost:5173
```

Both backend and frontend must be running at the same time.

## Environment Configuration

Keep sensitive configuration outside the repository.

Typical backend configuration includes:
- Database URL
- Database username
- Database password
- JWT secret
- JWT expiration/configuration

Never commit:
- Database passwords
- JWT secrets
- Production credentials
- API keys

## Recommended End-to-End Test

### User

```text
Register
  ↓
Login
  ↓
Search flight
  ↓
Select flight
  ↓
Enter passenger details
  ↓
Complete payment
  ↓
View confirmed booking
  ↓
Cancel booking
  ↓
Verify refund
  ↓
Check profile
  ↓
Logout
```

### Admin

```text
Admin Login
  ↓
Dashboard
  ↓
Manage Airlines
  ↓
Manage Airports
  ↓
Manage Flights
  ↓
View Bookings
  ↓
Check Admin Profile
  ↓
Logout
```

## Security

The application includes:
- JWT authentication
- Role-based authorization
- Protected backend endpoints
- Request validation
- Backend ownership checks for booking-related operations
- Separation between user and admin capabilities

Frontend route protection is not treated as the security boundary; authorization is enforced by the backend.

## Current Scope

This project demonstrates:
- REST API development
- Spring Boot architecture
- Spring Security
- JWT authentication
- Role-based authorization
- JPA/Hibernate
- MySQL persistence
- React frontend development
- API integration
- Flight booking workflows
- Payment/refund simulation
- Admin management

## Future Improvements

Possible improvements include:
- Real payment gateway integration
- Email booking confirmations
- Real airline/flight data integration
- Production deployment
- Cloud database hosting
- Automated backend tests
- Frontend unit/integration tests
- Advanced flight filtering
- Pagination
- Better observability and logging
- Production deployment configuration

## Disclaimer

This is an educational/portfolio project inspired by the workflow of flight booking platforms. It is not affiliated with or operated by MakeMyTrip.

Payment processing in this project is simulated and should not be used for real financial transactions.

## Author

**Tanmay Kaushik**

GitHub: https://github.com/Tanmay1880

## License

This project is intended primarily for educational and portfolio purposes.