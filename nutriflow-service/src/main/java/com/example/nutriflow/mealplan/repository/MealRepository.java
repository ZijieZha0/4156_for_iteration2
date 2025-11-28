package com.example.nutriflow.mealplan.repository;

import com.example.nutriflow.mealplan.model.Meal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for Meal entity.
 * Provides database access methods for meal operations.
 */
@Repository
public interface MealRepository extends JpaRepository<Meal, Integer> {

    /**
     * Find all meals by recipe ID.
     *
     * @param recipeId the recipe ID
     * @return list of meals with the specified recipe
     */
    List<Meal> findByRecipeId(Integer recipeId);

    /**
     * Find all meals by meal type.
     *
     * @param mealType the meal type (breakfast, lunch, dinner, snack)
     * @return list of meals of the specified type
     */
    List<Meal> findByMealType(String mealType);
}

