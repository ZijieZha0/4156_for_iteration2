package com.example.nutriflow.ingredient.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity representing nutritional information for an ingredient.
 * All values are per 100g of the ingredient.
 * This table allows users/programmers to update calorie and macro information.
 */
@Entity
@Table(name = "ingredient_nutrition", schema = "nutriflow")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IngredientNutrition {

    /** Unique identifier for the ingredient. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ingredient_id")
    private Integer ingredientId;

    /** Name of the ingredient (unique). */
    @Column(name = "ingredient_name", nullable = false, unique = true)
    private String ingredientName;

    /** Category (e.g., 'meat', 'vegetable', 'grain'). */
    @Column(name = "ingredient_category")
    private String ingredientCategory;

    // Macronutrients per 100g
    /** Calories per 100g. */
    @Column(name = "calories", nullable = false)
    private BigDecimal calories;

    /** Protein in grams per 100g. */
    @Column(name = "protein")
    private BigDecimal protein;

    /** Carbohydrates in grams per 100g. */
    @Column(name = "carbohydrates")
    private BigDecimal carbohydrates;

    /** Fat in grams per 100g. */
    @Column(name = "fat")
    private BigDecimal fat;

    /** Fiber in grams per 100g. */
    @Column(name = "fiber")
    private BigDecimal fiber;

    // Micronutrients per 100g (optional)
    /** Iron in mg per 100g. */
    @Column(name = "iron")
    private BigDecimal iron;

    /** Calcium in mg per 100g. */
    @Column(name = "calcium")
    private BigDecimal calcium;

    /** Vitamin A in IU per 100g. */
    @Column(name = "vitamin_a")
    private BigDecimal vitaminA;

    /** Vitamin C in mg per 100g. */
    @Column(name = "vitamin_c")
    private BigDecimal vitaminC;

    /** Vitamin D in IU per 100g. */
    @Column(name = "vitamin_d")
    private BigDecimal vitaminD;

    /** Sodium in mg per 100g. */
    @Column(name = "sodium")
    private BigDecimal sodium;

    /** Potassium in mg per 100g. */
    @Column(name = "potassium")
    private BigDecimal potassium;

    // Additional information
    /** Measurement unit (default: 'g'). */
    @Column(name = "unit")
    private String unit;

    /** Description of the ingredient. */
    @Column(name = "description")
    private String description;

    /** Source of nutrition data (e.g., 'USDA', 'user-defined'). */
    @Column(name = "source")
    private String source;

    /** Whether nutrition data is verified. */
    @Column(name = "is_verified")
    private Boolean isVerified;

    // Metadata
    /** Creation timestamp. */
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    /** Last update timestamp. */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /** User/system that created this entry. */
    @Column(name = "created_by")
    private String createdBy;

    /** Last user/system that updated this entry. */
    @Column(name = "updated_by")
    private String updatedBy;
}

