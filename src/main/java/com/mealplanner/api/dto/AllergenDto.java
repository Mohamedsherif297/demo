package com.mealplanner.api.dto;

public class AllergenDto {
    private Integer allergyId;
    private String allergyName;

    public AllergenDto() {
    }

    public AllergenDto(Integer allergyId, String allergyName) {
        this.allergyId = allergyId;
        this.allergyName = allergyName;
    }

    // Getters and Setters
    public Integer getAllergyId() {
        return allergyId;
    }

    public void setAllergyId(Integer allergyId) {
        this.allergyId = allergyId;
    }

    public String getAllergyName() {
        return allergyName;
    }

    public void setAllergyName(String allergyName) {
        this.allergyName = allergyName;
    }
}
