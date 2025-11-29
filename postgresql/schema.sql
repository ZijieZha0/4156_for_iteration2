CREATE SCHEMA IF NOT EXISTS nutriflow;
SET search_path TO nutriflow;

-- gender type
CREATE TYPE sex_type AS ENUM ('MALE', 'FEMALE', 'OTHER');

-- cooking skill level type
CREATE TYPE cooking_skill_level AS ENUM ('BEGINNER', 'INTERMEDIATE', 'ADVANCED', 'EXPERT');

--
-- create table 'users'
--
CREATE TABLE IF NOT EXISTS users (
    user_id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    height DECIMAL(5,2),
    weight DECIMAL(5,2),
    age INTEGER,
    sex sex_type,
    allergies TEXT[],
    dislikes TEXT[],
    budget DECIMAL(10,2),
    cooking_skill_level cooking_skill_level DEFAULT 'BEGINNER',
    equipments TEXT[],
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT check_height CHECK (height IS NULL OR (height > 0 AND height <= 300)),
    CONSTRAINT check_age CHECK (age IS NULL OR (age > 0 AND age <= 150)),
    CONSTRAINT check_budget CHECK (budget IS NULL OR budget >= 0)
);

--
-- create table 'user_targets'
--
CREATE TABLE IF NOT EXISTS user_targets (
    target_id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL,
    calories DECIMAL(7,2),
    protein DECIMAL(6,2),
    fiber DECIMAL(6,2),
    fat DECIMAL(6,2),
    carbs DECIMAL(6,2),
    iron DECIMAL(6,2),
    calcium DECIMAL(6,2),
    vitamin_a DECIMAL(6,2),
    vitamin_c DECIMAL(6,2),
    vitamin_d DECIMAL(6,2),
    sodium DECIMAL(6,2),
    potassium DECIMAL(6,2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT user_targets_fk 
        FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    CONSTRAINT check_calories CHECK (calories IS NULL OR calories >= 0),
    CONSTRAINT check_protein CHECK (protein IS NULL OR protein >= 0),
    CONSTRAINT check_fiber CHECK (fiber IS NULL OR fiber >= 0),
    CONSTRAINT check_fat CHECK (fat IS NULL OR fat >= 0),
    CONSTRAINT check_carbs CHECK (carbs IS NULL OR carbs >= 0),
    CONSTRAINT check_iron CHECK (iron IS NULL OR iron >= 0),
    CONSTRAINT check_calcium CHECK (calcium IS NULL OR calcium >= 0),
    CONSTRAINT check_vitamin_a CHECK (vitamin_a IS NULL OR vitamin_a >= 0),
    CONSTRAINT check_vitamin_c CHECK (vitamin_c IS NULL OR vitamin_c >= 0),
    CONSTRAINT check_vitamin_d CHECK (vitamin_d IS NULL OR vitamin_d >= 0),
    CONSTRAINT check_sodium CHECK (sodium IS NULL OR sodium >= 0),
    CONSTRAINT check_potassium CHECK (potassium IS NULL OR potassium >= 0)
);

--
-- create table 'user_health_history'
--
CREATE TABLE IF NOT EXISTS user_health_history (
    history_id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL,
    weight DECIMAL(5,2) NOT NULL,
    height DECIMAL(5,2) NOT NULL,
    bmi DECIMAL(5,2) GENERATED ALWAYS AS (weight / ((height / 100) * (height / 100))) STORED,
    recorded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT user_health_history_fk 
        FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    CONSTRAINT check_history_height CHECK (height > 0 AND height <= 300)
);

--
-- create trigger function 'update_updated_at_column'
--
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

--
-- create trigger 'update_users_updated_at'
--
CREATE TRIGGER update_users_updated_at
    BEFORE UPDATE ON users
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

--
-- create trigger 'update_user_targets_updated_at'
--
CREATE TRIGGER update_user_targets_updated_at
    BEFORE UPDATE ON user_targets
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

--
-- create table 'pantry_items'
--
CREATE TABLE IF NOT EXISTS pantry_items (
    item_id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL,
    name VARCHAR(255) NOT NULL,
    quantity DECIMAL(10,2) DEFAULT 0,
    unit VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pantry_user_fk FOREIGN KEY (user_id)
        REFERENCES nutriflow.users(user_id) ON DELETE CASCADE
);

--
-- create table 'recipes'
--
CREATE TABLE IF NOT EXISTS recipes (
    recipe_id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    cook_time INTEGER,
    cuisines TEXT[],
    tags TEXT[],
    ingredients JSONB,
    nutrition JSONB,
    calories DECIMAL(7,2),
    carbohydrates DECIMAL(7,2),
    fat DECIMAL(7,2),
    fiber DECIMAL(7,2),
    protein DECIMAL(7,2),
    popularity_score INTEGER DEFAULT 0
);

--
-- create table 'favorite_recipes'
--
CREATE TABLE IF NOT EXISTS favorite_recipes (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL,
    recipe_id INTEGER NOT NULL,
    times_used INTEGER DEFAULT 0,
    is_favorite BOOLEAN DEFAULT FALSE,
    CONSTRAINT favorite_recipe_user_fk FOREIGN KEY (user_id)
        REFERENCES nutriflow.users(user_id) ON DELETE CASCADE,
    CONSTRAINT favorite_recipe_fk FOREIGN KEY (recipe_id)
        REFERENCES nutriflow.recipes(recipe_id) ON DELETE CASCADE
);

-- 
-- create table recipe_ingredients (for per-recipe items)
-- 
CREATE TABLE IF NOT EXISTS nutriflow.recipe_ingredients (
    id SERIAL PRIMARY KEY,
    recipe_id INTEGER NOT NULL,
    ingredient TEXT,
    quantity NUMERIC,
    unit TEXT,
    allergen_tags TEXT[],
    CONSTRAINT recipe_ingredients_recipe_fk
        FOREIGN KEY (recipe_id)
        REFERENCES nutriflow.recipes (recipe_id)
        ON DELETE CASCADE
);

-- 
-- create substitution_rules (for ingredient swaps)
-- 
CREATE TABLE IF NOT EXISTS nutriflow.substitution_rules (
    id SERIAL PRIMARY KEY,
    ingredient TEXT NOT NULL,
    avoid TEXT,              -- nullable; null means general rule
    substitute TEXT NOT NULL,
    note TEXT
);

--
-- create table 'meals' (individual meals in a meal plan)
--
CREATE TABLE IF NOT EXISTS meals (
    meal_id SERIAL PRIMARY KEY,
    recipe_id INTEGER NOT NULL,
    meal_type VARCHAR(50) NOT NULL,
    scheduled_time TIME,
    servings INTEGER DEFAULT 1,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT meals_recipe_fk FOREIGN KEY (recipe_id)
        REFERENCES nutriflow.recipes(recipe_id) ON DELETE CASCADE,
    CONSTRAINT check_meal_type CHECK (meal_type IN ('breakfast', 'lunch', 'dinner', 'snack')),
    CONSTRAINT check_servings CHECK (servings > 0)
);

--
-- create table 'daily_meal_plans' (daily meal plan for a user)
--
CREATE TABLE IF NOT EXISTS daily_meal_plans (
    plan_id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL,
    plan_date DATE NOT NULL,
    meal_ids INTEGER[],
    total_calories DOUBLE PRECISION,
    total_protein DOUBLE PRECISION,
    total_carbs DOUBLE PRECISION,
    total_fat DOUBLE PRECISION,
    total_fiber DOUBLE PRECISION,
    max_prep_time INTEGER,
    status VARCHAR(50) DEFAULT 'active',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT daily_meal_plans_user_fk FOREIGN KEY (user_id)
        REFERENCES nutriflow.users(user_id) ON DELETE CASCADE,
    CONSTRAINT check_total_calories CHECK (total_calories IS NULL OR total_calories >= 0),
    CONSTRAINT check_total_protein CHECK (total_protein IS NULL OR total_protein >= 0),
    CONSTRAINT check_total_carbs CHECK (total_carbs IS NULL OR total_carbs >= 0),
    CONSTRAINT check_total_fat CHECK (total_fat IS NULL OR total_fat >= 0),
    CONSTRAINT check_total_fiber CHECK (total_fiber IS NULL OR total_fiber >= 0),
    CONSTRAINT check_status CHECK (status IN ('draft', 'active', 'completed', 'cancelled')),
    CONSTRAINT unique_user_date UNIQUE (user_id, plan_date)
);

--
-- create table 'weekly_meal_plans' (weekly meal plan for a user)
--
CREATE TABLE IF NOT EXISTS weekly_meal_plans (
    weekly_plan_id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    daily_plan_ids INTEGER[],
    avg_daily_calories DOUBLE PRECISION,
    avg_daily_protein DOUBLE PRECISION,
    avg_daily_carbs DOUBLE PRECISION,
    avg_daily_fat DOUBLE PRECISION,
    status VARCHAR(50) DEFAULT 'active',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT weekly_meal_plans_user_fk FOREIGN KEY (user_id)
        REFERENCES nutriflow.users(user_id) ON DELETE CASCADE,
    CONSTRAINT check_dates CHECK (end_date >= start_date),
    CONSTRAINT check_avg_calories CHECK (avg_daily_calories IS NULL OR avg_daily_calories >= 0),
    CONSTRAINT check_avg_protein CHECK (avg_daily_protein IS NULL OR avg_daily_protein >= 0),
    CONSTRAINT check_avg_carbs CHECK (avg_daily_carbs IS NULL OR avg_daily_carbs >= 0),
    CONSTRAINT check_avg_fat CHECK (avg_daily_fat IS NULL OR avg_daily_fat >= 0),
    CONSTRAINT check_weekly_status CHECK (status IN ('draft', 'active', 'completed', 'cancelled'))
);

--
-- create index on daily_meal_plans for faster lookups
--
CREATE INDEX IF NOT EXISTS idx_daily_meal_plans_user_date 
    ON daily_meal_plans(user_id, plan_date);

CREATE INDEX IF NOT EXISTS idx_daily_meal_plans_status 
    ON daily_meal_plans(status);

--
-- create index on weekly_meal_plans for faster lookups
--
CREATE INDEX IF NOT EXISTS idx_weekly_meal_plans_user_dates 
    ON weekly_meal_plans(user_id, start_date, end_date);

CREATE INDEX IF NOT EXISTS idx_weekly_meal_plans_status 
    ON weekly_meal_plans(status);

--
-- create table 'ingredient_nutrition'
-- Stores nutritional information per 100g for all ingredients
-- Allows programmers/users to update calorie and macro information
--
CREATE TABLE IF NOT EXISTS ingredient_nutrition (
    ingredient_id SERIAL PRIMARY KEY,
    ingredient_name VARCHAR(255) NOT NULL UNIQUE,
    ingredient_category VARCHAR(100),
    
    -- Nutritional values per 100g
    calories DECIMAL(7,2) NOT NULL DEFAULT 0,
    protein DECIMAL(6,2) DEFAULT 0,
    carbohydrates DECIMAL(6,2) DEFAULT 0,
    fat DECIMAL(6,2) DEFAULT 0,
    fiber DECIMAL(6,2) DEFAULT 0,
    
    -- Micronutrients per 100g (optional)
    iron DECIMAL(6,2) DEFAULT 0,
    calcium DECIMAL(6,2) DEFAULT 0,
    vitamin_a DECIMAL(6,2) DEFAULT 0,
    vitamin_c DECIMAL(6,2) DEFAULT 0,
    vitamin_d DECIMAL(6,2) DEFAULT 0,
    sodium DECIMAL(6,2) DEFAULT 0,
    potassium DECIMAL(6,2) DEFAULT 0,
    
    -- Additional information
    unit VARCHAR(50) DEFAULT 'g',
    description TEXT,
    source VARCHAR(255),
    is_verified BOOLEAN DEFAULT FALSE,
    
    -- Metadata
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    
    -- Constraints
    CONSTRAINT check_calories CHECK (calories >= 0),
    CONSTRAINT check_protein CHECK (protein >= 0),
    CONSTRAINT check_carbohydrates CHECK (carbohydrates >= 0),
    CONSTRAINT check_fat CHECK (fat >= 0)
);

--
-- create indexes on ingredient_nutrition for faster lookups
--
CREATE INDEX IF NOT EXISTS idx_ingredient_name 
    ON ingredient_nutrition(ingredient_name);

CREATE INDEX IF NOT EXISTS idx_ingredient_category 
    ON ingredient_nutrition(ingredient_category);