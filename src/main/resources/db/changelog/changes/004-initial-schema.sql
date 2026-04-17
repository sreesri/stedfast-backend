-- ----------------------------------------
-- Meals
-- ----------------------------------------
CREATE TABLE meals (
  id             VARCHAR(50) PRIMARY KEY,
  user_id        VARCHAR(50) NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  name           VARCHAR(255),
  calories       INT,
  protein        NUMERIC(5,2),
  carbs          NUMERIC(5,2),
  fat            NUMERIC(5,2),
  notes          TEXT,
  created_at     TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- ----------------------------------------
-- Meal Dishes
-- ----------------------------------------
CREATE TABLE meal_dishes (
  id             VARCHAR(50) PRIMARY KEY,
  meal_id        VARCHAR(50) NOT NULL REFERENCES meals(id) ON DELETE CASCADE,
  dish_id        VARCHAR(50) REFERENCES dishes(id) ON DELETE SET NULL,
  name           TEXT,
  calories       INT,
  protein        NUMERIC(5,2),
  carbs          NUMERIC(5,2),
  fat            NUMERIC(5,2),
  quantity       INT NOT NULL DEFAULT 1,
  created_at     TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);