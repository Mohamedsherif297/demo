package com.mealplanner.api.model;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

// Marks this class as an embeddable component (the composite key)
@Embeddable 
public class UserAllergyId implements Serializable {

    private Integer userId;
    private Integer allergyId;

    // ----------------------------------------------------
    // CONSTRUCTORS
    // ----------------------------------------------------
    public UserAllergyId() {}
    
    public UserAllergyId(Integer userId, Integer allergyId) {
        this.userId = userId;
        this.allergyId = allergyId;
    }

    // ----------------------------------------------------
    // GETTERS AND SETTERS
    // ----------------------------------------------------
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public Integer getAllergyId() { return allergyId; }
    public void setAllergyId(Integer allergyId) { this.allergyId = allergyId; }
    
    // ----------------------------------------------------
    // equals() and hashCode() (MANDATORY for composite keys)
    // ----------------------------------------------------
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserAllergyId that = (UserAllergyId) o;
        return Objects.equals(userId, that.userId) && Objects.equals(allergyId, that.allergyId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, allergyId);
    }
}