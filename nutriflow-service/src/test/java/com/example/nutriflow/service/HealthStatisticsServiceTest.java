package com.example.nutriflow.service;

import com.example.nutriflow.user.model.User;
import com.example.nutriflow.user.model.UserHealthHistory;
import com.example.nutriflow.user.dto.HealthStatisticsResponseDTO;
import com.example.nutriflow.shared.enums.BMICategory;
import com.example.nutriflow.user.repository.UserHealthHistoryRepository;
import com.example.nutriflow.user.repository.UserRepository;
import com.example.nutriflow.user.service.HealthStatisticsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for HealthStatisticsService
 * Tests BMI calculation, health metrics, and historical data retrieval
 */
@ExtendWith(MockitoExtension.class)
class HealthStatisticsServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserHealthHistoryRepository healthHistoryRepository;

    @InjectMocks
    private HealthStatisticsService healthStatisticsService;

    private User testUser;
    private List<UserHealthHistory> testHistory;

    @BeforeEach
    void setUp() {
        // Setup typical test user with normal BMI
        testUser = new User();
        testUser.setUserId(1);
        testUser.setName("John Doe");
        testUser.setHeight(new BigDecimal("175.0")); // 175 cm
        testUser.setWeight(new BigDecimal("70.0"));  // 70 kg, BMI ~22.86

        // Setup health history
        UserHealthHistory history1 = new UserHealthHistory();
        history1.setHistoryId(1);
        history1.setUserId(1);
        history1.setWeight(new BigDecimal("68.0"));
        history1.setHeight(new BigDecimal("175.0"));
        history1.setBmi(new BigDecimal("22.20"));
        history1.setRecordedAt(LocalDateTime.now().minusDays(30));

        UserHealthHistory history2 = new UserHealthHistory();
        history2.setHistoryId(2);
        history2.setUserId(1);
        history2.setWeight(new BigDecimal("69.0"));
        history2.setHeight(new BigDecimal("175.0"));
        history2.setBmi(new BigDecimal("22.53"));
        history2.setRecordedAt(LocalDateTime.now().minusDays(15));

        testHistory = Arrays.asList(history2, history1); // Most recent first
    }

    @Test
    void testGetHealthStatistics_TypicalValidInput_ReturnsCompleteStatistics() {
        // Arrange
        when(userRepository.findUserById(1)).thenReturn(Optional.of(testUser));
        when(healthHistoryRepository.findByUserIdOrderByRecordedAtDesc(1)).thenReturn(testHistory);

        Optional<HealthStatisticsResponseDTO> result = healthStatisticsService.getHealthStatistics(1);
        assertTrue(result.isPresent());
        
        // Verify current metrics
        HealthStatisticsResponseDTO.CurrentHealthMetrics metrics = result.get().getCurrentMetrics();
        assertNotNull(metrics);
        assertEquals(new BigDecimal("70.0"), metrics.getWeight());
        assertEquals(new BigDecimal("175.0"), metrics.getHeight());
        assertEquals(new BigDecimal("22.86"), metrics.getBmi()); // 70 / (1.75^2)
        assertEquals(BMICategory.NORMAL_WEIGHT, metrics.getBmiCategory());
        
        // Verify history
        List<HealthStatisticsResponseDTO.HealthHistoryEntry> history = result.get().getHistory();
        assertEquals(2, history.size());
        
        verify(userRepository, times(1)).findUserById(1);
        verify(healthHistoryRepository, times(1)).findByUserIdOrderByRecordedAtDesc(1);
    }

    @Test
    void testGetHealthStatistics_AtypicalValidInput_UnderweightUser() {
        // User with BMI < 18.5 (underweight)
        testUser.setWeight(new BigDecimal("50.0")); // BMI ~16.33
        
        when(userRepository.findUserById(1)).thenReturn(Optional.of(testUser));
        when(healthHistoryRepository.findByUserIdOrderByRecordedAtDesc(1)).thenReturn(Collections.emptyList());

        Optional<HealthStatisticsResponseDTO> result = healthStatisticsService.getHealthStatistics(1);

        assertTrue(result.isPresent());
        HealthStatisticsResponseDTO.CurrentHealthMetrics metrics = result.get().getCurrentMetrics();
        assertEquals(new BigDecimal("16.33"), metrics.getBmi());
        assertEquals(BMICategory.UNDERWEIGHT, metrics.getBmiCategory());
        assertEquals("Underweight", metrics.getBmiCategoryDisplay());
        assertTrue(metrics.getBmiInterpretation().contains("underweight"));
    }

    @Test
    void testGetHealthStatistics_AtypicalValidInput_ObeseUser() {
        // User with BMI >= 30 (obese)
        testUser.setWeight(new BigDecimal("100.0")); // BMI ~32.65
        
        when(userRepository.findUserById(1)).thenReturn(Optional.of(testUser));
        when(healthHistoryRepository.findByUserIdOrderByRecordedAtDesc(1)).thenReturn(Collections.emptyList());

        Optional<HealthStatisticsResponseDTO> result = healthStatisticsService.getHealthStatistics(1);

        assertTrue(result.isPresent());
        HealthStatisticsResponseDTO.CurrentHealthMetrics metrics = result.get().getCurrentMetrics();
        assertEquals(new BigDecimal("32.65"), metrics.getBmi());
        assertEquals(BMICategory.OBESE, metrics.getBmiCategory());
        assertEquals("Obese", metrics.getBmiCategoryDisplay());
    }

    @Test
    void testGetHealthStatistics_InvalidInput_UserDoesNotExist() {
        // User doesn't exist
        when(userRepository.findUserById(999)).thenReturn(Optional.empty());

        Optional<HealthStatisticsResponseDTO> result = healthStatisticsService.getHealthStatistics(999);

        // Should return empty
        assertFalse(result.isPresent());
        verify(userRepository, times(1)).findUserById(999);
        verify(healthHistoryRepository, never()).findByUserIdOrderByRecordedAtDesc(any());
    }

    @Test
    void testGetHealthStatistics_NoHistory_ReturnsEmptyHistoryList() {
        // User exists but has no health history
        when(userRepository.findUserById(1)).thenReturn(Optional.of(testUser));
        when(healthHistoryRepository.findByUserIdOrderByRecordedAtDesc(1)).thenReturn(Collections.emptyList());

        Optional<HealthStatisticsResponseDTO> result = healthStatisticsService.getHealthStatistics(1);

        // Should return current metrics but empty history
        assertTrue(result.isPresent());
        assertNotNull(result.get().getCurrentMetrics());
        assertTrue(result.get().getHistory().isEmpty());
    }

    @Test
    void testGetHealthStatistics_BoundaryValue_BMIExactly18Point5() {
        // User with BMI exactly 18.5 (boundary between underweight and normal)
        testUser.setHeight(new BigDecimal("175.0"));
        testUser.setWeight(new BigDecimal("56.66")); // BMI = 18.5
        
        when(userRepository.findUserById(1)).thenReturn(Optional.of(testUser));
        when(healthHistoryRepository.findByUserIdOrderByRecordedAtDesc(1)).thenReturn(Collections.emptyList());

        Optional<HealthStatisticsResponseDTO> result = healthStatisticsService.getHealthStatistics(1);

        // BMI of 18.5 should be NORMAL_WEIGHT (not underweight)
        assertTrue(result.isPresent());
        HealthStatisticsResponseDTO.CurrentHealthMetrics metrics = result.get().getCurrentMetrics();
        assertEquals(new BigDecimal("18.50"), metrics.getBmi());
        assertEquals(BMICategory.NORMAL_WEIGHT, metrics.getBmiCategory());
    }
}