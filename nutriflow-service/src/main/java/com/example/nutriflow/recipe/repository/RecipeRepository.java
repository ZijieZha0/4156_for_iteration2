package com.example.nutriflow.recipe.repository;

import com.example.nutriflow.recipe.model.Recipe;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for {@link Recipe} entities.
 * Provides CRUD operations and a query for popular recipes.
 */
@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Integer> {

    /**
     * Find recipes ordered by popularity score in descending order.
     * The supplied {@code Pageable} controls the maximum number returned.
     *
     * @param pageable pagination/limit information (e.g., PageRequest.of(0, 5))
     * @return list of recipes ordered by descending popularity
     */
    @Query("SELECT r FROM Recipe r ORDER BY r.popularityScore DESC")
    List<Recipe> findPopularRecipes(Pageable pageable);
}
