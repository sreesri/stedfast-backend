-- liquibase formatted sql

-- changeset stedfast:002-create-weight-log
CREATE TABLE IF NOT EXISTS weight_log (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    weight DECIMAL(5, 2) NOT NULL,
    logged_time TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_weight_log_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
