# OOP Concepts and Java Implementation Documentation
## Meal Planner API Project

---

## Table of Contents
1. [OOP Concepts Overview](#oop-concepts-overview)
2. [Core OOP Principles](#core-oop-principles)
3. [Java-Specific Implementations](#java-specific-implementations)
4. [Design Patterns](#design-patterns)
5. [Architecture Patterns](#architecture-patterns)
6. [Advanced OOP Features](#advanced-oop-features)

---

## OOP Concepts Overview

This project demonstrates comprehensive use of Object-Oriented Programming principles in a production-grade Spring Boot application for meal planning and delivery tracking.

---

## Core OOP Principles

### 1. **Encapsulation**

**Definition**: Bundling data (fields) and methods that operate on that data within a single unit (class), while restricting direct access to internal state.

**Implementation Examples**:

#### Model Classes (Entities)
```java
// User.java - Encapsulation with private fields and public getters/setters
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userId;  // Private field - cannot be accessed directly
    
    private String fullName;
    private String email;
    private String passwordHash;  // Sensitive data encapsulated
    
    // Public accessor methods provide controlled access
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
```

#### Service Layer Encapsulation
```java
// DeliveryService.java - Business logic encapsulated in service
@Service
@Transactional
public class DeliveryService {
    // Private dependencies - implementation details hidden
    @Autowired
    private DeliveryRepository deliveryRepository;
    
    @Autowired
    private DeliveryStatusRepository deliveryStatusRepository;
    
    // Public interface for business operations
    public DeliveryResponseDto getCurrentDelivery(Integer userId) {
        // Internal implementation hidden from clients
    }
    
    // Private helper methods - internal implementation details
    private DeliveryResponseDto mapToResponseDto(Delivery delivery, List<SubscriptionMeal> meals) {
        // Mapping logic encapsulated
    }
}
```

**Benefits Demonstrated**:
- Data protection (password hashing, sensitive user information)
- Controlled access through getters/setters
- Internal implementation details hidden from external classes
- Easier maintenance and refactoring

---

### 2. **Inheritance**

**Definition**: Mechanism where a class (subclass) inherits properties and behaviors from another class (superclass).

**Implementation Examples**:

#### Exception Hierarchy
```java
// Custom exception classes inherit from RuntimeException
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);  // Calling parent constructor
    }
}

public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}

public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String message) {
        super(message);
    }
}
```

#### Repository Inheritance
```java
// DeliveryRepository.java - Inherits from JpaRepository
@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Integer>, 
                                            JpaSpecificationExecutor<Delivery> {
    // Inherits all CRUD methods from JpaRepository:
    // - save(), findById(), findAll(), delete(), etc.
    
    // Custom query methods
    Optional<Delivery> findBySubscriptionMeal_SubscriptionMealId(Integer id);
}
```

#### Filter Inheritance
```java
// JwtAuthenticationFilter.java - Extends OncePerRequestFilter
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    // Inherits filter lifecycle management
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                   HttpServletResponse response, 
                                   FilterChain filterChain) {
        // Custom authentication logic
    }
}
```

**Benefits Demonstrated**:
- Code reuse (inheriting CRUD operations from JpaRepository)
- Consistent exception handling hierarchy
- Framework integration (Spring Security filters)
- Polymorphic behavior

---

### 3. **Polymorphism**

**Definition**: Ability of objects to take multiple forms, allowing different implementations of the same interface or method.

**Implementation Examples**:

#### Method Overloading (Compile-time Polymorphism)
```java
// User.java - Multiple constructors
public class User {
    // No-argument constructor for JPA
    public User() {}
    
    // Constructor for registration
    public User(String fullName, String email, String passwordHash, Role role) {
        this.fullName = fullName;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
    }
}
```

#### Interface Implementation (Runtime Polymorphism)
```java
// Repository interfaces can be used polymorphically
public interface DeliveryRepository extends JpaRepository<Delivery, Integer> {
    // Spring Data JPA provides implementation at runtime
}

// Service layer uses interface reference
@Service
public class DeliveryService {
    @Autowired
    private DeliveryRepository deliveryRepository;  // Interface reference
    
    // Actual implementation injected by Spring at runtime
}
```

#### Exception Handling Polymorphism
```java
// GlobalExceptionHandler.java - Handles different exception types polymorphically
@ControllerAdvice
public class GlobalExceptionHandler {
    // Handles specific exception type
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleResourceNotFound(
            ResourceNotFoundException ex, HttpServletRequest request) {
        // Specific handling
    }
    
    // Handles all exceptions polymorphically
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGeneral(
            Exception ex, HttpServletRequest request) {
        // Generic handling for any exception
    }
}
```

**Benefits Demonstrated**:
- Flexible code that works with different types
- Runtime behavior selection
- Framework integration (Spring dependency injection)
- Consistent error handling

---

### 4. **Abstraction**

**Definition**: Hiding complex implementation details and showing only essential features of an object.

**Implementation Examples**:

#### Repository Abstraction
```java
// Repository interface abstracts database operations
@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Integer> {
    // Abstract query - implementation generated by Spring Data JPA
    Optional<Delivery> findBySubscriptionMeal_Subscription_User_UserIdAndSubscriptionMeal_DeliveryDate(
        Integer userId, LocalDate deliveryDate);
    
    // Complex SQL query abstracted behind simple method name
    List<Delivery> findByStatus_StatusName(String statusName);
}
```

#### Service Layer Abstraction
```java
// DeliveryService.java - Abstracts business logic complexity
@Service
public class DeliveryService {
    // High-level operation - internal complexity hidden
    public DeliveryResponseDto getCurrentDelivery(Integer userId) {
        LocalDate today = LocalDate.now();
        
        // Complex query abstracted
        Delivery delivery = deliveryRepository
                .findBySubscriptionMeal_Subscription_User_UserIdAndSubscriptionMeal_DeliveryDate(
                    userId, today)
                .orElseThrow(() -> new ResourceNotFoundException("No active delivery found"));
        
        // Complex mapping abstracted
        return mapToResponseDto(delivery, subscriptionMeals);
    }
}
```

#### DTO Abstraction
```java
// DeliveryResponseDto.java - Abstracts internal entity structure
public class DeliveryResponseDto {
    private Integer deliveryId;
    private LocalDate deliveryDate;
    private String status;
    private List<MealSummaryDto> meals;
    
    // Client doesn't need to know about internal entity relationships
    // or database structure
}
```

**Benefits Demonstrated**:
- Simplified client code
- Hidden implementation complexity
- Separation of concerns
- Easier testing and maintenance

---

## Java-Specific Implementations

### 1. **Annotations**

Annotations provide metadata about the program and are extensively used in this project.

#### JPA/Hibernate Annotations
```java
@Entity  // Marks class as JPA entity
@Table(name = "delivery")  // Maps to database table
public class Delivery {
    @Id  // Primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // Auto-increment
    private Integer deliveryId;
    
    @OneToOne(fetch = FetchType.LAZY)  // Relationship mapping
    @JoinColumn(name = "subscription_meal_id")
    private SubscriptionMeal subscriptionMeal;
    
    @ManyToOne(fetch = FetchType.LAZY)  // Many-to-one relationship
    @JoinColumn(name = "status_id")
    private DeliveryStatus status;
    
    @Column(name = "created_at")  // Column mapping
    private LocalDateTime createdAt;
}
```

#### Spring Framework Annotations
```java
@Service  // Marks as service component
@Transactional  // Enables transaction management
public class DeliveryService {
    @Autowired  // Dependency injection
    private DeliveryRepository deliveryRepository;
}

@RestController  // Marks as REST controller
@RequestMapping("/api/deliveries")  // Base URL mapping
public class DeliveryController {
    @GetMapping("/current")  // HTTP GET mapping
    public ResponseEntity<DeliveryResponseDto> getCurrentDelivery() {
        // Handler method
    }
    
    @PostMapping("/{id}/confirm")  // HTTP POST with path variable
    public ResponseEntity<DeliveryResponseDto> confirmDelivery(@PathVariable Integer id) {
        // Handler method
    }
}
```

#### Security Annotations
```java
@PreAuthorize("hasRole('ADMIN')")  // Method-level security
public class AdminDeliveryController {
    // Only accessible to ADMIN role
}
```

#### Validation Annotations
```java
public class UpdateDeliveryDto {
    @JsonFormat(pattern = "HH:mm")  // JSON serialization format
    private LocalTime deliveryTime;
    
    @Valid  // Triggers validation
    private String address;
}
```

---

### 2. **Generics**

Generics provide type safety and code reusability.

```java
// Repository with generic types
public interface JpaRepository<T, ID> {
    Optional<T> findById(ID id);
    List<T> findAll();
    <S extends T> S save(S entity);
}

// Usage with specific types
public interface DeliveryRepository extends JpaRepository<Delivery, Integer> {
    // T = Delivery, ID = Integer
}

// Generic response wrapper
public class Page<T> {
    private List<T> content;
    private int totalPages;
    private long totalElements;
}

// Usage
Page<DeliveryHistoryDto> history = deliveryService.getDeliveryHistory(...);
```

---

### 3. **Collections Framework**

Extensive use of Java Collections for data management.

```java
// Lists
List<SubscriptionMeal> subscriptionMeals = subscriptionMealRepository
        .findBySubscription_SubscriptionIdAndDeliveryDate(subscriptionId, deliveryDate);

// Sets (for unique collections)
@OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
private Set<UserAllergy> userAllergies;

// Stream API for functional operations
List<MealSummaryDto> meals = subscriptionMeals.stream()
        .map(sm -> new MealSummaryDto(
                sm.getMeal().getMealId(),
                sm.getMeal().getMealName(),
                null
        ))
        .collect(Collectors.toList());

// Filtering with streams
List<Delivery> filteredDeliveries = allDeliveries.stream()
        .filter(delivery -> {
            LocalDate deliveryDate = delivery.getSubscriptionMeal().getDeliveryDate();
            return startDate == null || !deliveryDate.isBefore(startDate);
        })
        .collect(Collectors.toList());
```

---

### 4. **Lambda Expressions and Functional Programming**

```java
// Lambda for filtering
.filter(delivery -> delivery.getStatus().getStatusName().equals("DELIVERED"))

// Lambda for mapping
.map(sm -> new MealSummaryDto(sm.getMeal().getMealId(), sm.getMeal().getMealName(), null))

// Lambda for exception handling
.orElseThrow(() -> new ResourceNotFoundException("Delivery not found"));

// Method reference
.map(this::mapToHistoryDto)
```

---

### 5. **Optional Class**

Prevents NullPointerException and provides functional null handling.

```java
// Repository returns Optional
Optional<Delivery> findById(Integer id);

// Usage with orElseThrow
Delivery delivery = deliveryRepository.findById(deliveryId)
        .orElseThrow(() -> new ResourceNotFoundException("Delivery not found"));

// Usage with orElse
String address = Optional.ofNullable(subscription.getUser().getAddress())
        .orElse("Default Address");
```

---

### 6. **Date and Time API (java.time)**

Modern date/time handling introduced in Java 8.

```java
// LocalDate for dates without time
private LocalDate deliveryDate;
LocalDate today = LocalDate.now();

// LocalTime for time without date
private LocalTime deliveryTime;
LocalTime preferredTime = LocalTime.of(14, 30);

// LocalDateTime for date and time
private LocalDateTime createdAt;
LocalDateTime now = LocalDateTime.now();

// Comparisons
if (deliveryDate.isBefore(startDate)) { }
if (deliveryDate.isAfter(endDate)) { }
if (deliveryDate.equals(today)) { }
```

---

## Design Patterns

### 1. **Repository Pattern**

Abstracts data access logic and provides a collection-like interface.

```java
@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Integer> {
    // CRUD operations abstracted
    Optional<Delivery> findById(Integer id);
    Delivery save(Delivery delivery);
    void delete(Delivery delivery);
    
    // Custom queries
    List<Delivery> findByStatus_StatusName(String statusName);
}
```

**Benefits**:
- Decouples business logic from data access
- Easier testing with mock repositories
- Centralized data access logic

---

### 2. **Data Transfer Object (DTO) Pattern**

Separates internal entity structure from external API representation.

```java
// Entity (internal)
@Entity
public class Delivery {
    private Integer deliveryId;
    private SubscriptionMeal subscriptionMeal;  // Complex relationship
    private DeliveryStatus status;  // Another entity
}

// DTO (external)
public class DeliveryResponseDto {
    private Integer deliveryId;
    private String status;  // Simplified to string
    private List<MealSummaryDto> meals;  // Flattened structure
}

// Mapping in service
private DeliveryResponseDto mapToResponseDto(Delivery delivery, List<SubscriptionMeal> meals) {
    return new DeliveryResponseDto(
            delivery.getDeliveryId(),
            delivery.getSubscriptionMeal().getDeliveryDate(),
            delivery.getStatus().getStatusName(),
            // ... mapping logic
    );
}
```

**Benefits**:
- API stability (internal changes don't affect clients)
- Security (sensitive fields not exposed)
- Performance (only necessary data transferred)

---

### 3. **Dependency Injection Pattern**

Spring Framework's core pattern for managing dependencies.

```java
@Service
public class DeliveryService {
    // Dependencies injected by Spring
    @Autowired
    private DeliveryRepository deliveryRepository;
    
    @Autowired
    private DeliveryStatusRepository deliveryStatusRepository;
    
    @Autowired
    private SubscriptionRepository subscriptionRepository;
    
    // Constructor injection (preferred)
    public DeliveryService(DeliveryRepository deliveryRepository,
                          DeliveryStatusRepository deliveryStatusRepository) {
        this.deliveryRepository = deliveryRepository;
        this.deliveryStatusRepository = deliveryStatusRepository;
    }
}
```

**Benefits**:
- Loose coupling
- Easier testing (mock dependencies)
- Centralized configuration

---

### 4. **Builder Pattern (Implicit)**

Used in entity construction and DTO creation.

```java
// Entity construction
Delivery delivery = new Delivery();
delivery.setSubscriptionMeal(subscriptionMeal);
delivery.setAddress(address);
delivery.setDeliveryTime(subscription.getPreferredTime());
delivery.setStatus(preparingStatus);
delivery.setCreatedAt(LocalDateTime.now());

// DTO construction
return new DeliveryResponseDto(
        delivery.getDeliveryId(),
        delivery.getSubscriptionMeal().getDeliveryDate(),
        delivery.getDeliveryTime(),
        delivery.getAddress(),
        delivery.getStatus().getStatusName(),
        delivery.getStatusUpdatedAt(),
        delivery.getConfirmedAt(),
        meals
);
```

---

### 5. **Strategy Pattern**

Different exception handling strategies based on exception type.

```java
@ControllerAdvice
public class GlobalExceptionHandler {
    // Strategy for ResourceNotFoundException
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleResourceNotFound(...) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
    
    // Strategy for ValidationException
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponseDto> handleValidation(...) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
    
    // Default strategy
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGeneral(...) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
```

---

### 6. **Filter/Chain of Responsibility Pattern**

JWT authentication filter chain.

```java
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                   HttpServletResponse response, 
                                   FilterChain filterChain) {
        // Extract and validate JWT
        String jwt = extractJwt(request);
        
        if (isValid(jwt)) {
            // Set authentication
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }
        
        // Pass to next filter in chain
        filterChain.doFilter(request, response);
    }
}
```

---

## Architecture Patterns

### 1. **Layered Architecture**

Clear separation of concerns across layers.

```
┌─────────────────────────────────────┐
│     Controller Layer (API)          │  ← REST endpoints, request/response handling
├─────────────────────────────────────┤
│     Service Layer (Business Logic)  │  ← Business rules, transaction management
├─────────────────────────────────────┤
│     Repository Layer (Data Access)  │  ← Database operations, queries
├─────────────────────────────────────┤
│     Model Layer (Entities)          │  ← Domain objects, JPA entities
└─────────────────────────────────────┘
```

**Example Flow**:
```
DeliveryController → DeliveryService → DeliveryRepository → Database
      ↓                    ↓                  ↓
  HTTP Request      Business Logic      SQL Queries
  Validation        Transactions        Entity Mapping
  DTO Mapping       Authorization       CRUD Operations
```

---

### 2. **MVC (Model-View-Controller) Pattern**

Spring MVC implementation for REST API.

```java
// Model
@Entity
public class Delivery { }

// Controller
@RestController
@RequestMapping("/api/deliveries")
public class DeliveryController {
    @GetMapping("/current")
    public ResponseEntity<DeliveryResponseDto> getCurrentDelivery() {
        // Handles HTTP request
    }
}

// View (JSON response via DTOs)
public class DeliveryResponseDto { }
```

---

### 3. **Service Layer Pattern**

Business logic encapsulated in service classes.

```java
@Service
@Transactional
public class DeliveryService {
    // Business operations
    public DeliveryResponseDto createDailyDelivery(Integer subscriptionId, LocalDate date) {
        // Validation
        // Business rules
        // Data persistence
        // Response mapping
    }
    
    public DeliveryResponseDto confirmDelivery(Integer deliveryId, Integer userId) {
        // Authorization
        // State validation
        // State transition
        // Audit logging
    }
}
```

---

## Advanced OOP Features

### 1. **Association, Aggregation, and Composition**

#### Association (Weak Relationship)
```java
// User has a Role (association)
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "role_id")
private Role role;
// User and Role can exist independently
```

#### Aggregation (Has-A Relationship)
```java
// User has UserAllergies (aggregation)
@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
private Set<UserAllergy> userAllergies;
// UserAllergy records depend on User but represent independent concepts
```

#### Composition (Strong Ownership)
```java
// Delivery has timestamps (composition)
@Column(name = "created_at")
private LocalDateTime createdAt;
// Timestamps are part of Delivery's lifecycle
```

---

### 2. **Cascade Operations**

Automatic propagation of operations to related entities.

```java
@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
private Set<History> historyRecords;
// When User is deleted, all History records are automatically deleted
```

---

### 3. **Lazy vs Eager Loading**

Performance optimization for entity relationships.

```java
// Lazy loading (default for @OneToMany, @ManyToOne)
@ManyToOne(fetch = FetchType.LAZY)
private DeliveryStatus status;
// Status loaded only when accessed

// Eager loading
@ManyToOne(fetch = FetchType.EAGER)
private User user;
// User loaded immediately with parent entity
```

---

### 4. **Transaction Management**

ACID properties ensured through declarative transactions.

```java
@Service
@Transactional  // All methods run in transactions
public class DeliveryService {
    public DeliveryResponseDto confirmDelivery(Integer deliveryId, Integer userId) {
        // Multiple database operations in single transaction
        delivery.setStatus(confirmedStatus);
        delivery.setConfirmedAt(LocalDateTime.now());
        delivery = deliveryRepository.save(delivery);
        
        historyRepository.save(historyRecord);
        // All succeed or all rollback
    }
}
```

---

### 5. **Method-Level Security**

Authorization at method level using annotations.

```java
@PreAuthorize("hasRole('ADMIN')")
public Page<SubscriptionResponseDto> getAllSubscriptions(...) {
    // Only ADMIN role can execute
}
```

---

### 6. **Exception Handling Hierarchy**

Custom exception classes for different error scenarios.

```
RuntimeException
    ├── ResourceNotFoundException (404)
    ├── ValidationException (400)
    ├── UnauthorizedException (401)
    ├── ForbiddenException (403)
    ├── BusinessRuleException (422)
    └── DeliveryException (400)
```

---

## Summary of OOP Concepts Used

### Core Principles
✅ **Encapsulation**: Private fields, public methods, service layer abstraction  
✅ **Inheritance**: Exception hierarchy, repository interfaces, filter classes  
✅ **Polymorphism**: Method overloading, interface implementation, exception handling  
✅ **Abstraction**: Repository interfaces, service layer, DTO pattern  

### Java Features
✅ Annotations (JPA, Spring, Security, Validation)  
✅ Generics (Repository<T, ID>, Page<T>)  
✅ Collections (List, Set, Stream API)  
✅ Lambda Expressions  
✅ Optional Class  
✅ Date/Time API  

### Design Patterns
✅ Repository Pattern  
✅ DTO Pattern  
✅ Dependency Injection  
✅ Builder Pattern  
✅ Strategy Pattern  
✅ Filter/Chain of Responsibility  

### Architecture
✅ Layered Architecture  
✅ MVC Pattern  
✅ Service Layer Pattern  

### Advanced Features
✅ Association/Aggregation/Composition  
✅ Cascade Operations  
✅ Lazy/Eager Loading  
✅ Transaction Management  
✅ Method-Level Security  
✅ Exception Hierarchy  

---

## Conclusion

This meal planner project demonstrates professional-grade Java development with comprehensive OOP principles, modern Java features, industry-standard design patterns, and clean architecture. The codebase showcases best practices for building scalable, maintainable, and secure enterprise applications.

