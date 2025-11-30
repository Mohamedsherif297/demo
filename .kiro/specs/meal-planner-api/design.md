# Design Document

## Overview

The Meal Planner API provides RESTful endpoints for managing meals, custom meal plans, and subscriptions. The system follows a layered architecture with clear separation between controllers, services, repositories, and data models. It integrates with the existing Spring Boot authentication infrastructure using JWT tokens and role-based access control.

The API supports three primary domains:
1. **Meal Management** - Browse, search, rate, and manage meal catalog
2. **Custom Plan Management** - Create and manage personalized or pre-configured meal plans
3. **Subscription Management** - Subscribe to plans, manage subscription lifecycle, and view scheduled meals

## Architecture

### Layered Architecture

```
┌─────────────────────────────────────────┐
│         REST Controllers                │
│  (MealController, PlanController,       │
│   SubscriptionController)               │
└─────────────────┬───────────────────────┘
                  │
┌─────────────────▼───────────────────────┐
│         Service Layer                   │
│  (MealService, CustomPlanService,       │
│   SubscriptionService)                  │
└─────────────────┬───────────────────────┘
                  │
┌─────────────────▼───────────────────────┐
│         Repository Layer                │
│  (JPA Repositories)                     │
└─────────────────┬───────────────────────┘
                  │
┌─────────────────▼───────────────────────┐
│         Database (PostgreSQL)           │
└─────────────────────────────────────────┘
```

### Security Architecture

- JWT-based authentication using existing infrastructure
- Role-based authorization with USER and ADMIN roles
- Method-level security annotations (@PreAuthorize)
- User context extraction from SecurityContextHolder

## Components and Interfaces

### 1. Controllers

#### MealController
**Responsibility:** Handle HTTP requests for meal operations

**Endpoints:**
- `GET /api/meals` - List meals with pagination and filters (public)
- `GET /api/meals/{id}` - Get meal details (public)
- `POST /api/meals/{id}/rate` - Rate a meal (authenticated)
- `POST /api/meals` - Create meal (admin only)
- `PUT /api/meals/{id}` - Update meal (admin only)
- `DELETE /api/meals/{id}` - Delete meal (admin only)

**Query Parameters:**
- `page`, `size` - Pagination
- `search` - Name search
- `minRating` - Minimum rating filter
- `excludeAllergens` - Comma-separated allergen IDs

#### CustomPlanController
**Responsibility:** Handle HTTP requests for custom plan operations

**Endpoints:**
- `GET /api/plans/categories` - List all categories (public)
- `GET /api/plans` - List plans with pagination and filters (public)
- `GET /api/plans/{id}` - Get plan details (public)
- `POST /api/plans` - Create custom plan (authenticated)
- `PUT /api/plans/{id}` - Update plan (owner or admin)
- `DELETE /api/plans/{id}` - Delete plan (owner or admin)
- `POST /api/plans/{id}/meals` - Add meals to plan (owner or admin)
- `DELETE /api/plans/{id}/meals/{mealId}` - Remove meal from plan (owner or admin)

**Query Parameters:**
- `page`, `size` - Pagination
- `categoryId` - Filter by category

#### SubscriptionController
**Responsibility:** Handle HTTP requests for subscription operations

**Endpoints:**
- `POST /api/subscriptions` - Create subscription (authenticated)
- `GET /api/subscriptions` - List user's subscriptions (authenticated)
- `GET /api/subscriptions/{id}` - Get subscription details (owner or admin)
- `PATCH /api/subscriptions/{id}/pause` - Pause subscription (owner)
- `PATCH /api/subscriptions/{id}/resume` - Resume subscription (owner)
- `PATCH /api/subscriptions/{id}/cancel` - Cancel subscription (owner)
- `GET /api/subscriptions/{id}/meals` - Get scheduled meals (owner or admin)
- `GET /api/admin/subscriptions` - List all subscriptions (admin only)

**Query Parameters:**
- `status` - Filter by subscription status
- `userId` - Filter by user (admin only)
- `startDate`, `endDate` - Date range for scheduled meals

### 2. Services

#### MealService
**Responsibility:** Business logic for meal operations

**Methods:**
- `Page<MealResponseDto> getMeals(MealSearchCriteria criteria, Pageable pageable)`
- `MealDetailDto getMealById(Integer mealId)`
- `MealResponseDto createMeal(CreateMealDto dto)`
- `MealResponseDto updateMeal(Integer mealId, UpdateMealDto dto)`
- `void deleteMeal(Integer mealId)`
- `void rateMeal(Integer mealId, Integer userId, Integer rating)`

#### CustomPlanService
**Responsibility:** Business logic for custom plan operations

**Methods:**
- `List<PlanCategoryDto> getAllCategories()`
- `Page<CustomPlanResponseDto> getPlans(PlanSearchCriteria criteria, Pageable pageable)`
- `CustomPlanDetailDto getPlanById(Integer planId)`
- `CustomPlanResponseDto createPlan(Integer userId, CreateCustomPlanDto dto)`
- `CustomPlanResponseDto updatePlan(Integer planId, Integer userId, UpdateCustomPlanDto dto)`
- `void deletePlan(Integer planId, Integer userId, boolean isAdmin)`
- `void addMealsToPlan(Integer planId, Integer userId, List<Integer> mealIds)`
- `void removeMealFromPlan(Integer planId, Integer userId, Integer mealId)`

#### SubscriptionService
**Responsibility:** Business logic for subscription operations

**Methods:**
- `SubscriptionResponseDto createSubscription(Integer userId, CreateSubscriptionDto dto)`
- `Page<SubscriptionResponseDto> getUserSubscriptions(Integer userId, String status, Pageable pageable)`
- `SubscriptionDetailDto getSubscriptionById(Integer subscriptionId, Integer userId, boolean isAdmin)`
- `void pauseSubscription(Integer subscriptionId, Integer userId)`
- `void resumeSubscription(Integer subscriptionId, Integer userId)`
- `void cancelSubscription(Integer subscriptionId, Integer userId)`
- `List<SubscriptionMealDto> getScheduledMeals(Integer subscriptionId, Integer userId, LocalDate startDate, LocalDate endDate)`
- `Page<SubscriptionResponseDto> getAllSubscriptions(AdminSubscriptionCriteria criteria, Pageable pageable)`

### 3. DTOs (Data Transfer Objects)

#### Request DTOs
- `CreateMealDto` - mealName, recipeText, nutritionId, allergenIds
- `UpdateMealDto` - mealName, recipeText, nutritionId, rating, allergenIds
- `RateMealDto` - rating (1-5)
- `CreateCustomPlanDto` - categoryId, durationMinutes, price, mealIds
- `UpdateCustomPlanDto` - categoryId, durationMinutes, price
- `CreateSubscriptionDto` - customPlanId, startDate, preferredTime
- `AddMealsToP lanDto` - mealIds (list)

#### Response DTOs
- `MealResponseDto` - mealId, mealName, rating, nutritionSummary
- `MealDetailDto` - extends MealResponseDto + recipeText, nutrition, allergens
- `CustomPlanResponseDto` - customPlanId, categoryName, durationMinutes, price, mealCount
- `CustomPlanDetailDto` - extends CustomPlanResponseDto + meals list
- `SubscriptionResponseDto` - subscriptionId, planName, startDate, status
- `SubscriptionDetailDto` - extends SubscriptionResponseDto + customPlan, preferredTime, planTime
- `SubscriptionMealDto` - mealId, mealName, scheduledDate
- `PlanCategoryDto` - categoryId, categoryName
- `PageResponseDto<T>` - content, totalElements, totalPages, currentPage, pageSize

#### Error Response
- `ErrorResponseDto` - timestamp, status, error, message, path

### 4. Repositories

All repositories extend `JpaRepository` and use Spring Data JPA query methods:

- `MealRepository` - Custom queries for search and filtering
- `CustomPlanRepository` - Custom queries for user plans and category filtering
- `SubscriptionRepository` - Custom queries for user subscriptions and status filtering
- `PlanCategoryRepository` - Basic CRUD
- `SubscriptionMealRepository` - Custom queries for date range filtering
- `MealAllergyRepository` - Junction table operations
- `CustomPlanMealRepository` - Junction table operations

## Data Models

The system uses existing JPA entities:
- `Meal` - Core meal entity with nutrition and allergen relationships
- `CustomPlan` - Plan entity with user and category relationships
- `Subscription` - Subscription entity with user, plan, and status relationships
- `PlanCategory` - Lookup table for plan categories
- `SubscriptionStatus` - Lookup table for subscription statuses
- `Nutrition` - Nutritional information entity
- `Allergen` - Allergen entity
- `MealAllergy` - Junction table for meal-allergen relationships
- `CustomPlanMeal` - Junction table for plan-meal relationships
- `SubscriptionMeal` - Scheduled meals for subscriptions
- `User` - Existing user entity from authentication system


## Correctness Properties

*A property is a characteristic or behavior that should hold true across all valid executions of a system—essentially, a formal statement about what the system should do. Properties serve as the bridge between human-readable specifications and machine-verifiable correctness guarantees.*

### Property 1: Pagination size constraint
*For any* valid pagination request to the meal catalog, the number of returned meals should not exceed the requested page size.
**Validates: Requirements 1.1**

### Property 2: Name search containment
*For any* meal name search query, all returned meals should have names that contain the search term (case-insensitive).
**Validates: Requirements 1.2**

### Property 3: Minimum rating filter
*For any* minimum rating filter value, all returned meals should have ratings greater than or equal to the specified minimum.
**Validates: Requirements 1.3**

### Property 4: Allergen exclusion filter
*For any* set of excluded allergen IDs, all returned meals should not contain any of the specified allergens.
**Validates: Requirements 1.4**

### Property 5: Meal detail completeness
*For any* valid meal ID, the meal detail response should include all required fields: mealId, mealName, recipeText, nutrition, allergens, and rating.
**Validates: Requirements 1.5**

### Property 6: Valid rating acceptance
*For any* rating value in the range [1, 5], the system should accept and persist the rating for a meal.
**Validates: Requirements 2.1**

### Property 7: Invalid rating rejection
*For any* rating value outside the range [1, 5], the system should reject the request with a validation error.
**Validates: Requirements 2.2**

### Property 8: Rating update idempotence
*For any* meal and user, submitting a new rating should replace any existing rating, and retrieving the rating should return the most recent value.
**Validates: Requirements 2.3**

### Property 9: Meal creation with ID generation
*For any* valid meal creation request by an administrator, the system should persist the meal and return it with a generated ID that is non-null and positive.
**Validates: Requirements 3.1**

### Property 10: Meal update persistence
*For any* existing meal, when an administrator updates it with new data, retrieving the meal should return the updated values.
**Validates: Requirements 3.2**

### Property 11: Meal deletion removes from catalog
*For any* existing meal, after an administrator deletes it, attempting to retrieve the meal should result in a not found error.
**Validates: Requirements 3.3**

### Property 12: Plan pagination structure
*For any* valid pagination request to the custom plans endpoint, the response should include page metadata with totalElements, totalPages, currentPage, and pageSize.
**Validates: Requirements 4.2**

### Property 13: Plan category filter
*For any* category ID filter, all returned custom plans should belong to the specified category.
**Validates: Requirements 4.3**

### Property 14: Plan detail completeness
*For any* valid plan ID, the plan detail response should include complete information: planId, category, duration, price, and associated meals list.
**Validates: Requirements 4.4**

### Property 15: Custom plan user association
*For any* authenticated user creating a custom plan, the persisted plan should be associated with that user's ID.
**Validates: Requirements 5.1**

### Property 16: Meal-to-plan association creation
*For any* custom plan and set of meal IDs, after adding the meals to the plan, querying the plan details should include all added meals.
**Validates: Requirements 5.2**

### Property 17: Meal removal preserves plan
*For any* custom plan with associated meals, removing a meal should delete only the association while the plan itself remains retrievable.
**Validates: Requirements 5.3**

### Property 18: Plan deletion cascades to associations
*For any* custom plan with meal associations, after deleting the plan, both the plan and all its meal associations should be removed from the system.
**Validates: Requirements 5.4**

### Property 19: Admin plan creation with attributes
*For any* administrator creating a custom plan with specified category, duration, and price, the persisted plan should contain all these attributes with matching values.
**Validates: Requirements 6.1**

### Property 20: Plan update preserves subscriptions
*For any* custom plan with existing subscriptions, after updating the plan's attributes, all subscriptions should still reference the same plan ID.
**Validates: Requirements 6.2**

### Property 21: Subscription creation with active status
*For any* valid subscription creation request with a future start date and valid plan ID, the created subscription should have status set to "active".
**Validates: Requirements 7.1, 7.4**

### Property 22: Invalid plan ID rejection
*For any* subscription creation request with a non-existent plan ID, the system should reject the request with a validation error.
**Validates: Requirements 7.2**

### Property 23: Past start date rejection
*For any* subscription creation request with a start date before the current date, the system should reject the request with a validation error.
**Validates: Requirements 7.3**

### Property 24: User subscription isolation
*For any* authenticated user requesting their subscriptions, the returned list should contain only subscriptions where the user ID matches the authenticated user.
**Validates: Requirements 8.1**

### Property 25: Subscription status filter
*For any* subscription status filter value, all returned subscriptions should have a status matching the specified value.
**Validates: Requirements 8.2**

### Property 26: Subscription detail completeness
*For any* valid subscription ID, the subscription detail response should include complete information: subscriptionId, user, customPlan, startDate, status, preferredTime, and assigned meals.
**Validates: Requirements 8.3**

### Property 27: Pause updates status to paused
*For any* active subscription, after a pause operation, retrieving the subscription should show status as "paused".
**Validates: Requirements 9.1**

### Property 28: Resume updates status to active
*For any* paused subscription, after a resume operation, retrieving the subscription should show status as "active".
**Validates: Requirements 9.2**

### Property 29: Cancel updates status to cancelled
*For any* subscription, after a cancel operation, retrieving the subscription should show status as "cancelled".
**Validates: Requirements 9.3**

### Property 30: Scheduled meals include dates
*For any* subscription with scheduled meals, each meal in the response should include a non-null scheduledDate field.
**Validates: Requirements 10.1**

### Property 31: Date range filter for scheduled meals
*For any* date range filter (startDate, endDate), all returned scheduled meals should have scheduledDate values within the specified range (inclusive).
**Validates: Requirements 10.2**

### Property 32: Admin sees all users' subscriptions
*For any* administrator requesting all subscriptions without filters, the returned list should include subscriptions from multiple different users.
**Validates: Requirements 11.1**

### Property 33: Admin user filter
*For any* administrator filtering subscriptions by user ID, all returned subscriptions should belong to the specified user.
**Validates: Requirements 11.2**

### Property 34: Admin status filter
*For any* administrator filtering subscriptions by status, all returned subscriptions should have the specified status.
**Validates: Requirements 11.3**

### Property 35: Invalid data type rejection
*For any* request with invalid data types (e.g., string where integer expected), the system should reject the request with a validation error describing the type mismatch.
**Validates: Requirements 12.1**

### Property 36: Non-existent entity returns 404
*For any* request referencing a non-existent entity by ID, the system should return an HTTP 404 status code with a not found error message.
**Validates: Requirements 12.2**

### Property 37: Missing required fields rejection
*For any* request missing required fields, the system should reject the request with a validation error that lists all missing field names.
**Validates: Requirements 12.4**

### Property 38: Consistent success response structure
*For any* successful API operation returning data, the response should use consistent JSON structure with appropriate HTTP 2xx status codes.
**Validates: Requirements 13.1**

### Property 39: Standardized error response format
*For any* error condition, the error response should include all required fields: timestamp, status, error, message, and path.
**Validates: Requirements 13.2**

### Property 40: Pagination metadata completeness
*For any* paginated response, the response should include metadata fields: totalElements, totalPages, currentPage (or number), and pageSize (or size).
**Validates: Requirements 13.3**

### Property 41: ISO 8601 date format
*For any* response containing date or datetime fields, the values should be formatted according to ISO 8601 standard (e.g., "2024-01-15" or "2024-01-15T10:30:00Z").
**Validates: Requirements 13.4**

## Error Handling

### Exception Hierarchy

```
RuntimeException
├── ResourceNotFoundException (404)
├── ValidationException (400)
├── UnauthorizedException (401)
├── ForbiddenException (403)
└── BusinessRuleException (422)
```

### Global Exception Handler

A `@ControllerAdvice` class will handle all exceptions and return standardized error responses:

```java
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleNotFound(ResourceNotFoundException ex);
    
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponseDto> handleValidation(ValidationException ex);
    
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponseDto> handleAccessDenied(AccessDeniedException ex);
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleMethodArgumentNotValid(MethodArgumentNotValidException ex);
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGeneral(Exception ex);
}
```

### Validation Strategy

- Use Jakarta Bean Validation annotations (@NotNull, @NotBlank, @Min, @Max, @Size, @Email, @Pattern)
- Custom validators for business rules (e.g., @FutureDate for subscription start dates)
- Service-layer validation for complex business rules
- Repository-layer constraint handling with meaningful error messages

### Error Response Format

All errors return:
```json
{
  "timestamp": "2024-01-15T10:30:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed for field 'rating': must be between 1 and 5",
  "path": "/api/meals/123/rate"
}
```

## Testing Strategy

### Unit Testing

Unit tests will verify specific examples and edge cases:

- **Controller Tests**: Mock service layer, test request/response mapping, validation, and HTTP status codes
- **Service Tests**: Mock repository layer, test business logic, authorization checks, and exception handling
- **Repository Tests**: Use @DataJpaTest with H2 in-memory database, test custom queries and data integrity
- **DTO Validation Tests**: Test Jakarta Bean Validation annotations with valid and invalid inputs

Key unit test scenarios:
- Empty result sets return empty lists/pages
- Null handling and optional fields
- Boundary values (min/max ratings, page sizes)
- Authorization checks for user vs admin operations
- Cascade operations (delete plan with meals)

### Property-Based Testing

Property-based tests will verify universal properties across all inputs using **JUnit-Quickcheck** library.

**Configuration:**
- Minimum 100 iterations per property test
- Custom generators for domain objects (Meal, CustomPlan, Subscription)
- Shrinking enabled to find minimal failing cases

**Property Test Requirements:**
- Each property test MUST be tagged with a comment: `// Feature: meal-planner-api, Property {number}: {property_text}`
- Each correctness property MUST be implemented by a SINGLE property-based test
- Tests should use realistic data generators that respect domain constraints

**Generator Strategy:**
- `MealGenerator`: Generate meals with valid names, recipes, ratings (1-5), and allergen associations
- `CustomPlanGenerator`: Generate plans with valid categories, positive durations, positive prices
- `SubscriptionGenerator`: Generate subscriptions with future start dates, valid statuses
- `PaginationGenerator`: Generate valid page numbers (≥0) and page sizes (1-100)
- `DateRangeGenerator`: Generate valid date ranges where startDate ≤ endDate

**Example Property Test Structure:**
```java
@Property
// Feature: meal-planner-api, Property 2: Name search containment
public void searchByNameReturnsOnlyMatchingMeals(@ForAll String searchTerm, @ForAll List<Meal> meals) {
    // Setup: Save meals to repository
    // Execute: Search by name
    // Verify: All results contain searchTerm in name
}
```

### Integration Testing

Integration tests will verify end-to-end flows:
- Use @SpringBootTest with TestRestTemplate
- Test complete request/response cycles including authentication
- Verify database state changes
- Test transaction boundaries and rollback behavior

### Test Data Management

- Use test fixtures for common scenarios
- Implement test data builders for complex objects
- Clean database state between tests (@Transactional with rollback)
- Separate test data for unit vs integration tests

## Performance Considerations

- **Lazy Loading**: Use `@ManyToOne(fetch = FetchType.LAZY)` to avoid N+1 queries
- **Pagination**: Enforce maximum page size (e.g., 100) to prevent large result sets
- **Query Optimization**: Use JOIN FETCH for associations needed in responses
- **Caching**: Consider caching for plan categories and subscription statuses (rarely change)
- **Indexing**: Ensure database indexes on foreign keys and frequently queried fields

## Security Considerations

- **Authentication**: All endpoints except public browsing require JWT authentication
- **Authorization**: Role-based access control using @PreAuthorize annotations
- **Data Isolation**: Users can only access their own subscriptions and plans
- **Input Validation**: Sanitize all inputs to prevent injection attacks
- **Error Messages**: Don't expose sensitive information in error responses
- **Rate Limiting**: Consider implementing rate limiting for public endpoints

## API Versioning

- Use URI versioning: `/api/v1/meals`, `/api/v1/subscriptions`
- Current implementation is v1
- Future versions maintain backward compatibility or provide migration path

## Documentation

- OpenAPI/Swagger documentation auto-generated from annotations
- Include example requests/responses for each endpoint
- Document authentication requirements
- Provide error code reference
