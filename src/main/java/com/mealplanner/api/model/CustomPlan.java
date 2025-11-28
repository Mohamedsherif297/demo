package com.mealplanner.api.model;

import jakarta.persistence.*;
import java.util.Set;
// Assuming User class is in the same 'model' package as we defined previously
//import com.mealplanner.api.model.User; 

@Entity
@Table(name = "custom_plan")
public class CustomPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer customPlanId; // Maps to custom_plan_id

    // Association with User
    // NOTE: This references the User entity we created in the user_schema models.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "userId", nullable = false)
    private User user; // Maps to user_id

    // Association with PlanCategory
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", referencedColumnName = "categoryId")
    private PlanCategory category; // Maps to category_id

    private Integer durationMinutes; // Maps to duration_minutes
    private Double price; // Maps to price (DECIMAL(10,2))

    // Aggregation: Many-to-Many with Meal (via CustomPlanMeal join table)
    @OneToMany(mappedBy = "customPlan")
    private Set<CustomPlanMeal> customPlanMeals;

    // ----------------------------------------------------
    // CONSTRUCTORS
    // ----------------------------------------------------
    public CustomPlan() {}

    public CustomPlan(User user, PlanCategory category, Integer durationMinutes, Double price) {
        this.user = user;
        this.category = category;
        this.durationMinutes = durationMinutes;
        this.price = price;
    }

    // ----------------------------------------------------
    // GETTERS AND SETTERS
    // ----------------------------------------------------
    public Integer getCustomPlanId() { return customPlanId; }
    public void setCustomPlanId(Integer customPlanId) { this.customPlanId = customPlanId; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public PlanCategory getCategory() { return category; }
    public void setCategory(PlanCategory category) { this.category = category; }

    public Integer getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public Set<CustomPlanMeal> getCustomPlanMeals() { return customPlanMeals; }
    public void setCustomPlanMeals(Set<CustomPlanMeal> customPlanMeals) { this.customPlanMeals = customPlanMeals; }
}