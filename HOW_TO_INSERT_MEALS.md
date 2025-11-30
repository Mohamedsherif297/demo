# How to Insert Sample Meals into Database

## Option 1: Using MySQL Workbench (Recommended)
1. Open MySQL Workbench
2. Connect to your local MySQL server (localhost:3306)
3. Open the file `insert_sample_meals.sql`
4. Click the lightning bolt icon to execute the script
5. Check the output to verify 15 meals were inserted

## Option 2: Using Command Line
If you have MySQL in your PATH:
```bash
mysql -u root -pkekokeko2005 < insert_sample_meals.sql
```

## Option 3: Using phpMyAdmin
1. Open phpMyAdmin in your browser
2. Select the `mealplanerdb` database
3. Go to the "SQL" tab
4. Copy and paste the contents of `insert_sample_meals.sql`
5. Click "Go" to execute

## What's Included

The script inserts **15 sample meals** with:
- Complete nutrition information (calories, protein, carbs, fat, etc.)
- Detailed recipes
- Ratings (4-5 stars)
- Allergen information

### Sample Meals:
1. Grilled Chicken Breast with Vegetables (350 cal)
2. Salmon with Quinoa and Asparagus (450 cal)
3. Turkey Meatballs with Whole Wheat Pasta (420 cal)
4. Vegetarian Buddha Bowl (380 cal)
5. Beef Stir-Fry with Brown Rice (480 cal)
6. Greek Yogurt Parfait with Berries (280 cal)
7. Tuna Salad Wrap (320 cal)
8. Lentil Soup with Vegetables (290 cal)
9. Egg White Omelet with Spinach (180 cal)
10. Shrimp Tacos with Cabbage Slaw (340 cal)
11. Chicken Caesar Salad (380 cal)
12. Sweet Potato and Black Bean Bowl (400 cal)
13. Baked Cod with Roasted Vegetables (310 cal)
14. Protein Smoothie Bowl (320 cal)
15. Chicken Fajita Bowl (420 cal)

## Verify Installation

After running the script, you can verify by running this query:
```sql
SELECT COUNT(*) FROM meal;
```
You should see 15 meals.

To see all meals with nutrition:
```sql
SELECT m.meal_name, m.rating, 
       GROUP_CONCAT(CONCAT(nf.fact_name, ': ', nf.fact_value, nf.unit) SEPARATOR ', ') AS nutrition
FROM meal m
LEFT JOIN nutrition n ON m.nutrition_id = n.nutrition_id
LEFT JOIN nutrition_facts nf ON n.nutrition_id = nf.nutrition_id
GROUP BY m.meal_id, m.meal_name, m.rating;
```
