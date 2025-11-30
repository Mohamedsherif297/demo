package com.mealplanner.api.dto;

public class NutritionDto {
    private Integer nutritionId;
    private Integer calories;
    private Integer protein;
    private Integer carbs;
    private Integer fats;

    public NutritionDto() {
    }

    public NutritionDto(Integer nutritionId, Integer calories, Integer protein, Integer carbs, Integer fats) {
        this.nutritionId = nutritionId;
        this.calories = calories;
        this.protein = protein;
        this.carbs = carbs;
        this.fats = fats;
    }

    // Getters and Setters
    public Integer getNutritionId() {
        return nutritionId;
    }

    public void setNutritionId(Integer nutritionId) {
        this.nutritionId = nutritionId;
    }

    public Integer getCalories() {
        return calories;
    }

    public void setCalories(Integer calories) {
        this.calories = calories;
    }

    public Integer getProtein() {
        return protein;
    }

    public void setProtein(Integer protein) {
        this.protein = protein;
    }

    public Integer getCarbs() {
        return carbs;
    }

    public void setCarbs(Integer carbs) {
        this.carbs = carbs;
    }

    public Integer getFats() {
        return fats;
    }

    public void setFats(Integer fats) {
        this.fats = fats;
    }
}
