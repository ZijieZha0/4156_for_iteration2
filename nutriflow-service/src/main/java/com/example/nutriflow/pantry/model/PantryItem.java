package com.example.nutriflow.pantry.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity representing an item stored in a user's pantry.
 * Each record corresponds to a single ingredient or product
 * owned by a user, along with its quantity, unit, and timestamps.
 */
@Entity
@Table(name = "pantry_items", schema = "nutriflow")
@Data
@NoArgsConstructor
@AllArgsConstructor
public final class PantryItem {

    /** Unique identifier for the pantry item. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Integer itemId;

    /** Identifier of the user who owns this pantry item. */
    @Column(name = "user_id", nullable = false)
    private Integer userId;

    /** Name of the pantry item (e.g., "Rice", "Olive Oil"). */
    @Column(name = "name", nullable = false)
    private String ingredientName;

    /** Quantity of the pantry item available. */
    @Column(name = "quantity", precision =
        PantryItemConstants.QUANTITY_PRECISION,
            scale = PantryItemConstants.QUANTITY_SCALE)
    private BigDecimal quantity;

    /** Unit of measurement for the quantity (e.g., "kg", "ml"). */
    @Column(name = "unit")
    private String unit;

    /** Timestamp marking when the record was first created. */
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /** Timestamp marking the last time the record was updated. */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Lifecycle callback executed before the entity is persisted.
     * Initializes creation and update timestamps.
     * This method is final to prevent unsafe overriding.
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    /**
     * Lifecycle callback executed before the entity is updated.
     * Refreshes the update timestamp.
     * This method is final to prevent unsafe overriding.
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Constants used for pantry item entity configuration.
     */
    public static final class PantryItemConstants {
        /** Precision for the quantity column. */
        public static final int QUANTITY_PRECISION = 10;

        /** Scale for the quantity column. */
        public static final int QUANTITY_SCALE = 2;

        private PantryItemConstants() {
            // Prevent instantiation
        }
    }
}
