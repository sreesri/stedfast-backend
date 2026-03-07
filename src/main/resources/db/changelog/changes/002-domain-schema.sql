-- liquibase formatted sql

-- changeset stedfast:002-create-weight-log
CREATE TABLE IF NOT EXISTS weightLog (
    id BIGSERIAL PRIMARY KEY,
    userId BIGINT NOT NULL,
    weight DECIMAL(5, 2) NOT NULL,
    loggedTime TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_weight_log_user FOREIGN KEY (userId) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS userFasting(
    userId BIGINT PRIMARY KEY,
    currentFasting VARCHAR(20) NOT NULL,
    start_time TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
)

CREATE TABLE IF NOT EXISTS fastingLog (
    id BIGSERIAL PRIMARY KEY,
    userId BIGINT NOT NULL,
    status VARCHAR(50) NOT NULL,
    start_time TIMESTAMP WITH TIME ZONE NOT NULL,
    end_time TIMESTAMP WITH TIME ZONE,
    CONSTRAINT fk_fasting_log_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);