package com.example.nutriflow.service;

import com.example.nutriflow.recipe.model.FavoriteRecipe;
import com.example.nutriflow.recipe.model.Recipe;
import com.example.nutriflow.recipe.repository.FavoriteRecipeRepository;
import com.example.nutriflow.recipe.repository.RecipeRepository;
import com.example.nutriflow.recipe.service.RecipeService;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link RecipeService} using Mockito.
 */
@ExtendWith(MockitoExtension.class)
class RecipeServiceTest {

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private FavoriteRecipeRepository favoriteRecipeRepository;

    @InjectMocks
    private RecipeService recipeService;

    @Test
    @DisplayName("getRecipeById delegates to repository")
    void getRecipeById() {
        Recipe r = new Recipe();
        r.setRecipeId(42);
        when(recipeRepository.findById(42)).thenReturn(Optional.of(r));

        Optional<Recipe> out = recipeService.getRecipeById(42);

        assertThat(out).contains(r);
        verify(recipeRepository).findById(42);
    }

    @Test
    @DisplayName("getPopularRecipes uses valid limit and pageable")
    void getPopularRecipes_limit() {
        when(recipeRepository.findPopularRecipes(any(PageRequest.class)))
                .thenReturn(List.of());

        recipeService.getPopularRecipes(3);

        ArgumentCaptor<PageRequest> cap = ArgumentCaptor.forClass(PageRequest.class);
        verify(recipeRepository).findPopularRecipes(cap.capture());
        assertThat(cap.getValue().getPageSize()).isEqualTo(3);
    }

    @Test
    @DisplayName("getUserFavoriteRecipes maps FavoriteRecipe → Recipe IDs")
    void getUserFavoriteRecipes_mapsIds() {
        FavoriteRecipe f1 = new FavoriteRecipe(1, 9, 100, 0);
        FavoriteRecipe f2 = new FavoriteRecipe(2, 9, 200, 0);

        when(favoriteRecipeRepository.findByUserId(9))
                .thenReturn(List.of(f1, f2));

        Recipe r1 = new Recipe();
        r1.setRecipeId(100);
        Recipe r2 = new Recipe();
        r2.setRecipeId(200);
        when(recipeRepository.findAllById(List.of(100, 200)))
                .thenReturn(List.of(r1, r2));

        List<Recipe> out = recipeService.getUserFavoriteRecipes(9);

        assertThat(out).extracting(Recipe::getRecipeId).containsExactlyInAnyOrder(100, 200);
    }

    @Test
    @DisplayName("addFavorite inserts when not existing; throws when exists")
    void addFavorite_behaviour() {
        // not existing → save
        when(favoriteRecipeRepository.existsByUserIdAndRecipeId(7, 3)).thenReturn(false);
        when(favoriteRecipeRepository.save(any(FavoriteRecipe.class)))
                .thenAnswer(inv -> {
                    FavoriteRecipe f = inv.getArgument(0);
                    f.setFavoriteId(10);
                    return f;
                });

        FavoriteRecipe saved = recipeService.addFavorite(7, 3);
        assertThat(saved.getFavoriteId()).isEqualTo(10);
        assertThat(saved.getUserId()).isEqualTo(7);
        assertThat(saved.getRecipeId()).isEqualTo(3);
        assertThat(saved.getTimesUsed()).isZero();

        // existing → throws
        when(favoriteRecipeRepository.existsByUserIdAndRecipeId(7, 3)).thenReturn(true);
        assertThatThrownBy(() -> recipeService.addFavorite(7, 3))
                .isInstanceOf(IllegalStateException.class);
    }
    @Test
    @DisplayName("removeFavorite deletes rows for that (user, recipe)")
    void removeFavorite_deletes() {
        FavoriteRecipe a = new FavoriteRecipe(1, 7, 3, 0);
        FavoriteRecipe b = new FavoriteRecipe(2, 7, 99, 0);

        when(favoriteRecipeRepository.findByUserId(7))
                .thenReturn(List.of(a, b));

        recipeService.removeFavorite(7, 3);

        // Verify deleteAll called with iterable containing only recipeId 3
        verify(favoriteRecipeRepository).deleteAll(argThat(iterable -> {
            if (iterable == null) return false;
            long count = StreamSupport.stream(iterable.spliterator(), false)
                    .filter(f -> f.getRecipeId() == 3)
                    .count();
            return count == 1;
        }));
    }
}
