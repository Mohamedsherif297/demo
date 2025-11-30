package com.mealplanner.api.controller;

import com.mealplanner.api.dto.AdminDeliveryDto;
import com.mealplanner.api.dto.UpdateDeliveryStatusDto;
import com.mealplanner.api.security.CustomUserDetails;
import com.mealplanner.api.service.DeliveryService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * REST controller for admin delivery operations.
 * Provides endpoints for monitoring and managing all deliveries in the system.
 * All endpoints require ADMIN role.
 */
@RestController
@RequestMapping("/api/admin/deliveries")
@PreAuthorize("hasRole('ADMIN')")
public class AdminDeliveryController {

    private final DeliveryService deliveryService;

    public AdminDeliveryController(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    /**
     * GET /api/admin/deliveries - List all deliveries with filtering and pagination
     * Requirements: 9.1, 9.5
     */
    @GetMapping
    public ResponseEntity<Page<AdminDeliveryDto>> getAllDeliveries(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) String userEmail,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<AdminDeliveryDto> deliveries = deliveryService.getAllDeliveriesForAdmin(
                status, date, userId, userEmail, pageable);
        
        return ResponseEntity.ok(deliveries);
    }

    /**
     * GET /api/admin/deliveries/{id} - Get complete delivery details with user info and status history
     * Requirements: 9.2, 9.4
     */
    @GetMapping("/{id}")
    public ResponseEntity<AdminDeliveryDto> getDeliveryById(@PathVariable Integer id) {
        AdminDeliveryDto delivery = deliveryService.getDeliveryByIdForAdmin(id);
        return ResponseEntity.ok(delivery);
    }

    /**
     * PATCH /api/admin/deliveries/{id}/status - Manually update delivery status
     * Requirements: 9.3
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<AdminDeliveryDto> updateDeliveryStatus(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateDeliveryStatusDto statusUpdate) {
        
        Integer adminUserId = getCurrentUserId();
        AdminDeliveryDto delivery = deliveryService.updateDeliveryStatusByAdmin(
                id, statusUpdate.getStatus().toUpperCase(), adminUserId);
        
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
}
