package com.example.nutriflow.user.dto;

import com.example.nutriflow.shared.enums.CookingSkillLevel;
import com.example.nutriflow.shared.enums.SexType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Data Transfer Object for updating user information.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequestDTO {

    /**
     * User's height in centimeters.
     */
    private BigDecimal height;

    /**
     * User's weight in kilograms.
     */
    private BigDecimal weight;

    /**
     * User's age in years.
     */
    private Integer age;

    /**
     * User's biological sex.
     */
    private SexType sex;

    /**
     * List of user's food allergies.
     */
    private String[] allergies;

    /**
     * List of foods the user dislikes.
     */
    private String[] dislikes;

    /**
     * User's budget for meals.
     */
    private BigDecimal budget;

    /**
     * User's cooking skill level.
     */
    private CookingSkillLevel cookingSkill;

    /**
     * List of cooking equipment available to the user.
     */
    private String[] equipments;
}
