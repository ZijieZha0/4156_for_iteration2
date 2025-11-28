package com.example.nutriflow.pantry.repository;

import com.example.nutriflow.pantry.model.PantryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for accessing and managing {@link PantryItem} entities.
 * Provides CRUD operations and user-scoped lookups.
 */
@Repository
public interface PantryRepository extends JpaRepository<PantryItem, Integer> {

    /**
     * Find all pantry items owned by the specified user.
     *
     * @param userId the user ID
     * @return list of pantry items for the user (may be empty)
     */
    List<PantryItem> findByUserId(Integer userId);
}
