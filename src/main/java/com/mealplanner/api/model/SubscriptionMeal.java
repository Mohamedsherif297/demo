package com.mealplanner.api.model;

import jakarta.persistence.*;
import java.time.LocalDate; 

@Entity
@Table(name = "subscription_meal")
public class SubscriptionMeal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer subscriptionMealId; // Maps to subscription_meal_id

    // Association with Subscription
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_id", referencedColumnName = "subscriptionId")
    private Subscription subscription; // Maps to subscription_id

    // Association with Meal (planmeal_schema)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meal_id", referencedColumnName = "mealId")
    private Meal meal; // Maps to meal_id

    private LocalDate deliveryDate; // Maps to delivery_date

    // Aggregation: One SubscriptionMeal may have one delivery record
    @OneToOne(mappedBy = "subscriptionMeal", cascade = CascadeType.ALL, orphanRemoval = true)
    private Delivery delivery;

    // ----------------------------------------------------
    // CONSTRUCTORS
    // ----------------------------------------------------
    public SubscriptionMeal() {}

    public SubscriptionMeal(Subscription subscription, Meal meal, LocalDate deliveryDate) {
        this.subscription = subscription;
        this.meal = meal;
        this.deliveryDate = deliveryDate;
    }

    // ----------------------------------------------------
    // GETTERS AND SETTERS
    // ----------------------------------------------------
    public Integer getSubscriptionMealId() { return subscriptionMealId; }
    public void setSubscriptionMealId(Integer subscriptionMealId) { this.subscriptionMealId = subscriptionMealId; }

    public Subscription getSubscription() { return subscription; }
    public void setSubscription(Subscription subscription) { this.subscription = subscription; }

    public Meal getMeal() { return meal; }
    public void setMeal(Meal meal) { this.meal = meal; }

    public LocalDate getDeliveryDate() { return deliveryDate; }
    public void setDeliveryDate(LocalDate deliveryDate) { this.deliveryDate = deliveryDate; }

    public Delivery getDelivery() { return delivery; }
    public void setDelivery(Delivery delivery) { this.delivery = delivery; }
}