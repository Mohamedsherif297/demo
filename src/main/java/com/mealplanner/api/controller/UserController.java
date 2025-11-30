package com.mealplanner.api.controller;

import com.mealplanner.api.dto.*;
import com.mealplanner.api.model.RefreshToken;
import com.mealplanner.api.model.User;
import com.mealplanner.api.security.CustomUserDetails;
import com.mealplanner.api.security.JwtUtil;
import com.mealplanner.api.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;
    private final PasswordResetService passwordResetService;
    private final EmailService emailService;
    private final TokenBlacklistService tokenBlacklistService;
    private final EmailVerificationService emailVerificationService;

    @Autowired
    public UserController(UserService userService, AuthenticationManager authenticationManager, 
                         JwtUtil jwtUtil, RefreshTokenService refreshTokenService,
                         PasswordResetService passwordResetService, EmailService emailService,
                         TokenBlacklistService tokenBlacklistService,
                         EmailVerificationService emailVerificationService) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.refreshTokenService = refreshTokenService;
        this.passwordResetService = passwordResetService;
        this.emailService = emailService;
        this.tokenBlacklistService = tokenBlacklistService;
        this.emailVerificationService = emailVerificationService;
    }

    @PostMapping("/register") 
    public ResponseEntity<?> registerUser(@RequestBody UserRegistrationDto registrationDto) {
        try {
            System.out.println("=== REGISTRATION ATTEMPT ===");
            System.out.println("Email: " + registrationDto.getEmail());
            System.out.println("Full Name: " + registrationDto.getFullName());
            
            User newUser = new User(
                registrationDto.getFullName(), 
                registrationDto.getEmail(), 
                null,
                null
            );
            
            // Set optional fields if provided
            if (registrationDto.getPhoneNumber() != null) {
                newUser.setPhoneNumber(registrationDto.getPhoneNumber());
            }
            if (registrationDto.getAddress() != null) {
                newUser.setAddress(registrationDto.getAddress());
            }
            if (registrationDto.getPhotoUrl() != null) {
                newUser.setPhotoUrl(registrationDto.getPhotoUrl());
            }
            if (registrationDto.getDob() != null) {
                newUser.setDob(registrationDto.getDob());
            }
            if (registrationDto.getWeight() != null) {
                newUser.setWeight(registrationDto.getWeight());
            }
            if (registrationDto.getHeight() != null) {
                newUser.setHeight(registrationDto.getHeight());
            }
            if (registrationDto.getGymDays() != null) {
                newUser.setGymDays(registrationDto.getGymDays());
            }
            if (registrationDto.getWeightGoal() != null) {
                newUser.setWeightGoal(registrationDto.getWeightGoal());
            }
            if (registrationDto.getWeeklyDuration() != null) {
                newUser.setWeeklyDuration(registrationDto.getWeeklyDuration());
            }
            if (registrationDto.getCaloriesPerDay() != null) {
                newUser.setCaloriesPerDay(registrationDto.getCaloriesPerDay());
            }

            System.out.println("Calling userService.registerNewUser...");
            User savedUser = userService.registerNewUser(newUser, registrationDto.getPassword());
            System.out.println("User saved successfully with ID: " + savedUser.getUserId());

            // Generate tokens
            CustomUserDetails userDetails = new CustomUserDetails(savedUser);
            String accessToken = jwtUtil.generateToken(userDetails);
            String refreshToken = refreshTokenService.createRefreshToken(savedUser);

            // Send welcome email (non-blocking, don't fail registration if email fails)
            try {
                emailService.sendWelcomeEmail(savedUser.getEmail(), savedUser.getFullName());
            } catch (Exception e) {
                System.err.println("Failed to send welcome email: " + e.getMessage());
            }
            
            // Send email verification (non-blocking, don't fail registration if email fails)
            try {
                String verificationToken = emailVerificationService.createEmailVerificationToken(savedUser);
                emailService.sendEmailVerification(savedUser.getEmail(), verificationToken);
            } catch (Exception e) {
                System.err.println("Failed to send verification email: " + e.getMessage());
            }

            AuthResponseDto responseDto = new AuthResponseDto(
                accessToken,
                refreshToken,
                savedUser.getUserId(),
                savedUser.getEmail(),
                savedUser.getFullName(),
                savedUser.getRole().getRoleName()
            );

            return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
        } catch (Exception e) {
            System.err.println("=== REGISTRATION ERROR ===");
            System.err.println("Error Type: " + e.getClass().getName());
            System.err.println("Error Message: " + e.getMessage());
            e.printStackTrace();
            
            // Return detailed error in response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "error", e.getClass().getSimpleName(),
                    "message", e.getMessage() != null ? e.getMessage() : "Unknown error",
                    "details", "Check server logs for full stack trace"
                ));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> loginUser(@RequestBody LoginRequestDto loginDto) {
        
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String accessToken = jwtUtil.generateToken(userDetails);
        String refreshToken = refreshTokenService.createRefreshToken(userDetails.getUser());

        User user = userDetails.getUser();
        AuthResponseDto responseDto = new AuthResponseDto(
            accessToken,
            refreshToken,
            user.getUserId(),
            user.getEmail(),
            user.getFullName(),
            user.getRole().getRoleName()
        );

        return ResponseEntity.ok(responseDto);
    }
    
    @GetMapping("/{userId}")
    public ResponseEntity<UserResponseDto> getUserProfile(@PathVariable Integer userId) {
        
        User user = userService.findUserById(userId);

        UserResponseDto responseDto = new UserResponseDto(
            user.getUserId(),
            user.getFullName(),
            user.getEmail(),
            user.getRole().getRoleName()
        );

        return ResponseEntity.ok(responseDto);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UserResponseDto> updateProfile(@PathVariable Integer userId, 
                                                         @RequestBody UpdateProfileDto updateDto) {
        
        // Authorization check: user can only update their own profile
        CustomUserDetails currentUser = getCurrentUser();
        if (!currentUser.getUser().getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only update your own profile");
        }
        
        User updatedUser = userService.updateProfile(userId, updateDto);

        UserResponseDto responseDto = new UserResponseDto(
            updatedUser.getUserId(),
            updatedUser.getFullName(),
            updatedUser.getEmail(),
            updatedUser.getRole().getRoleName()
        );

        return ResponseEntity.ok(responseDto);
    }

    @PutMapping("/{userId}/password")
    public ResponseEntity<Map<String, String>> changePassword(@PathVariable Integer userId, 
                                                              @RequestBody ChangePasswordDto changePasswordDto) {
        
        // Authorization check: user can only change their own password
        CustomUserDetails currentUser = getCurrentUser();
        if (!currentUser.getUser().getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only change your own password");
        }
        
        userService.changePassword(userId, changePasswordDto.getCurrentPassword(), 
                                  changePasswordDto.getNewPassword());

        return ResponseEntity.ok(Map.of("message", "Password changed successfully"));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(@RequestBody ForgotPasswordDto forgotPasswordDto) {
        
        String resetToken = passwordResetService.createPasswordResetToken(forgotPasswordDto.getEmail());
        emailService.sendPasswordResetEmail(forgotPasswordDto.getEmail(), resetToken);

        return ResponseEntity.ok(Map.of("message", "Password reset email sent"));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(@RequestBody ResetPasswordDto resetPasswordDto) {
        
        passwordResetService.validateAndResetPassword(resetPasswordDto.getToken(), 
                                                      resetPasswordDto.getNewPassword());

        return ResponseEntity.ok(Map.of("message", "Password reset successfully"));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<AuthResponseDto> refreshToken(@RequestBody Map<String, String> request) {
        
        String refreshToken = request.get("refreshToken");
        RefreshToken validatedToken = refreshTokenService.validateRefreshToken(refreshToken);
        
        User user = validatedToken.getUser();
        CustomUserDetails userDetails = new CustomUserDetails(user);
        String newAccessToken = jwtUtil.generateToken(userDetails);

        AuthResponseDto responseDto = new AuthResponseDto(
            newAccessToken,
            refreshToken,
            user.getUserId(),
            user.getEmail(),
            user.getFullName(),
            user.getRole().getRoleName()
        );

        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(@RequestHeader("Authorization") String authHeader) {
        
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            tokenBlacklistService.blacklistToken(token);
        }

        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }

    @GetMapping("/verify-email")
    public ResponseEntity<Map<String, String>> verifyEmail(@RequestParam String token) {
        
        emailVerificationService.verifyEmail(token);

        return ResponseEntity.ok(Map.of("message", "Email verified successfully"));
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<Map<String, String>> resendVerification(@RequestBody Map<String, String> request) {
        
        String email = request.get("email");
        User user = userService.findUserByEmail(email);

        if (user.isEmailVerified()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email is already verified");
        }

        String verificationToken = emailVerificationService.createEmailVerificationToken(user);
        emailService.sendEmailVerification(user.getEmail(), verificationToken);

        return ResponseEntity.ok(Map.of("message", "Verification email sent"));
    }

    // Helper method to get current authenticated user
    private CustomUserDetails getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
        }
        return (CustomUserDetails) authentication.getPrincipal();
    }
}