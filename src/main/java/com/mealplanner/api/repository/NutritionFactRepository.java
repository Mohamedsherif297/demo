package com.mealplanner.api.repository;

import com.mealplanner.api.model.NutritionFact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NutritionFactRepository extends JpaRepository<NutritionFact, Integer> {

    /**
     * Finds all NutritionFact records associated with a specific Nutrition parent ID.
     */
    List<NutritionFact> findByNutrition_NutritionId(Integer nutritionId);
}