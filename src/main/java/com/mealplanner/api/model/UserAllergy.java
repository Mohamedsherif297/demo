package com.mealplanner.api.model;

import jakarta.persistence.*;

@Entity
@Table(name = "user_allergy")
public class UserAllergy {

    @EmbeddedId
    private UserAllergyId id = new UserAllergyId();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "allergy_id", insertable = false, updatable = false)
    private Allergy allergy;

    // ----------------------------------------------------
    // CONSTRUCTORS
    // ----------------------------------------------------
    public UserAllergy() {}

    public UserAllergy(User user, Allergy allergy) {
        this.user = user;
        this.allergy = allergy;
        // IMPORTANT: Manually set the keys in the composite ID object
        this.id.setUserId(user.getUserId());
        this.id.setAllergyId(allergy.getAllergyId());
    }

    // ----------------------------------------------------
    // GETTERS AND SETTERS
    // ----------------------------------------------------
    public UserAllergyId getId() { return id; }
    public void setId(UserAllergyId id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Allergy getAllergy() { return allergy; }
    public void setAllergy(Allergy allergy) { this.allergy = allergy; }
}