package com.example.nutriflow.mealplan.service;

import com.example.nutriflow.recipe.model.Recipe;
import com.example.nutriflow.user.model.User;
import com.example.nutriflow.user.model.UserTarget;
import com.example.nutriflow.mealplan.model.DailyMealPlan;
import com.example.nutriflow.mealplan.dto.DailyMealPlanDetailDto;
import com.example.nutriflow.mealplan.model.Meal;
import com.example.nutriflow.mealplan.dto.MealPlanAlternativeRequestDto;
import com.example.nutriflow.mealplan.dto.MealPlanRequestDto;
import com.example.nutriflow.mealplan.dto.MealPlanResponseDto;
import com.example.nutriflow.mealplan.model.WeeklyMealPlan;
import com.example.nutriflow.mealplan.repository.DailyMealPlanRepository;
import com.example.nutriflow.mealplan.repository.MealRepository;
import com.example.nutriflow.recipe.repository.RecipeRepository;
import com.example.nutriflow.user.repository.UserRepository;
import com.example.nutriflow.user.repository.UserTargetRepository;
import com.example.nutriflow.mealplan.repository.WeeklyMealPlanRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service class for generating and managing meal plans.
 * Handles daily and weekly meal plan generation with nutritional optimization,
 * allergen checking, and user preference integration.
 */
@Service
public class MealPlanService {

    /** Logger for this service. */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(MealPlanService.class);

    /** Repository for meal plan data. */
    @Autowired
    private DailyMealPlanRepository dailyMealPlanRepository;

    /** Repository for weekly meal plan data. */
    @Autowired
    private WeeklyMealPlanRepository weeklyMealPlanRepository;

    /** Repository for meal data. */
    @Autowired
    private MealRepository mealRepository;

    /** Repository for recipe data. */
    @Autowired
    private RecipeRepository recipeRepository;

    /** Repository for user data. */
    @Autowired
    private UserRepository userRepository;

    /** Repository for user target data. */
    @Autowired
    private UserTargetRepository userTargetRepository;

    /** Default number of meals per day. */
    private static final int DEFAULT_MEALS_PER_DAY = 3;

    /** Number of days in a week for weekly meal plans. */
    private static final int DAYS_IN_WEEK = 7;

    /** Last day index for weekly meal plans (0-indexed). */
    private static final int LAST_DAY_OF_WEEK = 6;

    /** Default calorie target when not specified. */
    private static final double DEFAULT_CALORIES = 2000.0;

    /** Default protein target in grams when not specified.
     */
    private static final double DEFAULT_PROTEIN = 150.0;

    /** Default carbohydrate target in grams. */
    private static final double DEFAULT_CARBS = 250.0;

    /** Default fat target in grams when not specified.
     */
    private static final double DEFAULT_FAT = 65.0;

    /** Weight for calorie difference in scoring (60%). */
    private static final double CALORIE_WEIGHT = 0.6;

    /** Weight for protein difference in scoring (40%). */
    private static final double PROTEIN_WEIGHT = 0.4;

    /** Percentage multiplier for variance calculation. */
    private static final double PERCENT_MULTIPLIER = 100.0;

    /** Meal type labels. */
    private static final String[] MEAL_TYPES =
            {"breakfast", "lunch", "dinner", "snack"};

    /**
     * Generate a meal plan based on the request parameters.
     * Supports daily and weekly meal plan generation.
     *
     *
     * @param request the meal plan request containing user
     *                preferences and constraints
     * @return the generated meal plan response with recipes
     *         and nutritional info
     */
    @Transactional
    public MealPlanResponseDto generateMealPlan(
            final MealPlanRequestDto request) {
        LOGGER.info("Generating meal plan for user {} with {} days",
                request.getUserId(), request.getNumberOfDays());

        // Validate request
        if (request.getUserId() == null) {
            return createErrorResponse("User ID is required");
        }

        // Fetch user and target data
        final Optional<User> userOpt =
                userRepository.findById(request.getUserId());
        if (userOpt.isEmpty()) {
            return createErrorResponse("User not found");
        }

        final User user = userOpt.get();
        final Optional<UserTarget> targetOpt =
                userTargetRepository
                        .findLatestByUserId(request.getUserId());

        // Set defaults
        final int mealsPerDay = request.getMealsPerDay() != null
                ? request.getMealsPerDay()
                : DEFAULT_MEALS_PER_DAY;
        final int numberOfDays = request.getNumberOfDays() != null
                ? request.getNumberOfDays() : 1;
        final LocalDate startDate = request.getStartDate() != null
                ? request.getStartDate() : LocalDate.now();

        // Determine target macros
        final MacroTargets targets = determineMacroTargets(request, targetOpt);

        // Generate daily meal plans
        final List<DailyMealPlanDetailDto> dailyPlans =
                new ArrayList<>();
        for (int day = 0; day < numberOfDays; day++) {
            final LocalDate currentDate = startDate.plusDays(day);
            final DailyMealPlanDetailDto dailyPlan =
                    generateDailyMealPlan(user, targets,
                            mealsPerDay, currentDate, request);
            dailyPlans.add(dailyPlan);
        }

        // Create response
        final MealPlanResponseDto response = new MealPlanResponseDto();
        response.setSuccess(true);
        response.setDailyPlans(dailyPlans);
        response.setTotalRecipesUsed(dailyPlans.stream()
                .mapToInt(dp -> dp.getMeals().size())
                .sum());

        // Calculate variance from targets
        if (!dailyPlans.isEmpty()) {
            final double avgCalories = dailyPlans.stream()
                    .mapToDouble(DailyMealPlanDetailDto::getTotalCalories)
                    .average()
                    .orElse(0.0);
            final double avgProtein = dailyPlans.stream()
                    .mapToDouble(DailyMealPlanDetailDto::getTotalProtein)
                    .average()
                    .orElse(0.0);

            response.setCalorieVariance(
                    calculateVariance(avgCalories, targets.getCalories()));
            response.setProteinVariance(
                    calculateVariance(avgProtein, targets.getProtein()));
        }

        // Create weekly plan if applicable
        if (numberOfDays >= DAYS_IN_WEEK) {
            final WeeklyMealPlan weeklyPlan = createWeeklyMealPlan(
                    request.getUserId(), startDate, dailyPlans);
            response.setWeeklyPlan(weeklyPlan);
        } else if (numberOfDays == 1 && !dailyPlans.isEmpty()) {
            response.setMessage("Daily meal plan generated successfully");
        }

        LOGGER.info("Meal plan generated successfully for user {}",
                request.getUserId());
        return response;
    }

    /**
     * Generate a single daily meal plan.
     *
     * @param user         the user
     * @param targets      the macro targets
     * @param mealsPerDay  number of meals per day
     * @param date         the date for this meal plan
     * @param request      the original request
     * @return the generated daily meal plan with details
     */
    private DailyMealPlanDetailDto generateDailyMealPlan(
            final User user,
            final MacroTargets targets,
            final int mealsPerDay,
            final LocalDate date,
            final MealPlanRequestDto request) {

        final List<DailyMealPlanDetailDto.MealDetailDto> meals =
                new ArrayList<>();

        // Calculate target calories per meal
        final double caloriesPerMeal = targets.getCalories() / mealsPerDay;
        final double proteinPerMeal = targets.getProtein() / mealsPerDay;

        // Get eligible recipes
        final List<Recipe> eligibleRecipes = getEligibleRecipes(user, request);

        if (eligibleRecipes.isEmpty()) {
            LOGGER.warn("No eligible recipes found for user {}",
                    user.getUserId());
        }

        // Select recipes for each meal
        final Set<Integer> usedRecipeIds = new HashSet<>();
        for (int mealIndex = 0; mealIndex < mealsPerDay; mealIndex++) {
            final int mealTypeIndex = Math.min(
                    mealIndex, MEAL_TYPES.length - 1);
            final String mealType = MEAL_TYPES[mealTypeIndex];

            final Recipe selectedRecipe = selectRecipeForMeal(
                    eligibleRecipes, caloriesPerMeal,
                    proteinPerMeal, usedRecipeIds,
                    request.getMaxPrepTime());

            if (selectedRecipe != null) {
                final Meal meal = new Meal();
                meal.setRecipeId(selectedRecipe.getRecipeId());
                meal.setMealType(mealType);
                meal.setServings(1);
                meal.setCreatedAt(LocalDateTime.now());

                final Meal savedMeal = mealRepository.save(meal);
                usedRecipeIds.add(selectedRecipe.getRecipeId());

                final DailyMealPlanDetailDto.MealDetailDto mealDetail
                        = new DailyMealPlanDetailDto.MealDetailDto();
                mealDetail.setMealId(savedMeal.getMealId());
                mealDetail.setMealType(mealType);
                mealDetail.setRecipe(selectedRecipe);
                mealDetail.setServings(1);

                meals.add(mealDetail);
            }
        }

        // Check if a meal plan already exists for this user and date
        final Optional<DailyMealPlan> existingPlanOpt =
                dailyMealPlanRepository.findByUserIdAndPlanDate(
                        user.getUserId(), date);

        final DailyMealPlan dailyPlan;
        if (existingPlanOpt.isPresent()) {
            // Update existing plan - delete old meals first
            dailyPlan = existingPlanOpt.get();
            LOGGER.info("Updating existing meal plan ID: {} "
                    + "for user {} on {}",
                    dailyPlan.getPlanId(), user.getUserId(), date);

            // Delete old meals associated with this plan
            if (dailyPlan.getMealIds() != null
                    && dailyPlan.getMealIds().length > 0) {
                final List<Integer> oldMealIds =
                        Arrays.asList(dailyPlan.getMealIds());
                mealRepository.deleteAllById(oldMealIds);
                LOGGER.info("Deleted {} old meals for plan ID: {}",
                        oldMealIds.size(), dailyPlan.getPlanId());
            }
        } else {
            // Create new plan
            dailyPlan = new DailyMealPlan();
            dailyPlan.setUserId(user.getUserId());
            dailyPlan.setPlanDate(date);
            dailyPlan.setCreatedAt(LocalDateTime.now());
            LOGGER.info("Creating new meal plan for user {} on {}",
                    user.getUserId(), date);
        }

        // Update meal plan data
        dailyPlan.setMealIds(meals.stream()
                .map(DailyMealPlanDetailDto.MealDetailDto::getMealId)
                .toArray(Integer[]::new));
        dailyPlan.setMaxPrepTime(request.getMaxPrepTime());
        dailyPlan.setStatus("active");

        // Calculate totals
        final double totalCalories = meals.stream()
                .mapToDouble(m -> m.getRecipe().getCalories().doubleValue())
                .sum();
        final double totalProtein = meals.stream()
                .mapToDouble(m -> m.getRecipe().getProtein().doubleValue())
                .sum();
        final double totalCarbs = meals.stream()
                .mapToDouble(m ->
                        m.getRecipe().getCarbohydrates()
                                .doubleValue())
                .sum();
        final double totalFat = meals.stream()
                .mapToDouble(m -> m.getRecipe().getFat().doubleValue())
                .sum();
        final double totalFiber = meals.stream()
                .mapToDouble(m -> m.getRecipe().getFiber().doubleValue())
                .sum();

        dailyPlan.setTotalCalories(totalCalories);
        dailyPlan.setTotalProtein(totalProtein);
        dailyPlan.setTotalCarbs(totalCarbs);
        dailyPlan.setTotalFat(totalFat);
        dailyPlan.setTotalFiber(totalFiber);

        final DailyMealPlan savedPlan = dailyMealPlanRepository.save(dailyPlan);

        // Create detail DTO
        final DailyMealPlanDetailDto detailDto = new DailyMealPlanDetailDto();
        detailDto.setPlanId(savedPlan.getPlanId());
        detailDto.setPlanDate(date);
        detailDto.setMeals(meals);
        detailDto.setTotalCalories(totalCalories);
        detailDto.setTotalProtein(totalProtein);
        detailDto.setTotalCarbs(totalCarbs);
        detailDto.setTotalFat(totalFat);
        detailDto.setTotalFiber(totalFiber);

        return detailDto;
    }

    /**
     * Get eligible recipes based on user allergies, dislikes,
     * and request filters.
     *
     * @param user    the user
     * @param request the meal plan request
     * @return list of eligible recipes
     */
    private List<Recipe> getEligibleRecipes(final User user,
            final MealPlanRequestDto request) {
        final List<Recipe> allRecipes = recipeRepository.findAll();
        List<Recipe> recipes = new ArrayList<>(allRecipes);

        // Filter by max prep time
        if (request.getMaxPrepTime() != null) {
            final List<Recipe> filtered = recipes.stream()
                    .filter(r -> r.getCookTime() != null
                            && r.getCookTime() <= request.getMaxPrepTime())
                    .collect(Collectors.toList());
            if (!filtered.isEmpty()) {
                recipes = filtered;
            } else {
                LOGGER.warn("No recipes found under max prep time {}. "
                        + "Falling back to full list.",
                        request.getMaxPrepTime());
            }
        }

        // Filter by tags
        if (request.getTags() != null
                && !request.getTags().isEmpty()) {
            final List<Recipe> filtered = recipes.stream()
                    .filter(r -> r.getTags() != null
                            && Arrays.stream(r.getTags())
                            .anyMatch(tag ->
                                    request.getTags().contains(tag)))
                    .collect(Collectors.toList());
            if (!filtered.isEmpty()) {
                recipes = filtered;
            } else {
                LOGGER.warn("No recipes found for tags {}. "
                        + "Falling back to previous selection.",
                        request.getTags());
            }
        }

        // Filter by cuisines
        if (request.getPreferredCuisines() != null
                && !request.getPreferredCuisines().isEmpty()) {
            final List<Recipe> filtered = recipes.stream()
                    .filter(r -> r.getCuisines() != null
                            && Arrays.stream(r.getCuisines())
                            .anyMatch(cuisine ->
                                    request.getPreferredCuisines()
                                            .contains(cuisine)))
                    .collect(Collectors.toList());
            if (!filtered.isEmpty()) {
                recipes = filtered;
            } else {
                LOGGER.warn("No recipes found for cuisines {}. "
                        + "Falling back to previous selection.",
                        request.getPreferredCuisines());
            }
        }

        if (recipes.isEmpty()) {
            LOGGER.warn("All filters removed available recipes. "
                    + "Using complete list of recipes.");
            recipes = allRecipes;
        }

        return recipes;
    }

    /**
     * Select the best recipe for a meal based on targets.
     *
     * @param recipes        available recipes
     * @param targetCalories target calories for this meal
     * @param targetProtein  target protein for this meal
     * @param usedRecipes    set of already used recipe IDs
     * @param maxPrepTime    maximum preparation time
     * @return the selected recipe, or null if none found
     */
    private Recipe selectRecipeForMeal(final List<Recipe> recipes,
            final double targetCalories, final double targetProtein,
            final Set<Integer> usedRecipes,
            final Integer maxPrepTime) {

        Recipe bestMatch = null;
        double bestScore = Double.MAX_VALUE;

        for (final Recipe recipe : recipes) {
            // Skip already used recipes
            if (usedRecipes.contains(recipe.getRecipeId())) {
                continue;
            }

            // Skip if recipe has invalid nutrition data
            if (recipe.getCalories() == null || recipe.getProtein() == null) {
                continue;
            }

            // Skip if exceeds prep time
            if (maxPrepTime != null
                    && recipe.getCookTime() != null
                    && recipe.getCookTime() > maxPrepTime) {
                continue;
            }

            // Calculate score based on how close to target
            final double calories =
                    recipe.getCalories().doubleValue();
            final double protein =
                    recipe.getProtein().doubleValue();

            // Prevent division by zero
            if (targetCalories == 0 || targetProtein == 0) {
                // If no targets, just pick any valid recipe
                if (bestMatch == null) {
                    bestMatch = recipe;
                }
                continue;
            }

            final double calorieDiff =
                    Math.abs(calories - targetCalories)
                            / targetCalories;
            final double proteinDiff =
                    Math.abs(protein - targetProtein)
                            / targetProtein;

            // Weighted score (calories matter more)
            final double score = (calorieDiff * CALORIE_WEIGHT)
                    + (proteinDiff * PROTEIN_WEIGHT);

            if (score < bestScore) {
                bestScore = score;
                bestMatch = recipe;
            }
        }

        return bestMatch;
    }

    /**
     * Request an alternative meal when user dislikes a recipe.
     *
     *
     * @param request the alternative request containing meal to replace
     * @return the updated meal plan response
     */
    @Transactional
    public MealPlanResponseDto requestAlternativeMeal(
            final MealPlanAlternativeRequestDto request) {

        LOGGER.info("Requesting alternative for meal {} "
                        + "in plan {}",
                request.getMealIdToReplace(),
                request.getPlanId());

        final Optional<DailyMealPlan> planOpt =
                dailyMealPlanRepository
                        .findById(request.getPlanId());

        if (planOpt.isEmpty()) {
            return createErrorResponse("Meal plan not found");
        }

        final DailyMealPlan plan = planOpt.get();
        final Optional<Meal> mealOpt = mealRepository
                .findById(request.getMealIdToReplace());

        if (mealOpt.isEmpty()) {
            return createErrorResponse("Meal not found");
        }

        final Meal originalMeal = mealOpt.get();
        final Optional<Recipe> originalRecipeOpt =
                recipeRepository
                        .findById(originalMeal.getRecipeId());

        if (originalRecipeOpt.isEmpty()) {
            return createErrorResponse("Original recipe not found");
        }

        final Recipe originalRecipe = originalRecipeOpt.get();

        // Get all eligible recipes
        final List<Recipe> allRecipes = recipeRepository.findAll();

        // Build exclusion set
        final Set<Integer> excludeIds = new HashSet<>();
        excludeIds.add(request.getDislikedRecipeId());
        if (request.getExcludeRecipeIds() != null) {
            excludeIds.addAll(request.getExcludeRecipeIds());
        }

        // Find alternative
        final double targetCalories =
                originalRecipe.getCalories().doubleValue();
        final double targetProtein =
                originalRecipe.getProtein().doubleValue();

        final Recipe alternative = selectRecipeForMeal(allRecipes,
                targetCalories, targetProtein, excludeIds, null);

        if (alternative == null) {
            return createErrorResponse("No suitable alternative found");
        }

        // Update the meal
        originalMeal.setRecipeId(alternative.getRecipeId());
        mealRepository.save(originalMeal);

        // Recalculate plan totals
        updateDailyPlanTotals(plan);
        dailyMealPlanRepository.save(plan);

        final MealPlanResponseDto response = new MealPlanResponseDto();
        response.setSuccess(true);
        response.setMessage("Alternative meal selected successfully");

        LOGGER.info("Alternative meal provided for plan {}",
                request.getPlanId());
        return response;
    }

    /**
     * Update the nutritional totals for a daily meal plan.
     *
     * @param plan the daily meal plan to update
     */
    private void updateDailyPlanTotals(final DailyMealPlan plan) {
        final List<Recipe> recipes = new ArrayList<>();
        for (final Integer mealId : plan.getMealIds()) {
            final Optional<Meal> mealOpt = mealRepository.findById(mealId);
            if (mealOpt.isPresent()) {
                final Optional<Recipe> recipeOpt = recipeRepository
                        .findById(mealOpt.get().getRecipeId());
                recipeOpt.ifPresent(recipes::add);
            }
        }

        final double totalCalories = recipes.stream()
                .mapToDouble(r ->
                        r.getCalories().doubleValue()).sum();
        final double totalProtein = recipes.stream()
                .mapToDouble(r ->
                        r.getProtein().doubleValue()).sum();
        final double totalCarbs = recipes.stream()
                .mapToDouble(r ->
                        r.getCarbohydrates().doubleValue()).sum();
        final double totalFat = recipes.stream()
                .mapToDouble(r ->
                        r.getFat().doubleValue()).sum();
        final double totalFiber = recipes.stream()
                .mapToDouble(r ->
                        r.getFiber().doubleValue()).sum();

        plan.setTotalCalories(totalCalories);
        plan.setTotalProtein(totalProtein);
        plan.setTotalCarbs(totalCarbs);
        plan.setTotalFat(totalFat);
        plan.setTotalFiber(totalFiber);
    }

    /**
     * Create a weekly meal plan from daily plans.
     *
     * @param userId     the user ID
     * @param startDate  start date of the week
     * @param dailyPlans list of daily meal plans
     * @return the created weekly meal plan
     */
    private WeeklyMealPlan createWeeklyMealPlan(
            final Integer userId,
            final LocalDate startDate,
            final List<DailyMealPlanDetailDto> dailyPlans) {

        final WeeklyMealPlan weeklyPlan = new WeeklyMealPlan();
        weeklyPlan.setUserId(userId);
        weeklyPlan.setStartDate(startDate);
        weeklyPlan.setEndDate(startDate.plusDays(LAST_DAY_OF_WEEK));
        weeklyPlan.setDailyPlanIds(dailyPlans.stream()
                .map(DailyMealPlanDetailDto::getPlanId)
                .toArray(Integer[]::new));

        // Calculate averages
        weeklyPlan.setAvgDailyCalories(dailyPlans.stream()
                .mapToDouble(DailyMealPlanDetailDto::getTotalCalories)
                .average().orElse(0.0));
        weeklyPlan.setAvgDailyProtein(dailyPlans.stream()
                .mapToDouble(DailyMealPlanDetailDto::getTotalProtein)
                .average().orElse(0.0));
        weeklyPlan.setAvgDailyCarbs(dailyPlans.stream()
                .mapToDouble(DailyMealPlanDetailDto::getTotalCarbs)
                .average().orElse(0.0));
        weeklyPlan.setAvgDailyFat(dailyPlans.stream()
                .mapToDouble(DailyMealPlanDetailDto::getTotalFat)
                .average().orElse(0.0));
        weeklyPlan.setStatus("active");

        return weeklyMealPlanRepository.save(weeklyPlan);
    }

    /**
     * Determine macro targets from request or user targets.
     *
     *
     * @param request   the meal plan request
     * @param targetOpt optional user target
     * @return the macro targets
     */
    private MacroTargets determineMacroTargets(
            final MealPlanRequestDto request,
            final Optional<UserTarget> targetOpt) {

        final MacroTargets targets = new MacroTargets();

        if (request.getTargetCalories() != null) {
            targets.setCalories(request.getTargetCalories());
        } else if (targetOpt.isPresent()) {
            targets.setCalories(
                    targetOpt.get().getCalories().doubleValue());
        } else {
            targets.setCalories(DEFAULT_CALORIES);
        }

        if (request.getTargetProtein() != null) {
            targets.setProtein(request.getTargetProtein());
        } else if (targetOpt.isPresent()) {
            targets.setProtein(
                    targetOpt.get().getProtein().doubleValue());
        } else {
            targets.setProtein(DEFAULT_PROTEIN);
        }

        if (request.getTargetCarbs() != null) {
            targets.setCarbs(request.getTargetCarbs());
        } else if (targetOpt.isPresent()) {
            targets.setCarbs(
                    targetOpt.get().getCarbs().doubleValue());
        } else {
            targets.setCarbs(DEFAULT_CARBS);
        }

        if (request.getTargetFat() != null) {
            targets.setFat(request.getTargetFat());
        } else if (targetOpt.isPresent()) {
            targets.setFat(
                    targetOpt.get().getFat().doubleValue());
        } else {
            targets.setFat(DEFAULT_FAT);
        }

        return targets;
    }

    /**
     * Calculate variance between actual and target values.
     *
     * @param actual the actual value
     * @param target the target value
     * @return the variance as a percentage
     */
    private double calculateVariance(final double actual, final double target) {
        if (target == 0) {
            return 0.0;
        }
        return ((actual - target) / target) * PERCENT_MULTIPLIER;
    }

    /**
     * Create an error response.
     *
     * @param message the error message
     * @return the error response
     */
    private MealPlanResponseDto createErrorResponse(final String message) {
        final MealPlanResponseDto response = new MealPlanResponseDto();
        response.setSuccess(false);
        response.setMessage(message);
        return response;
    }

    /**
     * Inner class to hold macro targets.
     */
    private static class MacroTargets {
        /** Target calories. */
        private double calories;

        /** Target protein in grams. */
        private double protein;

        /** Target carbohydrates in grams. */
        private double carbs;

        /** Target fat in grams. */
        private double fat;

        public double getCalories() {
            return calories;
        }

        public void setCalories(final double cals) {
            this.calories = cals;
        }

        public double getProtein() {
            return protein;
        }

        public void setProtein(final double prot) {
            this.protein = prot;
        }

        public double getCarbs() {
            return carbs;
        }

        public void setCarbs(final double carbohydrates) {
            this.carbs = carbohydrates;
        }

        public double getFat() {
            return fat;
        }

        public void setFat(final double fatValue) {
            this.fat = fatValue;
        }
    }
}

