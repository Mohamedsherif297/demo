package com.mealplanner.api.service;

import com.mealplanner.api.dto.DeliveryHistoryDto;
import com.mealplanner.api.dto.DeliveryResponseDto;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeliveryServiceTest {

    @Mock
    private DeliveryRepository deliveryRepository;

    @Mock
    private DeliveryStatusRepository deliveryStatusRepository;

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private SubscriptionMealRepository subscriptionMealRepository;

    @InjectMocks
    private DeliveryService deliveryService;

    private Subscription subscription;
    private User user;
    private DeliveryStatus preparingStatus;
    private SubscriptionMeal subscriptionMeal;
    private Delivery delivery;
    private Meal meal;

    @BeforeEach
    void setUp() {
        // Setup user
        user = new User();
        user.setUserId(1);
        user.setEmail("test@example.com");
        user.setAddress("123 Test St");

        // Setup subscription
        subscription = new Subscription();
        subscription.setSubscriptionId(1);
        subscription.setUser(user);
        subscription.setPreferredTime(LocalTime.of(18, 0));

        // Setup delivery status
        preparingStatus = new DeliveryStatus();
        preparingStatus.setStatusId(1);
        preparingStatus.setStatusName("PREPARING");

        // Setup meal
        meal = new Meal();
        meal.setMealId(1);
        meal.setMealName("Test Meal");

        // Setup subscription meal
        subscriptionMeal = new SubscriptionMeal();
        subscriptionMeal.setSubscriptionMealId(1);
        subscriptionMeal.setSubscription(subscription);
        subscriptionMeal.setMeal(meal);
        subscriptionMeal.setDeliveryDate(LocalDate.now());

        // Setup delivery
        delivery = new Delivery();
        delivery.setDeliveryId(1);
        delivery.setSubscriptionMeal(subscriptionMeal);
        delivery.setAddress("123 Test St");
        delivery.setDeliveryTime(LocalTime.of(18, 0));
        delivery.setStatus(preparingStatus);
        delivery.setCreatedAt(LocalDateTime.now());
        delivery.setStatusUpdatedAt(LocalDateTime.now());
    }

    @Test
    @SuppressWarnings("null")
    void testCreateDailyDelivery_Success() {
        // Arrange
        LocalDate deliveryDate = LocalDate.now();
        List<SubscriptionMeal> meals = List.of(subscriptionMeal);

        when(subscriptionRepository.findById(1)).thenReturn(Optional.of(subscription));
        when(subscriptionMealRepository.findBySubscription_SubscriptionIdAndDeliveryDate(1, deliveryDate))
                .thenReturn(meals);
        when(deliveryStatusRepository.findByStatusName("PREPARING")).thenReturn(Optional.of(preparingStatus));
        when(deliveryRepository.save(any())).thenReturn(delivery);

        // Act
        DeliveryResponseDto result = deliveryService.createDailyDelivery(1, deliveryDate);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getDeliveryId());
        assertEquals("PREPARING", result.getStatus());
        assertEquals(LocalTime.of(18, 0), result.getDeliveryTime());
        verify(deliveryRepository, times(1)).save(any(Delivery.class));
    }

    @Test
    void testCreateDailyDelivery_SubscriptionNotFound() {
        // Arrange
        when(subscriptionRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> 
            deliveryService.createDailyDelivery(999, LocalDate.now())
        );
    }

    @Test
    void testCreateDailyDelivery_NoMealsScheduled() {
        // Arrange
        LocalDate deliveryDate = LocalDate.now();
        when(subscriptionRepository.findById(1)).thenReturn(Optional.of(subscription));
        when(subscriptionMealRepository.findBySubscription_SubscriptionIdAndDeliveryDate(1, deliveryDate))
                .thenReturn(new ArrayList<>());

        // Act & Assert
        assertThrows(ValidationException.class, () -> 
            deliveryService.createDailyDelivery(1, deliveryDate)
        );
    }

    @Test
    void testGetCurrentDelivery_Success() {
        // Arrange
        LocalDate today = LocalDate.now();
        List<SubscriptionMeal> meals = List.of(subscriptionMeal);

        when(deliveryRepository.findBySubscriptionMeal_Subscription_User_UserIdAndSubscriptionMeal_DeliveryDate(1, today))
                .thenReturn(Optional.of(delivery));
        when(subscriptionMealRepository.findBySubscription_SubscriptionIdAndDeliveryDate(1, today))
                .thenReturn(meals);

        // Act
        DeliveryResponseDto result = deliveryService.getCurrentDelivery(1);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getDeliveryId());
        assertEquals("PREPARING", result.getStatus());
    }

    @Test
    void testGetCurrentDelivery_NotFound() {
        // Arrange
        LocalDate today = LocalDate.now();
        when(deliveryRepository.findBySubscriptionMeal_Subscription_User_UserIdAndSubscriptionMeal_DeliveryDate(1, today))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> 
            deliveryService.getCurrentDelivery(1)
        );
    }

    @Test
    void testGetDeliveryById_Success() {
        // Arrange
        List<SubscriptionMeal> meals = List.of(subscriptionMeal);

        when(deliveryRepository.findById(1)).thenReturn(Optional.of(delivery));
        when(subscriptionMealRepository.findBySubscription_SubscriptionIdAndDeliveryDate(1, LocalDate.now()))
                .thenReturn(meals);

        // Act
        DeliveryResponseDto result = deliveryService.getDeliveryById(1, 1, false);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getDeliveryId());
    }

    @Test
    void testGetDeliveryById_Forbidden() {
        // Arrange
        when(deliveryRepository.findById(1)).thenReturn(Optional.of(delivery));

        // Act & Assert
        assertThrows(ForbiddenException.class, () -> 
            deliveryService.getDeliveryById(1, 999, false)
        );
    }

    @Test
    void testGetDeliveryById_AdminCanAccess() {
        // Arrange
        List<SubscriptionMeal> meals = List.of(subscriptionMeal);

        when(deliveryRepository.findById(1)).thenReturn(Optional.of(delivery));
        when(subscriptionMealRepository.findBySubscription_SubscriptionIdAndDeliveryDate(1, LocalDate.now()))
                .thenReturn(meals);

        // Act
        DeliveryResponseDto result = deliveryService.getDeliveryById(1, 999, true);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getDeliveryId());
    }

    @Test
    void testGetDeliveryHistory_Success() {
        // Arrange
        List<Delivery> deliveries = List.of(delivery);
        List<SubscriptionMeal> meals = List.of(subscriptionMeal);
        Pageable pageable = PageRequest.of(0, 10);

        when(deliveryRepository.findBySubscriptionMeal_Subscription_User_UserIdOrderBySubscriptionMeal_DeliveryDateDesc(
                eq(1), any(Pageable.class)))
                .thenReturn(deliveries);
        when(subscriptionMealRepository.findBySubscription_SubscriptionIdAndDeliveryDate(1, LocalDate.now()))
                .thenReturn(meals);

        // Act
        Page<DeliveryHistoryDto> result = deliveryService.getDeliveryHistory(1, null, null, null, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
    }

    @Test
    void testGetDeliveryHistory_WithFilters() {
        // Arrange
        List<Delivery> deliveries = List.of(delivery);
        List<SubscriptionMeal> meals = List.of(subscriptionMeal);
        Pageable pageable = PageRequest.of(0, 10);
        LocalDate startDate = LocalDate.now().minusDays(7);
        LocalDate endDate = LocalDate.now();

        when(deliveryRepository.findBySubscriptionMeal_Subscription_User_UserIdOrderBySubscriptionMeal_DeliveryDateDesc(
                eq(1), any(Pageable.class)))
                .thenReturn(deliveries);
        when(subscriptionMealRepository.findBySubscription_SubscriptionIdAndDeliveryDate(1, LocalDate.now()))
                .thenReturn(meals);

        // Act
        Page<DeliveryHistoryDto> result = deliveryService.getDeliveryHistory(
                1, startDate, endDate, "PREPARING", pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    @SuppressWarnings("null")
    void testUpdateDeliveryPreferences_UpdateTimeSuccess() {
        // Arrange
        List<SubscriptionMeal> meals = List.of(subscriptionMeal);
        LocalTime newTime = LocalTime.of(20, 0);
        com.mealplanner.api.dto.UpdateDeliveryDto updateDto = 
            new com.mealplanner.api.dto.UpdateDeliveryDto(newTime, null);

        when(deliveryRepository.findById(1)).thenReturn(Optional.of(delivery));
        when(deliveryRepository.save(any())).thenReturn(delivery);
        when(subscriptionMealRepository.findBySubscription_SubscriptionIdAndDeliveryDate(1, LocalDate.now()))
                .thenReturn(meals);

        // Act
        DeliveryResponseDto result = deliveryService.updateDeliveryPreferences(1, 1, updateDto);

        // Assert
        assertNotNull(result);
        verify(deliveryRepository, times(1)).save(any());
    }

    @Test
    @SuppressWarnings("null")
    void testUpdateDeliveryPreferences_UpdateAddressSuccess() {
        // Arrange
        List<SubscriptionMeal> meals = List.of(subscriptionMeal);
        String newAddress = "456 New St";
        com.mealplanner.api.dto.UpdateDeliveryDto updateDto = 
            new com.mealplanner.api.dto.UpdateDeliveryDto(null, newAddress);

        when(deliveryRepository.findById(1)).thenReturn(Optional.of(delivery));
        when(deliveryRepository.save(any())).thenReturn(delivery);
        when(subscriptionMealRepository.findBySubscription_SubscriptionIdAndDeliveryDate(1, LocalDate.now()))
                .thenReturn(meals);

        // Act
        DeliveryResponseDto result = deliveryService.updateDeliveryPreferences(1, 1, updateDto);

        // Assert
        assertNotNull(result);
        verify(deliveryRepository, times(1)).save(any());
    }

    @Test
    @SuppressWarnings("null")
    void testUpdateDeliveryPreferences_UpdateBothSuccess() {
        // Arrange
        List<SubscriptionMeal> meals = List.of(subscriptionMeal);
        LocalTime newTime = LocalTime.of(20, 0);
        String newAddress = "456 New St";
        com.mealplanner.api.dto.UpdateDeliveryDto updateDto = 
            new com.mealplanner.api.dto.UpdateDeliveryDto(newTime, newAddress);

        when(deliveryRepository.findById(1)).thenReturn(Optional.of(delivery));
        when(deliveryRepository.save(any())).thenReturn(delivery);
        when(subscriptionMealRepository.findBySubscription_SubscriptionIdAndDeliveryDate(1, LocalDate.now()))
                .thenReturn(meals);

        // Act
        DeliveryResponseDto result = deliveryService.updateDeliveryPreferences(1, 1, updateDto);

        // Assert
        assertNotNull(result);
        verify(deliveryRepository, times(1)).save(any());
    }

    @Test
    void testUpdateDeliveryPreferences_DeliveryNotFound() {
        // Arrange
        com.mealplanner.api.dto.UpdateDeliveryDto updateDto = 
            new com.mealplanner.api.dto.UpdateDeliveryDto(LocalTime.of(20, 0), null);
        when(deliveryRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> 
            deliveryService.updateDeliveryPreferences(999, 1, updateDto)
        );
    }

    @Test
    void testUpdateDeliveryPreferences_Forbidden() {
        // Arrange
        com.mealplanner.api.dto.UpdateDeliveryDto updateDto = 
            new com.mealplanner.api.dto.UpdateDeliveryDto(LocalTime.of(20, 0), null);
        when(deliveryRepository.findById(1)).thenReturn(Optional.of(delivery));

        // Act & Assert
        assertThrows(ForbiddenException.class, () -> 
            deliveryService.updateDeliveryPreferences(1, 999, updateDto)
        );
    }

    @Test
    void testUpdateDeliveryPreferences_AlreadyShipped() {
        // Arrange
        DeliveryStatus shippedStatus = new DeliveryStatus();
        shippedStatus.setStatusId(2);
        shippedStatus.setStatusName("SHIPPED");
        delivery.setStatus(shippedStatus);

        com.mealplanner.api.dto.UpdateDeliveryDto updateDto = 
            new com.mealplanner.api.dto.UpdateDeliveryDto(LocalTime.of(20, 0), null);
        when(deliveryRepository.findById(1)).thenReturn(Optional.of(delivery));

        // Act & Assert
        assertThrows(ValidationException.class, () -> 
            deliveryService.updateDeliveryPreferences(1, 1, updateDto)
        );
    }

    @Test
    void testUpdateDeliveryPreferences_EmptyAddress() {
        // Arrange
        com.mealplanner.api.dto.UpdateDeliveryDto updateDto = 
            new com.mealplanner.api.dto.UpdateDeliveryDto(null, "   ");
        when(deliveryRepository.findById(1)).thenReturn(Optional.of(delivery));

        // Act & Assert
        assertThrows(ValidationException.class, () -> 
            deliveryService.updateDeliveryPreferences(1, 1, updateDto)
        );
    }

    @Test
    @SuppressWarnings("null")
    void testConfirmDelivery_Success() {
        // Arrange
        DeliveryStatus deliveredStatus = new DeliveryStatus();
        deliveredStatus.setStatusId(3);
        deliveredStatus.setStatusName("DELIVERED");
        delivery.setStatus(deliveredStatus);

        DeliveryStatus confirmedStatus = new DeliveryStatus();
        confirmedStatus.setStatusId(4);
        confirmedStatus.setStatusName("CONFIRMED");

        List<SubscriptionMeal> meals = List.of(subscriptionMeal);

        when(deliveryRepository.findById(1)).thenReturn(Optional.of(delivery));
        when(deliveryStatusRepository.findByStatusName("CONFIRMED")).thenReturn(Optional.of(confirmedStatus));
        when(deliveryRepository.save(any())).thenReturn(delivery);
        when(subscriptionMealRepository.findBySubscription_SubscriptionIdAndDeliveryDate(1, LocalDate.now()))
                .thenReturn(meals);

        // Act
        DeliveryResponseDto result = deliveryService.confirmDelivery(1, 1);

        // Assert
        assertNotNull(result);
        verify(deliveryRepository, times(1)).save(any());
        verify(deliveryStatusRepository, times(1)).findByStatusName("CONFIRMED");
    }

    @Test
    void testConfirmDelivery_DeliveryNotFound() {
        // Arrange
        when(deliveryRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> 
            deliveryService.confirmDelivery(999, 1)
        );
    }

    @Test
    void testConfirmDelivery_Forbidden() {
        // Arrange
        DeliveryStatus deliveredStatus = new DeliveryStatus();
        deliveredStatus.setStatusId(3);
        deliveredStatus.setStatusName("DELIVERED");
        delivery.setStatus(deliveredStatus);

        when(deliveryRepository.findById(1)).thenReturn(Optional.of(delivery));

        // Act & Assert
        assertThrows(ForbiddenException.class, () -> 
            deliveryService.confirmDelivery(1, 999)
        );
    }

    @Test
    void testConfirmDelivery_NotInDeliveredStatus() {
        // Arrange - delivery is in PREPARING status
        when(deliveryRepository.findById(1)).thenReturn(Optional.of(delivery));

        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class, () -> 
            deliveryService.confirmDelivery(1, 1)
        );
        assertTrue(exception.getMessage().contains("Delivered"));
    }

    @Test
    @SuppressWarnings("null")
    void testConfirmDelivery_AlreadyConfirmed_Idempotent() {
        // Arrange
        DeliveryStatus confirmedStatus = new DeliveryStatus();
        confirmedStatus.setStatusId(4);
        confirmedStatus.setStatusName("CONFIRMED");
        delivery.setStatus(confirmedStatus);
        delivery.setConfirmedAt(LocalDateTime.now().minusHours(1));

        List<SubscriptionMeal> meals = List.of(subscriptionMeal);

        when(deliveryRepository.findById(1)).thenReturn(Optional.of(delivery));
        when(subscriptionMealRepository.findBySubscription_SubscriptionIdAndDeliveryDate(1, LocalDate.now()))
                .thenReturn(meals);

        // Act
        DeliveryResponseDto result = deliveryService.confirmDelivery(1, 1);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getConfirmedAt());
        // Should not attempt to save or update status again
        verify(deliveryRepository, never()).save(any());
        verify(deliveryStatusRepository, never()).findByStatusName("CONFIRMED");
    }

    @Test
    void testConfirmDelivery_ShippedStatus() {
        // Arrange
        DeliveryStatus shippedStatus = new DeliveryStatus();
        shippedStatus.setStatusId(2);
        shippedStatus.setStatusName("SHIPPED");
        delivery.setStatus(shippedStatus);

        when(deliveryRepository.findById(1)).thenReturn(Optional.of(delivery));

        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class, () -> 
            deliveryService.confirmDelivery(1, 1)
        );
        assertTrue(exception.getMessage().contains("Delivered"));
    }
}
