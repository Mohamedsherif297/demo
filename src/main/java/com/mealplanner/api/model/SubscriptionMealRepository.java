package com.mealplanner.api.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface SubscriptionMealRepository extends JpaRepository<SubscriptionMeal, Integer> {

    /**
     * Finds all assigned meals for a specific subscription on a given date.
     */
    List<SubscriptionMeal> findBySubscription_SubscriptionIdAndDeliveryDate(Integer subscriptionId, LocalDate deliveryDate);

    /**
     * Finds all assigned meals for a specific meal ID (useful for tracking popularity).
     */
    List<SubscriptionMeal> findByMeal_MealId(Integer mealId);
}