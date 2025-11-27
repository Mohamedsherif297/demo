package com.mealplanner.api.model;

import jakarta.persistence.*;

@Entity
@Table(name = "nutrition_facts")
public class NutritionFact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer factId; // Maps to fact_id

    // Association with Nutrition (parent)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nutrition_id", referencedColumnName = "nutritionId")
    private Nutrition nutrition;

    private String factName; // Maps to fact_name
    private Double factValue; // Maps to fact_value (DECIMAL(10,2))
    private String unit; // Maps to unit

    // ----------------------------------------------------
    // CONSTRUCTORS
    // ----------------------------------------------------
    public NutritionFact() {}

    public NutritionFact(Nutrition nutrition, String factName, Double factValue, String unit) {
        this.nutrition = nutrition;
        this.factName = factName;
        this.factValue = factValue;
        this.unit = unit;
    }

    // ----------------------------------------------------
    // GETTERS AND SETTERS
    // ----------------------------------------------------
    public Integer getFactId() { return factId; }
    public void setFactId(Integer factId) { this.factId = factId; }

    public Nutrition getNutrition() { return nutrition; }
    public void setNutrition(Nutrition nutrition) { this.nutrition = nutrition; }

    public String getFactName() { return factName; }
    public void setFactName(String factName) { this.factName = factName; }

    public Double getFactValue() { return factValue; }
    public void setFactValue(Double factValue) { this.factValue = factValue; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
}