package com.mealplanner.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.time.LocalTime;

public class DeliveryHistoryDto {
    private Integer deliveryId;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate deliveryDate;
    
    private String status;
    
    @JsonFormat(pattern = "HH:mm")
    private LocalTime deliveryTime;
    
    private boolean confirmed;
    private int mealCount;

    public DeliveryHistoryDto() {
    }

    public DeliveryHistoryDto(Integer deliveryId, LocalDate deliveryDate, String status, 
                              LocalTime deliveryTime, boolean confirmed, int mealCount) {
        this.deliveryId = deliveryId;
        this.deliveryDate = deliveryDate;
        this.status = status;
        this.deliveryTime = deliveryTime;
        this.confirmed = confirmed;
        this.mealCount = mealCount;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalTime getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(LocalTime deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }

    public int getMealCount() {
        return mealCount;
    }

    public void setMealCount(int mealCount) {
        this.mealCount = mealCount;
    }
}
