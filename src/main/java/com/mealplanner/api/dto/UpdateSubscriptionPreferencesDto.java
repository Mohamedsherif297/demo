package com.mealplanner.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;

public class UpdateSubscriptionPreferencesDto {
    @NotNull(message = "Preferred delivery time is required")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime preferredTime;

    public UpdateSubscriptionPreferencesDto() {
    }

    public UpdateSubscriptionPreferencesDto(LocalTime preferredTime) {
        this.preferredTime = preferredTime;
    }

    public LocalTime getPreferredTime() {
        return preferredTime;
    }

    public void setPreferredTime(LocalTime preferredTime) {
        this.preferredTime = preferredTime;
    }
}
