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
    public ResponseEntity<AuthResponseDto> registerUser(@RequestBody UserRegistrationDto registrationDto) {
        
        User newUser = new User(
            registrationDto.getFullName(), 
            registrationDto.getEmail(), 
            null,
            null
        );

        User savedUser = userService.registerNewUser(newUser, registrationDto.getPassword());

        // Generate tokens
        CustomUserDetails userDetails = new CustomUserDetails(savedUser);
        String accessToken = jwtUtil.generateToken(userDetails);
        String refreshToken = refreshTokenService.createRefreshToken(savedUser);

        // Send welcome email
        emailService.sendWelcomeEmail(savedUser.getEmail(), savedUser.getFullName());
        
        // Send email verification
        String verificationToken = emailVerificationService.createEmailVerificationToken(savedUser);
        emailService.sendEmailVerification(savedUser.getEmail(), verificationToken);

        AuthResponseDto responseDto = new AuthResponseDto(
            accessToken,
            refreshToken,
            savedUser.getUserId(),
            savedUser.getEmail(),
            savedUser.getFullName(),
            savedUser.getRole().getRoleName()
        );

        return new ResponseEntity<>(responseDto, HttpStatus.CREATED); 
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