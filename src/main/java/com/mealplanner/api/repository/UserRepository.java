package com.mealplanner.api.repository;

import com.mealplanner.api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
// Extends JpaRepository<EntityClass, PrimaryKeyType>
public interface UserRepository extends JpaRepository<User, Integer> {

    /**
     * Finds a User entity by its unique email address.
     * Spring Data JPA automatically implements this based on the method name.
     */
    Optional<User> findByEmail(String email);
}