package com.mealplanner.api.dto;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public class AddMealsToPlanDto {
    @NotEmpty(message = "Meal IDs list cannot be empty")
    private List<Integer> mealIds;

    public AddMealsToPlanDto() {
    }

    public AddMealsToPlanDto(List<Integer> mealIds) {
        this.mealIds = mealIds;
    }

    // Getters and Setters
    public List<Integer> getMealIds() {
        return mealIds;
    }

    public void setMealIds(List<Integer> mealIds) {
        this.mealIds = mealIds;
    }
}
