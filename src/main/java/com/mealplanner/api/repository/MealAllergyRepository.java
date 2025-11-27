package com.mealplanner.api.repository;

import com.mealplanner.api.model.MealAllergy;
import com.mealplanner.api.model.MealAllergyId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MealAllergyRepository extends JpaRepository<MealAllergy, MealAllergyId> {

    /**
     * Finds all allergies associated with a specific meal ID.
     */
    List<MealAllergy> findById_MealId(Integer mealId);

    /**
     * Finds all meals associated with a specific allergy ID.
     */
    List<MealAllergy> findById_AllergyId(Integer allergyId);
}