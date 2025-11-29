package com.example.nutriflow.mealplan.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO for requesting meal plan generation.
 * Contains user preferences, constraints, and nutritional targets.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MealPlanRequestDto {

    /**
     * The user ID requesting the meal plan.
     */
    private Integer userId;

    /**
     * Number of meals per day (e.g., 3 for breakfast/lunch/dinner).
     */
    private Integer mealsPerDay;

    /**
     * Number of days to generate (1 for daily, 7 for weekly).
     */
    private Integer numberOfDays;

    /**
     * Start date for the meal plan.
     */
    private LocalDate startDate;

    /**
     * Maximum preparation time in minutes (optional).
     */
    private Integer maxPrepTime;

    /**
     * Target daily calories (optional, will use user target if not provided).
     */
    private Double targetCalories;

    /**
     * Target daily protein in grams (optional).
     */
    private Double targetProtein;

    /**
     * Target daily carbohydrates in grams (optional).
     */
    private Double targetCarbs;

    /**
     * Target daily fat in grams (optional).
     */
    private Double targetFat;

    /**
     * List of available ingredients (optional, for ingredient-based search).
     */
    private List<String> availableIngredients;

    /**
     * Cuisines to include (optional).
     */
    private List<String> preferredCuisines;

    /**
     * Tags to filter by (e.g., "vegetarian", "quick", "high-protein").
     */
    private List<String> tags;

    /**
     * Whether to use AI for recipe generation (default: false).
     */
    private Boolean useAiGeneration;

    /**
     * Client identifier for logging purposes.
     */
    private String clientId;
}

