-- liquibase formatted sql

-- changeset stedfast:005-exercise-schema

-- ----------------------------------------
-- Exercise Library
-- ----------------------------------------
CREATE TABLE exercise_library (
  id             VARCHAR(50) PRIMARY KEY,
  user_id        VARCHAR(50) NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  name           VARCHAR(255) NOT NULL,
  muscle_group   VARCHAR(20) NOT NULL,
  created_at     TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- ----------------------------------------
-- Workout Logs
-- ----------------------------------------
CREATE TABLE workout_logs (
  id             VARCHAR(50) PRIMARY KEY,
  user_id        VARCHAR(50) NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  log_date       DATE NOT NULL DEFAULT CURRENT_DATE,
  notes          TEXT,
  created_at     TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- ----------------------------------------
-- Workout Log Muscle Groups (multi-select tags for what the day targeted)
-- ----------------------------------------
CREATE TABLE workout_log_muscle_groups (
  workout_log_id VARCHAR(50) NOT NULL REFERENCES workout_logs(id) ON DELETE CASCADE,
  muscle_group   VARCHAR(20) NOT NULL
);

-- ----------------------------------------
-- Workout Log Exercises
-- ----------------------------------------
CREATE TABLE workout_log_exercises (
  id             VARCHAR(50) PRIMARY KEY,
  workout_log_id VARCHAR(50) NOT NULL REFERENCES workout_logs(id) ON DELETE CASCADE,
  exercise_id    VARCHAR(50) REFERENCES exercise_library(id) ON DELETE SET NULL,
  name           TEXT,
  muscle_group   VARCHAR(20),
  sets           INT NOT NULL DEFAULT 1,
  reps           INT NOT NULL DEFAULT 1,
  created_at     TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);
