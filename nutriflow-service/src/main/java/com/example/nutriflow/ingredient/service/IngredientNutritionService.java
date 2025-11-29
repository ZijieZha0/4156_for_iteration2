package com.example.nutriflow.ingredient.service;

import com.example.nutriflow.ingredient.model.IngredientNutrition;
import com.example.nutriflow.ingredient.repository.IngredientNutritionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service for managing ingredient nutrition data.
 * Allows users/programmers to CRUD ingredient nutrition information.
 */
@Service
public class IngredientNutritionService {

    /** Logger for this service. */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(IngredientNutritionService.class);

    /** Repository for ingredient nutrition data. */
    @Autowired
    private IngredientNutritionRepository ingredientNutritionRepository;

    /**
     * Get all ingredients.
     *
     * @return list of all ingredients
     */
    public List<IngredientNutrition> getAllIngredients() {
        LOGGER.info("Fetching all ingredients");
        return ingredientNutritionRepository.findAll();
    }

    /**
     * Get ingredient by ID.
     *
     * @param id the ingredient ID
     * @return optional containing the ingredient if found
     */
    public Optional<IngredientNutrition> getIngredientById(
            final Integer id) {
        LOGGER.info("Fetching ingredient by ID: {}", id);
        return ingredientNutritionRepository.findById(id);
    }

    /**
     * Get ingredient by name (case-insensitive).
     *
     * @param name the ingredient name
     * @return optional containing the ingredient if found
     */
    public Optional<IngredientNutrition> getIngredientByName(
            final String name) {
        LOGGER.info("Fetching ingredient by name: {}", name);
        return ingredientNutritionRepository
                .findByIngredientNameIgnoreCase(name);
    }

    /**
     * Search ingredients by keyword.
     *
     * @param keyword the search keyword
     * @return list of matching ingredients
     */
    public List<IngredientNutrition> searchIngredients(
            final String keyword) {
        LOGGER.info("Searching ingredients with keyword: {}", keyword);
        return ingredientNutritionRepository.searchByName(keyword);
    }

    /**
     * Get ingredients by category.
     *
     * @param category the ingredient category
     * @return list of ingredients in the category
     */
    public List<IngredientNutrition> getIngredientsByCategory(
            final String category) {
        LOGGER.info("Fetching ingredients by category: {}", category);
        return ingredientNutritionRepository
                .findByIngredientCategory(category);
    }

    /**
     * Create or update ingredient nutrition data.
     * If an ingredient with the same name already exists,
     * it will be updated instead of creating a duplicate.
     *
     * @param ingredient the ingredient to save
     * @param updatedBy  the user/system making the update
     * @return saved ingredient
     */
    @Transactional
    public IngredientNutrition saveIngredient(
            final IngredientNutrition ingredient,
            final String updatedBy) {
        final LocalDateTime now = LocalDateTime.now();

        if (ingredient.getIngredientId() == null) {
            // Check if ingredient with same name already exists
            final Optional<IngredientNutrition> existingOpt =
                    getIngredientByName(ingredient.getIngredientName());

            if (existingOpt.isPresent()) {
                // Update existing ingredient instead of creating duplicate
                final IngredientNutrition existing = existingOpt.get();
                LOGGER.info(
                        "Ingredient '{}' already exists (ID: {}). "
                                + "Updating instead of creating new.",
                        ingredient.getIngredientName(),
                        existing.getIngredientId());

                // Copy new values to existing ingredient
                if (ingredient.getIngredientCategory() != null) {
                    existing.setIngredientCategory(
                            ingredient.getIngredientCategory());
                }
                if (ingredient.getCalories() != null) {
                    existing.setCalories(ingredient.getCalories());
                }
                if (ingredient.getProtein() != null) {
                    existing.setProtein(ingredient.getProtein());
                }
                if (ingredient.getCarbohydrates() != null) {
                    existing.setCarbohydrates(ingredient.getCarbohydrates());
                }
                if (ingredient.getFat() != null) {
                    existing.setFat(ingredient.getFat());
                }
                if (ingredient.getFiber() != null) {
                    existing.setFiber(ingredient.getFiber());
                }
                if (ingredient.getIron() != null) {
                    existing.setIron(ingredient.getIron());
                }
                if (ingredient.getCalcium() != null) {
                    existing.setCalcium(ingredient.getCalcium());
                }
                if (ingredient.getVitaminA() != null) {
                    existing.setVitaminA(ingredient.getVitaminA());
                }
                if (ingredient.getVitaminC() != null) {
                    existing.setVitaminC(ingredient.getVitaminC());
                }
                if (ingredient.getVitaminD() != null) {
                    existing.setVitaminD(ingredient.getVitaminD());
                }
                if (ingredient.getSodium() != null) {
                    existing.setSodium(ingredient.getSodium());
                }
                if (ingredient.getPotassium() != null) {
                    existing.setPotassium(ingredient.getPotassium());
                }
                if (ingredient.getDescription() != null) {
                    existing.setDescription(ingredient.getDescription());
                }
                if (ingredient.getSource() != null) {
                    existing.setSource(ingredient.getSource());
                }
                if (ingredient.getUnit() != null) {
                    existing.setUnit(ingredient.getUnit());
                }

                existing.setUpdatedAt(now);
                existing.setUpdatedBy(updatedBy);

                return ingredientNutritionRepository.save(existing);
            }

            // New ingredient - set creation fields
            ingredient.setCreatedAt(now);
            ingredient.setCreatedBy(updatedBy);
            LOGGER.info("Creating new ingredient: {}",
                    ingredient.getIngredientName());
        } else {
            // Update existing ingredient
            LOGGER.info("Updating ingredient ID: {}",
                    ingredient.getIngredientId());
        }

        ingredient.setUpdatedAt(now);
        ingredient.setUpdatedBy(updatedBy);

        return ingredientNutritionRepository.save(ingredient);
    }

    /**
     * Update specific nutritional values for an ingredient.
     *
     * @param ingredientId  the ingredient ID
     * @param calories      new calorie value (optional)
     * @param protein       new protein value (optional)
     * @param carbs         new carbs value (optional)
     * @param fat           new fat value (optional)
     * @param updatedBy     the user making the update
     * @return updated ingredient
     * @throws IllegalArgumentException if ingredient not found
     */
    @Transactional
    public IngredientNutrition updateNutritionValues(
            final Integer ingredientId,
            final BigDecimal calories,
            final BigDecimal protein,
            final BigDecimal carbs,
            final BigDecimal fat,
            final String updatedBy) {

        final Optional<IngredientNutrition> ingredientOpt =
                ingredientNutritionRepository.findById(ingredientId);

        if (ingredientOpt.isEmpty()) {
            throw new IllegalArgumentException(
                    "Ingredient not found with ID: " + ingredientId);
        }

        final IngredientNutrition ingredient = ingredientOpt.get();

        if (calories != null) {
            ingredient.setCalories(calories);
        }
        if (protein != null) {
            ingredient.setProtein(protein);
        }
        if (carbs != null) {
            ingredient.setCarbohydrates(carbs);
        }
        if (fat != null) {
            ingredient.setFat(fat);
        }

        ingredient.setUpdatedAt(LocalDateTime.now());
        ingredient.setUpdatedBy(updatedBy);

        LOGGER.info("Updated nutrition values for ingredient ID: {}",
                ingredientId);

        return ingredientNutritionRepository.save(ingredient);
    }

    /**
     * Delete ingredient by ID.
     *
     * @param id the ingredient ID
     * @return true if deleted, false if not found
     */
    @Transactional
    public boolean deleteIngredient(final Integer id) {
        if (ingredientNutritionRepository.existsById(id)) {
            ingredientNutritionRepository.deleteById(id);
            LOGGER.info("Deleted ingredient ID: {}", id);
            return true;
        }
        LOGGER.warn("Ingredient not found for deletion, ID: {}", id);
        return false;
    }

    /**
     * Calculate nutrition for a given amount of ingredient.
     *
     * @param ingredientName the ingredient name
     * @param amountInGrams  the amount in grams
     * @return map of nutritional values
     * @throws IllegalArgumentException if ingredient not found
     */
    public IngredientNutrition calculateNutrition(
            final String ingredientName,
            final double amountInGrams) {

        final Optional<IngredientNutrition> ingredientOpt =
                getIngredientByName(ingredientName);

        if (ingredientOpt.isEmpty()) {
            throw new IllegalArgumentException(
                    "Ingredient not found: " + ingredientName);
        }

        final IngredientNutrition base = ingredientOpt.get();
        final IngredientNutrition calculated = new IngredientNutrition();

        // Calculate for the specific amount (all base values are per 100g)
        final double ratio = amountInGrams / 100.0;

        calculated.setIngredientName(base.getIngredientName());
        calculated.setIngredientCategory(base.getIngredientCategory());

        calculated.setCalories(base.getCalories() != null
                ? base.getCalories().multiply(BigDecimal.valueOf(ratio))
                : BigDecimal.ZERO);

        calculated.setProtein(base.getProtein() != null
                ? base.getProtein().multiply(BigDecimal.valueOf(ratio))
                : BigDecimal.ZERO);

        calculated.setCarbohydrates(base.getCarbohydrates() != null
                ? base.getCarbohydrates().multiply(BigDecimal.valueOf(ratio))
                : BigDecimal.ZERO);

        calculated.setFat(base.getFat() != null
                ? base.getFat().multiply(BigDecimal.valueOf(ratio))
                : BigDecimal.ZERO);

        calculated.setFiber(base.getFiber() != null
                ? base.getFiber().multiply(BigDecimal.valueOf(ratio))
                : BigDecimal.ZERO);

        LOGGER.info("Calculated nutrition for {}g of {}: {} calories",
                amountInGrams, ingredientName, calculated.getCalories());

        return calculated;
    }
}

