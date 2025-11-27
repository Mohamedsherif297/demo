package com.mealplanner.api.model;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable 
public class MealAllergyId implements Serializable {

    private Integer mealId;
    private Integer allergyId;

    // ----------------------------------------------------
    // CONSTRUCTORS, GETTERS, SETTERS (Similar structure to UserAllergyId)
    // ----------------------------------------------------
    public MealAllergyId() {}
    public MealAllergyId(Integer mealId, Integer allergyId) {
        this.mealId = mealId;
        this.allergyId = allergyId;
    }

    public Integer getMealId() { return mealId; }
    public void setMealId(Integer mealId) { this.mealId = mealId; }
    public Integer getAllergyId() { return allergyId; }
    public void setAllergyId(Integer allergyId) { this.allergyId = allergyId; }

    // equals() and hashCode() mandatory implementation
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MealAllergyId that = (MealAllergyId) o;
        return Objects.equals(mealId, that.mealId) && Objects.equals(allergyId, that.allergyId);
    }
    @Override
    public int hashCode() { return Objects.hash(mealId, allergyId); }
}