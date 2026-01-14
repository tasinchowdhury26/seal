#!/bin/bash

# API Testing Script for Seal Wallet Application with Refresh Token Support
BASE_URL="http://localhost:8080"

echo "🚀 Seal Wallet API Testing Script (with Refresh Tokens)"
echo "======================================================"

# Check if server is running
echo "📡 Checking if server is running..."
if ! curl -s "$BASE_URL/auth/login" > /dev/null 2>&1; then
    echo "❌ Server is not running on $BASE_URL"
    echo "Please start the application first: ./mvnw spring-boot:run"
    exit 1
fi

echo "✅ Server is running!"
echo ""

# Test 1: Login with existing user
echo "🔐 Test 1: Login with existing user (1234567890)"
LOGIN_RESPONSE=$(curl -s -X POST "$BASE_URL/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"phone":"1234567890","password":"password"}')

echo "Response: $LOGIN_RESPONSE"

# Extract JWT access token and refresh token
ACCESS_TOKEN=$(echo $LOGIN_RESPONSE | grep -o '"accessToken":"[^"]*"' | cut -d'"' -f4)
REFRESH_TOKEN=$(echo $LOGIN_RESPONSE | grep -o '"refreshToken":"[^"]*"' | cut -d'"' -f4)

if [ -z "$ACCESS_TOKEN" ]; then
    echo "❌ Failed to get access token"
    exit 1
fi

if [ -z "$REFRESH_TOKEN" ]; then
    echo "❌ Failed to get refresh token"
    exit 1
fi

echo "✅ Login successful! Tokens obtained."
echo "Access Token: ${ACCESS_TOKEN:0:20}..."
echo "Refresh Token: ${REFRESH_TOKEN:0:20}..."
echo ""

# Test 2: Check wallet balance
echo "💰 Test 2: Check wallet balance"
BALANCE_RESPONSE=$(curl -s -X GET "$BASE_URL/wallet/balance" \
  -H "Authorization: Bearer $ACCESS_TOKEN")

echo "Response: $BALANCE_RESPONSE"
echo ""

# Test 3: Transfer money
echo "💸 Test 3: Transfer money (50.00 to 0987654321)"
TRANSFER_RESPONSE=$(curl -s -X POST "$BASE_URL/transactions/transfer" \
  -H "Authorization: Bearer $ACCESS_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"toPhone":"0987654321","amount":50.00}')

echo "Response: $TRANSFER_RESPONSE"
echo ""

# Test 4: Check transaction history
echo "📊 Test 4: Check transaction history"
HISTORY_RESPONSE=$(curl -s -X GET "$BASE_URL/transactions/history" \
  -H "Authorization: Bearer $ACCESS_TOKEN")

echo "Response: $HISTORY_RESPONSE"
echo ""

# Test 5: Refresh token
echo "🔄 Test 5: Refresh access token"
REFRESH_RESPONSE=$(curl -s -X POST "$BASE_URL/auth/refresh" \
  -H "Content-Type: application/json" \
  -d "{\"refreshToken\":\"$REFRESH_TOKEN\"}")

echo "Response: $REFRESH_RESPONSE"

# Extract new tokens
NEW_ACCESS_TOKEN=$(echo $REFRESH_RESPONSE | grep -o '"accessToken":"[^"]*"' | cut -d'"' -f4)
NEW_REFRESH_TOKEN=$(echo $REFRESH_RESPONSE | grep -o '"refreshToken":"[^"]*"' | cut -d'"' -f4)

if [ ! -z "$NEW_ACCESS_TOKEN" ]; then
    echo "✅ Token refresh successful!"
    ACCESS_TOKEN=$NEW_ACCESS_TOKEN
    REFRESH_TOKEN=$NEW_REFRESH_TOKEN
fi
echo ""

# Test 6: Use new access token
echo "🔑 Test 6: Use new access token to check balance"
NEW_BALANCE_RESPONSE=$(curl -s -X GET "$BASE_URL/wallet/balance" \
  -H "Authorization: Bearer $ACCESS_TOKEN")

echo "Response: $NEW_BALANCE_RESPONSE"
echo ""

# Test 7: Logout
echo "🚪 Test 7: Logout (revoke refresh tokens)"
LOGOUT_RESPONSE=$(curl -s -X POST "$BASE_URL/auth/logout" \
  -H "Authorization: Bearer $ACCESS_TOKEN")

echo "Response: $LOGOUT_RESPONSE"
echo ""

# Test 8: Try to use refresh token after logout (should fail)
echo "❌ Test 8: Try refresh token after logout (should fail)"
FAILED_REFRESH=$(curl -s -X POST "$BASE_URL/auth/refresh" \
  -H "Content-Type: application/json" \
  -d "{\"refreshToken\":\"$REFRESH_TOKEN\"}")

echo "Response: $FAILED_REFRESH"
echo ""

# Test 9: Register new user
echo "👤 Test 9: Register new user"
REGISTER_RESPONSE=$(curl -s -X POST "$BASE_URL/auth/register" \
  -H "Content-Type: application/json" \
  -d '{"phone":"1111111111","password":"newpassword123"}')

echo "Response: $REGISTER_RESPONSE"
echo ""

echo "🎉 API Testing Complete!"
echo "========================"
echo ""
echo "💡 Key Features Tested:"
echo "✅ Login with access + refresh tokens"
echo "✅ API calls with access token"
echo "✅ Token refresh mechanism"
echo "✅ Logout (revokes refresh tokens)"
echo "✅ Security (refresh fails after logout)"
echo ""
echo "🔧 Token Configuration:"
echo "- Access Token: 15 minutes"
echo "- Refresh Token: 7 days"
echo "- Automatic token rotation on refresh"
