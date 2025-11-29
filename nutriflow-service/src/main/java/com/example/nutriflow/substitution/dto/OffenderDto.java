package com.example.nutriflow.substitution.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

/**
 * DTO representing an ingredient that causes an allergy.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OffenderDto {

    /** Offending ingredient name. */
    private String ingredient;

    /** The allergen that triggers the reaction. */
    private String allergen;
}
