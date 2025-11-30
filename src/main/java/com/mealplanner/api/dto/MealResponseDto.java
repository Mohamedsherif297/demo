package com.mealplanner.api.dto;

public class MealResponseDto {
    private Integer mealId;
    private String mealName;
    private Integer rating;
    private String nutritionSummary;

    public MealResponseDto() {
    }

    public MealResponseDto(Integer mealId, String mealName, Integer rating, String nutritionSummary) {
        this.mealId = mealId;
        this.mealName = mealName;
        this.rating = rating;
        this.nutritionSummary = nutritionSummary;
    }

    // Getters and Setters
    public Integer getMealId() {
        return mealId;
    }

    public void setMealId(Integer mealId) {
        this.mealId = mealId;
    }

    public String getMealName() {
        return mealName;
    }

    public void setMealName(String mealName) {
        this.mealName = mealName;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getNutritionSummary() {
        return nutritionSummary;
    }

    public void setNutritionSummary(String nutritionSummary) {
        this.nutritionSummary = nutritionSummary;
    }
}
