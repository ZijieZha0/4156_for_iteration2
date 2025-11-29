package com.example.nutriflow.recipe.controller;

import com.example.nutriflow.recipe.service.AIRecipeService;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * REST controller for managing AI recipe-related operations.
 * Provides endpoints for retrieving ai generated recipes.
 */
@RestController
@RequestMapping("/api/ai/recipes")
public class AIRecipeController {
    /** Service handling recipe-related logic. */
    @Autowired
    private AIRecipeService aiRecipeService;

    /**
     * GET endpoint to retrieve a recipe with the given ingredient.
     * First, it checks whether a recipe with the given ingredient
     * already exists in the database;
     * If not, then ask an LLM to recommend a recipe with the given ingredient.
     *
     * Example:
     * /api/ai/recipes/ingredient/{ingredient} - returns a recipe
     * with the given ingredient.
     * @param ingredient ingredient that the user wants to use
     * @return ResponseEntity containing the appropriate recipe
     */
    @GetMapping("ingredient/{ingredient}")
    public ResponseEntity<?> getAIRecipe(
        final @PathVariable String ingredient) {
        try {
            return ResponseEntity.ok(
                aiRecipeService.getAIRecipe(ingredient));
        } catch (Exception e) {
        return ResponseEntity.badRequest()
            .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * GET endpoint to retrieve an AI recommended recipe.
     * An LLM recommends a randomly generated delicious recipe.
     * Example:
     * /api/ai/recipes/recommendation - returns some AI recommended recipe
     * @return ResponseEntity containing a recommended recipe
     */
    @GetMapping("/recommendation")
    public ResponseEntity<?> getAIRecommendedRecipe() {
        try {
            return ResponseEntity.ok(
                aiRecipeService.getAIRecommendedRecipe());
        } catch (Exception e) {
        return ResponseEntity.badRequest()
            .body(Map.of("error", e.getMessage()));
        }
    }
}
