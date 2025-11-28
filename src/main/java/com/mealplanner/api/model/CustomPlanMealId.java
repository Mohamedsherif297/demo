package com.mealplanner.api.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable 
public class CustomPlanMealId implements Serializable {

    @Column(name = "custom_plan_id")
    private Integer customPlanId;
    @Column(name = "meal_id")
    private Integer mealId;

    // ----------------------------------------------------
    // CONSTRUCTORS, GETTERS, SETTERS 
    // ----------------------------------------------------
    public CustomPlanMealId() {}
    public CustomPlanMealId(Integer customPlanId, Integer mealId) {
        this.customPlanId = customPlanId;
        this.mealId = mealId;
    }
    
    public Integer getCustomPlanId() { return customPlanId; }
    public void setCustomPlanId(Integer customPlanId) { this.customPlanId = customPlanId; }
    public Integer getMealId() { return mealId; }
    public void setMealId(Integer mealId) { this.mealId = mealId; }
    
    // equals() and hashCode() mandatory implementation
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomPlanMealId that = (CustomPlanMealId) o;
        return Objects.equals(customPlanId, that.customPlanId) && Objects.equals(mealId, that.mealId);
    }
    @Override
    public int hashCode() { return Objects.hash(customPlanId, mealId); }
}