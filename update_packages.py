#!/usr/bin/env python3
"""
Updates package declarations and imports after code reorganization.
"""

import os
import re
from pathlib import Path

BASE_PATH = Path("/Users/rinh/Documents/GitHub/4156_for_iteration2/nutriflow-service/src")

# Mapping of old packages to new packages
PACKAGE_MAPPING = {
    # User module
    "com.example.nutriflow.model.User": "com.example.nutriflow.user.model.User",
    "com.example.nutriflow.model.UserTarget": "com.example.nutriflow.user.model.UserTarget",
    "com.example.nutriflow.model.UserHealthHistory": "com.example.nutriflow.user.model.UserHealthHistory",
    "com.example.nutriflow.model.dto.UpdateUserRequestDTO": "com.example.nutriflow.user.dto.UpdateUserRequestDTO",
    "com.example.nutriflow.model.dto.UpdateUserTargetRequestDTO": "com.example.nutriflow.user.dto.UpdateUserTargetRequestDTO",
    "com.example.nutriflow.model.dto.HealthStatisticsResponseDTO": "com.example.nutriflow.user.dto.HealthStatisticsResponseDTO",
    
    # Recipe module
    "com.example.nutriflow.model.Recipe": "com.example.nutriflow.recipe.model.Recipe",
    "com.example.nutriflow.model.RecipeIngredient": "com.example.nutriflow.recipe.model.RecipeIngredient",
    "com.example.nutriflow.model.FavoriteRecipe": "com.example.nutriflow.recipe.model.FavoriteRecipe",
    
    # Meal plan module (update paths)
    "com.example.nutriflow.model.mealplan": "com.example.nutriflow.mealplan.model",
    "com.example.nutriflow.mealplan.model.MealPlanRequestDto": "com.example.nutriflow.mealplan.dto.MealPlanRequestDto",
    "com.example.nutriflow.mealplan.model.MealPlanResponseDto": "com.example.nutriflow.mealplan.dto.MealPlanResponseDto",
    "com.example.nutriflow.mealplan.model.DailyMealPlanDetailDto": "com.example.nutriflow.mealplan.dto.DailyMealPlanDetailDto",
    "com.example.nutriflow.mealplan.model.MealPlanAlternativeRequestDto": "com.example.nutriflow.mealplan.dto.MealPlanAlternativeRequestDto",
    
    # Ingredient module
    "com.example.nutriflow.model.IngredientNutrition": "com.example.nutriflow.ingredient.model.IngredientNutrition",
    
    # Pantry module
    "com.example.nutriflow.model.PantryItem": "com.example.nutriflow.pantry.model.PantryItem",
    
    # Substitution module
    "com.example.nutriflow.model.SubstitutionRule": "com.example.nutriflow.substitution.model.SubstitutionRule",
    "com.example.nutriflow.model.dto.SubstitutionCheckRequest": "com.example.nutriflow.substitution.dto.SubstitutionCheckRequest",
    "com.example.nutriflow.model.dto.SubstitutionCheckResponse": "com.example.nutriflow.substitution.dto.SubstitutionCheckResponse",
    "com.example.nutriflow.model.dto.SubstitutionSuggestionDto": "com.example.nutriflow.substitution.dto.SubstitutionSuggestionDto",
    "com.example.nutriflow.model.dto.OffenderDto": "com.example.nutriflow.substitution.dto.OffenderDto",
    
    # Shared module
    "com.example.nutriflow.model.enums": "com.example.nutriflow.shared.enums",
    
    # Services
    "com.example.nutriflow.service.UserService": "com.example.nutriflow.user.service.UserService",
    "com.example.nutriflow.service.UserTargetService": "com.example.nutriflow.user.service.UserTargetService",
    "com.example.nutriflow.service.HealthStatisticsService": "com.example.nutriflow.user.service.HealthStatisticsService",
    "com.example.nutriflow.service.RecipeService": "com.example.nutriflow.recipe.service.RecipeService",
    "com.example.nutriflow.service.AIRecipeService": "com.example.nutriflow.recipe.service.AIRecipeService",
    "com.example.nutriflow.service.MealPlanService": "com.example.nutriflow.mealplan.service.MealPlanService",
    "com.example.nutriflow.service.IngredientNutritionService": "com.example.nutriflow.ingredient.service.IngredientNutritionService",
    "com.example.nutriflow.service.PantryService": "com.example.nutriflow.pantry.service.PantryService",
    "com.example.nutriflow.service.SubstitutionService": "com.example.nutriflow.substitution.service.SubstitutionService",
    
    # Repositories
    "com.example.nutriflow.service.repository": "com.example.nutriflow",
}

def update_file(file_path):
    """Update package declarations and imports in a Java file."""
    try:
        with open(file_path, 'r', encoding='utf-8') as f:
            content = f.read()
        
        original_content = content
        
        # Update package declarations and imports
        for old_pkg, new_pkg in PACKAGE_MAPPING.items():
            # Update package declaration
            content = content.replace(f'package {old_pkg}', f'package {new_pkg}')
            # Update imports
            content = content.replace(f'import {old_pkg}', f'import {new_pkg}')
        
        # If content changed, write it back
        if content != original_content:
            with open(file_path, 'w', encoding='utf-8') as f:
                f.write(content)
            return True
        return False
    except Exception as e:
        print(f"Error processing {file_path}: {e}")
        return False

def main():
    print("🔄 Updating package declarations and imports...")
    
    updated_files = []
    total_files = 0
    
    # Find all Java files
    for java_file in BASE_PATH.rglob("*.java"):
        total_files += 1
        if update_file(java_file):
            updated_files.append(str(java_file))
            print(f"✓ Updated: {java_file.name}")
    
    print(f"\n✅ Complete!")
    print(f"   Total files scanned: {total_files}")
    print(f"   Files updated: {len(updated_files)}")

if __name__ == "__main__":
    main()

