package com.mealplanner.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;

public class SubscriptionMealDto {
    private Integer mealId;
    private String mealName;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate scheduledDate;

    public SubscriptionMealDto() {
    }

    public SubscriptionMealDto(Integer mealId, String mealName, LocalDate scheduledDate) {
        this.mealId = mealId;
        this.mealName = mealName;
        this.scheduledDate = scheduledDate;
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

    public LocalDate getScheduledDate() {
        return scheduledDate;
    }

    public void setScheduledDate(LocalDate scheduledDate) {
        this.scheduledDate = scheduledDate;
    }
}
