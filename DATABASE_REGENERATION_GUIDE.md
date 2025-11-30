# Database Regeneration Guide

## Quick Regeneration

### Option 1: Using MySQL Command Line
```bash
mysql -u root -p < DATABASE_SCHEMA.sql
```

### Option 2: Using MySQL Workbench
1. Open MySQL Workbench
2. Connect to your MySQL server
3. File → Open SQL Script → Select `DATABASE_SCHEMA.sql`
4. Execute the script (⚡ icon or Ctrl+Shift+Enter)

### Option 3: Using Command Line with Password
```bash
mysql -u root -pMyNewPassword123! < DATABASE_SCHEMA.sql
```

## Full Regeneration Prompt for AI

Use this prompt to regenerate the entire database from scratch:

---

**PROMPT:**

Create a complete MySQL database schema for a Meal Planner API system with the following requirements:

### Database Name
- `mealplanerdb` (UTF-8 with utf8mb4_unicode_ci collation)

### Core Schemas

#### 1. User Management
- **users** table with: user_id (PK), full_name, email (unique), password_hash, email_verified (boolean), dob, phone_number, address, photo_url, role_id (FK), weight, height, gym_days, weight_goal, weekly_duration, calories_per_day
- **role** lookup table: role_id (PK), role_name (unique) - Values: USER, ADMIN, MODERATOR
- **allergy** lookup table: allergy_id (PK), allergy_name (unique)
- **user_allergy** join table: user_id + allergy_id (composite PK)
- **history** table: history_id (PK), user_id (FK), event_type, description, event_time (auto-timestamp)

#### 2. Authentication & Security
- **refresh_tokens**: id (PK), token (unique), user_id (FK), expiry_date, revoked (boolean)
- **email_verification_tokens**: id (PK), token (unique), user_id (FK), expiry_date, used (boolean)
- **password_reset_tokens**: id (PK), token (unique), user_id (FK), expiry_date, used (boolean)
- **blacklisted_tokens**: id (PK), token (unique), blacklisted_at, expiry_date

#### 3. Meal & Nutrition
- **nutrition**: nutrition_id (PK)
- **nutrition_facts**: fact_id (PK), nutrition_id (FK), fact_name, fact_value (decimal), unit
- **meal**: meal_id (PK), meal_name, recipe_text, nutrition_id (FK), rating
- **meal_allergy** join table: meal_id + allergy_id (composite PK)

#### 4. Custom Plans
- **plan_category** lookup: category_id (PK), category_name (unique) - Values: Weight Loss, Muscle Gain, Balanced Diet, Vegan, Keto, Low Carb, High Protein
- **custom_plan**: custom_plan_id (PK), user_id (FK), category_id (FK), duration_minutes, price (decimal)
- **custom_plan_meal** join table: custom_plan_id + meal_id (composite PK)

#### 5. Subscriptions
- **subscription_status** lookup: status_id (PK), status_name (unique) - Values: ACTIVE, PAUSED, CANCELLED, EXPIRED
- **subscription**: subscription_id (PK), user_id (FK), custom_plan_id (FK), start_date, plan_time (date), preferred_time (time), status_id (FK)
- **subscription_meal**: subscription_meal_id (PK), subscription_id (FK), meal_id (FK), delivery_date
- **subscription_history**: history_id (PK), subscription_id (FK), event_type, description, event_time (auto-timestamp)

#### 6. Delivery Tracking
- **delivery_status** lookup: status_id (PK), status_name (unique) - Values: PREPARING, SHIPPED, DELIVERED, CONFIRMED
- **delivery**: delivery_id (PK), subscription_meal_id (FK, unique), address, delivery_time (time), status_id (FK), created_at (timestamp), status_updated_at (timestamp), confirmed_at (timestamp), estimated_delivery_time (time)

### Requirements
- Use InnoDB engine for all tables
- Add appropriate indexes on foreign keys and frequently queried columns
- Use CASCADE delete for dependent records
- Use SET NULL for optional references
- Include initial data for all lookup tables
- Use AUTO_INCREMENT for all primary keys
- Proper data types: VARCHAR for strings, TEXT for long content, INT for IDs, DECIMAL(10,2) for prices, DATE/TIME/DATETIME/TIMESTAMP as appropriate

---

## Verification Steps

After regenerating the database, verify it's working:

```sql
-- Check all tables were created
SHOW TABLES;

-- Verify lookup tables have data
SELECT * FROM role;
SELECT * FROM subscription_status;
SELECT * FROM delivery_status;
SELECT * FROM plan_category;
SELECT * FROM allergy;

-- Check table structure
DESCRIBE users;
DESCRIBE subscription;
DESCRIBE delivery;
```

## Connection Configuration

Update your `application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/mealplanerdb?useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=MyNewPassword123!
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
```

## Troubleshooting

### If database exists and you want to keep data:
```sql
USE mealplanerdb;
-- Run only the CREATE TABLE statements for missing tables
-- Run only the INSERT statements for missing lookup data
```

### If you get "database exists" error:
```sql
DROP DATABASE IF EXISTS mealplanerdb;
-- Then run the full schema script
```

### If foreign key constraints fail:
- Ensure parent tables are created before child tables
- Check that referenced columns exist and have correct data types
- Verify InnoDB engine is being used

## Database Diagram

```
users ──┬─→ user_allergy ──→ allergy
        ├─→ history
        ├─→ custom_plan ──┬─→ custom_plan_meal ──→ meal ──┬─→ meal_allergy ──→ allergy
        │                 │                                 └─→ nutrition ──→ nutrition_facts
        │                 └─→ subscription ──┬─→ subscription_meal ──→ delivery
        │                                    └─→ subscription_history
        ├─→ refresh_tokens
        ├─→ email_verification_tokens
        ├─→ password_reset_tokens
        └─→ role

Lookup Tables:
- role
- allergy
- plan_category
- subscription_status
- delivery_status
```

## Table Count
Total: 23 tables
- Core entities: 10
- Join tables: 4
- Lookup tables: 5
- Security/Auth tables: 4
