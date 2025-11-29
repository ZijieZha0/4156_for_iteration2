package com.example.nutriflow.controller;

import com.example.nutriflow.user.controller.UserController;
import com.example.nutriflow.user.model.User;
import com.example.nutriflow.user.model.UserTarget;
import com.example.nutriflow.user.dto.HealthStatisticsResponseDTO;
import com.example.nutriflow.user.dto.UpdateUserRequestDTO;
import com.example.nutriflow.user.dto.UpdateUserTargetRequestDTO;
import com.example.nutriflow.shared.enums.BMICategory;
import com.example.nutriflow.shared.enums.CookingSkillLevel;
import com.example.nutriflow.shared.enums.SexType;
import com.example.nutriflow.user.service.HealthStatisticsService;
import com.example.nutriflow.user.service.UserService;
import com.example.nutriflow.user.controller.UserController;
import com.example.nutriflow.user.service.UserTargetService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private UserTargetService userTargetService;

    @MockBean
    private HealthStatisticsService healthStatisticsService;

    private User sampleUser;
    private UserTarget sampleUserTarget;

    @BeforeEach
    void setUp() {
        // Set up sample user for testing
        sampleUser = new User();
        sampleUser.setUserId(1);
        sampleUser.setName("John Doe");
        sampleUser.setHeight(new BigDecimal("175.50"));
        sampleUser.setWeight(new BigDecimal("70.00"));
        sampleUser.setAge(30);
        sampleUser.setSex(SexType.MALE);
        sampleUser.setAllergies(new String[]{"peanuts", "shellfish"});
        sampleUser.setDislikes(new String[]{"broccoli"});
        sampleUser.setBudget(new BigDecimal("500.00"));
        sampleUser.setCookingSkillLevel(CookingSkillLevel.INTERMEDIATE);
        sampleUser.setEquipments(new String[]{"oven", "stove"});
        sampleUser.setCreatedAt(LocalDateTime.now());
        sampleUser.setUpdatedAt(LocalDateTime.now());

        // Set up sample user target for testing
        sampleUserTarget = new UserTarget();
        sampleUserTarget.setTargetId(1);
        sampleUserTarget.setUserId(1);
        sampleUserTarget.setCalories(new BigDecimal("2000.00"));
        sampleUserTarget.setProtein(new BigDecimal("150.00"));
        sampleUserTarget.setFiber(new BigDecimal("30.00"));
        sampleUserTarget.setFat(new BigDecimal("65.00"));
        sampleUserTarget.setCarbs(new BigDecimal("250.00"));
        sampleUserTarget.setIron(new BigDecimal("18.00"));
        sampleUserTarget.setCalcium(new BigDecimal("1000.00"));
        sampleUserTarget.setVitaminA(new BigDecimal("900.00"));
        sampleUserTarget.setVitaminC(new BigDecimal("90.00"));
        sampleUserTarget.setVitaminD(new BigDecimal("20.00"));
        sampleUserTarget.setSodium(new BigDecimal("2300.00"));
        sampleUserTarget.setPotassium(new BigDecimal("3500.00"));
        sampleUserTarget.setCreatedAt(LocalDateTime.now());
        sampleUserTarget.setUpdatedAt(LocalDateTime.now());
    }

    // ===============================================================
    // GET /api/users/{userId} - Get User By ID Tests
    // ===============================================================

    @Test
    void testGetUserById_TypicalValidInput_ReturnsUser() throws Exception {
        when(userService.getUserById(1)).thenReturn(Optional.of(sampleUser));

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.height").value(175.50))
                .andExpect(jsonPath("$.weight").value(70.00))
                .andExpect(jsonPath("$.age").value(30))
                .andExpect(jsonPath("$.sex").value("MALE"));
    }

    @Test
    void testGetUserById_AtypicalValidInput_LargeUserId() throws Exception {
        User userWithLargeId = new User();
        userWithLargeId.setUserId(999999);
        userWithLargeId.setName("Test User");
        when(userService.getUserById(999999)).thenReturn(Optional.of(userWithLargeId));

        mockMvc.perform(get("/api/users/999999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(999999));
    }

    @Test
    void testGetUserById_InvalidInput_NonExistentUser() throws Exception {
        when(userService.getUserById(9999)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/users/9999"))
                .andExpect(status().isNotFound());
    }

    // ===============================================================
    // PUT /api/users/{userId} - Update User Tests
    // ===============================================================

    @Test
    void testUpdateUser_TypicalValidInput_ReturnsUpdatedUser() throws Exception {
        UpdateUserRequestDTO request = new UpdateUserRequestDTO();
        request.setHeight(new BigDecimal("180.00"));
        request.setWeight(new BigDecimal("75.00"));
        request.setAge(31);
        request.setSex(SexType.MALE);
        request.setAllergies(new String[]{"peanuts", "shellfish", "dairy"});
        request.setDislikes(new String[]{"broccoli", "spinach"});
        request.setBudget(new BigDecimal("600.00"));
        request.setCookingSkill(CookingSkillLevel.ADVANCED);
        request.setEquipments(new String[]{"oven", "stove", "blender"});

        User updatedUser = new User();
        updatedUser.setUserId(1);
        updatedUser.setName("John Doe");
        updatedUser.setHeight(request.getHeight());
        updatedUser.setWeight(request.getWeight());
        updatedUser.setAge(request.getAge());
        updatedUser.setSex(request.getSex());
        updatedUser.setAllergies(request.getAllergies());
        updatedUser.setDislikes(request.getDislikes());
        updatedUser.setBudget(request.getBudget());
        updatedUser.setCookingSkillLevel(request.getCookingSkill());
        updatedUser.setEquipments(request.getEquipments());

        when(userService.updateUser(eq(1), any(UpdateUserRequestDTO.class)))
                .thenReturn(Optional.of(updatedUser));

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.height").value(180.00))
                .andExpect(jsonPath("$.weight").value(75.00))
                .andExpect(jsonPath("$.age").value(31))
                .andExpect(jsonPath("$.cookingSkillLevel").value("ADVANCED"));
    }

    @Test
    void testUpdateUser_AtypicalValidInput_PartialUpdate() throws Exception {
        // update only a few fields, some with boundary values
        UpdateUserRequestDTO request = new UpdateUserRequestDTO();
        request.setWeight(new BigDecimal("50.00")); // minimal healthy weight
        request.setAge(18); // minimum adult age
        request.setAllergies(new String[]{}); // empty array

        User updatedUser = new User();
        updatedUser.setUserId(1);
        updatedUser.setName("John Doe");
        updatedUser.setHeight(sampleUser.getHeight()); // unchanged
        updatedUser.setWeight(request.getWeight());
        updatedUser.setAge(request.getAge());
        updatedUser.setAllergies(request.getAllergies());

        when(userService.updateUser(eq(1), any(UpdateUserRequestDTO.class)))
                .thenReturn(Optional.of(updatedUser));

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.weight").value(50.00))
                .andExpect(jsonPath("$.age").value(18));
    }

    @Test
    void testUpdateUser_InvalidInput_UserNotFound() throws Exception {
        // trying to update non-existent user
        UpdateUserRequestDTO request = new UpdateUserRequestDTO();
        request.setWeight(new BigDecimal("75.00"));

        when(userService.updateUser(eq(9999), any(UpdateUserRequestDTO.class)))
                .thenReturn(Optional.empty());

        mockMvc.perform(put("/api/users/9999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    // ===============================================================
    // GET /api/users/{userId}/targets - Get User Targets Tests
    // ===============================================================

    @Test
    void testGetUserTargets_TypicalValidInput_ReturnsTargets() throws Exception {
        // get targets for existing user
        when(userTargetService.getUserTargets(1)).thenReturn(Optional.of(sampleUserTarget));

        mockMvc.perform(get("/api/users/1/targets"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.calories").value(2000.00))
                .andExpect(jsonPath("$.protein").value(150.00))
                .andExpect(jsonPath("$.fiber").value(30.00))
                .andExpect(jsonPath("$.fat").value(65.00))
                .andExpect(jsonPath("$.carbs").value(250.00));
    }

    @Test
    void testGetUserTargets_AtypicalValidInput_MinimalTargets() throws Exception {
        // user with minimal target values
        UserTarget minimalTarget = new UserTarget();
        minimalTarget.setTargetId(2);
        minimalTarget.setUserId(2);
        minimalTarget.setCalories(new BigDecimal("1200.00")); // minimum healthy calories
        minimalTarget.setProtein(new BigDecimal("50.00"));
        
        when(userTargetService.getUserTargets(2)).thenReturn(Optional.of(minimalTarget));

        mockMvc.perform(get("/api/users/2/targets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.calories").value(1200.00))
                .andExpect(jsonPath("$.protein").value(50.00));
    }

    @Test
    void testGetUserTargets_InvalidInput_UserNotFound() throws Exception {
        // get targets for non-existent user
        when(userTargetService.getUserTargets(9999)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/users/9999/targets"))
                .andExpect(status().isNotFound());
    }

    // ===============================================================
    // PUT /api/users/{userId}/targets - Update User Targets Tests
    // ===============================================================

    @Test
    void testUpdateUserTargets_TypicalValidInput_ReturnsUpdatedTargets() throws Exception {
        // update targets with standard nutritional values
        UpdateUserTargetRequestDTO request = new UpdateUserTargetRequestDTO();
        request.setCalories(new BigDecimal("2200.00"));
        request.setProtein(new BigDecimal("160.00"));
        request.setFiber(new BigDecimal("35.00"));
        request.setFat(new BigDecimal("70.00"));
        request.setCarbs(new BigDecimal("275.00"));
        request.setIron(new BigDecimal("18.00"));
        request.setCalcium(new BigDecimal("1200.00"));
        request.setVitaminA(new BigDecimal("900.00"));
        request.setVitaminC(new BigDecimal("90.00"));
        request.setVitaminD(new BigDecimal("20.00"));
        request.setSodium(new BigDecimal("2300.00"));
        request.setPotassium(new BigDecimal("3500.00"));

        UserTarget updatedTarget = new UserTarget();
        updatedTarget.setTargetId(1);
        updatedTarget.setUserId(1);
        updatedTarget.setCalories(request.getCalories());
        updatedTarget.setProtein(request.getProtein());
        updatedTarget.setFiber(request.getFiber());
        updatedTarget.setFat(request.getFat());
        updatedTarget.setCarbs(request.getCarbs());
        updatedTarget.setIron(request.getIron());
        updatedTarget.setCalcium(request.getCalcium());
        updatedTarget.setVitaminA(request.getVitaminA());
        updatedTarget.setVitaminC(request.getVitaminC());
        updatedTarget.setVitaminD(request.getVitaminD());
        updatedTarget.setSodium(request.getSodium());
        updatedTarget.setPotassium(request.getPotassium());

        when(userTargetService.updateUserTargets(eq(1), any(UpdateUserTargetRequestDTO.class)))
                .thenReturn(Optional.of(updatedTarget));

        mockMvc.perform(put("/api/users/1/targets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.calories").value(2200.00))
                .andExpect(jsonPath("$.protein").value(160.00))
                .andExpect(jsonPath("$.fiber").value(35.00));
    }

    @Test
    void testUpdateUserTargets_AtypicalValidInput_PartialUpdateWithExtremeValues() throws Exception {
        // partial update with boundary/extreme values
        UpdateUserTargetRequestDTO request = new UpdateUserTargetRequestDTO();
        request.setCalories(new BigDecimal("5000.00")); // high calorie target for athletes
        request.setProtein(new BigDecimal("250.00")); // high protein for bodybuilders
        // Other fields are null - partial update

        UserTarget updatedTarget = new UserTarget();
        updatedTarget.setTargetId(1);
        updatedTarget.setUserId(1);
        updatedTarget.setCalories(request.getCalories());
        updatedTarget.setProtein(request.getProtein());
        // Keep other fields from existing target
        updatedTarget.setFiber(sampleUserTarget.getFiber());
        updatedTarget.setFat(sampleUserTarget.getFat());

        when(userTargetService.updateUserTargets(eq(1), any(UpdateUserTargetRequestDTO.class)))
                .thenReturn(Optional.of(updatedTarget));

        mockMvc.perform(put("/api/users/1/targets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.calories").value(5000.00))
                .andExpect(jsonPath("$.protein").value(250.00));
    }

    @Test
    void testUpdateUserTargets_InvalidInput_UserNotFound() throws Exception {
        // update targets for non-existent user
        UpdateUserTargetRequestDTO request = new UpdateUserTargetRequestDTO();
        request.setCalories(new BigDecimal("2000.00"));

        when(userTargetService.updateUserTargets(eq(9999), any(UpdateUserTargetRequestDTO.class)))
                .thenReturn(Optional.empty());

        mockMvc.perform(put("/api/users/9999/targets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    // ===============================================================
    // GET /api/users/{userId}/health_statistics - Get Health Statistics Tests
    // ===============================================================

    @Test
    void testGetHealthStatistics_TypicalValidInput_ReturnsHealthStats() throws Exception {
        // get health statistics for user with complete data
        HealthStatisticsResponseDTO.CurrentHealthMetrics currentMetrics = 
        new HealthStatisticsResponseDTO.CurrentHealthMetrics(
                new BigDecimal("70.00"),     
                new BigDecimal("175.50"),   
                new BigDecimal("22.76"),
                BMICategory.NORMAL_WEIGHT,
                "Normal Weight",
                "Your BMI is in the healthy range."
        );

    HealthStatisticsResponseDTO.HealthHistoryEntry historyEntry = 
        new HealthStatisticsResponseDTO.HealthHistoryEntry(
                1,
                new BigDecimal("68.00"),
                new BigDecimal("175.50"),
                new BigDecimal("22.09"),
                LocalDateTime.now().minusDays(30)
        );

    HealthStatisticsResponseDTO response = new HealthStatisticsResponseDTO(
        currentMetrics,
        Arrays.asList(historyEntry)
    );

    when(healthStatisticsService.getHealthStatistics(1)).thenReturn(Optional.of(response));

    mockMvc.perform(get("/api/users/1/health_statistics"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.currentMetrics.weight").value(70.00))
        .andExpect(jsonPath("$.currentMetrics.bmi").value(22.76))
        .andExpect(jsonPath("$.currentMetrics.bmiCategory").value("NORMAL_WEIGHT"))
        .andExpect(jsonPath("$.history[0].weight").value(68.00));
    }

    @Test
    void testGetHealthStatistics_AtypicalValidInput_EmptyHistory() throws Exception {
    // user with no health history, only current metrics
    HealthStatisticsResponseDTO.CurrentHealthMetrics currentMetrics = 
    new HealthStatisticsResponseDTO.CurrentHealthMetrics(
            new BigDecimal("95.00"),
            new BigDecimal("175.50"),
            new BigDecimal("30.89"),
            BMICategory.OBESE,
            "Obese",
            "Your BMI indicates obesity."
    );

    HealthStatisticsResponseDTO response = new HealthStatisticsResponseDTO(
            currentMetrics,
            Arrays.asList() // Empty history
    );

    when(healthStatisticsService.getHealthStatistics(1)).thenReturn(Optional.of(response));

    mockMvc.perform(get("/api/users/1/health_statistics"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.currentMetrics.weight").value(95.00))
            .andExpect(jsonPath("$.currentMetrics.bmiCategory").value("OBESE"))
            .andExpect(jsonPath("$.history").isEmpty());
     }

    @Test
    void testGetHealthStatistics_InvalidInput_UserNotFound() throws Exception {
        // get health statistics for non-existent user
        when(healthStatisticsService.getHealthStatistics(9999)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/users/9999/health_statistics"))
                .andExpect(status().isNotFound());
    }
}