package com.mealplanner.api.model;

import jakarta.persistence.*; 

@Entity
@Table(name = "meal_allergy")
public class MealAllergy {

    @EmbeddedId
    private MealAllergyId id = new MealAllergyId();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meal_id", insertable = false, updatable = false)
    private Meal meal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "allergy_id", insertable = false, updatable = false)
    private Allergy allergy;

    // ----------------------------------------------------
    // CONSTRUCTORS, GETTERS, SETTERS
    // ----------------------------------------------------
    public MealAllergy() {}

    public MealAllergy(Meal meal, Allergy allergy) {
        this.meal = meal;
        this.allergy = allergy;
        this.id.setMealId(meal.getMealId());
        this.id.setAllergyId(allergy.getAllergyId());
    }

    public MealAllergyId getId() { return id; }
    public void setId(MealAllergyId id) { this.id = id; }
    public Meal getMeal() { return meal; }
    public void setMeal(Meal meal) { this.meal = meal; }
    public Allergy getAllergy() { return allergy; }
    public void setAllergy(Allergy allergy) { this.allergy = allergy; }
}