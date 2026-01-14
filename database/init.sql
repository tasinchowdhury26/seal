-- Create database user
CREATE USER seal_admin
WITH
    PASSWORD 'seal';

-- Create database
CREATE DATABASE seal_db;

-- Grant privileges
GRANT ALL PRIVILEGES ON DATABASE seal_db TO seal_admin;
GRANT ALL ON SCHEMA public TO seal_admin;

-- Connect to seal_db and create tables
\c seal_db;

-- Create all tables
\i tables.sql;