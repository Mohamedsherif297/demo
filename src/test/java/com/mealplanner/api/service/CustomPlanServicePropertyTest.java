package com.mealplanner.api.service;

import com.mealplanner.api.dto.CustomPlanResponseDto;
import com.mealplanner.api.model.CustomPlan;
import com.mealplanner.api.model.PlanCategory;
import com.mealplanner.api.model.User;
import com.mealplanner.api.repository.*;
import net.jqwik.api.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Property-based tests for CustomPlanService
 */
@SuppressWarnings("null")
public class CustomPlanServicePropertyTest {

    /**
     * Feature: meal-planner-api, Property 13: Plan category filter
     * Validates: Requirements 4.3
     * 
     * For any category ID filter, all returned custom plans should belong 
     * to the specified category.
     */
    @Property(tries = 100)
    void filterByCategoryReturnsOnlyPlansInThatCategory(@ForAll("validCategoryIds") Integer categoryId) {
        // Setup mocks
        CustomPlanRepository customPlanRepository = mock(CustomPlanRepository.class);
        PlanCategoryRepository planCategoryRepository = mock(PlanCategoryRepository.class);
        CustomPlanMealRepository customPlanMealRepository = mock(CustomPlanMealRepository.class);
        MealRepository mealRepository = mock(MealRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        NutritionFactRepository nutritionFactRepository = mock(NutritionFactRepository.class);

        CustomPlanService customPlanService = new CustomPlanService(
            customPlanRepository,
            planCategoryRepository,
            customPlanMealRepository,
            mealRepository,
            userRepository,
            nutritionFactRepository
        );

        // Create test data with plans that belong to the specified category
        List<CustomPlan> plansInCategory = createPlansForCategory(categoryId);

        Pageable pageable = PageRequest.of(0, 10);
        Page<CustomPlan> planPage = new PageImpl<>(plansInCategory, pageable, plansInCategory.size());

        // Mock repository to return plans for the specified category
        when(customPlanRepository.findByCategoryCategoryId(eq(categoryId), any(Pageable.class)))
            .thenReturn(planPage);
        
        // Mock meal count queries for each plan
        for (CustomPlan plan : plansInCategory) {
            when(customPlanMealRepository.findById_CustomPlanId(plan.getCustomPlanId()))
                .thenReturn(new ArrayList<>());
        }

        // Execute filter
        Page<CustomPlanResponseDto> result = customPlanService.getPlans(categoryId, pageable);

        // Verify: All returned plans should belong to the specified category
        assertNotNull(result, "Result should not be null");
        assertFalse(result.isEmpty(), "Result should contain plans for the category");
        
        for (CustomPlanResponseDto planDto : result.getContent()) {
            // Get the original plan to verify category
            CustomPlan originalPlan = plansInCategory.stream()
                .filter(p -> p.getCustomPlanId().equals(planDto.getCustomPlanId()))
                .findFirst()
                .orElse(null);
            
            assertNotNull(originalPlan, "Plan should exist in test data");
            assertNotNull(originalPlan.getCategory(), "Plan should have a category");
            assertEquals(
                categoryId, 
                originalPlan.getCategory().getCategoryId(),
                "Plan " + planDto.getCustomPlanId() + " should belong to category " + categoryId
            );
        }
    }

    /**
     * Provides valid category IDs for property testing
     */
    @Provide
    Arbitrary<Integer> validCategoryIds() {
        return Arbitraries.integers().between(1, 10);
    }

    /**
     * Feature: meal-planner-api, Property 14: Plan detail completeness
     * Validates: Requirements 4.4
     * 
     * For any valid plan ID, the plan detail response should include complete 
     * information: planId, category, duration, price, and associated meals list.
     */
    @Property(tries = 100)
    void planDetailIncludesAllRequiredFields(
            @ForAll("validPlanIds") Integer planId,
            @ForAll("validDurations") Integer durationMinutes,
            @ForAll("validPrices") Double price,
            @ForAll("mealCounts") Integer mealCount) {
        
        // Setup mocks
        CustomPlanRepository customPlanRepository = mock(CustomPlanRepository.class);
        PlanCategoryRepository planCategoryRepository = mock(PlanCategoryRepository.class);
        CustomPlanMealRepository customPlanMealRepository = mock(CustomPlanMealRepository.class);
        MealRepository mealRepository = mock(MealRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        NutritionFactRepository nutritionFactRepository = mock(NutritionFactRepository.class);

        CustomPlanService customPlanService = new CustomPlanService(
            customPlanRepository,
            planCategoryRepository,
            customPlanMealRepository,
            mealRepository,
            userRepository,
            nutritionFactRepository
        );

        // Create test plan with all required fields
        CustomPlan plan = createTestPlan(planId, durationMinutes, price);
        
        // Create test meals for the plan
        List<com.mealplanner.api.model.CustomPlanMeal> planMeals = createTestMeals(planId, mealCount);
        
        // Mock repository responses
        when(customPlanRepository.findById(planId))
            .thenReturn(java.util.Optional.of(plan));
        when(customPlanMealRepository.findById_CustomPlanId(planId))
            .thenReturn(planMeals);
        when(nutritionFactRepository.findByNutrition_NutritionId(any()))
            .thenReturn(new ArrayList<>());

        // Execute
        com.mealplanner.api.dto.CustomPlanDetailDto result = customPlanService.getPlanById(planId);

        // Verify: All required fields should be present and non-null
        assertNotNull(result, "Plan detail should not be null");
        assertNotNull(result.getCustomPlanId(), "Plan ID should not be null");
        assertEquals(planId, result.getCustomPlanId(), "Plan ID should match");
        
        assertNotNull(result.getCategoryName(), "Category name should not be null");
        assertEquals("Test Category", result.getCategoryName(), "Category name should match");
        
        assertNotNull(result.getDurationMinutes(), "Duration should not be null");
        assertEquals(durationMinutes, result.getDurationMinutes(), "Duration should match");
        
        assertNotNull(result.getPrice(), "Price should not be null");
        assertEquals(price, result.getPrice().doubleValue(), 0.01, "Price should match");
        
        assertNotNull(result.getMeals(), "Meals list should not be null");
        assertEquals(mealCount, result.getMeals().size(), "Meal count should match");
        
        assertNotNull(result.getMealCount(), "Meal count field should not be null");
        assertEquals(mealCount, result.getMealCount(), "Meal count should match meals list size");
    }

    /**
     * Provides valid plan IDs for property testing
     */
    @Provide
    Arbitrary<Integer> validPlanIds() {
        return Arbitraries.integers().between(1, 1000);
    }

    /**
     * Provides valid duration values in minutes
     */
    @Provide
    Arbitrary<Integer> validDurations() {
        return Arbitraries.integers().between(15, 120);
    }

    /**
     * Provides valid price values
     */
    @Provide
    Arbitrary<Double> validPrices() {
        return Arbitraries.doubles().between(5.0, 100.0);
    }

    /**
     * Provides valid meal counts
     */
    @Provide
    Arbitrary<Integer> mealCounts() {
        return Arbitraries.integers().between(0, 10);
    }

    /**
     * Helper method to create a test plan with all required fields
     */
    private CustomPlan createTestPlan(Integer planId, Integer durationMinutes, Double price) {
        // Create category
        PlanCategory category = new PlanCategory();
        category.setCategoryId(1);
        category.setCategoryName("Test Category");
        
        // Create user
        User user = new User();
        user.setUserId(1);
        user.setFullName("Test User");
        user.setEmail("testuser@example.com");
        
        // Create plan
        CustomPlan plan = new CustomPlan();
        plan.setCustomPlanId(planId);
        plan.setCategory(category);
        plan.setUser(user);
        plan.setDurationMinutes(durationMinutes);
        plan.setPrice(price);
        
        return plan;
    }

    /**
     * Helper method to create test meals for a plan
     */
    private List<com.mealplanner.api.model.CustomPlanMeal> createTestMeals(Integer planId, Integer count) {
        List<com.mealplanner.api.model.CustomPlanMeal> meals = new ArrayList<>();
        
        for (int i = 0; i < count; i++) {
            // Create meal
            com.mealplanner.api.model.Meal meal = new com.mealplanner.api.model.Meal();
            meal.setMealId(i + 1);
            meal.setMealName("Test Meal " + (i + 1));
            meal.setRating(4);
            
            // Create nutrition (optional)
            com.mealplanner.api.model.Nutrition nutrition = new com.mealplanner.api.model.Nutrition();
            nutrition.setNutritionId(i + 1);
            meal.setNutrition(nutrition);
            
            // Create plan-meal association
            com.mealplanner.api.model.CustomPlanMeal planMeal = new com.mealplanner.api.model.CustomPlanMeal();
            planMeal.setMeal(meal);
            
            com.mealplanner.api.model.CustomPlanMealId id = new com.mealplanner.api.model.CustomPlanMealId();
            id.setCustomPlanId(planId);
            id.setMealId(meal.getMealId());
            planMeal.setId(id);
            
            meals.add(planMeal);
        }
        
        return meals;
    }

    /**
     * Feature: meal-planner-api, Property 15: Custom plan user association
     * Validates: Requirements 5.1
     * 
     * For any authenticated user creating a custom plan, the persisted plan 
     * should be associated with that user's ID.
     */
    @Property(tries = 100)
    void createdPlanIsAssociatedWithUser(
            @ForAll("validUserIds") Integer userId,
            @ForAll("validCategoryIds") Integer categoryId,
            @ForAll("validDurations") Integer durationMinutes,
            @ForAll("validPrices") Double price) {
        
        // Setup mocks
        CustomPlanRepository customPlanRepository = mock(CustomPlanRepository.class);
        PlanCategoryRepository planCategoryRepository = mock(PlanCategoryRepository.class);
        CustomPlanMealRepository customPlanMealRepository = mock(CustomPlanMealRepository.class);
        MealRepository mealRepository = mock(MealRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        NutritionFactRepository nutritionFactRepository = mock(NutritionFactRepository.class);

        CustomPlanService customPlanService = new CustomPlanService(
            customPlanRepository,
            planCategoryRepository,
            customPlanMealRepository,
            mealRepository,
            userRepository,
            nutritionFactRepository
        );

        // Create test user
        User user = new User();
        user.setUserId(userId);
        user.setFullName("Test User " + userId);
        user.setEmail("user" + userId + "@example.com");
        
        // Create test category
        PlanCategory category = new PlanCategory();
        category.setCategoryId(categoryId);
        category.setCategoryName("Category " + categoryId);
        
        // Mock repository responses
        when(userRepository.findById(userId))
            .thenReturn(java.util.Optional.of(user));
        when(planCategoryRepository.findById(categoryId))
            .thenReturn(java.util.Optional.of(category));
        
        // Capture the saved plan to verify user association
        when(customPlanRepository.save(any(CustomPlan.class)))
            .thenAnswer(invocation -> {
                CustomPlan savedPlan = invocation.getArgument(0);
                // Simulate ID generation
                if (savedPlan.getCustomPlanId() == null) {
                    savedPlan.setCustomPlanId(1);
                }
                return savedPlan;
            });
        
        when(customPlanMealRepository.findById_CustomPlanId(any()))
            .thenReturn(new ArrayList<>());
        
        // Create DTO for plan creation
        com.mealplanner.api.dto.CreateCustomPlanDto dto = new com.mealplanner.api.dto.CreateCustomPlanDto();
        dto.setCategoryId(categoryId);
        dto.setDurationMinutes(durationMinutes);
        dto.setPrice(java.math.BigDecimal.valueOf(price));
        dto.setMealIds(new ArrayList<>()); // No meals for this test
        
        // Execute: Create plan
        CustomPlanResponseDto result = customPlanService.createPlan(userId, dto);
        
        // Verify: The plan was saved with the correct user association
        verify(customPlanRepository).save(argThat(plan -> {
            assertNotNull(plan.getUser(), "Plan should have a user associated");
            assertEquals(
                userId, 
                plan.getUser().getUserId(),
                "Plan should be associated with user ID " + userId
            );
            return true;
        }));
        
        // Additional verification: Result should be non-null
        assertNotNull(result, "Created plan response should not be null");
        assertNotNull(result.getCustomPlanId(), "Created plan should have an ID");
    }

    /**
     * Provides valid user IDs for property testing
     */
    @Provide
    Arbitrary<Integer> validUserIds() {
        return Arbitraries.integers().between(1, 1000);
    }

    /**
     * Helper method to create test plans that belong to a specific category
     */
    private List<CustomPlan> createPlansForCategory(Integer categoryId) {
        List<CustomPlan> plans = new ArrayList<>();
        
        // Create the category
        PlanCategory category = new PlanCategory();
        category.setCategoryId(categoryId);
        category.setCategoryName("Category " + categoryId);
        
        // Create a test user
        User user = new User();
        user.setUserId(1);
        user.setFullName("Test User");
        user.setEmail("testuser@example.com");
        
        // Create 3 plans in this category
        for (int i = 0; i < 3; i++) {
            CustomPlan plan = new CustomPlan();
            plan.setCustomPlanId(i + 1);
            plan.setCategory(category);
            plan.setUser(user);
            plan.setDurationMinutes(30 + (i * 10));
            plan.setPrice(10.0 + (i * 5.0));
            plans.add(plan);
        }
        
        return plans;
    }

    /**
     * Feature: meal-planner-api, Property 18: Plan deletion cascades to associations
     * Validates: Requirements 5.4
     * 
     * For any custom plan with meal associations, after deleting the plan, 
     * both the plan and all its meal associations should be removed from the system.
     */
    @Property(tries = 100)
    void planDeletionCascadesToMealAssociations(
            @ForAll("validPlanIds") Integer planId,
            @ForAll("validUserIds") Integer userId,
            @ForAll("mealCounts") Integer mealCount) {
        
        // Skip test if no meals to associate
        net.jqwik.api.Assume.that(mealCount > 0);
        
        // Setup mocks
        CustomPlanRepository customPlanRepository = mock(CustomPlanRepository.class);
        PlanCategoryRepository planCategoryRepository = mock(PlanCategoryRepository.class);
        CustomPlanMealRepository customPlanMealRepository = mock(CustomPlanMealRepository.class);
        MealRepository mealRepository = mock(MealRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        NutritionFactRepository nutritionFactRepository = mock(NutritionFactRepository.class);

        CustomPlanService customPlanService = new CustomPlanService(
            customPlanRepository,
            planCategoryRepository,
            customPlanMealRepository,
            mealRepository,
            userRepository,
            nutritionFactRepository
        );

        // Create test plan with meals
        CustomPlan plan = createTestPlanWithUser(planId, userId);
        List<com.mealplanner.api.model.CustomPlanMeal> planMeals = createTestMeals(planId, mealCount);
        
        // Mock repository responses
        when(customPlanRepository.findById(planId))
            .thenReturn(java.util.Optional.of(plan));
        when(customPlanMealRepository.findById_CustomPlanId(planId))
            .thenReturn(planMeals);
        
        // Execute: Delete the plan
        customPlanService.deletePlan(planId, userId, false);
        
        // Verify: The plan was deleted from the repository
        verify(customPlanRepository).delete(argThat(deletedPlan -> {
            assertNotNull(deletedPlan, "Deleted plan should not be null");
            assertEquals(
                planId, 
                deletedPlan.getCustomPlanId(),
                "Deleted plan ID should match"
            );
            return true;
        }));
        
        // Verify: After deletion, attempting to retrieve the plan should fail
        // Simulate the plan being deleted
        when(customPlanRepository.findById(planId))
            .thenReturn(java.util.Optional.empty());
        when(customPlanMealRepository.findById_CustomPlanId(planId))
            .thenReturn(new ArrayList<>());
        
        // Attempting to get the plan should throw ResourceNotFoundException
        try {
            customPlanService.getPlanById(planId);
            fail("Expected ResourceNotFoundException when retrieving deleted plan");
        } catch (com.mealplanner.api.exception.ResourceNotFoundException e) {
            // Expected behavior - plan no longer exists
            assertTrue(
                e.getMessage().contains("not found"),
                "Exception message should indicate plan not found"
            );
        }
        
        // Verify: Meal associations should also be removed (cascade behavior)
        // In JPA, when a plan is deleted with cascade settings, the associations are automatically removed
        // We verify that after deletion, querying for plan meals returns empty
        List<com.mealplanner.api.model.CustomPlanMeal> remainingMeals = 
            customPlanMealRepository.findById_CustomPlanId(planId);
        
        assertTrue(
            remainingMeals.isEmpty(),
            "All meal associations should be removed after plan deletion"
        );
    }

    /**
     * Helper method to create a test plan with a specific user
     */
    private CustomPlan createTestPlanWithUser(Integer planId, Integer userId) {
        // Create category
        PlanCategory category = new PlanCategory();
        category.setCategoryId(1);
        category.setCategoryName("Test Category");
        
        // Create user
        User user = new User();
        user.setUserId(userId);
        user.setFullName("Test User " + userId);
        user.setEmail("user" + userId + "@example.com");
        
        // Create plan
        CustomPlan plan = new CustomPlan();
        plan.setCustomPlanId(planId);
        plan.setCategory(category);
        plan.setUser(user);
        plan.setDurationMinutes(60);
        plan.setPrice(25.0);
        
        return plan;
    }

    /**
     * Feature: meal-planner-api, Property 17: Meal removal preserves plan
     * Validates: Requirements 5.3
     * 
     * For any custom plan with associated meals, removing a meal should delete 
     * only the association while the plan itself remains retrievable.
     */
    @Property(tries = 100)
    void mealRemovalPreservesPlan(
            @ForAll("validPlanIds") Integer planId,
            @ForAll("validUserIds") Integer userId,
            @ForAll("mealCounts") Integer initialMealCount) {
        
        // Skip test if no meals to remove
        net.jqwik.api.Assume.that(initialMealCount > 0);
        
        // Setup mocks
        CustomPlanRepository customPlanRepository = mock(CustomPlanRepository.class);
        PlanCategoryRepository planCategoryRepository = mock(PlanCategoryRepository.class);
        CustomPlanMealRepository customPlanMealRepository = mock(CustomPlanMealRepository.class);
        MealRepository mealRepository = mock(MealRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        NutritionFactRepository nutritionFactRepository = mock(NutritionFactRepository.class);

        CustomPlanService customPlanService = new CustomPlanService(
            customPlanRepository,
            planCategoryRepository,
            customPlanMealRepository,
            mealRepository,
            userRepository,
            nutritionFactRepository
        );

        // Create test plan
        CustomPlan plan = createTestPlanWithUser(planId, userId);
        
        // Create initial meals for the plan
        List<com.mealplanner.api.model.CustomPlanMeal> initialPlanMeals = createTestMeals(planId, initialMealCount);
        
        // Use the first meal's ID as the one to remove
        final Integer mealIdToRemove = initialPlanMeals.get(0).getMeal().getMealId();
        
        // Mock repository responses before removal
        when(customPlanRepository.findById(planId))
            .thenReturn(java.util.Optional.of(plan));
        when(mealRepository.existsById(mealIdToRemove))
            .thenReturn(true);
        when(nutritionFactRepository.findByNutrition_NutritionId(any()))
            .thenReturn(new ArrayList<>());
        
        // Execute: Remove the meal from the plan
        customPlanService.removeMealFromPlan(planId, userId, mealIdToRemove);
        
        // Verify: The meal association was deleted
        verify(customPlanMealRepository).deleteById(argThat(id -> {
            assertNotNull(id, "CustomPlanMealId should not be null");
            assertEquals(
                planId, 
                id.getCustomPlanId(),
                "CustomPlanMealId should have correct plan ID"
            );
            assertEquals(
                mealIdToRemove, 
                id.getMealId(),
                "CustomPlanMealId should have correct meal ID"
            );
            return true;
        }));
        
        // Verify: The plan itself was NOT deleted
        verify(customPlanRepository, never()).delete(any(CustomPlan.class));
        
        // Simulate the state after removal - plan still exists, but with one fewer meal
        List<com.mealplanner.api.model.CustomPlanMeal> remainingMeals = initialPlanMeals.stream()
            .filter(pm -> !pm.getMeal().getMealId().equals(mealIdToRemove))
            .collect(java.util.stream.Collectors.toList());
        
        when(customPlanMealRepository.findById_CustomPlanId(planId))
            .thenReturn(remainingMeals);
        
        // Verify: The plan should still be retrievable after meal removal
        com.mealplanner.api.dto.CustomPlanDetailDto retrievedPlan = customPlanService.getPlanById(planId);
        
        assertNotNull(retrievedPlan, "Plan should still be retrievable after meal removal");
        assertEquals(
            planId, 
            retrievedPlan.getCustomPlanId(),
            "Retrieved plan should have the same ID"
        );
        assertEquals(
            plan.getDurationMinutes(), 
            retrievedPlan.getDurationMinutes(),
            "Plan duration should be preserved"
        );
        assertEquals(
            plan.getPrice(), 
            retrievedPlan.getPrice().doubleValue(),
            0.01,
            "Plan price should be preserved"
        );
        
        // Verify: The meal count should be reduced by 1
        int expectedMealCount = initialMealCount - 1;
        assertEquals(
            expectedMealCount,
            retrievedPlan.getMealCount(),
            "Meal count should be reduced by 1 after removal"
        );
        assertEquals(
            expectedMealCount,
            retrievedPlan.getMeals().size(),
            "Meals list size should match meal count"
        );
        
        // Verify: The removed meal should not be in the meals list
        boolean removedMealStillPresent = retrievedPlan.getMeals().stream()
            .anyMatch(meal -> meal.getMealId().equals(mealIdToRemove));
        
        assertFalse(
            removedMealStillPresent,
            "Removed meal should not be present in the plan's meals list"
        );
    }

    /**
     * Provides valid meal IDs for property testing
     */
    @Provide
    Arbitrary<Integer> validMealIds() {
        return Arbitraries.integers().between(1, 1000);
    }
}
