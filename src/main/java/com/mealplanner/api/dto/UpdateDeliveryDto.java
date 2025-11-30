package com.mealplanner.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Size;
import java.time.LocalTime;

/**
 * DTO for updating delivery preferences.
 * Users can update delivery time and/or address while delivery is in PREPARING status.
 * At least one field should be provided.
 */
public class UpdateDeliveryDto {
    @JsonFormat(pattern = "HH:mm")
    private LocalTime deliveryTime;
    
    @Size(min = 5, max = 255, message = "Address must be between 5 and 255 characters")
    private String address;

    public UpdateDeliveryDto() {
    }

    public UpdateDeliveryDto(LocalTime deliveryTime, String address) {
        this.deliveryTime = deliveryTime;
        this.address = address;
    }

    // Getters and Setters
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
}
