CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY,
    email VARCHAR(255),
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    avatar VARCHAR(255)
);

CREATE INDEX IF NOT EXISTS idx_users_first_name ON users (first_name);