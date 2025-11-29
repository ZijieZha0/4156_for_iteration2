package com.example.nutriflow.substitution.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

/**
 * Entity class representing a substitution rule for ingredients.
 * Defines what ingredient can replace another,
 * optionally avoiding a specific allergen.
 */
@Entity
@Table(name = "substitution_rules")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubstitutionRule {

    /** Unique identifier for the substitution rule. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The ingredient that may need substitution. */
    private String ingredient;

    /**
     * The allergen or ingredient to avoid when substituting.
     * May be null if the rule applies generally.
     */
    private String avoid;

    /** The suggested substitute ingredient. */
    private String substitute;

    /** Additional notes or details about the substitution rule. */
    @Column(columnDefinition = "text")
    private String note;
}
