package com.mealplanner.api.repository;

import com.mealplanner.api.model.SubscriptionHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SubscriptionHistoryRepository extends JpaRepository<SubscriptionHistory, Integer> {

    /**
     * Finds all history records for a specific subscription, ordered by event time descending.
     */
    List<SubscriptionHistory> findBySubscription_SubscriptionIdOrderByEventTimeDesc(Integer subscriptionId);
}