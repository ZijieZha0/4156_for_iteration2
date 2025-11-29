package com.example.nutriflow.shared.enums;

/**
 * Enum representing BMI (Body Mass Index) categories based on WHO
 * classification.
 */
public enum BMICategory {

    /**
     * BMI category for underweight individuals.
     */
    UNDERWEIGHT(
        "Underweight",
        "Your BMI indicates you are underweight. Consider consulting "
            + "with a healthcare provider about a healthy weight gain plan."
    ),

    /**
     * BMI category for individuals with normal weight.
     */
    NORMAL_WEIGHT(
        "Normal weight",
        "Your BMI is in the healthy range. Maintain your current weight "
            + "through a balanced diet and regular physical activity."
    ),

    /**
     * BMI category for overweight individuals.
     */
    OVERWEIGHT(
        "Overweight",
        "Your BMI indicates you are overweight. Consider adopting "
            + "healthier eating habits and increasing physical activity."
    ),

    /**
     * BMI category for obese individuals.
     */
    OBESE(
        "Obese",
        "Your BMI indicates obesity. It's recommended to consult with "
            + "a healthcare provider for a personalized health plan."
    ),

    /**
     * BMI category when calculation is not possible.
     */
    UNKNOWN(
        "Unknown",
        "Insufficient data to calculate BMI"
    );

    /**
     * Display name for the BMI category.
     */
    private final String displayName;

    /**
     * Interpretation text for the BMI category.
     */
    private final String interpretation;

    /**
     * Constructor for BMI category.
     *
     * @param name the display name of the category
     * @param desc the interpretation text for the category
     */
    BMICategory(final String name, final String desc) {
        this.displayName = name;
        this.interpretation = desc;
    }

    /**
     * Gets the display name of the BMI category.
     *
     * @return the display name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Gets the interpretation text of the BMI category.
     *
     * @return the interpretation text
     */
    public String getInterpretation() {
        return interpretation;
    }

    /**
     * Determine BMI category based on BMI value using WHO classification.
     *
     * @param bmi the calculated BMI value
     * @return the corresponding BMI category
     */
    public static BMICategory fromBMI(final java.math.BigDecimal bmi) {
        if (bmi == null) {
            return UNKNOWN;
        }

        if (bmi.compareTo(new java.math.BigDecimal("18.5")) < 0) {
            return UNDERWEIGHT;
        } else if (bmi.compareTo(new java.math.BigDecimal("25.0")) < 0) {
            return NORMAL_WEIGHT;
        } else if (bmi.compareTo(new java.math.BigDecimal("30.0")) < 0) {
            return OVERWEIGHT;
        } else {
            return OBESE;
        }
    }
}
