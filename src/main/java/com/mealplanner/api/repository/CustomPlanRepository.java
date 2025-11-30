package com.mealplanner.api.repository;

import com.mealplanner.api.model.CustomPlan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CustomPlanRepository extends JpaRepository<CustomPlan, Integer> {

    /**
     * Finds all custom plans created by a specific user.
     */
    List<CustomPlan> findByUser_UserId(Integer userId);

    /**
     * Finds all custom plans created by a specific user (alternative naming).
     */
    List<CustomPlan> findByUserUserId(Integer userId);

    /**
     * Finds all custom plans belonging to a specific plan category.
     */
    List<CustomPlan> findByCategory_CategoryId(Integer categoryId);

    /**
     * Finds all custom plans belonging to a specific plan category with pagination.
     */
    Page<CustomPlan> findByCategoryCategoryId(Integer categoryId, Pageable pageable);

    /**
     * Checks if a custom plan has any active subscriptions.
     * Returns true if there are any subscriptions with status "active" for the given plan.
     */
    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END " +
           "FROM Subscription s " +
           "WHERE s.customPlan.customPlanId = :planId " +
           "AND s.status.statusName = 'active'")
    boolean hasActiveSubscriptions(@Param("planId") Integer planId);
}