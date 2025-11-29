package com.example.nutriflow.service;

import com.example.nutriflow.recipe.model.Recipe;
import com.example.nutriflow.recipe.model.RecipeIngredient;
import com.example.nutriflow.recipe.repository.RecipeIngredientRepository;
import com.example.nutriflow.recipe.repository.RecipeRepository;
import com.example.nutriflow.recipe.service.AIRecipeService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AIRecipeServiceTest {

    private RecipeRepository recipeRepository;
    private RecipeIngredientRepository recipeIngredientRepository;
    private AIRecipeService aiRecipeService;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() throws Exception {
        recipeRepository = mock(RecipeRepository.class);
        recipeIngredientRepository = mock(RecipeIngredientRepository.class);
        objectMapper = new ObjectMapper();
        aiRecipeService = new AIRecipeService("test-api-key", "test-model", objectMapper);

        injectDependency("recipeRepository", recipeRepository);
        injectDependency("recipeIngredientRepository", recipeIngredientRepository);
    }

    @Test
    @DisplayName("Get AI recipe returns an existing recipe when ingredient matches")
    void getAIRecipe_returnsExistingRecipeWhenIngredientMatches() {
        Recipe storedRecipe = new Recipe();
        storedRecipe.setRecipeId(8);
        storedRecipe.setTitle("Avocado Toast");

        RecipeIngredient ingredient = new RecipeIngredient();
        ingredient.setRecipeId(8);
        ingredient.setIngredient("avocado");

        when(recipeRepository.findAll()).thenReturn(List.of(storedRecipe));
        when(recipeIngredientRepository.findByRecipeId(8)).thenReturn(List.of(ingredient));

        Recipe result = aiRecipeService.getAIRecipe("avocado");

        assertSame(storedRecipe, result);
        verify(recipeRepository).findAll();
        verify(recipeIngredientRepository).findByRecipeId(8);
        verifyNoMoreInteractions(recipeRepository, recipeIngredientRepository);
    }

    @Test
    @DisplayName("Testing parse recipe method and populating a recipe object fields")
    void parseRecipe_populatesRecipeFieldsFromJson() throws Exception {
        String json = """
                {
                  "title": "AI Pasta",
                  "cookTime": 20,
                  "cuisines": ["Italian"],
                  "tags": ["quick", "vegetarian"],
                  "ingredients": [
                    {
                      "ingredient": "Tomato",
                      "quantity": 2.5,
                      "unit": "pcs",
                      "allergenTags": ["nightshade"]
                    }
                  ],
                  "nutrition": {
                    "summary": "Balanced"
                  },
                  "calories": 450.5,
                  "carbohydrates": 55,
                  "fat": 12,
                  "fiber": 8,
                  "protein": 18,
                  "popularityScore": 99
                }
                """;

        Recipe result = invokeParseRecipe(json);

        assertEquals("AI Pasta", result.getTitle());
        assertEquals(20, result.getCookTime());
        assertArrayEquals(new String[]{"Italian"}, result.getCuisines());
        assertArrayEquals(new String[]{"quick", "vegetarian"}, result.getTags());

        JsonNode ingredientsNode = objectMapper.readTree(result.getIngredients());
        assertEquals(1, ingredientsNode.size());
        assertEquals("Tomato", ingredientsNode.get(0).get("ingredient").asText());
        assertEquals(2.5, ingredientsNode.get(0).get("quantity").asDouble(), 0.0001);
        assertEquals("pcs", ingredientsNode.get(0).get("unit").asText());
        assertEquals("nightshade", ingredientsNode.get(0).get("allergenTags").get(0).asText());

        JsonNode nutritionNode = objectMapper.readTree(result.getNutrition());
        assertEquals("Balanced", nutritionNode.get("summary").asText());

        assertBigDecimalEquals("450.5", result.getCalories());
        assertBigDecimalEquals("55", result.getCarbohydrates());
        assertBigDecimalEquals("12", result.getFat());
        assertBigDecimalEquals("8", result.getFiber());
        assertBigDecimalEquals("18", result.getProtein());
        assertEquals(99, result.getPopularityScore());
    }

    @Test
    @DisplayName("parseRecipe throws IllegalStateException when JSON is invalid")
    void parseRecipe_throwsIllegalStateExceptionWhenJsonInvalid() {
        InvocationTargetException exception = assertThrows(
                InvocationTargetException.class,
                () -> invokeParseRecipe("not-json")
        );

        Throwable cause = exception.getCause();
        assertInstanceOf(IllegalStateException.class, cause);
        assertEquals("Failed to parse recipe response", cause.getMessage());
    }

    private void injectDependency(final String fieldName, final Object value) throws Exception {
        Field field = AIRecipeService.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(aiRecipeService, value);
    }

    private Recipe invokeParseRecipe(final String json) throws Exception {
        Method parseRecipe = AIRecipeService.class.getDeclaredMethod("parseRecipe", String.class);
        parseRecipe.setAccessible(true);
        return (Recipe) parseRecipe.invoke(aiRecipeService, json);
    }

    private void assertBigDecimalEquals(final String expected, final BigDecimal actual) {
        assertNotNull(actual);
        assertEquals(0, new BigDecimal(expected).compareTo(actual));
    }
}
