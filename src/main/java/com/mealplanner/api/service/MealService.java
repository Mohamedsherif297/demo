package com.mealplanner.api.service;

import com.mealplanner.api.dto.*;
import com.mealplanner.api.exception.ResourceNotFoundException;
import com.mealplanner.api.exception.ValidationException;
import com.mealplanner.api.model.*;
import com.mealplanner.api.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class MealService {

    private final MealRepository mealRepository;
    private final NutritionRepository nutritionRepository;
    private final NutritionFactRepository nutritionFactRepository;
    private final AllergyRepository allergyRepository;
    private final MealAllergyRepository mealAllergyRepository;

    public MealService(MealRepository mealRepository,
                      NutritionRepository nutritionRepository,
                      NutritionFactRepository nutritionFactRepository,
                      AllergyRepository allergyRepository,
                      MealAllergyRepository mealAllergyRepository) {
        this.mealRepository = mealRepository;
        this.nutritionRepository = nutritionRepository;
        this.nutritionFactRepository = nutritionFactRepository;
        this.allergyRepository = allergyRepository;
        this.mealAllergyRepository = mealAllergyRepository;
    }

    /**
     * Get meals with pagination, name search, rating filter, and allergen exclusion
     */
    @Transactional(readOnly = true)
    @SuppressWarnings("null")
    public Page<MealResponseDto> getMeals(String search, Integer minRating, List<Integer> excludeAllergens, Pageable pageable) {
        Page<Meal> meals;

        // Apply filters based on provided criteria
        if (excludeAllergens != null && !excludeAllergens.isEmpty()) {
            meals = mealRepository.findMealsExcludingAllergens(excludeAllergens, pageable);
        } else if (search != null && !search.trim().isEmpty()) {
            meals = mealRepository.findByMealNameContainingIgnoreCase(search, pageable);
        } else if (minRating != null) {
            meals = mealRepository.findByRatingGreaterThanEqual(minRating, pageable);
        } else {
            meals = mealRepository.findAll(pageable);
        }

        // Apply additional filters if needed (when multiple filters are provided)
        List<Meal> filteredMeals = meals.getContent();
        
        if (search != null && !search.trim().isEmpty() && (minRating != null || (excludeAllergens != null && !excludeAllergens.isEmpty()))) {
            filteredMeals = filteredMeals.stream()
                    .filter(meal -> meal.getMealName().toLowerCase().contains(search.toLowerCase()))
                    .collect(Collectors.toList());
        }
        
        if (minRating != null && (excludeAllergens != null && !excludeAllergens.isEmpty())) {
            filteredMeals = filteredMeals.stream()
                    .filter(meal -> meal.getRating() != null && meal.getRating() >= minRating)
                    .collect(Collectors.toList());
        }

        List<MealResponseDto> dtos = filteredMeals.stream()
                .map(this::mapToMealResponseDto)
                .collect(Collectors.toList());

        return new PageImpl<>(dtos, pageable, meals.getTotalElements());
    }

    /**
     * Get meal by ID with complete details
     */
    @Transactional(readOnly = true)
    @SuppressWarnings("null")
    public MealDetailDto getMealById(Integer mealId) {
        Meal meal = mealRepository.findById(mealId)
                .orElseThrow(() -> new ResourceNotFoundException("Meal not found with id: " + mealId));

        return mapToMealDetailDto(meal);
    }

    /**
     * Rate a meal
     */
    @SuppressWarnings("null")
    public void rateMeal(Integer mealId, Integer userId, Integer rating) {
        // Validate rating range
        if (rating < 1 || rating > 5) {
            throw new ValidationException("Rating must be between 1 and 5");
        }

        Meal meal = mealRepository.findById(mealId)
                .orElseThrow(() -> new ResourceNotFoundException("Meal not found with id: " + mealId));

        // Update or create rating (simplified - just updating the meal's rating field)
        meal.setRating(rating);
        mealRepository.save(meal);
    }

    /**
     * Create a new meal (admin only)
     * Requirements: 3.1, 3.4
     */
    @PreAuthorize("hasRole('ADMIN')")
    @SuppressWarnings("null")
    public MealResponseDto createMeal(CreateMealDto dto) {
        // Validate nutrition exists
        Nutrition nutrition = nutritionRepository.findById(dto.getNutritionId())
                .orElseThrow(() -> new ValidationException("Nutrition not found with id: " + dto.getNutritionId()));

        // Create meal
        Meal meal = new Meal();
        meal.setMealName(dto.getMealName());
        meal.setRecipeText(dto.getRecipeText());
        meal.setNutrition(nutrition);

        meal = mealRepository.save(meal);

        // Add allergen associations if provided
        if (dto.getAllergenIds() != null && !dto.getAllergenIds().isEmpty()) {
            for (Integer allergenId : dto.getAllergenIds()) {
                Allergy allergy = allergyRepository.findById(allergenId)
                        .orElseThrow(() -> new ValidationException("Allergen not found with id: " + allergenId));
                
                MealAllergy mealAllergy = new MealAllergy(meal, allergy);
                mealAllergyRepository.save(mealAllergy);
            }
        }

        return mapToMealResponseDto(meal);
    }

    /**
     * Update an existing meal (admin only)
     * Requirements: 3.2, 3.4
     */
    @PreAuthorize("hasRole('ADMIN')")
    @SuppressWarnings("null")
    public MealResponseDto updateMeal(Integer mealId, UpdateMealDto dto) {
        Meal meal = mealRepository.findById(mealId)
                .orElseThrow(() -> new ResourceNotFoundException("Meal not found with id: " + mealId));

        // Update fields if provided
        if (dto.getMealName() != null) {
            meal.setMealName(dto.getMealName());
        }
        if (dto.getRecipeText() != null) {
            meal.setRecipeText(dto.getRecipeText());
        }
        if (dto.getRating() != null) {
            meal.setRating(dto.getRating());
        }
        if (dto.getNutritionId() != null) {
            Nutrition nutrition = nutritionRepository.findById(dto.getNutritionId())
                    .orElseThrow(() -> new ValidationException("Nutrition not found with id: " + dto.getNutritionId()));
            meal.setNutrition(nutrition);
        }

        // Update allergen associations if provided
        if (dto.getAllergenIds() != null) {
            // Remove existing allergen associations
            List<MealAllergy> existingAllergies = mealAllergyRepository.findById_MealId(mealId);
            mealAllergyRepository.deleteAll(existingAllergies);

            // Add new allergen associations
            for (Integer allergenId : dto.getAllergenIds()) {
                Allergy allergy = allergyRepository.findById(allergenId)
                        .orElseThrow(() -> new ValidationException("Allergen not found with id: " + allergenId));
                
                MealAllergy mealAllergy = new MealAllergy(meal, allergy);
                mealAllergyRepository.save(mealAllergy);
            }
        }

        meal = mealRepository.save(meal);
        return mapToMealResponseDto(meal);
    }

    /**
     * Delete a meal (admin only)
     * Requirements: 3.3, 3.4
     */
    @PreAuthorize("hasRole('ADMIN')")
    @SuppressWarnings("null")
    public void deleteMeal(Integer mealId) {
        Meal meal = mealRepository.findById(mealId)
                .orElseThrow(() -> new ResourceNotFoundException("Meal not found with id: " + mealId));

        // Delete allergen associations first
        List<MealAllergy> mealAllergies = mealAllergyRepository.findById_MealId(mealId);
        mealAllergyRepository.deleteAll(mealAllergies);

        // Delete the meal
        mealRepository.delete(meal);
    }

    // ========== Helper Methods ==========

    private MealResponseDto mapToMealResponseDto(Meal meal) {
        String nutritionSummary = buildNutritionSummary(meal.getNutrition());
        return new MealResponseDto(
                meal.getMealId(),
                meal.getMealName(),
                meal.getRating(),
                nutritionSummary
        );
    }

    private MealDetailDto mapToMealDetailDto(Meal meal) {
        String nutritionSummary = buildNutritionSummary(meal.getNutrition());
        NutritionDto nutritionDto = mapToNutritionDto(meal.getNutrition());
        List<AllergenDto> allergenDtos = mapToAllergenDtos(meal.getMealId());

        return new MealDetailDto(
                meal.getMealId(),
                meal.getMealName(),
                meal.getRating(),
                nutritionSummary,
                meal.getRecipeText(),
                nutritionDto,
                allergenDtos
        );
    }

    private String buildNutritionSummary(Nutrition nutrition) {
        if (nutrition == null) {
            return "No nutrition information available";
        }

        List<NutritionFact> facts = nutritionFactRepository.findByNutrition_NutritionId(nutrition.getNutritionId());
        if (facts.isEmpty()) {
            return "No nutrition information available";
        }

        // Build a simple summary from nutrition facts
        StringBuilder summary = new StringBuilder();
        for (NutritionFact fact : facts) {
            if (summary.length() > 0) {
                summary.append(", ");
            }
            summary.append(fact.getFactName())
                   .append(": ")
                   .append(fact.getFactValue())
                   .append(fact.getUnit());
        }

        return summary.toString();
    }

    private NutritionDto mapToNutritionDto(Nutrition nutrition) {
        if (nutrition == null) {
            return null;
        }

        List<NutritionFact> facts = nutritionFactRepository.findByNutrition_NutritionId(nutrition.getNutritionId());
        
        // Extract specific nutrition values
        Integer calories = extractNutritionValue(facts, "calories");
        Integer protein = extractNutritionValue(facts, "protein");
        Integer carbs = extractNutritionValue(facts, "carbs");
        Integer fats = extractNutritionValue(facts, "fats");

        return new NutritionDto(nutrition.getNutritionId(), calories, protein, carbs, fats);
    }

    private Integer extractNutritionValue(List<NutritionFact> facts, String factName) {
        return facts.stream()
                .filter(fact -> fact.getFactName().equalsIgnoreCase(factName))
                .findFirst()
                .map(fact -> fact.getFactValue().intValue())
                .orElse(null);
    }

    private List<AllergenDto> mapToAllergenDtos(Integer mealId) {
        List<MealAllergy> mealAllergies = mealAllergyRepository.findById_MealId(mealId);
        return mealAllergies.stream()
                .map(ma -> new AllergenDto(ma.getAllergy().getAllergyId(), ma.getAllergy().getAllergyName()))
                .collect(Collectors.toList());
    }
}
