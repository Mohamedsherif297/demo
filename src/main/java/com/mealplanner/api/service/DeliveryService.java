package com.mealplanner.api.service;

import com.mealplanner.api.dto.*;
import com.mealplanner.api.exception.ForbiddenException;
import com.mealplanner.api.exception.ResourceNotFoundException;
import com.mealplanner.api.exception.ValidationException;
import com.mealplanner.api.model.*;
import com.mealplanner.api.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class DeliveryService {

    @Autowired
    private DeliveryRepository deliveryRepository;

    @Autowired
    private DeliveryStatusRepository deliveryStatusRepository;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private SubscriptionMealRepository subscriptionMealRepository;

    @Autowired
    private HistoryRepository historyRepository;

    /**
     * Creates a daily delivery for a given subscription and date.
     * Sets initial status to PREPARING, delivery time from subscription preferred time,
     * associates with subscription meals, and sets createdAt timestamp.
     * 
     * Requirements: 2.1, 2.3, 2.4, 2.5
     */
    @SuppressWarnings("null")
    public DeliveryResponseDto createDailyDelivery(Integer subscriptionId, LocalDate deliveryDate) {
        // Validate input parameters
        if (subscriptionId == null) {
            throw new ValidationException("Subscription ID is required");
        }
        if (deliveryDate == null) {
            throw new ValidationException("Delivery date is required");
        }

        // Get subscription
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found with ID: " + subscriptionId));

        // Validate subscription has preferred time set (Requirement 1.1, 1.3)
        if (subscription.getPreferredTime() == null) {
            throw new ValidationException("Subscription does not have a preferred delivery time set");
        }

        // Get subscription meals for the delivery date
        List<SubscriptionMeal> subscriptionMeals = subscriptionMealRepository
                .findBySubscription_SubscriptionIdAndDeliveryDate(subscriptionId, deliveryDate);

        if (subscriptionMeals.isEmpty()) {
            throw new ValidationException("No meals scheduled for date: " + deliveryDate);
        }

        // Use the first subscription meal to associate with delivery
        SubscriptionMeal subscriptionMeal = subscriptionMeals.get(0);

        // Check if delivery already exists for this subscription meal
        if (subscriptionMeal.getDelivery() != null) {
            throw new ValidationException("Delivery already exists for date: " + deliveryDate);
        }

        // Get PREPARING status
        DeliveryStatus preparingStatus = deliveryStatusRepository.findByStatusName("PREPARING")
                .orElseThrow(() -> new ValidationException("PREPARING status not found in system"));

        // Get user address or use default
        String address = subscription.getUser().getAddress();
        if (address == null || address.trim().isEmpty()) {
            address = "Default Address - Please update";
        }

        // Create delivery
        Delivery delivery = new Delivery();
        delivery.setSubscriptionMeal(subscriptionMeal);
        delivery.setAddress(address);
        delivery.setDeliveryTime(subscription.getPreferredTime());
        delivery.setEstimatedDeliveryTime(subscription.getPreferredTime());
        delivery.setStatus(preparingStatus);
        delivery.setCreatedAt(LocalDateTime.now());
        delivery.setStatusUpdatedAt(LocalDateTime.now());

        delivery = deliveryRepository.save(delivery);

        return mapToResponseDto(delivery, subscriptionMeals);
    }

    /**
     * Gets the current delivery for a user (today's date).
     * Returns 404 if no delivery found.
     * 
     * Requirements: 4.1, 4.4
     */
    public DeliveryResponseDto getCurrentDelivery(Integer userId) {
        LocalDate today = LocalDate.now();
        
        Delivery delivery = deliveryRepository
                .findBySubscriptionMeal_Subscription_User_UserIdAndSubscriptionMeal_DeliveryDate(userId, today)
                .orElseThrow(() -> new ResourceNotFoundException("No active delivery found for today"));

        // Get all subscription meals for this delivery date
        List<SubscriptionMeal> subscriptionMeals = subscriptionMealRepository
                .findBySubscription_SubscriptionIdAndDeliveryDate(
                        delivery.getSubscriptionMeal().getSubscription().getSubscriptionId(),
                        today
                );

        return mapToResponseDto(delivery, subscriptionMeals);
    }

    /**
     * Gets delivery by ID.
     * Verifies delivery ownership or admin role.
     * Includes associated meals.
     * 
     * Requirements: 4.2, 4.3
     */
    @SuppressWarnings("null")
    public DeliveryResponseDto getDeliveryById(Integer deliveryId, Integer userId, boolean isAdmin) {
        if (deliveryId == null) {
            throw new ValidationException("Delivery ID is required");
        }
        
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery not found with ID: " + deliveryId));

        // Verify ownership
        Integer deliveryOwnerId = delivery.getSubscriptionMeal().getSubscription().getUser().getUserId();
        if (!isAdmin && !deliveryOwnerId.equals(userId)) {
            throw new ForbiddenException("You do not have permission to access this delivery");
        }

        // Get all subscription meals for this delivery date
        LocalDate deliveryDate = delivery.getSubscriptionMeal().getDeliveryDate();
        Integer subscriptionId = delivery.getSubscriptionMeal().getSubscription().getSubscriptionId();
        
        List<SubscriptionMeal> subscriptionMeals = subscriptionMealRepository
                .findBySubscription_SubscriptionIdAndDeliveryDate(subscriptionId, deliveryDate);

        return mapToResponseDto(delivery, subscriptionMeals);
    }

    /**
     * Gets delivery history for a user.
     * Orders by date descending, supports pagination and filtering.
     * 
     * Requirements: 4.5, 10.1, 10.3, 10.5
     */
    @SuppressWarnings("null")
    public Page<DeliveryHistoryDto> getDeliveryHistory(Integer userId, LocalDate startDate, 
                                                       LocalDate endDate, String status, 
                                                       Pageable pageable) {
        // Get all deliveries for user ordered by date descending
        List<Delivery> allDeliveries = deliveryRepository
                .findBySubscriptionMeal_Subscription_User_UserIdOrderBySubscriptionMeal_DeliveryDateDesc(
                        userId, Pageable.unpaged());

        // Apply filters
        List<Delivery> filteredDeliveries = allDeliveries.stream()
                .filter(delivery -> {
                    LocalDate deliveryDate = delivery.getSubscriptionMeal().getDeliveryDate();
                    
                    // Date range filter
                    if (startDate != null && deliveryDate.isBefore(startDate)) {
                        return false;
                    }
                    if (endDate != null && deliveryDate.isAfter(endDate)) {
                        return false;
                    }
                    
                    // Status filter
                    if (status != null && !status.isEmpty() && 
                        !delivery.getStatus().getStatusName().equalsIgnoreCase(status)) {
                        return false;
                    }
                    
                    return true;
                })
                .collect(Collectors.toList());

        // Apply pagination
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), filteredDeliveries.size());
        
        List<DeliveryHistoryDto> pageContent;
        if (start < filteredDeliveries.size()) {
            pageContent = filteredDeliveries.subList(start, end).stream()
                    .map(this::mapToHistoryDto)
                    .collect(Collectors.toCollection(ArrayList::new));
        } else {
            pageContent = new ArrayList<>();
        }

        return new PageImpl<>(pageContent, pageable, filteredDeliveries.size());
    }

    /**
     * Updates delivery preferences (time and/or address).
     * Verifies delivery ownership, validates delivery is in PREPARING status,
     * validates time format and address, updates delivery time and/or address,
     * and recalculates status progression timing.
     * 
     * Requirements: 8.1, 8.2, 8.3, 8.4, 8.5
     */
    @SuppressWarnings("null")
    public DeliveryResponseDto updateDeliveryPreferences(Integer deliveryId, Integer userId, UpdateDeliveryDto dto) {
        // Validate at least one field is provided (Requirement 8.4)
        if (dto.getDeliveryTime() == null && dto.getAddress() == null) {
            throw new ValidationException("At least one field (deliveryTime or address) must be provided");
        }

        // Get delivery
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery not found"));

        // Verify ownership (Requirement 8.1, 8.2)
        Integer deliveryOwnerId = delivery.getSubscriptionMeal().getSubscription().getUser().getUserId();
        if (!deliveryOwnerId.equals(userId)) {
            throw new ForbiddenException("You do not have permission to update this delivery");
        }

        // Validate delivery is in PREPARING status (Requirement 8.3)
        String currentStatus = delivery.getStatus().getStatusName();
        if (!"PREPARING".equals(currentStatus)) {
            if ("SHIPPED".equals(currentStatus)) {
                throw new ValidationException("Cannot update delivery preferences. Delivery has already shipped");
            } else if ("DELIVERED".equals(currentStatus)) {
                throw new ValidationException("Cannot update delivery preferences. Delivery has already been delivered");
            } else if ("CONFIRMED".equals(currentStatus)) {
                throw new ValidationException("Cannot update delivery preferences. Delivery has been confirmed");
            } else {
                throw new ValidationException("Cannot update delivery preferences. Current status: " + currentStatus);
            }
        }

        // Validate and update delivery time if provided (Requirement 8.1, 8.4)
        if (dto.getDeliveryTime() != null) {
            // Time format validation is handled by @JsonFormat annotation
            // Additional validation: ensure time is reasonable (between 6 AM and 11 PM)
            LocalTime newTime = dto.getDeliveryTime();
            if (newTime.isBefore(LocalTime.of(6, 0)) || newTime.isAfter(LocalTime.of(23, 0))) {
                throw new ValidationException("Delivery time must be between 06:00 and 23:00");
            }
            delivery.setDeliveryTime(newTime);
            delivery.setEstimatedDeliveryTime(newTime);
        }

        // Validate and update address if provided (Requirement 8.2, 8.4)
        if (dto.getAddress() != null) {
            String trimmedAddress = dto.getAddress().trim();
            if (trimmedAddress.isEmpty()) {
                throw new ValidationException("Address cannot be empty");
            }
            delivery.setAddress(trimmedAddress);
        }

        // Save updated delivery
        delivery = deliveryRepository.save(delivery);

        // Get all subscription meals for this delivery date
        LocalDate deliveryDate = delivery.getSubscriptionMeal().getDeliveryDate();
        Integer subscriptionId = delivery.getSubscriptionMeal().getSubscription().getSubscriptionId();
        
        List<SubscriptionMeal> subscriptionMeals = subscriptionMealRepository
                .findBySubscription_SubscriptionIdAndDeliveryDate(subscriptionId, deliveryDate);

        return mapToResponseDto(delivery, subscriptionMeals);
    }

    /**
     * Confirms delivery receipt by user.
     * Verifies delivery ownership, validates delivery is in DELIVERED status,
     * updates status to CONFIRMED, sets confirmedAt timestamp,
     * and handles idempotent confirmations.
     * 
     * Requirements: 5.1, 5.2, 5.3, 5.4, 5.5
     */
    @SuppressWarnings("null")
    public DeliveryResponseDto confirmDelivery(Integer deliveryId, Integer userId) {
        // Get delivery
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery not found"));

        // Verify ownership (Requirement 5.3)
        Integer deliveryOwnerId = delivery.getSubscriptionMeal().getSubscription().getUser().getUserId();
        if (!deliveryOwnerId.equals(userId)) {
            throw new ForbiddenException("You do not have permission to confirm this delivery");
        }

        String currentStatus = delivery.getStatus().getStatusName();

        // Handle idempotent confirmations (Requirement 5.5)
        if ("CONFIRMED".equals(currentStatus)) {
            // Already confirmed, return current state without error
            LocalDate deliveryDate = delivery.getSubscriptionMeal().getDeliveryDate();
            Integer subscriptionId = delivery.getSubscriptionMeal().getSubscription().getSubscriptionId();
            
            List<SubscriptionMeal> subscriptionMeals = subscriptionMealRepository
                    .findBySubscription_SubscriptionIdAndDeliveryDate(subscriptionId, deliveryDate);

            return mapToResponseDto(delivery, subscriptionMeals);
        }

        // Validate delivery is in DELIVERED status (Requirement 5.2)
        if (!"DELIVERED".equals(currentStatus)) {
            if ("PREPARING".equals(currentStatus)) {
                throw new ValidationException("Cannot confirm delivery. Delivery must be in 'Delivered' status (currently being prepared)");
            } else if ("SHIPPED".equals(currentStatus)) {
                throw new ValidationException("Cannot confirm delivery. Delivery must be in 'Delivered' status (currently in transit)");
            } else {
                throw new ValidationException("Cannot confirm delivery. Delivery must be in 'Delivered' status. Current status: " + currentStatus);
            }
        }

        // Get CONFIRMED status
        DeliveryStatus confirmedStatus = deliveryStatusRepository.findByStatusName("CONFIRMED")
                .orElseThrow(() -> new ValidationException("CONFIRMED status not found in system"));

        // Update status to CONFIRMED and set confirmedAt timestamp (Requirements 5.1, 5.4)
        delivery.setStatus(confirmedStatus);
        delivery.setConfirmedAt(LocalDateTime.now());
        delivery.setStatusUpdatedAt(LocalDateTime.now());

        delivery = deliveryRepository.save(delivery);

        // Get all subscription meals for this delivery date
        LocalDate deliveryDate = delivery.getSubscriptionMeal().getDeliveryDate();
        Integer subscriptionId = delivery.getSubscriptionMeal().getSubscription().getSubscriptionId();
        
        List<SubscriptionMeal> subscriptionMeals = subscriptionMealRepository
                .findBySubscription_SubscriptionIdAndDeliveryDate(subscriptionId, deliveryDate);

        return mapToResponseDto(delivery, subscriptionMeals);
    }

    /**
     * Gets all deliveries with filtering for admin.
     * Supports filtering by status, date, and user.
     * 
     * Requirements: 9.1, 9.5
     */
    @SuppressWarnings("null")
    public Page<AdminDeliveryDto> getAllDeliveriesForAdmin(String status, LocalDate date, 
                                                           Integer userId, String userEmail,
                                                           Pageable pageable) {
        List<Delivery> allDeliveries = deliveryRepository.findAll();

        // Apply filters
        List<Delivery> filteredDeliveries = allDeliveries.stream()
                .filter(delivery -> {
                    // Status filter
                    if (status != null && !status.isEmpty() && 
                        !delivery.getStatus().getStatusName().equalsIgnoreCase(status)) {
                        return false;
                    }
                    
                    // Date filter
                    if (date != null) {
                        LocalDate deliveryDate = delivery.getSubscriptionMeal().getDeliveryDate();
                        if (!deliveryDate.equals(date)) {
                            return false;
                        }
                    }
                    
                    // User ID filter
                    if (userId != null) {
                        Integer deliveryUserId = delivery.getSubscriptionMeal()
                                .getSubscription().getUser().getUserId();
                        if (!deliveryUserId.equals(userId)) {
                            return false;
                        }
                    }
                    
                    // User email filter
                    if (userEmail != null && !userEmail.isEmpty()) {
                        String deliveryUserEmail = delivery.getSubscriptionMeal()
                                .getSubscription().getUser().getEmail();
                        if (!deliveryUserEmail.toLowerCase().contains(userEmail.toLowerCase())) {
                            return false;
                        }
                    }
                    
                    return true;
                })
                .collect(Collectors.toList());

        // Apply pagination
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), filteredDeliveries.size());
        
        List<AdminDeliveryDto> pageContent;
        if (start < filteredDeliveries.size()) {
            pageContent = filteredDeliveries.subList(start, end).stream()
                    .map(this::mapToAdminDto)
                    .collect(Collectors.toCollection(ArrayList::new));
        } else {
            pageContent = new ArrayList<>();
        }

        return new PageImpl<>(pageContent, pageable, filteredDeliveries.size());
    }

    /**
     * Gets delivery by ID for admin with complete details including user info and status history.
     * 
     * Requirements: 9.2, 9.4
     */
    @SuppressWarnings("null")
    public AdminDeliveryDto getDeliveryByIdForAdmin(Integer deliveryId) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery not found"));

        return mapToAdminDtoWithHistory(delivery);
    }

    /**
     * Manually updates delivery status for admin.
     * Records admin action in history.
     * 
     * Requirements: 9.3
     */
    @SuppressWarnings("null")
    public AdminDeliveryDto updateDeliveryStatusByAdmin(Integer deliveryId, String newStatusName, Integer adminUserId) {
        // Get delivery
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery not found"));

        // Validate status
        DeliveryStatus newStatus = deliveryStatusRepository.findByStatusName(newStatusName)
                .orElseThrow(() -> new ValidationException("Invalid status: " + newStatusName));

        String oldStatus = delivery.getStatus().getStatusName();

        // Update status
        delivery.setStatus(newStatus);
        delivery.setStatusUpdatedAt(LocalDateTime.now());

        // If status is CONFIRMED, set confirmedAt
        if ("CONFIRMED".equals(newStatusName) && delivery.getConfirmedAt() == null) {
            delivery.setConfirmedAt(LocalDateTime.now());
        }

        delivery = deliveryRepository.save(delivery);

        // Record admin action in history
        User deliveryOwner = delivery.getSubscriptionMeal().getSubscription().getUser();
        History historyRecord = new History();
        historyRecord.setUser(deliveryOwner);
        historyRecord.setEventType("DELIVERY_STATUS_UPDATE");
        historyRecord.setDescription(String.format(
                "Admin manually updated delivery #%d status from %s to %s",
                deliveryId, oldStatus, newStatusName
        ));
        historyRecord.setEventTime(LocalDateTime.now());
        historyRepository.save(historyRecord);

        return mapToAdminDtoWithHistory(delivery);
    }

    // Helper methods for mapping entities to DTOs

    private DeliveryResponseDto mapToResponseDto(Delivery delivery, List<SubscriptionMeal> subscriptionMeals) {
        List<MealSummaryDto> meals = subscriptionMeals.stream()
                .map(sm -> new MealSummaryDto(
                        sm.getMeal().getMealId(),
                        sm.getMeal().getMealName(),
                        null // mealType not available in Meal model
                ))
                .collect(Collectors.toList());

        return new DeliveryResponseDto(
                delivery.getDeliveryId(),
                delivery.getSubscriptionMeal().getDeliveryDate(),
                delivery.getDeliveryTime(),
                delivery.getAddress(),
                delivery.getStatus().getStatusName(),
                delivery.getStatusUpdatedAt(),
                delivery.getConfirmedAt(),
                meals
        );
    }

    private DeliveryHistoryDto mapToHistoryDto(Delivery delivery) {
        // Count meals for this delivery date
        LocalDate deliveryDate = delivery.getSubscriptionMeal().getDeliveryDate();
        Integer subscriptionId = delivery.getSubscriptionMeal().getSubscription().getSubscriptionId();
        
        List<SubscriptionMeal> meals = subscriptionMealRepository
                .findBySubscription_SubscriptionIdAndDeliveryDate(subscriptionId, deliveryDate);

        boolean confirmed = delivery.getConfirmedAt() != null;

        return new DeliveryHistoryDto(
                delivery.getDeliveryId(),
                deliveryDate,
                delivery.getStatus().getStatusName(),
                delivery.getDeliveryTime(),
                confirmed,
                meals.size()
        );
    }

    private AdminDeliveryDto mapToAdminDto(Delivery delivery) {
        LocalDate deliveryDate = delivery.getSubscriptionMeal().getDeliveryDate();
        Integer subscriptionId = delivery.getSubscriptionMeal().getSubscription().getSubscriptionId();
        Subscription subscription = delivery.getSubscriptionMeal().getSubscription();
        User user = subscription.getUser();

        List<SubscriptionMeal> subscriptionMeals = subscriptionMealRepository
                .findBySubscription_SubscriptionIdAndDeliveryDate(subscriptionId, deliveryDate);

        List<MealSummaryDto> meals = subscriptionMeals.stream()
                .map(sm -> new MealSummaryDto(
                        sm.getMeal().getMealId(),
                        sm.getMeal().getMealName(),
                        null
                ))
                .collect(Collectors.toList());

        String planName = "N/A";
        if (subscription.getCustomPlan() != null && subscription.getCustomPlan().getCategory() != null) {
            planName = subscription.getCustomPlan().getCategory().getCategoryName();
        }

        return new AdminDeliveryDto(
                delivery.getDeliveryId(),
                deliveryDate,
                delivery.getDeliveryTime(),
                delivery.getAddress(),
                delivery.getStatus().getStatusName(),
                delivery.getCreatedAt(),
                delivery.getStatusUpdatedAt(),
                delivery.getConfirmedAt(),
                user.getUserId(),
                user.getEmail(),
                user.getFullName(),
                subscriptionId,
                planName,
                meals,
                null // statusHistory not included in list view
        );
    }

    private AdminDeliveryDto mapToAdminDtoWithHistory(Delivery delivery) {
        LocalDate deliveryDate = delivery.getSubscriptionMeal().getDeliveryDate();
        Integer subscriptionId = delivery.getSubscriptionMeal().getSubscription().getSubscriptionId();
        Subscription subscription = delivery.getSubscriptionMeal().getSubscription();
        User user = subscription.getUser();

        List<SubscriptionMeal> subscriptionMeals = subscriptionMealRepository
                .findBySubscription_SubscriptionIdAndDeliveryDate(subscriptionId, deliveryDate);

        List<MealSummaryDto> meals = subscriptionMeals.stream()
                .map(sm -> new MealSummaryDto(
                        sm.getMeal().getMealId(),
                        sm.getMeal().getMealName(),
                        null
                ))
                .collect(Collectors.toList());

        // Build status history from delivery status changes
        List<StatusHistoryDto> statusHistory = new ArrayList<>();
        
        // Add creation event
        if (delivery.getCreatedAt() != null) {
            statusHistory.add(new StatusHistoryDto(
                    "PREPARING",
                    delivery.getCreatedAt(),
                    "System"
            ));
        }

        // Get relevant history events for this delivery
        List<History> userHistory = historyRepository
                .findByUser_UserIdOrderByEventTimeDesc(user.getUserId());
        
        for (History h : userHistory) {
            if (h.getDescription() != null && 
                h.getDescription().contains("delivery #" + delivery.getDeliveryId())) {
                // Extract status from description if it's a status update
                String status = extractStatusFromDescription(h.getDescription());
                if (status != null) {
                    statusHistory.add(new StatusHistoryDto(
                            status,
                            h.getEventTime(),
                            "Admin"
                    ));
                }
            }
        }

        // Add current status if different from creation
        if (delivery.getStatusUpdatedAt() != null && 
            !delivery.getStatus().getStatusName().equals("PREPARING")) {
            statusHistory.add(new StatusHistoryDto(
                    delivery.getStatus().getStatusName(),
                    delivery.getStatusUpdatedAt(),
                    delivery.getConfirmedAt() != null ? "User" : "System"
            ));
        }

        String planName = "N/A";
        if (subscription.getCustomPlan() != null && subscription.getCustomPlan().getCategory() != null) {
            planName = subscription.getCustomPlan().getCategory().getCategoryName();
        }

        return new AdminDeliveryDto(
                delivery.getDeliveryId(),
                deliveryDate,
                delivery.getDeliveryTime(),
                delivery.getAddress(),
                delivery.getStatus().getStatusName(),
                delivery.getCreatedAt(),
                delivery.getStatusUpdatedAt(),
                delivery.getConfirmedAt(),
                user.getUserId(),
                user.getEmail(),
                user.getFullName(),
                subscriptionId,
                planName,
                meals,
                statusHistory
        );
    }

    private String extractStatusFromDescription(String description) {
        // Extract status from description like "Admin manually updated delivery #1 status from PREPARING to SHIPPED"
        if (description.contains(" to ")) {
            String[] parts = description.split(" to ");
            if (parts.length > 1) {
                return parts[1].trim();
            }
        }
        return null;
    }
}
