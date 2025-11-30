-- Manual initialization script for delivery tracking system
-- This script can be run manually to set up the delivery tracking schema
-- Note: The new columns will be created automatically by Hibernate with ddl-auto=update
-- This script primarily ensures the delivery status values exist

-- Insert delivery status values (using INSERT IGNORE to avoid duplicates)
INSERT IGNORE INTO delivery_status (status_name) VALUES ('PREPARING');
INSERT IGNORE INTO delivery_status (status_name) VALUES ('SHIPPED');
INSERT IGNORE INTO delivery_status (status_name) VALUES ('DELIVERED');
INSERT IGNORE INTO delivery_status (status_name) VALUES ('CONFIRMED');

-- Verify the status values were inserted
SELECT * FROM delivery_status ORDER BY status_id;
