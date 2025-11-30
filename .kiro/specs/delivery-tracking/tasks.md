# Implementation Plan: Delivery Tracking System

- [x] 1. Set up database schema and initial data
  - Add new columns to delivery table (created_at, status_updated_at, confirmed_at, estimated_delivery_time)
  - Create database migration script
  - Insert delivery status values (PREPARING, SHIPPED, DELIVERED, CONFIRMED)
  - _Requirements: 2.3, 3.3, 5.4_

- [x] 2. Create DTOs for delivery operations
  - Create DeliveryResponseDto with all required fields
  - Create UpdateDeliveryDto for preference updates
  - Create DeliveryHistoryDto for history listing
  - Create AdminDeliveryDto with extended information
  - _Requirements: 4.3, 7.1-7.4, 9.2, 10.2_

- [x] 3. Enhance Delivery model and repository
  - [x] 3.1 Update Delivery entity with new fields
    - Add createdAt, statusUpdatedAt, confirmedAt, estimatedDeliveryTime fields
    - Add appropriate JPA annotations
    - Update constructors and getters/setters
    - _Requirements: 2.3, 3.3, 5.4_

  - [ ]* 3.2 Write property test for Delivery model
    - **Property 4: Initial status invariant**
    - **Validates: Requirements 2.3**

  - [x] 3.3 Add custom query methods to DeliveryRepository
    - Add findBySubscriptionMeal_Subscription_UserUserIdAndSubscriptionMeal_DeliveryDate
    - Add findBySubscriptionMeal_Subscription_UserUserIdOrderBySubscriptionMeal_DeliveryDateDesc
    - Add findByStatus_StatusName and findByStatus_StatusNameIn
    - Add admin search methods with Specification support
    - _Requirements: 4.1, 4.5, 9.1, 9.5_

  - [ ]* 3.4 Write property test for repository queries
    - **Property 13: History ordering invariant**
    - **Validates: Requirements 4.5**

- [x] 4. Implement DeliveryService core functionality
  - [x] 4.1 Implement createDailyDelivery method
    - Create delivery for given subscription and date
    - Set initial status to PREPARING
    - Set delivery time from subscription preferred time
    - Associate with subscription meals
    - Set createdAt timestamp
    - _Requirements: 2.1, 2.3, 2.4, 2.5_

  - [ ]* 4.2 Write property test for delivery creation
    - **Property 5: Delivery time inheritance**
    - **Validates: Requirements 2.4**

  - [ ]* 4.3 Write property test for subscription meal association
    - **Property 6: Subscription meal association**
    - **Validates: Requirements 2.5**

  - [x] 4.4 Implement getCurrentDelivery method
    - Query delivery for current user and today's date
    - Return 404 if no delivery found
    - Map to DeliveryResponseDto
    - _Requirements: 4.1, 4.4_

  - [x] 4.5 Implement getDeliveryById method
    - Verify delivery ownership or admin role
    - Return delivery details
    - Include associated meals
    - _Requirements: 4.2, 4.3_

  - [ ]* 4.6 Write property test for delivery ownership
    - **Property 11: Delivery ownership authorization**
    - **Validates: Requirements 4.2**

  - [ ]* 4.7 Write property test for response completeness
    - **Property 12: Response completeness**
    - **Validates: Requirements 4.3**

  - [x] 4.8 Implement getDeliveryHistory method
    - Query all past deliveries for user
    - Order by date descending
    - Support pagination
    - Support filtering by date range and status
    - _Requirements: 4.5, 10.1, 10.3, 10.5_

  - [ ]* 4.9 Write property test for history filtering
    - **Property 29: History filtering**
    - **Validates: Requirements 10.3**

  - [ ]* 4.10 Write property test for pagination
    - **Property 31: Pagination correctness**
    - **Validates: Requirements 10.5**

- [x] 5. Implement delivery preference updates
  - [x] 5.1 Implement updateDeliveryPreferences method
    - Verify delivery ownership
    - Validate delivery is in PREPARING status
    - Validate time format and address
    - Update delivery time and/or address
    - Recalculate status progression timing
    - _Requirements: 8.1, 8.2, 8.3, 8.4, 8.5_

  - [ ]* 5.2 Write property test for update validation
    - **Property 21: Update rejected after shipment**
    - **Validates: Requirements 8.3**

  - [ ]* 5.3 Write property test for update allowed in Preparing
    - **Property 20: Update allowed in Preparing status**
    - **Validates: Requirements 8.1, 8.2**

  - [ ]* 5.4 Write property test for input validation
    - **Property 22: Update input validation**
    - **Validates: Requirements 8.4**

- [x] 6. Implement delivery confirmation
  - [x] 6.1 Implement confirmDelivery method
    - Verify delivery ownership
    - Validate delivery is in DELIVERED status
    - Update status to CONFIRMED
    - Set confirmedAt timestamp
    - Handle idempotent confirmations
    - _Requirements: 5.1, 5.2, 5.3, 5.4, 5.5_

  - [ ]* 6.2 Write property test for confirmation state transition
    - **Property 14: Confirmation state transition**
    - **Validates: Requirements 5.1, 5.4**

  - [ ]* 6.3 Write property test for confirmation validation
    - **Property 15: Confirmation validation**
    - **Validates: Requirements 5.2**

  - [ ]* 6.4 Write property test for confirmation authorization
    - **Property 16: Confirmation authorization**
    - **Validates: Requirements 5.3**

  - [ ]* 6.5 Write property test for confirmation idempotence
    - **Property 17: Confirmation idempotence**
    - **Validates: Requirements 5.5**

- [x] 7. Checkpoint - Ensure all tests pass
  - Ensure all tests pass, ask the user if questions arise.

- [x] 8. Implement DeliverySchedulerService
  - [x] 8.1 Implement createDailyDeliveries scheduled job
    - Run daily at midnight
    - Query all active subscriptions
    - Create delivery for each subscription for current day
    - Skip if delivery already exists
    - _Requirements: 2.1, 2.2, 11.1, 11.2_

  - [ ]* 8.2 Write property test for daily delivery creation
    - **Property 3: Daily delivery creation**
    - **Validates: Requirements 2.2**

  - [ ]* 8.3 Write property test for cancelled subscription
    - **Property 32: Cancelled subscription no delivery**
    - **Validates: Requirements 11.1**

  - [ ]* 8.4 Write property test for paused subscription
    - **Property 33: Paused subscription no delivery**
    - **Validates: Requirements 11.2**

  - [x] 8.5 Implement updateDeliveryStatuses scheduled job
    - Run every minute
    - Query deliveries in PREPARING and SHIPPED status
    - Apply status progression logic based on time
    - Update status and statusUpdatedAt timestamp
    - Handle catch-up for late deliveries
    - _Requirements: 3.1, 3.2, 3.3, 3.4, 11.3_

  - [ ]* 8.6 Write property test for status progression from Preparing
    - **Property 7: Status progression from Preparing**
    - **Validates: Requirements 3.1**

  - [ ]* 8.7 Write property test for status progression from Shipped
    - **Property 8: Status progression from Shipped**
    - **Validates: Requirements 3.2**

  - [ ]* 8.8 Write property test for status update timestamp
    - **Property 9: Status update timestamp invariant**
    - **Validates: Requirements 3.3**

  - [ ]* 8.9 Write property test for delivered status idempotence
    - **Property 10: Delivered status idempotence**
    - **Validates: Requirements 3.4**

  - [ ]* 8.10 Write property test for status sequence
    - **Property 18: Status sequence invariant**
    - **Validates: Requirements 6.5**

  - [ ]* 8.11 Write property test for catch-up progression
    - **Property 34: Catch-up status progression**
    - **Validates: Requirements 11.3**

- [x] 9. Create DeliveryController with user endpoints
  - [x] 9.1 Implement GET /api/deliveries/current endpoint
    - Extract user ID from security context
    - Call deliveryService.getCurrentDelivery
    - Return 200 with delivery or 404 if not found
    - _Requirements: 7.1, 4.1_

  - [x] 9.2 Implement GET /api/deliveries/{id} endpoint
    - Extract user ID from security context
    - Call deliveryService.getDeliveryById
    - Return 200 with delivery or 404/403 for errors
    - _Requirements: 7.2, 4.2_

  - [x] 9.3 Implement GET /api/deliveries/history endpoint
    - Extract user ID from security context
    - Support pagination and filtering parameters
    - Call deliveryService.getDeliveryHistory
    - Return 200 with paginated results
    - _Requirements: 7.4, 4.5, 10.1_

  - [x] 9.4 Implement PATCH /api/deliveries/{id} endpoint
    - Extract user ID from security context
    - Validate UpdateDeliveryDto
    - Call deliveryService.updateDeliveryPreferences
    - Return 200 with updated delivery or 400/403 for errors
    - _Requirements: 8.1, 8.2, 8.3_

  - [x] 9.5 Implement POST /api/deliveries/{id}/confirm endpoint
    - Extract user ID from security context
    - Call deliveryService.confirmDelivery
    - Return 200 with confirmed delivery or 400/403 for errors
    - _Requirements: 7.3, 5.1_

  - [ ]* 9.6 Write property test for authentication requirement
    - **Property 19: Authentication requirement**
    - **Validates: Requirements 7.5**

- [x] 10. Create AdminDeliveryController with admin endpoints
  - [x] 10.1 Implement GET /api/admin/deliveries endpoint
    - Require ADMIN role
    - Support filtering by status, date, user
    - Support pagination
    - Call deliveryService with admin flag
    - Return 200 with paginated results
    - _Requirements: 9.1, 9.5_

  - [ ]* 10.2 Write property test for admin filtering
    - **Property 24: Admin filtering**
    - **Validates: Requirements 9.1**

  - [ ]* 10.3 Write property test for admin search
    - **Property 27: Admin search functionality**
    - **Validates: Requirements 9.5**

  - [x] 10.4 Implement GET /api/admin/deliveries/{id} endpoint
    - Require ADMIN role
    - Return complete delivery details with user info
    - Include status history
    - _Requirements: 9.2, 9.4_

  - [ ]* 10.5 Write property test for status history tracking
    - **Property 26: Status history tracking**
    - **Validates: Requirements 9.4**

  - [x] 10.6 Implement PATCH /api/admin/deliveries/{id}/status endpoint
    - Require ADMIN role
    - Validate status value
    - Update delivery status manually
    - Record admin action
    - _Requirements: 9.3_

  - [ ]* 10.7 Write property test for admin status update
    - **Property 25: Admin status update**
    - **Validates: Requirements 9.3**

- [x] 11. Update subscription creation to include preferred time
  - [x] 11.1 Update CreateSubscriptionDto to require preferredTime
    - Add validation for time format
    - Update SubscriptionService.createSubscription
    - _Requirements: 1.1, 1.2_

  - [ ]* 11.2 Write property test for preferred time validation
    - **Property 1: Preferred time validation**
    - **Validates: Requirements 1.2**

  - [x] 11.3 Write property test for preferred time persistence
    - **Property 2: Preferred time persistence (Round-trip)**
    - **Validates: Requirements 1.3**


  - [x] 11.4 Update subscription update endpoint to allow time modification
    - Add PATCH /api/subscriptions/{id}/preferences endpoint
    - Allow updating preferred delivery time
    - _Requirements: 1.4_

- [x] 12. Add exception handling and validation
  - Create custom exceptions for delivery operations
  - Add global exception handler for delivery errors
  - Implement validation for all DTOs
  - Add appropriate error messages
  - _Requirements: All error handling requirements_

- [x] 13. Final checkpoint - Ensure all tests pass
  - Ensure all tests pass, ask the user if questions arise.

- [x] 14. Update API documentation
  - Document all new delivery endpoints
  - Add request/response examples
  - Document error responses
  - Update authentication requirements
  - _Requirements: 7.1-7.5_
