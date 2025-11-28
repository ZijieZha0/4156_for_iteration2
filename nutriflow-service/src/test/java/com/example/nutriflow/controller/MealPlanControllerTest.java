package com.example.nutriflow.controller;

import com.example.nutriflow.mealplan.controller.MealPlanController;
import com.example.nutriflow.mealplan.dto.DailyMealPlanDetailDto;
import com.example.nutriflow.mealplan.dto.MealPlanResponseDto;
import com.example.nutriflow.mealplan.model.DailyMealPlan;
import com.example.nutriflow.mealplan.repository.DailyMealPlanRepository;
import com.example.nutriflow.mealplan.service.MealPlanService;
import com.example.nutriflow.mealplan.controller.MealPlanController;
import com.example.nutriflow.recipe.model.Recipe;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Web layer tests for {@link MealPlanController}.
 * Uses MockMvc and mocks {@link MealPlanService}.
 */
@WebMvcTest(controllers = MealPlanController.class)
class MealPlanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MealPlanService mealPlanService;

    @MockBean
    private DailyMealPlanRepository dailyMealPlanRepository;

    @Test
    @DisplayName("POST /api/meal-plans/generate → 200 with daily meal plan")
    void generateMealPlan_dailyPlan_success() throws Exception {
        // Given
        DailyMealPlanDetailDto dailyPlan = createSampleDailyPlan();
        
        MealPlanResponseDto response = new MealPlanResponseDto();
        response.setSuccess(true);
        response.setDailyPlans(Arrays.asList(dailyPlan));
        response.setTotalRecipesUsed(3);
        response.setCalorieVariance(2.5);
        response.setProteinVariance(3.5);
        response.setMessage("Daily meal plan generated successfully");

        when(mealPlanService.generateMealPlan(any())).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/api/meal-plans/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "userId": 1,
                        "mealsPerDay": 3,
                        "numberOfDays": 1,
                        "startDate": "2025-11-28",
                        "maxPrepTime": 30,
                        "clientId": "fitness-app"
                    }
                    """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.dailyPlans").isArray())
                .andExpect(jsonPath("$.dailyPlans", hasSize(1)))
                .andExpect(jsonPath("$.totalRecipesUsed").value(3))
                .andExpect(jsonPath("$.calorieVariance").value(2.5))
                .andExpect(jsonPath("$.proteinVariance").value(3.5));

        verify(mealPlanService, times(1)).generateMealPlan(any());
    }

    @Test
    @DisplayName("POST /api/meal-plans/generate → 200 with weekly meal plan")
    void generateMealPlan_weeklyPlan_success() throws Exception {
        // Given
        MealPlanResponseDto response = new MealPlanResponseDto();
        response.setSuccess(true);
        response.setDailyPlans(Arrays.asList(
            createSampleDailyPlan(), createSampleDailyPlan(), 
            createSampleDailyPlan(), createSampleDailyPlan(),
            createSampleDailyPlan(), createSampleDailyPlan(),
            createSampleDailyPlan()
        ));
        response.setTotalRecipesUsed(21);

        when(mealPlanService.generateMealPlan(any())).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/api/meal-plans/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "userId": 1,
                        "mealsPerDay": 3,
                        "numberOfDays": 7,
                        "startDate": "2025-11-28",
                        "targetCalories": 2000,
                        "targetProtein": 150,
                        "clientId": "fitness-app"
                    }
                    """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.dailyPlans", hasSize(7)))
                .andExpect(jsonPath("$.totalRecipesUsed").value(21));

        verify(mealPlanService, times(1)).generateMealPlan(any());
    }

    @Test
    @DisplayName("POST /api/meal-plans/generate with filters → 200")
    void generateMealPlan_withFilters_success() throws Exception {
        // Given
        MealPlanResponseDto response = new MealPlanResponseDto();
        response.setSuccess(true);
        response.setDailyPlans(Arrays.asList(createSampleDailyPlan()));

        when(mealPlanService.generateMealPlan(any())).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/api/meal-plans/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "userId": 1,
                        "mealsPerDay": 3,
                        "numberOfDays": 1,
                        "maxPrepTime": 30,
                        "tags": ["quick", "healthy"],
                        "preferredCuisines": ["Italian", "Mexican"],
                        "availableIngredients": ["chicken", "rice"],
                        "clientId": "fitness-app"
                    }
                    """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(mealPlanService, times(1)).generateMealPlan(any());
    }

    @Test
    @DisplayName("POST /api/meal-plans/generate → 400 when user not found")
    void generateMealPlan_userNotFound() throws Exception {
        // Given
        MealPlanResponseDto response = new MealPlanResponseDto();
        response.setSuccess(false);
        response.setMessage("User not found");

        when(mealPlanService.generateMealPlan(any())).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/api/meal-plans/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "userId": 999,
                        "mealsPerDay": 3,
                        "numberOfDays": 1,
                        "clientId": "fitness-app"
                    }
                    """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("User not found"));
    }

    @Test
    @DisplayName("POST /api/meal-plans/alternative → 200 with alternative meal")
    void requestAlternative_success() throws Exception {
        // Given
        MealPlanResponseDto response = new MealPlanResponseDto();
        response.setSuccess(true);
        response.setMessage("Alternative meal selected successfully");

        when(mealPlanService.requestAlternativeMeal(any())).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/api/meal-plans/alternative")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "userId": 1,
                        "planId": 123,
                        "mealIdToReplace": 456,
                        "dislikedRecipeId": 789,
                        "dislikeReason": "Too spicy",
                        "maintainMealType": true
                    }
                    """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Alternative meal selected successfully"));

        verify(mealPlanService, times(1)).requestAlternativeMeal(any());
    }

    @Test
    @DisplayName("POST /api/meal-plans/alternative → 400 when plan not found")
    void requestAlternative_planNotFound() throws Exception {
        // Given
        MealPlanResponseDto response = new MealPlanResponseDto();
        response.setSuccess(false);
        response.setMessage("Meal plan not found");

        when(mealPlanService.requestAlternativeMeal(any())).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/api/meal-plans/alternative")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "userId": 1,
                        "planId": 999,
                        "mealIdToReplace": 456,
                        "dislikedRecipeId": 789
                    }
                    """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Meal plan not found"));
    }

    @Test
    @DisplayName("GET /api/meal-plans/user/{userId} → 200 with meal plans")
    void getMealPlansByUser_success() throws Exception {
        // Given
        DailyMealPlan plan1 = createDailyMealPlanEntity(1, 1, LocalDate.now());
        DailyMealPlan plan2 = createDailyMealPlanEntity(2, 1, LocalDate.now().plusDays(1));
        
        when(dailyMealPlanRepository.findByUserId(1))
            .thenReturn(Arrays.asList(plan1, plan2));

        // When & Then
        mockMvc.perform(get("/api/meal-plans/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.count").value(2))
                .andExpect(jsonPath("$.mealPlans").isArray())
                .andExpect(jsonPath("$.mealPlans", hasSize(2)));

        verify(dailyMealPlanRepository, times(1)).findByUserId(1);
    }

    @Test
    @DisplayName("GET /api/meal-plans/user/{userId}?startDate&endDate → 200 with filtered plans")
    void getMealPlansByUser_withDateRange_success() throws Exception {
        // Given
        DailyMealPlan plan1 = createDailyMealPlanEntity(1, 1, LocalDate.of(2025, 11, 28));
        
        when(dailyMealPlanRepository.findByUserIdAndPlanDateBetween(
            eq(1), any(LocalDate.class), any(LocalDate.class)))
            .thenReturn(Arrays.asList(plan1));

        // When & Then
        mockMvc.perform(get("/api/meal-plans/user/1")
                .param("startDate", "2025-11-28")
                .param("endDate", "2025-11-30"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.count").value(1));

        verify(dailyMealPlanRepository, times(1))
            .findByUserIdAndPlanDateBetween(eq(1), any(LocalDate.class), any(LocalDate.class));
    }

    @Test
    @DisplayName("GET /api/meal-plans/user/{userId}?status=active → 200 with active plans")
    void getMealPlansByUser_withStatus_success() throws Exception {
        // Given
        DailyMealPlan plan1 = createDailyMealPlanEntity(1, 1, LocalDate.now());
        plan1.setStatus("active");
        
        when(dailyMealPlanRepository.findByUserIdAndStatus(1, "active"))
            .thenReturn(Arrays.asList(plan1));

        // When & Then
        mockMvc.perform(get("/api/meal-plans/user/1")
                .param("status", "active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.count").value(1));

        verify(dailyMealPlanRepository, times(1))
            .findByUserIdAndStatus(1, "active");
    }

    @Test
    @DisplayName("GET /api/meal-plans/{planId} → 200 with meal plan")
    void getMealPlanById_success() throws Exception {
        // Given
        DailyMealPlan plan = createDailyMealPlanEntity(123, 1, LocalDate.now());
        
        when(dailyMealPlanRepository.findById(123))
            .thenReturn(Optional.of(plan));

        // When & Then
        mockMvc.perform(get("/api/meal-plans/123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.mealPlan.planId").value(123));

        verify(dailyMealPlanRepository, times(1)).findById(123);
    }

    @Test
    @DisplayName("GET /api/meal-plans/{planId} → 404 when not found")
    void getMealPlanById_notFound() throws Exception {
        // Given
        when(dailyMealPlanRepository.findById(999))
            .thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/meal-plans/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Meal plan not found"));

        verify(dailyMealPlanRepository, times(1)).findById(999);
    }

    @Test
    @DisplayName("PUT /api/meal-plans/{planId}/status → 200 updates status")
    void updateMealPlanStatus_success() throws Exception {
        // Given
        DailyMealPlan plan = createDailyMealPlanEntity(123, 1, LocalDate.now());
        plan.setStatus("active");
        
        when(dailyMealPlanRepository.findById(123))
            .thenReturn(Optional.of(plan));
        when(dailyMealPlanRepository.save(any(DailyMealPlan.class)))
            .thenReturn(plan);

        // When & Then
        mockMvc.perform(put("/api/meal-plans/123/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "status": "completed"
                    }
                    """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Meal plan status updated successfully"));

        verify(dailyMealPlanRepository, times(1)).findById(123);
        verify(dailyMealPlanRepository, times(1)).save(any(DailyMealPlan.class));
    }

    @Test
    @DisplayName("PUT /api/meal-plans/{planId}/status → 404 when not found")
    void updateMealPlanStatus_notFound() throws Exception {
        // Given
        when(dailyMealPlanRepository.findById(999))
            .thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(put("/api/meal-plans/999/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "status": "completed"
                    }
                    """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Meal plan not found"));

        verify(dailyMealPlanRepository, times(1)).findById(999);
        verify(dailyMealPlanRepository, never()).save(any());
    }

    @Test
    @DisplayName("DELETE /api/meal-plans/{planId} → 200 deletes plan")
    void deleteMealPlan_success() throws Exception {
        // Given
        DailyMealPlan plan = createDailyMealPlanEntity(123, 1, LocalDate.now());
        
        when(dailyMealPlanRepository.findById(123))
            .thenReturn(Optional.of(plan));
        doNothing().when(dailyMealPlanRepository).deleteById(123);

        // When & Then
        mockMvc.perform(delete("/api/meal-plans/123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Meal plan deleted successfully"));

        verify(dailyMealPlanRepository, times(1)).findById(123);
        verify(dailyMealPlanRepository, times(1)).deleteById(123);
    }

    @Test
    @DisplayName("DELETE /api/meal-plans/{planId} → 404 when not found")
    void deleteMealPlan_notFound() throws Exception {
        // Given
        when(dailyMealPlanRepository.findById(999))
            .thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(delete("/api/meal-plans/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Meal plan not found"));

        verify(dailyMealPlanRepository, times(1)).findById(999);
        verify(dailyMealPlanRepository, never()).deleteById(anyInt());
    }

    // Helper methods

    private DailyMealPlanDetailDto createSampleDailyPlan() {
        DailyMealPlanDetailDto plan = new DailyMealPlanDetailDto();
        plan.setPlanId(1);
        plan.setPlanDate(LocalDate.of(2025, 11, 28));
        plan.setTotalCalories(2050.5);
        plan.setTotalProtein(155.2);
        plan.setTotalCarbs(245.8);
        plan.setTotalFat(68.3);
        plan.setTotalFiber(35.0);
        
        // Add sample meals
        DailyMealPlanDetailDto.MealDetailDto meal1 = new DailyMealPlanDetailDto.MealDetailDto();
        meal1.setMealId(1);
        meal1.setMealType("breakfast");
        meal1.setServings(1);
        
        Recipe recipe1 = new Recipe();
        recipe1.setRecipeId(1);
        recipe1.setTitle("Scrambled Eggs");
        recipe1.setCalories(new BigDecimal("400"));
        recipe1.setProtein(new BigDecimal("30"));
        meal1.setRecipe(recipe1);
        
        plan.setMeals(Arrays.asList(meal1));
        
        return plan;
    }

    private DailyMealPlan createDailyMealPlanEntity(int planId, int userId, LocalDate date) {
        DailyMealPlan plan = new DailyMealPlan();
        plan.setPlanId(planId);
        plan.setUserId(userId);
        plan.setPlanDate(date);
        plan.setMealIds(new Integer[]{1, 2, 3});
        plan.setTotalCalories(2000.0);
        plan.setTotalProtein(150.0);
        plan.setTotalCarbs(250.0);
        plan.setTotalFat(65.0);
        plan.setTotalFiber(30.0);
        plan.setStatus("active");
        return plan;
    }
}

