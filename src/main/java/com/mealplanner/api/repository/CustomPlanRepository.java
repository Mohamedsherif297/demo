package com.mealplanner.api.repository;

import com.mealplanner.api.model.CustomPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CustomPlanRepository extends JpaRepository<CustomPlan, Integer> {

    /**
     * Finds all custom plans created by a specific user.
     */
    List<CustomPlan> findByUser_UserId(Integer userId);

    /**
     * Finds all custom plans belonging to a specific plan category.
     */
    List<CustomPlan> findByCategory_CategoryId(Integer categoryId);
}