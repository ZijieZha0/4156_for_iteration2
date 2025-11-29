package com.example.nutriflow.service;

import com.example.nutriflow.recipe.model.RecipeIngredient;
import com.example.nutriflow.substitution.model.SubstitutionRule;
import com.example.nutriflow.user.model.User;
import com.example.nutriflow.substitution.dto.SubstitutionCheckResponse;
import com.example.nutriflow.recipe.repository.RecipeIngredientRepository;
import com.example.nutriflow.recipe.repository.RecipeRepository;
import com.example.nutriflow.substitution.repository.SubstitutionRuleRepository;
import com.example.nutriflow.substitution.service.SubstitutionService;
import com.example.nutriflow.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Partial unit test for SubstitutionService.
 * Covers core allergen detection and substitution rule lookup.
 */
class SubstitutionServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private RecipeIngredientRepository recipeIngredientRepository;

    @Mock
    private SubstitutionRuleRepository substitutionRuleRepository;

    @InjectMocks
    private SubstitutionService substitutionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Test case: recipe has allergen ingredient for the user.
     * Should detect allergen and provide substitution suggestion.
     */
    @Test
    void testCheckRecipeForUser_withAllergen() {
        User user = new User();
        user.setAllergies(new String[]{"lactose"});

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(recipeRepository.existsById(3)).thenReturn(true);

        RecipeIngredient ingredient = new RecipeIngredient();
        ingredient.setIngredient("milk");
        ingredient.setAllergenTags(new String[]{"lactose"});
        ingredient.setRecipeId(3);

        when(recipeIngredientRepository.findByRecipeId(3))
                .thenReturn(List.of(ingredient));

        SubstitutionRule rule = new SubstitutionRule();
        rule.setIngredient("milk");
        rule.setAvoid("lactose");
        rule.setSubstitute("almond milk");
        rule.setNote("Use 1:1");

        when(substitutionRuleRepository
                .findByIngredientIgnoreCaseAndAvoidIgnoreCase("milk", "lactose"))
                .thenReturn(List.of(rule));

        SubstitutionCheckResponse response =
                substitutionService.checkRecipeForUser(3, 1);

        assertTrue(response.isHasAllergens());
        assertEquals(1, response.getOffenders().size());
        assertEquals("milk", response.getOffenders().get(0).getIngredient());
        assertEquals("lactose", response.getOffenders().get(0).getAllergen());
        assertEquals("almond milk", response.getSuggestions().get(0).getAlt());
    }

    /**
     * Test case: user has no allergies or ingredients are safe.
     * Should return no offenders.
     */
    @Test
    void testCheckRecipeForUser_noAllergens() {
        User user = new User();
        user.setAllergies(new String[]{});

        when(userRepository.findById(2)).thenReturn(Optional.of(user));
        when(recipeRepository.existsById(5)).thenReturn(true);
        when(recipeIngredientRepository.findByRecipeId(5))
                .thenReturn(Collections.emptyList());

        SubstitutionCheckResponse response =
                substitutionService.checkRecipeForUser(5, 2);

        assertFalse(response.isHasAllergens());
        assertTrue(response.getOffenders().isEmpty());
        assertTrue(response.getSuggestions().isEmpty());
    }

    /**
     * Test case: ingredient substitution lookup (GET endpoint behavior).
     */
    @Test
    void testFindSubstitutions_basicLookup() {
        SubstitutionRule rule = new SubstitutionRule();
        rule.setIngredient("bread");
        rule.setSubstitute("gluten-free bread");
        rule.setNote("Same slices.");

        when(substitutionRuleRepository.findByIngredientIgnoreCase("bread"))
                .thenReturn(List.of(rule));

        var result = substitutionService.findSubstitutions("bread", Optional.empty());

        assertEquals(1, result.size());
        assertEquals("gluten-free bread", result.get(0).getAlt());
    }
}