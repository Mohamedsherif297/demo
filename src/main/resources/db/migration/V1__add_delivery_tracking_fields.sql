-- Migration script for delivery tracking system
-- Adds new columns to delivery table and inserts delivery status values

-- Add new columns to delivery table
ALTER TABLE delivery 
ADD COLUMN created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
ADD COLUMN status_updated_at TIMESTAMP NULL,
ADD COLUMN confirmed_at TIMESTAMP NULL,
ADD COLUMN estimated_delivery_time TIME NULL;

-- Insert delivery status values if they don't exist
INSERT INTO delivery_status (status_name) 
SELECT 'PREPARING' WHERE NOT EXISTS (SELECT 1 FROM delivery_status WHERE status_name = 'PREPARING');

INSERT INTO delivery_status (status_name) 
SELECT 'SHIPPED' WHERE NOT EXISTS (SELECT 1 FROM delivery_status WHERE status_name = 'SHIPPED');

INSERT INTO delivery_status (status_name) 
SELECT 'DELIVERED' WHERE NOT EXISTS (SELECT 1 FROM delivery_status WHERE status_name = 'DELIVERED');

INSERT INTO delivery_status (status_name) 
SELECT 'CONFIRMED' WHERE NOT EXISTS (SELECT 1 FROM delivery_status WHERE status_name = 'CONFIRMED');
