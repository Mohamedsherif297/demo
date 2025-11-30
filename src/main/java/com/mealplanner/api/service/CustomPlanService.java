package com.mealplanner.api.service;

import com.mealplanner.api.dto.*;
import com.mealplanner.api.exception.BusinessRuleException;
import com.mealplanner.api.exception.ForbiddenException;
import com.mealplanner.api.exception.ResourceNotFoundException;
import com.mealplanner.api.exception.ValidationException;
import com.mealplanner.api.model.*;
import com.mealplanner.api.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CustomPlanService {

    private final CustomPlanRepository customPlanRepository;
    private final PlanCategoryRepository planCategoryRepository;
    private final CustomPlanMealRepository customPlanMealRepository;
    private final MealRepository mealRepository;
    private final UserRepository userRepository;
    private final NutritionFactRepository nutritionFactRepository;

    public CustomPlanService(CustomPlanRepository customPlanRepository,
                            PlanCategoryRepository planCategoryRepository,
                            CustomPlanMealRepository customPlanMealRepository,
                            MealRepository mealRepository,
                            UserRepository userRepository,
                            NutritionFactRepository nutritionFactRepository) {
        this.customPlanRepository = customPlanRepository;
        this.planCategoryRepository = planCategoryRepository;
        this.customPlanMealRepository = customPlanMealRepository;
        this.mealRepository = mealRepository;
        this.userRepository = userRepository;
        this.nutritionFactRepository = nutritionFactRepository;
    }

    // ============================================
    // Task 4.1: Plan browsing and categories
    // ============================================

    /**
     * Get all plan categories
     * Requirements: 4.1
     */
    public List<PlanCategoryDto> getAllCategories() {
        return planCategoryRepository.findAll().stream()
                .map(this::mapToCategoryDto)
                .collect(Collectors.toList());
    }

    /**
     * Get plans with pagination and optional category filtering
     * Requirements: 4.2, 4.3
     */
    @SuppressWarnings("null")
    public Page<CustomPlanResponseDto> getPlans(Integer categoryId, Pageable pageable) {
        Page<CustomPlan> plans;
        
        if (categoryId != null) {
            plans = customPlanRepository.findByCategoryCategoryId(categoryId, pageable);
        } else {
            plans = customPlanRepository.findAll(pageable);
        }
        
        return plans.map(this::mapToResponseDto);
    }

    /**
     * Get plan details by ID including associated meals
     * Requirements: 4.4
     */
    @SuppressWarnings("null")
    public CustomPlanDetailDto getPlanById(Integer planId) {
        CustomPlan plan = customPlanRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("Custom plan not found with ID: " + planId));
        
        return mapToDetailDto(plan);
    }

    // ============================================
    // Task 4.4: User custom plan creation and management
    // ============================================

    /**
     * Create a custom plan associated with authenticated user
     * Requirements: 5.1
     */
    @SuppressWarnings("null")
    public CustomPlanResponseDto createPlan(Integer userId, CreateCustomPlanDto dto) {
        // Fetch user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
        
        // Fetch category
        PlanCategory category = planCategoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new ValidationException("Plan category not found with ID: " + dto.getCategoryId()));
        
        // Create plan
        CustomPlan plan = new CustomPlan();
        plan.setUser(user);
        plan.setCategory(category);
        plan.setDurationMinutes(dto.getDurationMinutes());
        plan.setPrice(dto.getPrice().doubleValue());
        
        plan = customPlanRepository.save(plan);
        
        // Add meals if provided
        if (dto.getMealIds() != null && !dto.getMealIds().isEmpty()) {
            addMealsToExistingPlan(plan, dto.getMealIds());
        }
        
        return mapToResponseDto(plan);
    }

    /**
     * Update a custom plan with ownership verification
     * Requirements: 5.1
     */
    @SuppressWarnings("null")
    public CustomPlanResponseDto updatePlan(Integer planId, Integer userId, UpdateCustomPlanDto dto) {
        CustomPlan plan = customPlanRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("Custom plan not found with ID: " + planId));
        
        // Verify ownership
        if (!plan.getUser().getUserId().equals(userId)) {
            throw new ForbiddenException("You do not have permission to update this plan");
        }
        
        // Update fields if provided
        if (dto.getCategoryId() != null) {
            PlanCategory category = planCategoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new ValidationException("Plan category not found with ID: " + dto.getCategoryId()));
            plan.setCategory(category);
        }
        
        if (dto.getDurationMinutes() != null) {
            plan.setDurationMinutes(dto.getDurationMinutes());
        }
        
        if (dto.getPrice() != null) {
            plan.setPrice(dto.getPrice().doubleValue());
        }
        
        plan = customPlanRepository.save(plan);
        return mapToResponseDto(plan);
    }

    /**
     * Delete a custom plan with ownership verification and cascade deletion
     * Requirements: 5.4
     */
    @SuppressWarnings("null")
    public void deletePlan(Integer planId, Integer userId, boolean isAdmin) {
        CustomPlan plan = customPlanRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("Custom plan not found with ID: " + planId));
        
        // Verify ownership (unless admin)
        if (!isAdmin && !plan.getUser().getUserId().equals(userId)) {
            throw new ForbiddenException("You do not have permission to delete this plan");
        }
        
        // Delete plan (cascade will handle meal associations)
        customPlanRepository.delete(plan);
    }

    // ============================================
    // Task 4.7: Meal-to-plan association management
    // ============================================

    /**
     * Add meals to a custom plan with ownership verification
     * Requirements: 5.2
     */
    @SuppressWarnings("null")
    public void addMealsToPlan(Integer planId, Integer userId, List<Integer> mealIds) {
        CustomPlan plan = customPlanRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("Custom plan not found with ID: " + planId));
        
        // Verify ownership
        if (!plan.getUser().getUserId().equals(userId)) {
            throw new ForbiddenException("You do not have permission to modify this plan");
        }
        
        addMealsToExistingPlan(plan, mealIds);
    }

    /**
     * Remove a meal from a custom plan with ownership verification
     * Requirements: 5.3
     */
    @SuppressWarnings("null")
    public void removeMealFromPlan(Integer planId, Integer userId, Integer mealId) {
        CustomPlan plan = customPlanRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("Custom plan not found with ID: " + planId));
        
        // Verify ownership
        if (!plan.getUser().getUserId().equals(userId)) {
            throw new ForbiddenException("You do not have permission to modify this plan");
        }
        
        // Check if meal exists
        if (!mealRepository.existsById(mealId)) {
            throw new ResourceNotFoundException("Meal not found with ID: " + mealId);
        }
        
        // Remove association
        CustomPlanMealId id = new CustomPlanMealId();
        id.setCustomPlanId(planId);
        id.setMealId(mealId);
        
        customPlanMealRepository.deleteById(id);
    }

    // ============================================
    // Task 4.10: Admin plan management
    // ============================================

    /**
     * Admin creates a custom plan with category, duration, and pricing
     * Requirements: 6.1, 6.3
     */
    @PreAuthorize("hasRole('ADMIN')")
    @SuppressWarnings("null")
    public CustomPlanResponseDto adminCreatePlan(Integer adminUserId, CreateCustomPlanDto dto) {
        // For admin plans, we associate with the admin user
        return createPlan(adminUserId, dto);
    }

    /**
     * Admin updates a custom plan preserving existing subscriptions
     * Requirements: 6.2, 6.3
     */
    @PreAuthorize("hasRole('ADMIN')")
    @SuppressWarnings("null")
    public CustomPlanResponseDto adminUpdatePlan(Integer planId, UpdateCustomPlanDto dto) {
        CustomPlan plan = customPlanRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("Custom plan not found with ID: " + planId));
        
        // Update fields if provided (no ownership check for admin)
        if (dto.getCategoryId() != null) {
            PlanCategory category = planCategoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new ValidationException("Plan category not found with ID: " + dto.getCategoryId()));
            plan.setCategory(category);
        }
        
        if (dto.getDurationMinutes() != null) {
            plan.setDurationMinutes(dto.getDurationMinutes());
        }
        
        if (dto.getPrice() != null) {
            plan.setPrice(dto.getPrice().doubleValue());
        }
        
        plan = customPlanRepository.save(plan);
        return mapToResponseDto(plan);
    }

    /**
     * Admin deletes a custom plan, preventing deletion if active subscriptions exist
     * Requirements: 6.3
     */
    @PreAuthorize("hasRole('ADMIN')")
    @SuppressWarnings("null")
    public void adminDeletePlan(Integer planId) {
        CustomPlan plan = customPlanRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("Custom plan not found with ID: " + planId));
        
        // Check for active subscriptions
        if (customPlanRepository.hasActiveSubscriptions(planId)) {
            throw new BusinessRuleException("Cannot delete plan with active subscriptions");
        }
        
        customPlanRepository.delete(plan);
    }

    // ============================================
    // Helper methods
    // ============================================

    @SuppressWarnings("null")
    private void addMealsToExistingPlan(CustomPlan plan, List<Integer> mealIds) {
        for (Integer mealId : mealIds) {
            Meal meal = mealRepository.findById(mealId)
                    .orElseThrow(() -> new ResourceNotFoundException("Meal not found with ID: " + mealId));
            
            CustomPlanMeal customPlanMeal = new CustomPlanMeal();
            customPlanMeal.setCustomPlan(plan);
            customPlanMeal.setMeal(meal);
            
            CustomPlanMealId id = new CustomPlanMealId();
            id.setCustomPlanId(plan.getCustomPlanId());
            id.setMealId(mealId);
            customPlanMeal.setId(id);
            
            customPlanMealRepository.save(customPlanMeal);
        }
    }

    private PlanCategoryDto mapToCategoryDto(PlanCategory category) {
        return new PlanCategoryDto(
                category.getCategoryId(),
                category.getCategoryName()
        );
    }

    private CustomPlanResponseDto mapToResponseDto(CustomPlan plan) {
        // Count meals
        int mealCount = customPlanMealRepository.findById_CustomPlanId(plan.getCustomPlanId()).size();
        
        return new CustomPlanResponseDto(
                plan.getCustomPlanId(),
                plan.getCategory() != null ? plan.getCategory().getCategoryName() : null,
                plan.getDurationMinutes(),
                plan.getPrice() != null ? BigDecimal.valueOf(plan.getPrice()) : null,
                mealCount
        );
    }

    private CustomPlanDetailDto mapToDetailDto(CustomPlan plan) {
        // Get meals
        List<CustomPlanMeal> planMeals = customPlanMealRepository.findById_CustomPlanId(plan.getCustomPlanId());
        List<MealResponseDto> meals = planMeals.stream()
                .map(cpm -> mapMealToResponseDto(cpm.getMeal()))
                .collect(Collectors.toList());
        
        return new CustomPlanDetailDto(
                plan.getCustomPlanId(),
                plan.getCategory() != null ? plan.getCategory().getCategoryName() : null,
                plan.getDurationMinutes(),
                plan.getPrice() != null ? BigDecimal.valueOf(plan.getPrice()) : null,
                meals.size(),
                meals
        );
    }

    private MealResponseDto mapMealToResponseDto(Meal meal) {
        String nutritionSummary = buildNutritionSummary(meal.getNutrition());
        
        return new MealResponseDto(
                meal.getMealId(),
                meal.getMealName(),
                meal.getRating(),
                nutritionSummary
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
}
