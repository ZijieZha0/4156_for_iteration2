package com.example.nutriflow.service;

import com.example.nutriflow.recipe.model.Recipe;
import com.example.nutriflow.user.model.User;
import com.example.nutriflow.user.model.UserTarget;
import com.example.nutriflow.mealplan.model.DailyMealPlan;
import com.example.nutriflow.mealplan.model.Meal;
import com.example.nutriflow.mealplan.dto.MealPlanAlternativeRequestDto;
import com.example.nutriflow.mealplan.dto.MealPlanRequestDto;
import com.example.nutriflow.mealplan.dto.MealPlanResponseDto;
import com.example.nutriflow.mealplan.model.WeeklyMealPlan;
import com.example.nutriflow.mealplan.service.MealPlanService;
import com.example.nutriflow.mealplan.repository.DailyMealPlanRepository;
import com.example.nutriflow.mealplan.repository.MealRepository;
import com.example.nutriflow.mealplan.repository.WeeklyMealPlanRepository;
import com.example.nutriflow.recipe.repository.RecipeRepository;
import com.example.nutriflow.user.repository.UserRepository;
import com.example.nutriflow.user.repository.UserTargetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link MealPlanService} using Mockito.
 * Tests meal plan generation, alternative meal requests, and nutritional calculations.
 */
@ExtendWith(MockitoExtension.class)
class MealPlanServiceTest {

    @Mock
    private DailyMealPlanRepository dailyMealPlanRepository;

    @Mock
    private WeeklyMealPlanRepository weeklyMealPlanRepository;

    @Mock
    private MealRepository mealRepository;

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserTargetRepository userTargetRepository;

    @InjectMocks
    private MealPlanService mealPlanService;

    private User testUser;
    private UserTarget testTarget;
    private List<Recipe> testRecipes;

    @BeforeEach
    void setUp() {
        // Create test user
        testUser = new User();
        testUser.setUserId(1);
        testUser.setName("Test User");
        testUser.setAllergies(new String[]{"peanuts"});
        testUser.setDislikes(new String[]{"mushrooms"});

        // Create test target
        testTarget = new UserTarget();
        testTarget.setTargetId(1);
        testTarget.setUserId(1);
        testTarget.setCalories(new BigDecimal("2000.00"));
        testTarget.setProtein(new BigDecimal("150.00"));
        testTarget.setCarbs(new BigDecimal("250.00"));
        testTarget.setFat(new BigDecimal("65.00"));

        // Create test recipes
        testRecipes = new ArrayList<>();
        
        Recipe r1 = new Recipe();
        r1.setRecipeId(1);
        r1.setTitle("Breakfast Recipe");
        r1.setCookTime(15);
        r1.setCalories(new BigDecimal("400"));
        r1.setProtein(new BigDecimal("30"));
        r1.setCarbohydrates(new BigDecimal("50"));
        r1.setFat(new BigDecimal("15"));
        r1.setFiber(new BigDecimal("5"));
        r1.setTags(new String[]{"breakfast", "quick"});
        r1.setCuisines(new String[]{"American"});
        testRecipes.add(r1);

        Recipe r2 = new Recipe();
        r2.setRecipeId(2);
        r2.setTitle("Lunch Recipe");
        r2.setCookTime(30);
        r2.setCalories(new BigDecimal("600"));
        r2.setProtein(new BigDecimal("50"));
        r2.setCarbohydrates(new BigDecimal("70"));
        r2.setFat(new BigDecimal("20"));
        r2.setFiber(new BigDecimal("10"));
        r2.setTags(new String[]{"lunch", "healthy"});
        r2.setCuisines(new String[]{"Italian"});
        testRecipes.add(r2);

        Recipe r3 = new Recipe();
        r3.setRecipeId(3);
        r3.setTitle("Dinner Recipe");
        r3.setCookTime(45);
        r3.setCalories(new BigDecimal("700"));
        r3.setProtein(new BigDecimal("60"));
        r3.setCarbohydrates(new BigDecimal("80"));
        r3.setFat(new BigDecimal("25"));
        r3.setFiber(new BigDecimal("12"));
        r3.setTags(new String[]{"dinner", "high-protein"});
        r3.setCuisines(new String[]{"Mexican"});
        testRecipes.add(r3);
    }

    @Test
    @DisplayName("Generate daily meal plan successfully")
    void generateDailyMealPlan_success() {
        // Given
        MealPlanRequestDto request = new MealPlanRequestDto();
        request.setUserId(1);
        request.setMealsPerDay(3);
        request.setNumberOfDays(1);
        request.setStartDate(LocalDate.now());
        request.setClientId("test-client");

        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(userTargetRepository.findLatestByUserId(1)).thenReturn(Optional.of(testTarget));
        when(recipeRepository.findAll()).thenReturn(testRecipes);
        
        // Mock meal saves
        Meal meal1 = new Meal(1, "breakfast", 1);
        meal1.setMealId(1);
        Meal meal2 = new Meal(2, "lunch", 1);
        meal2.setMealId(2);
        Meal meal3 = new Meal(3, "dinner", 1);
        meal3.setMealId(3);
        
        when(mealRepository.save(any(Meal.class)))
            .thenReturn(meal1, meal2, meal3);
        
        // Mock daily plan save
        DailyMealPlan savedPlan = new DailyMealPlan();
        savedPlan.setPlanId(1);
        savedPlan.setUserId(1);
        savedPlan.setPlanDate(LocalDate.now());
        when(dailyMealPlanRepository.save(any(DailyMealPlan.class)))
            .thenReturn(savedPlan);

        // When
        MealPlanResponseDto response = mealPlanService.generateMealPlan(request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getSuccess()).isTrue();
        assertThat(response.getDailyPlans()).hasSize(1);
        assertThat(response.getDailyPlans().get(0).getMeals()).hasSize(3);
        assertThat(response.getTotalRecipesUsed()).isEqualTo(3);
        
        // Verify interactions
        verify(userRepository).findById(1);
        verify(userTargetRepository).findLatestByUserId(1);
        verify(recipeRepository).findAll();
        verify(mealRepository, times(3)).save(any(Meal.class));
        verify(dailyMealPlanRepository).save(any(DailyMealPlan.class));
    }

    @Test
    @DisplayName("Generate weekly meal plan successfully")
    void generateWeeklyMealPlan_success() {
        // Given
        MealPlanRequestDto request = new MealPlanRequestDto();
        request.setUserId(1);
        request.setMealsPerDay(3);
        request.setNumberOfDays(7);
        request.setStartDate(LocalDate.now());
        request.setClientId("test-client");

        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(userTargetRepository.findLatestByUserId(1)).thenReturn(Optional.of(testTarget));
        when(recipeRepository.findAll()).thenReturn(testRecipes);
        
        // Mock meal and plan saves
        when(mealRepository.save(any(Meal.class)))
            .thenAnswer(invocation -> {
                Meal meal = invocation.getArgument(0);
                meal.setMealId((int) (Math.random() * 1000));
                return meal;
            });
        
        when(dailyMealPlanRepository.save(any(DailyMealPlan.class)))
            .thenAnswer(invocation -> {
                DailyMealPlan plan = invocation.getArgument(0);
                plan.setPlanId((int) (Math.random() * 1000));
                return plan;
            });

        WeeklyMealPlan savedWeeklyPlan = new WeeklyMealPlan();
        savedWeeklyPlan.setWeeklyPlanId(1);
        when(weeklyMealPlanRepository.save(any(WeeklyMealPlan.class)))
            .thenReturn(savedWeeklyPlan);

        // When
        MealPlanResponseDto response = mealPlanService.generateMealPlan(request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getSuccess()).isTrue();
        assertThat(response.getDailyPlans()).hasSize(7);
        assertThat(response.getWeeklyPlan()).isNotNull();
        assertThat(response.getWeeklyPlan().getWeeklyPlanId()).isEqualTo(1);
        
        // Verify weekly plan was created
        verify(weeklyMealPlanRepository).save(any(WeeklyMealPlan.class));
    }

    @Test
    @DisplayName("Generate meal plan with custom targets")
    void generateMealPlan_withCustomTargets() {
        // Given
        MealPlanRequestDto request = new MealPlanRequestDto();
        request.setUserId(1);
        request.setMealsPerDay(3);
        request.setNumberOfDays(1);
        request.setStartDate(LocalDate.now());
        request.setTargetCalories(2500.0);
        request.setTargetProtein(180.0);
        request.setClientId("test-client");

        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(userTargetRepository.findLatestByUserId(1)).thenReturn(Optional.of(testTarget));
        when(recipeRepository.findAll()).thenReturn(testRecipes);
        when(mealRepository.save(any(Meal.class)))
            .thenAnswer(invocation -> {
                Meal meal = invocation.getArgument(0);
                meal.setMealId((int) (Math.random() * 1000));
                return meal;
            });
        when(dailyMealPlanRepository.save(any(DailyMealPlan.class)))
            .thenAnswer(invocation -> {
                DailyMealPlan plan = invocation.getArgument(0);
                plan.setPlanId(1);
                return plan;
            });

        // When
        MealPlanResponseDto response = mealPlanService.generateMealPlan(request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getSuccess()).isTrue();
        
        // Custom targets should be used (variance should be calculated from 2500 and 180)
        assertThat(response.getCalorieVariance()).isNotNull();
        assertThat(response.getProteinVariance()).isNotNull();
    }

    @Test
    @DisplayName("Generate meal plan with max prep time filter")
    void generateMealPlan_withMaxPrepTime() {
        // Given
        MealPlanRequestDto request = new MealPlanRequestDto();
        request.setUserId(1);
        request.setMealsPerDay(2);
        request.setNumberOfDays(1);
        request.setMaxPrepTime(20); // Only breakfast recipe should match
        request.setStartDate(LocalDate.now());

        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(userTargetRepository.findLatestByUserId(1)).thenReturn(Optional.of(testTarget));
        when(recipeRepository.findAll()).thenReturn(testRecipes);
        when(mealRepository.save(any(Meal.class)))
            .thenAnswer(invocation -> {
                Meal meal = invocation.getArgument(0);
                meal.setMealId((int) (Math.random() * 1000));
                return meal;
            });
        when(dailyMealPlanRepository.save(any(DailyMealPlan.class)))
            .thenAnswer(invocation -> {
                DailyMealPlan plan = invocation.getArgument(0);
                plan.setPlanId(1);
                return plan;
            });

        // When
        MealPlanResponseDto response = mealPlanService.generateMealPlan(request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getSuccess()).isTrue();
        
        // Should have saved a plan with max_prep_time set
        ArgumentCaptor<DailyMealPlan> planCaptor = ArgumentCaptor.forClass(DailyMealPlan.class);
        verify(dailyMealPlanRepository).save(planCaptor.capture());
        assertThat(planCaptor.getValue().getMaxPrepTime()).isEqualTo(20);
    }

    @Test
    @DisplayName("Generate meal plan with cuisine filter")
    void generateMealPlan_withCuisineFilter() {
        // Given
        MealPlanRequestDto request = new MealPlanRequestDto();
        request.setUserId(1);
        request.setMealsPerDay(2);
        request.setNumberOfDays(1);
        request.setPreferredCuisines(Arrays.asList("Italian", "Mexican"));
        request.setStartDate(LocalDate.now());

        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(userTargetRepository.findLatestByUserId(1)).thenReturn(Optional.of(testTarget));
        when(recipeRepository.findAll()).thenReturn(testRecipes);
        when(mealRepository.save(any(Meal.class)))
            .thenAnswer(invocation -> {
                Meal meal = invocation.getArgument(0);
                meal.setMealId((int) (Math.random() * 1000));
                return meal;
            });
        when(dailyMealPlanRepository.save(any(DailyMealPlan.class)))
            .thenAnswer(invocation -> {
                DailyMealPlan plan = invocation.getArgument(0);
                plan.setPlanId(1);
                return plan;
            });

        // When
        MealPlanResponseDto response = mealPlanService.generateMealPlan(request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getSuccess()).isTrue();
        // Should only include Italian and Mexican recipes
    }

    @Test
    @DisplayName("Generate meal plan with tag filter")
    void generateMealPlan_withTagFilter() {
        // Given
        MealPlanRequestDto request = new MealPlanRequestDto();
        request.setUserId(1);
        request.setMealsPerDay(2);
        request.setNumberOfDays(1);
        request.setTags(Arrays.asList("quick", "healthy"));
        request.setStartDate(LocalDate.now());

        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(userTargetRepository.findLatestByUserId(1)).thenReturn(Optional.of(testTarget));
        when(recipeRepository.findAll()).thenReturn(testRecipes);
        when(mealRepository.save(any(Meal.class)))
            .thenAnswer(invocation -> {
                Meal meal = invocation.getArgument(0);
                meal.setMealId((int) (Math.random() * 1000));
                return meal;
            });
        when(dailyMealPlanRepository.save(any(DailyMealPlan.class)))
            .thenAnswer(invocation -> {
                DailyMealPlan plan = invocation.getArgument(0);
                plan.setPlanId(1);
                return plan;
            });

        // When
        MealPlanResponseDto response = mealPlanService.generateMealPlan(request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getSuccess()).isTrue();
    }

    @Test
    @DisplayName("Generate meal plan fails when user not found")
    void generateMealPlan_userNotFound() {
        // Given
        MealPlanRequestDto request = new MealPlanRequestDto();
        request.setUserId(999);
        request.setMealsPerDay(3);
        request.setNumberOfDays(1);

        when(userRepository.findById(999)).thenReturn(Optional.empty());

        // When
        MealPlanResponseDto response = mealPlanService.generateMealPlan(request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getSuccess()).isFalse();
        assertThat(response.getMessage()).isEqualTo("User not found");
        
        verify(userRepository).findById(999);
        verifyNoInteractions(recipeRepository);
        verifyNoInteractions(mealRepository);
    }

    @Test
    @DisplayName("Generate meal plan with null user ID fails")
    void generateMealPlan_nullUserId() {
        // Given
        MealPlanRequestDto request = new MealPlanRequestDto();
        request.setUserId(null);
        request.setMealsPerDay(3);
        request.setNumberOfDays(1);

        // When
        MealPlanResponseDto response = mealPlanService.generateMealPlan(request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getSuccess()).isFalse();
        assertThat(response.getMessage()).isEqualTo("User ID is required");
        
        verifyNoInteractions(userRepository);
    }

    @Test
    @DisplayName("Request alternative meal successfully")
    void requestAlternativeMeal_success() {
        // Given
        MealPlanAlternativeRequestDto request = new MealPlanAlternativeRequestDto();
        request.setUserId(1);
        request.setPlanId(1);
        request.setMealIdToReplace(1);
        request.setDislikedRecipeId(1);

        DailyMealPlan existingPlan = new DailyMealPlan();
        existingPlan.setPlanId(1);
        existingPlan.setUserId(1);
        existingPlan.setMealIds(new Integer[]{1, 2, 3});

        Meal existingMeal = new Meal(1, "breakfast", 1);
        existingMeal.setMealId(1);

        Recipe originalRecipe = testRecipes.get(0);

        when(dailyMealPlanRepository.findById(1)).thenReturn(Optional.of(existingPlan));
        when(mealRepository.findById(1)).thenReturn(Optional.of(existingMeal));
        when(recipeRepository.findAll()).thenReturn(testRecipes);
        when(mealRepository.save(any(Meal.class))).thenReturn(existingMeal);
        
        // Mock recipe lookups for total calculation (handles all recipe ID lookups including the original)
        when(recipeRepository.findById(anyInt())).thenAnswer(invocation -> {
            Integer id = invocation.getArgument(0);
            return testRecipes.stream()
                .filter(r -> r.getRecipeId().equals(id))
                .findFirst();
        });
        
        when(dailyMealPlanRepository.save(any(DailyMealPlan.class)))
            .thenReturn(existingPlan);

        // When
        MealPlanResponseDto response = mealPlanService.requestAlternativeMeal(request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getSuccess()).isTrue();
        assertThat(response.getMessage()).contains("Alternative meal selected successfully");
        
        verify(mealRepository).save(any(Meal.class));
        verify(dailyMealPlanRepository).save(any(DailyMealPlan.class));
    }

    @Test
    @DisplayName("Request alternative meal fails when plan not found")
    void requestAlternativeMeal_planNotFound() {
        // Given
        MealPlanAlternativeRequestDto request = new MealPlanAlternativeRequestDto();
        request.setUserId(1);
        request.setPlanId(999);
        request.setMealIdToReplace(1);
        request.setDislikedRecipeId(1);

        when(dailyMealPlanRepository.findById(999)).thenReturn(Optional.empty());

        // When
        MealPlanResponseDto response = mealPlanService.requestAlternativeMeal(request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getSuccess()).isFalse();
        assertThat(response.getMessage()).isEqualTo("Meal plan not found");
        
        verify(dailyMealPlanRepository).findById(999);
        verifyNoInteractions(mealRepository);
    }

    @Test
    @DisplayName("Request alternative meal fails when meal not found")
    void requestAlternativeMeal_mealNotFound() {
        // Given
        MealPlanAlternativeRequestDto request = new MealPlanAlternativeRequestDto();
        request.setUserId(1);
        request.setPlanId(1);
        request.setMealIdToReplace(999);
        request.setDislikedRecipeId(1);

        DailyMealPlan existingPlan = new DailyMealPlan();
        existingPlan.setPlanId(1);

        when(dailyMealPlanRepository.findById(1)).thenReturn(Optional.of(existingPlan));
        when(mealRepository.findById(999)).thenReturn(Optional.empty());

        // When
        MealPlanResponseDto response = mealPlanService.requestAlternativeMeal(request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getSuccess()).isFalse();
        assertThat(response.getMessage()).isEqualTo("Meal not found");
    }

    @Test
    @DisplayName("Request alternative meal fails when original recipe not found")
    void requestAlternativeMeal_originalRecipeNotFound() {
        // Given
        MealPlanAlternativeRequestDto request = new MealPlanAlternativeRequestDto();
        request.setUserId(1);
        request.setPlanId(1);
        request.setMealIdToReplace(1);
        request.setDislikedRecipeId(1);

        DailyMealPlan existingPlan = new DailyMealPlan();
        existingPlan.setPlanId(1);

        Meal existingMeal = new Meal(999, "breakfast", 1);
        existingMeal.setMealId(1);

        when(dailyMealPlanRepository.findById(1)).thenReturn(Optional.of(existingPlan));
        when(mealRepository.findById(1)).thenReturn(Optional.of(existingMeal));
        when(recipeRepository.findById(999)).thenReturn(Optional.empty());

        // When
        MealPlanResponseDto response = mealPlanService.requestAlternativeMeal(request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getSuccess()).isFalse();
        assertThat(response.getMessage()).isEqualTo("Original recipe not found");
    }

    @Test
    @DisplayName("Request alternative meal fails when no suitable alternative found")
    void requestAlternativeMeal_noAlternativeFound() {
        // Given
        MealPlanAlternativeRequestDto request = new MealPlanAlternativeRequestDto();
        request.setUserId(1);
        request.setPlanId(1);
        request.setMealIdToReplace(1);
        request.setDislikedRecipeId(1);
        request.setExcludeRecipeIds(Arrays.asList(2, 3)); // Exclude all alternatives

        DailyMealPlan existingPlan = new DailyMealPlan();
        existingPlan.setPlanId(1);
        existingPlan.setMealIds(new Integer[]{1});

        Meal existingMeal = new Meal(1, "breakfast", 1);
        existingMeal.setMealId(1);

        Recipe originalRecipe = testRecipes.get(0);

        when(dailyMealPlanRepository.findById(1)).thenReturn(Optional.of(existingPlan));
        when(mealRepository.findById(1)).thenReturn(Optional.of(existingMeal));
        when(recipeRepository.findById(1)).thenReturn(Optional.of(originalRecipe));
        when(recipeRepository.findAll()).thenReturn(testRecipes);

        // When
        MealPlanResponseDto response = mealPlanService.requestAlternativeMeal(request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getSuccess()).isFalse();
        assertThat(response.getMessage()).isEqualTo("No suitable alternative found");
    }

    @Test
    @DisplayName("Generate meal plan uses default values when not specified")
    void generateMealPlan_usesDefaults() {
        // Given
        MealPlanRequestDto request = new MealPlanRequestDto();
        request.setUserId(1);
        // Not setting mealsPerDay, numberOfDays, startDate

        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(userTargetRepository.findLatestByUserId(1)).thenReturn(Optional.empty()); // No user target
        when(recipeRepository.findAll()).thenReturn(testRecipes);
        when(mealRepository.save(any(Meal.class)))
            .thenAnswer(invocation -> {
                Meal meal = invocation.getArgument(0);
                meal.setMealId((int) (Math.random() * 1000));
                return meal;
            });
        when(dailyMealPlanRepository.save(any(DailyMealPlan.class)))
            .thenAnswer(invocation -> {
                DailyMealPlan plan = invocation.getArgument(0);
                plan.setPlanId(1);
                return plan;
            });

        // When
        MealPlanResponseDto response = mealPlanService.generateMealPlan(request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getSuccess()).isTrue();
        assertThat(response.getDailyPlans()).hasSize(1); // Default 1 day
        // Should use default targets (2000 cal, 150g protein)
    }
}

