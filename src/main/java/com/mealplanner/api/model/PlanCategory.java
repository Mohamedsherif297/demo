package com.mealplanner.api.model;

import jakarta.persistence.*;

@Entity
@Table(name = "plan_category")
public class PlanCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer categoryId; // Maps to category_id

    private String categoryName; // Maps to category_name

    // ----------------------------------------------------
    // CONSTRUCTORS
    // ----------------------------------------------------
    public PlanCategory() {}

    public PlanCategory(String categoryName) {
        this.categoryName = categoryName;
    }

    // ----------------------------------------------------
    // GETTERS AND SETTERS
    // ----------------------------------------------------
    public Integer getCategoryId() { return categoryId; }
    public void setCategoryId(Integer categoryId) { this.categoryId = categoryId; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
}