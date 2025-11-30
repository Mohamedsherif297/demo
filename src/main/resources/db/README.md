# Database Migration for Delivery Tracking

## Overview
This directory contains database migration scripts for the delivery tracking system.

## Changes Made

### 1. Delivery Table Schema Updates
The following columns were added to the `delivery` table:
- `created_at` (TIMESTAMP) - Timestamp when the delivery was created
- `status_updated_at` (TIMESTAMP) - Timestamp of the last status update
- `confirmed_at` (TIMESTAMP) - Timestamp when the user confirmed delivery receipt
- `estimated_delivery_time` (TIME) - Estimated time for delivery

### 2. Delivery Status Values
The following status values are automatically inserted into the `delivery_status` table:
- PREPARING - Initial status when delivery is created
- SHIPPED - Delivery is in transit
- DELIVERED - Delivery has arrived at destination
- CONFIRMED - User has confirmed receipt

## Automatic Schema Management

The application uses `spring.jpa.hibernate.ddl-auto=update` which means:
- New columns are automatically added when the application starts
- Existing data is preserved
- No manual migration is required

## Data Initialization

The `DeliveryDataInitializer` configuration class ensures that all required delivery status values exist in the database on application startup. This runs automatically and is idempotent (safe to run multiple times).

## Manual Migration (Optional)

If you prefer to run migrations manually, you can use the provided SQL scripts:

### Using Flyway/Liquibase Migration
`V1__add_delivery_tracking_fields.sql` - Standard migration format

### Manual Execution
`init_delivery_tracking.sql` - Can be executed directly in MySQL

```bash
mysql -u root -p mealplanerdb < src/main/resources/db/init_delivery_tracking.sql
```

## Verification

After the application starts, you can verify the changes:

```sql
-- Check delivery table structure
DESCRIBE delivery;

-- Check delivery status values
SELECT * FROM delivery_status ORDER BY status_id;
```

Expected delivery_status values:
| status_id | status_name |
|-----------|-------------|
| 1         | PREPARING   |
| 2         | SHIPPED     |
| 3         | DELIVERED   |
| 4         | CONFIRMED   |
