package com.mealplanner.api.model;

import jakarta.persistence.*;
import java.time.LocalTime;
import java.time.LocalDateTime;

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

    // NEW FIELDS for delivery tracking
    @Column(name = "created_at")
    private LocalDateTime createdAt; // Maps to created_at

    @Column(name = "status_updated_at")
    private LocalDateTime statusUpdatedAt; // Maps to status_updated_at

    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt; // Maps to confirmed_at

    @Column(name = "estimated_delivery_time")
    private LocalTime estimatedDeliveryTime; // Maps to estimated_delivery_time

    // ----------------------------------------------------
    // CONSTRUCTORS
    // ----------------------------------------------------
    public Delivery() {}

    public Delivery(SubscriptionMeal subscriptionMeal, String address, LocalTime deliveryTime, DeliveryStatus status) {
        this.subscriptionMeal = subscriptionMeal;
        this.address = address;
        this.deliveryTime = deliveryTime;
        this.status = status;
        this.createdAt = LocalDateTime.now();
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

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getStatusUpdatedAt() { return statusUpdatedAt; }
    public void setStatusUpdatedAt(LocalDateTime statusUpdatedAt) { this.statusUpdatedAt = statusUpdatedAt; }

    public LocalDateTime getConfirmedAt() { return confirmedAt; }
    public void setConfirmedAt(LocalDateTime confirmedAt) { this.confirmedAt = confirmedAt; }

    public LocalTime getEstimatedDeliveryTime() { return estimatedDeliveryTime; }
    public void setEstimatedDeliveryTime(LocalTime estimatedDeliveryTime) { this.estimatedDeliveryTime = estimatedDeliveryTime; }
}