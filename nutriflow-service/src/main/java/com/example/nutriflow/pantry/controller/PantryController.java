package com.example.nutriflow.pantry.controller;

import com.example.nutriflow.pantry.model.PantryItem;
import com.example.nutriflow.pantry.service.PantryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * REST controller for pantry operations.
 * Base path: /api/pantry
 */
@RestController
@RequestMapping("/api/pantry")
public class PantryController {

    /** Service layer handling pantry logic. */
    @Autowired
    private PantryService pantryService;

    /**
     * Retrieve all pantry items for a user.
     *
     * GET /api/pantry/user/{userId}
     *
     * @param userId the user ID
     * @return 200 OK with a list of pantry items; empty list if none
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PantryItem>>
        getPantry(final @PathVariable Integer userId) {
        return ResponseEntity.ok(pantryService.getPantryItems(userId));
    }

    /**
     * Add a single pantry item for a user.
     *
     * POST /api/pantry
     *
     * @param item the pantry item to add
     * @return 201 Created with the persisted pantry item
     */
    @PostMapping
    public ResponseEntity<PantryItem> addPantryItem(
            final @RequestBody PantryItem item) {
        final PantryItem saved = pantryService.addPantryItem(item);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    /**
     * Replace the user's pantry with the provided list of items.
     * Existing items for the user are removed and replaced by the request body.
     *
     * PUT /api/pantry/user/{userId}
     *
     * @param userId the user ID
     * @param items  the new pantry items to persist for the user
     * @return 200 OK with the persisted list of pantry items
     */
    @PutMapping("/user/{userId}")
    public ResponseEntity<List<PantryItem>> updatePantry(
            final @PathVariable Integer userId,
            final @RequestBody List<PantryItem> items) {

        return ResponseEntity.ok(
                pantryService.updatePantryItems(userId, items));
    }

    /**
     * Delete a pantry item by ID.
     *
     * DELETE /api/pantry/{itemId}
     *
     * @param itemId the pantry item ID
     * @return 200 OK with success message, or 404 if not found
     */
    @DeleteMapping("/{itemId}")
    public ResponseEntity<Map<String, String>> deletePantryItem(
            final @PathVariable Integer itemId) {
        final boolean deleted = pantryService.deletePantryItem(itemId);
        if (deleted) {
            return ResponseEntity.ok(Map.of(
                    "message", "Pantry item deleted successfully",
                    "itemId", itemId.toString()));
        }
        return ResponseEntity.notFound().build();
    }
}
