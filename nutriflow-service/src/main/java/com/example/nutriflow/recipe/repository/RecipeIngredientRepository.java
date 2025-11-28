package com.example.nutriflow.recipe.repository;

import com.example.nutriflow.recipe.model.RecipeIngredient;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * Repository interface for accessing {@link RecipeIngredient} data.
 * Provides methods to retrieve ingredients associated with a specific recipe.
 */
public interface RecipeIngredientRepository
    extends JpaRepository<RecipeIngredient, Long> {

    /**
     * Finds all recipe ingredients by the given recipe ID.
     *
     * @param recipeId the ID of the recipe
     * @return list of ingredients belonging to the specified recipe
     */
    List<RecipeIngredient> findByRecipeId(Integer recipeId);
}
