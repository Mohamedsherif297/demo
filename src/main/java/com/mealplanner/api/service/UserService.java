package com.mealplanner.api.service;

import com.mealplanner.api.exception.ResourceNotFoundException;
import com.mealplanner.api.model.User;
import com.mealplanner.api.model.Role;
import com.mealplanner.api.repository.UserRepository;
import com.mealplanner.api.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
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
            throw new IllegalStateException("User with email " + newUser.getEmail() + " already exists.");
        }

        Role clientRole = roleRepository.findByRoleName("CLIENT")
                .orElseThrow(() -> new ResourceNotFoundException("Default role 'CLIENT' not found."));

        String hashedPassword = passwordEncoder.encode(rawPassword);
        
        newUser.setPasswordHash(hashedPassword);
        newUser.setRole(clientRole);

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
        return userRepository.findById(userId.intValue())
            .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
    }
}