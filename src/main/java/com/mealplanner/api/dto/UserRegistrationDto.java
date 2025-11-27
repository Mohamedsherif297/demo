package com.mealplanner.api.dto;

// This is not a JPA Entity; just a simple data class.
public class UserRegistrationDto {
    private String fullName;
    private String email;
    private String password; // Raw password from the user
    // Other fields (dob, phone_number, etc.) if required at registration

    // --- CONSTRUCTORS, GETTERS, SETTERS ---
    public UserRegistrationDto() {}
    
    // Minimal constructor
    public UserRegistrationDto(String fullName, String email, String password) {
        this.fullName = fullName;
        this.email = email;
        this.password = password;
    }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; } // Keep this for the service layer
    public void setPassword(String password) { this.password = password; }
}