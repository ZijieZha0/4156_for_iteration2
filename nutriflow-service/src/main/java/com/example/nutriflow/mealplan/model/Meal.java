package com.example.nutriflow.mealplan.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Entity representing a single meal in a meal plan.
 * Links a recipe to a specific meal time (breakfast, lunch, dinner, snack)
 * with scheduling information.
 */
@Entity
@Table(name = "meals", schema = "nutriflow")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Meal {

    /**
     * Unique identifier for the meal.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "meal_id")
    private Integer mealId;

    /**
     * The recipe associated with this meal.
     */
    @Column(name = "recipe_id", nullable = false)
    private Integer recipeId;

    /**
     * Type of meal (e.g., "breakfast", "lunch", "dinner", "snack").
     */
    @Column(name = "meal_type", nullable = false)
    private String mealType;

    /**
     * Scheduled time for the meal.
     */
    @Column(name = "scheduled_time")
    private LocalTime scheduledTime;

    /**
     * Number of servings for this meal.
     */
    @Column(name = "servings")
    private Integer servings;

    /**
     * Optional notes for the meal (e.g., preparation tips, modifications).
     */
    @Column(name = "notes", columnDefinition = "text")
    private String notes;

    /**
     * Timestamp when the meal was created.
     */
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /**
     * Constructor for creating a new meal with essential fields.
     *
     * @param recId the recipe ID
     * @param type the meal type (breakfast, lunch, dinner, snack)
     * @param servCount number of servings
     */
    public Meal(final Integer recId, final String type,
            final Integer servCount) {
        this.recipeId = recId;
        this.mealType = type;
        this.servings = servCount;
        this.createdAt = LocalDateTime.now();
    }
}

