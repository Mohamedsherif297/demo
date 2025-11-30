-- ============================================================
-- SAMPLE MEALS DATA INSERTION
-- ============================================================
-- This script inserts sample meals with nutrition information
-- Run this after creating the database schema
-- ============================================================

USE mealplanerdb;

-- ============================================================
-- INSERT NUTRITION DATA AND MEALS
-- ============================================================

-- Meal 1: Grilled Chicken Breast with Vegetables
INSERT INTO nutrition (nutrition_id) VALUES (1);
INSERT INTO nutrition_facts (nutrition_id, fact_name, fact_value, unit) VALUES
(1, 'Calories', 350, 'kcal'),
(1, 'Protein', 45, 'g'),
(1, 'Carbohydrates', 20, 'g'),
(1, 'Fat', 10, 'g'),
(1, 'Fiber', 5, 'g');

INSERT INTO meal (meal_name, recipe_text, nutrition_id, rating) VALUES
('Grilled Chicken Breast with Vegetables', 
'Season chicken breast with salt, pepper, and herbs. Grill for 6-7 minutes per side. Serve with steamed broccoli, carrots, and bell peppers.',
1, 5);

-- Meal 2: Salmon with Quinoa and Asparagus
INSERT INTO nutrition (nutrition_id) VALUES (2);
INSERT INTO nutrition_facts (nutrition_id, fact_name, fact_value, unit) VALUES
(2, 'Calories', 450, 'kcal'),
(2, 'Protein', 35, 'g'),
(2, 'Carbohydrates', 40, 'g'),
(2, 'Fat', 15, 'g'),
(2, 'Omega-3', 2.5, 'g');

INSERT INTO meal (meal_name, recipe_text, nutrition_id, rating) VALUES
('Salmon with Quinoa and Asparagus',
'Bake salmon at 400°F for 12-15 minutes. Cook quinoa according to package. Steam asparagus for 5 minutes. Season with lemon and herbs.',
2, 5);

-- Meal 3: Turkey Meatballs with Whole Wheat Pasta
INSERT INTO nutrition (nutrition_id) VALUES (3);
INSERT INTO nutrition_facts (nutrition_id, fact_name, fact_value, unit) VALUES
(3, 'Calories', 420, 'kcal'),
(3, 'Protein', 38, 'g'),
(3, 'Carbohydrates', 45, 'g'),
(3, 'Fat', 12, 'g'),
(3, 'Fiber', 7, 'g');

INSERT INTO meal (meal_name, recipe_text, nutrition_id, rating) VALUES
('Turkey Meatballs with Whole Wheat Pasta',
'Mix ground turkey with breadcrumbs, egg, and seasonings. Form into balls and bake at 375°F for 20 minutes. Serve over whole wheat pasta with marinara sauce.',
3, 4);

-- Meal 4: Vegetarian Buddha Bowl
INSERT INTO nutrition (nutrition_id) VALUES (4);
INSERT INTO nutrition_facts (nutrition_id, fact_name, fact_value, unit) VALUES
(4, 'Calories', 380, 'kcal'),
(4, 'Protein', 18, 'g'),
(4, 'Carbohydrates', 55, 'g'),
(4, 'Fat', 12, 'g'),
(4, 'Fiber', 12, 'g');

INSERT INTO meal (meal_name, recipe_text, nutrition_id, rating) VALUES
('Vegetarian Buddha Bowl',
'Combine cooked brown rice, roasted chickpeas, avocado, cherry tomatoes, cucumber, and mixed greens. Top with tahini dressing.',
4, 5);

-- Meal 5: Beef Stir-Fry with Brown Rice
INSERT INTO nutrition (nutrition_id) VALUES (5);
INSERT INTO nutrition_facts (nutrition_id, fact_name, fact_value, unit) VALUES
(5, 'Calories', 480, 'kcal'),
(5, 'Protein', 40, 'g'),
(5, 'Carbohydrates', 50, 'g'),
(5, 'Fat', 14, 'g'),
(5, 'Iron', 4.5, 'mg');

INSERT INTO meal (meal_name, recipe_text, nutrition_id, rating) VALUES
('Beef Stir-Fry with Brown Rice',
'Slice beef thinly and stir-fry with garlic and ginger. Add bell peppers, snap peas, and broccoli. Season with soy sauce and serve over brown rice.',
5, 4);

-- Meal 6: Greek Yogurt Parfait with Berries
INSERT INTO nutrition (nutrition_id) VALUES (6);
INSERT INTO nutrition_facts (nutrition_id, fact_name, fact_value, unit) VALUES
(6, 'Calories', 280, 'kcal'),
(6, 'Protein', 20, 'g'),
(6, 'Carbohydrates', 35, 'g'),
(6, 'Fat', 6, 'g'),
(6, 'Calcium', 300, 'mg');

INSERT INTO meal (meal_name, recipe_text, nutrition_id, rating) VALUES
('Greek Yogurt Parfait with Berries',
'Layer Greek yogurt with fresh strawberries, blueberries, and granola. Drizzle with honey.',
6, 5);

-- Meal 7: Tuna Salad Wrap
INSERT INTO nutrition (nutrition_id) VALUES (7);
INSERT INTO nutrition_facts (nutrition_id, fact_name, fact_value, unit) VALUES
(7, 'Calories', 320, 'kcal'),
(7, 'Protein', 28, 'g'),
(7, 'Carbohydrates', 30, 'g'),
(7, 'Fat', 10, 'g'),
(7, 'Omega-3', 1.2, 'g');

INSERT INTO meal (meal_name, recipe_text, nutrition_id, rating) VALUES
('Tuna Salad Wrap',
'Mix canned tuna with Greek yogurt, diced celery, and red onion. Wrap in whole wheat tortilla with lettuce and tomato.',
7, 4);

-- Meal 8: Lentil Soup with Vegetables
INSERT INTO nutrition (nutrition_id) VALUES (8);
INSERT INTO nutrition_facts (nutrition_id, fact_name, fact_value, unit) VALUES
(8, 'Calories', 290, 'kcal'),
(8, 'Protein', 18, 'g'),
(8, 'Carbohydrates', 48, 'g'),
(8, 'Fat', 4, 'g'),
(8, 'Fiber', 15, 'g');

INSERT INTO meal (meal_name, recipe_text, nutrition_id, rating) VALUES
('Lentil Soup with Vegetables',
'Sauté onions, carrots, and celery. Add lentils, vegetable broth, diced tomatoes, and spices. Simmer for 30 minutes until lentils are tender.',
8, 5);

-- Meal 9: Egg White Omelet with Spinach
INSERT INTO nutrition (nutrition_id) VALUES (9);
INSERT INTO nutrition_facts (nutrition_id, fact_name, fact_value, unit) VALUES
(9, 'Calories', 180, 'kcal'),
(9, 'Protein', 22, 'g'),
(9, 'Carbohydrates', 8, 'g'),
(9, 'Fat', 6, 'g'),
(9, 'Iron', 2.5, 'mg');

INSERT INTO meal (meal_name, recipe_text, nutrition_id, rating) VALUES
('Egg White Omelet with Spinach',
'Whisk egg whites and pour into heated pan. Add fresh spinach, mushrooms, and feta cheese. Fold and serve.',
9, 4);

-- Meal 10: Shrimp Tacos with Cabbage Slaw
INSERT INTO nutrition (nutrition_id) VALUES (10);
INSERT INTO nutrition_facts (nutrition_id, fact_name, fact_value, unit) VALUES
(10, 'Calories', 340, 'kcal'),
(10, 'Protein', 30, 'g'),
(10, 'Carbohydrates', 35, 'g'),
(10, 'Fat', 10, 'g'),
(10, 'Vitamin C', 45, 'mg');

INSERT INTO meal (meal_name, recipe_text, nutrition_id, rating) VALUES
('Shrimp Tacos with Cabbage Slaw',
'Season shrimp with chili powder and lime. Sauté until pink. Serve in corn tortillas with cabbage slaw and avocado.',
10, 5);

-- Meal 11: Chicken Caesar Salad
INSERT INTO nutrition (nutrition_id) VALUES (11);
INSERT INTO nutrition_facts (nutrition_id, fact_name, fact_value, unit) VALUES
(11, 'Calories', 380, 'kcal'),
(11, 'Protein', 42, 'g'),
(11, 'Carbohydrates', 15, 'g'),
(11, 'Fat', 18, 'g'),
(11, 'Calcium', 250, 'mg');

INSERT INTO meal (meal_name, recipe_text, nutrition_id, rating) VALUES
('Chicken Caesar Salad',
'Grill chicken breast and slice. Toss romaine lettuce with Caesar dressing, parmesan cheese, and whole wheat croutons. Top with chicken.',
11, 4);

-- Meal 12: Sweet Potato and Black Bean Bowl
INSERT INTO nutrition (nutrition_id) VALUES (12);
INSERT INTO nutrition_facts (nutrition_id, fact_name, fact_value, unit) VALUES
(12, 'Calories', 400, 'kcal'),
(12, 'Protein', 15, 'g'),
(12, 'Carbohydrates', 70, 'g'),
(12, 'Fat', 8, 'g'),
(12, 'Fiber', 18, 'g');

INSERT INTO meal (meal_name, recipe_text, nutrition_id, rating) VALUES
('Sweet Potato and Black Bean Bowl',
'Roast cubed sweet potatoes at 425°F for 25 minutes. Combine with black beans, corn, avocado, and cilantro-lime dressing.',
12, 5);

-- Meal 13: Baked Cod with Roasted Vegetables
INSERT INTO nutrition (nutrition_id) VALUES (13);
INSERT INTO nutrition_facts (nutrition_id, fact_name, fact_value, unit) VALUES
(13, 'Calories', 310, 'kcal'),
(13, 'Protein', 35, 'g'),
(13, 'Carbohydrates', 25, 'g'),
(13, 'Fat', 8, 'g'),
(13, 'Vitamin A', 8000, 'IU');

INSERT INTO meal (meal_name, recipe_text, nutrition_id, rating) VALUES
('Baked Cod with Roasted Vegetables',
'Season cod with lemon, garlic, and herbs. Bake at 400°F for 12-15 minutes. Serve with roasted zucchini, bell peppers, and cherry tomatoes.',
13, 4);

-- Meal 14: Protein Smoothie Bowl
INSERT INTO nutrition (nutrition_id) VALUES (14);
INSERT INTO nutrition_facts (nutrition_id, fact_name, fact_value, unit) VALUES
(14, 'Calories', 320, 'kcal'),
(14, 'Protein', 25, 'g'),
(14, 'Carbohydrates', 42, 'g'),
(14, 'Fat', 8, 'g'),
(14, 'Potassium', 600, 'mg');

INSERT INTO meal (meal_name, recipe_text, nutrition_id, rating) VALUES
('Protein Smoothie Bowl',
'Blend banana, protein powder, almond milk, and spinach. Pour into bowl and top with sliced almonds, chia seeds, and fresh berries.',
14, 5);

-- Meal 15: Chicken Fajita Bowl
INSERT INTO nutrition (nutrition_id) VALUES (15);
INSERT INTO nutrition_facts (nutrition_id, fact_name, fact_value, unit) VALUES
(15, 'Calories', 420, 'kcal'),
(15, 'Protein', 40, 'g'),
(15, 'Carbohydrates', 45, 'g'),
(15, 'Fat', 12, 'g'),
(15, 'Vitamin C', 80, 'mg');

INSERT INTO meal (meal_name, recipe_text, nutrition_id, rating) VALUES
('Chicken Fajita Bowl',
'Sauté sliced chicken with bell peppers and onions in fajita seasoning. Serve over brown rice with black beans, salsa, and guacamole.',
15, 5);

-- ============================================================
-- LINK MEALS TO ALLERGIES
-- ============================================================

-- Meals containing dairy
INSERT INTO meal_allergy (meal_id, allergy_id) VALUES
(6, 3),  -- Greek Yogurt Parfait - Milk
(9, 3),  -- Egg White Omelet - Milk (feta)
(11, 3); -- Chicken Caesar Salad - Milk (parmesan)

-- Meals containing eggs
INSERT INTO meal_allergy (meal_id, allergy_id) VALUES
(3, 4),  -- Turkey Meatballs - Eggs
(9, 4);  -- Egg White Omelet - Eggs

-- Meals containing wheat/gluten
INSERT INTO meal_allergy (meal_id, allergy_id) VALUES
(3, 5),  -- Turkey Meatballs - Wheat (pasta)
(3, 10), -- Turkey Meatballs - Gluten
(7, 5),  -- Tuna Salad Wrap - Wheat
(7, 10), -- Tuna Salad Wrap - Gluten
(11, 5), -- Chicken Caesar Salad - Wheat (croutons)
(11, 10); -- Chicken Caesar Salad - Gluten

-- Meals containing fish
INSERT INTO meal_allergy (meal_id, allergy_id) VALUES
(2, 7),  -- Salmon - Fish
(7, 7),  -- Tuna Salad Wrap - Fish
(13, 7); -- Baked Cod - Fish

-- Meals containing shellfish
INSERT INTO meal_allergy (meal_id, allergy_id) VALUES
(10, 8); -- Shrimp Tacos - Shellfish

-- Meals containing tree nuts
INSERT INTO meal_allergy (meal_id, allergy_id) VALUES
(14, 2); -- Protein Smoothie Bowl - Tree Nuts (almonds)

-- ============================================================
-- VERIFICATION QUERIES
-- ============================================================

-- Count meals inserted
SELECT COUNT(*) AS total_meals FROM meal;

-- Show all meals with their nutrition
SELECT m.meal_id, m.meal_name, m.rating, 
       GROUP_CONCAT(CONCAT(nf.fact_name, ': ', nf.fact_value, nf.unit) SEPARATOR ', ') AS nutrition
FROM meal m
LEFT JOIN nutrition n ON m.nutrition_id = n.nutrition_id
LEFT JOIN nutrition_facts nf ON n.nutrition_id = nf.nutrition_id
GROUP BY m.meal_id, m.meal_name, m.rating
ORDER BY m.meal_id;

-- Show meals with their allergens
SELECT m.meal_name, GROUP_CONCAT(a.allergy_name SEPARATOR ', ') AS allergens
FROM meal m
LEFT JOIN meal_allergy ma ON m.meal_id = ma.meal_id
LEFT JOIN allergy a ON ma.allergy_id = a.allergy_id
GROUP BY m.meal_id, m.meal_name
ORDER BY m.meal_id;

-- ============================================================
-- END OF SAMPLE MEALS INSERTION
-- ============================================================
