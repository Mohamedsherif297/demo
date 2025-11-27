package com.mealplanner.api.repository;

import com.mealplanner.api.model.Meal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MealRepository extends JpaRepository<Meal, Integer> {

    /**
     * Finds all meals with a rating equal to or above a certain minimum value.
     */
    List<Meal> findByRatingGreaterThanEqual(Integer minRating);

    /**
     * Finds meals by searching within the meal name.
     */
    List<Meal> findByMealNameContainingIgnoreCase(String keyword);
}