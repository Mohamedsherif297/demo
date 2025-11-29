package com.mealplanner.api.service;

import com.mealplanner.api.model.BlacklistedToken;
import com.mealplanner.api.repository.BlacklistedTokenRepository;
import com.mealplanner.api.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
public class TokenBlacklistService {

    private final BlacklistedTokenRepository blacklistedTokenRepository;
    private final JwtUtil jwtUtil;

    @Autowired
    public TokenBlacklistService(BlacklistedTokenRepository blacklistedTokenRepository, JwtUtil jwtUtil) {
        this.blacklistedTokenRepository = blacklistedTokenRepository;
        this.jwtUtil = jwtUtil;
    }

    public void blacklistToken(String token) {
        Date expirationDate = jwtUtil.extractExpiration(token);
        LocalDateTime expiryDateTime = expirationDate.toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime();
        
        BlacklistedToken blacklistedToken = new BlacklistedToken(token, expiryDateTime);
        blacklistedTokenRepository.save(blacklistedToken);
    }

    public boolean isTokenBlacklisted(String token) {
        return blacklistedTokenRepository.findByToken(token).isPresent();
    }

    @Transactional
    public void cleanupExpiredTokens() {
        blacklistedTokenRepository.deleteByExpiryDateBefore(LocalDateTime.now());
    }
}
