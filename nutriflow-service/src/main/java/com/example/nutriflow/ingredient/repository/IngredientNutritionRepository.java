package com.example.nutriflow.ingredient.repository;

import com.example.nutriflow.ingredient.model.IngredientNutrition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for IngredientNutrition entities.
 * Provides CRUD operations and custom queries for ingredient nutrition data.
 */
@Repository
public interface IngredientNutritionRepository
        extends JpaRepository<IngredientNutrition, Integer> {

    /**
     * Find ingredient by name (case-insensitive).
     *
     * @param name the ingredient name
     * @return optional containing the ingredient if found
     */
    @Query("SELECT i FROM IngredientNutrition i "
            + "WHERE LOWER(i.ingredientName) = LOWER(:name)")
    Optional<IngredientNutrition> findByIngredientNameIgnoreCase(
            @Param("name") String name);

    /**
     * Find all ingredients by category.
     *
     * @param category the ingredient category
     * @return list of ingredients in the category
     */
    List<IngredientNutrition> findByIngredientCategory(String category);

    /**
     * Find all verified ingredients.
     *
     * @return list of verified ingredients
     */
    List<IngredientNutrition> findByIsVerifiedTrue();

    /**
     * Search ingredients by name (partial match).
     *
     * @param keyword the search keyword
     * @return list of matching ingredients
     */
    @Query("SELECT i FROM IngredientNutrition i "
            + "WHERE LOWER(i.ingredientName) LIKE "
            + "LOWER(CONCAT('%', :keyword, '%'))")
    List<IngredientNutrition> searchByName(@Param("keyword") String keyword);

    /**
     * Check if ingredient exists by name.
     *
     * @param name the ingredient name
     * @return true if exists
     */
    boolean existsByIngredientNameIgnoreCase(
            String name);
}

