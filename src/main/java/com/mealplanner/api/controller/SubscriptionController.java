package com.mealplanner.api.controller;

import com.mealplanner.api.dto.*;
import com.mealplanner.api.security.CustomUserDetails;
import com.mealplanner.api.service.SubscriptionService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    /**
     * POST /api/subscriptions - Create subscription (authenticated)
     */
    @PostMapping("/api/subscriptions")
    public ResponseEntity<SubscriptionResponseDto> createSubscription(
            @Valid @RequestBody CreateSubscriptionDto createDto) {
        
        Integer userId = getCurrentUserId();
        SubscriptionResponseDto subscription = subscriptionService.createSubscription(userId, createDto);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(subscription);
    }

    /**
     * GET /api/subscriptions - List user's subscriptions (authenticated)
     */
    @GetMapping("/api/subscriptions")
    public ResponseEntity<Page<SubscriptionResponseDto>> getUserSubscriptions(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Integer userId = getCurrentUserId();
        Pageable pageable = PageRequest.of(page, size);
        Page<SubscriptionResponseDto> subscriptions = subscriptionService.getUserSubscriptions(userId, status, pageable);
        
        return ResponseEntity.ok(subscriptions);
    }

    /**
     * GET /api/subscriptions/{id} - Get subscription details (owner or admin)
     */
    @GetMapping("/api/subscriptions/{id}")
    public ResponseEntity<SubscriptionDetailDto> getSubscriptionById(@PathVariable Integer id) {
        Integer userId = getCurrentUserId();
        boolean isAdmin = isCurrentUserAdmin();
        
        SubscriptionDetailDto subscription = subscriptionService.getSubscriptionById(id, userId, isAdmin);
        return ResponseEntity.ok(subscription);
    }

    /**
     * PATCH /api/subscriptions/{id}/pause - Pause subscription (owner)
     */
    @PatchMapping("/api/subscriptions/{id}/pause")
    public ResponseEntity<Void> pauseSubscription(@PathVariable Integer id) {
        Integer userId = getCurrentUserId();
        subscriptionService.pauseSubscription(id, userId);
        return ResponseEntity.ok().build();
    }

    /**
     * PATCH /api/subscriptions/{id}/resume - Resume subscription (owner)
     */
    @PatchMapping("/api/subscriptions/{id}/resume")
    public ResponseEntity<Void> resumeSubscription(@PathVariable Integer id) {
        Integer userId = getCurrentUserId();
        subscriptionService.resumeSubscription(id, userId);
        return ResponseEntity.ok().build();
    }

    /**
     * PATCH /api/subscriptions/{id}/cancel - Cancel subscription (owner)
     */
    @PatchMapping("/api/subscriptions/{id}/cancel")
    public ResponseEntity<Void> cancelSubscription(@PathVariable Integer id) {
        Integer userId = getCurrentUserId();
        subscriptionService.cancelSubscription(id, userId);
        return ResponseEntity.ok().build();
    }

    /**
     * PATCH /api/subscriptions/{id}/preferences - Update subscription preferences (owner)
     */
    @PatchMapping("/api/subscriptions/{id}/preferences")
    public ResponseEntity<SubscriptionResponseDto> updateSubscriptionPreferences(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateSubscriptionPreferencesDto updateDto) {
        Integer userId = getCurrentUserId();
        SubscriptionResponseDto subscription = subscriptionService.updateSubscriptionPreferences(id, userId, updateDto);
        return ResponseEntity.ok(subscription);
    }

    /**
     * GET /api/subscriptions/{id}/meals - Get scheduled meals (owner or admin)
     */
    @GetMapping("/api/subscriptions/{id}/meals")
    public ResponseEntity<List<SubscriptionMealDto>> getScheduledMeals(
            @PathVariable Integer id,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        Integer userId = getCurrentUserId();
        boolean isAdmin = isCurrentUserAdmin();
        
        List<SubscriptionMealDto> meals = subscriptionService.getScheduledMeals(id, userId, startDate, endDate, isAdmin);
        return ResponseEntity.ok(meals);
    }

    /**
     * GET /api/admin/subscriptions - List all subscriptions (admin only)
     */
    @GetMapping("/api/admin/subscriptions")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<SubscriptionResponseDto>> getAllSubscriptions(
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<SubscriptionResponseDto> subscriptions = subscriptionService.getAllSubscriptions(userId, status, pageable);
        
        return ResponseEntity.ok(subscriptions);
    }

    /**
     * Helper method to extract current user ID from SecurityContext
     */
    private Integer getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            return userDetails.getUser().getUserId();
        }
        throw new RuntimeException("Unable to get current user");
    }

    /**
     * Helper method to check if current user is admin
     */
    private boolean isCurrentUserAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            return authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
        }
        return false;
    }
}
