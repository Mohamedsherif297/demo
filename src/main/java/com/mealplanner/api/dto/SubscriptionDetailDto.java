package com.mealplanner.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.time.LocalTime;

public class SubscriptionDetailDto extends SubscriptionResponseDto {
    private CustomPlanResponseDto customPlan;
    
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime preferredTime;
    
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime planTime;

    public SubscriptionDetailDto() {
        super();
    }

    public SubscriptionDetailDto(Integer subscriptionId, String planName, LocalDate startDate, String status,
                                 CustomPlanResponseDto customPlan, LocalTime preferredTime, LocalTime planTime) {
        super(subscriptionId, planName, startDate, status);
        this.customPlan = customPlan;
        this.preferredTime = preferredTime;
        this.planTime = planTime;
    }

    // Getters and Setters
    public CustomPlanResponseDto getCustomPlan() {
        return customPlan;
    }

    public void setCustomPlan(CustomPlanResponseDto customPlan) {
        this.customPlan = customPlan;
    }

    public LocalTime getPreferredTime() {
        return preferredTime;
    }

    public void setPreferredTime(LocalTime preferredTime) {
        this.preferredTime = preferredTime;
    }

    public LocalTime getPlanTime() {
        return planTime;
    }

    public void setPlanTime(LocalTime planTime) {
        this.planTime = planTime;
    }
}
