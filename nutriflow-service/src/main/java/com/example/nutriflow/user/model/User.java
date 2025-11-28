package com.example.nutriflow.user.model;

import com.example.nutriflow.shared.enums.SexType;
import com.example.nutriflow.shared.enums.CookingSkillLevel;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity class representing a user in the nutriflow system.
 */
@Entity
@Table(name = "users", schema = "nutriflow")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    /** Maximum length for user name. */
    private static final int NAME_MAX_LENGTH = 255;

    /** Precision for height and weight fields. */
    private static final int DIM_PRECISION = 5;

    /** Scale for height and weight fields. */
    private static final int DIM_SCALE = 2;

    /** Precision for budget field. */
    private static final int BUDGET_PREC = 10;

    /** Scale for budget field. */
    private static final int BUDGET_SCALE = 2;

    /** Unique identifier for the user. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer userId;

    /** Name of the user. */
    @Column(name = "name", nullable = false, length = NAME_MAX_LENGTH)
    private String name;

    /** Height of the user in centimeters. */
    @Column(name = "height", precision = DIM_PRECISION, scale = DIM_SCALE)
    private BigDecimal height;

    /** Weight of the user in kilograms. */
    @Column(name = "weight", precision = DIM_PRECISION, scale = DIM_SCALE)
    private BigDecimal weight;

    /** Age of the user. */
    @Column(name = "age")
    private Integer age;

    /** Biological sex of the user. */
    @Enumerated(EnumType.STRING)
    @Column(name = "sex", columnDefinition = "sex_type")
    private SexType sex;

    /** List of user's food allergies. */
    @Column(name = "allergies", columnDefinition = "text[]")
    private String[] allergies;

    /** List of foods the user dislikes. */
    @Column(name = "dislikes", columnDefinition = "text[]")
    private String[] dislikes;

    /** User's budget for meals. */
    @Column(name = "budget", precision = BUDGET_PREC, scale = BUDGET_SCALE)
    private BigDecimal budget;

    /** User's cooking skill level. */
    @Enumerated(EnumType.STRING)
    @Column(name = "cooking_skill_level",
            columnDefinition = "cooking_skill_level")
    private CookingSkillLevel cookingSkillLevel;

    /** List of cooking equipment available to the user. */
    @Column(name = "equipments", columnDefinition = "text[]")
    private String[] equipments;

    /** Timestamp when the user record was created. */
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /** Timestamp when the user record was last updated. */
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
