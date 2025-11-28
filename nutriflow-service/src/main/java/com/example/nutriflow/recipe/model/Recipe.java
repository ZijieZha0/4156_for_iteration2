package com.example.nutriflow.recipe.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Entity representing a recipe stored in the system.
 * Captures core metadata, optional categorization, structured
 * ingredient and nutrition payloads, macronutrients, and
 * a popularity score for ranking.
 */
@Entity
@Table(name = "recipes", schema = "nutriflow")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Recipe {

    /** Unique identifier for the recipe. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recipe_id")
    private Integer recipeId;

    /** Title or name of the recipe. */
    @Column(name = "title", nullable = false)
    private String title;

    /** Estimated cook time in minutes. */
    @Column(name = "cook_time")
    private Integer cookTime;

    /**
     * Optional cuisines associated with the recipe
     *          (e.g., "Italian", "Mexican").
     * Stored as a PostgreSQL text array.
     */
    @Column(name = "cuisines", columnDefinition = "text[]")
    private String[] cuisines;

    /**
     * Optional tags for filtering or search (e.g., "vegetarian", "quick").
     * Stored as a PostgreSQL text array.
     */
    @Column(name = "tags", columnDefinition = "text[]")
    private String[] tags;

    /**
     * Structured ingredient list as JSON (JSONB in PostgreSQL).
     * Expected format: an array of objects with name, quantity, and unit.
     */
    @Column(name = "ingredients", columnDefinition = "jsonb")
    private String ingredients;

    /**
     * Structured nutrition payload as JSON (JSONB in PostgreSQL).
     * May include detailed nutrient breakdown per serving.
     */
    @Column(name = "nutrition", columnDefinition = "jsonb")
    private String nutrition;

    /** Calories per serving. */
    private BigDecimal calories;

    /** Carbohydrates per serving (grams). */
    private BigDecimal carbohydrates;

    /** Fat per serving (grams). */
    private BigDecimal fat;

    /** Fiber per serving (grams). */
    private BigDecimal fiber;

    /** Protein per serving (grams). */
    private BigDecimal protein;

    /** Popularity score used to rank recipes (higher means more popular). */
    @Column(name = "popularity_score")
    private Integer popularityScore;
}
