package com.teenread.controller;

import com.teenread.repository.BookRepository;
import com.teenread.repository.BorrowedBookRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Basic health-check endpoint (Sprint 2 monitoring requirement).
 *
 * GET /health returns a JSON object with:
 *   - status:       always "UP" while the JVM is running
 *   - timestamp:    current server date-time (ISO-8601)
 *   - totalBooks:   total books in the catalogue
 *   - borrowedBooks: number of active borrow records
 *
 * No authentication required – this is a simple internal readiness probe.
 * In production this would be replaced by Spring Boot Actuator.
 */
@RestController
@RequestMapping("/health")

public class HealthController {

    /** Used to count total books in the catalogue */
    private final BookRepository bookRepository;

    /** Used to count active borrow records */
    private final BorrowedBookRepository borrowedBookRepository;

    /**
     * Constructor injection of both repositories for metric gathering.
     *
     * @param bookRepository         catalogue repository
     * @param borrowedBookRepository borrow-record repository
     */
    public HealthController(BookRepository bookRepository,
                            BorrowedBookRepository borrowedBookRepository) {
        this.bookRepository         = bookRepository;
        this.borrowedBookRepository = borrowedBookRepository;
    }

    // ---------------------------------------------------------------
    // Health check endpoint
    // ---------------------------------------------------------------

    /**
     * GET /health
     *
     * Returns a JSON health payload.
     * A 200 response means the application is running and repositories are accessible.
     *
     * Example response:
     * {
     *   "status":       "UP",
     *   "timestamp":    "2024-03-15T14:30:00",
     *   "totalBooks":   12,
     *   "borrowedBooks": 2
     * }
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> health() {
        // Collect metrics from both repositories
        int totalBooks   = bookRepository.findAll().size();
        int borrowedCount = borrowedBookRepository.count();

        // Build the response map
        Map<String, Object> payload = Map.of(
                "status",        "UP",
                "timestamp",     LocalDateTime.now().toString(),
                "totalBooks",    totalBooks,
                "borrowedBooks", borrowedCount
        );

        return ResponseEntity.ok(payload);
    }
}
