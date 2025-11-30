package com.mealplanner.api.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import java.util.List;

public class UpdateMealDto {
    @Size(min = 1, max = 255, message = "Meal name must be between 1 and 255 characters")
    private String mealName;
    
    @Size(min = 1, max = 5000, message = "Recipe text must be between 1 and 5000 characters")
    private String recipeText;
    
    private Integer nutritionId;
    
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be at most 5")
    private Integer rating;
    
    private List<Integer> allergenIds;

    public UpdateMealDto() {
    }

    public UpdateMealDto(String mealName, String recipeText, Integer nutritionId, Integer rating, List<Integer> allergenIds) {
        this.mealName = mealName;
        this.recipeText = recipeText;
        this.nutritionId = nutritionId;
        this.rating = rating;
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

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public List<Integer> getAllergenIds() {
        return allergenIds;
    }

    public void setAllergenIds(List<Integer> allergenIds) {
        this.allergenIds = allergenIds;
    }
}
