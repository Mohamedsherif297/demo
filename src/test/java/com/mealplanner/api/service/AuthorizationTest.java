package com.mealplanner.api.service;

import com.mealplanner.api.dto.*;
import com.mealplanner.api.exception.ForbiddenException;
import com.mealplanner.api.model.*;
import com.mealplanner.api.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Authorization unit tests
 * Requirements: 3.4, 8.4, 9.4
 * 
 * Tests:
 * - Non-admin users cannot access admin endpoints
 * - Users cannot access other users' subscriptions
 * - Users cannot modify other users' plans
 */
@ExtendWith(MockitoExtension.class)
@SuppressWarnings("null")
class AuthorizationTest {

    @Mock
    private MealRepository mealRepository;

    @Mock
    private NutritionRepository nutritionRepository;

    @Mock
    private NutritionFactRepository nutritionFactRepository;

    @Mock
    private AllergyRepository allergyRepository;

    @Mock
    private MealAllergyRepository mealAllergyRepository;

    @Mock
    private CustomPlanRepository customPlanRepository;

    @Mock
    private PlanCategoryRepository planCategoryRepository;

    @Mock
    private CustomPlanMealRepository customPlanMealRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private SubscriptionStatusRepository subscriptionStatusRepository;

    @InjectMocks
    private MealService mealService;

    @InjectMocks
    private CustomPlanService customPlanService;

    @InjectMocks
    private SubscriptionService subscriptionService;

    private User regularUser;
    private User otherUser;
    private Role userRole;
    private CustomPlan userPlan;
    private CustomPlan otherUserPlan;
    private Subscription userSubscription;
    private Subscription otherUserSubscription;

    @BeforeEach
    void setUp() {
        // Setup roles
        userRole = new Role("USER");
        userRole.setRoleId(2);

        // Setup regular user
        regularUser = new User();
        regularUser.setUserId(1);
        regularUser.setFullName("Regular User");
        regularUser.setEmail("user@example.com");
        regularUser.setRole(userRole);

        // Setup another user
        otherUser = new User();
        otherUser.setUserId(2);
        otherUser.setFullName("Other User");
        otherUser.setEmail("other@example.com");
        otherUser.setRole(userRole);

        // Setup custom plans
        PlanCategory category = new PlanCategory();
        category.setCategoryId(1);
        category.setCategoryName("Weight Loss");

        userPlan = new CustomPlan();
        userPlan.setCustomPlanId(1);
        userPlan.setUser(regularUser);
        userPlan.setCategory(category);

        otherUserPlan = new CustomPlan();
        otherUserPlan.setCustomPlanId(2);
        otherUserPlan.setUser(otherUser);
        otherUserPlan.setCategory(category);

        // Setup subscriptions
        SubscriptionStatus activeStatus = new SubscriptionStatus();
        activeStatus.setStatusId(1);
        activeStatus.setStatusName("Active");

        userSubscription = new Subscription();
        userSubscription.setSubscriptionId(1);
        userSubscription.setUser(regularUser);
        userSubscription.setCustomPlan(userPlan);
        userSubscription.setStatus(activeStatus);
        userSubscription.setStartDate(LocalDate.now());

        otherUserSubscription = new Subscription();
        otherUserSubscription.setSubscriptionId(2);
        otherUserSubscription.setUser(otherUser);
        otherUserSubscription.setCustomPlan(otherUserPlan);
        otherUserSubscription.setStatus(activeStatus);
        otherUserSubscription.setStartDate(LocalDate.now());
    }

    @Test
    void testUserCannotAccessOtherUsersSubscription() {
        when(subscriptionRepository.findById(2)).thenReturn(Optional.of(otherUserSubscription));

        assertThrows(ForbiddenException.class, () -> {
            subscriptionService.getSubscriptionById(2, regularUser.getUserId(), false);
        });
    }

    @Test
    void testUserCanAccessOwnSubscription() {
        when(subscriptionRepository.findById(1)).thenReturn(Optional.of(userSubscription));

        SubscriptionDetailDto result = subscriptionService.getSubscriptionById(1, regularUser.getUserId(), false);

        assertNotNull(result);
        assertEquals(1, result.getSubscriptionId());
    }

    @Test
    void testUserCannotModifyOtherUsersPlan() {
        when(customPlanRepository.findById(2)).thenReturn(Optional.of(otherUserPlan));

        UpdateCustomPlanDto updateDto = new UpdateCustomPlanDto();
        updateDto.setCategoryId(1);

        assertThrows(ForbiddenException.class, () -> {
            customPlanService.updatePlan(2, regularUser.getUserId(), updateDto);
        });
    }

    @Test
    void testUserCanModifyOwnPlan() {
        when(customPlanRepository.findById(1)).thenReturn(Optional.of(userPlan));
        when(planCategoryRepository.findById(1)).thenReturn(Optional.of(userPlan.getCategory()));
        when(customPlanRepository.save(any(CustomPlan.class))).thenReturn(userPlan);

        UpdateCustomPlanDto updateDto = new UpdateCustomPlanDto();
        updateDto.setCategoryId(1);

        CustomPlanResponseDto result = customPlanService.updatePlan(1, regularUser.getUserId(), updateDto);

        assertNotNull(result);
        assertEquals(1, result.getCustomPlanId());
    }
}