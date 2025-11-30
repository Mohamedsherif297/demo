package com.mealplanner.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * DTO for admin delivery status updates.
 * Used by administrators to manually update delivery status.
 */
public class UpdateDeliveryStatusDto {
    @NotBlank(message = "Status is required")
    @Pattern(regexp = "^(PREPARING|SHIPPED|DELIVERED|CONFIRMED)$", 
             message = "Status must be one of: PREPARING, SHIPPED, DELIVERED, CONFIRMED")
    private String status;

    public UpdateDeliveryStatusDto() {
    }

    public UpdateDeliveryStatusDto(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
