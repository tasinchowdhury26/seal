# Seal Wallet - Mini Money Transfer Application

A secure Spring Boot application for digital wallet and money transfer operations with JWT authentication.

## Features

- **User Management**: Registration and login with phone number
- **Wallet Operations**: View balance and wallet status
- **Money Transfer**: Send money between users with ACID transactions
- **Transaction History**: View sent, received, and all transactions
- **Security**: JWT authentication, BCrypt password hashing
- **Database**: PostgreSQL with proper relationships

## Technology Stack

- **Backend**: Spring Boot 3.5.9, Java 21
- **Database**: PostgreSQL
- **Security**: Spring Security + JWT
- **Build Tool**: Maven
- **Password Encryption**: BCrypt

## Database Schema

```sql
-- Users Table
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    phone VARCHAR(15) UNIQUE NOT NULL,
    password TEXT NOT NULL,
    role VARCHAR(20) DEFAULT 'USER',
    status VARCHAR(20) DEFAULT 'ACTIVE',
    last_login TIMESTAMP,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

-- Wallets Table
CREATE TABLE wallets (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    balance NUMERIC(15,2) DEFAULT 0.0,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    updated_at TIMESTAMP DEFAULT NOW()
);

-- Transactions Table
CREATE TABLE transactions (
    id BIGSERIAL PRIMARY KEY,
    from_wallet BIGINT REFERENCES wallets(id) ON DELETE CASCADE,
    to_wallet BIGINT REFERENCES wallets(id) ON DELETE CASCADE,
    amount NUMERIC(15,2) NOT NULL,
    type VARCHAR(20) NOT NULL, -- SEND / RECEIVE
    status VARCHAR(20) DEFAULT 'SUCCESS',
    created_at TIMESTAMP DEFAULT NOW()
);

-- OTP Table (Optional feature)
CREATE TABLE otps (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    transaction_id BIGINT REFERENCES transactions(id),
    otp_code VARCHAR(6) NOT NULL,
    purpose VARCHAR(20) NOT NULL, -- LOGIN / TRANSACTION
    status VARCHAR(20) DEFAULT 'PENDING', -- PENDING / VERIFIED / EXPIRED
    created_at TIMESTAMP DEFAULT NOW(),
    expires_at TIMESTAMP
);
```

## API Endpoints

### Authentication (Public)

#### Register User
```http
POST /auth/register
Content-Type: application/json

{
    "phone": "1234567890",
    "password": "password123"
}
```

**Response:**
```json
{
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "phone": "1234567890",
    "message": "User registered successfully"
}
```

#### Login
```http
POST /auth/login
Content-Type: application/json

{
    "phone": "1234567890",
    "password": "password123"
}
```

**Response:**
```json
{
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "phone": "1234567890",
    "message": "Login successful"
}
```

### Wallet Operations (Protected)

#### Get Wallet Balance
```http
GET /wallet/balance
Authorization: Bearer <JWT_TOKEN>
```

**Response:**
```json
{
    "id": 1,
    "balance": 1000.00,
    "status": "ACTIVE",
    "userPhone": "1234567890"
}
```

### Transaction Operations (Protected)

#### Transfer Money
```http
POST /transactions/transfer
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json

{
    "toPhone": "0987654321",
    "amount": 100.50
}
```

**Response:**
```json
{
    "id": 1,
    "fromPhone": "1234567890",
    "toPhone": "0987654321",
    "amount": 100.50,
    "type": "SEND",
    "status": "SUCCESS",
    "createdAt": "2024-01-14T05:30:00"
}
```

#### Get Transaction History
```http
GET /transactions/history
Authorization: Bearer <JWT_TOKEN>
```

#### Get Sent Transactions
```http
GET /transactions/sent
Authorization: Bearer <JWT_TOKEN>
```

#### Get Received Transactions
```http
GET /transactions/received
Authorization: Bearer <JWT_TOKEN>
```

## Setup Instructions

### 1. Database Setup
```bash
# Create PostgreSQL user and database
sudo -u postgres psql -c "CREATE USER seal_admin WITH PASSWORD 'seal';"
sudo -u postgres psql -c "CREATE DATABASE seal_db OWNER seal_admin;"

# Run database schema
PGPASSWORD=seal psql -h localhost -U seal_admin -d seal_db -f database/tables.sql
```

### 2. Application Configuration
The application is configured in `src/main/resources/application.properties`:

```properties
spring.application.name=Seal

# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/seal_db
spring.datasource.username=seal_admin
spring.datasource.password=seal
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA Configuration
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

# JWT Configuration
jwt.secret=sealSecretKeyForJWTTokenGenerationAndValidation2024
jwt.expiration=86400000

# Server Configuration
server.port=8080
```

### 3. Build and Run
```bash
# Build the application
./mvnw clean package

# Run the application
./mvnw spring-boot:run

# Or run the JAR file
java -jar target/seal-0.0.1-SNAPSHOT.jar
```

## Testing

### Sample Test Data
The application includes sample users for testing:
- Phone: `1234567890`, Password: `password`
- Phone: `0987654321`, Password: `password`
- Each user has a wallet with 1000.00 balance

### Test Scenarios

1. **User Registration**
```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{"phone":"1111111111","password":"password123"}'
```

2. **User Login**
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"phone":"1234567890","password":"password"}'
```

3. **Check Balance**
```bash
curl -X GET http://localhost:8080/wallet/balance \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

4. **Transfer Money**
```bash
curl -X POST http://localhost:8080/transactions/transfer \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"toPhone":"0987654321","amount":50.00}'
```

5. **View Transaction History**
```bash
curl -X GET http://localhost:8080/transactions/history \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## Security Features

- **JWT Authentication**: Secure token-based authentication
- **BCrypt Password Hashing**: Passwords are securely hashed
- **ACID Transactions**: Money transfers are atomic operations
- **Input Validation**: Request validation using Bean Validation
- **Error Handling**: Comprehensive error responses

## Project Structure

```
src/main/java/com/seal/seal/
├── config/
│   └── SecurityConfig.java
├── controller/
│   ├── AuthController.java
│   ├── TransactionController.java
│   └── WalletController.java
├── dto/
│   ├── AuthResponse.java
│   ├── LoginRequest.java
│   ├── RegisterRequest.java
│   ├── TransactionResponse.java
│   ├── TransferRequest.java
│   └── WalletResponse.java
├── entity/
│   ├── Otp.java
│   ├── Transaction.java
│   ├── User.java
│   └── Wallet.java
├── exception/
│   └── GlobalExceptionHandler.java
├── repository/
│   ├── TransactionRepository.java
│   ├── UserRepository.java
│   └── WalletRepository.java
├── security/
│   ├── JwtAuthenticationEntryPoint.java
│   └── JwtAuthenticationFilter.java
├── service/
│   ├── TransactionService.java
│   ├── UserService.java
│   └── WalletService.java
├── util/
│   └── JwtUtil.java
└── SealApplication.java
```

## Business Rules

1. **User Registration**: Phone numbers must be unique
2. **Wallet Creation**: Each user gets one wallet automatically
3. **Money Transfer**: 
   - Sender must have sufficient balance
   - Both wallets must be active
   - Cannot transfer to yourself
   - All operations are atomic (ACID)
4. **Transaction Types**: SEND/RECEIVE based on user perspective
5. **Security**: All wallet and transaction endpoints require authentication

## Future Enhancements

- OTP verification for transactions
- KYC integration
- Push notifications
- Transaction limits and fees
- Admin panel for user management
- Microservices architecture
- API rate limiting

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is not licensed yet.
