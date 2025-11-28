package com.example.nutriflow.user.service;

import com.example.nutriflow.user.model.UserTarget;
import com.example.nutriflow.user.dto.UpdateUserTargetRequestDTO;
import com.example.nutriflow.user.repository.UserTargetRepository;
import com.example.nutriflow.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service class for handling user target-related business logic.
 * Manages daily nutritional targets for users.
 */
@Service
public class UserTargetService {

    /**
     * Repository for user target data.
     */
    @Autowired
    private UserTargetRepository userTargetRepository;

    /**
     * Repository for user data.
     */
    @Autowired
    private UserRepository userRepository;

    /**
     * Get the most recent nutritional targets for a user.
     *
     * @param userId the ID of the user
     * @return Optional containing the user's latest targets if found,
     *         empty otherwise
     */
    public Optional<UserTarget> getUserTargets(final Integer userId) {
        // First verify the user exists
        if (!userRepository.existsById(userId)) {
            return Optional.empty();
        }

        return userTargetRepository.findLatestByUserId(userId);
    }

    /**
     * Update or create nutritional targets for a user.
     * If targets exist, they will be updated; otherwise,
     * new targets will be created.
     *
     * @param userId the ID of the user
     * @param request the request containing updated target information
     * @return Optional containing the updated/created targets if successful,
     *         empty if user not found
     */
    @Transactional
    public Optional<UserTarget> updateUserTargets(final Integer userId,
                                          final UpdateUserTargetRequestDTO
                                                  request) {
        // First verify the user exists
        if (!userRepository.existsById(userId)) {
            return Optional.empty();
        }

        // Try to find existing targets
        Optional<UserTarget> existingTargetOpt =
                userTargetRepository.findLatestByUserId(userId);

        UserTarget target;
        if (existingTargetOpt.isPresent()) {
            // Update existing targets
            target = existingTargetOpt.get();
            updateTargetFields(target, request);
        } else {
            // Create new targets
            target = new UserTarget();
            target.setUserId(userId);
            updateTargetFields(target, request);
        }

        return Optional.of(userTargetRepository.save(target));
    }

    /**
     * Helper method to update target fields from request.
     * Only updates fields that are not null in the request.
     *
     * @param target the target entity to update
     * @param request the request containing new values
     */
    private void updateTargetFields(final UserTarget target,
                                    final UpdateUserTargetRequestDTO request) {
        updateIfNotNull(request.getCalories(), target::setCalories);
        updateIfNotNull(request.getProtein(), target::setProtein);
        updateIfNotNull(request.getFiber(), target::setFiber);
        updateIfNotNull(request.getFat(), target::setFat);
        updateIfNotNull(request.getCarbs(), target::setCarbs);
        updateIfNotNull(request.getIron(), target::setIron);
        updateIfNotNull(request.getCalcium(), target::setCalcium);
        updateIfNotNull(request.getVitaminA(), target::setVitaminA);
        updateIfNotNull(request.getVitaminC(), target::setVitaminC);
        updateIfNotNull(request.getVitaminD(), target::setVitaminD);
        updateIfNotNull(request.getSodium(), target::setSodium);
        updateIfNotNull(request.getPotassium(), target::setPotassium);
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
