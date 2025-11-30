package com.mealplanner.api.repository;

import com.mealplanner.api.model.Meal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    /**
     * Finds meals by searching within the meal name with pagination.
     */
    Page<Meal> findByMealNameContainingIgnoreCase(String name, Pageable pageable);

    /**
     * Finds all meals with a rating equal to or above a certain minimum value with pagination.
     */
    Page<Meal> findByRatingGreaterThanEqual(Integer minRating, Pageable pageable);

    /**
     * Finds meals that do not contain any of the specified allergens.
     * Uses a custom query to exclude meals that have associations with the given allergen IDs.
     */
    @Query("SELECT DISTINCT m FROM Meal m " +
           "WHERE m.mealId NOT IN (" +
           "  SELECT ma.meal.mealId FROM MealAllergy ma " +
           "  WHERE ma.allergy.allergyId IN :allergenIds" +
           ")")
    Page<Meal> findMealsExcludingAllergens(@Param("allergenIds") List<Integer> allergenIds, Pageable pageable);
}