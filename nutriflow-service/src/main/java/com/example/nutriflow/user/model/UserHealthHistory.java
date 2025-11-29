package com.example.nutriflow.user.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity class representing user health history records.
 * Stores historical weight, height, and calculated BMI values over time.
 */
@Entity
@Table(name = "user_health_history", schema = "nutriflow")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserHealthHistory {

    /** Precision for health measurement fields. */
    private static final int HEALTH_PRECISION = 5;

    /** Scale for health measurement fields. */
    private static final int HEALTH_SCALE = 2;

    /**
     * Unique identifier for the health history record.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "history_id")
    private Integer historyId;

    /**
     * Foreign key reference to the user.
     */
    @Column(name = "user_id", nullable = false)
    private Integer userId;

    /**
     * User's weight in kilograms.
     */
    @Column(name = "weight", nullable = false,
            precision = HEALTH_PRECISION, scale = HEALTH_SCALE)
    private BigDecimal weight;

    /**
     * User's height in centimeters.
     */
    @Column(name = "height", nullable = false,
            precision = HEALTH_PRECISION, scale = HEALTH_SCALE)
    private BigDecimal height;

    /**
     * Body Mass Index.
     * BMI is auto-calculated by database as a generated column.
     */
    @Column(name = "bmi", precision = HEALTH_PRECISION, scale = HEALTH_SCALE,
            insertable = false, updatable = false)
    private BigDecimal bmi;

    /**
     * Timestamp when this health record was created.
     */
    @Column(name = "recorded_at", updatable = false)
    private LocalDateTime recordedAt;

    /**
     * Set the recorded timestamp before persisting the entity.
     */
    @PrePersist
    protected void onCreate() {
        recordedAt = LocalDateTime.now();
    }
}
