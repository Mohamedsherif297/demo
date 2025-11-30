package com.mealplanner.api.controller;

import com.mealplanner.api.dto.DeliveryHistoryDto;
import com.mealplanner.api.dto.DeliveryResponseDto;
import com.mealplanner.api.dto.UpdateDeliveryDto;
import com.mealplanner.api.security.CustomUserDetails;
import com.mealplanner.api.service.DeliveryService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * REST controller for user delivery operations.
 * Provides endpoints for tracking deliveries, viewing history, updating preferences, and confirming receipt.
 */
@RestController
@RequestMapping("/api/deliveries")
public class DeliveryController {

    private final DeliveryService deliveryService;

    public DeliveryController(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    /**
     * GET /api/deliveries/current - Get current day's delivery
     * Requirements: 7.1, 4.1
     */
    @GetMapping("/current")
    public ResponseEntity<DeliveryResponseDto> getCurrentDelivery() {
        Integer userId = getCurrentUserId();
        DeliveryResponseDto delivery = deliveryService.getCurrentDelivery(userId);
        return ResponseEntity.ok(delivery);
    }

    /**
     * GET /api/deliveries/{id} - Get specific delivery by ID
     * Requirements: 7.2, 4.2
     */
    @GetMapping("/{id}")
    public ResponseEntity<DeliveryResponseDto> getDeliveryById(@PathVariable Integer id) {
        Integer userId = getCurrentUserId();
        boolean isAdmin = isCurrentUserAdmin();
        DeliveryResponseDto delivery = deliveryService.getDeliveryById(id, userId, isAdmin);
        return ResponseEntity.ok(delivery);
    }

    /**
     * GET /api/deliveries/history - Get delivery history with pagination and filtering
     * Requirements: 7.4, 4.5, 10.1
     */
    @GetMapping("/history")
    public ResponseEntity<Page<DeliveryHistoryDto>> getDeliveryHistory(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Integer userId = getCurrentUserId();
        Pageable pageable = PageRequest.of(page, size);
        Page<DeliveryHistoryDto> history = deliveryService.getDeliveryHistory(
                userId, startDate, endDate, status, pageable);
        
        return ResponseEntity.ok(history);
    }

    /**
     * PATCH /api/deliveries/{id} - Update delivery preferences (time and/or address)
     * Requirements: 8.1, 8.2, 8.3
     */
    @PatchMapping("/{id}")
    public ResponseEntity<DeliveryResponseDto> updateDeliveryPreferences(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateDeliveryDto updateDto) {
        
        Integer userId = getCurrentUserId();
        DeliveryResponseDto delivery = deliveryService.updateDeliveryPreferences(id, userId, updateDto);
        return ResponseEntity.ok(delivery);
    }

    /**
     * POST /api/deliveries/{id}/confirm - Confirm delivery receipt
     * Requirements: 7.3, 5.1
     */
    @PostMapping("/{id}/confirm")
    public ResponseEntity<DeliveryResponseDto> confirmDelivery(@PathVariable Integer id) {
        Integer userId = getCurrentUserId();
        DeliveryResponseDto delivery = deliveryService.confirmDelivery(id, userId);
        return ResponseEntity.ok(delivery);
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
