package com.mealplanner.api.model;

import jakarta.persistence.*;

@Entity
@Table(name = "delivery_status")
public class DeliveryStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer statusId; // Maps to status_id

    private String statusName; // Maps to status_name

    // ----------------------------------------------------
    // CONSTRUCTORS
    // ----------------------------------------------------
    public DeliveryStatus() {}

    public DeliveryStatus(String statusName) {
        this.statusName = statusName;
    }

    // ----------------------------------------------------
    // GETTERS AND SETTERS
    // ----------------------------------------------------
    public Integer getStatusId() { return statusId; }
    public void setStatusId(Integer statusId) { this.statusId = statusId; }

    public String getStatusName() { return statusName; }
    public void setStatusName(String statusName) { this.statusName = statusName; }
}