package com.mealplanner.api.repository;

import com.mealplanner.api.model.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Integer> {

    /**
     * Finds the delivery record for a specific subscription meal ID.
     */
    Optional<Delivery> findBySubscriptionMeal_SubscriptionMealId(Integer subscriptionMealId);

    /**
     * Finds all deliveries scheduled for a specific date (useful for driver routing/dispatch).
     * Note: This requires navigating from Delivery -> SubscriptionMeal -> DeliveryDate.
     */
    List<Delivery> findBySubscriptionMeal_DeliveryDate(LocalDate deliveryDate);
}