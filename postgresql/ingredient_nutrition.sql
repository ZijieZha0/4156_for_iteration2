-- ==========================================
-- Ingredient Nutrition Database
-- Stores nutritional values per 100g for all ingredients
-- Allows programmers/users to update calorie and macro information
-- ==========================================

SET search_path TO nutriflow;

--
-- Create table 'ingredient_nutrition'
-- Stores nutritional information per 100g of each ingredient
--
CREATE TABLE IF NOT EXISTS ingredient_nutrition (
    ingredient_id SERIAL PRIMARY KEY,
    ingredient_name VARCHAR(255) NOT NULL UNIQUE,
    ingredient_category VARCHAR(100), -- e.g., 'meat', 'vegetable', 'grain', 'dairy'
    
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
    unit VARCHAR(50) DEFAULT 'g', -- measurement unit
    description TEXT,
    source VARCHAR(255), -- e.g., 'USDA', 'user-defined', 'manual'
    is_verified BOOLEAN DEFAULT FALSE, -- whether nutrition data is verified
    
    -- Metadata
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100), -- user/system that created this entry
    updated_by VARCHAR(100), -- last user/system that updated this entry
    
    -- Constraints
    CONSTRAINT check_calories CHECK (calories >= 0),
    CONSTRAINT check_protein CHECK (protein >= 0),
    CONSTRAINT check_carbohydrates CHECK (carbohydrates >= 0),
    CONSTRAINT check_fat CHECK (fat >= 0)
);

-- Index for faster lookups by name
CREATE INDEX idx_ingredient_name ON ingredient_nutrition(ingredient_name);
CREATE INDEX idx_ingredient_category ON ingredient_nutrition(ingredient_category);

-- Trigger to update 'updated_at' timestamp
CREATE OR REPLACE FUNCTION update_ingredient_nutrition_timestamp()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_update_ingredient_nutrition_timestamp
BEFORE UPDATE ON ingredient_nutrition
FOR EACH ROW
EXECUTE FUNCTION update_ingredient_nutrition_timestamp();

-- ==========================================
-- Sample Data - Common Ingredients
-- ==========================================

-- Meats & Proteins
INSERT INTO ingredient_nutrition (ingredient_name, ingredient_category, calories, protein, carbohydrates, fat, fiber, source, is_verified) VALUES
('chicken breast', 'meat', 165, 31, 0, 3.6, 0, 'USDA', TRUE),
('ground beef', 'meat', 250, 26, 0, 15, 0, 'USDA', TRUE),
('salmon', 'seafood', 208, 20, 0, 13, 0, 'USDA', TRUE),
('tuna', 'seafood', 144, 23, 0, 5, 0, 'USDA', TRUE),
('pork chop', 'meat', 231, 23, 0, 15, 0, 'USDA', TRUE),
('turkey breast', 'meat', 135, 30, 0, 1, 0, 'USDA', TRUE),
('eggs', 'protein', 155, 13, 1.1, 11, 0, 'USDA', TRUE),
('tofu', 'protein', 76, 8, 1.9, 4.8, 0.3, 'USDA', TRUE);

-- Vegetables
INSERT INTO ingredient_nutrition (ingredient_name, ingredient_category, calories, protein, carbohydrates, fat, fiber, source, is_verified) VALUES
('broccoli', 'vegetable', 34, 2.8, 7, 0.4, 2.6, 'USDA', TRUE),
('spinach', 'vegetable', 23, 2.9, 3.6, 0.4, 2.2, 'USDA', TRUE),
('tomato', 'vegetable', 18, 0.9, 3.9, 0.2, 1.2, 'USDA', TRUE),
('carrot', 'vegetable', 41, 0.9, 10, 0.2, 2.8, 'USDA', TRUE),
('bell pepper', 'vegetable', 26, 1, 6, 0.3, 2.1, 'USDA', TRUE),
('onion', 'vegetable', 40, 1.1, 9, 0.1, 1.7, 'USDA', TRUE),
('lettuce', 'vegetable', 15, 1.4, 2.9, 0.2, 1.3, 'USDA', TRUE),
('cucumber', 'vegetable', 16, 0.7, 3.6, 0.1, 0.5, 'USDA', TRUE);

-- Grains & Carbohydrates
INSERT INTO ingredient_nutrition (ingredient_name, ingredient_category, calories, protein, carbohydrates, fat, fiber, source, is_verified) VALUES
('white rice', 'grain', 130, 2.7, 28, 0.3, 0.4, 'USDA', TRUE),
('brown rice', 'grain', 112, 2.6, 24, 0.9, 1.8, 'USDA', TRUE),
('quinoa', 'grain', 120, 4.4, 21, 1.9, 2.8, 'USDA', TRUE),
('pasta', 'grain', 131, 5, 25, 1.1, 1.8, 'USDA', TRUE),
('bread', 'grain', 265, 9, 49, 3.2, 2.7, 'USDA', TRUE),
('oats', 'grain', 389, 17, 66, 7, 10.6, 'USDA', TRUE),
('sweet potato', 'vegetable', 86, 1.6, 20, 0.1, 3, 'USDA', TRUE),
('potato', 'vegetable', 77, 2, 17, 0.1, 2.1, 'USDA', TRUE);

-- Dairy
INSERT INTO ingredient_nutrition (ingredient_name, ingredient_category, calories, protein, carbohydrates, fat, fiber, source, is_verified) VALUES
('milk', 'dairy', 42, 3.4, 5, 1, 0, 'USDA', TRUE),
('cheese', 'dairy', 402, 25, 1.3, 33, 0, 'USDA', TRUE),
('greek yogurt', 'dairy', 59, 10, 3.6, 0.4, 0, 'USDA', TRUE),
('butter', 'dairy', 717, 0.9, 0.1, 81, 0, 'USDA', TRUE);

-- Legumes
INSERT INTO ingredient_nutrition (ingredient_name, ingredient_category, calories, protein, carbohydrates, fat, fiber, source, is_verified) VALUES
('black beans', 'legume', 132, 8.9, 24, 0.5, 8.7, 'USDA', TRUE),
('chickpeas', 'legume', 164, 8.9, 27, 2.6, 7.6, 'USDA', TRUE),
('lentils', 'legume', 116, 9, 20, 0.4, 7.9, 'USDA', TRUE);

-- Fruits
INSERT INTO ingredient_nutrition (ingredient_name, ingredient_category, calories, protein, carbohydrates, fat, fiber, source, is_verified) VALUES
('apple', 'fruit', 52, 0.3, 14, 0.2, 2.4, 'USDA', TRUE),
('banana', 'fruit', 89, 1.1, 23, 0.3, 2.6, 'USDA', TRUE),
('orange', 'fruit', 47, 0.9, 12, 0.1, 2.4, 'USDA', TRUE),
('strawberry', 'fruit', 32, 0.7, 8, 0.3, 2, 'USDA', TRUE),
('blueberry', 'fruit', 57, 0.7, 14, 0.3, 2.4, 'USDA', TRUE);

-- Nuts & Seeds
INSERT INTO ingredient_nutrition (ingredient_name, ingredient_category, calories, protein, carbohydrates, fat, fiber, source, is_verified) VALUES
('almonds', 'nut', 579, 21, 22, 50, 12.5, 'USDA', TRUE),
('peanuts', 'nut', 567, 26, 16, 49, 8.5, 'USDA', TRUE),
('chia seeds', 'seed', 486, 17, 42, 31, 34, 'USDA', TRUE);

-- Oils & Fats
INSERT INTO ingredient_nutrition (ingredient_name, ingredient_category, calories, protein, carbohydrates, fat, fiber, source, is_verified) VALUES
('olive oil', 'oil', 884, 0, 0, 100, 0, 'USDA', TRUE),
('coconut oil', 'oil', 862, 0, 0, 100, 0, 'USDA', TRUE);

COMMENT ON TABLE ingredient_nutrition IS 'Stores nutritional information per 100g for all ingredients. Used to calculate meal nutrition and can be updated by users/programmers.';
COMMENT ON COLUMN ingredient_nutrition.calories IS 'Calories per 100g of ingredient';
COMMENT ON COLUMN ingredient_nutrition.protein IS 'Protein in grams per 100g';
COMMENT ON COLUMN ingredient_nutrition.is_verified IS 'Whether the nutritional data has been verified by a reliable source';


