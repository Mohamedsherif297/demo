package com.mealplanner.api.service;

import com.mealplanner.api.dto.UpdateProfileDto;
import com.mealplanner.api.exception.ResourceNotFoundException;
import com.mealplanner.api.model.User;
import com.mealplanner.api.model.Role;
import com.mealplanner.api.repository.UserRepository;
import com.mealplanner.api.repository.RoleRepository;
import com.mealplanner.api.util.PasswordValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository; 
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerNewUser(User newUser, String rawPassword) {
        if (userRepository.findByEmail(newUser.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already exists.");
        }

        // Validate password strength
        PasswordValidator.ValidationResult validationResult = PasswordValidator.validate(rawPassword);
        if (!validationResult.isValid()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, validationResult.getErrorMessage());
        }

        Role clientRole = roleRepository.findByRoleName("CLIENT")
                .orElseThrow(() -> new ResourceNotFoundException("Default role 'CLIENT' not found."));

        String hashedPassword = passwordEncoder.encode(rawPassword);
        
        newUser.setPasswordHash(hashedPassword);
        newUser.setRole(clientRole);
        newUser.setEmailVerified(false);

        return userRepository.save(newUser);
    }

    public Optional<User> authenticate(String email, String rawPassword) {
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            
            if (passwordEncoder.matches(rawPassword, user.getPasswordHash())) {
                return Optional.of(user);
            }
        }
        return Optional.empty(); 
    }

    public User findUserById(Integer userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
    }

    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    public User updateProfile(Integer userId, UpdateProfileDto updateDto) {
        User user = findUserById(userId);

        if (updateDto.getFullName() != null) {
            user.setFullName(updateDto.getFullName());
        }
        if (updateDto.getPhoneNumber() != null) {
            user.setPhoneNumber(updateDto.getPhoneNumber());
        }
        if (updateDto.getAddress() != null) {
            user.setAddress(updateDto.getAddress());
        }
        if (updateDto.getPhotoUrl() != null) {
            user.setPhotoUrl(updateDto.getPhotoUrl());
        }
        if (updateDto.getDob() != null) {
            user.setDob(updateDto.getDob());
        }
        if (updateDto.getWeight() != null) {
            user.setWeight(updateDto.getWeight());
        }
        if (updateDto.getHeight() != null) {
            user.setHeight(updateDto.getHeight());
        }
        if (updateDto.getGymDays() != null) {
            user.setGymDays(updateDto.getGymDays());
        }
        if (updateDto.getWeightGoal() != null) {
            user.setWeightGoal(updateDto.getWeightGoal());
        }
        if (updateDto.getWeeklyDuration() != null) {
            user.setWeeklyDuration(updateDto.getWeeklyDuration());
        }
        if (updateDto.getCaloriesPerDay() != null) {
            user.setCaloriesPerDay(updateDto.getCaloriesPerDay());
        }

        return userRepository.save(user);
    }

    public void changePassword(Integer userId, String currentPassword, String newPassword) {
        User user = findUserById(userId);

        if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Current password is incorrect.");
        }

        // Validate new password strength
        PasswordValidator.ValidationResult validationResult = PasswordValidator.validate(newPassword);
        if (!validationResult.isValid()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, validationResult.getErrorMessage());
        }

        String hashedPassword = passwordEncoder.encode(newPassword);
        user.setPasswordHash(hashedPassword);
        userRepository.save(user);
    }

    public void resetPassword(Integer userId, String newPassword) {
        // Validate new password strength
        PasswordValidator.ValidationResult validationResult = PasswordValidator.validate(newPassword);
        if (!validationResult.isValid()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, validationResult.getErrorMessage());
        }

        User user = findUserById(userId);
        String hashedPassword = passwordEncoder.encode(newPassword);
        user.setPasswordHash(hashedPassword);
        userRepository.save(user);
    }

    public void verifyEmail(Integer userId) {
        User user = findUserById(userId);
        user.setEmailVerified(true);
        userRepository.save(user);
    }
}