package com.mealplanner.api.service;

import com.mealplanner.api.dto.*;
import com.mealplanner.api.exception.ForbiddenException;
import com.mealplanner.api.exception.ResourceNotFoundException;
import com.mealplanner.api.exception.ValidationException;
import com.mealplanner.api.model.*;
import com.mealplanner.api.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("null")
class SubscriptionServiceTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private CustomPlanRepository customPlanRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SubscriptionStatusRepository subscriptionStatusRepository;

    @Mock
    private SubscriptionMealRepository subscriptionMealRepository;

    @InjectMocks
    private SubscriptionService subscriptionService;

    private User testUser;
    private CustomPlan testPlan;
    private PlanCategory testCategory;
    private SubscriptionStatus activeStatus;
    private SubscriptionStatus pausedStatus;
    private SubscriptionStatus cancelledStatus;
    private Subscription testSubscription;

    @BeforeEach
    void setUp() {
        // Setup test user
        testUser = new User();
        testUser.setUserId(1);
        testUser.setEmail("test@example.com");

        // Setup test category
        testCategory = new PlanCategory();
        testCategory.setCategoryId(1);
        testCategory.setCategoryName("Weight Loss");

        // Setup test plan
        testPlan = new CustomPlan();
        testPlan.setCustomPlanId(1);
        testPlan.setUser(testUser);
        testPlan.setCategory(testCategory);
        testPlan.setDurationMinutes(30);
        testPlan.setPrice(99.99);
        testPlan.setCustomPlanMeals(new HashSet<>());

        // Setup subscription statuses
        activeStatus = new SubscriptionStatus();
        activeStatus.setStatusId(1);
        activeStatus.setStatusName("active");

        pausedStatus = new SubscriptionStatus();
        pausedStatus.setStatusId(2);
        pausedStatus.setStatusName("paused");

        cancelledStatus = new SubscriptionStatus();
        cancelledStatus.setStatusId(3);
        cancelledStatus.setStatusName("cancelled");

        // Setup test subscription
        testSubscription = new Subscription();
        testSubscription.setSubscriptionId(1);
        testSubscription.setUser(testUser);
        testSubscription.setCustomPlan(testPlan);
        testSubscription.setStartDate(LocalDate.now().plusDays(1));
        testSubscription.setPreferredTime(LocalTime.of(12, 0));
        testSubscription.setStatus(activeStatus);
    }

    @Test
    void createSubscription_WithValidData_CreatesSubscription() {
        // Arrange
        CreateSubscriptionDto dto = new CreateSubscriptionDto(1, LocalDate.now().plusDays(1), LocalTime.of(12, 0));
        
        when(customPlanRepository.findById(1)).thenReturn(Optional.of(testPlan));
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(subscriptionStatusRepository.findByStatusName("active")).thenReturn(Optional.of(activeStatus));
        when(subscriptionRepository.save(any(Subscription.class))).thenReturn(testSubscription);

        // Act
        SubscriptionResponseDto result = subscriptionService.createSubscription(1, dto);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getSubscriptionId());
        assertEquals("Weight Loss", result.getPlanName());
        assertEquals("active", result.getStatus());
        verify(subscriptionRepository).save(any(Subscription.class));
    }

    @Test
    void createSubscription_WithInvalidPlanId_ThrowsValidationException() {
        // Arrange
        CreateSubscriptionDto dto = new CreateSubscriptionDto(999, LocalDate.now().plusDays(1), LocalTime.of(12, 0));
        when(customPlanRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ValidationException.class, () -> subscriptionService.createSubscription(1, dto));
    }

    @Test
    void createSubscription_WithPastStartDate_ThrowsValidationException() {
        // Arrange
        CreateSubscriptionDto dto = new CreateSubscriptionDto(1, LocalDate.now().minusDays(1), LocalTime.of(12, 0));
        when(customPlanRepository.findById(1)).thenReturn(Optional.of(testPlan));

        // Act & Assert
        assertThrows(ValidationException.class, () -> subscriptionService.createSubscription(1, dto));
    }

    @Test
    void getUserSubscriptions_WithNoFilter_ReturnsAllUserSubscriptions() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        List<Subscription> subscriptions = List.of(testSubscription);
        Page<Subscription> subscriptionPage = new PageImpl<>(subscriptions, pageable, 1);

        when(subscriptionRepository.findByUserUserId(1, pageable)).thenReturn(subscriptionPage);

        // Act
        Page<SubscriptionResponseDto> result = subscriptionService.getUserSubscriptions(1, null, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Weight Loss", result.getContent().get(0).getPlanName());
        verify(subscriptionRepository).findByUserUserId(1, pageable);
    }

    @Test
    void getUserSubscriptions_WithStatusFilter_ReturnsFilteredSubscriptions() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        List<Subscription> subscriptions = List.of(testSubscription);
        Page<Subscription> subscriptionPage = new PageImpl<>(subscriptions, pageable, 1);

        when(subscriptionRepository.findByUserUserIdAndStatusStatusName(1, "active", pageable))
                .thenReturn(subscriptionPage);

        // Act
        Page<SubscriptionResponseDto> result = subscriptionService.getUserSubscriptions(1, "active", pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(subscriptionRepository).findByUserUserIdAndStatusStatusName(1, "active", pageable);
    }

    @Test
    void getSubscriptionById_WithValidIdAndOwner_ReturnsSubscriptionDetail() {
        // Arrange
        when(subscriptionRepository.findById(1)).thenReturn(Optional.of(testSubscription));

        // Act
        SubscriptionDetailDto result = subscriptionService.getSubscriptionById(1, 1, false);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getSubscriptionId());
        assertEquals("Weight Loss", result.getPlanName());
        assertNotNull(result.getCustomPlan());
        verify(subscriptionRepository).findById(1);
    }

    @Test
    void getSubscriptionById_WithNonOwner_ThrowsForbiddenException() {
        // Arrange
        when(subscriptionRepository.findById(1)).thenReturn(Optional.of(testSubscription));

        // Act & Assert
        assertThrows(ForbiddenException.class, 
                () -> subscriptionService.getSubscriptionById(1, 999, false));
    }

    @Test
    void getSubscriptionById_WithInvalidId_ThrowsResourceNotFoundException() {
        // Arrange
        when(subscriptionRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, 
                () -> subscriptionService.getSubscriptionById(999, 1, false));
    }

    @Test
    void pauseSubscription_WithValidIdAndOwner_PausesSubscription() {
        // Arrange
        when(subscriptionRepository.findById(1)).thenReturn(Optional.of(testSubscription));
        when(subscriptionStatusRepository.findByStatusName("paused")).thenReturn(Optional.of(pausedStatus));
        when(subscriptionRepository.save(any(Subscription.class))).thenReturn(testSubscription);

        // Act
        subscriptionService.pauseSubscription(1, 1);

        // Assert
        verify(subscriptionRepository).save(any(Subscription.class));
        assertEquals(pausedStatus, testSubscription.getStatus());
    }

    @Test
    void pauseSubscription_WithNonOwner_ThrowsForbiddenException() {
        // Arrange
        when(subscriptionRepository.findById(1)).thenReturn(Optional.of(testSubscription));

        // Act & Assert
        assertThrows(ForbiddenException.class, 
                () -> subscriptionService.pauseSubscription(1, 999));
    }

    @Test
    void resumeSubscription_WithValidIdAndOwner_ResumesSubscription() {
        // Arrange
        testSubscription.setStatus(pausedStatus);
        when(subscriptionRepository.findById(1)).thenReturn(Optional.of(testSubscription));
        when(subscriptionStatusRepository.findByStatusName("active")).thenReturn(Optional.of(activeStatus));
        when(subscriptionRepository.save(any(Subscription.class))).thenReturn(testSubscription);

        // Act
        subscriptionService.resumeSubscription(1, 1);

        // Assert
        verify(subscriptionRepository).save(any(Subscription.class));
        assertEquals(activeStatus, testSubscription.getStatus());
    }

    @Test
    void cancelSubscription_WithValidIdAndOwner_CancelsSubscription() {
        // Arrange
        when(subscriptionRepository.findById(1)).thenReturn(Optional.of(testSubscription));
        when(subscriptionStatusRepository.findByStatusName("cancelled")).thenReturn(Optional.of(cancelledStatus));
        when(subscriptionRepository.save(any(Subscription.class))).thenReturn(testSubscription);

        // Act
        subscriptionService.cancelSubscription(1, 1);

        // Assert
        verify(subscriptionRepository).save(any(Subscription.class));
        assertEquals(cancelledStatus, testSubscription.getStatus());
    }

    @Test
    void getScheduledMeals_WithValidSubscription_ReturnsMeals() {
        // Arrange
        Meal testMeal = new Meal();
        testMeal.setMealId(1);
        testMeal.setMealName("Test Meal");

        SubscriptionMeal subscriptionMeal = new SubscriptionMeal();
        subscriptionMeal.setSubscriptionMealId(1);
        subscriptionMeal.setSubscription(testSubscription);
        subscriptionMeal.setMeal(testMeal);
        subscriptionMeal.setDeliveryDate(LocalDate.now().plusDays(1));

        when(subscriptionRepository.findById(1)).thenReturn(Optional.of(testSubscription));
        when(subscriptionMealRepository.findBySubscriptionIdAndDateRange(1, null, null))
                .thenReturn(List.of(subscriptionMeal));

        // Act
        List<SubscriptionMealDto> result = subscriptionService.getScheduledMeals(1, 1, null, null, false);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Meal", result.get(0).getMealName());
        verify(subscriptionMealRepository).findBySubscriptionIdAndDateRange(1, null, null);
    }

    @Test
    void getScheduledMeals_WithNoMeals_ReturnsEmptyList() {
        // Arrange
        when(subscriptionRepository.findById(1)).thenReturn(Optional.of(testSubscription));
        when(subscriptionMealRepository.findBySubscriptionIdAndDateRange(1, null, null))
                .thenReturn(new ArrayList<>());

        // Act
        List<SubscriptionMealDto> result = subscriptionService.getScheduledMeals(1, 1, null, null, false);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getAllSubscriptions_WithNoFilters_ReturnsAllSubscriptions() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        List<Subscription> subscriptions = List.of(testSubscription);
        Page<Subscription> subscriptionPage = new PageImpl<>(subscriptions, pageable, 1);

        when(subscriptionRepository.findAll(pageable)).thenReturn(subscriptionPage);

        // Act
        Page<SubscriptionResponseDto> result = subscriptionService.getAllSubscriptions(null, null, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(subscriptionRepository).findAll(pageable);
    }

    @Test
    void getAllSubscriptions_WithUserFilter_ReturnsUserSubscriptions() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        List<Subscription> subscriptions = List.of(testSubscription);
        Page<Subscription> subscriptionPage = new PageImpl<>(subscriptions, pageable, 1);

        when(subscriptionRepository.findByUserUserId(1, pageable)).thenReturn(subscriptionPage);

        // Act
        Page<SubscriptionResponseDto> result = subscriptionService.getAllSubscriptions(1, null, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(subscriptionRepository).findByUserUserId(1, pageable);
    }

    @Test
    void getAllSubscriptions_WithStatusFilter_ReturnsFilteredSubscriptions() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        List<Subscription> subscriptions = List.of(testSubscription);
        Page<Subscription> subscriptionPage = new PageImpl<>(subscriptions, pageable, 1);

        when(subscriptionRepository.findByStatusStatusName("active", pageable)).thenReturn(subscriptionPage);

        // Act
        Page<SubscriptionResponseDto> result = subscriptionService.getAllSubscriptions(null, "active", pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(subscriptionRepository).findByStatusStatusName("active", pageable);
    }
}
