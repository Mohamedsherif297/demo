package com.mealplanner.api.config;

import com.mealplanner.api.model.DeliveryStatus;
import com.mealplanner.api.repository.DeliveryStatusRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Initializes delivery tracking data on application startup.
 * Ensures required delivery status values exist in the database.
 */
@Configuration
public class DeliveryDataInitializer {

    @Bean
    public CommandLineRunner initDeliveryStatuses(DeliveryStatusRepository deliveryStatusRepository) {
        return args -> {
            // Insert delivery status values if they don't exist
            String[] statusNames = {"PREPARING", "SHIPPED", "DELIVERED", "CONFIRMED"};
            
            for (String statusName : statusNames) {
                if (deliveryStatusRepository.findByStatusName(statusName).isEmpty()) {
                    DeliveryStatus status = new DeliveryStatus(statusName);
                    deliveryStatusRepository.save(status);
                    System.out.println("Initialized delivery status: " + statusName);
                }
            }
        };
    }
}
