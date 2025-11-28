package com.example.nutriflow.pantry.controller;

import com.example.nutriflow.pantry.model.PantryItem;
import com.example.nutriflow.pantry.service.PantryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for user pantry operations.
 * Base path: /api/users/{userId}/pantry
 */
@RestController
@RequestMapping("/api/users/{userId}/pantry")
public class PantryController {

    /** Service layer handling pantry logic. */
    @Autowired
    private PantryService pantryService;

    /**
     * Retrieve all pantry items for a user.
     *
     * @param userId the user ID
     * @return 200 OK with a list of pantry items; empty list if none
     */
    @GetMapping
    public ResponseEntity<List<PantryItem>>
        getPantry(final @PathVariable Integer userId) {
        return ResponseEntity.ok(pantryService.getPantryItems(userId));
    }

    /**
     * Replace the user's pantry with the provided list of items.
     * Existing items for the user are removed and replaced by the request body.
     *
     * @param userId the user ID
     * @param items  the new pantry items to persist for the user
     * @return 200 OK with the persisted list of pantry items
     */
    @PutMapping
    public ResponseEntity<List<PantryItem>> updatePantry(
            final @PathVariable Integer userId,
            final @RequestBody List<PantryItem> items) {

        return ResponseEntity.ok(
                pantryService.updatePantryItems(userId, items));
    }
}
