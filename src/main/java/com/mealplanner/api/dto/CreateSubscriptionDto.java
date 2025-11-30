package com.mealplanner.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

public class CreateSubscriptionDto {
    @NotNull(message = "Custom plan ID is required")
    private Integer customPlanId;

    @NotNull(message = "Start date is required")
    @FutureOrPresent(message = "Start date must be today or in the future")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime preferredTime;

    public CreateSubscriptionDto() {
    }

    public CreateSubscriptionDto(Integer customPlanId, LocalDate startDate, LocalTime preferredTime) {
        this.customPlanId = customPlanId;
        this.startDate = startDate;
        this.preferredTime = preferredTime;
    }

    // Getters and Setters
    public Integer getCustomPlanId() {
        return customPlanId;
    }

    public void setCustomPlanId(Integer customPlanId) {
        this.customPlanId = customPlanId;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalTime getPreferredTime() {
        return preferredTime;
    }

    public void setPreferredTime(LocalTime preferredTime) {
        this.preferredTime = preferredTime;
    }
}
