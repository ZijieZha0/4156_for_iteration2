package com.example.nutriflow.substitution.controller;

import com.example.nutriflow.substitution.dto.SubstitutionCheckRequest;
import com.example.nutriflow.substitution.dto.SubstitutionCheckResponse;
import com.example.nutriflow.substitution.dto.SubstitutionSuggestionDto;
import com.example.nutriflow.substitution.service.SubstitutionService;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoints for allergen check and ingredient substitutions.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/substitutions")
public final class SubstitutionController {

    /** Business service for substitution logic. */
    private final SubstitutionService substitutionService;

    /**
     * Check a recipe against a user's allergies and return suggestions.
     *
     * <p>Returns 200 with payload when resources exist. Returns 404 when the
     * underlying resource (user or recipe) is missing.</p>
     *
     * @param req request body with recipeId and userId
     * @return check result with offenders and suggestions, or 404 if missing
     */
    @PostMapping("/check")
    public ResponseEntity<SubstitutionCheckResponse> check(
            @RequestBody final SubstitutionCheckRequest req) {
        try {
            final SubstitutionCheckResponse resp =
                substitutionService.checkRecipeForUser(
                    req.getRecipeId(), req.getUserId());
            return ResponseEntity.ok(resp);
        } catch (NoSuchElementException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Query substitutions for an ingredient, optionally avoiding a tag.
     *
     * @param ingredient ingredient to replace
     * @param avoid optional tag to avoid (e.g., nuts, gluten)
     * @return substitution suggestions (200, possibly empty)
     */
    @GetMapping
    public ResponseEntity<List<SubstitutionSuggestionDto>> query(
            @RequestParam final String ingredient,
            @RequestParam(required = false) final String avoid) {
        final List<SubstitutionSuggestionDto> out =
            substitutionService.findSubstitutions(
                ingredient, Optional.ofNullable(avoid));
        return ResponseEntity.ok(out);
    }
}
