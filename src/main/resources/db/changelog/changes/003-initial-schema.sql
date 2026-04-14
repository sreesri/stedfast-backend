-- liquibase formatted sql

-- changeset stedfast:003-initial-schema

-- ----------------------------------------
-- User Intake Summary
-- ----------------------------------------
CREATE TABLE user_intake_summary (
    id                  VARCHAR(50) PRIMARY KEY,
    user_id             VARCHAR(50) NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    calorie_limit       INT NOT NULL,
    protein_limit       INT NOT NULL,
    carbs_limit         INT NOT NULL,
    fat_limit           INT NOT NULL,
    logged_date         TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    consumed_calories   INT NOT NULL DEFAULT 0,
    consumed_protein    INT NOT NULL DEFAULT 0,
    consumed_carbs      INT NOT NULL DEFAULT 0,
    consumed_fat        INT NOT NULL DEFAULT 0
);