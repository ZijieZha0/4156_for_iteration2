package com.example.nutriflow.user.repository;

import com.example.nutriflow.user.model.UserTarget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for UserTarget entity.
 * Provides database access methods for user nutritional targets.
 */
@Repository
public interface UserTargetRepository
        extends JpaRepository<UserTarget, Integer> {

    /**
     * Find the most recent target for a specific user.
     * Orders by created_at descending to get the latest target.
     *
     * @param userId the ID of the user
     * @return Optional containing the user's most recent target if found,
     *         empty otherwise
     */
    @Query("SELECT ut FROM UserTarget ut WHERE ut.userId = :userId "
            + "ORDER BY ut.createdAt DESC LIMIT 1")
    Optional<UserTarget> findLatestByUserId(
            @Param("userId") Integer userId);
}
