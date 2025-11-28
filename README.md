# NutriFlow
## Overview

NutriFlow is a personalized nutrition and recipe recommendation platform.
This milestone implements core backend modules for user data management, pantry tracking, and recipe retrieval, built with Spring Boot, Spring Data JPA, and PostgreSQL.

## How to Run
1. use Java 17
```shell
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
export PATH="$JAVA_HOME/bin:$PATH"
java -version
```  
(Ensure it prints a Java 17 version.)

2. Compile
```shell
mvn clean compile
```
3. Run the application
```
mvn spring-boot:run
```   
The service will start on `http://localhost:8080` by default.

## Testing Frameworks
This project uses the following testing and mocking frameworks:

- **JUnit 5 (Jupiter)**
- **Mockito 5.11.0**
- **Spring Boot Test**

## Project Management Software
https://trello.com/b/wPgSYaB3/coms-4156-project

## Implemented Features
### User Management

Purpose: Manage basic user information including personal details, dietary preferences, and cooking capabilities.

Modules:
- Entity: User
    - Fields: userId, name, height, weight, age, sex, allergies, dislikes, budget, cookingSkillLevel, equipments, timestamps
- Repository: UserRepository
    - `findUserById(Integer userId)`
    - `existsById(Integer userId)`
- Service: UserService
    - `getUserById(Integer userId)` → retrieve user information by ID
    - `updateUser(Integer userId, UpdateUserRequestDTO request)` → update user information (supports partial updates)
- Controller: UserController
    - `GET /api/users/{userId}` → retrieve user basic information
    - `PUT /api/users/{userId}` → update user information

### User Nutritional Targets

Purpose: Store and manage daily nutritional targets for users including macros and micronutrients.

Modules:
- Entity: UserTarget
    - Fields: targetId, userId, calories, protein, fiber, fat, carbs, iron, calcium, vitaminA, vitaminC, vitaminD, sodium, potassium, timestamps
- Repository: UserTargetRepository
    - `findLatestByUserId(Integer userId)`
- Service: UserTargetService
    - `getUserTargets(Integer userId)` → retrieve user's latest nutritional targets
    - `updateUserTargets(Integer userId, UpdateUserTargetRequestDTO request)` → update or create user nutritional targets
- Controller: UserController
    - `GET /api/users/{userId}/targets` → retrieve user nutritional targets
    - `PUT /api/users/{userId}/targets` → update user nutritional targets

### Health Statistics

Purpose: Track user health metrics over time and provide BMI calculations and historical data analysis.

Modules:
- Entity: UserHealthHistory
    - Fields: historyId, userId, weight, height, bmi (auto-calculated by database), recordedAt
- Repository: UserHealthHistoryRepository
    - `findByUserIdOrderByRecordedAtDesc(Integer userId)`
- Service: HealthStatisticsService
    - `getHealthStatistics(Integer userId)` → retrieve comprehensive health statistics
- Controller: UserController
    - `GET /api/users/{userId}/health_statistics` → retrieve user health statistics with BMI calculation and historical records

### Recipe Management

Purpose: Store and retrieve recipe data, including nutritional information and popularity ranking.

Modules:
- Entity: Recipe
    - Fields: title, cuisines, tags, ingredients (JSONB), nutrition (JSONB), macros, popularity score
- Repository: RecipeRepository
    - Custom query: findPopularRecipes(`Pageable pageable`)
- Service: RecipeService
    - `getRecipeById(Integer id)`
    - `getPopularRecipesDefault()` (top 5)
    - `getPopularRecipes(int limit)` (custom size)
    - `getUserFavoriteRecipes(Integer userId)`
- Controller: RecipeController
    - GET `/api/recipes/{id}` → retrieve recipe by ID
    - GET `/api/recipes/popular?limit={n}` → retrieve top N recipes
    - GET `/api/recipes/{userId}/favorites` → placeholder for favorite recipes

### Pantry Management

Purpose: Enable users to manage ingredients they currently have at home.

Modules:
- Entity: PantryItem
    - Fields: itemId, userId, name, quantity, unit, timestamps
    - Auto-managed timestamps via @PrePersist and @PreUpdate
- Repository: PantryRepository
    - `findByUserId(Integer userId)`
- Service: PantryService
    - `getPantryItems(Integer userId)`
    - `updatePantryItems(Integer userId, List<PantryItem> items)` replaces user’s pantry with the new list
- Controller: PantryController
    - `GET /api/users/{userId}/pantry` get all pantry items for user
    - `PUT /api/users/{userId}/pantry` replace all pantry items

### Favorites Management
Purpose: Allow users to mark recipes as favorites and track how often they use them.

Modules:

- Entity: FavoriteRecipe
    - Maps a user to recipes they’ve marked as favorites
    - Fields: favoriteId, userId, recipeId, timesUsed
- Repository: FavoriteRecipeRepository
- Methods:
    - `findByUserId(Integer userId)`
    - `findByUserIdAndRecipeId(Integer userId, Integer recipeId)`
- Service: FavoriteRecipeService
    - `addFavorite(Integer userId, Integer recipeId)` → Adds a recipe to favorites
    - `getFavoritesByUser(Integer userId)` → Returns user’s favorite recipes
    - `removeFavorite(Integer userId, Integer recipeId)` → Removes a recipe from favorites
- Controller: FavoriteRecipeController 
    - `POST /api/recipes/{userId}/favorites/{recipeId}` → Add a recipe to favorites
    - `GET /api/recipes/{userId}/favorites` → Retrieve user’s favorite recipes
    - `DELETE /api/recipes/{userId}/favorites/{recipeId}` → Remove a recipe from favorites

### AI Recipe Management
Purpose: Allow users to look for recipes with a specific ingredient and get AI-recommended recipes. 

- Service: AIRecipeService
    - `getAIRecipe(String ingredient)` - returns a recipe with the given ingredient.  
    - `searchIngredient(String ingredient)` - searches the repository to see whether a recipe with the given ingredient exists.
    - `getAIRecommendedRecipe()` - returns an AI recommended recipe. 
    - `requestRecipe(String prompt)` - sets up a structured output schema and makes an LLM query with the given prompt. 
    - `parseRecipe(String json)` - parses the given json object and creates a Recipe object. 
    - and a few minor helper functions.
- Controller: AIRecipeController 
    - `GET /api/ai/recipes/ingredient/{ingredient}` - retrieves a recipe with the given ingredient (pulls from the repository if a recipe with the given ingredient exists, otherwise asks an LLM to generate a recipe).
    - `GET /api/ai/recipes/recommendation` - returns a recipe recommended by an LLM. 

### Substitution Management

Purpose: Detect allergens in recipes and suggest alternative ingredients based on user dietary preferences.

Modules:
- Entity: SubstitutionRule
    - Fields: ingredient, avoid, substitute, note
- Repository: SubstitutionRuleRepository
    - `findByIngredientIgnoreCase(String ingredient)`
    - `findByIngredientAndAvoidIgnoreCase(String ingredient, String avoid)`
- Service: SubstitutionService
    - `checkRecipeForUser(Integer recipeId, Integer userId)` checks for allergen conflicts in a recipe and returns substitution suggestions
    - `findSubstitute(String ingredient, String avoid)` retrieves alternative ingredients based on avoidance type
- Controller: SubstitutionController
    - `POST /substitutions/check` checks if a recipe contains ingredients a user should avoid
    - `GET /substitutions?ingredient={name}&avoid={category}` retrieves substitution suggestions for a given ingredient


## Database & Data Seeding

Schema: nutriflow

Tables:
- users
- user_targets
- user_health_history
- recipes
- pantry_items
- favorite_recipes (future use)
- recipe_ingredients
- substitution_rules

## Test Coverage
```shell
mvn clean verify
open target/site/jacoco/index.html
```
coverage: 60% for iteration 1
![coverage](./resources/coverage_iteration_1.png)

## Checkstyle
```
mvn checkstyle:check
```
![checkstyle](./resources/style_check_iteration_1.png)