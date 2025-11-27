package com.mealplanner.api.repository;

import com.mealplanner.api.model.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface SubscriptionStatusRepository extends JpaRepository<SubscriptionStatus, Integer> {

    /**
     * Finds a SubscriptionStatus entity by its unique name (e.g., "Active", "Paused").
     */
    Optional<SubscriptionStatus> findByStatusName(String statusName);
}