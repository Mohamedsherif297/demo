package com.mealplanner.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

public class CreateCustomPlanDto {
    @NotNull(message = "Category ID is required")
    private Integer categoryId;

    @NotNull(message = "Duration is required")
    @Min(value = 1, message = "Duration must be at least 1 minute")
    private Integer durationMinutes;

    @NotNull(message = "Price is required")
    @Min(value = 0, message = "Price must be non-negative")
    private BigDecimal price;

    private List<Integer> mealIds;

    public CreateCustomPlanDto() {
    }

    public CreateCustomPlanDto(Integer categoryId, Integer durationMinutes, BigDecimal price, List<Integer> mealIds) {
        this.categoryId = categoryId;
        this.durationMinutes = durationMinutes;
        this.price = price;
        this.mealIds = mealIds;
    }

    // Getters and Setters
    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
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

    public List<Integer> getMealIds() {
        return mealIds;
    }

    public void setMealIds(List<Integer> mealIds) {
        this.mealIds = mealIds;
    }
}
