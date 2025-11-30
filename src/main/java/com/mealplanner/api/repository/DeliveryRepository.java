package com.mealplanner.api.repository;

import com.mealplanner.api.model.Delivery;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Integer>, JpaSpecificationExecutor<Delivery> {

    /**
     * Finds the delivery record for a specific subscription meal ID.
     */
    Optional<Delivery> findBySubscriptionMeal_SubscriptionMealId(Integer subscriptionMealId);

    /**
     * Finds all deliveries scheduled for a specific date (useful for driver routing/dispatch).
     * Note: This requires navigating from Delivery -> SubscriptionMeal -> DeliveryDate.
     */
    List<Delivery> findBySubscriptionMeal_DeliveryDate(LocalDate deliveryDate);

    /**
     * Finds the delivery for a specific user on a specific date.
     * Used for getting current delivery.
     */
    Optional<Delivery> findBySubscriptionMeal_Subscription_User_UserIdAndSubscriptionMeal_DeliveryDate(
        Integer userId, LocalDate deliveryDate);

    /**
     * Finds all deliveries for a specific user ordered by delivery date descending.
     * Used for delivery history.
     */
    List<Delivery> findBySubscriptionMeal_Subscription_User_UserIdOrderBySubscriptionMeal_DeliveryDateDesc(
        Integer userId, Pageable pageable);

    /**
     * Finds all deliveries with a specific status.
     * Used for status progression scheduler.
     */
    List<Delivery> findByStatus_StatusName(String statusName);

    /**
     * Finds all deliveries with any of the specified statuses.
     * Used for batch status updates.
     */
    List<Delivery> findByStatus_StatusNameIn(List<String> statusNames);

    /**
     * Counts deliveries for a specific user.
     * Used for pagination metadata.
     */
    long countBySubscriptionMeal_Subscription_User_UserId(Integer userId);
}