package com.example.nutriflow.service;

import com.example.nutriflow.user.model.User;
import com.example.nutriflow.user.dto.UpdateUserRequestDTO;
import com.example.nutriflow.shared.enums.CookingSkillLevel;
import com.example.nutriflow.shared.enums.SexType;
import com.example.nutriflow.user.repository.UserRepository;
import com.example.nutriflow.user.service.UserService;
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
 * Unit tests for UserService
 * Tests user retrieval and update operations with various input scenarios
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;
    
    @BeforeEach
    void setUp() {
        // Setup a typical test user
        testUser = new User();
        testUser.setUserId(1);
        testUser.setName("John Doe");
        testUser.setHeight(new BigDecimal("175.0"));
        testUser.setWeight(new BigDecimal("70.0"));
        testUser.setAge(30);
        testUser.setSex(SexType.MALE);
        testUser.setAllergies(new String[]{"peanuts"});
        testUser.setDislikes(new String[]{"mushrooms"});
        testUser.setBudget(new BigDecimal("100.0"));
        testUser.setCookingSkillLevel(CookingSkillLevel.INTERMEDIATE);
        testUser.setEquipments(new String[]{"oven", "stove"});
    }

    @Test
    void testGetUserById_TypicalValidInput_ReturnsUser() {
        // Setup repository to return user when queried with valid ID
        when(userRepository.findUserById(1)).thenReturn(Optional.of(testUser));

        Optional<User> result = userService.getUserById(1);

        assertTrue(result.isPresent());
        assertEquals(1, result.get().getUserId());
        assertEquals("John Doe", result.get().getName());
        verify(userRepository, times(1)).findUserById(1);
    }

    @Test
    void testGetUserById_AtypicalValidInput_ReturnsUser() {
        // Test with maximum integer value as ID
        Integer maxId = Integer.MAX_VALUE;
        User userWithMaxId = new User();
        userWithMaxId.setUserId(maxId);
        userWithMaxId.setName("Edge Case User");
        
        when(userRepository.findUserById(maxId)).thenReturn(Optional.of(userWithMaxId));
        Optional<User> result = userService.getUserById(maxId);

        assertTrue(result.isPresent());
        assertEquals(maxId, result.get().getUserId());
        verify(userRepository, times(1)).findUserById(maxId);
    }

    @Test
    void testGetUserById_InvalidInput_ReturnsEmpty() {
        // Repository returns empty for non-existent user
        when(userRepository.findUserById(999)).thenReturn(Optional.empty());

        Optional<User> result = userService.getUserById(999);

        // Should return empty optional
        assertFalse(result.isPresent());
        verify(userRepository, times(1)).findUserById(999);
    }

    @Test
    void testUpdateUser_TypicalValidInput_UpdatesUserSuccessfully() {
        // Setup update request with some fields
        UpdateUserRequestDTO request = new UpdateUserRequestDTO();
        request.setHeight(new BigDecimal("180.0"));
        request.setWeight(new BigDecimal("75.0"));
        request.setAge(31);

        when(userRepository.findUserById(1)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        Optional<User> result = userService.updateUser(1, request);

        assertTrue(result.isPresent());
        assertEquals(new BigDecimal("180.0"), testUser.getHeight());
        assertEquals(new BigDecimal("75.0"), testUser.getWeight());
        assertEquals(31, testUser.getAge());
        verify(userRepository, times(1)).findUserById(1);
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void testUpdateUser_AtypicalValidInput_UpdatesAllFields() {
        // Update request with all possible fields
        UpdateUserRequestDTO request = new UpdateUserRequestDTO();
        request.setHeight(new BigDecimal("185.5"));
        request.setWeight(new BigDecimal("82.3"));
        request.setAge(35);
        request.setSex(SexType.FEMALE);
        request.setAllergies(new String[]{"shellfish", "nuts"});
        request.setDislikes(new String[]{"olives", "tomatoes"});
        request.setBudget(new BigDecimal("150.0"));
        request.setCookingSkill(CookingSkillLevel.ADVANCED);
        request.setEquipments(new String[]{"oven", "stove", "blender", "microwave"});

        when(userRepository.findUserById(1)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        Optional<User> result = userService.updateUser(1, request);

        // Verify all fields are updated
        assertTrue(result.isPresent());
        assertEquals(new BigDecimal("185.5"), testUser.getHeight());
        assertEquals(new BigDecimal("82.3"), testUser.getWeight());
        assertEquals(35, testUser.getAge());
        assertEquals(SexType.FEMALE, testUser.getSex());
        assertArrayEquals(new String[]{"shellfish", "nuts"}, testUser.getAllergies());
        assertEquals(CookingSkillLevel.ADVANCED, testUser.getCookingSkillLevel());
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void testUpdateUser_InvalidInput_ReturnsEmpty() {
        // Repository returns empty for non-existent user
        UpdateUserRequestDTO request = new UpdateUserRequestDTO();
        request.setHeight(new BigDecimal("180.0"));

        when(userRepository.findUserById(999)).thenReturn(Optional.empty());

        Optional<User> result = userService.updateUser(999, request);

        // Should return empty and not attempt to save
        assertFalse(result.isPresent());
        verify(userRepository, times(1)).findUserById(999);
        verify(userRepository, never()).save(any(User.class));
    }
}