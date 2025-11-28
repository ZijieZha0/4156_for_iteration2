package com.example.nutriflow.controller;

import com.example.nutriflow.recipe.controller.AIRecipeController;
import com.example.nutriflow.recipe.model.Recipe;
import com.example.nutriflow.recipe.service.AIRecipeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = AIRecipeController.class)
class AIRecipeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AIRecipeService aiRecipeService;

    @Test
    @DisplayName("GET ingredient endpoint returns recipe from service")
    void getAIRecipe_ok() throws Exception {
        Recipe recipe = new Recipe();
        recipe.setRecipeId(7);
        recipe.setTitle("Milkshake");

        Mockito.when(aiRecipeService.getAIRecipe(eq("milk")))
                .thenReturn(recipe);

        mockMvc.perform(get("/api/ai/recipes/ingredient/milk"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recipeId").value(7))
                .andExpect(jsonPath("$.title").value("Milkshake"));
    }

    @Test
    @DisplayName("GET ingredient endpoint returns 400 on service exception")
    void getAIRecipe_error() throws Exception {
        Mockito.when(aiRecipeService.getAIRecipe("milk"))
                .thenThrow(new IllegalStateException("LLM down"));

        mockMvc.perform(get("/api/ai/recipes/ingredient/milk"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(containsString("LLM down")));
    }

    @Test
    @DisplayName("GET recommendation endpoint returns recipe from service")
    void getAIRecommendedRecipe_ok() throws Exception {
        Recipe recipe = new Recipe();
        recipe.setRecipeId(12);
        recipe.setTitle("AI Recommendation");

        Mockito.when(aiRecipeService.getAIRecommendedRecipe())
                .thenReturn(recipe);

        mockMvc.perform(get("/api/ai/recipes/recommendation"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recipeId").value(12))
                .andExpect(jsonPath("$.title").value("AI Recommendation"));
    }

    @Test
    @DisplayName("GET recommendation endpoint returns 400 on service exception")
    void getAIRecommendedRecipe_error() throws Exception {
        Mockito.when(aiRecipeService.getAIRecommendedRecipe())
                .thenThrow(new IllegalStateException("Model timeout"));

        mockMvc.perform(get("/api/ai/recipes/recommendation"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(containsString("Model timeout")));
    }
}
