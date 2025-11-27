package com.mealplanner.api.controller;

import com.mealplanner.api.dto.UserRegistrationDto;
import com.mealplanner.api.dto.UserResponseDto;
import com.mealplanner.api.model.User;
import com.mealplanner.api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@RestController // Marks this class to handle HTTP requests and return data (not views)
@RequestMapping("/api/v1/users") // Base path for all endpoints in this controller
public class UserController {

    private final UserService userService; // Association via Dependency Injection

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // --------------------------------------------------------------------------
    // 1. REGISTRATION ENDPOINT
    // URL: POST /api/v1/users/register
    // --------------------------------------------------------------------------
    @PostMapping("/register") 
    public ResponseEntity<UserResponseDto> registerUser(@RequestBody UserRegistrationDto registrationDto) {
        
        // 1. Map DTO to Entity
        User newUser = new User(
            registrationDto.getFullName(), 
            registrationDto.getEmail(), 
            null, // Password hash will be set by the Service
            null
        );

        // 2. Delegate business logic to the Service Layer
        User savedUser = userService.registerNewUser(newUser, registrationDto.getPassword());

        // 3. Map Entity back to Response DTO
        UserResponseDto responseDto = new UserResponseDto(
            savedUser.getUserId(),
            savedUser.getFullName(),
            savedUser.getEmail(),
            savedUser.getRole().getRoleName()
        );

        // Return 201 Created status
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED); 
    }

    // --------------------------------------------------------------------------
    // 2. AUTHENTICATION/LOGIN ENDPOINT (Simulated - often handled by Spring Security filters)
    // URL: POST /api/v1/users/login
    // --------------------------------------------------------------------------
    @PostMapping("/login")
    public ResponseEntity<UserResponseDto> loginUser(@RequestBody UserRegistrationDto loginDto) {
        
        // Delegate authentication logic to the Service
        Optional<User> authenticatedUser = userService.authenticate(loginDto.getEmail(), loginDto.getPassword());

        if (authenticatedUser.isPresent()) {
            User user = authenticatedUser.get();
            
            // In a real app, a JWT token would be generated here and returned.
            // For now, we return the user details.
            UserResponseDto responseDto = new UserResponseDto(
                user.getUserId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole().getRoleName()
            );
            
            // Return 200 OK status
            return ResponseEntity.ok(responseDto);
        } else {
            // Return 401 Unauthorized status
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }
    
    // --------------------------------------------------------------------------
    // 3. GET USER PROFILE ENDPOINT (Requires future security configuration)
    // URL: GET /api/v1/users/{userId}
    // --------------------------------------------------------------------------
    @GetMapping("/{userId}")
    public ResponseEntity<UserResponseDto> getUserProfile(@PathVariable Integer userId) {
        
        // Delegate retrieval to the Service Layer (includes Exception Handling)
        User user = userService.findUserById(userId);

        UserResponseDto responseDto = new UserResponseDto(
            user.getUserId(),
            user.getFullName(),
            user.getEmail(),
            user.getRole().getRoleName()
        );

        // Return 200 OK status
        return ResponseEntity.ok(responseDto);
    }
}