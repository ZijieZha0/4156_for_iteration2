package com.example.nutriflow.recipe.model;

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
 * Entity class representing an ingredient belonging to a recipe.
 * Includes ingredient name, quantity, unit, and allergen tags.
 */
@Entity
@Table(name = "recipe_ingredients")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecipeIngredient {

    /** Unique identifier for this recipe ingredient record. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The ID of the recipe this ingredient belongs to. */
    @Column(name = "recipe_id", nullable = false)
    private Integer recipeId;

    /** The name of the ingredient (e.g., "almond milk"). */
    private String ingredient;

    /** The quantity amount of the ingredient. */
    private Double quantity;

    /** The measurement unit for the ingredient (e.g., "ml", "g"). */
    private String unit;

    /** List of allergen tags associated with this ingredient. */
    @Column(name = "allergen_tags", columnDefinition = "text[]")
    private String[] allergenTags;
}
