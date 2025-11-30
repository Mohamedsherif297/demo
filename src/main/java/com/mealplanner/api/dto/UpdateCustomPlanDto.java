package com.mealplanner.api.dto;

import jakarta.validation.constraints.Min;
import java.math.BigDecimal;

public class UpdateCustomPlanDto {
    private Integer categoryId;

    @Min(value = 1, message = "Duration must be at least 1 minute")
    private Integer durationMinutes;

    @Min(value = 0, message = "Price must be non-negative")
    private BigDecimal price;

    public UpdateCustomPlanDto() {
    }

    public UpdateCustomPlanDto(Integer categoryId, Integer durationMinutes, BigDecimal price) {
        this.categoryId = categoryId;
        this.durationMinutes = durationMinutes;
        this.price = price;
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
}
