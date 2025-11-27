package com.mealplanner.api.repository;

import com.mealplanner.api.model.Nutrition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NutritionRepository extends JpaRepository<Nutrition, Integer> {
    // Standard CRUD operations are sufficient for the parent Nutrition container.
}