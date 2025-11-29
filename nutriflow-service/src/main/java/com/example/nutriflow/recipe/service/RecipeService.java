package com.example.nutriflow.recipe.service;

import com.example.nutriflow.recipe.model.FavoriteRecipe;
import com.example.nutriflow.recipe.model.Recipe;
import com.example.nutriflow.recipe.repository.FavoriteRecipeRepository;
import com.example.nutriflow.recipe.repository.RecipeRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

/**
 * Service class for handling business logic related to recipes.
 * Provides methods for fetching recipes, popular recipes, and user favorites.
 */
@Service
public class RecipeService {

    /** Repository for accessing recipe data. */
    @Autowired
    private RecipeRepository recipeRepository;

    /** Repository for accessing favorite-recipe associations. */
    @Autowired
    private FavoriteRecipeRepository favoriteRecipeRepository;

    /** Default number of recipes when limit is not specified. */
    private static final int DEFAULT_POPULAR_LIMIT = 5;

    /**
     * Retrieves a recipe by its unique ID.
     *
     * @param id the ID of the recipe
     * @return Optional containing the recipe if found, or empty if not
     */
    public Optional<Recipe> getRecipeById(final Integer id) {
        return recipeRepository.findById(id);
    }

    /**
     * Retrieves the top {@value #DEFAULT_POPULAR_LIMIT} most popular recipes
     * based on popularity score.
     *
     * @return list of the most popular recipes
     */
    public List<Recipe> getPopularRecipesDefault() {
        return recipeRepository.findPopularRecipes(
                PageRequest.of(0, DEFAULT_POPULAR_LIMIT));
    }

    /**
     * Retrieves a custom number of popular recipes based on the provided limit.
     * Defaults to {@value #DEFAULT_POPULAR_LIMIT} if the limit is invalid.
     *
     * @param limit the number of recipes to return
     * @return list of popular recipes up to the specified limit
     */
    public List<Recipe> getPopularRecipes(final int limit) {
        final int validLimit = limit > 0 ? limit : DEFAULT_POPULAR_LIMIT;
        return recipeRepository.findPopularRecipes(
                PageRequest.of(0, validLimit));
    }

    /**
     * Retrieves all recipes from the database.
     *
     * @return list of all recipes
     */
    public List<Recipe> getAllRecipes() {
        return recipeRepository.findAll();
    }

    /**
     * Retrieves all favorite recipes for a given user.
     * If the user has no favorites, returns an empty list.
     *
     * @param userId the ID of the user
     * @return list of Recipe entities the user favorited
     */
    public List<Recipe> getUserFavoriteRecipes(final Integer userId) {
        final List<Integer> recipeIds = favoriteRecipeRepository
                .findByUserId(userId)
                .stream()
                .map(FavoriteRecipe::getRecipeId)
                .collect(Collectors.toList());

        return recipeRepository.findAllById(recipeIds);
    }

    /**
     * Add a recipe to a user's favorites.
     * Throws IllegalStateException if it already exists.
     *
     * @param userId   the user ID
     * @param recipeId the recipe ID
     * @return the persisted FavoriteRecipe row
     */
    public FavoriteRecipe addFavorite(
            final Integer userId,
            final Integer recipeId) {

        if (favoriteRecipeRepository
                .existsByUserIdAndRecipeId(userId, recipeId)) {
            throw new IllegalStateException(
                    "Recipe already in favorites");
        }

        final FavoriteRecipe favorite = new FavoriteRecipe();
        favorite.setUserId(userId);
        favorite.setRecipeId(recipeId);
        favorite.setTimesUsed(0);

        return favoriteRecipeRepository.save(favorite);
    }

    /**
     * Remove a recipe from a user's favorites.
     * A no-op if the mapping does not exist.
     *
     * @param userId   the user ID
     * @param recipeId the recipe ID
     */
    public void removeFavorite(
            final Integer userId,
            final Integer recipeId) {

        final List<FavoriteRecipe> rows = favoriteRecipeRepository
                .findByUserId(userId)
                .stream()
                .filter(f -> f.getRecipeId().equals(recipeId))
                .collect(Collectors.toList());

        favoriteRecipeRepository.deleteAll(rows);
    }
}
