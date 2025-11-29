// ...existing code...
package com.mealplanner.api.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userId;

    private String fullName;

    @Column(unique = true)
    private String email;

    private String passwordHash;
    
    @Column(nullable = false)
    private boolean emailVerified = false;
    
    private LocalDate dob;
    private String phoneNumber;
    private String address;
    private String photoUrl;

    // Association: Many Users belong to one Role
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id")
    private Role role;

    private Double weight;
    private Double height;
    private Integer gymDays;
    private String weightGoal;
    private Integer weeklyDuration;
    private Integer caloriesPerDay;

    // Aggregation: One User can have many UserAllergy join records.
    // UserAllergy records use User and Allergy, but the User can exist without these records.
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true) 
    private Set<UserAllergy> userAllergies;

    // Aggregation: One User can have many History records.
    // History records are collected over time, but the core User profile is independent.
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<History> historyRecords;

    // ----------------------------------------------------
    // CONSTRUCTORS
    // ----------------------------------------------------
    // 1. No-Argument Constructor (MANDATORY for JPA/Hibernate)
    public User() {}

    // 2. Common Constructor for mandatory registration fields
    public User(String fullName, String email, String passwordHash, Role role) {
        this.fullName = fullName;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    // ----------------------------------------------------
    // GETTERS AND SETTERS
    // ----------------------------------------------------
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public boolean isEmailVerified() { return emailVerified; }
    public void setEmailVerified(boolean emailVerified) { this.emailVerified = emailVerified; }

    public LocalDate getDob() { return dob; }
    public void setDob(LocalDate dob) { this.dob = dob; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public Double getWeight() { return weight; }
    public void setWeight(Double weight) { this.weight = weight; }

    public Double getHeight() { return height; }
    public void setHeight(Double height) { this.height = height; }

    public Integer getGymDays() { return gymDays; }
    public void setGymDays(Integer gymDays) { this.gymDays = gymDays; }

    public String getWeightGoal() { return weightGoal; }
    public void setWeightGoal(String weightGoal) { this.weightGoal = weightGoal; }

    public Integer getWeeklyDuration() { return weeklyDuration; }
    public void setWeeklyDuration(Integer weeklyDuration) { this.weeklyDuration = weeklyDuration; }

    public Integer getCaloriesPerDay() { return caloriesPerDay; }
    public void setCaloriesPerDay(Integer caloriesPerDay) { this.caloriesPerDay = caloriesPerDay; }

    public Set<UserAllergy> getUserAllergies() { return userAllergies; }
    public void setUserAllergies(Set<UserAllergy> userAllergies) { this.userAllergies = userAllergies; }

    public Set<History> getHistoryRecords() { return historyRecords; }
    public void setHistoryRecords(Set<History> historyRecords) { this.historyRecords = historyRecords; }
}
// ...existing code...