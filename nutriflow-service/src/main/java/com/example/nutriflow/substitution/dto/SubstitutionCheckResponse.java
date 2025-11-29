package com.example.nutriflow.substitution.dto;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

/**
 * Response body for allergen check results and substitution suggestions.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubstitutionCheckResponse {

    /** Whether the recipe contains allergens for the user. */
    private boolean hasAllergens;

    /** List of ingredients that trigger allergens. */
    private List<OffenderDto> offenders;

    /** Suggested substitutions for offending ingredients. */
    private List<SubstitutionSuggestionDto> suggestions;
}
