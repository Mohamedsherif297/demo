package com.mealplanner.api.repository;

import com.mealplanner.api.model.PlanCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PlanCategoryRepository extends JpaRepository<PlanCategory, Integer> {

    /**
     * Finds a PlanCategory by its unique name (e.g., "Weight Loss", "Bulking").
     */
    Optional<PlanCategory> findByCategoryName(String categoryName);
}