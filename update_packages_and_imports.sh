#!/bin/bash

# Script to update package declarations and imports after reorganization

BASE_DIR="/Users/rinh/Documents/GitHub/4156_for_iteration2/nutriflow-service/src"

echo "🔄 Updating package declarations and imports..."

# Function to update package and imports in files
update_packages() {
    local dir=$1
    local old_package=$2
    local new_package=$3
    
    find "$dir" -name "*.java" -type f | while read file; do
        # Update package declaration
        sed -i '' "s|package $old_package|package $new_package|g" "$file"
        # Update imports
        sed -i '' "s|import $old_package|import $new_package|g" "$file"
    done
}

# ==========================================
# Update package declarations in moved files
# ==========================================

echo "📦 Updating USER module packages..."
find "$BASE_DIR/main/java/com/example/nutriflow/user" -name "*.java" -exec sed -i '' \
    -e 's|package com\.example\.nutriflow\.model;|package com.example.nutriflow.user.model;|g' \
    -e 's|package com\.example\.nutriflow\.model\.dto;|package com.example.nutriflow.user.dto;|g' \
    -e 's|package com\.example\.nutriflow\.service;|package com.example.nutriflow.user.service;|g' \
    -e 's|package com\.example\.nutriflow\.service\.repository;|package com.example.nutriflow.user.repository;|g' \
    -e 's|package com\.example\.nutriflow\.controller;|package com.example.nutriflow.user.controller;|g' \
    {} \;

echo "📦 Updating RECIPE module packages..."
find "$BASE_DIR/main/java/com/example/nutriflow/recipe" -name "*.java" -exec sed -i '' \
    -e 's|package com\.example\.nutriflow\.model;|package com.example.nutriflow.recipe.model;|g' \
    -e 's|package com\.example\.nutriflow\.service;|package com.example.nutriflow.recipe.service;|g' \
    -e 's|package com\.example\.nutriflow\.service\.repository;|package com.example.nutriflow.recipe.repository;|g' \
    -e 's|package com\.example\.nutriflow\.controller;|package com.example.nutriflow.recipe.controller;|g' \
    {} \;

echo "📦 Updating MEALPLAN module packages..."
find "$BASE_DIR/main/java/com/example/nutriflow/mealplan" -name "*.java" -exec sed -i '' \
    -e 's|package com\.example\.nutriflow\.model\.mealplan;|package com.example.nutriflow.mealplan.model;|g' \
    -e 's|package com\.example\.nutriflow\.mealplan\.model;|package com.example.nutriflow.mealplan.dto;|g' \
    -e 's|package com\.example\.nutriflow\.service;|package com.example.nutriflow.mealplan.service;|g' \
    -e 's|package com\.example\.nutriflow\.service\.repository;|package com.example.nutriflow.mealplan.repository;|g' \
    -e 's|package com\.example\.nutriflow\.controller;|package com.example.nutriflow.mealplan.controller;|g' \
    {} \;

# Fix DTOs specifically
find "$BASE_DIR/main/java/com/example/nutriflow/mealplan/dto" -name "*.java" -exec sed -i '' \
    's|package com\.example\.nutriflow\.mealplan\.model;|package com.example.nutriflow.mealplan.dto;|g' \
    {} \;

echo "📦 Updating INGREDIENT module packages..."
find "$BASE_DIR/main/java/com/example/nutriflow/ingredient" -name "*.java" -exec sed -i '' \
    -e 's|package com\.example\.nutriflow\.model;|package com.example.nutriflow.ingredient.model;|g' \
    -e 's|package com\.example\.nutriflow\.service;|package com.example.nutriflow.ingredient.service;|g' \
    -e 's|package com\.example\.nutriflow\.service\.repository;|package com.example.nutriflow.ingredient.repository;|g' \
    -e 's|package com\.example\.nutriflow\.controller;|package com.example.nutriflow.ingredient.controller;|g' \
    {} \;

echo "📦 Updating PANTRY module packages..."
find "$BASE_DIR/main/java/com/example/nutriflow/pantry" -name "*.java" -exec sed -i '' \
    -e 's|package com\.example\.nutriflow\.model;|package com.example.nutriflow.pantry.model;|g' \
    -e 's|package com\.example\.nutriflow\.service;|package com.example.nutriflow.pantry.service;|g' \
    -e 's|package com\.example\.nutriflow\.service\.repository;|package com.example.nutriflow.pantry.repository;|g' \
    -e 's|package com\.example\.nutriflow\.controller;|package com.example.nutriflow.pantry.controller;|g' \
    {} \;

echo "📦 Updating SUBSTITUTION module packages..."
find "$BASE_DIR/main/java/com/example/nutriflow/substitution" -name "*.java" -exec sed -i '' \
    -e 's|package com\.example\.nutriflow\.model;|package com.example.nutriflow.substitution.model;|g' \
    -e 's|package com\.example\.nutriflow\.model\.dto;|package com.example.nutriflow.substitution.dto;|g' \
    -e 's|package com\.example\.nutriflow\.service;|package com.example.nutriflow.substitution.service;|g' \
    -e 's|package com\.example\.nutriflow\.service\.repository;|package com.example.nutriflow.substitution.repository;|g' \
    -e 's|package com\.example\.nutriflow\.controller;|package com.example.nutriflow.substitution.controller;|g' \
    {} \;

echo "📦 Updating SHARED module packages..."
find "$BASE_DIR/main/java/com/example/nutriflow/shared" -name "*.java" -exec sed -i '' \
    's|package com\.example\.nutriflow\.model\.enums;|package com.example.nutriflow.shared.enums;|g' \
    {} \;

# ==========================================
# Update imports in ALL files
# ==========================================

echo "🔗 Updating imports across all files..."

find "$BASE_DIR/main/java" -name "*.java" -exec sed -i '' \
    -e 's|import com\.example\.nutriflow\.model\.User;|import com.example.nutriflow.user.model.User;|g' \
    -e 's|import com\.example\.nutriflow\.model\.UserTarget;|import com.example.nutriflow.user.model.UserTarget;|g' \
    -e 's|import com\.example\.nutriflow\.model\.UserHealthHistory;|import com.example.nutriflow.user.model.UserHealthHistory;|g' \
    -e 's|import com\.example\.nutriflow\.model\.dto\.UpdateUserRequestDTO;|import com.example.nutriflow.user.dto.UpdateUserRequestDTO;|g' \
    -e 's|import com\.example\.nutriflow\.model\.dto\.UpdateUserTargetRequestDTO;|import com.example.nutriflow.user.dto.UpdateUserTargetRequestDTO;|g' \
    -e 's|import com\.example\.nutriflow\.model\.dto\.HealthStatisticsResponseDTO;|import com.example.nutriflow.user.dto.HealthStatisticsResponseDTO;|g' \
    -e 's|import com\.example\.nutriflow\.model\.Recipe;|import com.example.nutriflow.recipe.model.Recipe;|g' \
    -e 's|import com\.example\.nutriflow\.model\.RecipeIngredient;|import com.example.nutriflow.recipe.model.RecipeIngredient;|g' \
    -e 's|import com\.example\.nutriflow\.model\.FavoriteRecipe;|import com.example.nutriflow.recipe.model.FavoriteRecipe;|g' \
    -e 's|import com\.example\.nutriflow\.model\.mealplan\.|import com.example.nutriflow.mealplan.model.|g' \
    -e 's|import com\.example\.nutriflow\.mealplan\.model\.MealPlanRequestDto;|import com.example.nutriflow.mealplan.dto.MealPlanRequestDto;|g' \
    -e 's|import com\.example\.nutriflow\.mealplan\.model\.MealPlanResponseDto;|import com.example.nutriflow.mealplan.dto.MealPlanResponseDto;|g' \
    -e 's|import com\.example\.nutriflow\.mealplan\.model\.DailyMealPlanDetailDto;|import com.example.nutriflow.mealplan.dto.DailyMealPlanDetailDto;|g' \
    -e 's|import com\.example\.nutriflow\.mealplan\.model\.MealPlanAlternativeRequestDto;|import com.example.nutriflow.mealplan.dto.MealPlanAlternativeRequestDto;|g' \
    -e 's|import com\.example\.nutriflow\.model\.IngredientNutrition;|import com.example.nutriflow.ingredient.model.IngredientNutrition;|g' \
    -e 's|import com\.example\.nutriflow\.model\.PantryItem;|import com.example.nutriflow.pantry.model.PantryItem;|g' \
    -e 's|import com\.example\.nutriflow\.model\.SubstitutionRule;|import com.example.nutriflow.substitution.model.SubstitutionRule;|g' \
    -e 's|import com\.example\.nutriflow\.model\.dto\.SubstitutionCheckRequest;|import com.example.nutriflow.substitution.dto.SubstitutionCheckRequest;|g' \
    -e 's|import com\.example\.nutriflow\.model\.dto\.SubstitutionCheckResponse;|import com.example.nutriflow.substitution.dto.SubstitutionCheckResponse;|g' \
    -e 's|import com\.example\.nutriflow\.model\.dto\.SubstitutionSuggestionDto;|import com.example.nutriflow.substitution.dto.SubstitutionSuggestionDto;|g' \
    -e 's|import com\.example\.nutriflow\.model\.dto\.OffenderDto;|import com.example.nutriflow.substitution.dto.OffenderDto;|g' \
    -e 's|import com\.example\.nutriflow\.model\.enums\.|import com.example.nutriflow.shared.enums.|g' \
    -e 's|import com\.example\.nutriflow\.service\.UserService;|import com.example.nutriflow.user.service.UserService;|g' \
    -e 's|import com\.example\.nutriflow\.service\.UserTargetService;|import com.example.nutriflow.user.service.UserTargetService;|g' \
    -e 's|import com\.example\.nutriflow\.service\.HealthStatisticsService;|import com.example.nutriflow.user.service.HealthStatisticsService;|g' \
    -e 's|import com\.example\.nutriflow\.service\.RecipeService;|import com.example.nutriflow.recipe.service.RecipeService;|g' \
    -e 's|import com\.example\.nutriflow\.service\.AIRecipeService;|import com.example.nutriflow.recipe.service.AIRecipeService;|g' \
    -e 's|import com\.example\.nutriflow\.service\.MealPlanService;|import com.example.nutriflow.mealplan.service.MealPlanService;|g' \
    -e 's|import com\.example\.nutriflow\.service\.IngredientNutritionService;|import com.example.nutriflow.ingredient.service.IngredientNutritionService;|g' \
    -e 's|import com\.example\.nutriflow\.service\.PantryService;|import com.example.nutriflow.pantry.service.PantryService;|g' \
    -e 's|import com\.example\.nutriflow\.service\.SubstitutionService;|import com.example.nutriflow.substitution.service.SubstitutionService;|g' \
    -e 's|import com\.example\.nutriflow\.service\.repository\.|import com.example.nutriflow.|g' \
    {} \;

# Update test imports
echo "🧪 Updating test imports..."
find "$BASE_DIR/test/java" -name "*.java" -exec sed -i '' \
    -e 's|import com\.example\.nutriflow\.model\.User;|import com.example.nutriflow.user.model.User;|g' \
    -e 's|import com\.example\.nutriflow\.model\.UserTarget;|import com.example.nutriflow.user.model.UserTarget;|g' \
    -e 's|import com\.example\.nutriflow\.model\.Recipe;|import com.example.nutriflow.recipe.model.Recipe;|g' \
    -e 's|import com\.example\.nutriflow\.model\.mealplan\.|import com.example.nutriflow.mealplan.model.|g' \
    -e 's|import com\.example\.nutriflow\.mealplan\.model\.MealPlanRequestDto;|import com.example.nutriflow.mealplan.dto.MealPlanRequestDto;|g' \
    -e 's|import com\.example\.nutriflow\.mealplan\.model\.MealPlanResponseDto;|import com.example.nutriflow.mealplan.dto.MealPlanResponseDto;|g' \
    -e 's|import com\.example\.nutriflow\.mealplan\.model\.DailyMealPlanDetailDto;|import com.example.nutriflow.mealplan.dto.DailyMealPlanDetailDto;|g' \
    -e 's|import com\.example\.nutriflow\.mealplan\.model\.MealPlanAlternativeRequestDto;|import com.example.nutriflow.mealplan.dto.MealPlanAlternativeRequestDto;|g' \
    -e 's|import com\.example\.nutriflow\.service\.MealPlanService;|import com.example.nutriflow.mealplan.service.MealPlanService;|g' \
    -e 's|import com\.example\.nutriflow\.service\.RecipeService;|import com.example.nutriflow.recipe.service.RecipeService;|g' \
    -e 's|import com\.example\.nutriflow\.service\.UserService;|import com.example.nutriflow.user.service.UserService;|g' \
    {} \;

echo ""
echo "✅ Package declarations and imports updated!"
echo ""
echo "📋 Next steps:"
echo "1. Check for compilation errors: mvn clean compile"
echo "2. Run tests: mvn test"
echo "3. Fix any remaining import issues"

