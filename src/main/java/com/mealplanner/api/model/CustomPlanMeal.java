package com.mealplanner.api.model;

import jakarta.persistence.*;

@Entity
@Table(name = "custom_plan_meal")
public class CustomPlanMeal {

    @EmbeddedId
    private CustomPlanMealId id = new CustomPlanMealId();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "custom_plan_id", insertable = false, updatable = false)
    private CustomPlan customPlan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meal_id", insertable = false, updatable = false)
    private Meal meal;

    // ----------------------------------------------------
    // CONSTRUCTORS, GETTERS, SETTERS 
    // ----------------------------------------------------
    public CustomPlanMeal() {}

    public CustomPlanMeal(CustomPlan customPlan, Meal meal) {
        this.customPlan = customPlan;
        this.meal = meal;
        this.id.setCustomPlanId(customPlan.getCustomPlanId());
        this.id.setMealId(meal.getMealId());
    }

    public CustomPlanMealId getId() { return id; }
    public void setId(CustomPlanMealId id) { this.id = id; }
    public CustomPlan getCustomPlan() { return customPlan; }
    public void setCustomPlan(CustomPlan customPlan) { this.customPlan = customPlan; }
    public Meal getMeal() { return meal; }
    public void setMeal(Meal meal) { this.meal = meal; }
}