package com.example.nutriflow.mealplan.dto;

import com.example.nutriflow.mealplan.model.DailyMealPlan;
import com.example.nutriflow.mealplan.model.WeeklyMealPlan;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for meal plan generation response.
 * Contains the generated meal plan(s) and summary information.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MealPlanResponseDto {

    /**
     * The daily meal plan (for single day requests).
     */
    private DailyMealPlan dailyPlan;

    /**
     * The weekly meal plan (for weekly requests).
     */
    private WeeklyMealPlan weeklyPlan;

    /**
     * List of daily meal plans (for multi-day or weekly requests).
     */
    private List<DailyMealPlanDetailDto> dailyPlans;

    /**
     * Success status.
     */
    private Boolean success;

    /**
     * Message with additional information.
     */
    private String message;

    /**
     * Total number of recipes used.
     */
    private Integer totalRecipesUsed;

    /**
     * Variance from target calories (percentage).
     */
    private Double calorieVariance;

    /**
     * Variance from target protein (percentage).
     */
    private Double proteinVariance;
}

