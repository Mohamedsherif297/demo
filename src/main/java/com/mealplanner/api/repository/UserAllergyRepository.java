package com.mealplanner.api.repository;

import com.mealplanner.api.model.UserAllergy;
import com.mealplanner.api.model.UserAllergyId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
// The primary key type here is the composite key class: UserAllergyId
public interface UserAllergyRepository extends JpaRepository<UserAllergy, UserAllergyId> {

    /**
     * Finds all UserAllergy records associated with a specific user ID.
     * The naming convention (findById_UserId) navigates the 'id' (UserAllergyId) 
     * composite key object to get the 'userId'.
     */
    List<UserAllergy> findById_UserId(Integer userId);
}