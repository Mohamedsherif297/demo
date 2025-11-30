package com.mealplanner.api.repository;

import com.mealplanner.api.model.SubscriptionMeal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    /**
     * Finds all scheduled meals for a subscription within a date range.
     * If startDate is null, returns all meals up to endDate.
     * If endDate is null, returns all meals from startDate onwards.
     */
    @Query("SELECT sm FROM SubscriptionMeal sm " +
           "WHERE sm.subscription.subscriptionId = :subscriptionId " +
           "AND (:startDate IS NULL OR sm.deliveryDate >= :startDate) " +
           "AND (:endDate IS NULL OR sm.deliveryDate <= :endDate) " +
           "ORDER BY sm.deliveryDate")
    List<SubscriptionMeal> findBySubscriptionIdAndDateRange(
        @Param("subscriptionId") Integer subscriptionId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
}
