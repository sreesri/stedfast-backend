-- liquibase formatted sql

-- changeset stedfast:001-initial-schema
CREATE TABLE IF NOT EXISTS users (
    id VARCHAR(31) PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    createdAt TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);
