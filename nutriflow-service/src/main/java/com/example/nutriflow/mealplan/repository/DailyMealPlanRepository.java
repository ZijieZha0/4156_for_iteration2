package com.example.nutriflow.mealplan.repository;

import com.example.nutriflow.mealplan.model.DailyMealPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for DailyMealPlan entity.
 * Provides database access methods for daily meal plans.
 */
@Repository
public interface DailyMealPlanRepository
        extends JpaRepository<DailyMealPlan, Integer> {

    /**
     * Find all daily meal plans for a specific user.
     *
     * @param userId the user ID
     * @return list of daily meal plans for the user
     */
    List<DailyMealPlan> findByUserId(Integer userId);

    /**
     * Find a daily meal plan for a specific user and date.
     *
     * @param userId   the user ID
     * @param planDate the plan date
     * @return optional containing the daily meal plan if found
     */
    Optional<DailyMealPlan> findByUserIdAndPlanDate(
            Integer userId, LocalDate planDate);

    /**
     * Find all daily meal plans for a user within a date range.
     *
     * @param userId    the user ID
     * @param startDate start date of the range
     * @param endDate   end date of the range
     * @return list of daily meal plans within the date range
     */
    List<DailyMealPlan> findByUserIdAndPlanDateBetween(
            Integer userId, LocalDate startDate, LocalDate endDate);

    /**
     * Find all daily meal plans with a specific status.
     *
     * @param userId the user ID
     * @param status the status (e.g., "active", "completed")
     * @return list of daily meal plans with the specified status
     */
    List<DailyMealPlan> findByUserIdAndStatus(Integer userId, String status);
}

