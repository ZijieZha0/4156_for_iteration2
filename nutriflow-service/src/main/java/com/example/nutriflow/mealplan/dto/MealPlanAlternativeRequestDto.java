package com.example.nutriflow.mealplan.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for requesting alternative meals when user dislikes a suggested meal.
 * Allows replacement of specific meals while maintaining nutritional targets.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MealPlanAlternativeRequestDto {

    /**
     * The user ID.
     */
    private Integer userId;

    /**
     * The meal plan ID.
     */
    private Integer planId;

    /**
     * The meal ID to replace.
     */
    private Integer mealIdToReplace;

    /**
     * The recipe ID that the user dislikes.
     */
    private Integer dislikedRecipeId;

    /**
     * Reason for disliking (optional, for logging).
     */
    private String dislikeReason;

    /**
     * Specific recipe IDs to exclude from alternatives.
     */
    private List<Integer> excludeRecipeIds;

    /**
     * Whether to maintain the same meal type (breakfast, lunch, etc.).
     */
    private Boolean maintainMealType;

    /**
     * Maximum calorie difference allowed from original meal.
     */
    private Double maxCalorieDifference;
}

