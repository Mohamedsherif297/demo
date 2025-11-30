package com.mealplanner.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

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