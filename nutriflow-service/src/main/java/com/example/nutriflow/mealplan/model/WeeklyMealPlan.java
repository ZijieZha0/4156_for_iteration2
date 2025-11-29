package com.example.nutriflow.mealplan.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.PrePersist;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entity representing a weekly meal plan for a user.
 * Contains references to daily meal plans for a week.
 */
@Entity
@Table(name = "weekly_meal_plans", schema = "nutriflow")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeeklyMealPlan {

    /**
     * Unique identifier for the weekly meal plan.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "weekly_plan_id")
    private Integer weeklyPlanId;

    /**
     * The user ID this weekly meal plan belongs to.
     */
    @Column(name = "user_id", nullable = false)
    private Integer userId;

    /**
     * Start date of the week.
     */
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    /**
     * End date of the week.
     */
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    /**
     * Array of daily meal plan IDs (7 days).
     * Stored as a PostgreSQL integer array.
     */
    @Column(name = "daily_plan_ids", columnDefinition = "integer[]")
    private Integer[] dailyPlanIds;

    /**
     * Average daily calories for the week.
     */
    @Column(name = "avg_daily_calories")
    private Double avgDailyCalories;

    /**
     * Average daily protein for the week (grams).
     */
    @Column(name = "avg_daily_protein")
    private Double avgDailyProtein;

    /**
     * Average daily carbohydrates for the week (grams).
     */
    @Column(name = "avg_daily_carbs")
    private Double avgDailyCarbs;

    /**
     * Average daily fat for the week (grams).
     */
    @Column(name = "avg_daily_fat")
    private Double avgDailyFat;

    /**
     * Status of the weekly meal plan (e.g., "draft", "active", "completed").
     */
    @Column(name = "status")
    private String status;

    /**
     * Timestamp when the weekly meal plan was created.
     */
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /**
     * Set the creation timestamp before persisting the entity.
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) {
            status = "active";
        }
    }
}

