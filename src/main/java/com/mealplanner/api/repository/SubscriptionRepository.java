package com.mealplanner.api.repository;

import com.mealplanner.api.model.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Integer> {

    /**
     * Finds all active subscriptions for a specific user ID.
     * Assumes 'Active' status has a statusName we can reference via the relationship.
     */
    List<Subscription> findByUser_UserIdAndStatus_StatusName(Integer userId, String statusName);

    /**
     * Finds the current or most recent subscription for a user.
     */
    Optional<Subscription> findTopByUser_UserIdOrderByStartDateDesc(Integer userId);
}