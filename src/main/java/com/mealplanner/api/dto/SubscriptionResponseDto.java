package com.mealplanner.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;

public class SubscriptionResponseDto {
    private Integer subscriptionId;
    private String planName;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    
    private String status;

    public SubscriptionResponseDto() {
    }

    public SubscriptionResponseDto(Integer subscriptionId, String planName, LocalDate startDate, String status) {
        this.subscriptionId = subscriptionId;
        this.planName = planName;
        this.startDate = startDate;
        this.status = status;
    }

    // Getters and Setters
    public Integer getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(Integer subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
