package com.example.nutriflow.mealplan.controller;

import com.example.nutriflow.mealplan.model.DailyMealPlan;
import com.example.nutriflow.mealplan.dto.MealPlanAlternativeRequestDto;
import com.example.nutriflow.mealplan.dto.MealPlanRequestDto;
import com.example.nutriflow.mealplan.dto.MealPlanResponseDto;
import com.example.nutriflow.mealplan.service.MealPlanService;
import com.example.nutriflow.mealplan.repository.DailyMealPlanRepository;
import com.example.nutriflow.mealplan.repository.WeeklyMealPlanRepository;
import com.example.nutriflow.mealplan.model.WeeklyMealPlan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * REST controller for meal plan operations.
 * Provides endpoints for the fitness app client to:
 * - Generate daily/weekly meal plans
 * - Request alternative meals when user dislikes a suggestion
 * - Retrieve existing meal plans
 * - Update and delete meal plans
 *
 * All API calls are logged with timestamp, client identifier,
 * endpoint, and status.
 */
@RestController
@RequestMapping("/api/meal-plans")
public class MealPlanController {

    /** Logger for this controller. */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(MealPlanController.class);

    /** Service for meal plan operations. */
    @Autowired
    private MealPlanService mealPlanService;

    /** Repository for meal plan data. */
    @Autowired
    private DailyMealPlanRepository dailyMealPlanRepository;

    /** Repository for weekly meal plan data. */
    @Autowired
    private WeeklyMealPlanRepository weeklyMealPlanRepository;

    /**
     * Generate a meal plan based on user preferences and constraints.
     * Supports both daily and weekly meal plan generation.
     *
     *
     * POST /api/meal-plans/generate
     *
     * Request body example:
     * {
     *   "userId": 1,
     *   "mealsPerDay": 3,
     *   "numberOfDays": 7,
     *   "startDate": "2025-11-28",
     *   "maxPrepTime": 30,
     *   "targetCalories": 2000,
     *   "targetProtein": 150,
     *   "availableIngredients": ["chicken", "rice", "broccoli"],
     *   "preferredCuisines": ["Italian", "Mexican"],
     *   "tags": ["high-protein", "quick"],
     *   "useAiGeneration": false,
     *   "clientId": "fitness-app"
     * }
     *
     * @param request the meal plan request DTO
     * @return ResponseEntity with the generated meal plan or
     *         error message
     */
    @PostMapping("/generate")
    public ResponseEntity<MealPlanResponseDto> generateMealPlan(
            @RequestBody final MealPlanRequestDto request) {

        final LocalDateTime requestTime = LocalDateTime.now();
        final String clientId = request.getClientId() != null
                ? request.getClientId() : "unknown";

        LOGGER.info("[API_CALL] timestamp={}, client={}, "
                        + "endpoint=POST /api/meal-plans/generate, "
                        + "userId={}, mealsPerDay={}, days={}",
                requestTime, clientId, request.getUserId(),
                request.getMealsPerDay(), request.getNumberOfDays());

        try {
            final MealPlanResponseDto response =
                    mealPlanService.generateMealPlan(request);

            final HttpStatus status = response.getSuccess()
                    ? HttpStatus.OK : HttpStatus.BAD_REQUEST;

            LOGGER.info("[API_RESPONSE] timestamp={}, client={}, "
                    + "endpoint=POST /api/meal-plans/generate, "
                    + "status={}, success={}",
                    LocalDateTime.now(), clientId, status,
                    response.getSuccess());

            return ResponseEntity.status(status).body(response);

        } catch (final Exception e) {
            LOGGER.error("[API_ERROR] timestamp={}, client={}, "
                    + "endpoint=POST /api/meal-plans/generate, "
                    + "error={}",
                    LocalDateTime.now(), clientId, e.getMessage(), e);

            final MealPlanResponseDto errorResponse =
                    new MealPlanResponseDto();
            errorResponse.setSuccess(false);
            errorResponse.setMessage(
                    "Error generating meal plan: " + e.getMessage());

            return ResponseEntity.status(
                    HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse);
        }
    }

    /**
     * Request an alternative meal when user dislikes a recipe.
     * Replaces the meal while maintaining nutritional balance.
     *
     * POST /api/meal-plans/alternative
     *
     * Request body example:
     * {
     *   "userId": 1,
     *   "planId": 123,
     *   "mealIdToReplace": 456,
     *   "dislikedRecipeId": 789,
     *   "dislikeReason": "Too spicy",
     *   "excludeRecipeIds": [789, 790],
     *   "maintainMealType": true,
     *   "maxCalorieDifference": 100
     * }
     *
     * @param request the alternative request DTO
     * @return ResponseEntity with the updated meal plan
     */
    @PostMapping("/alternative")
    public ResponseEntity<MealPlanResponseDto> requestAlternative(
            @RequestBody final MealPlanAlternativeRequestDto request) {

        final LocalDateTime requestTime = LocalDateTime.now();

        LOGGER.info("[API_CALL] timestamp={}, "
                        + "endpoint=POST /api/meal-plans/alternative, "
                        + "userId={}, planId={}, mealId={}, "
                        + "dislikedRecipe={}",
                requestTime, request.getUserId(), request.getPlanId(),
                request.getMealIdToReplace(), request.getDislikedRecipeId());

        try {
            final MealPlanResponseDto response = mealPlanService
                    .requestAlternativeMeal(request);

            final HttpStatus status = response.getSuccess()
                    ? HttpStatus.OK : HttpStatus.BAD_REQUEST;

            LOGGER.info("[API_RESPONSE] timestamp={}, "
                    + "endpoint=POST /api/meal-plans/alternative, "
                    + "status={}, success={}",
                    LocalDateTime.now(), status,
                    response.getSuccess());

            return ResponseEntity.status(status).body(response);

        } catch (final Exception e) {
            LOGGER.error("[API_ERROR] timestamp={}, "
                    + "endpoint=POST /api/meal-plans/alternative, "
                    + "error={}",
                    LocalDateTime.now(), e.getMessage(), e);

            final MealPlanResponseDto errorResponse =
                    new MealPlanResponseDto();
            errorResponse.setSuccess(false);
            errorResponse.setMessage(
                    "Error requesting alternative: " + e.getMessage());

            return ResponseEntity.status(
                    HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse);
        }
    }

    /**
     * Retrieve meal plans for a specific user.
     * Optionally filter by date range and status.
     *
     *
     * GET /api/meal-plans/user/{userId}?startDate=...
     *     &endDate=...&status=active
     *
     * @param userId    the user ID
     * @param startDate optional start date filter
     * @param endDate   optional end date filter
     * @param status    optional status filter
     *                  (e.g., "active", "completed")
     * @return ResponseEntity with list of meal plans
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> getMealPlansByUser(
            @PathVariable final Integer userId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            final LocalDate startDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            final LocalDate endDate,
            @RequestParam(required = false)
            final String status) {

        final LocalDateTime requestTime = LocalDateTime.now();

        LOGGER.info("[API_CALL] timestamp={}, "
                        + "endpoint=GET /api/meal-plans/user/{}, "
                        + "startDate={}, endDate={}, status={}",
                requestTime, userId, startDate, endDate, status);

        try {
            List<DailyMealPlan> mealPlans;

            if (startDate != null && endDate != null) {
                mealPlans = dailyMealPlanRepository
                        .findByUserIdAndPlanDateBetween(
                                userId, startDate, endDate);
            } else if (status != null) {
                mealPlans = dailyMealPlanRepository
                        .findByUserIdAndStatus(userId, status);
            } else {
                mealPlans = dailyMealPlanRepository.findByUserId(userId);
            }

            final Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("count", mealPlans.size());
            response.put("mealPlans", mealPlans);

            LOGGER.info("[API_RESPONSE] timestamp={}, "
                    + "endpoint=GET /api/meal-plans/user/{}, "
                    + "status=200, count={}",
                    LocalDateTime.now(), userId,
                    mealPlans.size());

            return ResponseEntity.ok(response);

        } catch (final Exception e) {
            LOGGER.error("[API_ERROR] timestamp={}, "
                    + "endpoint=GET /api/meal-plans/user/{}, "
                    + "error={}",
                    LocalDateTime.now(), userId, e.getMessage(), e);

            final Map<String, Object> errorResponse =
                    new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message",
                    "Error retrieving meal plans: " + e.getMessage());

            return ResponseEntity.status(
                    HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse);
        }
    }

    /**
     * Retrieve a specific meal plan by ID.
     *
     * GET /api/meal-plans/{planId}
     *
     * @param planId the meal plan ID
     * @return ResponseEntity with the meal plan details
     */
    @GetMapping("/{planId}")
    public ResponseEntity<Map<String, Object>> getMealPlanById(
            @PathVariable final Integer planId) {

        final LocalDateTime requestTime = LocalDateTime.now();

        LOGGER.info("[API_CALL] timestamp={}, "
                + "endpoint=GET /api/meal-plans/{}",
                requestTime, planId);

        try {
            final Optional<DailyMealPlan> dailyPlanOpt =
                    dailyMealPlanRepository.findById(planId);

            if (dailyPlanOpt.isPresent()) {
                final Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("planType", "daily");
                response.put("mealPlan", dailyPlanOpt.get());

                LOGGER.info("[API_RESPONSE] timestamp={}, "
                        + "endpoint=GET /api/meal-plans/{}, "
                        + "status=200 (daily)",
                        LocalDateTime.now(), planId);

                return ResponseEntity.ok(response);
            }

            final Optional<WeeklyMealPlan> weeklyPlanOpt =
                    weeklyMealPlanRepository.findById(planId);
            if (weeklyPlanOpt.isPresent()) {
                final Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("planType", "weekly");
                response.put("mealPlan", weeklyPlanOpt.get());

                LOGGER.info("[API_RESPONSE] timestamp={}, "
                        + "endpoint=GET /api/meal-plans/{}, "
                        + "status=200 (weekly)",
                        LocalDateTime.now(), planId);

                return ResponseEntity.ok(response);
            }

            LOGGER.info("[API_RESPONSE] timestamp={}, "
                    + "endpoint=GET /api/meal-plans/{}, status=404",
                    LocalDateTime.now(), planId);

            final Map<String, Object> errorResponse =
                    new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Meal plan not found");

            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(errorResponse);

        } catch (final Exception e) {
            LOGGER.error("[API_ERROR] timestamp={}, "
                    + "endpoint=GET /api/meal-plans/{}, "
                    + "error={}",
                    LocalDateTime.now(), planId,
                    e.getMessage(), e);

            final Map<String, Object> errorResponse =
                    new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message",
                    "Error retrieving meal plan: " + e.getMessage());

            return ResponseEntity.status(
                    HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse);
        }
    }

    /**
     * Update the status of a meal plan.
     *
     * PUT /api/meal-plans/{planId}/status
     *
     * Request body: { "status": "completed" }
     *
     * @param planId  the meal plan ID
     * @param request map containing the new status
     * @return ResponseEntity with success message
     *
     */
    @PutMapping("/{planId}/status")
    public ResponseEntity<Map<String, Object>> updateMealPlanStatus(
            @PathVariable final Integer planId,
            @RequestBody final Map<String, String> request) {

        final LocalDateTime requestTime = LocalDateTime.now();
        final String newStatus = request.get("status");

        LOGGER.info("[API_CALL] timestamp={}, "
                        + "endpoint=PUT /api/meal-plans/{}/status, "
                        + "newStatus={}",
                requestTime, planId, newStatus);

        try {
            final Optional<DailyMealPlan> planOpt =
                    dailyMealPlanRepository.findById(planId);

            if (planOpt.isEmpty()) {
                final Map<String, Object> errorResponse =
                        new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message",
                        "Meal plan not found");

                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(errorResponse);
            }

            final DailyMealPlan plan = planOpt.get();
            plan.setStatus(newStatus);
            dailyMealPlanRepository.save(plan);

            final Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message",
                    "Meal plan status updated successfully");
            response.put("mealPlan", plan);

            LOGGER.info("[API_RESPONSE] timestamp={}, "
                    + "endpoint=PUT /api/meal-plans/{}/status, "
                    + "status=200",
                    LocalDateTime.now(), planId);

            return ResponseEntity.ok(response);

        } catch (final Exception e) {
            LOGGER.error("[API_ERROR] timestamp={}, "
                    + "endpoint=PUT /api/meal-plans/{}/status, "
                    + "error={}",
                    LocalDateTime.now(), planId, e.getMessage(), e);

            final Map<String, Object> errorResponse =
                    new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message",
                    "Error updating meal plan: " + e.getMessage());

            return ResponseEntity.status(
                    HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse);
        }
    }

    /**
     * Delete a meal plan.
     *
     * DELETE /api/meal-plans/{planId}
     *
     * @param planId the meal plan ID to delete
     * @return ResponseEntity with success message
     */
    @DeleteMapping("/{planId}")
    public ResponseEntity<Map<String, Object>> deleteMealPlan(
            @PathVariable final Integer planId) {

        final LocalDateTime requestTime = LocalDateTime.now();

        LOGGER.info("[API_CALL] timestamp={}, "
                + "endpoint=DELETE /api/meal-plans/{}",
                requestTime, planId);

        try {
            final Optional<DailyMealPlan> planOpt =
                    dailyMealPlanRepository.findById(planId);

            if (planOpt.isEmpty()) {
                final Map<String, Object> errorResponse =
                        new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message",
                        "Meal plan not found");

                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(errorResponse);
            }

            dailyMealPlanRepository.deleteById(planId);

            final Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Meal plan deleted successfully");

            LOGGER.info("[API_RESPONSE] timestamp={}, "
                    + "endpoint=DELETE /api/meal-plans/{}, "
                    + "status=200",
                    LocalDateTime.now(), planId);

            return ResponseEntity.ok(response);

        } catch (final Exception e) {
            LOGGER.error("[API_ERROR] timestamp={}, "
                    + "endpoint=DELETE /api/meal-plans/{}, "
                    + "error={}",
                    LocalDateTime.now(), planId,
                    e.getMessage(), e);

            final Map<String, Object> errorResponse =
                    new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message",
                    "Error deleting meal plan: " + e.getMessage());

            return ResponseEntity.status(
                    HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse);
        }
    }
}

