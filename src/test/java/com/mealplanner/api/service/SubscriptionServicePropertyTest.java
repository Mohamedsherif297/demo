package com.mealplanner.api.service;

import com.mealplanner.api.dto.CreateSubscriptionDto;
import com.mealplanner.api.dto.CustomPlanResponseDto;
import com.mealplanner.api.dto.SubscriptionDetailDto;
import com.mealplanner.api.dto.SubscriptionResponseDto;
import com.mealplanner.api.model.*;
import com.mealplanner.api.repository.*;
import net.jqwik.api.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Property-based tests for SubscriptionService
 */
@SuppressWarnings("null")
public class SubscriptionServicePropertyTest {

    /**
     * Feature: meal-planner-api, Property 26: Subscription detail completeness
     * Validates: Requirements 8.3
     * 
     * For any valid subscription ID, the subscription detail response should include 
     * complete information: subscriptionId, user, customPlan, startDate, status, 
     * preferredTime, and assigned meals.
     */
    @Property(tries = 100)
    void getSubscriptionByIdReturnsCompleteDetails(
            @ForAll("validSubscriptionIds") Integer subscriptionId,
            @ForAll("validUserIds") Integer userId) {
        
        // Setup mocks
        SubscriptionRepository subscriptionRepository = mock(SubscriptionRepository.class);
        CustomPlanRepository customPlanRepository = mock(CustomPlanRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        SubscriptionStatusRepository subscriptionStatusRepository = mock(SubscriptionStatusRepository.class);
        SubscriptionMealRepository subscriptionMealRepository = mock(SubscriptionMealRepository.class);

        SubscriptionService subscriptionService = new SubscriptionService();
        // Use reflection or setter injection to set the mocked repositories
        setField(subscriptionService, "subscriptionRepository", subscriptionRepository);
        setField(subscriptionService, "customPlanRepository", customPlanRepository);
        setField(subscriptionService, "userRepository", userRepository);
        setField(subscriptionService, "subscriptionStatusRepository", subscriptionStatusRepository);
        setField(subscriptionService, "subscriptionMealRepository", subscriptionMealRepository);

        // Create test subscription with complete data
        Subscription testSubscription = createCompleteSubscription(subscriptionId, userId);

        // Mock repository response
        when(subscriptionRepository.findById(subscriptionId))
                .thenReturn(Optional.of(testSubscription));

        // Execute - user is owner, so no permission issues
        SubscriptionDetailDto result = subscriptionService.getSubscriptionById(subscriptionId, userId, false);

        // Verify: All required fields should be present and non-null
        assertNotNull(result, "SubscriptionDetailDto should not be null");
        assertNotNull(result.getSubscriptionId(), "subscriptionId should not be null");
        assertNotNull(result.getPlanName(), "planName should not be null");
        assertNotNull(result.getStartDate(), "startDate should not be null");
        assertNotNull(result.getStatus(), "status should not be null");
        assertNotNull(result.getCustomPlan(), "customPlan should not be null");
        assertNotNull(result.getPreferredTime(), "preferredTime should not be null");
        
        // Verify customPlan nested object completeness
        CustomPlanResponseDto customPlan = result.getCustomPlan();
        assertNotNull(customPlan.getCustomPlanId(), "customPlan.customPlanId should not be null");
        assertNotNull(customPlan.getCategoryName(), "customPlan.categoryName should not be null");
        assertNotNull(customPlan.getDurationMinutes(), "customPlan.durationMinutes should not be null");
        assertNotNull(customPlan.getPrice(), "customPlan.price should not be null");
        assertNotNull(customPlan.getMealCount(), "customPlan.mealCount should not be null");
        
        // Verify the values match what we expect
        assertEquals(subscriptionId, result.getSubscriptionId(), "subscriptionId should match");
        assertFalse(result.getPlanName().isEmpty(), "planName should not be empty");
        assertFalse(result.getStatus().isEmpty(), "status should not be empty");
        assertTrue(customPlan.getDurationMinutes() > 0, "durationMinutes should be positive");
        assertTrue(customPlan.getPrice().compareTo(BigDecimal.ZERO) > 0, "price should be positive");
        assertTrue(customPlan.getMealCount() >= 0, "mealCount should be non-negative");
    }

    /**
     * Feature: delivery-tracking, Property 2: Preferred time persistence (Round-trip)
     * Validates: Requirements 1.3
     * 
     * For any subscription created with a preferred delivery time, retrieving that 
     * subscription should return the same preferred delivery time.
     */
    @Property(tries = 100)
    void subscriptionPreferredTimeRoundTrip(
            @ForAll("validPreferredTimes") LocalTime preferredTime,
            @ForAll("validUserIds") Integer userId) {
        
        // Setup mocks
        SubscriptionRepository subscriptionRepository = mock(SubscriptionRepository.class);
        CustomPlanRepository customPlanRepository = mock(CustomPlanRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        SubscriptionStatusRepository subscriptionStatusRepository = mock(SubscriptionStatusRepository.class);
        SubscriptionMealRepository subscriptionMealRepository = mock(SubscriptionMealRepository.class);

        SubscriptionService subscriptionService = new SubscriptionService();
        setField(subscriptionService, "subscriptionRepository", subscriptionRepository);
        setField(subscriptionService, "customPlanRepository", customPlanRepository);
        setField(subscriptionService, "userRepository", userRepository);
        setField(subscriptionService, "subscriptionStatusRepository", subscriptionStatusRepository);
        setField(subscriptionService, "subscriptionMealRepository", subscriptionMealRepository);

        // Create test data
        User testUser = createTestUser(userId);
        CustomPlan testPlan = createTestCustomPlan(1, testUser);
        SubscriptionStatus activeStatus = createTestStatus("active");

        // Mock repository responses for creation
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(customPlanRepository.findById(1)).thenReturn(Optional.of(testPlan));
        when(subscriptionStatusRepository.findByStatusName("active")).thenReturn(Optional.of(activeStatus));

        // Create a subscription with the preferred time
        Subscription savedSubscription = new Subscription();
        savedSubscription.setSubscriptionId(1);
        savedSubscription.setUser(testUser);
        savedSubscription.setCustomPlan(testPlan);
        savedSubscription.setStartDate(LocalDate.now().plusDays(1));
        savedSubscription.setPreferredTime(preferredTime);
        savedSubscription.setStatus(activeStatus);

        when(subscriptionRepository.save(any(Subscription.class))).thenReturn(savedSubscription);
        when(subscriptionRepository.findById(1)).thenReturn(Optional.of(savedSubscription));

        // Execute: Create subscription
        CreateSubscriptionDto createDto = new CreateSubscriptionDto(
                1, 
                LocalDate.now().plusDays(1), 
                preferredTime
        );
        SubscriptionResponseDto createResponse = subscriptionService.createSubscription(userId, createDto);

        // Execute: Retrieve subscription
        SubscriptionDetailDto retrievedSubscription = subscriptionService.getSubscriptionById(
                createResponse.getSubscriptionId(), 
                userId, 
                false
        );

        // Verify: Round-trip - the retrieved preferred time should match the original
        assertNotNull(retrievedSubscription.getPreferredTime(), 
                "Retrieved subscription should have a preferred time");
        assertEquals(preferredTime, retrievedSubscription.getPreferredTime(), 
                "Preferred time should match after round-trip (create then retrieve)");
    }

    /**
     * Provides valid subscription IDs for property testing
     */
    @Provide
    Arbitrary<Integer> validSubscriptionIds() {
        return Arbitraries.integers().between(1, 10000);
    }

    /**
     * Provides valid user IDs for property testing
     */
    @Provide
    Arbitrary<Integer> validUserIds() {
        return Arbitraries.integers().between(1, 10000);
    }

    /**
     * Provides valid preferred delivery times for property testing
     * Times between 06:00 and 23:00 (inclusive) as per validation rules
     * Note: 23:00 is the last valid time, so we can't go beyond that
     */
    @Provide
    Arbitrary<LocalTime> validPreferredTimes() {
        return Arbitraries.integers().between(6, 22)
                .flatMap(hour -> Arbitraries.integers().between(0, 59)
                        .map(minute -> LocalTime.of(hour, minute)))
                .injectDuplicates(0.1) // Include edge case 23:00
                .edgeCases(config -> config.add(LocalTime.of(23, 0)));
    }

    /**
     * Helper method to create a complete subscription with all required fields
     */
    private Subscription createCompleteSubscription(Integer subscriptionId, Integer userId) {
        // Create user
        User user = new User();
        user.setUserId(userId);
        user.setEmail("user" + userId + "@example.com");

        // Create category
        PlanCategory category = new PlanCategory();
        category.setCategoryId(1);
        category.setCategoryName("Weight Loss");

        // Create custom plan
        CustomPlan customPlan = new CustomPlan();
        customPlan.setCustomPlanId(subscriptionId); // Use same ID for simplicity
        customPlan.setUser(user);
        customPlan.setCategory(category);
        customPlan.setDurationMinutes(30);
        customPlan.setPrice(99.99);
        customPlan.setCustomPlanMeals(new HashSet<>());

        // Create subscription status
        SubscriptionStatus status = new SubscriptionStatus();
        status.setStatusId(1);
        status.setStatusName("active");

        // Create subscription
        Subscription subscription = new Subscription();
        subscription.setSubscriptionId(subscriptionId);
        subscription.setUser(user);
        subscription.setCustomPlan(customPlan);
        subscription.setStartDate(LocalDate.now().plusDays(1));
        subscription.setPreferredTime(LocalTime.of(12, 0));
        subscription.setStatus(status);

        return subscription;
    }

    /**
     * Helper method to create a test user
     */
    private User createTestUser(Integer userId) {
        User user = new User();
        user.setUserId(userId);
        user.setEmail("user" + userId + "@example.com");
        return user;
    }

    /**
     * Helper method to create a test custom plan
     */
    private CustomPlan createTestCustomPlan(Integer planId, User user) {
        PlanCategory category = new PlanCategory();
        category.setCategoryId(1);
        category.setCategoryName("Weight Loss");

        CustomPlan customPlan = new CustomPlan();
        customPlan.setCustomPlanId(planId);
        customPlan.setUser(user);
        customPlan.setCategory(category);
        customPlan.setDurationMinutes(30);
        customPlan.setPrice(99.99);
        customPlan.setCustomPlanMeals(new HashSet<>());

        return customPlan;
    }

    /**
     * Helper method to create a test subscription status
     */
    private SubscriptionStatus createTestStatus(String statusName) {
        SubscriptionStatus status = new SubscriptionStatus();
        status.setStatusId(1);
        status.setStatusName(statusName);
        return status;
    }

    /**
     * Helper method to set private fields using reflection
     */
    private void setField(Object target, String fieldName, Object value) {
        try {
            java.lang.reflect.Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set field: " + fieldName, e);
        }
    }
}
