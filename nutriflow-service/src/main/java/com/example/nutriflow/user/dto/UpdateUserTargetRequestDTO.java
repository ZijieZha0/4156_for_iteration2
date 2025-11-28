package com.example.nutriflow.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Data Transfer Object for updating user's daily nutritional targets.
 * All fields are optional to support partial updates.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserTargetRequestDTO {

    /**
     * Daily calorie target in kcal.
     */
    private BigDecimal calories;

    /**
     * Daily protein target in grams.
     */
    private BigDecimal protein;

    /**
     * Daily fiber target in grams.
     */
    private BigDecimal fiber;

    /**
     * Daily fat target in grams.
     */
    private BigDecimal fat;

    /**
     * Daily carbohydrates target in grams.
     */
    private BigDecimal carbs;

    /**
     * Daily iron target in milligrams.
     */
    private BigDecimal iron;

    /**
     * Daily calcium target in milligrams.
     */
    private BigDecimal calcium;

    /**
     * Daily vitamin A target in micrograms.
     */
    private BigDecimal vitaminA;

    /**
     * Daily vitamin C target in milligrams.
     */
    private BigDecimal vitaminC;

    /**
     * Daily vitamin D target in micrograms.
     */
    private BigDecimal vitaminD;

    /**
     * Daily sodium target in milligrams.
     */
    private BigDecimal sodium;

    /**
     * Daily potassium target in milligrams.
     */
    private BigDecimal potassium;
}
