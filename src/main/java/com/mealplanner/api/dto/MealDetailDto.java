package com.mealplanner.api.dto;

import java.util.List;

public class MealDetailDto extends MealResponseDto {
    private String recipeText;
    private NutritionDto nutrition;
    private List<AllergenDto> allergens;

    public MealDetailDto() {
        super();
    }

    public MealDetailDto(Integer mealId, String mealName, Integer rating, String nutritionSummary,
                         String recipeText, NutritionDto nutrition, List<AllergenDto> allergens) {
        super(mealId, mealName, rating, nutritionSummary);
        this.recipeText = recipeText;
        this.nutrition = nutrition;
        this.allergens = allergens;
    }

    // Getters and Setters
    public String getRecipeText() {
        return recipeText;
    }

    public void setRecipeText(String recipeText) {
        this.recipeText = recipeText;
    }

    public NutritionDto getNutrition() {
        return nutrition;
    }

    public void setNutrition(NutritionDto nutrition) {
        this.nutrition = nutrition;
    }

    public List<AllergenDto> getAllergens() {
        return allergens;
    }

    public void setAllergens(List<AllergenDto> allergens) {
        this.allergens = allergens;
    }
}
