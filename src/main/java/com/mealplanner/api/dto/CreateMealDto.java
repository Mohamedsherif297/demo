package com.mealplanner.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public class CreateMealDto {
    @NotBlank(message = "Meal name is required")
    @Size(min = 1, max = 255, message = "Meal name must be between 1 and 255 characters")
    private String mealName;

    @NotBlank(message = "Recipe text is required")
    @Size(min = 1, max = 5000, message = "Recipe text must be between 1 and 5000 characters")
    private String recipeText;

    @NotNull(message = "Nutrition ID is required")
    private Integer nutritionId;

    private List<Integer> allergenIds;

    public CreateMealDto() {
    }

    public CreateMealDto(String mealName, String recipeText, Integer nutritionId, List<Integer> allergenIds) {
        this.mealName = mealName;
        this.recipeText = recipeText;
        this.nutritionId = nutritionId;
        this.allergenIds = allergenIds;
    }

    // Getters and Setters
    public String getMealName() {
        return mealName;
    }

    public void setMealName(String mealName) {
        this.mealName = mealName;
    }

    public String getRecipeText() {
        return recipeText;
    }

    public void setRecipeText(String recipeText) {
        this.recipeText = recipeText;
    }

    public Integer getNutritionId() {
        return nutritionId;
    }

    public void setNutritionId(Integer nutritionId) {
        this.nutritionId = nutritionId;
    }

    public List<Integer> getAllergenIds() {
        return allergenIds;
    }

    public void setAllergenIds(List<Integer> allergenIds) {
        this.allergenIds = allergenIds;
    }
}
