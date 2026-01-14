#!/bin/bash

echo "🏦 Starting Seal Wallet Application (with Refresh Token Support)"
echo "==============================================================="

# Check if PostgreSQL is running
echo "🔍 Checking PostgreSQL service..."
if ! systemctl is-active --quiet postgresql; then
    echo "⚠️  PostgreSQL is not running. Starting it..."
    sudo systemctl start postgresql
fi

# Check database connection
echo "🔗 Testing database connection..."
if PGPASSWORD=seal psql -h localhost -U seal_admin -d seal_db -c "SELECT 1;" > /dev/null 2>&1; then
    echo "✅ Database connection successful!"
else
    echo "❌ Database connection failed!"
    echo "Please ensure PostgreSQL is running and database is set up correctly."
    exit 1
fi

# Check if refresh_tokens table exists
echo "🔍 Checking refresh_tokens table..."
if PGPASSWORD=seal psql -h localhost -U seal_admin -d seal_db -c "\d refresh_tokens" > /dev/null 2>&1; then
    echo "✅ Refresh tokens table exists!"
else
    echo "⚠️  Creating refresh_tokens table..."
    PGPASSWORD=seal psql -h localhost -U seal_admin -d seal_db -c "
    CREATE TABLE refresh_tokens (
        id BIGSERIAL PRIMARY KEY,
        token VARCHAR(255) UNIQUE NOT NULL,
        user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
        expires_at TIMESTAMP NOT NULL,
        revoked BOOLEAN DEFAULT FALSE,
        created_at TIMESTAMP DEFAULT NOW()
    );"
    echo "✅ Refresh tokens table created!"
fi

# Navigate to project directory
cd /home/tasin/tasinShahriar/Project/TASINSC/seal/seal

echo "🚀 Starting Spring Boot application..."
echo "Server will be available at: http://localhost:8080"
echo ""
echo "📋 Available endpoints:"
echo "  POST /auth/register - Register new user"
echo "  POST /auth/login    - User login (returns access + refresh tokens)"
echo "  POST /auth/refresh  - Refresh access token"
echo "  POST /auth/logout   - Logout (revokes refresh tokens)"
echo "  GET  /wallet/balance - Check wallet balance (requires access token)"
echo "  POST /transactions/transfer - Transfer money (requires access token)"
echo "  GET  /transactions/history - Transaction history (requires access token)"
echo ""
echo "🔑 Token Configuration:"
echo "  Access Token:  15 minutes (for API calls)"
echo "  Refresh Token: 7 days (for getting new access tokens)"
echo ""
echo "🧪 Test users available:"
echo "  Phone: 1234567890, Password: password"
echo "  Phone: 0987654321, Password: password"
echo ""
echo "🔄 Refresh Token Flow:"
echo "  1. Login → Get access + refresh tokens"
echo "  2. Use access token for API calls"
echo "  3. When access token expires → Use /auth/refresh"
echo "  4. Logout → Revokes all refresh tokens"
echo ""
echo "Press Ctrl+C to stop the application"
echo "====================================="

# Start the application
./mvnw spring-boot:run
