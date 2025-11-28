package com.example.nutriflow.controller;

import com.example.nutriflow.recipe.controller.RecipeController;
import com.example.nutriflow.recipe.model.FavoriteRecipe;
import com.example.nutriflow.recipe.model.Recipe;
import com.example.nutriflow.recipe.service.RecipeService;
import com.example.nutriflow.recipe.controller.RecipeController;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Web layer tests for {@link RecipeController}.
 * Uses MockMvc and mocks {@link RecipeService}.
 */
@WebMvcTest(controllers = RecipeController.class)
class RecipeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RecipeService recipeService;

    @Test
    @DisplayName("GET /api/recipes/{id} → 200 with recipe")
    void getRecipeById_ok() throws Exception {
        Recipe r = new Recipe();
        r.setRecipeId(1);
        r.setTitle("Avocado Toast");

        Mockito.when(recipeService.getRecipeById(1))
                .thenReturn(Optional.of(r));

        mockMvc.perform(get("/api/recipes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Avocado Toast"));
    }

    @Test
    @DisplayName("GET /api/recipes/{id} → 404 when not found")
    void getRecipeById_notFound() throws Exception {
        Mockito.when(recipeService.getRecipeById(anyInt()))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/recipes/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName(
        "GET /api/recipes/{id} → 200 empty result")
    void getRecipeById_largeNumber() throws Exception {
        Mockito.when(recipeService.getRecipeById(999999))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/recipes/999999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/recipes/popular → 200 default list")
    void getPopular_default() throws Exception {
        Recipe a = new Recipe();
        a.setRecipeId(1);
        a.setTitle("A");
        Recipe b = new Recipe();
        b.setRecipeId(2);
        b.setTitle("B");
        Mockito.when(recipeService.getPopularRecipesDefault())
                .thenReturn(List.of(a, b));

        mockMvc.perform(get("/api/recipes/popular"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].title").value("A"));
    }

    @Test
    @DisplayName("GET /api/recipes/popular?limit=-1 → 400")
    void getPopular_badLimit() throws Exception {
        mockMvc.perform(get("/api/recipes/popular?limit=-1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(containsString("positive")));
    }

    @Test
    @DisplayName("GET /api/recipes/{userId}/favorites → 200 list")
    void getUserFavorites_ok() throws Exception {
        Recipe a = new Recipe();
        a.setRecipeId(1);
        a.setTitle("A");
        Mockito.when(recipeService.getUserFavoriteRecipes(5))
                .thenReturn(List.of(a));

        mockMvc.perform(get("/api/recipes/5/favorites"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].recipeId").value(1));
    }

    @Test
    @DisplayName("POST add favorite → 200 with FavoriteRecipe")
    void addFavorite_ok() throws Exception {
        FavoriteRecipe fav = new FavoriteRecipe(10, 7, 3, 0);
        Mockito.when(recipeService.addFavorite(7, 3)).thenReturn(fav);

        mockMvc.perform(post("/api/recipes/7/favorites/3")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.favoriteId").value(10))
                .andExpect(jsonPath("$.userId").value(7))
                .andExpect(jsonPath("$.recipeId").value(3));
    }

    @Test
    @DisplayName("POST add favorite when exists → 400 with error")
    void addFavorite_exists() throws Exception {
        Mockito.when(recipeService.addFavorite(7, 3))
                .thenThrow(new IllegalStateException("already"));

        mockMvc.perform(post("/api/recipes/7/favorites/3"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString("already")));
    }

    @Test
    @DisplayName("DELETE remove favorite → 200 with message")
    void removeFavorite_ok() throws Exception {
        mockMvc.perform(delete("/api/recipes/7/favorites/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Favorite removed successfully"));
        Mockito.verify(recipeService).removeFavorite(7, 3);
    }
}
