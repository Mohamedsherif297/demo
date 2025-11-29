package com.mealplanner.api.repository;

import com.mealplanner.api.model.BlacklistedToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface BlacklistedTokenRepository extends JpaRepository<BlacklistedToken, Integer> {
    Optional<BlacklistedToken> findByToken(String token);
    void deleteByExpiryDateBefore(LocalDateTime date);
}
