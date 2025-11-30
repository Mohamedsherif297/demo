# Design Document: Delivery Tracking System

## Overview

The Delivery Tracking System provides real-time tracking of daily meal deliveries for subscribed users. The system automatically creates daily delivery records for active subscriptions, progresses delivery status through predefined stages based on time, and allows users to confirm receipt. The design integrates with the existing subscription system and provides both user-facing and administrative interfaces.

### Key Features
- Automatic daily delivery creation for active subscriptions
- Time-based automatic status progression (Preparing → Shipped → Delivered)
- User confirmation of delivery receipt
- Delivery preference modification before shipment
- Comprehensive delivery history tracking
- Administrative monitoring and management capabilities

## Architecture

### System Components

```
┌─────────────────────────────────────────────────────────────┐
│                     Presentation Layer                       │
│  ┌──────────────────┐         ┌──────────────────────────┐  │
│  │ DeliveryController│         │ AdminDeliveryController │  │
│  └──────────────────┘         └──────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
                              │
┌─────────────────────────────────────────────────────────────┐
│                      Service Layer                           │
│  ┌──────────────────┐         ┌──────────────────────────┐  │
│  │ DeliveryService  │         │ DeliverySchedulerService │  │
│  └──────────────────┘         └──────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
                              │
┌─────────────────────────────────────────────────────────────┐
│                    Repository Layer                          │
│  ┌──────────────────┐  ┌──────────────────────────────────┐ │
│  │DeliveryRepository│  │ DeliveryStatusRepository        │ │
│  └──────────────────┘  └──────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────┘
                              │
┌─────────────────────────────────────────────────────────────┐
│                       Data Layer                             │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────────┐  │
│  │  Delivery    │  │DeliveryStatus│  │SubscriptionMeal │  │
│  └──────────────┘  └──────────────┘  └──────────────────┘  │
└─────────────────────────────────────────────────────────────┘
```

### Integration Points

1. **Subscription System**: Delivery creation triggered by active subscriptions
2. **User System**: Delivery ownership and authentication
3. **Meal System**: Association with daily meal assignments via SubscriptionMeal
4. **Scheduler**: Background job for status progression

## Components and Interfaces

### 1. DeliveryService

Primary service for delivery operations.

**Responsibilities:**
- Create daily deliveries for active subscriptions
- Retrieve delivery information for users
- Update delivery preferences (time, address)
- Confirm delivery receipt
- Provide delivery history

**Key Methods:**
```java
public DeliveryResponseDto createDailyDelivery(Integer subscriptionId, LocalDate deliveryDate)
public DeliveryResponseDto getCurrentDelivery(Integer userId)
public DeliveryResponseDto getDeliveryById(Integer deliveryId, Integer userId, boolean isAdmin)
public List<DeliveryResponseDto> getDeliveryHistory(Integer userId, LocalDate startDate, LocalDate endDate, Pageable pageable)
public DeliveryResponseDto updateDeliveryPreferences(Integer deliveryId, Integer userId, UpdateDeliveryDto dto)
public DeliveryResponseDto confirmDelivery(Integer deliveryId, Integer userId)
```

### 2. DeliverySchedulerService

Background service for automated delivery management.

**Responsibilities:**
- Create deliveries for all active subscriptions at day start
- Progress delivery status based on time
- Handle status transitions (Preparing → Shipped → Delivered)

**Key Methods:**
```java
@Scheduled(cron = "0 0 0 * * *") // Daily at midnight
public void createDailyDeliveries()

@Scheduled(fixedRate = 60000) // Every minute
public void updateDeliveryStatuses()

private void progressDeliveryStatus(Delivery delivery)
private boolean shouldTransitionToShipped(Delivery delivery)
private boolean shouldTransitionToDelivered(Delivery delivery)
```

### 3. DeliveryController

REST API endpoints for user delivery operations.

**Endpoints:**
- `GET /api/deliveries/current` - Get current day's delivery
- `GET /api/deliveries/{id}` - Get specific delivery
- `GET /api/deliveries/history` - Get delivery history with pagination
- `PATCH /api/deliveries/{id}` - Update delivery preferences
- `POST /api/deliveries/{id}/confirm` - Confirm delivery receipt

### 4. AdminDeliveryController

REST API endpoints for administrative operations.

**Endpoints:**
- `GET /api/admin/deliveries` - List all deliveries with filters
- `GET /api/admin/deliveries/{id}` - Get delivery details
- `PATCH /api/admin/deliveries/{id}/status` - Manually update delivery status
- `GET /api/admin/deliveries/stats` - Get delivery statistics

### 5. DeliveryRepository

Data access layer for delivery operations.

**Key Query Methods:**
```java
Optional<Delivery> findBySubscriptionMeal_Subscription_UserUserIdAndSubscriptionMeal_DeliveryDate(Integer userId, LocalDate date)
List<Delivery> findBySubscriptionMeal_Subscription_UserUserIdOrderBySubscriptionMeal_DeliveryDateDesc(Integer userId, Pageable pageable)
List<Delivery> findByStatus_StatusName(String statusName)
List<Delivery> findByStatus_StatusNameIn(List<String> statusNames)
Page<Delivery> findAll(Specification<Delivery> spec, Pageable pageable)
```

## Data Models

### Enhanced Delivery Model

The existing `Delivery` entity will be enhanced with additional fields:

```java
@Entity
@Table(name = "delivery")
public class Delivery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer deliveryId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_meal_id")
    private SubscriptionMeal subscriptionMeal;

    private String address;
    
    private LocalTime deliveryTime;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id")
    private DeliveryStatus status;
    
    // NEW FIELDS
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "status_updated_at")
    private LocalDateTime statusUpdatedAt;
    
    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;
    
    @Column(name = "estimated_delivery_time")
    private LocalTime estimatedDeliveryTime;
}
```

### DeliveryStatus Values

The system will use the following status values:
- `PREPARING` - Initial status when delivery is created
- `SHIPPED` - Delivery is in transit
- `DELIVERED` - Delivery has arrived at destination
- `CONFIRMED` - User has confirmed receipt

### DTOs

**DeliveryResponseDto:**
```java
public class DeliveryResponseDto {
    private Integer deliveryId;
    private LocalDate deliveryDate;
    private LocalTime deliveryTime;
    private String address;
    private String status;
    private LocalDateTime statusUpdatedAt;
    private LocalDateTime confirmedAt;
    private List<MealSummaryDto> meals;
}
```

**UpdateDeliveryDto:**
```java
public class UpdateDeliveryDto {
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime deliveryTime;
    
    @Size(max = 255)
    private String address;
}
```

**DeliveryHistoryDto:**
```java
public class DeliveryHistoryDto {
    private Integer deliveryId;
    private LocalDate deliveryDate;
    private String status;
    private LocalTime deliveryTime;
    private boolean confirmed;
    private int mealCount;
}
```

## Status Progression Logic

### Timing Configuration

The system uses time-based rules for status progression:

1. **Preparing → Shipped**: 
   - Transition occurs when current time reaches (preferredDeliveryTime - 2 hours)
   - Example: If delivery time is 18:00, transition at 16:00

2. **Shipped → Delivered**:
   - Transition occurs when current time reaches preferredDeliveryTime
   - Example: If delivery time is 18:00, transition at 18:00

3. **Delivered → Confirmed**:
   - Manual transition only, triggered by user confirmation

### Status Progression Algorithm

```java
private void progressDeliveryStatus(Delivery delivery) {
    LocalDateTime now = LocalDateTime.now();
    LocalDate today = LocalDate.now();
    LocalTime currentTime = now.toLocalTime();
    
    DeliveryStatus currentStatus = delivery.getStatus();
    LocalTime preferredTime = delivery.getDeliveryTime();
    
    if ("PREPARING".equals(currentStatus.getStatusName())) {
        // Transition to SHIPPED 2 hours before delivery time
        LocalTime shipTime = preferredTime.minusHours(2);
        if (currentTime.isAfter(shipTime) || currentTime.equals(shipTime)) {
            updateStatus(delivery, "SHIPPED");
        }
    } else if ("SHIPPED".equals(currentStatus.getStatusName())) {
        // Transition to DELIVERED at delivery time
        if (currentTime.isAfter(preferredTime) || currentTime.equals(preferredTime)) {
            updateStatus(delivery, "DELIVERED");
        }
    }
    // DELIVERED status requires manual confirmation
}
```

### Catch-up Logic

For deliveries that fall behind schedule (e.g., system downtime):

```java
private void handleLateDelivery(Delivery delivery) {
    LocalTime currentTime = LocalTime.now();
    LocalTime preferredTime = delivery.getDeliveryTime();
    
    // If current time is past delivery time and still preparing/shipped
    if (currentTime.isAfter(preferredTime)) {
        if ("PREPARING".equals(delivery.getStatus().getStatusName()) ||
            "SHIPPED".equals(delivery.getStatus().getStatusName())) {
            updateStatus(delivery, "DELIVERED");
        }
    }
}
```


## Correctness Properties

*A property is a characteristic or behavior that should hold true across all valid executions of a system-essentially, a formal statement about what the system should do. Properties serve as the bridge between human-readable specifications and machine-verifiable correctness guarantees.*

### Property 1: Preferred time validation
*For any* subscription creation request, if the preferred delivery time is not in valid HH:MM format, the system should reject the request with a validation error.
**Validates: Requirements 1.2**

### Property 2: Preferred time persistence (Round-trip)
*For any* subscription created with a preferred delivery time, retrieving that subscription should return the same preferred delivery time.
**Validates: Requirements 1.3**

### Property 3: Daily delivery creation
*For any* active subscription on any given day, the scheduler should create exactly one delivery record for that day.
**Validates: Requirements 2.2**

### Property 4: Initial status invariant
*For any* newly created delivery, the initial status should always be "Preparing".
**Validates: Requirements 2.3**

### Property 5: Delivery time inheritance
*For any* delivery created from a subscription, the delivery time should match the subscription's preferred delivery time.
**Validates: Requirements 2.4**

### Property 6: Subscription meal association
*For any* delivery, it should be associated with a valid SubscriptionMeal that belongs to the same subscription.
**Validates: Requirements 2.5**

### Property 7: Status progression from Preparing
*For any* delivery in "Preparing" status, when current time advances past (preferredDeliveryTime - 2 hours), the status should transition to "Shipped".
**Validates: Requirements 3.1**

### Property 8: Status progression from Shipped
*For any* delivery in "Shipped" status, when current time reaches or passes the preferredDeliveryTime, the status should transition to "Delivered".
**Validates: Requirements 3.2**

### Property 9: Status update timestamp invariant
*For any* delivery status change, the statusUpdatedAt field should be set to the current timestamp.
**Validates: Requirements 3.3**

### Property 10: Delivered status idempotence
*For any* delivery in "Delivered" status, running the status progression scheduler multiple times should not change the status (unless manually confirmed).
**Validates: Requirements 3.4**

### Property 11: Delivery ownership authorization
*For any* delivery and any user, the user can retrieve the delivery details if and only if the delivery belongs to that user (or user is admin).
**Validates: Requirements 4.2**

### Property 12: Response completeness
*For any* delivery returned by the API, the response should include status, deliveryTime, address, and associated meals.
**Validates: Requirements 4.3**

### Property 13: History ordering invariant
*For any* user's delivery history, the results should be ordered by delivery date in descending order.
**Validates: Requirements 4.5**

### Property 14: Confirmation state transition
*For any* delivery in "Delivered" status, when a user confirms receipt, the status should transition to "Confirmed" and confirmedAt timestamp should be set.
**Validates: Requirements 5.1, 5.4**

### Property 15: Confirmation validation
*For any* delivery not in "Delivered" status, attempting to confirm should fail with a validation error.
**Validates: Requirements 5.2**

### Property 16: Confirmation authorization
*For any* delivery, only the owner of that delivery can confirm receipt (authorization check).
**Validates: Requirements 5.3**

### Property 17: Confirmation idempotence
*For any* delivery already in "Confirmed" status, attempting to confirm again should either succeed without changes or return a benign message.
**Validates: Requirements 5.5**

### Property 18: Status sequence invariant
*For any* delivery, the status should never skip stages (e.g., cannot go from "Preparing" directly to "Delivered" without passing through "Shipped").
**Validates: Requirements 6.5**

### Property 19: Authentication requirement
*For any* delivery API endpoint, requests without valid authentication should be rejected with HTTP 401 status.
**Validates: Requirements 7.5**

### Property 20: Update allowed in Preparing status
*For any* delivery in "Preparing" status and any valid delivery time or address, the update operation should succeed.
**Validates: Requirements 8.1, 8.2**

### Property 21: Update rejected after shipment
*For any* delivery in "Shipped" or "Delivered" status, attempting to update delivery preferences should fail with a validation error.
**Validates: Requirements 8.3**

### Property 22: Update input validation
*For any* delivery update request, invalid time formats or empty addresses should be rejected with validation errors.
**Validates: Requirements 8.4**

### Property 23: Timing recalculation on update
*For any* delivery in "Preparing" status, when the preferred delivery time is updated, the status progression timing should be recalculated based on the new time.
**Validates: Requirements 8.5**

### Property 24: Admin filtering
*For any* admin query with filters (status, date, user), only deliveries matching all specified filters should be returned.
**Validates: Requirements 9.1**

### Property 25: Admin status update
*For any* delivery and any valid status, an admin should be able to manually update the status.
**Validates: Requirements 9.3**

### Property 26: Status history tracking
*For any* delivery that has undergone status changes, the status history should contain entries for each transition with timestamps.
**Validates: Requirements 9.4**

### Property 27: Admin search functionality
*For any* admin search by user email, delivery ID, or date range, only deliveries matching the search criteria should be returned.
**Validates: Requirements 9.5**

### Property 28: History response completeness
*For any* delivery in history results, the response should include delivery date, status, delivery time, address, and confirmation status.
**Validates: Requirements 10.2**

### Property 29: History filtering
*For any* delivery history query with date range or status filters, only deliveries matching the filters should be returned.
**Validates: Requirements 10.3**

### Property 30: Historical meal association
*For any* historical delivery, the associated meals should be retrievable and match the meals that were assigned on that delivery date.
**Validates: Requirements 10.4**

### Property 31: Pagination correctness
*For any* paginated delivery history request, the page size, total count, and page numbers should be mathematically consistent.
**Validates: Requirements 10.5**

### Property 32: Cancelled subscription no delivery
*For any* subscription in "cancelled" status, the daily delivery creation scheduler should not create new deliveries for future dates.
**Validates: Requirements 11.1**

### Property 33: Paused subscription no delivery
*For any* subscription in "paused" status, the daily delivery creation scheduler should not create deliveries during the pause period.
**Validates: Requirements 11.2**

### Property 34: Catch-up status progression
*For any* delivery where current time is past the preferred delivery time and status is still "Preparing" or "Shipped", the status should immediately progress to "Delivered".
**Validates: Requirements 11.3**

## Error Handling

### Validation Errors

**Invalid Time Format:**
- HTTP 400 Bad Request
- Message: "Invalid time format. Expected HH:MM"

**Invalid Status Transition:**
- HTTP 400 Bad Request
- Message: "Cannot confirm delivery. Delivery must be in 'Delivered' status"

**Update Not Allowed:**
- HTTP 400 Bad Request
- Message: "Cannot update delivery preferences. Delivery has already shipped"

### Authorization Errors

**Unauthorized Access:**
- HTTP 401 Unauthorized
- Message: "Authentication required"

**Forbidden Access:**
- HTTP 403 Forbidden
- Message: "You do not have permission to access this delivery"

### Not Found Errors

**Delivery Not Found:**
- HTTP 404 Not Found
- Message: "Delivery not found"

**No Current Delivery:**
- HTTP 404 Not Found
- Message: "No active delivery found for today"

### Business Logic Errors

**Duplicate Confirmation:**
- HTTP 200 OK (idempotent)
- Message: "Delivery already confirmed"

**Missing Address:**
- HTTP 400 Bad Request
- Message: "Delivery address is required"

## Testing Strategy

### Unit Testing

Unit tests will verify specific examples and integration points:

1. **Service Layer Tests:**
   - Test delivery creation with specific subscription data
   - Test status progression with mocked time
   - Test confirmation with various delivery states
   - Test update validation logic

2. **Controller Layer Tests:**
   - Test endpoint routing and HTTP status codes
   - Test authentication and authorization
   - Test request/response DTO mapping
   - Test error response formatting

3. **Repository Layer Tests:**
   - Test custom query methods
   - Test filtering and pagination
   - Test relationship loading

### Property-Based Testing

Property-based tests will verify universal properties using **jqwik** (Java property-based testing library):

**Configuration:**
- Minimum 100 iterations per property test
- Each test tagged with: `@Property` annotation
- Each test commented with: `// Feature: delivery-tracking, Property X: [property text]`

**Test Categories:**

1. **Validation Properties (Properties 1, 15, 21, 22):**
   - Generate random valid and invalid inputs
   - Verify validation logic across input space

2. **State Transition Properties (Properties 7, 8, 14, 18, 34):**
   - Generate deliveries in various states
   - Verify correct state transitions

3. **Invariant Properties (Properties 4, 5, 6, 9, 13):**
   - Generate random deliveries
   - Verify invariants hold across all instances

4. **Authorization Properties (Properties 11, 16, 19):**
   - Generate random user/delivery combinations
   - Verify authorization rules

5. **Round-trip Properties (Property 2):**
   - Generate random subscription data
   - Verify data persistence and retrieval

6. **Idempotence Properties (Properties 10, 17):**
   - Generate random deliveries
   - Verify operations can be repeated safely

**Example Property Test Structure:**
```java
@Property
// Feature: delivery-tracking, Property 4: Initial status invariant
void newDeliveriesAlwaysHavePreparingStatus(@ForAll("validSubscriptions") Subscription subscription) {
    Delivery delivery = deliveryService.createDailyDelivery(
        subscription.getSubscriptionId(), 
        LocalDate.now()
    );
    
    assertThat(delivery.getStatus().getStatusName()).isEqualTo("PREPARING");
}
```

### Integration Testing

Integration tests will verify end-to-end workflows:

1. **Delivery Lifecycle:**
   - Create subscription → Create delivery → Progress status → Confirm delivery

2. **Scheduler Integration:**
   - Verify daily delivery creation job
   - Verify status progression job

3. **API Integration:**
   - Test complete API workflows with authentication
   - Test admin operations

### Test Data Generators

For property-based testing, custom generators will be created:

```java
@Provide
Arbitrary<Subscription> validSubscriptions() {
    return Combinators.combine(
        Arbitraries.integers().between(1, 1000),
        Arbitraries.of("active", "paused", "cancelled"),
        Arbitraries.localTimes()
    ).as((id, status, time) -> createSubscription(id, status, time));
}

@Provide
Arbitrary<LocalTime> validDeliveryTimes() {
    return Arbitraries.localTimes()
        .between(LocalTime.of(8, 0), LocalTime.of(22, 0));
}

@Provide
Arbitrary<Delivery> deliveriesInVariousStates() {
    return Arbitraries.of("PREPARING", "SHIPPED", "DELIVERED", "CONFIRMED")
        .map(status -> createDeliveryWithStatus(status));
}
```

## Implementation Notes

### Database Migrations

New fields need to be added to the `delivery` table:

```sql
ALTER TABLE delivery 
ADD COLUMN created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
ADD COLUMN status_updated_at TIMESTAMP,
ADD COLUMN confirmed_at TIMESTAMP,
ADD COLUMN estimated_delivery_time TIME;
```

Ensure delivery_status table has the required status values:

```sql
INSERT INTO delivery_status (status_name) VALUES 
('PREPARING'),
('SHIPPED'),
('DELIVERED'),
('CONFIRMED')
ON CONFLICT (status_name) DO NOTHING;
```

### Scheduler Configuration

Spring Scheduler will be used for automated tasks:

```java
@Configuration
@EnableScheduling
public class SchedulerConfig {
    // Configuration for scheduler thread pool
}
```

### Performance Considerations

1. **Batch Status Updates:** Update all eligible deliveries in a single transaction
2. **Indexing:** Add indexes on `delivery.status_id` and `subscription_meal.delivery_date`
3. **Lazy Loading:** Use lazy loading for relationships to avoid N+1 queries
4. **Caching:** Consider caching delivery status lookups

### Security Considerations

1. **Authentication:** All endpoints require valid JWT token
2. **Authorization:** Users can only access their own deliveries (except admins)
3. **Input Validation:** Validate all user inputs to prevent injection attacks
4. **Rate Limiting:** Consider rate limiting for API endpoints

## Future Enhancements

1. **Real-time Notifications:** Push notifications for status changes
2. **Delivery Tracking Map:** GPS tracking integration
3. **Delivery Instructions:** Allow users to add special delivery instructions
4. **Delivery Rating:** Allow users to rate delivery experience
5. **Multiple Deliveries Per Day:** Support for multiple delivery windows
6. **Delivery Driver Assignment:** Track which driver handles each delivery
