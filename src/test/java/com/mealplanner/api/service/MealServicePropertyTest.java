package com.mealplanner.api.service;

import com.mealplanner.api.dto.MealDetailDto;
import com.mealplanner.api.dto.MealResponseDto;
import com.mealplanner.api.model.Meal;
import com.mealplanner.api.model.Nutrition;
import com.mealplanner.api.repository.*;
import net.jqwik.api.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Property-based tests for MealService
 */
public class MealServicePropertyTest {

    /**
     * Feature: meal-planner-api, Property 2: Name search containment
     * Validates: Requirements 1.2
     * 
     * For any meal name search query, all returned meals should have names 
     * that contain the search term (case-insensitive).
     */
    @Property(tries = 100)
    void searchByNameReturnsOnlyMatchingMeals(@ForAll("nonEmptySearchTerms") String searchTerm) {
        // Setup mocks
        MealRepository mealRepository = mock(MealRepository.class);
        NutritionRepository nutritionRepository = mock(NutritionRepository.class);
        NutritionFactRepository nutritionFactRepository = mock(NutritionFactRepository.class);
        AllergyRepository allergyRepository = mock(AllergyRepository.class);
        MealAllergyRepository mealAllergyRepository = mock(MealAllergyRepository.class);

        MealService mealService = new MealService(
            mealRepository,
            nutritionRepository,
            nutritionFactRepository,
            allergyRepository,
            mealAllergyRepository
        );

        // Create test data with meals that match the search term
        List<Meal> matchingMeals = createMatchingMeals(searchTerm);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Meal> mealPage = new PageImpl<>(matchingMeals, pageable, matchingMeals.size());

        // Mock repository to return matching meals
        when(mealRepository.findByMealNameContainingIgnoreCase(eq(searchTerm), any(Pageable.class)))
            .thenReturn(mealPage);
        when(nutritionFactRepository.findByNutrition_NutritionId(anyInt()))
            .thenReturn(new ArrayList<>());

        // Execute search
        Page<MealResponseDto> result = mealService.getMeals(searchTerm, null, null, pageable);

        // Verify: All returned meals should contain the search term (case-insensitive)
        for (MealResponseDto meal : result.getContent()) {
            String mealNameLower = meal.getMealName().toLowerCase();
            String searchTermLower = searchTerm.toLowerCase();
            assertTrue(
                mealNameLower.contains(searchTermLower),
                "Meal name '" + meal.getMealName() + "' should contain search term '" + searchTerm + "'"
            );
        }
    }

    /**
     * Provides non-empty search terms for property testing
     */
    @Provide
    Arbitrary<String> nonEmptySearchTerms() {
        return Arbitraries.strings()
            .alpha()
            .ofMinLength(1)
            .ofMaxLength(20)
            .filter(s -> !s.trim().isEmpty());
    }

    /**
     * Feature: meal-planner-api, Property 5: Meal detail completeness
     * Validates: Requirements 1.5
     * 
     * For any valid meal ID, the meal detail response should include all required fields:
     * mealId, mealName, recipeText, nutrition, allergens, and rating.
     */
    @Property(tries = 100)
    void getMealByIdReturnsCompleteDetails(@ForAll("validMealIds") Integer mealId) {
        // Setup mocks
        MealRepository mealRepository = mock(MealRepository.class);
        NutritionRepository nutritionRepository = mock(NutritionRepository.class);
        NutritionFactRepository nutritionFactRepository = mock(NutritionFactRepository.class);
        AllergyRepository allergyRepository = mock(AllergyRepository.class);
        MealAllergyRepository mealAllergyRepository = mock(MealAllergyRepository.class);

        MealService mealService = new MealService(
            mealRepository,
            nutritionRepository,
            nutritionFactRepository,
            allergyRepository,
            mealAllergyRepository
        );

        // Create test meal with complete data
        Meal testMeal = createCompleteMeal(mealId);

        // Mock repository responses
        when(mealRepository.findById(mealId)).thenReturn(Optional.of(testMeal));
        when(nutritionFactRepository.findByNutrition_NutritionId(anyInt()))
            .thenReturn(new ArrayList<>());
        when(mealAllergyRepository.findById_MealId(mealId))
            .thenReturn(new ArrayList<>());

        // Execute
        MealDetailDto result = mealService.getMealById(mealId);

        // Verify: All required fields should be present and non-null
        assertNotNull(result, "MealDetailDto should not be null");
        assertNotNull(result.getMealId(), "mealId should not be null");
        assertNotNull(result.getMealName(), "mealName should not be null");
        assertNotNull(result.getRecipeText(), "recipeText should not be null");
        assertNotNull(result.getNutrition(), "nutrition should not be null");
        assertNotNull(result.getAllergens(), "allergens should not be null");
        // Note: rating can be null as it's optional until users rate the meal
        
        // Verify the values match what we expect
        assertEquals(mealId, result.getMealId(), "mealId should match");
        assertFalse(result.getMealName().isEmpty(), "mealName should not be empty");
        assertFalse(result.getRecipeText().isEmpty(), "recipeText should not be empty");
    }

    /**
     * Provides valid meal IDs for property testing
     */
    @Provide
    Arbitrary<Integer> validMealIds() {
        return Arbitraries.integers().between(1, 10000);
    }

    /**
     * Helper method to create test meals that match the search term
     */
    private List<Meal> createMatchingMeals(String searchTerm) {
        List<Meal> meals = new ArrayList<>();
        Nutrition nutrition = new Nutrition();
        nutrition.setNutritionId(1);

        // Create meals that match the search term in different ways
        Meal matchingMeal1 = new Meal();
        matchingMeal1.setMealId(1);
        matchingMeal1.setMealName("Delicious " + searchTerm + " Dish");
        matchingMeal1.setRecipeText("Recipe 1");
        matchingMeal1.setRating(4);
        matchingMeal1.setNutrition(nutrition);
        meals.add(matchingMeal1);

        Meal matchingMeal2 = new Meal();
        matchingMeal2.setMealId(2);
        matchingMeal2.setMealName(searchTerm.toUpperCase() + " Special");
        matchingMeal2.setRecipeText("Recipe 2");
        matchingMeal2.setRating(5);
        matchingMeal2.setNutrition(nutrition);
        meals.add(matchingMeal2);

        Meal matchingMeal3 = new Meal();
        matchingMeal3.setMealId(3);
        matchingMeal3.setMealName("Amazing " + searchTerm.toLowerCase() + " Recipe");
        matchingMeal3.setRecipeText("Recipe 3");
        matchingMeal3.setRating(3);
        matchingMeal3.setNutrition(nutrition);
        meals.add(matchingMeal3);

        return meals;
    }

    /**
     * Helper method to create a complete meal with all required fields
     */
    private Meal createCompleteMeal(Integer mealId) {
        Nutrition nutrition = new Nutrition();
        nutrition.setNutritionId(1);

        Meal meal = new Meal();
        meal.setMealId(mealId);
        meal.setMealName("Test Meal " + mealId);
        meal.setRecipeText("Recipe for meal " + mealId);
        meal.setRating(4);
        meal.setNutrition(nutrition);

        return meal;
    }
}
