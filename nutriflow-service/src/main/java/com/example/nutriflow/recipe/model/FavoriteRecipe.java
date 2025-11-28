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

/**
 * Entity representing a user's association to a favorite recipe.
 * Persisted in nutriflow.favorite_recipes.
 */
@Entity
@Table(name = "favorite_recipes", schema = "nutriflow")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteRecipe {

    /** Unique identifier for the favorite recipe entry. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "favorite_id")
    private Integer favoriteId;

    /** Identifier of the user who favorited the recipe. */
    @Column(name = "user_id", nullable = false)
    private Integer userId;

    /** Identifier of the recipe that was favorited. */
    @Column(name = "recipe_id", nullable = false)
    private Integer recipeId;

    /** Number of times the user has used or accessed this recipe. */
    @Column(name = "times_used")
    private Integer timesUsed;
}
