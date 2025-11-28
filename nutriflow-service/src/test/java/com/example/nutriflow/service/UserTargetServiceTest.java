package com.example.nutriflow.service;

import com.example.nutriflow.user.model.UserTarget;
import com.example.nutriflow.user.dto.UpdateUserTargetRequestDTO;
import com.example.nutriflow.user.repository.UserRepository;
import com.example.nutriflow.user.repository.UserTargetRepository;
import com.example.nutriflow.user.service.UserTargetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for UserTargetService
 * Tests user target retrieval and update operations
 */
@ExtendWith(MockitoExtension.class)
class UserTargetServiceTest {

    @Mock
    private UserTargetRepository userTargetRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserTargetService userTargetService;

    private UserTarget testTarget;

    @BeforeEach
    void setUp() {
        // Setup typical test target
        testTarget = new UserTarget();
        testTarget.setTargetId(1);
        testTarget.setUserId(1);
        testTarget.setCalories(new BigDecimal("2000.0"));
        testTarget.setProtein(new BigDecimal("150.0"));
        testTarget.setFiber(new BigDecimal("25.0"));
        testTarget.setFat(new BigDecimal("70.0"));
        testTarget.setCarbs(new BigDecimal("250.0"));
    }

    @Test
    void testGetUserTargets_TypicalValidInput_ReturnsTargets() {
        when(userRepository.existsById(1)).thenReturn(true);
        when(userTargetRepository.findLatestByUserId(1)).thenReturn(Optional.of(testTarget));

        Optional<UserTarget> result = userTargetService.getUserTargets(1);

        assertTrue(result.isPresent());
        assertEquals(1, result.get().getUserId());
        assertEquals(new BigDecimal("2000.0"), result.get().getCalories());
        verify(userRepository, times(1)).existsById(1);
        verify(userTargetRepository, times(1)).findLatestByUserId(1);
    }

    @Test
    void testGetUserTargets_AtypicalValidInput_UserExistsNoTargets() {
        // User exists but has no targets yet
        when(userRepository.existsById(1)).thenReturn(true);
        when(userTargetRepository.findLatestByUserId(1)).thenReturn(Optional.empty());

        Optional<UserTarget> result = userTargetService.getUserTargets(1);

        // Should return empty but still query the repository
        assertFalse(result.isPresent());
        verify(userRepository, times(1)).existsById(1);
        verify(userTargetRepository, times(1)).findLatestByUserId(1);
    }

    @Test
    void testGetUserTargets_InvalidInput_UserDoesNotExist() {
        // User doesn't exist
        when(userRepository.existsById(999)).thenReturn(false);

        Optional<UserTarget> result = userTargetService.getUserTargets(999);

        // Should return empty without querying target repository
        assertFalse(result.isPresent());
        verify(userRepository, times(1)).existsById(999);
        verify(userTargetRepository, never()).findLatestByUserId(any());
    }

    @Test
    void testUpdateUserTargets_TypicalValidInput_UpdatesExistingTargets() {
        // Update some fields of existing target
        UpdateUserTargetRequestDTO request = new UpdateUserTargetRequestDTO();
        request.setCalories(new BigDecimal("2200.0"));
        request.setProtein(new BigDecimal("160.0"));

        when(userRepository.existsById(1)).thenReturn(true);
        when(userTargetRepository.findLatestByUserId(1)).thenReturn(Optional.of(testTarget));
        when(userTargetRepository.save(any(UserTarget.class))).thenReturn(testTarget);

        Optional<UserTarget> result = userTargetService.updateUserTargets(1, request);

        // Verify fields are updated
        assertTrue(result.isPresent());
        assertEquals(new BigDecimal("2200.0"), testTarget.getCalories());
        assertEquals(new BigDecimal("160.0"), testTarget.getProtein());
        // Other fields should remain unchanged
        assertEquals(new BigDecimal("25.0"), testTarget.getFiber());
        verify(userTargetRepository, times(1)).save(testTarget);
    }

    @Test
    void testUpdateUserTargets_AtypicalValidInput_CreatesNewTargets() {
        UpdateUserTargetRequestDTO request = new UpdateUserTargetRequestDTO();
        request.setCalories(new BigDecimal("1800.0"));
        request.setProtein(new BigDecimal("120.0"));
        request.setFat(new BigDecimal("60.0"));
        request.setCarbs(new BigDecimal("200.0"));
        request.setFiber(new BigDecimal("30.0"));
        request.setIron(new BigDecimal("18.0"));
        request.setCalcium(new BigDecimal("1000.0"));
        request.setVitaminA(new BigDecimal("900.0"));
        request.setVitaminC(new BigDecimal("90.0"));
        request.setVitaminD(new BigDecimal("20.0"));
        request.setSodium(new BigDecimal("2300.0"));
        request.setPotassium(new BigDecimal("3500.0"));

        when(userRepository.existsById(1)).thenReturn(true);
        when(userTargetRepository.findLatestByUserId(1)).thenReturn(Optional.empty());
        when(userTargetRepository.save(any(UserTarget.class))).thenAnswer(invocation -> {
            UserTarget saved = invocation.getArgument(0);
            saved.setTargetId(1);
            return saved;
        });

        Optional<UserTarget> result = userTargetService.updateUserTargets(1, request);

        // New target created with all fields
        assertTrue(result.isPresent());
        assertEquals(1, result.get().getUserId());
        assertEquals(new BigDecimal("1800.0"), result.get().getCalories());
        assertEquals(new BigDecimal("120.0"), result.get().getProtein());
        assertEquals(new BigDecimal("18.0"), result.get().getIron());
        verify(userTargetRepository, times(1)).save(any(UserTarget.class));
    }

    @Test
    void testUpdateUserTargets_InvalidInput_UserDoesNotExist() {
        // User doesn't exist
        UpdateUserTargetRequestDTO request = new UpdateUserTargetRequestDTO();
        request.setCalories(new BigDecimal("2000.0"));

        when(userRepository.existsById(999)).thenReturn(false);
        
        Optional<UserTarget> result = userTargetService.updateUserTargets(999, request);

        // Should return empty without saving
        assertFalse(result.isPresent());
        verify(userRepository, times(1)).existsById(999);
        verify(userTargetRepository, never()).save(any(UserTarget.class));
    }
}