package com.mealplanner.api.service;

import com.mealplanner.api.dto.MealDetailDto;
import com.mealplanner.api.dto.MealResponseDto;
import com.mealplanner.api.model.Allergy;
import com.mealplanner.api.model.Meal;
import com.mealplanner.api.model.MealAllergy;
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
@SuppressWarnings("null")
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
     * Feature: meal-planner-api, Property 3: Minimum rating filter
     * Validates: Requirements 1.3
     * 
     * For any minimum rating filter value, all returned meals should have 
     * ratings greater than or equal to the specified minimum.
     */
    @Property(tries = 100)
    void filterByMinRatingReturnsOnlyQualifyingMeals(@ForAll("validRatings") Integer minRating) {
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

        // Create test data with meals that meet the minimum rating
        List<Meal> qualifyingMeals = createMealsWithMinimumRating(minRating);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Meal> mealPage = new PageImpl<>(qualifyingMeals, pageable, qualifyingMeals.size());

        // Mock repository to return meals with rating >= minRating
        when(mealRepository.findByRatingGreaterThanEqual(eq(minRating), any(Pageable.class)))
            .thenReturn(mealPage);
        when(nutritionFactRepository.findByNutrition_NutritionId(anyInt()))
            .thenReturn(new ArrayList<>());

        // Execute filter
        Page<MealResponseDto> result = mealService.getMeals(null, minRating, null, pageable);

        // Verify: All returned meals should have rating >= minRating
        for (MealResponseDto meal : result.getContent()) {
            Integer mealRating = meal.getRating();
            assertNotNull(mealRating, "Meal rating should not be null for filtered results");
            assertTrue(
                mealRating >= minRating,
                "Meal rating " + mealRating + " should be >= minimum rating " + minRating
            );
        }
    }

    /**
     * Provides valid rating values (1-5) for property testing
     */
    @Provide
    Arbitrary<Integer> validRatings() {
        return Arbitraries.integers().between(1, 5);
    }

    /**
     * Feature: meal-planner-api, Property 6: Valid rating acceptance
     * Validates: Requirements 2.1
     * 
     * For any rating value in the range [1, 5], the system should accept 
     * and persist the rating for a meal.
     */
    @Property(tries = 100)
    void validRatingIsAcceptedAndPersisted(
            @ForAll("validRatings") Integer rating,
            @ForAll("validMealIds") Integer mealId,
            @ForAll("validUserIds") Integer userId) {
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

        // Create test meal
        Meal testMeal = createCompleteMeal(mealId);
        testMeal.setRating(null); // Start with no rating

        // Mock repository to return the meal
        when(mealRepository.findById(mealId)).thenReturn(Optional.of(testMeal));
        when(mealRepository.save(any(Meal.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Execute: Rate the meal with a valid rating
        mealService.rateMeal(mealId, userId, rating);

        // Verify: The meal's rating should be updated to the provided value
        verify(mealRepository).save(any(Meal.class));
        assertEquals(rating, testMeal.getRating(), 
            "Meal rating should be set to " + rating + " after rating operation");
        
        // Verify the rating is within valid range [1, 5]
        assertTrue(testMeal.getRating() >= 1 && testMeal.getRating() <= 5,
            "Persisted rating should be within valid range [1, 5]");
    }

    /**
     * Provides valid user IDs for property testing
     */
    @Provide
    Arbitrary<Integer> validUserIds() {
        return Arbitraries.integers().between(1, 10000);
    }

    /**
     * Feature: meal-planner-api, Property 7: Invalid rating rejection
     * Validates: Requirements 2.2
     * 
     * For any rating value outside the range [1, 5], the system should reject 
     * the request with a validation error.
     */
    @Property(tries = 100)
    void invalidRatingIsRejected(
            @ForAll("invalidRatings") Integer rating,
            @ForAll("validMealIds") Integer mealId,
            @ForAll("validUserIds") Integer userId) {
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

        // Create test meal
        Meal testMeal = createCompleteMeal(mealId);

        // Mock repository to return the meal
        when(mealRepository.findById(mealId)).thenReturn(Optional.of(testMeal));

        // Execute: Attempt to rate the meal with an invalid rating
        // Verify: Should throw ValidationException
        com.mealplanner.api.exception.ValidationException exception = 
            assertThrows(
                com.mealplanner.api.exception.ValidationException.class,
                () -> mealService.rateMeal(mealId, userId, rating),
                "Rating " + rating + " should be rejected as it's outside the valid range [1, 5]"
            );

        // Verify the error message mentions the valid range
        String errorMessage = exception.getMessage();
        assertTrue(
            errorMessage.contains("1") && errorMessage.contains("5"),
            "Error message should mention the valid rating range [1, 5]"
        );

        // Verify that the meal was NOT saved (repository save should not be called)
        verify(mealRepository, never()).save(any(Meal.class));
    }

    /**
     * Provides invalid rating values (outside the range [1, 5]) for property testing
     */
    @Provide
    Arbitrary<Integer> invalidRatings() {
        // Generate integers that are either < 1 or > 5
        Arbitrary<Integer> tooLow = Arbitraries.integers().between(-1000, 0);
        Arbitrary<Integer> tooHigh = Arbitraries.integers().between(6, 1000);
        return Arbitraries.oneOf(tooLow, tooHigh);
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
     * Feature: meal-planner-api, Property 4: Allergen exclusion filter
     * Validates: Requirements 1.4
     * 
     * For any set of excluded allergen IDs, all returned meals should not 
     * contain any of the specified allergens.
     */
    @Property(tries = 100)
    void filterByExcludedAllergensReturnsOnlyMealsWithoutThoseAllergens(
            @ForAll("allergenIdLists") List<Integer> excludeAllergens) {
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

        // Create test data with meals that don't contain the excluded allergens
        List<Meal> mealsWithoutExcludedAllergens = createMealsWithoutAllergens(excludeAllergens);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Meal> mealPage = new PageImpl<>(mealsWithoutExcludedAllergens, pageable, 
                                             mealsWithoutExcludedAllergens.size());

        // Mock repository to return meals without excluded allergens
        when(mealRepository.findMealsExcludingAllergens(eq(excludeAllergens), any(Pageable.class)))
            .thenReturn(mealPage);
        when(nutritionFactRepository.findByNutrition_NutritionId(anyInt()))
            .thenReturn(new ArrayList<>());
        
        // Mock allergen repository to return allergens for each meal
        for (Meal meal : mealsWithoutExcludedAllergens) {
            List<MealAllergy> mealAllergies = createMealAllergiesForMeal(meal, excludeAllergens);
            when(mealAllergyRepository.findById_MealId(meal.getMealId()))
                .thenReturn(mealAllergies);
        }

        // Execute filter
        Page<MealResponseDto> result = mealService.getMeals(null, null, excludeAllergens, pageable);

        // Verify: No returned meals should contain any of the excluded allergens
        for (MealResponseDto mealDto : result.getContent()) {
            // Get the meal's allergens from the mock
            List<MealAllergy> mealAllergies = mealAllergyRepository.findById_MealId(mealDto.getMealId());
            
            // Extract allergen IDs from the meal
            List<Integer> mealAllergenIds = mealAllergies.stream()
                .map(ma -> ma.getAllergy().getAllergyId())
                .collect(java.util.stream.Collectors.toList());
            
            // Verify none of the excluded allergens are present in this meal
            for (Integer excludedAllergenId : excludeAllergens) {
                assertFalse(
                    mealAllergenIds.contains(excludedAllergenId),
                    "Meal " + mealDto.getMealId() + " should not contain excluded allergen " + excludedAllergenId
                );
            }
        }
    }

    /**
     * Provides lists of allergen IDs for property testing
     */
    @Provide
    Arbitrary<List<Integer>> allergenIdLists() {
        return Arbitraries.integers()
            .between(1, 20)  // Allergen IDs from 1 to 20
            .list()
            .ofMinSize(1)
            .ofMaxSize(5);   // Test with 1-5 excluded allergens
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

    /**
     * Helper method to create test meals with ratings >= minRating
     */
    private List<Meal> createMealsWithMinimumRating(Integer minRating) {
        List<Meal> meals = new ArrayList<>();
        Nutrition nutrition = new Nutrition();
        nutrition.setNutritionId(1);

        // Create meals with ratings at and above the minimum
        for (int i = 0; i < 3; i++) {
            Meal meal = new Meal();
            meal.setMealId(i + 1);
            meal.setMealName("Meal " + (i + 1));
            meal.setRecipeText("Recipe " + (i + 1));
            // Assign ratings >= minRating (minRating, minRating+1, or 5)
            int rating = Math.min(5, minRating + i);
            meal.setRating(rating);
            meal.setNutrition(nutrition);
            meals.add(meal);
        }

        return meals;
    }

    /**
     * Helper method to create test meals that don't contain the excluded allergens
     */
    private List<Meal> createMealsWithoutAllergens(List<Integer> excludeAllergens) {
        List<Meal> meals = new ArrayList<>();
        Nutrition nutrition = new Nutrition();
        nutrition.setNutritionId(1);

        // Create meals with allergens that are NOT in the excluded list
        for (int i = 0; i < 3; i++) {
            Meal meal = new Meal();
            meal.setMealId(i + 1);
            meal.setMealName("Safe Meal " + (i + 1));
            meal.setRecipeText("Recipe " + (i + 1));
            meal.setRating(4);
            meal.setNutrition(nutrition);
            meals.add(meal);
        }

        return meals;
    }

    /**
     * Helper method to create MealAllergy associations for a meal that don't include excluded allergens
     */
    private List<MealAllergy> createMealAllergiesForMeal(Meal meal, List<Integer> excludeAllergens) {
        List<MealAllergy> mealAllergies = new ArrayList<>();
        
        // Find the max excluded allergen ID to create safe allergen IDs
        int maxExcludedId = excludeAllergens.stream().max(Integer::compareTo).orElse(0);
        
        // Create 1-2 allergens for this meal that are NOT in the excluded list
        // Use allergen IDs that are higher than any excluded ID
        for (int i = 0; i < 2; i++) {
            int safeAllergenId = maxExcludedId + 100 + i;  // Use IDs well outside the excluded range
            
            Allergy allergy = new Allergy();
            allergy.setAllergyId(safeAllergenId);
            allergy.setAllergyName("Safe Allergen " + safeAllergenId);
            
            MealAllergy mealAllergy = new MealAllergy(meal, allergy);
            mealAllergies.add(mealAllergy);
        }
        
        return mealAllergies;
    }

    /**
     * Feature: meal-planner-api, Property 9: Meal creation with ID generation
     * Validates: Requirements 3.1
     * 
     * For any valid meal creation request by an administrator, the system should 
     * persist the meal and return it with a generated ID that is non-null and positive.
     */
    @Property(tries = 100)
    void createMealGeneratesValidId(
            @ForAll("mealNames") String mealName,
            @ForAll("recipeTexts") String recipeText,
            @ForAll("validNutritionIds") Integer nutritionId) {
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

        // Create test nutrition
        Nutrition testNutrition = new Nutrition();
        testNutrition.setNutritionId(nutritionId);

        // Mock nutrition repository to return the nutrition
        when(nutritionRepository.findById(nutritionId)).thenReturn(Optional.of(testNutrition));

        // Mock meal repository save to simulate ID generation
        when(mealRepository.save(any(Meal.class))).thenAnswer(invocation -> {
            Meal savedMeal = invocation.getArgument(0);
            // Simulate database ID generation
            if (savedMeal.getMealId() == null) {
                savedMeal.setMealId(generateRandomPositiveId());
            }
            return savedMeal;
        });

        // Mock nutrition facts repository
        when(nutritionFactRepository.findByNutrition_NutritionId(anyInt()))
            .thenReturn(new ArrayList<>());

        // Create DTO for meal creation
        com.mealplanner.api.dto.CreateMealDto createDto = 
            new com.mealplanner.api.dto.CreateMealDto(mealName, recipeText, nutritionId, null);

        // Execute: Create the meal
        com.mealplanner.api.dto.MealResponseDto result = mealService.createMeal(createDto);

        // Verify: The returned meal should have a non-null, positive ID
        assertNotNull(result, "Created meal response should not be null");
        assertNotNull(result.getMealId(), "Created meal should have a non-null ID");
        assertTrue(result.getMealId() > 0, 
            "Created meal ID should be positive, but was: " + result.getMealId());

        // Verify: The meal data should match the input
        assertEquals(mealName, result.getMealName(), 
            "Created meal name should match the input");

        // Verify: The meal was saved to the repository
        verify(mealRepository).save(any(Meal.class));
    }

    /**
     * Provides valid meal names for property testing
     */
    @Provide
    Arbitrary<String> mealNames() {
        return Arbitraries.strings()
            .alpha()
            .numeric()
            .withChars(' ', '-', '\'')
            .ofMinLength(1)
            .ofMaxLength(50)
            .filter(s -> !s.trim().isEmpty());
    }

    /**
     * Provides valid recipe texts for property testing
     */
    @Provide
    Arbitrary<String> recipeTexts() {
        return Arbitraries.strings()
            .alpha()
            .numeric()
            .withChars(' ', '.', ',', '-', '\n')
            .ofMinLength(10)
            .ofMaxLength(200)
            .filter(s -> !s.trim().isEmpty());
    }

    /**
     * Provides valid nutrition IDs for property testing
     */
    @Provide
    Arbitrary<Integer> validNutritionIds() {
        return Arbitraries.integers().between(1, 1000);
    }

    /**
     * Helper method to generate a random positive ID (simulating database auto-increment)
     */
    private int generateRandomPositiveId() {
        return (int) (Math.random() * 10000) + 1;
    }

    /**
     * Feature: meal-planner-api, Property 10: Meal update persistence
     * Validates: Requirements 3.2
     * 
     * For any existing meal, when an administrator updates it with new data, 
     * retrieving the meal should return the updated values.
     */
    @Property(tries = 100)
    void updateMealPersistsChanges(
            @ForAll("validMealIds") Integer mealId,
            @ForAll("mealNames") String newMealName,
            @ForAll("recipeTexts") String newRecipeText,
            @ForAll("validRatings") Integer newRating,
            @ForAll("validNutritionIds") Integer newNutritionId) {
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

        // Create existing meal with original data
        Meal existingMeal = createCompleteMeal(mealId);
        String originalMealName = existingMeal.getMealName();
        String originalRecipeText = existingMeal.getRecipeText();
        Integer originalRating = existingMeal.getRating();

        // Create new nutrition for update
        Nutrition newNutrition = new Nutrition();
        newNutrition.setNutritionId(newNutritionId);

        // Mock repository responses
        when(mealRepository.findById(mealId)).thenReturn(Optional.of(existingMeal));
        when(nutritionRepository.findById(newNutritionId)).thenReturn(Optional.of(newNutrition));
        when(mealRepository.save(any(Meal.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(mealAllergyRepository.findById_MealId(mealId)).thenReturn(new ArrayList<>());
        when(nutritionFactRepository.findByNutrition_NutritionId(anyInt()))
            .thenReturn(new ArrayList<>());

        // Create update DTO with new values
        com.mealplanner.api.dto.UpdateMealDto updateDto = 
            new com.mealplanner.api.dto.UpdateMealDto(
                newMealName, 
                newRecipeText, 
                newNutritionId, 
                newRating, 
                null  // No allergen updates for this test
            );

        // Execute: Update the meal
        com.mealplanner.api.dto.MealResponseDto result = mealService.updateMeal(mealId, updateDto);

        // Verify: The meal was saved to the repository
        verify(mealRepository).save(any(Meal.class));

        // Verify: The meal's fields were updated with the new values
        assertEquals(newMealName, existingMeal.getMealName(), 
            "Meal name should be updated to: " + newMealName);
        assertEquals(newRecipeText, existingMeal.getRecipeText(), 
            "Recipe text should be updated to: " + newRecipeText);
        assertEquals(newRating, existingMeal.getRating(), 
            "Rating should be updated to: " + newRating);
        assertEquals(newNutritionId, existingMeal.getNutrition().getNutritionId(), 
            "Nutrition ID should be updated to: " + newNutritionId);

        // Verify: The returned DTO reflects the updated values
        assertNotNull(result, "Update result should not be null");
        assertEquals(mealId, result.getMealId(), "Meal ID should remain unchanged");
        assertEquals(newMealName, result.getMealName(), 
            "Returned meal name should match the updated value");

        // Verify: The original values were actually changed (not a no-op)
        assertFalse(
            newMealName.equals(originalMealName) && 
            newRecipeText.equals(originalRecipeText) && 
            newRating.equals(originalRating),
            "At least some values should have changed from the original"
        );
    }

    /**
     * Feature: meal-planner-api, Property 11: Meal deletion removes from catalog
     * Validates: Requirements 3.3
     * 
     * For any existing meal, after an administrator deletes it, attempting to 
     * retrieve the meal should result in a not found error.
     */
    @Property(tries = 100)
    void deleteMealRemovesFromCatalog(@ForAll("validMealIds") Integer mealId) {
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

        // Create existing meal
        Meal existingMeal = createCompleteMeal(mealId);

        // Mock repository responses for deletion
        when(mealRepository.findById(mealId))
            .thenReturn(Optional.of(existingMeal))  // First call: meal exists for deletion
            .thenReturn(Optional.empty());           // Second call: meal no longer exists after deletion

        when(mealAllergyRepository.findById_MealId(mealId))
            .thenReturn(new ArrayList<>());

        // Execute: Delete the meal
        mealService.deleteMeal(mealId);

        // Verify: The meal and its allergen associations were deleted
        verify(mealAllergyRepository).deleteAll(any());
        verify(mealRepository).delete(existingMeal);

        // Verify: Attempting to retrieve the deleted meal should throw ResourceNotFoundException
        com.mealplanner.api.exception.ResourceNotFoundException exception = 
            assertThrows(
                com.mealplanner.api.exception.ResourceNotFoundException.class,
                () -> mealService.getMealById(mealId),
                "Attempting to retrieve deleted meal " + mealId + " should throw ResourceNotFoundException"
            );

        // Verify: The error message mentions the meal ID
        String errorMessage = exception.getMessage();
        assertTrue(
            errorMessage.contains(mealId.toString()),
            "Error message should mention the meal ID: " + mealId
        );
    }
}
