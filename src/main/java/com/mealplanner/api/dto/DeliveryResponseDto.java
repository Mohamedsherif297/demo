package com.mealplanner.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class DeliveryResponseDto {
    private Integer deliveryId;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate deliveryDate;
    
    @JsonFormat(pattern = "HH:mm")
    private LocalTime deliveryTime;
    
    private String address;
    private String status;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime statusUpdatedAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime confirmedAt;
    
    private List<MealSummaryDto> meals;

    public DeliveryResponseDto() {
    }

    public DeliveryResponseDto(Integer deliveryId, LocalDate deliveryDate, LocalTime deliveryTime, 
                               String address, String status, LocalDateTime statusUpdatedAt, 
                               LocalDateTime confirmedAt, List<MealSummaryDto> meals) {
        this.deliveryId = deliveryId;
        this.deliveryDate = deliveryDate;
        this.deliveryTime = deliveryTime;
        this.address = address;
        this.status = status;
        this.statusUpdatedAt = statusUpdatedAt;
        this.confirmedAt = confirmedAt;
        this.meals = meals;
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

    public List<MealSummaryDto> getMeals() {
        return meals;
    }

    public void setMeals(List<MealSummaryDto> meals) {
        this.meals = meals;
    }
}
