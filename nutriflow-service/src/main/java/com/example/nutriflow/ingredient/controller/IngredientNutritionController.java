package com.example.nutriflow.ingredient.controller;

import com.example.nutriflow.ingredient.model.IngredientNutrition;
import com.example.nutriflow.ingredient.service.IngredientNutritionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * REST controller for ingredient nutrition operations.
 * Allows users/programmers to CRUD ingredient nutrition data.
 * All nutrition values are per 100g unless otherwise specified.
 */
@RestController
@RequestMapping("/api/ingredients")
public class IngredientNutritionController {

    /** Logger for this controller. */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(IngredientNutritionController.class);

    /** Service for ingredient nutrition operations. */
    @Autowired
    private IngredientNutritionService ingredientNutritionService;

    /**
     * Get all ingredients.
     *
     * GET /api/ingredients
     *
     * @return list of all ingredients
     */
    @GetMapping
    public ResponseEntity<List<IngredientNutrition>> getAllIngredients() {
        LOGGER.info("[API_CALL] GET /api/ingredients");

        final List<IngredientNutrition> ingredients =
                ingredientNutritionService.getAllIngredients();

        LOGGER.info("[API_RESPONSE] Found {} ingredients",
                ingredients.size());

        return ResponseEntity.ok(ingredients);
    }

    /**
     * Get ingredient by ID.
     *
     * GET /api/ingredients/{id}
     *
     * @param id the ingredient ID
     * @return the ingredient if found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Object> getIngredientById(
            @PathVariable final Integer id) {
        LOGGER.info("[API_CALL] GET /api/ingredients/{}", id);

        final Optional<IngredientNutrition> ingredient =
                ingredientNutritionService.getIngredientById(id);

        if (ingredient.isEmpty()) {
            final Map<String, String> error = new HashMap<>();
            error.put("error", "Ingredient not found with ID: " + id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }

        return ResponseEntity.ok(ingredient.get());
    }

    /**
     * Search ingredients by keyword.
     *
     * GET /api/ingredients/search?keyword=chicken
     *
     * @param keyword the search keyword
     * @return list of matching ingredients
     */
    @GetMapping("/search")
    public ResponseEntity<List<IngredientNutrition>> searchIngredients(
            @RequestParam final String keyword) {
        LOGGER.info("[API_CALL] GET /api/ingredients/search?keyword={}",
                keyword);

        final List<IngredientNutrition> ingredients =
                ingredientNutritionService.searchIngredients(keyword);

        LOGGER.info("[API_RESPONSE] Found {} matching ingredients",
                ingredients.size());

        return ResponseEntity.ok(ingredients);
    }

    /**
     * Get ingredients by category.
     *
     * GET /api/ingredients/category/{category}
     *
     * @param category the ingredient category
     * @return list of ingredients in the category
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<List<IngredientNutrition>>
            getIngredientsByCategory(
            @PathVariable final String category) {
        LOGGER.info("[API_CALL] GET /api/ingredients/category/{}",
                category);

        final List<IngredientNutrition> ingredients =
                ingredientNutritionService.getIngredientsByCategory(category);

        LOGGER.info("[API_RESPONSE] Found {} ingredients in category {}",
                ingredients.size(), category);

        return ResponseEntity.ok(ingredients);
    }

    /**
     * Get ingredient by name.
     *
     * GET /api/ingredients/name/{name}
     *
     * @param name the ingredient name
     * @return the ingredient if found
     */
    @GetMapping("/name/{name}")
    public ResponseEntity<Object> getIngredientByName(
            @PathVariable final String name) {
        LOGGER.info("[API_CALL] GET /api/ingredients/name/{}", name);

        final Optional<IngredientNutrition> ingredient =
                ingredientNutritionService.getIngredientByName(name);

        if (ingredient.isEmpty()) {
            final Map<String, String> error = new HashMap<>();
            error.put("error", "Ingredient not found: " + name);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }

        return ResponseEntity.ok(ingredient.get());
    }

    /**
     * Calculate nutrition for a specific amount.
     *
     * GET /api/ingredients/calculate?name=chicken&amount=200
     *
     * Request parameters:
     * - name: ingredient name
     * - amount: amount in grams
     *
     * @param name   the ingredient name
     * @param amount the amount in grams
     * @return calculated nutrition values
     */
    @GetMapping("/calculate")
    public ResponseEntity<Object> calculateNutrition(
            @RequestParam final String name,
            @RequestParam final double amount) {
        LOGGER.info("[API_CALL] GET /api/ingredients/calculate"
                + "?name={}&amount={}g", name, amount);

        try {
            final IngredientNutrition calculated =
                    ingredientNutritionService
                            .calculateNutrition(name, amount);

            final Map<String, Object> response = new HashMap<>();
            response.put("ingredient", name);
            response.put("amount_grams", amount);
            response.put("calories",
                    calculated.getCalories());
            response.put("protein",
                    calculated.getProtein());
            response.put("carbohydrates",
                    calculated.getCarbohydrates());
            response.put("fat", calculated.getFat());
            response.put("fiber", calculated.getFiber());

            LOGGER.info("[API_RESPONSE] Calculated: {} cal for {}g of {}",
                    calculated.getCalories(), amount, name);

            return ResponseEntity.ok(response);

        } catch (final IllegalArgumentException e) {
            final Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    /**
     * Create a new ingredient.
     *
     * POST /api/ingredients
     *
     * Request body example:
     * {
     *   "ingredientName": "chicken breast",
     *   "ingredientCategory": "meat",
     *   "calories": 165,
     *   "protein": 31,
     *   "carbohydrates": 0,
     *   "fat": 3.6,
     *   "fiber": 0,
     *   "source": "USDA",
     *   "isVerified": true
     * }
     *
     * @param ingredient the ingredient to create
     * @param updatedBy  the user creating (optional header)
     * @return created ingredient
     */
    @PostMapping
    public ResponseEntity<Object> createIngredient(
            @RequestBody final IngredientNutrition ingredient,
            @RequestParam(required = false,
                    defaultValue = "system") final String updatedBy) {
        LOGGER.info("[API_CALL] POST /api/ingredients - Creating: {}",
                ingredient.getIngredientName());

        try {
            final IngredientNutrition saved =
                    ingredientNutritionService
                            .saveIngredient(ingredient, updatedBy);

            LOGGER.info("[API_RESPONSE] Created ingredient ID: {}",
                    saved.getIngredientId());

            return ResponseEntity.status(HttpStatus.CREATED).body(saved);

        } catch (final Exception e) {
            LOGGER.error("[API_ERROR] Error creating ingredient: {}",
                    e.getMessage());

            final Map<String, String> error = new HashMap<>();
            error.put("error", "Error creating ingredient: "
                    + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * Update an existing ingredient.
     *
     * PUT /api/ingredients/{id}
     *
     * Request body: updated ingredient data
     *
     * @param id         the ingredient ID
     * @param ingredient the updated ingredient data
     * @param updatedBy  the user updating (optional header)
     * @return updated ingredient
     */
    @PutMapping("/{id}")
    public ResponseEntity<Object> updateIngredient(
            @PathVariable final Integer id,
            @RequestBody final IngredientNutrition ingredient,
            @RequestParam(required = false,
                    defaultValue = "system") final String updatedBy) {
        LOGGER.info("[API_CALL] PUT /api/ingredients/{} - Updating", id);

        if (!ingredientNutritionService.getIngredientById(id).isPresent()) {
            final Map<String, String> error = new HashMap<>();
            error.put("error", "Ingredient not found with ID: " + id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }

        ingredient.setIngredientId(id);

        try {
            final IngredientNutrition saved =
                    ingredientNutritionService
                            .saveIngredient(ingredient, updatedBy);

            LOGGER.info("[API_RESPONSE] Updated ingredient ID: {}",
                    saved.getIngredientId());

            return ResponseEntity.ok(saved);

        } catch (final Exception e) {
            LOGGER.error("[API_ERROR] Error updating ingredient: {}",
                    e.getMessage());

            final Map<String, String> error = new HashMap<>();
            error.put("error", "Error updating ingredient: "
                    + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * Update only nutrition values for an ingredient.
     *
     * PATCH /api/ingredients/{id}/nutrition
     *
     * Request body example:
     * {
     *   "calories": 170,
     *   "protein": 32,
     *   "carbohydrates": 0.5,
     *   "fat": 4.0
     * }
     *
     * @param id        the ingredient ID
     * @param values    map of nutrition values to update
     * @param updatedBy the user updating
     * @return updated ingredient
     */
    @PutMapping("/{id}/nutrition")
    public ResponseEntity<Object> updateNutritionValues(
            @PathVariable final Integer id,
            @RequestBody final Map<String, BigDecimal> values,
            @RequestParam(required = false,
                    defaultValue = "system") final String updatedBy) {
        LOGGER.info("[API_CALL] PATCH /api/ingredients/{}/nutrition", id);

        try {
            final IngredientNutrition updated =
                    ingredientNutritionService.updateNutritionValues(
                            id,
                            values.get("calories"),
                            values.get("protein"),
                            values.get("carbohydrates"),
                            values.get("fat"),
                            updatedBy);

            LOGGER.info("[API_RESPONSE] Updated nutrition for ID: {}", id);

            return ResponseEntity.ok(updated);

        } catch (final IllegalArgumentException e) {
            final Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);

        } catch (final Exception e) {
            LOGGER.error("[API_ERROR] Error updating nutrition: {}",
                    e.getMessage());

            final Map<String, String> error = new HashMap<>();
            error.put("error", "Error updating nutrition: "
                    + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * Delete an ingredient.
     *
     * DELETE /api/ingredients/{id}
     *
     * @param id the ingredient ID
     * @return success message
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteIngredient(
            @PathVariable final Integer id) {
        LOGGER.info("[API_CALL] DELETE /api/ingredients/{}", id);

        final boolean deleted =
                ingredientNutritionService.deleteIngredient(id);

        if (!deleted) {
            final Map<String, String> error = new HashMap<>();
            error.put("error", "Ingredient not found with ID: " + id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }

        final Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Ingredient deleted successfully");
        response.put("deletedId", id);

        LOGGER.info("[API_RESPONSE] Deleted ingredient ID: {}", id);

        return ResponseEntity.ok(response);
    }
}

