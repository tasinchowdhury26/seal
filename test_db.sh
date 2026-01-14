#!/bin/bash

# Test database connection and create sample data
echo "Testing database connection..."

# Test connection
PGPASSWORD=seal psql -h localhost -U seal_admin -d seal_db -c "SELECT 'Database connection successful!' as status;"

if [ $? -eq 0 ]; then
    echo "✅ Database connection successful!"
    
    # Insert sample users for testing
    echo "Creating sample users..."
    
    PGPASSWORD=seal psql -h localhost -U seal_admin -d seal_db << EOF
-- Insert sample users
INSERT INTO users (phone, password, role, status, created_at, updated_at) VALUES 
('1234567890', '\$2a\$10\$N9qo8uLOickgx2ZMRZoMye.Ym4.Je9k/e/l.yMTEA1YBdxcsnOhWy', 'USER', 'ACTIVE', NOW(), NOW()),
('0987654321', '\$2a\$10\$N9qo8uLOickgx2ZMRZoMye.Ym4.Je9k/e/l.yMTEA1YBdxcsnOhWy', 'USER', 'ACTIVE', NOW(), NOW())
ON CONFLICT (phone) DO NOTHING;

-- Insert sample wallets
INSERT INTO wallets (user_id, balance, status, updated_at) 
SELECT u.id, 1000.00, 'ACTIVE', NOW() 
FROM users u 
WHERE u.phone IN ('1234567890', '0987654321')
ON CONFLICT (user_id) DO NOTHING;

-- Check data
SELECT 'Users created:' as info, COUNT(*) as count FROM users;
SELECT 'Wallets created:' as info, COUNT(*) as count FROM wallets;
SELECT u.phone, w.balance FROM users u JOIN wallets w ON u.id = w.user_id;
EOF

    echo "✅ Sample data created!"
    echo "Sample users: 1234567890 and 0987654321 (password: 'password' for both)"
    echo "Each user has a wallet with 1000.00 balance"
else
    echo "❌ Database connection failed!"
fi
