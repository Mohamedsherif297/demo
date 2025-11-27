package com.mealplanner.api.repository;

import com.mealplanner.api.model.DeliveryStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface DeliveryStatusRepository extends JpaRepository<DeliveryStatus, Integer> {

    /**
     * Finds a DeliveryStatus entity by its unique name (e.g., "Delivered", "Canceled").
     */
    Optional<DeliveryStatus> findByStatusName(String statusName);
}