package com.mealplanner.api.repository;

import com.mealplanner.api.model.CustomPlanMeal;
import com.mealplanner.api.model.CustomPlanMealId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CustomPlanMealRepository extends JpaRepository<CustomPlanMeal, CustomPlanMealId> {

    /**
     * Finds all meals included in a specific custom plan ID.
     */
    List<CustomPlanMeal> findById_CustomPlanId(Integer customPlanId);
}