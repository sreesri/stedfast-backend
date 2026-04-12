-- liquibase formatted sql

-- changeset stedfast:002-initial-schema

-- ----------------------------------------
-- User Intake Limits
-- ----------------------------------------
CREATE TABLE user_intake_limits (
    id              VARCHAR(50) PRIMARY KEY,
    user_id         VARCHAR(50) NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    calorie_limit   INT NOT NULL,
    protein_limit   INT NOT NULL,
    carbs_limit     INT NOT NULL,
    fat_limit       INT NOT NULL,
    created_at      TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);