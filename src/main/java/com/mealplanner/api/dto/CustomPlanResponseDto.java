package com.mealplanner.api.dto;

import java.math.BigDecimal;

public class CustomPlanResponseDto {
    private Integer customPlanId;
    private String categoryName;
    private Integer durationMinutes;
    private BigDecimal price;
    private Integer mealCount;

    public CustomPlanResponseDto() {
    }

    public CustomPlanResponseDto(Integer customPlanId, String categoryName, Integer durationMinutes, 
                                 BigDecimal price, Integer mealCount) {
        this.customPlanId = customPlanId;
        this.categoryName = categoryName;
        this.durationMinutes = durationMinutes;
        this.price = price;
        this.mealCount = mealCount;
    }

    // Getters and Setters
    public Integer getCustomPlanId() {
        return customPlanId;
    }

    public void setCustomPlanId(Integer customPlanId) {
        this.customPlanId = customPlanId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getMealCount() {
        return mealCount;
    }

    public void setMealCount(Integer mealCount) {
        this.mealCount = mealCount;
    }
}
