-- liquibase formatted sql

-- changeset stedfast:001-initial-schema

-- ----------------------------------------
-- Users
-- ----------------------------------------
CREATE TABLE users (
  id          VARCHAR(50) PRIMARY KEY,
  name        VARCHAR(100) NOT NULL,
  email       VARCHAR(255) NOT NULL UNIQUE,
  created_at  TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
  password    VARCHAR(255) NOT NULL
);

-- ----------------------------------------
-- Fasting schledules
-- ----------------------------------------
CREATE TABLE fasting_schedules (
  id             VARCHAR(50) PRIMARY KEY,
  user_id        VARCHAR(50) NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  fasting_hours  INT NOT NULL CHECK (fasting_hours > 0 AND fasting_hours < 24),
  eating_hours   INT NOT NULL CHECK (eating_hours > 0 AND eating_hours < 24),
  label          VARCHAR(50),               -- e.g. "16:8", "My Custom Plan"
  is_active      BOOLEAN NOT NULL DEFAULT TRUE,
  created_at     TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
  CONSTRAINT valid_window CHECK (fasting_hours + eating_hours = 24)
);

-- ----------------------------------------
-- Fasting sessions
-- ----------------------------------------
CREATE TABLE fasting_sessions (
  id               VARCHAR(50) PRIMARY KEY,
  user_id          VARCHAR(50) NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  schedule_id      VARCHAR(50) REFERENCES fasting_schedules(id) ON DELETE SET NULL,
  session_type     VARCHAR(10) NOT NULL CHECK (session_type IN ('FAST', 'EAT')),
  started_at       TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
  ended_at         TIMESTAMP WITH TIME ZONE,
  duration_minutes INT,
  status           VARCHAR(10) NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'COMPLETED')),
  CONSTRAINT one_active_session_per_user
    EXCLUDE USING btree (user_id WITH =)
    WHERE (status = 'active')
);

-- ----------------------------------------
-- Body stats
-- ----------------------------------------
CREATE TABLE body_stats (
  id             VARCHAR(50) PRIMARY KEY,
  user_id        VARCHAR(50) NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  logged_date    DATE NOT NULL DEFAULT CURRENT_DATE,
  height_cm      NUMERIC(5,2),
  weight_kg      NUMERIC(5,2),
  waist_cm       NUMERIC(5,2),
  chest_cm       NUMERIC(5,2),
  hips_cm        NUMERIC(5,2),
  body_fat_pct   NUMERIC(4,2),
  created_at     TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
  UNIQUE (user_id, logged_date)   -- one entry per day per user
);

-- ----------------------------------------
-- Dishes
-- ----------------------------------------
CREATE TABLE dishes (
  id             VARCHAR(50) PRIMARY KEY,
  user_id        VARCHAR(50) NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  name           TEXT,
  calories       INT,
  protein        NUMERIC(5,2),
  carbs          NUMERIC(5,2),
  fat            NUMERIC(5,2),
  created_at     TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- ----------------------------------------
-- Meal Logs
-- ----------------------------------------
CREATE TABLE meal_logs (
  id             VARCHAR(50) PRIMARY KEY,
  user_id        VARCHAR(50) NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  meal_time      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
  calories       INT,
  protein        NUMERIC(5,2),
  carbs          NUMERIC(5,2),
  fat            NUMERIC(5,2),
  notes          TEXT,
  created_at     TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- ----------------------------------------
-- Meal Log Dishes
-- ----------------------------------------
CREATE TABLE meal_log_dishes (
  id             VARCHAR(50) PRIMARY KEY,
  meal_log_id    VARCHAR(50) NOT NULL REFERENCES meal_logs(id) ON DELETE CASCADE,
  dish_id        VARCHAR(50) REFERENCES dishes(id) ON DELETE SET NULL,
  name           TEXT,
  calories       INT,
  protein        NUMERIC(5,2),
  carbs          NUMERIC(5,2),
  fat            NUMERIC(5,2),
  quantity       INT NOT NULL DEFAULT 1,
  created_at     TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);
  