package com.example.nutriflow.mealplan.dto;

import com.example.nutriflow.recipe.model.Recipe;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO for detailed daily meal plan information.
 * Includes the actual recipe details for each meal.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailyMealPlanDetailDto {

    /**
     * The meal plan ID.
     */
    private Integer planId;

    /**
     * The date for this meal plan.
     */
    private LocalDate planDate;

    /**
     * List of meal details with recipes.
     */
    private List<MealDetailDto> meals;

    /**
     * Total calories for the day.
     */
    private Double totalCalories;

    /**
     * Total protein for the day (grams).
     */
    private Double totalProtein;

    /**
     * Total carbohydrates for the day (grams).
     */
    private Double totalCarbs;

    /**
     * Total fat for the day (grams).
     */
    private Double totalFat;

    /**
     * Total fiber for the day (grams).
     */
    private Double totalFiber;

    /**
     * DTO for individual meal details.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MealDetailDto {
        /**
         * The meal ID.
         */
        private Integer mealId;

        /**
         * Type of meal (breakfast, lunch, dinner, snack).
         */
        private String mealType;

        /**
         * The recipe for this meal.
         */
        private Recipe recipe;

        /**
         * Number of servings.
         */
        private Integer servings;

        /**
         * Optional notes.
         */
        private String notes;
    }
}

