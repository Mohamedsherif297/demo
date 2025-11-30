package com.mealplanner.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class AdminDeliveryDto {
    private Integer deliveryId;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate deliveryDate;
    
    @JsonFormat(pattern = "HH:mm")
    private LocalTime deliveryTime;
    
    private String address;
    private String status;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime statusUpdatedAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime confirmedAt;
    
    private Integer userId;
    private String userEmail;
    private String userName;
    
    private Integer subscriptionId;
    private String subscriptionPlan;
    
    private List<MealSummaryDto> meals;
    private List<StatusHistoryDto> statusHistory;

    public AdminDeliveryDto() {
    }

    public AdminDeliveryDto(Integer deliveryId, LocalDate deliveryDate, LocalTime deliveryTime, 
                            String address, String status, LocalDateTime createdAt, 
                            LocalDateTime statusUpdatedAt, LocalDateTime confirmedAt, 
                            Integer userId, String userEmail, String userName, 
                            Integer subscriptionId, String subscriptionPlan, 
                            List<MealSummaryDto> meals, List<StatusHistoryDto> statusHistory) {
        this.deliveryId = deliveryId;
        this.deliveryDate = deliveryDate;
        this.deliveryTime = deliveryTime;
        this.address = address;
        this.status = status;
        this.createdAt = createdAt;
        this.statusUpdatedAt = statusUpdatedAt;
        this.confirmedAt = confirmedAt;
        this.userId = userId;
        this.userEmail = userEmail;
        this.userName = userName;
        this.subscriptionId = subscriptionId;
        this.subscriptionPlan = subscriptionPlan;
        this.meals = meals;
        this.statusHistory = statusHistory;
    }

    // Getters and Setters
    public Integer getDeliveryId() {
        return deliveryId;
    }

    public void setDeliveryId(Integer deliveryId) {
        this.deliveryId = deliveryId;
    }

    public LocalDate getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(LocalDate deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public LocalTime getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(LocalTime deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getStatusUpdatedAt() {
        return statusUpdatedAt;
    }

    public void setStatusUpdatedAt(LocalDateTime statusUpdatedAt) {
        this.statusUpdatedAt = statusUpdatedAt;
    }

    public LocalDateTime getConfirmedAt() {
        return confirmedAt;
    }

    public void setConfirmedAt(LocalDateTime confirmedAt) {
        this.confirmedAt = confirmedAt;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(Integer subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public String getSubscriptionPlan() {
        return subscriptionPlan;
    }

    public void setSubscriptionPlan(String subscriptionPlan) {
        this.subscriptionPlan = subscriptionPlan;
    }

    public List<MealSummaryDto> getMeals() {
        return meals;
    }

    public void setMeals(List<MealSummaryDto> meals) {
        this.meals = meals;
    }

    public List<StatusHistoryDto> getStatusHistory() {
        return statusHistory;
    }

    public void setStatusHistory(List<StatusHistoryDto> statusHistory) {
        this.statusHistory = statusHistory;
    }
}
