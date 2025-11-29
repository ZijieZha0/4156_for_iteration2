package com.example.nutriflow.user.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity class representing daily macro/micro nutrient targets for a user.
 */
@Entity
@Table(name = "user_targets", schema = "nutriflow")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserTarget {

    /**
     * The precision for calorie values.
     */
    private static final int CAL_PRECISION = 7;

    /**
     * The precision for nutrient values.
     */
    private static final int NUTRI_PRECISION = 6;

    /**
     * The scale for decimal values.
     */
    private static final int DECI_SCALE = 2;

    /**
     * The unique identifier for this target.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "target_id")
    private Integer targetId;

    /**
     * The user ID associated with this target.
     */
    @Column(name = "user_id", nullable = false)
    private Integer userId;

    /**
     * Daily calorie target.
     */
    @Column(name = "calories", precision = CAL_PRECISION, scale = DECI_SCALE)
    private BigDecimal calories;

    /**
     * Daily protein target in grams.
     */
    @Column(name = "protein", precision = NUTRI_PRECISION, scale = DECI_SCALE)
    private BigDecimal protein;

    /**
     * Daily fiber target in grams.
     */
    @Column(name = "fiber", precision = NUTRI_PRECISION, scale = DECI_SCALE)
    private BigDecimal fiber;

    /**
     * Daily fat target in grams.
     */
    @Column(name = "fat", precision = NUTRI_PRECISION, scale = DECI_SCALE)
    private BigDecimal fat;

    /**
     * Daily carbohydrate target in grams.
     */
    @Column(name = "carbs", precision = NUTRI_PRECISION, scale = DECI_SCALE)
    private BigDecimal carbs;

    /**
     * Daily iron target in milligrams.
     */
    @Column(name = "iron", precision = NUTRI_PRECISION, scale = DECI_SCALE)
    private BigDecimal iron;

    /**
     * Daily calcium target in milligrams.
     */
    @Column(name = "calcium", precision = NUTRI_PRECISION, scale = DECI_SCALE)
    private BigDecimal calcium;

    /**
     * Daily vitamin A target in micrograms.
     */
    @Column(name = "vitamin_a", precision = NUTRI_PRECISION, scale = DECI_SCALE)
    private BigDecimal vitaminA;

    /**
     * Daily vitamin C target in milligrams.
     */
    @Column(name = "vitamin_c", precision = NUTRI_PRECISION, scale = DECI_SCALE)
    private BigDecimal vitaminC;

    /**
     * Daily vitamin D target in micrograms.
     */
    @Column(name = "vitamin_d", precision = NUTRI_PRECISION, scale = DECI_SCALE)
    private BigDecimal vitaminD;

    /**
     * Daily sodium target in milligrams.
     */
    @Column(name = "sodium", precision = NUTRI_PRECISION, scale = DECI_SCALE)
    private BigDecimal sodium;

    /**
     * Daily potassium target in milligrams.
     */
    @Column(name = "potassium", precision = NUTRI_PRECISION, scale = DECI_SCALE)
    private BigDecimal potassium;

    /**
     * The timestamp when this target was created.
     */
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /**
     * The timestamp when this target was last updated.
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Set the creation timestamp before persisting the entity.
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    /**
     * Update the timestamp before updating the entity.
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
