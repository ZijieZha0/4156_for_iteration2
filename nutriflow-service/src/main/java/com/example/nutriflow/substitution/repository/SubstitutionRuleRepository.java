package com.example.nutriflow.substitution.repository;

import com.example.nutriflow.substitution.model.SubstitutionRule;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * Repository interface for accessing and managing
 *  {@link SubstitutionRule} entities.
 * Provides query methods for finding substitution rules
 *  by ingredient or allergen.
 */
public interface SubstitutionRuleRepository
     extends JpaRepository<SubstitutionRule, Long> {

    /**
     * Finds all substitution rules that match a given ingredient.
     *
     * @param ingredient the ingredient to search for
     * @return list of matching substitution rules
     */
    List<SubstitutionRule> findByIngredientIgnoreCase(String ingredient);

    /**
     * Finds substitution rules matching both ingredient
     *  and avoid criteria (case-insensitive).
     *
     * @param ingredient the ingredient to substitute
     * @param avoid the allergen or ingredient to avoid
     * @return list of matching substitution rules
     */
    List<SubstitutionRule>
         findByIngredientIgnoreCaseAndAvoidIgnoreCase(
            String ingredient, String avoid);
}
