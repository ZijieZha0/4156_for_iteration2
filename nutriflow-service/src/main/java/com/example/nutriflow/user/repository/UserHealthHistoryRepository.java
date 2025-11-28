package com.example.nutriflow.user.repository;

import com.example.nutriflow.user.model.UserHealthHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for UserHealthHistory entity.
 * Provides data access methods for user health history records.
 */
@Repository
public interface UserHealthHistoryRepository
        extends JpaRepository<UserHealthHistory, Integer> {

    /**
     * Find all health history records for a specific user.
     * Ordered by recorded date descending.
     *
     * @param userId the ID of the user
     * @return List of health history records, most recent first
     */
    @Query("SELECT h FROM UserHealthHistory h WHERE h.userId = :userId "
            + "ORDER BY h.recordedAt DESC")
    List<UserHealthHistory> findByUserIdOrderByRecordedAtDesc(
            @Param("userId") Integer userId);

    /**
     * Find the most recent health history record for a specific user.
     *
     * @param userId the ID of the user
     * @return The most recent health history record, or null if none exists
     */
    @Query("SELECT h FROM UserHealthHistory h WHERE h.userId = :userId "
            + "ORDER BY h.recordedAt DESC LIMIT 1")
    UserHealthHistory findLatestByUserId(
            @Param("userId") Integer userId);
}
