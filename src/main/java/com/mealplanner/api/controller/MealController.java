package com.mealplanner.api.controller;

import com.mealplanner.api.dto.*;
import com.mealplanner.api.security.CustomUserDetails;
import com.mealplanner.api.service.MealService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/meals")
public class MealController {

    private final MealService mealService;

    public MealController(MealService mealService) {
        this.mealService = mealService;
    }

    /**
     * GET /api/meals - List meals with pagination and filters (public)
     */
    @GetMapping
    public ResponseEntity<Page<MealResponseDto>> getMeals(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Integer minRating,
            @RequestParam(required = false) String excludeAllergens,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        // Parse excludeAllergens from comma-separated string
        List<Integer> allergenIds = null;
        if (excludeAllergens != null && !excludeAllergens.trim().isEmpty()) {
            allergenIds = Arrays.stream(excludeAllergens.split(","))
                    .map(String::trim)
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<MealResponseDto> meals = mealService.getMeals(search, minRating, allergenIds, pageable);
        
        return ResponseEntity.ok(meals);
    }

    /**
     * GET /api/meals/{id} - Get meal details (public)
     */
    @GetMapping("/{id}")
    public ResponseEntity<MealDetailDto> getMealById(@PathVariable Integer id) {
        MealDetailDto meal = mealService.getMealById(id);
        return ResponseEntity.ok(meal);
    }

    /**
     * POST /api/meals/{id}/rate - Rate a meal (authenticated)
     */
    @PostMapping("/{id}/rate")
    public ResponseEntity<Void> rateMeal(
            @PathVariable Integer id,
            @Valid @RequestBody RateMealDto rateDto) {
        
        Integer userId = getCurrentUserId();
        mealService.rateMeal(id, userId, rateDto.getRating());
        
        return ResponseEntity.ok().build();
    }

    /**
     * POST /api/meals - Create meal (admin only)
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MealResponseDto> createMeal(@Valid @RequestBody CreateMealDto createDto) {
        MealResponseDto meal = mealService.createMeal(createDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(meal);
    }

    /**
     * PUT /api/meals/{id} - Update meal (admin only)
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MealResponseDto> updateMeal(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateMealDto updateDto) {
        
        MealResponseDto meal = mealService.updateMeal(id, updateDto);
        return ResponseEntity.ok(meal);
    }

    /**
     * DELETE /api/meals/{id} - Delete meal (admin only)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteMeal(@PathVariable Integer id) {
        mealService.deleteMeal(id);
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
}
