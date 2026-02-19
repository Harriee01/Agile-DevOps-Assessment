package com.teenread.controller;

import com.teenread.model.Book;
import com.teenread.service.RecommendationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for book recommendations (US5).
 *
 * Base path: /api/recommendations
 *
 * Endpoints:
 *   GET /api/recommendations – return a short list of recommended books
 *
 * Returns 200 OK even when the list is empty (US5 AC: must not block core functionality).
 */
@RestController
@RequestMapping("/api/recommendations")
@CrossOrigin(origins = "*")

public class RecommendationController {

    private final RecommendationService recommendationService;

    /**
     * Constructor injection of RecommendationService.
     *
     * @param recommendationService recommendation logic service
     */
    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    // ---------------------------------------------------------------
    // US5 – Get book recommendations
    // ---------------------------------------------------------------

    /**
     * GET /api/recommendations
     *
     * Returns up to 3 recommended books.
     * Response: 200 OK [ { id, title, author, available }, … ]
     *
     * Always returns 200 – an empty array is valid (no books available to recommend).
     */
    @GetMapping
    public ResponseEntity<List<Book>> getRecommendations() {
        return ResponseEntity.ok(recommendationService.getRecommendations());
    }
}
