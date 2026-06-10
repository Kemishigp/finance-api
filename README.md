# Finance API

A RESTful banking API built with Java, Spring Boot, Hibernate, and Oracle Database.
Designed to demonstrate enterprise backend development patterns used in financial systems.

## Tech Stack

- **Java 17** — core language
- **Spring Boot** — REST API framework
- **Spring Data JPA + Hibernate** — ORM layer
- **Oracle Database** — persistence
- **Docker** — containerized database
- **Maven** — build tool

## Features

- Customer management (create, update, delete)
- Bank account management (checking & savings)
- Deposits and withdrawals with transaction history
- Loan applications with payment tracking
- Global exception handling with proper HTTP status codes
- `@Transactional` support — balance updates and records always in sync

## API Endpoints

### Customers
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /api/customers | Create customer |
| GET | /api/customers | List all customers |
| GET | /api/customers/{id} | Get customer |
| PUT | /api/customers/{id} | Update customer |
| DELETE | /api/customers/{id} | Delete customer |

### Accounts
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /api/accounts | Open account |
| GET | /api/accounts/{id} | Get account + balance |
| GET | /api/accounts/customer/{id} | Customer's accounts |
| POST | /api/accounts/{id}/deposit | Deposit funds |
| POST | /api/accounts/{id}/withdraw | Withdraw funds |
| GET | /api/accounts/{id}/transactions | Transaction history |

### Loans
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /api/loans | Apply for loan |
| GET | /api/loans/{id} | Loan details |
| GET | /api/loans/customer/{id} | Customer's loans |
| POST | /api/loans/{id}/pay | Make payment |
| GET | /api/loans/{id}/payments | Payment history |

## Running Locally

### 1. Start Oracle Database
```bash
docker run -d \
  -p 1521:1521 \
  -e ORACLE_PASSWORD=password123 \
  --name oracle-db \
  gvenzl/oracle-free:latest
```

### 2. Configure application.properties
```properties
spring.datasource.url=jdbc:oracle:thin:@localhost:1521/FREEPDB1
spring.datasource.username=system
spring.datasource.password=password123
```

### 3. Run the app
```bash
./mvnw spring-boot:run
```

API available at `http://localhost:8081`

## Example Usage

```bash
# Create a customer
curl -X POST http://localhost:8081/api/customers \
  -H "Content-Type: application/json" \
  -d '{"name": "Jesse Pine", "email": "jesse@example.com", "phone": "555-1234"}'

# Open an account
curl -X POST http://localhost:8081/api/accounts \
  -H "Content-Type: application/json" \
  -d '{"customerId": "1", "type": "CHECKING"}'

# Deposit funds
curl -X POST http://localhost:8081/api/accounts/1/deposit \
  -H "Content-Type: application/json" \
  -d '{"amount": "1000.00"}'
```
