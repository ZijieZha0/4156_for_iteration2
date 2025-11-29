package com.example.nutriflow.substitution.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

/**
 * Request body for checking allergens in a recipe for a user.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubstitutionCheckRequest {

    /** ID of the recipe to check. */
    private Integer recipeId;

    /** ID of the user whose allergies are applied. */
    private Integer userId;

    /** Ingredient to check for substitutions (optional). */
    private String originalIngredient;

    /** Allergens to avoid when suggesting substitutes. */
    private String[] allergens;

    /** Disliked ingredients to avoid. */
    private String[] dislikes;
}
