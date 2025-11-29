package com.mealplanner.api.service;

import com.mealplanner.api.model.EmailVerificationToken;
import com.mealplanner.api.model.User;
import com.mealplanner.api.repository.EmailVerificationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class EmailVerificationService {

    private final EmailVerificationTokenRepository tokenRepository;
    private final UserService userService;

    @Autowired
    public EmailVerificationService(EmailVerificationTokenRepository tokenRepository, UserService userService) {
        this.tokenRepository = tokenRepository;
        this.userService = userService;
    }

    @Transactional
    public String createEmailVerificationToken(User user) {
        // Delete any existing tokens for this user
        tokenRepository.deleteByUserId(user.getUserId());
        
        // Generate new token
        String token = UUID.randomUUID().toString();
        LocalDateTime expiryDate = LocalDateTime.now().plusHours(24);
        
        EmailVerificationToken verificationToken = new EmailVerificationToken(token, user, expiryDate);
        tokenRepository.save(verificationToken);
        
        return token;
    }

    @Transactional
    public void verifyEmail(String token) {
        EmailVerificationToken verificationToken = tokenRepository.findByToken(token)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid verification token."));

        if (verificationToken.isUsed()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Verification token has already been used.");
        }

        if (verificationToken.isExpired()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Verification token has expired.");
        }

        // Verify email
        User user = verificationToken.getUser();
        userService.verifyEmail(user.getUserId());

        // Mark token as used
        verificationToken.setUsed(true);
        tokenRepository.save(verificationToken);
    }
}
