package com.mealplanner.api.dto;

public class MealSummaryDto {
    private Integer mealId;
    private String mealName;
    private String mealType;

    public MealSummaryDto() {
    }

    public MealSummaryDto(Integer mealId, String mealName, String mealType) {
        this.mealId = mealId;
        this.mealName = mealName;
        this.mealType = mealType;
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

    public String getMealType() {
        return mealType;
    }

    public void setMealType(String mealType) {
        this.mealType = mealType;
    }
}
