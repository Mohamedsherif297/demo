package com.mealplanner.api.dto;

import java.time.LocalDate;

public class UpdateProfileDto {
    private String fullName;
    private String phoneNumber;
    private String address;
    private String photoUrl;
    private LocalDate dob;
    private Double weight;
    private Double height;
    private Integer gymDays;
    private String weightGoal;
    private Integer weeklyDuration;
    private Integer caloriesPerDay;

    public UpdateProfileDto() {}

    // Getters and Setters
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public LocalDate getDob() {
        return dob;
    }

    public void setDob(LocalDate dob) {
        this.dob = dob;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public Integer getGymDays() {
        return gymDays;
    }

    public void setGymDays(Integer gymDays) {
        this.gymDays = gymDays;
    }

    public String getWeightGoal() {
        return weightGoal;
    }

    public void setWeightGoal(String weightGoal) {
        this.weightGoal = weightGoal;
    }

    public Integer getWeeklyDuration() {
        return weeklyDuration;
    }

    public void setWeeklyDuration(Integer weeklyDuration) {
        this.weeklyDuration = weeklyDuration;
    }

    public Integer getCaloriesPerDay() {
        return caloriesPerDay;
    }

    public void setCaloriesPerDay(Integer caloriesPerDay) {
        this.caloriesPerDay = caloriesPerDay;
    }
}
