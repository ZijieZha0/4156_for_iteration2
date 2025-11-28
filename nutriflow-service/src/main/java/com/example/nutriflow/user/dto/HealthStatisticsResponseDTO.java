package com.example.nutriflow.user.dto;

import com.example.nutriflow.shared.enums.BMICategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Data Transfer Object for health statistics response.
 * Contains current health metrics and historical data
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HealthStatisticsResponseDTO {

    /**
     * Current health metrics.
     */
    private CurrentHealthMetrics currentMetrics;

    /**
     * Historical health data entries.
     */
    private List<HealthHistoryEntry> history;

     /**
     * Nested class representing current health metrics.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CurrentHealthMetrics {
        /**
         * Current weight in kilograms.
         */
        private BigDecimal weight;
        /**
         * Current height in centimeters.
         */
        private BigDecimal height;
        /**
         * Calculated BMI value.
         */
        private BigDecimal bmi;
        /**
         * BMI category enum value.
         */
        private BMICategory bmiCategory;
        /**
         * Display string for BMI category.
         */
        private String bmiCategoryDisplay;
        /**
         * Interpretation text for BMI.
         */
        private String bmiInterpretation;
    }

    /**
     * Nested class representing a single health history entry.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HealthHistoryEntry {
        /**
         * Unique identifier for this history record.
         */
        private Integer historyId;
        /**
         * Weight at the time of recording.
         */
        private BigDecimal weight;
        /**
         * Height at the time of recording.
         */
        private BigDecimal height;
        /**
         * BMI at the time of recording.
         */
        private BigDecimal bmi;
        /**
         * Timestamp when this record was created.
         */
        private LocalDateTime recordedAt;
    }
}
