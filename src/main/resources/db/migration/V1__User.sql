DROP TABLE IF EXISTS users;
CREATE TABLE users (
    user_id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE,
    mobile VARCHAR(20) UNIQUE,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(100),
    middle_name VARCHAR(100),
    last_name VARCHAR(100),
    role VARCHAR(50) DEFAULT 'PILOT',
    country_code VARCHAR(10),

    -- ✅ Audit Columns
    created_dtm TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_dtm TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,

    -- ✅ Soft Delete Support
    is_active BOOLEAN DEFAULT TRUE,
    deleted_dtm TIMESTAMP WITH TIME ZONE NULL
);

CREATE UNIQUE INDEX idx_users_email ON users (email);
CREATE UNIQUE INDEX idx_users_mobile ON users (mobile);

ALTER TABLE users ADD CONSTRAINT uq_users_email UNIQUE (email);