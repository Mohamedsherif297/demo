package com.mealplanner.api.dto;

import java.math.BigDecimal;
import java.util.List;

public class CustomPlanDetailDto extends CustomPlanResponseDto {
    private List<MealResponseDto> meals;

    public CustomPlanDetailDto() {
        super();
    }

    public CustomPlanDetailDto(Integer customPlanId, String categoryName, Integer durationMinutes,
                               BigDecimal price, Integer mealCount, List<MealResponseDto> meals) {
        super(customPlanId, categoryName, durationMinutes, price, mealCount);
        this.meals = meals;
    }

    // Getters and Setters
    public List<MealResponseDto> getMeals() {
        return meals;
    }

    public void setMeals(List<MealResponseDto> meals) {
        this.meals = meals;
    }
}
