package com.example.nutriflow.user.service;

import com.example.nutriflow.user.model.User;
import com.example.nutriflow.user.model.UserHealthHistory;
import com.example.nutriflow.user.dto.HealthStatisticsResponseDTO;
import com.example.nutriflow.shared.enums.BMICategory;
import com.example.nutriflow.user.repository.UserHealthHistoryRepository;
import com.example.nutriflow.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class for handling health statistics calculations.
 * Provides BMI calculations, health metrics, and historical data tracking.
 */
@Service
public class HealthStatisticsService {

    /**
     * Repository for user data.
     */
    @Autowired
    private UserRepository userRepository;

    /**
     * Repository for user health history records.
     */
    @Autowired
    private UserHealthHistoryRepository healthHistoryRepository;

    /**
     * Get comprehensive health statistics for a user.
     * Includes current BMI, health metrics, and historical data.
     *
     * @param userId the ID of the user
     * @return Optional containing health statistics if user found,
     *         empty otherwise
     */
    public Optional<HealthStatisticsResponseDTO> getHealthStatistics(
            final Integer userId) {
        Optional<User> userOpt = userRepository.findUserById(userId);

        if (userOpt.isEmpty()) {
            return Optional.empty();
        }

        User user = userOpt.get();

        // Get current health metrics from user profile
        HealthStatisticsResponseDTO.CurrentHealthMetrics currentMetrics =
            calculateCurrentMetrics(user);

        // Get historical health data
        List<UserHealthHistory> historyRecords =
            healthHistoryRepository.findByUserIdOrderByRecordedAtDesc(userId);

        List<HealthStatisticsResponseDTO.HealthHistoryEntry> historyEntries =
            historyRecords.stream()
                .map(this::mapToHistoryEntry)
                .collect(Collectors.toList());

        HealthStatisticsResponseDTO response =
                new HealthStatisticsResponseDTO();
        response.setCurrentMetrics(currentMetrics);
        response.setHistory(historyEntries);

        return Optional.of(response);
    }

    /**
     * Calculate current health metrics based on user's weight and height.
     *
     * @param user the user object
     * @return CurrentHealthMetrics object with calculated BMI and category
     */
    private HealthStatisticsResponseDTO.CurrentHealthMetrics
            calculateCurrentMetrics(final User user) {
        HealthStatisticsResponseDTO.CurrentHealthMetrics metrics =
            new HealthStatisticsResponseDTO.CurrentHealthMetrics();

        metrics.setWeight(user.getWeight());
        metrics.setHeight(user.getHeight());

        // Calculate BMI if both weight and height are available
        if (user.getWeight() != null && user.getHeight() != null
                && user.getHeight().compareTo(BigDecimal.ZERO) > 0) {

            BigDecimal bmi = calculateBMI(user.getWeight(),
                    user.getHeight());
            BMICategory category = BMICategory.fromBMI(bmi);

            metrics.setBmi(bmi);
            metrics.setBmiCategory(category);
            metrics.setBmiCategoryDisplay(category.getDisplayName());
            metrics.setBmiInterpretation(category.getInterpretation());
        } else {
            BMICategory unknownCategory = BMICategory.UNKNOWN;
            metrics.setBmi(null);
            metrics.setBmiCategory(unknownCategory);
            metrics.setBmiCategoryDisplay(unknownCategory.getDisplayName());
            metrics.setBmiInterpretation(
                    unknownCategory.getInterpretation());
        }

        return metrics;
    }

    /**
     * Calculate BMI (Body Mass Index).
     * Formula: BMI = weight(kg) / (height(cm) / 100)^2.
     *
     * @param weight weight in kilograms
     * @param height height in centimeters
     * @return calculated BMI rounded to 2 decimal places
     */
    private BigDecimal calculateBMI(final BigDecimal weight,
                                    final BigDecimal height) {
        final int scale = 4;
        final int hundred = 100;
        final int precision = 2;

        // Convert height from cm to meters
        BigDecimal heightInMeters = height.divide(
                new BigDecimal(String.valueOf(hundred)),
                scale,
                RoundingMode.HALF_UP);

        // BMI = weight / height^2
        BigDecimal heightSquared = heightInMeters.multiply(heightInMeters);
        BigDecimal bmi = weight.divide(heightSquared,
                precision,
                RoundingMode.HALF_UP);

        return bmi;
    }

    /**
     * Map UserHealthHistory entity to HealthHistoryEntry DTO.
     *
     * @param history the health history entity
     * @return HealthHistoryEntry DTO
     */
    private HealthStatisticsResponseDTO.HealthHistoryEntry
            mapToHistoryEntry(final UserHealthHistory history) {
        return new HealthStatisticsResponseDTO.HealthHistoryEntry(
            history.getHistoryId(),
            history.getWeight(),
            history.getHeight(),
            history.getBmi(),
            history.getRecordedAt()
        );
    }
}
