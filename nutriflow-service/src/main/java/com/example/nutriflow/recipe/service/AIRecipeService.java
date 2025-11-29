package com.example.nutriflow.recipe.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.example.nutriflow.recipe.model.Recipe;
import com.example.nutriflow.recipe.model.RecipeIngredient;
import com.example.nutriflow.recipe.repository.RecipeIngredientRepository;
import com.example.nutriflow.recipe.repository.RecipeRepository;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;
import com.google.genai.types.Schema;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.core.JsonProcessingException;


/**
 * Service class for handling AI logic related to recipes.
 * Provides methods for fetching recipes with desired ingredient
 * and also recommends a recipe.
 */
@Service
public class AIRecipeService {
    /** Client object that makes a connection to LLM. */
    private final Client client;
    /** Model type used for LLM. */
    private final String model;
    /** An ObjectMapper object that parses a json object. */
    private final ObjectMapper objectMapper;
    /** Repository for accessing recipe ingredient data. */
    @Autowired
    private RecipeIngredientRepository recipeIngredientRepository;
    /** Service handling recipe-related logic. */
    @Autowired
    private RecipeRepository recipeRepository;
    /**
     * Initializes an AIRecipeService object.
     *
     * @param apiKey apikey used for the LLM authentication
     * @param modelName the model type (for ex., gemini-flash)
     * @param myObjectMapper an objectmapper object
     */
    public AIRecipeService(
        final @Value("${GOOGLE_API_KEY}") String apiKey,
        final @Value("${GOOGLE_MODEL_NAME}") String modelName,
        final ObjectMapper myObjectMapper) {
        this.client = Client.builder().apiKey(apiKey).build();
        this.model = modelName;
        this.objectMapper = myObjectMapper;
    }

    /**
     * Generates a recipe with the given ingredient.
     *
     * @param ingredient the ingredient
     * @return Returns a recipe object with the found or generated recipe.
     */
    public Recipe getAIRecipe(final String ingredient) {
        final Optional<Recipe> existingRecipe = searchIngredient(ingredient);
        if (existingRecipe.isPresent()) {
            return existingRecipe.get();
        }

        final String finalPrompt =
            "Generate a delicious recipe with the following ingredient: "
                + ingredient;
        return requestRecipe(finalPrompt);
    }

    private Optional<Recipe> searchIngredient(final String ingredient) {
        List<Recipe> allRecipes = recipeRepository.findAll();
        for (Recipe recipe : allRecipes) {
            Integer recipeId = recipe.getRecipeId();
            final List<RecipeIngredient> recipeIngredients =
                recipeIngredientRepository.findByRecipeId(recipeId);
            for (RecipeIngredient ri : recipeIngredients) {
                final String candidate = ri.getIngredient();
                if (candidate != null
                    && ingredient.equalsIgnoreCase(candidate)) {
                    return Optional.of(recipe);
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Generates a recipe recommendation (AI generated).
     *
     * @return Returns a recipe object with the generated recipe.
     */
    public Recipe getAIRecommendedRecipe() {
        String finalPrompt = "Generate a delicious recipe";
        return requestRecipe(finalPrompt);
}

    /**
     * A method that creates a structured output schema
     * and uses the prompt to make an LLM query.
     *
     * @param prompt prompt that is used to make an LLM query.
     * @return Returns a recipe object with the generated recipe.
     */
    private Recipe requestRecipe(final String prompt) {
        Schema responseSchema = Schema.builder().type("OBJECT")
            .properties(Map.ofEntries(
                Map.entry("title",
                    Schema.builder().type("STRING").build()),
                Map.entry("cookTime",
                    Schema.builder().type("INTEGER").build()),
                Map.entry("cuisines",
                    Schema.builder().type("ARRAY")
                        .items(Schema.builder().type("STRING").build())
                        .build()),
                Map.entry("tags",
                    Schema.builder().type("ARRAY")
                        .items(Schema.builder().type("STRING").build())
                        .build()),
                Map.entry("ingredients",
                    Schema.builder().type("ARRAY").items(
                        Schema.builder().type("OBJECT")
                            .properties(Map.ofEntries(
                                Map.entry("id",
                                    Schema.builder().type("NULL").build()),
                                Map.entry("recipeId",
                                    Schema.builder().type("NULL").build()),
                                Map.entry("ingredient",
                                    Schema.builder().type("STRING").build()),
                                Map.entry("quantity",
                                    Schema.builder().type("NUMBER").build()),
                                Map.entry("unit",
                                    Schema.builder().type("STRING").build()),
                                Map.entry("allergenTags",
                                    Schema.builder().type("ARRAY")
                                        .items(Schema.builder()
                                            .type("STRING").build())
                                        .build())
                                )).build())
                        .build()),
                Map.entry("nutrition",
                    Schema.builder().type("OBJECT").properties(
                        Map.ofEntries(
                            Map.entry("summary",
                                Schema.builder().type("NULL").build())
                        )
                        )
                    .build()),
                Map.entry("calories",
                    Schema.builder().type("NUMBER").build()),
                Map.entry("carbohydrates",
                    Schema.builder().type("NUMBER").build()),
                Map.entry("fat",
                    Schema.builder().type("NUMBER").build()),
                Map.entry("fiber",
                    Schema.builder().type("NUMBER").build()),
                Map.entry("protein",
                    Schema.builder().type("NUMBER").build())
            ))
            .required(List.of("title", "ingredients"))
            .build();

        GenerateContentConfig config = GenerateContentConfig.builder()
                .responseMimeType("application/json")
                .responseSchema(responseSchema)
                .build();

        GenerateContentResponse response =
                client.models.generateContent(model, prompt, config);

        return parseRecipe(response.text());
    }

    /**
     * This method is used to parse a json object
     * and create a recipe object.
     * @param json a json object that will be parsed.
     * @return Returns a recipe object.
     */
    private Recipe parseRecipe(final String json) {
        try {
            JsonNode node = objectMapper.readTree(json);
            Recipe recipe = new Recipe();

            recipe.setTitle(node.path("title").asText(null));
            if (node.hasNonNull("cookTime")) {
                recipe.setCookTime(node.get("cookTime").asInt());
            }
            recipe.setCuisines(readStringArray(node.get("cuisines")));
            recipe.setTags(readStringArray(node.get("tags")));
            recipe.setIngredients(writeJsonOrEmpty(node.get("ingredients")));
            if (node.hasNonNull("nutrition")) {
                recipe.setNutrition(writeJsonOrEmpty(node.get("nutrition")));
            }
            recipe.setCalories(readDecimal(node, "calories"));
            recipe.setCarbohydrates(readDecimal(node, "carbohydrates"));
            recipe.setFat(readDecimal(node, "fat"));
            recipe.setFiber(readDecimal(node, "fiber"));
            recipe.setProtein(readDecimal(node, "protein"));
            if (node.hasNonNull("popularityScore")) {
                recipe.setPopularityScore(node.get("popularityScore").asInt());
            }
            return recipe;
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException(
                "Failed to parse recipe response", ex
                );
        }
    }

    private String[] readStringArray(final JsonNode node) {
        if (node == null || !node.isArray()) {
            return null;
        }
        List<String> values = new ArrayList<>();
        node.forEach(item -> values.add(item.asText()));
        return values.toArray(String[]::new);
    }

    private String writeJsonOrEmpty(final JsonNode node)
        throws JsonProcessingException {
            return (node == null || node.isNull())
                    ? "{}"
                    : objectMapper.writeValueAsString(node);
    }

    private BigDecimal readDecimal(final JsonNode node, final String field) {
        JsonNode valueNode = node.get(field);
        return valueNode != null && valueNode.isNumber()
                ? valueNode.decimalValue()
                : null;
    }

}
