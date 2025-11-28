package com.example.nutriflow.shared.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Welcome controller for root endpoint.
 */
@RestController
@RequestMapping("/")
public class WelcomeController {

    /**
     * Root endpoint that displays available API information.
     *
     * @return API information and available endpoints
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> welcome() {
        final Map<String, Object> response = new HashMap<>();
        response.put("application", "NutriFlow API");
        response.put("version", "2.0.0");
        response.put("status", "Running");
        response.put("message", "Welcome to NutriFlow - "
                + "Personalized Nutrition & Recipe Platform");

        final Map<String, String> endpoints = new HashMap<>();
        endpoints.put("Users", "/api/users");
        endpoints.put("Recipes", "/api/recipes");
        endpoints.put("AI Recipes", "/api/ai/recipes");
        endpoints.put("Meal Plans", "/api/meal-plans");
        endpoints.put("Ingredients", "/api/ingredients");
        endpoints.put("Pantry", "/api/users/{userId}/pantry");
        endpoints.put("Substitutions", "/substitutions");

        response.put("endpoints", endpoints);

        final Map<String, String> documentation = new HashMap<>();
        documentation.put("Postman Collections", "/postman");
        documentation.put("GitHub", "https://github.com/your-repo/nutriflow");

        response.put("documentation", documentation);

        return ResponseEntity.ok(response);
    }
}

