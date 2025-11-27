package com.mealplanner.api.dto;

public class UserResponseDto {
    private Integer userId;
    private String fullName;
    private String email;
    private String roleName;

    // --- CONSTRUCTORS, GETTERS, SETTERS ---
    public UserResponseDto() {}
    
    public UserResponseDto(Integer userId, String fullName, String email, String roleName) {
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.roleName = roleName;
    }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getRoleName() { return roleName; }
    public void setRoleName(String roleName) { this.roleName = roleName; }
}