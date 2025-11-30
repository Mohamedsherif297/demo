package com.mealplanner.api.service;

import com.mealplanner.api.dto.*;
import com.mealplanner.api.exception.ForbiddenException;
import com.mealplanner.api.exception.ResourceNotFoundException;
import com.mealplanner.api.exception.ValidationException;
import com.mealplanner.api.model.*;
import com.mealplanner.api.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class SubscriptionService {

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private CustomPlanRepository customPlanRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SubscriptionStatusRepository subscriptionStatusRepository;

    @Autowired
    private SubscriptionMealRepository subscriptionMealRepository;

    /**
     * Creates a new subscription for a user.
     * Validates that the plan exists and the start date is not in the past.
     * Sets initial status to "active".
     * 
     * Requirements: 7.1, 7.2, 7.3, 7.4
     */
    @SuppressWarnings("null")
    public SubscriptionResponseDto createSubscription(Integer userId, CreateSubscriptionDto dto) {
        // Validate plan exists
        CustomPlan customPlan = customPlanRepository.findById(dto.getCustomPlanId())
                .orElseThrow(() -> new ValidationException("Invalid plan ID: Plan does not exist"));

        // Validate start date is not in the past
        if (dto.getStartDate().isBefore(LocalDate.now())) {
            throw new ValidationException("Start date cannot be in the past");
        }

        // Validate preferred time (Requirement 1.2)
        if (dto.getPreferredTime() == null) {
            throw new ValidationException("Preferred delivery time is required");
        }
        
        // Validate time is reasonable (between 6 AM and 11 PM)
        LocalTime preferredTime = dto.getPreferredTime();
        if (preferredTime.isBefore(LocalTime.of(6, 0)) || preferredTime.isAfter(LocalTime.of(23, 0))) {
            throw new ValidationException("Preferred delivery time must be between 06:00 and 23:00");
        }

        // Get user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Get active status
        SubscriptionStatus activeStatus = subscriptionStatusRepository.findByStatusName("active")
                .orElseThrow(() -> new ValidationException("Active status not found in system"));

        // Create subscription
        Subscription subscription = new Subscription();
        subscription.setUser(user);
        subscription.setCustomPlan(customPlan);
        subscription.setStartDate(dto.getStartDate());
        subscription.setPreferredTime(dto.getPreferredTime());
        subscription.setStatus(activeStatus);

        subscription = subscriptionRepository.save(subscription);

        return mapToResponseDto(subscription);
    }

    /**
     * Gets all subscriptions for a user with optional status filtering.
     * 
     * Requirements: 8.1, 8.2
     */
    public Page<SubscriptionResponseDto> getUserSubscriptions(Integer userId, String status, Pageable pageable) {
        Page<Subscription> subscriptions;
        
        if (status != null && !status.isEmpty()) {
            subscriptions = subscriptionRepository.findByUserUserIdAndStatusStatusName(userId, status, pageable);
        } else {
            subscriptions = subscriptionRepository.findByUserUserId(userId, pageable);
        }

        return subscriptions.map(this::mapToResponseDto);
    }

    /**
     * Gets detailed subscription information by ID.
     * Verifies ownership unless user is admin.
     * 
     * Requirements: 8.3, 8.4
     */
    @SuppressWarnings("null")
    public SubscriptionDetailDto getSubscriptionById(Integer subscriptionId, Integer userId, boolean isAdmin) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found"));

        // Verify ownership
        if (!isAdmin && !subscription.getUser().getUserId().equals(userId)) {
            throw new ForbiddenException("You do not have permission to access this subscription");
        }

        return mapToDetailDto(subscription);
    }

    /**
     * Pauses an active subscription.
     * Verifies ownership.
     * 
     * Requirements: 9.1, 9.4
     */
    @SuppressWarnings("null")
    public void pauseSubscription(Integer subscriptionId, Integer userId) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found"));

        // Verify ownership
        if (!subscription.getUser().getUserId().equals(userId)) {
            throw new ForbiddenException("You do not have permission to modify this subscription");
        }

        // Get paused status
        SubscriptionStatus pausedStatus = subscriptionStatusRepository.findByStatusName("paused")
                .orElseThrow(() -> new ValidationException("Paused status not found in system"));

        subscription.setStatus(pausedStatus);
        subscriptionRepository.save(subscription);
    }

    /**
     * Resumes a paused subscription.
     * Verifies ownership.
     * 
     * Requirements: 9.2, 9.4
     */
    @SuppressWarnings("null")
    public void resumeSubscription(Integer subscriptionId, Integer userId) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found"));

        // Verify ownership
        if (!subscription.getUser().getUserId().equals(userId)) {
            throw new ForbiddenException("You do not have permission to modify this subscription");
        }

        // Get active status
        SubscriptionStatus activeStatus = subscriptionStatusRepository.findByStatusName("active")
                .orElseThrow(() -> new ValidationException("Active status not found in system"));

        subscription.setStatus(activeStatus);
        subscriptionRepository.save(subscription);
    }

    /**
     * Cancels a subscription.
     * Verifies ownership.
     * 
     * Requirements: 9.3, 9.4
     */
    @SuppressWarnings("null")
    public void cancelSubscription(Integer subscriptionId, Integer userId) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found"));

        // Verify ownership
        if (!subscription.getUser().getUserId().equals(userId)) {
            throw new ForbiddenException("You do not have permission to modify this subscription");
        }

        // Get cancelled status
        SubscriptionStatus cancelledStatus = subscriptionStatusRepository.findByStatusName("cancelled")
                .orElseThrow(() -> new ValidationException("Cancelled status not found in system"));

        subscription.setStatus(cancelledStatus);
        subscriptionRepository.save(subscription);
    }

    /**
     * Updates subscription preferences (preferred delivery time).
     * Verifies ownership.
     * 
     * Requirements: 1.4
     */
    @SuppressWarnings("null")
    public SubscriptionResponseDto updateSubscriptionPreferences(Integer subscriptionId, Integer userId, UpdateSubscriptionPreferencesDto dto) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found"));

        // Verify ownership
        if (!subscription.getUser().getUserId().equals(userId)) {
            throw new ForbiddenException("You do not have permission to modify this subscription");
        }

        // Validate preferred time (Requirement 1.2, 1.4)
        if (dto.getPreferredTime() == null) {
            throw new ValidationException("Preferred delivery time is required");
        }
        
        // Validate time is reasonable (between 6 AM and 11 PM)
        LocalTime preferredTime = dto.getPreferredTime();
        if (preferredTime.isBefore(LocalTime.of(6, 0)) || preferredTime.isAfter(LocalTime.of(23, 0))) {
            throw new ValidationException("Preferred delivery time must be between 06:00 and 23:00");
        }

        // Update preferred time
        subscription.setPreferredTime(dto.getPreferredTime());
        subscription = subscriptionRepository.save(subscription);

        return mapToResponseDto(subscription);
    }

    /**
     * Gets scheduled meals for a subscription within an optional date range.
     * Verifies ownership unless user is admin.
     * 
     * Requirements: 10.1, 10.2, 10.3
     */
    @SuppressWarnings("null")
    public List<SubscriptionMealDto> getScheduledMeals(Integer subscriptionId, Integer userId, 
                                                       LocalDate startDate, LocalDate endDate, boolean isAdmin) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found"));

        // Verify ownership
        if (!isAdmin && !subscription.getUser().getUserId().equals(userId)) {
            throw new ForbiddenException("You do not have permission to access this subscription");
        }

        List<SubscriptionMeal> meals = subscriptionMealRepository.findBySubscriptionIdAndDateRange(
                subscriptionId, startDate, endDate);

        return meals.stream()
                .map(this::mapToMealDto)
                .collect(Collectors.toList());
    }

    /**
     * Gets all subscriptions in the system for admin users.
     * Supports filtering by user and status.
     * 
     * Requirements: 11.1, 11.2, 11.3
     */
    @PreAuthorize("hasRole('ADMIN')")
    @SuppressWarnings("null")
    public Page<SubscriptionResponseDto> getAllSubscriptions(Integer userId, String status, Pageable pageable) {
        Page<Subscription> subscriptions;

        if (userId != null && status != null && !status.isEmpty()) {
            // Filter by both user and status
            subscriptions = subscriptionRepository.findByUserUserIdAndStatusStatusName(userId, status, pageable);
        } else if (userId != null) {
            // Filter by user only
            subscriptions = subscriptionRepository.findByUserUserId(userId, pageable);
        } else if (status != null && !status.isEmpty()) {
            // Filter by status only
            subscriptions = subscriptionRepository.findByStatusStatusName(status, pageable);
        } else {
            // No filters
            subscriptions = subscriptionRepository.findAll(pageable);
        }

        return subscriptions.map(this::mapToResponseDto);
    }

    // Helper methods for mapping entities to DTOs

    private SubscriptionResponseDto mapToResponseDto(Subscription subscription) {
        String planName = subscription.getCustomPlan().getCategory() != null 
                ? subscription.getCustomPlan().getCategory().getCategoryName() 
                : "Custom Plan";
        
        return new SubscriptionResponseDto(
                subscription.getSubscriptionId(),
                planName,
                subscription.getStartDate(),
                subscription.getStatus().getStatusName()
        );
    }

    private SubscriptionDetailDto mapToDetailDto(Subscription subscription) {
        CustomPlan plan = subscription.getCustomPlan();
        
        // Map custom plan to DTO
        CustomPlanResponseDto planDto = new CustomPlanResponseDto(
                plan.getCustomPlanId(),
                plan.getCategory() != null ? plan.getCategory().getCategoryName() : null,
                plan.getDurationMinutes(),
                plan.getPrice() != null ? BigDecimal.valueOf(plan.getPrice()) : null,
                plan.getCustomPlanMeals() != null ? plan.getCustomPlanMeals().size() : 0
        );

        String planName = plan.getCategory() != null 
                ? plan.getCategory().getCategoryName() 
                : "Custom Plan";

        return new SubscriptionDetailDto(
                subscription.getSubscriptionId(),
                planName,
                subscription.getStartDate(),
                subscription.getStatus().getStatusName(),
                planDto,
                subscription.getPreferredTime(),
                null // planTime is LocalTime in DTO but LocalDate in entity - needs clarification
        );
    }

    private SubscriptionMealDto mapToMealDto(SubscriptionMeal subscriptionMeal) {
        return new SubscriptionMealDto(
                subscriptionMeal.getMeal().getMealId(),
                subscriptionMeal.getMeal().getMealName(),
                subscriptionMeal.getDeliveryDate()
        );
    }
}
