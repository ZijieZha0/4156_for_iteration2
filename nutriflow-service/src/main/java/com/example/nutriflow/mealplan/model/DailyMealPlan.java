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
 * Entity representing a daily meal plan for a user.
 * Contains references to meals for a specific day and tracks
 * nutritional totals and user preferences.
 */
@Entity
@Table(name = "daily_meal_plans", schema = "nutriflow")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailyMealPlan {

    /**
     * Unique identifier for the daily meal plan.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "plan_id")
    private Integer planId;

    /**
     * The user ID this meal plan belongs to.
     */
    @Column(name = "user_id", nullable = false)
    private Integer userId;

    /**
     * The date for this meal plan.
     */
    @Column(name = "plan_date", nullable = false)
    private LocalDate planDate;

    /**
     * Meal IDs stored as an array (breakfast, lunch, dinner, snacks).
     * Stored as a PostgreSQL integer array.
     */
    @Column(name = "meal_ids", columnDefinition = "integer[]")
    private Integer[] mealIds;

    /**
     * Total calories for the day's meal plan.
     */
    @Column(name = "total_calories")
    private Double totalCalories;

    /**
     * Total protein for the day's meal plan (grams).
     */
    @Column(name = "total_protein")
    private Double totalProtein;

    /**
     * Total carbohydrates for the day's meal plan (grams).
     */
    @Column(name = "total_carbs")
    private Double totalCarbs;

    /**
     * Total fat for the day's meal plan (grams).
     */
    @Column(name = "total_fat")
    private Double totalFat;

    /**
     * Total fiber for the day's meal plan (grams).
     */
    @Column(name = "total_fiber")
    private Double totalFiber;

    /**
     * Maximum preparation time constraint (minutes).
     */
    @Column(name = "max_prep_time")
    private Integer maxPrepTime;

    /**
     * Status of the meal plan (e.g., "draft", "active", "completed").
     */
    @Column(name = "status")
    private String status;

    /**
     * Timestamp when the meal plan was created.
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

