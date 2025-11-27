package com.mealplanner.api.repository;

import com.mealplanner.api.model.History;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface HistoryRepository extends JpaRepository<History, Integer> {

    /**
     * Finds all History records for a specific User, ordered by event time descending (newest first).
     * The naming convention (findByUser_UserId) navigates the 'user' relationship 
     * in the History entity to get the 'userId'.
     */
    List<History> findByUser_UserIdOrderByEventTimeDesc(Integer userId);
}