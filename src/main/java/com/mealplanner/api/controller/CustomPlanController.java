package com.mealplanner.api.controller;

import com.mealplanner.api.dto.*;
import com.mealplanner.api.security.CustomUserDetails;
import com.mealplanner.api.service.CustomPlanService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/plans")
public class CustomPlanController {

    private final CustomPlanService customPlanService;

    public CustomPlanController(CustomPlanService customPlanService) {
        this.customPlanService = customPlanService;
    }

    /**
     * GET /api/plans/categories - List all categories (public)
     */
    @GetMapping("/categories")
    public ResponseEntity<List<PlanCategoryDto>> getAllCategories() {
        List<PlanCategoryDto> categories = customPlanService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    /**
     * GET /api/plans - List plans with pagination and category filter (public)
     */
    @GetMapping
    public ResponseEntity<Page<CustomPlanResponseDto>> getPlans(
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<CustomPlanResponseDto> plans = customPlanService.getPlans(categoryId, pageable);
        
        return ResponseEntity.ok(plans);
    }

    /**
     * GET /api/plans/{id} - Get plan details (public)
     */
    @GetMapping("/{id}")
    public ResponseEntity<CustomPlanDetailDto> getPlanById(@PathVariable Integer id) {
        CustomPlanDetailDto plan = customPlanService.getPlanById(id);
        return ResponseEntity.ok(plan);
    }

    /**
     * POST /api/plans - Create custom plan (authenticated)
     */
    @PostMapping
    public ResponseEntity<CustomPlanResponseDto> createPlan(@Valid @RequestBody CreateCustomPlanDto createDto) {
        Integer userId = getCurrentUserId();
        CustomPlanResponseDto plan = customPlanService.createPlan(userId, createDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(plan);
    }

    /**
     * PUT /api/plans/{id} - Update plan (owner or admin)
     */
    @PutMapping("/{id}")
    public ResponseEntity<CustomPlanResponseDto> updatePlan(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateCustomPlanDto updateDto) {
        
        Integer userId = getCurrentUserId();
        boolean isAdmin = isCurrentUserAdmin();
        
        CustomPlanResponseDto plan;
        if (isAdmin) {
            plan = customPlanService.adminUpdatePlan(id, updateDto);
        } else {
            plan = customPlanService.updatePlan(id, userId, updateDto);
        }
        
        return ResponseEntity.ok(plan);
    }

    /**
     * DELETE /api/plans/{id} - Delete plan (owner or admin)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlan(@PathVariable Integer id) {
        Integer userId = getCurrentUserId();
        boolean isAdmin = isCurrentUserAdmin();
        
        if (isAdmin) {
            customPlanService.adminDeletePlan(id);
        } else {
            customPlanService.deletePlan(id, userId, false);
        }
        
        return ResponseEntity.noContent().build();
    }

    /**
     * POST /api/plans/{id}/meals - Add meals to plan (owner or admin)
     */
    @PostMapping("/{id}/meals")
    public ResponseEntity<Void> addMealsToPlan(
            @PathVariable Integer id,
            @Valid @RequestBody AddMealsToPlanDto addMealsDto) {
        
        Integer userId = getCurrentUserId();
        customPlanService.addMealsToPlan(id, userId, addMealsDto.getMealIds());
        
        return ResponseEntity.ok().build();
    }

    /**
     * DELETE /api/plans/{id}/meals/{mealId} - Remove meal from plan (owner or admin)
     */
    @DeleteMapping("/{id}/meals/{mealId}")
    public ResponseEntity<Void> removeMealFromPlan(
            @PathVariable Integer id,
            @PathVariable Integer mealId) {
        
        Integer userId = getCurrentUserId();
        customPlanService.removeMealFromPlan(id, userId, mealId);
        
        return ResponseEntity.noContent().build();
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
