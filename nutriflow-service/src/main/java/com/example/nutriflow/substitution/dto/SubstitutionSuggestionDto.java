package com.example.nutriflow.substitution.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

/**
 * DTO representing a suggested substitute for an ingredient.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubstitutionSuggestionDto {

    /** The ingredient that needs substitution. */
    private String ingredient;

    /** The suggested alternative ingredient. */
    private String alt;

    /** Optional note describing the substitution reason or detail. */
    private String note;
}
