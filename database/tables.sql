-- Database Creation 
CREATE TABLE
    users (
        id BIGSERIAL PRIMARY KEY,
        phone VARCHAR(15) UNIQUE NOT NULL,
        password TEXT NOT NULL,
        role VARCHAR(20) DEFAULT 'USER',
        status VARCHAR(20) DEFAULT 'ACTIVE',
        last_login TIMESTAMP,
        created_at TIMESTAMP DEFAULT NOW (),
        updated_at TIMESTAMP DEFAULT NOW ()
    );

-- Wallets Table
CREATE TABLE
    wallets (
        id BIGSERIAL PRIMARY KEY,
        user_id BIGINT UNIQUE REFERENCES users (id) ON DELETE CASCADE,
        balance NUMERIC(15, 2) DEFAULT 0.0,
        status VARCHAR(20) DEFAULT 'ACTIVE',
        updated_at TIMESTAMP DEFAULT NOW ()
    );

-- Transactions Table
CREATE TABLE
    transactions (
        id BIGSERIAL PRIMARY KEY,
        from_wallet BIGINT REFERENCES wallets (id) ON DELETE CASCADE,
        to_wallet BIGINT REFERENCES wallets (id) ON DELETE CASCADE,
        amount NUMERIC(15, 2) NOT NULL,
        type VARCHAR(20) NOT NULL, -- SEND / RECEIVE
        status VARCHAR(20) DEFAULT 'SUCCESS',
        created_at TIMESTAMP DEFAULT NOW ()
    );

-- OTP Table
CREATE TABLE
    otps (
        id BIGSERIAL PRIMARY KEY,
        user_id BIGINT REFERENCES users (id) ON DELETE CASCADE,
        transaction_id BIGINT REFERENCES transactions (id),
        otp_code VARCHAR(6) NOT NULL,
        purpose VARCHAR(20) NOT NULL, -- LOGIN / TRANSACTION
        status VARCHAR(20) DEFAULT 'PENDING', -- PENDING / VERIFIED / EXPIRED
        created_at TIMESTAMP DEFAULT NOW (),
        expires_at TIMESTAMP
    );

-- Refresh Tokens Table
CREATE TABLE
    refresh_tokens (
        id BIGSERIAL PRIMARY KEY,
        token VARCHAR(255) UNIQUE NOT NULL,
        user_id BIGINT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
        expires_at TIMESTAMP NOT NULL,
        revoked BOOLEAN DEFAULT FALSE,
        created_at TIMESTAMP DEFAULT NOW ()
    );