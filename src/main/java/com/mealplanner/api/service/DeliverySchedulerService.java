package com.mealplanner.api.service;

import com.mealplanner.api.model.*;
import com.mealplanner.api.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

/**
 * Background service for automated delivery management.
 * Handles daily delivery creation and status progression.
 */
@Service
@Transactional
public class DeliverySchedulerService {

    private static final Logger logger = LoggerFactory.getLogger(DeliverySchedulerService.class);

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private SubscriptionMealRepository subscriptionMealRepository;

    @Autowired
    private DeliveryRepository deliveryRepository;

    @Autowired
    private DeliveryStatusRepository deliveryStatusRepository;

    /**
     * Creates deliveries for all active subscriptions at day start.
     * Runs daily at midnight.
     * Skips if delivery already exists.
     * Does not create deliveries for cancelled or paused subscriptions.
     * 
     * Requirements: 2.1, 2.2, 11.1, 11.2
     */
    @Scheduled(cron = "0 0 0 * * *") // Daily at midnight
    public void createDailyDeliveries() {
        logger.info("Starting daily delivery creation job");
        LocalDate today = LocalDate.now();
        
        // Query all active subscriptions (Requirements 11.1, 11.2)
        List<Subscription> activeSubscriptions = subscriptionRepository
                .findByStatus_StatusName("active");
        
        logger.info("Found {} active subscriptions", activeSubscriptions.size());
        
        int created = 0;
        int skipped = 0;
        
        for (Subscription subscription : activeSubscriptions) {
            try {
                // Get subscription meals for today
                List<SubscriptionMeal> subscriptionMeals = subscriptionMealRepository
                        .findBySubscription_SubscriptionIdAndDeliveryDate(
                                subscription.getSubscriptionId(), 
                                today
                        );
                
                if (subscriptionMeals.isEmpty()) {
                    logger.debug("No meals scheduled for subscription {} on {}", 
                            subscription.getSubscriptionId(), today);
                    continue;
                }
                
                // Use the first subscription meal to check if delivery exists
                SubscriptionMeal subscriptionMeal = subscriptionMeals.get(0);
                
                // Skip if delivery already exists (Requirement 2.2)
                if (subscriptionMeal.getDelivery() != null) {
                    logger.debug("Delivery already exists for subscription {} on {}", 
                            subscription.getSubscriptionId(), today);
                    skipped++;
                    continue;
                }
                
                // Create delivery for this subscription (Requirements 2.1, 2.3, 2.4, 2.5)
                createDeliveryForSubscription(subscription, subscriptionMeal, today);
                created++;
                
            } catch (Exception e) {
                logger.error("Error creating delivery for subscription {}: {}", 
                        subscription.getSubscriptionId(), e.getMessage(), e);
            }
        }
        
        logger.info("Daily delivery creation completed. Created: {}, Skipped: {}", created, skipped);
    }

    /**
     * Helper method to create a delivery for a subscription.
     */
    private void createDeliveryForSubscription(Subscription subscription, 
                                              SubscriptionMeal subscriptionMeal, 
                                              LocalDate deliveryDate) {
        // Get PREPARING status (Requirement 2.3)
        DeliveryStatus preparingStatus = deliveryStatusRepository.findByStatusName("PREPARING")
                .orElseThrow(() -> new RuntimeException("PREPARING status not found in system"));
        
        // Get user address or use default
        String address = subscription.getUser().getAddress();
        if (address == null || address.trim().isEmpty()) {
            address = "Default Address - Please update";
        }
        
        // Create delivery
        Delivery delivery = new Delivery();
        delivery.setSubscriptionMeal(subscriptionMeal);
        delivery.setAddress(address);
        delivery.setDeliveryTime(subscription.getPreferredTime()); // Requirement 2.4
        delivery.setEstimatedDeliveryTime(subscription.getPreferredTime());
        delivery.setStatus(preparingStatus);
        delivery.setCreatedAt(LocalDateTime.now());
        delivery.setStatusUpdatedAt(LocalDateTime.now());
        
        deliveryRepository.save(delivery);
        
        logger.debug("Created delivery for subscription {} on {}", 
                subscription.getSubscriptionId(), deliveryDate);
    }

    /**
     * Updates delivery statuses based on time progression.
     * Runs every minute.
     * Handles status transitions: PREPARING -> SHIPPED -> DELIVERED
     * Handles catch-up for late deliveries.
     * 
     * Requirements: 3.1, 3.2, 3.3, 3.4, 11.3
     */
    @Scheduled(fixedRate = 60000) // Every minute
    public void updateDeliveryStatuses() {
        logger.debug("Starting delivery status update job");
        
        // Query deliveries in PREPARING and SHIPPED status (Requirement 3.1, 3.2)
        List<Delivery> deliveries = deliveryRepository
                .findByStatus_StatusNameIn(Arrays.asList("PREPARING", "SHIPPED"));
        
        if (deliveries.isEmpty()) {
            logger.debug("No deliveries to update");
            return;
        }
        
        logger.debug("Found {} deliveries to process", deliveries.size());
        
        int updated = 0;
        
        for (Delivery delivery : deliveries) {
            try {
                if (progressDeliveryStatus(delivery)) {
                    updated++;
                }
            } catch (Exception e) {
                logger.error("Error updating delivery {}: {}", 
                        delivery.getDeliveryId(), e.getMessage(), e);
            }
        }
        
        logger.debug("Delivery status update completed. Updated: {}", updated);
    }

    /**
     * Progresses a delivery through its status lifecycle based on time.
     * Returns true if status was updated, false otherwise.
     * 
     * Requirements: 3.1, 3.2, 3.3, 3.4, 11.3
     */
    private boolean progressDeliveryStatus(Delivery delivery) {
        LocalDateTime now = LocalDateTime.now();
        LocalTime currentTime = now.toLocalTime();
        
        String currentStatus = delivery.getStatus().getStatusName();
        LocalTime preferredTime = delivery.getDeliveryTime();
        
        if (preferredTime == null) {
            logger.warn("Delivery {} has no preferred time set", delivery.getDeliveryId());
            return false;
        }
        
        // Handle catch-up for late deliveries (Requirement 11.3)
        // If current time is past delivery time and still preparing/shipped, jump to delivered
        if (currentTime.isAfter(preferredTime)) {
            if ("PREPARING".equals(currentStatus) || "SHIPPED".equals(currentStatus)) {
                updateStatus(delivery, "DELIVERED");
                logger.info("Catch-up: Delivery {} progressed to DELIVERED", delivery.getDeliveryId());
                return true;
            }
        }
        
        // Normal status progression
        if ("PREPARING".equals(currentStatus)) {
            // Transition to SHIPPED 2 hours before delivery time (Requirement 3.1)
            LocalTime shipTime = preferredTime.minusHours(2);
            if (currentTime.isAfter(shipTime) || currentTime.equals(shipTime)) {
                updateStatus(delivery, "SHIPPED");
                logger.debug("Delivery {} progressed to SHIPPED", delivery.getDeliveryId());
                return true;
            }
        } else if ("SHIPPED".equals(currentStatus)) {
            // Transition to DELIVERED at delivery time (Requirement 3.2)
            if (currentTime.isAfter(preferredTime) || currentTime.equals(preferredTime)) {
                updateStatus(delivery, "DELIVERED");
                logger.debug("Delivery {} progressed to DELIVERED", delivery.getDeliveryId());
                return true;
            }
        }
        // DELIVERED status requires manual confirmation (Requirement 3.4)
        
        return false;
    }

    /**
     * Updates the status of a delivery and records the timestamp.
     * 
     * Requirement 3.3
     */
    private void updateStatus(Delivery delivery, String statusName) {
        DeliveryStatus newStatus = deliveryStatusRepository.findByStatusName(statusName)
                .orElseThrow(() -> new RuntimeException(statusName + " status not found in system"));
        
        delivery.setStatus(newStatus);
        delivery.setStatusUpdatedAt(LocalDateTime.now()); // Requirement 3.3
        
        deliveryRepository.save(delivery);
    }
}
