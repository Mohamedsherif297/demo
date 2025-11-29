package com.mealplanner.api.repository;

import com.mealplanner.api.model.EmailVerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, Integer> {
    Optional<EmailVerificationToken> findByToken(String token);
    
    @Modifying
    @Query("DELETE FROM EmailVerificationToken e WHERE e.user.userId = :userId")
    void deleteByUserId(@Param("userId") Integer userId);
}
