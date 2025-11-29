package com.example.nutriflow.mealplan.repository;

import com.example.nutriflow.mealplan.model.WeeklyMealPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for WeeklyMealPlan entity.
 * Provides database access methods for weekly meal plans.
 */
@Repository
public interface WeeklyMealPlanRepository
        extends JpaRepository<WeeklyMealPlan, Integer> {

    /**
     * Find all weekly meal plans for a specific user.
     *
     * @param userId the user ID
     * @return list of weekly meal plans for the user
     */
    List<WeeklyMealPlan> findByUserId(Integer userId);

    /**
     * Find a weekly meal plan that includes a specific date.
     *
     * @param userId the user ID
     * @param date   the date to check
     * @param dateDup the same date for range check
     * @return optional containing the weekly meal plan if found
     */
    Optional<WeeklyMealPlan>
        findByUserIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            Integer userId, LocalDate date, LocalDate dateDup);

    /**
     * Find all weekly meal plans for a user within a date range.
     *
     * @param userId    the user ID
     * @param startDate start date of the range
     * @param endDate   end date of the range
     * @return list of weekly meal plans within the date range
     */
    List<WeeklyMealPlan> findByUserIdAndStartDateBetween(
            Integer userId, LocalDate startDate, LocalDate endDate);

    /**
     * Find all weekly meal plans with a specific status.
     *
     * @param userId the user ID
     * @param status the status (e.g., "active", "completed")
     * @return list of weekly meal plans with the specified status
     */
    List<WeeklyMealPlan> findByUserIdAndStatus(Integer userId, String status);
}

