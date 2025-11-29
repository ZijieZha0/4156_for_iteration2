package com.example.nutriflow.pantry.service;

import com.example.nutriflow.pantry.model.PantryItem;
import com.example.nutriflow.pantry.repository.PantryRepository;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service for user pantry operations.
 * Provides read and replace-all behaviors for a user's pantry.
 */
@Service
public class PantryService {

    /** Repository for accessing pantry item persistence. */
    @Autowired
    private PantryRepository pantryRepository;

    /**
     * Retrieves all pantry items for the given user.
     *
     * @param userId the user ID
     * @return list of pantry items owned by the user (may be empty)
     */
    public List<PantryItem> getPantryItems(final Integer userId) {
        return pantryRepository.findByUserId(userId);
    }

    /**
     * Add a single pantry item.
     *
     * @param item the pantry item to add
     * @return the saved pantry item
     */
    @Transactional
    public PantryItem addPantryItem(final PantryItem item) {
        item.setCreatedAt(LocalDateTime.now());
        return pantryRepository.save(item);
    }

    /**
     * Replaces the user's pantry with the provided items.
     * Existing items for the user are deleted and replaced
     * with the given list; each item is bound to the userId.
     *
     * @param userId the user ID
     * @param items  the new set of pantry items to persist for the user
     * @return the persisted list of pantry items
     */
    @Transactional
    public List<PantryItem> updatePantryItems(
            final Integer userId,
            final List<PantryItem> items) {

        items.forEach(item -> item.setUserId(userId));
        pantryRepository.deleteAll(
                pantryRepository.findByUserId(userId));
        return pantryRepository.saveAll(items);
    }

    /**
     * Delete a pantry item by its ID.
     *
     * @param itemId the pantry item ID
     * @return true if deleted, false if not found
     */
    @Transactional
    public boolean deletePantryItem(final Integer itemId) {
        if (pantryRepository.existsById(itemId)) {
            pantryRepository.deleteById(itemId);
            return true;
        }
        return false;
    }
}
