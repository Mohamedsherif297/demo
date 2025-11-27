package com.mealplanner.api.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "subscription_history")
public class SubscriptionHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer historyId; // Maps to history_id

    // Association with Subscription
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_id", referencedColumnName = "subscriptionId")
    private Subscription subscription; // Maps to subscription_id

    private String eventType; // Maps to event_type
    private String description; // Maps to description
    private LocalDateTime eventTime; // Maps to event_time

    // ----------------------------------------------------
    // CONSTRUCTORS
    // ----------------------------------------------------
    public SubscriptionHistory() {}

    public SubscriptionHistory(Subscription subscription, String eventType, String description) {
        this.subscription = subscription;
        this.eventType = eventType;
        this.description = description;
    }

    // JPA Lifecycle callback to set DATETIME stamp automatically on save
    @PrePersist
    protected void onCreate() {
        if (this.eventTime == null) {
            this.eventTime = LocalDateTime.now();
        }
    }

    // ----------------------------------------------------
    // GETTERS AND SETTERS
    // ----------------------------------------------------
    public Integer getHistoryId() { return historyId; }
    public void setHistoryId(Integer historyId) { this.historyId = historyId; }

    public Subscription getSubscription() { return subscription; }
    public void setSubscription(Subscription subscription) { this.subscription = subscription; }

    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getEventTime() { return eventTime; }
    public void setEventTime(LocalDateTime eventTime) { this.eventTime = eventTime; }
}