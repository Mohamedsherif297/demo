package com.mealplanner.api.service;

import com.mealplanner.api.dto.*;
import com.mealplanner.api.exception.ResourceNotFoundException;
import com.mealplanner.api.exception.ValidationException;
import com.mealplanner.api.model.*;
import com.mealplanner.api.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("null")
class MealServiceTest {

    @Mock
    private MealRepository mealRepository;

    @Mock
    private NutritionRepository nutritionRepository;

    @Mock
    private NutritionFactRepository nutritionFactRepository;

    @Mock
    private AllergyRepository allergyRepository;

    @Mock
    private MealAllergyRepository mealAllergyRepository;

    @InjectMocks
    private MealService mealService;

    private Meal testMeal;
    private Nutrition testNutrition;

    @BeforeEach
    void setUp() {
        testNutrition = new Nutrition();
        testNutrition.setNutritionId(1);

        testMeal = new Meal();
        testMeal.setMealId(1);
        testMeal.setMealName("Test Meal");
        testMeal.setRecipeText("Test Recipe");
        testMeal.setRating(4);
        testMeal.setNutrition(testNutrition);
    }

    @Test
    void getMeals_WithNoFilters_ReturnsAllMeals() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        List<Meal> meals = List.of(testMeal);
        Page<Meal> mealPage = new PageImpl<>(meals, pageable, 1);

        when(mealRepository.findAll(pageable)).thenReturn(mealPage);
        when(nutritionFactRepository.findByNutrition_NutritionId(anyInt())).thenReturn(new ArrayList<>());

        // Act
        Page<MealResponseDto> result = mealService.getMeals(null, null, null, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Test Meal", result.getContent().get(0).getMealName());
        verify(mealRepository).findAll(pageable);
    }

    @Test
    void getMealById_WithValidId_ReturnsMealDetail() {
        // Arrange
        when(mealRepository.findById(1)).thenReturn(Optional.of(testMeal));
        when(nutritionFactRepository.findByNutrition_NutritionId(1)).thenReturn(new ArrayList<>());
        when(mealAllergyRepository.findById_MealId(1)).thenReturn(new ArrayList<>());

        // Act
        MealDetailDto result = mealService.getMealById(1);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getMealId());
        assertEquals("Test Meal", result.getMealName());
        assertEquals("Test Recipe", result.getRecipeText());
        verify(mealRepository).findById(1);
    }

    @Test
    void getMealById_WithInvalidId_ThrowsResourceNotFoundException() {
        // Arrange
        when(mealRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> mealService.getMealById(999));
    }

    @Test
    void rateMeal_WithValidRating_UpdatesMealRating() {
        // Arrange
        when(mealRepository.findById(1)).thenReturn(Optional.of(testMeal));
        when(mealRepository.save(any(Meal.class))).thenReturn(testMeal);

        // Act
        mealService.rateMeal(1, 1, 5);

        // Assert
        verify(mealRepository).save(any(Meal.class));
        assertEquals(5, testMeal.getRating());
    }

    @Test
    void rateMeal_WithInvalidRating_ThrowsValidationException() {
        // Act & Assert - validation happens before repository call
        assertThrows(ValidationException.class, () -> mealService.rateMeal(1, 1, 6));
        assertThrows(ValidationException.class, () -> mealService.rateMeal(1, 1, 0));
    }

    @Test
    void createMeal_WithValidData_CreatesMeal() {
        // Arrange
        CreateMealDto dto = new CreateMealDto("New Meal", "New Recipe", 1, null);
        when(nutritionRepository.findById(1)).thenReturn(Optional.of(testNutrition));
        when(mealRepository.save(any(Meal.class))).thenReturn(testMeal);
        when(nutritionFactRepository.findByNutrition_NutritionId(anyInt())).thenReturn(new ArrayList<>());

        // Act
        MealResponseDto result = mealService.createMeal(dto);

        // Assert
        assertNotNull(result);
        verify(mealRepository).save(any(Meal.class));
    }

    @Test
    void createMeal_WithInvalidNutritionId_ThrowsValidationException() {
        // Arrange
        CreateMealDto dto = new CreateMealDto("New Meal", "New Recipe", 999, null);
        when(nutritionRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ValidationException.class, () -> mealService.createMeal(dto));
    }

    @Test
    void updateMeal_WithValidData_UpdatesMeal() {
        // Arrange
        UpdateMealDto dto = new UpdateMealDto();
        dto.setMealName("Updated Meal");
        dto.setRating(5);

        when(mealRepository.findById(1)).thenReturn(Optional.of(testMeal));
        when(mealRepository.save(any(Meal.class))).thenReturn(testMeal);
        when(nutritionFactRepository.findByNutrition_NutritionId(anyInt())).thenReturn(new ArrayList<>());

        // Act
        MealResponseDto result = mealService.updateMeal(1, dto);

        // Assert
        assertNotNull(result);
        verify(mealRepository).save(any(Meal.class));
    }

    @Test
    void updateMeal_WithInvalidId_ThrowsResourceNotFoundException() {
        // Arrange
        UpdateMealDto dto = new UpdateMealDto();
        when(mealRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> mealService.updateMeal(999, dto));
    }

    @Test
    void deleteMeal_WithValidId_DeletesMeal() {
        // Arrange
        when(mealRepository.findById(1)).thenReturn(Optional.of(testMeal));
        when(mealAllergyRepository.findById_MealId(1)).thenReturn(new ArrayList<>());

        // Act
        mealService.deleteMeal(1);

        // Assert
        verify(mealRepository).delete(testMeal);
    }

    @Test
    void deleteMeal_WithInvalidId_ThrowsResourceNotFoundException() {
        // Arrange
        when(mealRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> mealService.deleteMeal(999));
    }
}
