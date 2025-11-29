package com.example.nutriflow.user.service;

import com.example.nutriflow.user.model.User;
import com.example.nutriflow.user.dto.UpdateUserRequestDTO;
import com.example.nutriflow.user.repository.UserRepository;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service class for handling user-related business logic.
 */
@Service
public class UserService {

    /**
     * Repository for user data.
     */
    @Autowired
    private UserRepository userRepository;

    /**
     * Create a new user.
     * Note: Works correctly with stringtype=unspecified in datasource URL.
     *
     * @param user the user object to create
     * @return the created user with generated ID
     */
    @Transactional
    public User createUser(final User user) {
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    /**
     * Find a user by their user ID.
     *
     * @param userId the ID of the user to find
     * @return Optional containing the user if found, empty otherwise
     */
    public Optional<User> getUserById(final Integer userId) {
        return userRepository.findUserById(userId);
    }

    /**
     * Update an existing user with new information.
     *
     * @param userId the ID of the user to update
     * @param request the request containing updated information
     * @return Optional containing the updated user if successful,
     *         empty if user not found
     */
    @Transactional
    public Optional<User> updateUser(final Integer userId,
                                     final UpdateUserRequestDTO request) {
        return userRepository.findUserById(userId)
                .map(existingUser -> {
                    // Update fields if they are provided
                    updateIfNotNull(request.getHeight(),
                            existingUser::setHeight);
                    updateIfNotNull(request.getWeight(),
                            existingUser::setWeight);
                    updateIfNotNull(request.getAge(),
                            existingUser::setAge);
                    updateIfNotNull(request.getSex(),
                            existingUser::setSex);
                    updateIfNotNull(request.getAllergies(),
                            existingUser::setAllergies);
                    updateIfNotNull(request.getDislikes(),
                            existingUser::setDislikes);
                    updateIfNotNull(request.getBudget(),
                            existingUser::setBudget);
                    updateIfNotNull(request.getCookingSkill(),
                            existingUser::setCookingSkillLevel);
                    updateIfNotNull(request.getEquipments(),
                            existingUser::setEquipments);

                    return userRepository.save(existingUser);
                });
    }

    /**
     * Delete a user by their user ID.
     *
     * @param userId the ID of the user to delete
     * @return true if user was deleted, false if user not found
     */
    @Transactional
    public boolean deleteUser(final Integer userId) {
        return userRepository.findUserById(userId)
                .map(user -> {
                    userRepository.delete(user);
                    return true;
                })
                .orElse(false);
    }

    /**
     * Helper method to update a field only if the new value is not null.
     *
     * @param value the new value (can be null)
     * @param setter the setter method to call if value is not null
     * @param <T> the type of the value
     */
    private <T> void updateIfNotNull(final T value,
                                      final java.util.function.Consumer<T>
                                              setter) {
        Optional.ofNullable(value).ifPresent(setter);
    }
}
