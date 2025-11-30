package com.mealplanner.api.repository;

import com.mealplanner.api.model.Subscription;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    /**
     * Finds all subscriptions for a specific user with pagination.
     */
    Page<Subscription> findByUserUserId(Integer userId, Pageable pageable);

    /**
     * Finds all subscriptions for a specific user filtered by status with pagination.
     */
    Page<Subscription> findByUserUserIdAndStatusStatusName(Integer userId, String status, Pageable pageable);

    /**
     * Finds all subscriptions with a specific status with pagination.
     */
    Page<Subscription> findByStatusStatusName(String status, Pageable pageable);

    /**
     * Finds all subscriptions with a specific status.
     * Used by scheduler to get all active subscriptions.
     */
    List<Subscription> findByStatus_StatusName(String statusName);
}