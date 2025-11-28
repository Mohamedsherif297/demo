package com.mealplanner.api.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

// Imports for external entities (User, CustomPlan)
//import com.mealplanner.api.model.User;

@Entity
@Table(name = "subscription")
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer subscriptionId; // Maps to subscription_id

    // Association with User (user_schema)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "userId")
    private User user; // Maps to user_id

    // Association with CustomPlan (planmeal_schema)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "custom_plan_id", referencedColumnName = "customPlanId")
    private CustomPlan customPlan; // Maps to custom_plan_id

    private LocalDate startDate; // Maps to start_date
    private LocalDate planTime; // Maps to plan_time (Note: This likely means End Date or Plan Duration, but following SQL)
    private LocalTime preferredTime; // Maps to preferred_time (TIME)

    // Association with SubscriptionStatus (lookup)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id", referencedColumnName = "statusId")
    private SubscriptionStatus status; // Maps to status_id

    // Aggregation: One Subscription has many daily assigned meals
    @OneToMany(mappedBy = "subscription", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<SubscriptionMeal> subscriptionMeals;

    // Aggregation: One Subscription has many history records
    @OneToMany(mappedBy = "subscription", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<SubscriptionHistory> historyRecords;

    // ----------------------------------------------------
    // CONSTRUCTORS
    // ----------------------------------------------------
    public Subscription() {}

    public Subscription(User user, CustomPlan customPlan, LocalDate startDate, LocalTime preferredTime, SubscriptionStatus status) {
        this.user = user;
        this.customPlan = customPlan;
        this.startDate = startDate;
        this.preferredTime = preferredTime;
        this.status = status;
    }

    // ----------------------------------------------------
    // GETTERS AND SETTERS
    // ----------------------------------------------------
    public Integer getSubscriptionId() { return subscriptionId; }
    public void setSubscriptionId(Integer subscriptionId) { this.subscriptionId = subscriptionId; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public CustomPlan getCustomPlan() { return customPlan; }
    public void setCustomPlan(CustomPlan customPlan) { this.customPlan = customPlan; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getPlanTime() { return planTime; }
    public void setPlanTime(LocalDate planTime) { this.planTime = planTime; } // Assuming this should be LocalDate based on SQL

    public LocalTime getPreferredTime() { return preferredTime; }
    public void setPreferredTime(LocalTime preferredTime) { this.preferredTime = preferredTime; }

    public SubscriptionStatus getStatus() { return status; }
    public void setStatus(SubscriptionStatus status) { this.status = status; }

    public Set<SubscriptionMeal> getSubscriptionMeals() { return subscriptionMeals; }
    public void setSubscriptionMeals(Set<SubscriptionMeal> subscriptionMeals) { this.subscriptionMeals = subscriptionMeals; }

    public Set<SubscriptionHistory> getHistoryRecords() { return historyRecords; }
    public void setHistoryRecords(Set<SubscriptionHistory> historyRecords) { this.historyRecords = historyRecords; }
}