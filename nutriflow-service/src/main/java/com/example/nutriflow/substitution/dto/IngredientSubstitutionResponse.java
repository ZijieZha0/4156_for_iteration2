package com.example.nutriflow.substitution.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Response returned when checking substitutions for a single ingredient.
 */
@Getter
@Setter
@Builder
public class IngredientSubstitutionResponse {

    /** Ingredient that needs a substitute. */
    private String originalIngredient;

    /** Allergens that must be avoided. */
    private List<String> allergens;

    /** Disliked ingredients to avoid. */
    private List<String> dislikes;

    /** Suggested alternatives. */
    private List<SubstitutionSuggestionDto> suggestions;
}

