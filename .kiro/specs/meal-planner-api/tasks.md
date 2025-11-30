# Implementation Plan

- [x] 1. Set up core infrastructure and DTOs
  - [x] 1.1 Create exception classes and global exception handler
    - Create ResourceNotFoundException, ValidationException, BusinessRuleException
    - Implement GlobalExceptionHandler with @ControllerAdvice
    - Create ErrorResponseDto with timestamp, status, error, message, path fields
    - _Requirements: 12.1, 12.2, 12.3, 12.4, 13.2_

  - [x] 1.2 Create common response DTOs
    - Create PageResponseDto<T> with content, totalElements, totalPages, currentPage, pageSize
    - Ensure date/time serialization uses ISO 8601 format
    - _Requirements: 13.1, 13.3, 13.4_

  - [x] 1.3 Create meal-related DTOs
    - Create MealResponseDto (mealId, mealName, rating, nutritionSummary)
    - Create MealDetailDto extending MealResponseDto (add recipeText, nutrition, allergens)
    - Create CreateMealDto with validation annotations
    - Create UpdateMealDto with validation annotations
    - Create RateMealDto with @Min(1) @Max(5) validation
    - _Requirements: 1.1, 1.5, 2.1, 3.1, 3.2_

  - [x] 1.4 Create custom plan-related DTOs
    - Create PlanCategoryDto (categoryId, categoryName)
    - Create CustomPlanResponseDto (customPlanId, categoryName, durationMinutes, price, mealCount)
    - Create CustomPlanDetailDto extending CustomPlanResponseDto (add meals list)
    - Create CreateCustomPlanDto with validation annotations
    - Create UpdateCustomPlanDto with validation annotations
    - Create AddMealsToPlanDto with meal IDs list
    - _Requirements: 4.1, 4.2, 4.4, 5.1, 6.1_

  - [x] 1.5 Create subscription-related DTOs
    - Create SubscriptionResponseDto (subscriptionId, planName, startDate, status)
    - Create SubscriptionDetailDto extending SubscriptionResponseDto (add customPlan, preferredTime, planTime)
    - Create CreateSubscriptionDto with validation annotations and @FutureOrPresent for startDate
    - Create SubscriptionMealDto (mealId, mealName, scheduledDate)
    - _Requirements: 7.1, 7.3, 8.1, 8.3, 10.1_

- [x] 2. Implement repositories with custom queries
  - [x] 2.1 Create MealRepository with search and filter methods
    - Extend JpaRepository<Meal, Integer>
    - Add method: Page<Meal> findByMealNameContainingIgnoreCase(String name, Pageable pageable)
    - Add method: Page<Meal> findByRatingGreaterThanEqual(Integer minRating, Pageable pageable)
    - Add custom query to filter by excluded allergens
    - _Requirements: 1.1, 1.2, 1.3, 1.4_

  - [x] 2.2 Create CustomPlanRepository with category filtering
    - Extend JpaRepository<CustomPlan, Integer>
    - Add method: Page<CustomPlan> findByCategoryCategoryId(Integer categoryId, Pageable pageable)
    - Add method: List<CustomPlan> findByUserUserId(Integer userId)
    - Add method to check if plan has active subscriptions
    - _Requirements: 4.2, 4.3, 5.1, 6.3_

  - [x] 2.3 Create SubscriptionRepository with user and status filtering
    - Extend JpaRepository<Subscription, Integer>
    - Add method: Page<Subscription> findByUserUserId(Integer userId, Pageable pageable)
    - Add method: Page<Subscription> findByUserUserIdAndStatusStatusName(Integer userId, String status, Pageable pageable)
    - Add method: Page<Subscription> findByStatusStatusName(String status, Pageable pageable)
    - _Requirements: 8.1, 8.2, 11.1, 11.3_

  - [x] 2.4 Create supporting repositories
    - Create PlanCategoryRepository extending JpaRepository<PlanCategory, Integer>
    - Create SubscriptionStatusRepository extending JpaRepository<SubscriptionStatus, Integer>
    - Create SubscriptionMealRepository with date range query method
    - Create CustomPlanMealRepository for junction table operations
    - _Requirements: 4.1, 7.4, 10.2_

- [x] 3. Implement MealService with business logic
  - [x] 3.1 Implement meal browsing and search
    - Create getMeals method with pagination, name search, rating filter, allergen exclusion
    - Create getMealById method with complete details
    - Map entities to MealResponseDto and MealDetailDto
    - _Requirements: 1.1, 1.2, 1.3, 1.4, 1.5_

  - [x] 3.2 Write property test for meal search
    - **Property 2: Name search containment**
    - **Validates: Requirements 1.2**

  - [x] 3.3 Write property test for rating filter
    - **Property 3: Minimum rating filter**
    - **Validates: Requirements 1.3**

  - [x] 3.4 Write property test for allergen exclusion
    - **Property 4: Allergen exclusion filter**
    - **Validates: Requirements 1.4**

  - [x] 3.5 Implement meal rating functionality
    - Create rateMeal method accepting userId, mealId, and rating value
    - Validate rating is between 1 and 5
    - Update or create rating record
    - _Requirements: 2.1, 2.2, 2.3_

  - [x] 3.6 Write property test for valid rating acceptance
    - **Property 6: Valid rating acceptance**
    - **Validates: Requirements 2.1**

  - [x] 3.7 Write property test for invalid rating rejection
    - **Property 7: Invalid rating rejection**
    - **Validates: Requirements 2.2**

  - [x] 3.8 Implement admin meal management
    - Create createMeal method (admin only)
    - Create updateMeal method (admin only)
    - Create deleteMeal method (admin only)
    - Throw ResourceNotFoundException for non-existent meals
    - _Requirements: 3.1, 3.2, 3.3, 12.2_

  - [x] 3.9 Write property test for meal creation
    - **Property 9: Meal creation with ID generation**
    - **Validates: Requirements 3.1**

  - [x] 3.10 Write property test for meal update
    - **Property 10: Meal update persistence**
    - **Validates: Requirements 3.2**

  - [x] 3.11 Write property test for meal deletion
    - **Property 11: Meal deletion removes from catalog**
    - **Validates: Requirements 3.3**

- [x] 4. Implement CustomPlanService with business logic
  - [x] 4.1 Implement plan browsing and categories
    - Create getAllCategories method
    - Create getPlans method with pagination and category filtering
    - Create getPlanById method with complete details including meals
    - Map entities to response DTOs
    - _Requirements: 4.1, 4.2, 4.3, 4.4_

  - [x] 4.2 Write property test for plan category filter
    - **Property 13: Plan category filter**
    - **Validates: Requirements 4.3**

  - [x] 4.3 Write property test for plan detail completeness
    - **Property 14: Plan detail completeness**
    - **Validates: Requirements 4.4**

  - [x] 4.4 Implement user custom plan creation and management
    - Create createPlan method associating plan with authenticated user
    - Create updatePlan method with ownership verification
    - Create deletePlan method with ownership verification and cascade deletion
    - _Requirements: 5.1, 5.4_

  - [x] 4.5 Write property test for plan user association
    - **Property 15: Custom plan user association**
    - **Validates: Requirements 5.1**

  - [x] 4.6 Write property test for plan deletion cascade
    - **Property 18: Plan deletion cascades to associations**
    - **Validates: Requirements 5.4**

  - [x] 4.7 Implement meal-to-plan association management
    - Create addMealsToPlan method with ownership verification
    - Create removeMealFromPlan method with ownership verification
    - Ensure plan persists after meal removal
    - _Requirements: 5.2, 5.3_

  - [x] 4.8 Write property test for meal addition
    - **Property 16: Meal-to-plan association creation**
    - **Validates: Requirements 5.2**

  - [x] 4.9 Write property test for meal removal
    - **Property 17: Meal removal preserves plan**
    - **Validates: Requirements 5.3**

  - [x] 4.10 Implement admin plan management
    - Create admin createPlan method with category, duration, and pricing
    - Create admin updatePlan method preserving existing subscriptions
    - Create admin deletePlan method preventing deletion if active subscriptions exist
    - _Requirements: 6.1, 6.2, 6.3_

  - [ ]* 4.11 Write property test for admin plan creation
    - **Property 19: Admin plan creation with attributes**
    - **Validates: Requirements 6.1**

  - [ ]* 4.12 Write property test for plan update preserving subscriptions
    - **Property 20: Plan update preserves subscriptions**
    - **Validates: Requirements 6.2**

- [x] 5. Implement SubscriptionService with business logic
  - [x] 5.1 Implement subscription creation
    - Create createSubscription method with plan validation
    - Validate plan exists (throw ValidationException if not)
    - Validate start date is not in the past
    - Set initial status to "active"
    - _Requirements: 7.1, 7.2, 7.3, 7.4_

  - [ ]* 5.2 Write property test for subscription creation
    - **Property 21: Subscription creation with active status**
    - **Validates: Requirements 7.1, 7.4**

  - [ ]* 5.3 Write property test for invalid plan rejection
    - **Property 22: Invalid plan ID rejection**
    - **Validates: Requirements 7.2**

  - [ ]* 5.4 Write property test for past date rejection
    - **Property 23: Past start date rejection**
    - **Validates: Requirements 7.3**

  - [x] 5.5 Implement user subscription viewing
    - Create getUserSubscriptions method with status filtering
    - Create getSubscriptionById method with ownership verification
    - Throw ForbiddenException if user tries to access another user's subscription
    - Map entities to response DTOs
    - _Requirements: 8.1, 8.2, 8.3, 8.4_

  - [ ]* 5.6 Write property test for user subscription isolation
    - **Property 24: User subscription isolation**
    - **Validates: Requirements 8.1**

  - [ ]* 5.7 Write property test for subscription status filter
    - **Property 25: Subscription status filter**
    - **Validates: Requirements 8.2**

  - [x] 5.8 Implement subscription lifecycle management
    - Create pauseSubscription method with ownership verification
    - Create resumeSubscription method with ownership verification
    - Create cancelSubscription method with ownership verification
    - Update subscription status accordingly
    - _Requirements: 9.1, 9.2, 9.3, 9.4_

  - [ ]* 5.9 Write property test for pause operation
    - **Property 27: Pause updates status to paused**
    - **Validates: Requirements 9.1**

  - [ ]* 5.10 Write property test for resume operation
    - **Property 28: Resume updates status to active**
    - **Validates: Requirements 9.2**

  - [ ]* 5.11 Write property test for cancel operation
    - **Property 29: Cancel updates status to cancelled**
    - **Validates: Requirements 9.3**

  - [x] 5.12 Implement scheduled meals viewing
    - Create getScheduledMeals method with ownership verification
    - Support date range filtering
    - Return empty list when no meals scheduled
    - _Requirements: 10.1, 10.2, 10.3_

  - [ ]* 5.13 Write property test for scheduled meals date inclusion
    - **Property 30: Scheduled meals include dates**
    - **Validates: Requirements 10.1**

  - [ ]* 5.14 Write property test for date range filter
    - **Property 31: Date range filter for scheduled meals**
    - **Validates: Requirements 10.2**

  - [x] 5.15 Implement admin subscription viewing
    - Create getAllSubscriptions method for admins
    - Support filtering by user and status
    - Return paginated results
    - _Requirements: 11.1, 11.2, 11.3_

  - [ ]* 5.16 Write property test for admin viewing all subscriptions
    - **Property 32: Admin sees all users' subscriptions**
    - **Validates: Requirements 11.1**

- [x] 6. Implement REST controllers
  - [x] 6.1 Implement MealController
    - Create GET /api/meals endpoint with pagination and filters (public)
    - Create GET /api/meals/{id} endpoint (public)
    - Create POST /api/meals/{id}/rate endpoint (authenticated)
    - Create POST /api/meals endpoint (admin only with @PreAuthorize)
    - Create PUT /api/meals/{id} endpoint (admin only)
    - Create DELETE /api/meals/{id} endpoint (admin only)
    - Use @Valid for request body validation
    - Return appropriate HTTP status codes (200, 201, 204, 400, 401, 403, 404)
    - _Requirements: 1.1, 1.2, 1.3, 1.4, 1.5, 2.1, 3.1, 3.2, 3.3, 3.4_

  - [ ]* 6.2 Write property test for pagination size constraint
    - **Property 1: Pagination size constraint**
    - **Validates: Requirements 1.1**

  - [x] 6.3 Write property test for meal detail completeness
    - **Property 5: Meal detail completeness**
    - **Validates: Requirements 1.5**

  - [x] 6.4 Implement CustomPlanController
    - Create GET /api/plans/categories endpoint (public)
    - Create GET /api/plans endpoint with pagination and category filter (public)
    - Create GET /api/plans/{id} endpoint (public)
    - Create POST /api/plans endpoint (authenticated)
    - Create PUT /api/plans/{id} endpoint (owner or admin)
    - Create DELETE /api/plans/{id} endpoint (owner or admin)
    - Create POST /api/plans/{id}/meals endpoint (owner or admin)
    - Create DELETE /api/plans/{id}/meals/{mealId} endpoint (owner or admin)
    - Extract user ID from SecurityContextHolder
    - _Requirements: 4.1, 4.2, 4.3, 4.4, 5.1, 5.2, 5.3, 5.4, 6.1, 6.2, 6.3_

  - [ ]* 6.5 Write property test for plan pagination structure
    - **Property 12: Plan pagination structure**
    - **Validates: Requirements 4.2**

  - [x] 6.6 Implement SubscriptionController
    - Create POST /api/subscriptions endpoint (authenticated)
    - Create GET /api/subscriptions endpoint with status filter (authenticated)
    - Create GET /api/subscriptions/{id} endpoint (owner or admin)
    - Create PATCH /api/subscriptions/{id}/pause endpoint (owner)
    - Create PATCH /api/subscriptions/{id}/resume endpoint (owner)
    - Create PATCH /api/subscriptions/{id}/cancel endpoint (owner)
    - Create GET /api/subscriptions/{id}/meals endpoint with date range (owner or admin)
    - Create GET /api/admin/subscriptions endpoint (admin only)
    - Extract user ID from SecurityContextHolder
    - _Requirements: 7.1, 8.1, 8.2, 8.3, 9.1, 9.2, 9.3, 10.1, 10.2, 11.1, 11.2, 11.3_

  - [x] 6.7 Write property test for subscription detail completeness
    - **Property 26: Subscription detail completeness**
    - **Validates: Requirements 8.3**

- [x] 7. Implement validation and error handling
  - [x] 7.1 Add comprehensive validation annotations to all DTOs
    - Ensure @NotNull, @NotBlank on required fields
    - Add @Min, @Max for numeric constraints
    - Add @Size for string length constraints
    - Add custom @FutureOrPresent validator for subscription start dates
    - _Requirements: 12.1, 12.4_

  - [x] 7.2 Enhance GlobalExceptionHandler
    - Handle MethodArgumentNotValidException for validation errors
    - Extract field names and error messages for validation failures
    - Ensure all error responses include timestamp, status, error, message, path
    - Prevent exposure of internal details in error messages
    - _Requirements: 12.1, 12.2, 12.3, 12.4, 13.2_

  - [ ]* 7.3 Write property test for invalid data type rejection
    - **Property 35: Invalid data type rejection**
    - **Validates: Requirements 12.1**

  - [ ]* 7.4 Write property test for non-existent entity 404
    - **Property 36: Non-existent entity returns 404**
    - **Validates: Requirements 12.2**

  - [ ]* 7.5 Write property test for missing required fields
    - **Property 37: Missing required fields rejection**
    - **Validates: Requirements 12.4**

- [x] 8. Implement response format consistency
  - [x] 8.1 Configure Jackson for ISO 8601 date serialization
    - Add @JsonFormat annotations to date/time fields in DTOs
    - Configure ObjectMapper for ISO 8601 format globally
    - Test date serialization in responses
    - _Requirements: 13.4_

  - [x] 8.2 Ensure consistent response structures across all endpoints
    - Verify all success responses use appropriate HTTP status codes
    - Verify all paginated responses include complete metadata
    - Verify all error responses follow standardized format
    - _Requirements: 13.1, 13.2, 13.3_

  - [ ]* 8.3 Write property test for consistent success response structure
    - **Property 38: Consistent success response structure**
    - **Validates: Requirements 13.1**

  - [ ]* 8.4 Write property test for standardized error format
    - **Property 39: Standardized error response format**
    - **Validates: Requirements 13.2**

  - [ ]* 8.5 Write property test for pagination metadata
    - **Property 40: Pagination metadata completeness**
    - **Validates: Requirements 13.3**

  - [ ]* 8.6 Write property test for ISO 8601 date format
    - **Property 41: ISO 8601 date format**
    - **Validates: Requirements 13.4**

- [x] 9. Configure security and authorization
  - [x] 9.1 Update SecurityConfig to permit public endpoints
    - Permit GET /api/meals/** without authentication
    - Permit GET /api/plans/** without authentication
    - Require authentication for all other endpoints
    - Configure CORS if needed for frontend integration
    - _Requirements: 1.1, 1.5, 4.1, 4.2, 4.4_

  - [x] 9.2 Add method-level security annotations
    - Add @PreAuthorize("hasRole('ADMIN')") to admin-only methods
    - Implement ownership checks in service methods
    - Extract authenticated user from SecurityContextHolder
    - _Requirements: 3.4, 6.3, 8.4, 9.4_

  - [x] 9.3 Write unit tests for authorization
    - Test non-admin users cannot access admin endpoints
    - Test users cannot access other users' subscriptions
    - Test users cannot modify other users' plans
    - _Requirements: 3.4, 8.4, 9.4_

- [x] 10. Final checkpoint - Ensure all tests pass
  - Ensure all tests pass, ask the user if questions arise.
