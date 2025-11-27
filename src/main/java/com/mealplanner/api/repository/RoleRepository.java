package com.mealplanner.api.repository;

import com.mealplanner.api.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {

    /**
     * Finds a Role entity by its name.
     */
    Optional<Role> findByRoleName(String roleName);
}