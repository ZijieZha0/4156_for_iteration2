package com.example.nutriflow.recipe.controller;

import com.example.nutriflow.recipe.model.FavoriteRecipe;
import com.example.nutriflow.recipe.model.Recipe;
import com.example.nutriflow.recipe.service.RecipeService;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing recipe-related operations.
 * Provides endpoints for retrieving recipes, popular recipes, and user favorite
 * recipes.
 */
@RestController
@RequestMapping("/api/recipes")
public class RecipeController {

    /** Service handling recipe-related logic. */
    @Autowired
    private RecipeService recipeService;

    /**
    * GET endpoint to retrieve a recipe by its unique ID.
    *
    * @param id the ID of the recipe to retrieve
    * @return ResponseEntity containing the recipe if found,
    *         or 404 if not found
    */
    @GetMapping("/{id}")
    public ResponseEntity<Recipe> getRecipe(
        final @PathVariable Integer id) {
            return recipeService.getRecipeById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
    * GET endpoint to retrieve the most popular recipes.
    * Returns the top 5 recipes default (rank by popularity score).
    * Users can specify a custom limit using the 'limit'
    * query parameter.
    *
    * Example:
    * - /api/recipes/popular → returns top 5
    * - /api/recipes/popular?limit=10 → returns top 10
    *
    * @param limit optional query parameter to specify how many
    *              recipes to return
    * @return ResponseEntity containing a list of popular recipes,
    *         or 400 Bad Request if limit is not a positive integer
    */
    @GetMapping("/popular")
    public ResponseEntity<?> getPopularRecipes(
        final @RequestParam(name = "limit",
        required = false) Integer limit) {

    if (limit == null) {
        return ResponseEntity.ok(
            recipeService.getPopularRecipesDefault());
    }

    if (limit <= 0) {
        return ResponseEntity.badRequest()
            .body(Map.of("error",
                "limit must be a positive integer"));
    }

    return ResponseEntity.ok(
        recipeService.getPopularRecipes(limit));
    }

    /**
    * GET endpoint to retrieve all favorite recipes for a specific user.
    * Currently returns an empty list placeholder until favorite data is
    * integrated.
    *
    * Example:
    * - /api/recipes/1/favorites → returns favorites for user with ID 1
    *
    * @param userId the ID of the user
    * @return ResponseEntity containing a list of the user's favorite
    *         recipes
    */
    @GetMapping("/{userId}/favorites")
    public ResponseEntity<List<Recipe>> getUserFavorites(
        final @PathVariable Integer userId) {

    return ResponseEntity.ok(
        recipeService.getUserFavoriteRecipes(userId));
    }

    /**
    * POST endpoint to add a recipe to a user's favorites.
    *
    * @param userId   the user ID
    * @param recipeId the recipe ID
    * @return 200 OK with FavoriteRecipe, or 400 if already exists
    */
    @PostMapping("/{userId}/favorites/{recipeId}")
    public ResponseEntity<?> addFavorite(
            final @PathVariable Integer userId,
            final @PathVariable Integer recipeId) {

        try {
            final FavoriteRecipe fav =
                recipeService.addFavorite(userId, recipeId);
            return ResponseEntity.ok(fav);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }

    /**
        * DELETE endpoint to remove a recipe from a user's favorites.
        *
        * @param userId   the user ID
        * @param recipeId the recipe ID
        * @return 200 OK with a confirmation message
        */
    @DeleteMapping("/{userId}/favorites/{recipeId}")
    public ResponseEntity<Map<String, String>> removeFavorite(
        final @PathVariable Integer userId,
        final @PathVariable Integer recipeId) {

        recipeService.removeFavorite(userId, recipeId);
        return ResponseEntity.ok(
            Map.of("message",
                    "Favorite removed successfully"));
    }
}
