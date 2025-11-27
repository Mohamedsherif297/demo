package com.mealplanner.api.model;

import jakarta.persistence.*;
import java.util.Set;

@Entity
@Table(name = "meal")
public class Meal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer mealId; // Maps to meal_id

    private String mealName; // Maps to meal_name
    private String recipeText; // Maps to recipe_text

    // Association with Nutrition
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nutrition_id", referencedColumnName = "nutritionId")
    private Nutrition nutrition;

    private Integer rating; // Maps to rating

    // Aggregation: Many-to-Many with Allergy (via MealAllergy join table)
    @OneToMany(mappedBy = "meal")
    private Set<MealAllergy> mealAllergies;
    
    // Aggregation: Many-to-Many with CustomPlan (via CustomPlanMeal join table)
    @OneToMany(mappedBy = "meal")
    private Set<CustomPlanMeal> customPlanMeals;

    // ----------------------------------------------------
    // CONSTRUCTORS
    // ----------------------------------------------------
    public Meal() {}

    public Meal(String mealName, String recipeText, Nutrition nutrition) {
        this.mealName = mealName;
        this.recipeText = recipeText;
        this.nutrition = nutrition;
    }

    // ----------------------------------------------------
    // GETTERS AND SETTERS
    // ----------------------------------------------------
    public Integer getMealId() { return mealId; }
    public void setMealId(Integer mealId) { this.mealId = mealId; }

    public String getMealName() { return mealName; }
    public void setMealName(String mealName) { this.mealName = mealName; }

    public String getRecipeText() { return recipeText; }
    public void setRecipeText(String recipeText) { this.recipeText = recipeText; }

    public Nutrition getNutrition() { return nutrition; }
    public void setNutrition(Nutrition nutrition) { this.nutrition = nutrition; }

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    public Set<MealAllergy> getMealAllergies() { return mealAllergies; }
    public void setMealAllergies(Set<MealAllergy> mealAllergies) { this.mealAllergies = mealAllergies; }

    public Set<CustomPlanMeal> getCustomPlanMeals() { return customPlanMeals; }
    public void setCustomPlanMeals(Set<CustomPlanMeal> customPlanMeals) { this.customPlanMeals = customPlanMeals; }
}