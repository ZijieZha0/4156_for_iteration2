# Postman API Test Collections

## Overview
This folder contains Postman collection JSON files for testing all Nutriflow API endpoints.

## Collections

### 1. Meal_Plan_API.postman_collection.json
**Endpoints:** 10 requests covering meal plan generation
- Generate daily meal plan
- Generate weekly meal plan
- Generate with filters (cuisine, tags, prep time)
- Request alternative meals
- Get user's meal plans (all, by date range, by status)
- Get specific meal plan
- Update meal plan status
- Delete meal plan

### 2. Ingredient_Nutrition_API.postman_collection.json
**Endpoints:** 11 requests covering ingredient nutrition management
- Get all ingredients
- Get by ID, name, category
- Search ingredients
- Calculate nutrition for specific amounts
- Create new ingredient
- Update ingredient (full and partial)
- Delete ingredient

### 3. User_API.postman_collection.json
**Endpoints:** 7 requests covering user management
- Create user
- Get user information
- Update user
- Set nutritional targets
- Get nutritional targets
- Get health statistics
- Delete user

### 4. Recipe_API.postman_collection.json
**Endpoints:** 7 requests covering recipe operations
- Get all recipes
- Get recipe by ID
- Search by ingredient
- Manage favorites
- AI recipe generation

### 5. Complete_Nutriflow_API.postman_collection.json
**All-in-one collection** with organized folders for all modules

## How to Import into Postman

### Method 1: Import Individual Collections
1. Open Postman
2. Click "Import" button (top left)
3. Click "Upload Files"
4. Select one or more `.postman_collection.json` files
5. Click "Import"

### Method 2: Import Complete Collection
1. Import `Complete_Nutriflow_API.postman_collection.json` for all endpoints
2. Use folders to organize testing by module

## Configuration

### Environment Variables
The collections use these variables (set in Postman):

- `baseUrl`: localhost:8080
- `userId`: 1

**To set variables:**
1. Click "Environments" (left sidebar)
2. Create "Nutriflow Local" environment
3. Add variables:
   - `baseUrl` = `localhost:8080`
   - `userId` = `1`
4. Select this environment before testing

## Testing Workflow

### 1. Start the Application
```bash
cd nutriflow-service
export JAVA_HOME=/Library/Java/JavaVirtualMachines/temurin-17.jdk/Contents/Home
mvn spring-boot:run
```

### 2. Test Ingredient Nutrition API
1. Import `Ingredient_Nutrition_API.postman_collection.json`
2. Run "Get All Ingredients" - verify 40+ ingredients loaded
3. Run "Calculate Nutrition" - verify calculations work
4. Run "Create Custom Ingredient" - test adding new data
5. Run "Update Nutrition Values" - test updating calories

### 3. Test Meal Plan API
1. Import `Meal_Plan_API.postman_collection.json`
2. Run "Generate Daily Meal Plan" - should return 3 meals
3. Run "Generate Weekly Meal Plan" - should return 7 days
4. Run "Get User's Meal Plans" - verify saved plans
5. Run "Request Alternative Meal" - test meal swapping

### 4. Test User API
1. Import `User_API.postman_collection.json`
2. Run "Create User" - create test user
3. Run "Set Nutritional Targets" - set user goals
4. Run "Get Health Statistics" - verify BMI calculations

## Example Responses

### Calculate Nutrition Response
```json
{
  "ingredient": "chicken breast",
  "amount_grams": 200.0,
  "calories": 330.00,
  "protein": 62.00,
  "carbohydrates": 0.00,
  "fat": 7.20,
  "fiber": 0.00
}
```

### Generate Meal Plan Response
```json
{
  "success": true,
  "message": "Meal plan generated successfully",
  "dailyPlans": [
    {
      "date": "2025-11-28",
      "meals": [
        {
          "mealType": "Breakfast",
          "recipe": {
            "recipeId": 42,
            "title": "Protein Pancakes",
            "calories": 520,
            "protein": 35
          }
        }
      ],
      "totalCalories": 1985,
      "totalProtein": 148
    }
  ],
  "totalRecipesUsed": 3,
  "calorieVariance": -0.75
}
```

## Quick Tests

### Test 1: Calculate Custom Meal
```
Step 1: Calculate 200g chicken → 330 cal
Step 2: Calculate 150g rice → 195 cal
Step 3: Calculate 100g broccoli → 34 cal
Total: 559 calories
```

### Test 2: Generate and Modify Plan
```
Step 1: Generate meal plan → Get plan ID
Step 2: Request alternative for lunch → New recipe
Step 3: Update status to "completed"
Step 4: Verify changes saved
```

## Tips

1. **Run in Order:** Some requests depend on previous ones (e.g., need planId from generation)
2. **Save Responses:** Use Postman's "Save Response" to compare results
3. **Use Variables:** Update `userId` variable to test different users
4. **Check Logs:** Application logs show all API calls in terminal

## Support

All collections are ready to import and test immediately. No additional configuration required beyond setting the environment variables.

**Happy Testing!**


