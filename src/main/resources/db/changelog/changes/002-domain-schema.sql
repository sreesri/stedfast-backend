-- liquibase formatted sql

-- changeset stedfast:002-create-weight-log runOnChange:true
CREATE TABLE IF NOT EXISTS weightLog (
    id VARCHAR(31) PRIMARY KEY,
    userId VARCHAR(31) NOT NULL,
    weight DECIMAL(5, 2) NOT NULL,
    loggedTime TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_weight_log_user FOREIGN KEY (userId) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS userFasting (
    userId VARCHAR(31) PRIMARY KEY,
    status VARCHAR(20) NOT NULL,
    startTime TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_userFasting_user FOREIGN KEY (userId) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS fastingLog (
    id VARCHAR(31) PRIMARY KEY,
    userId VARCHAR(31) NOT NULL,
    status VARCHAR(50) NOT NULL,
    start_time TIMESTAMP WITH TIME ZONE NOT NULL,
    end_time TIMESTAMP WITH TIME ZONE,
    CONSTRAINT fk_fastingLog_user FOREIGN KEY (userId) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS userConfig (
    userId varchar(31) PRIMARY KEY,
    fastingWindow int DEFAULT 18,
    eatingWindow int DEFAULT 6,
    calorieLimit int DEFAULT 2000,
    CONSTRAINT fk_userConfig_user FOREIGN KEY (userId) REFERENCES users(id) ON DELETE CASCADE
);