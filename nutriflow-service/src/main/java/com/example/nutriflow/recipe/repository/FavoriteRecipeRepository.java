package com.example.nutriflow.recipe.repository;

import com.example.nutriflow.recipe.model.FavoriteRecipe;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for FavoriteRecipe persistence and lookups.
 */
@Repository
public interface FavoriteRecipeRepository
    extends JpaRepository<FavoriteRecipe, Integer> {

  /**
   * Find all favorites for a given user.
   *
   * @param userId user identifier
   * @return list of FavoriteRecipe rows
   */
  List<FavoriteRecipe> findByUserId(Integer userId);

  /**
   * Check if a favorite link already exists for a user and recipe.
   *
   * @param userId   user identifier
   * @param recipeId recipe identifier
   * @return true if a row exists; false otherwise
   */
  boolean existsByUserIdAndRecipeId(Integer userId, Integer recipeId);
}
