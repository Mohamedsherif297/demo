package com.mealplanner.api.model;

import jakarta.persistence.*;
import java.time.LocalTime;

@Entity
@Table(name = "delivery")
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer deliveryId; // Maps to delivery_id

    // Association (One-to-One): Links back to the SubscriptionMeal
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_meal_id", referencedColumnName = "subscriptionMealId")
    private SubscriptionMeal subscriptionMeal; // Maps to subscription_meal_id

    private String address; // Maps to address
    private LocalTime deliveryTime; // Maps to delivery_time

    // Association with DeliveryStatus (lookup)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id", referencedColumnName = "statusId")
    private DeliveryStatus status; // Maps to status_id

    // ----------------------------------------------------
    // CONSTRUCTORS
    // ----------------------------------------------------
    public Delivery() {}

    public Delivery(SubscriptionMeal subscriptionMeal, String address, LocalTime deliveryTime, DeliveryStatus status) {
        this.subscriptionMeal = subscriptionMeal;
        this.address = address;
        this.deliveryTime = deliveryTime;
        this.status = status;
    }

    // ----------------------------------------------------
    // GETTERS AND SETTERS
    // ----------------------------------------------------
    public Integer getDeliveryId() { return deliveryId; }
    public void setDeliveryId(Integer deliveryId) { this.deliveryId = deliveryId; }

    public SubscriptionMeal getSubscriptionMeal() { return subscriptionMeal; }
    public void setSubscriptionMeal(SubscriptionMeal subscriptionMeal) { this.subscriptionMeal = subscriptionMeal; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public LocalTime getDeliveryTime() { return deliveryTime; }
    public void setDeliveryTime(LocalTime deliveryTime) { this.deliveryTime = deliveryTime; }

    public DeliveryStatus getStatus() { return status; }
    public void setStatus(DeliveryStatus status) { this.status = status; }
}