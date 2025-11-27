package com.mealplanner.api.repository;

import com.mealplanner.api.model.Allergy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AllergyRepository extends JpaRepository<Allergy, Integer> {
    // Standard CRUD methods are inherited from JpaRepository.
}