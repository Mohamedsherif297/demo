-- ============================================================
-- MEAL PLANNER DATABASE - COMPLETE SCHEMA
-- ============================================================
-- Database: mealplanerdb
-- This script creates all tables for the Meal Planner API system
-- ============================================================

-- Drop existing database and recreate (CAUTION: This will delete all data)
DROP DATABASE IF EXISTS mealplanerdb;
CREATE DATABASE mealplanerdb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE mealplanerdb;

-- ============================================================
-- USER MANAGEMENT SCHEMA
-- ============================================================

-- Role lookup table
CREATE TABLE role (
    role_id INT AUTO_INCREMENT PRIMARY KEY,
    role_name VARCHAR(50) NOT NULL UNIQUE
) ENGINE=InnoDB;

-- Users table
CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    email_verified BOOLEAN NOT NULL DEFAULT FALSE,
    dob DATE,
    phone_number VARCHAR(20),
    address TEXT,
    photo_url VARCHAR(500),
    role_id INT,
    weight DOUBLE,
    height DOUBLE,
    gym_days INT,
    weight_goal VARCHAR(50),
    weekly_duration INT,
    calories_per_day INT,
    FOREIGN KEY (role_id) REFERENCES role(role_id) ON DELETE SET NULL,
    INDEX idx_email (email),
    INDEX idx_role (role_id)
) ENGINE=InnoDB;

-- Allergy lookup table
CREATE TABLE allergy (
    allergy_id INT AUTO_INCREMENT PRIMARY KEY,
    allergy_name VARCHAR(100) NOT NULL UNIQUE
) ENGINE=InnoDB;

-- User allergies (many-to-many join table)
CREATE TABLE user_allergy (
    user_id INT NOT NULL,
    allergy_id INT NOT NULL,
    PRIMARY KEY (user_id, allergy_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (allergy_id) REFERENCES allergy(allergy_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- User activity history
CREATE TABLE history (
    history_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    event_type VARCHAR(100) NOT NULL,
    description TEXT,
    event_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_user_time (user_id, event_time)
) ENGINE=InnoDB;

-- ============================================================
-- AUTHENTICATION & SECURITY SCHEMA
-- ============================================================

-- Refresh tokens for JWT authentication
CREATE TABLE refresh_tokens (
    id INT AUTO_INCREMENT PRIMARY KEY,
    token VARCHAR(500) NOT NULL UNIQUE,
    user_id INT NOT NULL,
    expiry_date DATETIME NOT NULL,
    revoked BOOLEAN NOT NULL DEFAULT FALSE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_token (token),
    INDEX idx_user (user_id)
) ENGINE=InnoDB;

-- Email verification tokens
CREATE TABLE email_verification_tokens (
    id INT AUTO_INCREMENT PRIMARY KEY,
    token VARCHAR(500) NOT NULL UNIQUE,
    user_id INT NOT NULL,
    expiry_date DATETIME NOT NULL,
    used BOOLEAN NOT NULL DEFAULT FALSE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_token (token)
) ENGINE=InnoDB;

-- Password reset tokens
CREATE TABLE password_reset_tokens (
    id INT AUTO_INCREMENT PRIMARY KEY,
    token VARCHAR(500) NOT NULL UNIQUE,
    user_id INT NOT NULL,
    expiry_date DATETIME NOT NULL,
    used BOOLEAN NOT NULL DEFAULT FALSE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_token (token)
) ENGINE=InnoDB;

-- Blacklisted JWT tokens (for logout)
CREATE TABLE blacklisted_tokens (
    id INT AUTO_INCREMENT PRIMARY KEY,
    token VARCHAR(500) NOT NULL UNIQUE,
    blacklisted_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expiry_date DATETIME NOT NULL,
    INDEX idx_token (token),
    INDEX idx_expiry (expiry_date)
) ENGINE=InnoDB;

-- ============================================================
-- MEAL & NUTRITION SCHEMA
-- ============================================================

-- Nutrition information
CREATE TABLE nutrition (
    nutrition_id INT AUTO_INCREMENT PRIMARY KEY
) ENGINE=InnoDB;

-- Nutrition facts (composition relationship)
CREATE TABLE nutrition_facts (
    fact_id INT AUTO_INCREMENT PRIMARY KEY,
    nutrition_id INT NOT NULL,
    fact_name VARCHAR(100) NOT NULL,
    fact_value DECIMAL(10,2) NOT NULL,
    unit VARCHAR(20),
    FOREIGN KEY (nutrition_id) REFERENCES nutrition(nutrition_id) ON DELETE CASCADE,
    INDEX idx_nutrition (nutrition_id)
) ENGINE=InnoDB;

-- Meals table
CREATE TABLE meal (
    meal_id INT AUTO_INCREMENT PRIMARY KEY,
    meal_name VARCHAR(255) NOT NULL,
    recipe_text TEXT,
    nutrition_id INT,
    rating INT,
    FOREIGN KEY (nutrition_id) REFERENCES nutrition(nutrition_id) ON DELETE SET NULL,
    INDEX idx_nutrition (nutrition_id),
    INDEX idx_rating (rating)
) ENGINE=InnoDB;

-- Meal allergies (many-to-many join table)
CREATE TABLE meal_allergy (
    meal_id INT NOT NULL,
    allergy_id INT NOT NULL,
    PRIMARY KEY (meal_id, allergy_id),
    FOREIGN KEY (meal_id) REFERENCES meal(meal_id) ON DELETE CASCADE,
    FOREIGN KEY (allergy_id) REFERENCES allergy(allergy_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ============================================================
-- CUSTOM PLAN SCHEMA
-- ============================================================

-- Plan category lookup table
CREATE TABLE plan_category (
    category_id INT AUTO_INCREMENT PRIMARY KEY,
    category_name VARCHAR(100) NOT NULL UNIQUE
) ENGINE=InnoDB;

-- Custom meal plans
CREATE TABLE custom_plan (
    custom_plan_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    category_id INT,
    duration_minutes INT,
    price DECIMAL(10,2),
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES plan_category(category_id) ON DELETE SET NULL,
    INDEX idx_user (user_id),
    INDEX idx_category (category_id)
) ENGINE=InnoDB;

-- Custom plan meals (many-to-many join table)
CREATE TABLE custom_plan_meal (
    custom_plan_id INT NOT NULL,
    meal_id INT NOT NULL,
    PRIMARY KEY (custom_plan_id, meal_id),
    FOREIGN KEY (custom_plan_id) REFERENCES custom_plan(custom_plan_id) ON DELETE CASCADE,
    FOREIGN KEY (meal_id) REFERENCES meal(meal_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ============================================================
-- SUBSCRIPTION SCHEMA
-- ============================================================

-- Subscription status lookup table
CREATE TABLE subscription_status (
    status_id INT AUTO_INCREMENT PRIMARY KEY,
    status_name VARCHAR(50) NOT NULL UNIQUE
) ENGINE=InnoDB;

-- Subscriptions table
CREATE TABLE subscription (
    subscription_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    custom_plan_id INT NOT NULL,
    start_date DATE NOT NULL,
    plan_time DATE,
    preferred_time TIME,
    status_id INT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (custom_plan_id) REFERENCES custom_plan(custom_plan_id) ON DELETE CASCADE,
    FOREIGN KEY (status_id) REFERENCES subscription_status(status_id),
    INDEX idx_user (user_id),
    INDEX idx_plan (custom_plan_id),
    INDEX idx_status (status_id),
    INDEX idx_start_date (start_date)
) ENGINE=InnoDB;

-- Subscription meals (daily meal assignments)
CREATE TABLE subscription_meal (
    subscription_meal_id INT AUTO_INCREMENT PRIMARY KEY,
    subscription_id INT NOT NULL,
    meal_id INT NOT NULL,
    delivery_date DATE NOT NULL,
    FOREIGN KEY (subscription_id) REFERENCES subscription(subscription_id) ON DELETE CASCADE,
    FOREIGN KEY (meal_id) REFERENCES meal(meal_id) ON DELETE CASCADE,
    INDEX idx_subscription (subscription_id),
    INDEX idx_delivery_date (delivery_date)
) ENGINE=InnoDB;

-- Subscription history
CREATE TABLE subscription_history (
    history_id INT AUTO_INCREMENT PRIMARY KEY,
    subscription_id INT NOT NULL,
    event_type VARCHAR(100) NOT NULL,
    description TEXT,
    event_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (subscription_id) REFERENCES subscription(subscription_id) ON DELETE CASCADE,
    INDEX idx_subscription_time (subscription_id, event_time)
) ENGINE=InnoDB;

-- ============================================================
-- DELIVERY TRACKING SCHEMA
-- ============================================================

-- Delivery status lookup table
CREATE TABLE delivery_status (
    status_id INT AUTO_INCREMENT PRIMARY KEY,
    status_name VARCHAR(50) NOT NULL UNIQUE
) ENGINE=InnoDB;

-- Deliveries table
CREATE TABLE delivery (
    delivery_id INT AUTO_INCREMENT PRIMARY KEY,
    subscription_meal_id INT NOT NULL UNIQUE,
    address TEXT NOT NULL,
    delivery_time TIME,
    status_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status_updated_at TIMESTAMP NULL,
    confirmed_at TIMESTAMP NULL,
    estimated_delivery_time TIME NULL,
    FOREIGN KEY (subscription_meal_id) REFERENCES subscription_meal(subscription_meal_id) ON DELETE CASCADE,
    FOREIGN KEY (status_id) REFERENCES delivery_status(status_id),
    INDEX idx_subscription_meal (subscription_meal_id),
    INDEX idx_status (status_id),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB;

-- ============================================================
-- INITIAL DATA POPULATION
-- ============================================================

-- Insert default roles
INSERT INTO role (role_name) VALUES 
    ('USER'),
    ('ADMIN'),
    ('MODERATOR');

-- Insert subscription statuses
INSERT INTO subscription_status (status_name) VALUES 
    ('ACTIVE'),
    ('PAUSED'),
    ('CANCELLED'),
    ('EXPIRED');

-- Insert delivery statuses
INSERT INTO delivery_status (status_name) VALUES 
    ('PREPARING'),
    ('SHIPPED'),
    ('DELIVERED'),
    ('CONFIRMED');

-- Insert common plan categories
INSERT INTO plan_category (category_name) VALUES 
    ('Weight Loss'),
    ('Muscle Gain'),
    ('Balanced Diet'),
    ('Vegan'),
    ('Keto'),
    ('Low Carb'),
    ('High Protein');

-- Insert common allergies
INSERT INTO allergy (allergy_name) VALUES 
    ('Peanuts'),
    ('Tree Nuts'),
    ('Milk'),
    ('Eggs'),
    ('Wheat'),
    ('Soy'),
    ('Fish'),
    ('Shellfish'),
    ('Sesame'),
    ('Gluten');

-- ============================================================
-- END OF SCHEMA
-- ============================================================
