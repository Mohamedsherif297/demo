package com.mealplanner.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Past;
import java.time.LocalDate;

// This is not a JPA Entity; just a simple data class.
public class UserRegistrationDto {
    @NotBlank(message = "Full name is required")
    @Size(min = 1, max = 255, message = "Full name must be between 1 and 255 characters")
    private String fullName;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    private String email;
    
    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 255, message = "Password must be between 8 and 255 characters")
    private String password;
    
    // Optional fields
    @Size(max = 20, message = "Phone number must not exceed 20 characters")
    private String phoneNumber;
    
    @Size(max = 500, message = "Address must not exceed 500 characters")
    private String address;
    
    @Size(max = 500, message = "Photo URL must not exceed 500 characters")
    private String photoUrl;
    
    @Past(message = "Date of birth must be in the past")
    private LocalDate dob;
    
    @Min(value = 0, message = "Weight must be non-negative")
    @Max(value = 500, message = "Weight must not exceed 500 kg")
    private Double weight;
    
    @Min(value = 0, message = "Height must be non-negative")
    @Max(value = 300, message = "Height must not exceed 300 cm")
    private Double height;
    
    @Min(value = 0, message = "Gym days must be non-negative")
    @Max(value = 7, message = "Gym days must not exceed 7")
    private Integer gymDays;
    
    @Size(max = 50, message = "Weight goal must not exceed 50 characters")
    private String weightGoal;
    
    @Min(value = 0, message = "Weekly duration must be non-negative")
    private Integer weeklyDuration;
    
    @Min(value = 0, message = "Calories per day must be non-negative")
    @Max(value = 10000, message = "Calories per day must not exceed 10000")
    private Integer caloriesPerDay;

    // --- CONSTRUCTORS ---
    public UserRegistrationDto() {}
    
    // Minimal constructor
    public UserRegistrationDto(String fullName, String email, String password) {
        this.fullName = fullName;
        this.email = email;
        this.password = password;
    }

    // --- GETTERS AND SETTERS ---
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    
    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }
    
    public LocalDate getDob() { return dob; }
    public void setDob(LocalDate dob) { this.dob = dob; }
    
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
}