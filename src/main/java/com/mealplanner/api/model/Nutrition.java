package com.mealplanner.api.model;

import jakarta.persistence.*;
import java.util.Set;

@Entity
@Table(name = "nutrition")
public class Nutrition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer nutritionId; // Maps to nutrition_id

    // Composition: Nutrition owns NutritionFacts
    @OneToMany(mappedBy = "nutrition", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<NutritionFact> facts;

    // ----------------------------------------------------
    // CONSTRUCTORS
    // ----------------------------------------------------
    public Nutrition() {}

    // ----------------------------------------------------
    // GETTERS AND SETTERS
    // ----------------------------------------------------
    public Integer getNutritionId() { return nutritionId; }
    public void setNutritionId(Integer nutritionId) { this.nutritionId = nutritionId; }

    public Set<NutritionFact> getFacts() { return facts; }
    public void setFacts(Set<NutritionFact> facts) { this.facts = facts; }
}