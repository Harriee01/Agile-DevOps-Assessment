package com.teenread.controller;

import com.teenread.model.Book;
import com.teenread.service.BookService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST controller exposing book-related API endpoints consumed by the frontend.
 *
 * Base path: /api/books
 *
 * Sprint 1 endpoints:
 *   GET /api/books              – US1: browse all books
 *   GET /api/books/search       – US2: search books by title keyword
 *
 * @RestController = @Controller + @ResponseBody (all methods return JSON automatically)
 * @RequestMapping sets the base URL prefix for all methods in this class
 * @CrossOrigin allows the frontend served on the same origin to call these endpoints
 */
@RestController
@RequestMapping("/api/books")
@CrossOrigin(origins = "*")

public class BookController {
    // Service bean injected via constructor injection
    private final BookService bookService;

    /**
     * Spring injects BookService automatically because there is exactly one
     * constructor and BookService is a registered @Service bean.
     *
     * @param bookService the book business-logic service
     */
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    // ---------------------------------------------------------------
    // US1 – Browse available books
    // ---------------------------------------------------------------

    /**
     * GET /api/books
     * <p>
     * Returns the full book catalogue as a JSON array.
     * Frontend calls this on page load to populate the book list.
     * <p>
     * Response 200 OK with body: [ { id, title, author, available }, … ]
     */
    @GetMapping
    public ResponseEntity<List<Book>> getAllBooks() {
        // Ask the service for all books
        List<Book> books = bookService.getAllBooks();

        // Wrap in 200 OK response; Spring Jackson serialises List<Book> to JSON array
        return ResponseEntity.ok(books);
    }

    // ---------------------------------------------------------------
    // US2 + US6 – Search and feedback
    // ---------------------------------------------------------------

    /**
     * GET /api/books/search?keyword=…
     * <p>
     * Response 200: { "results": [...], "message": "Found X book(s)…" }
     * Response 400: { "message": "Please enter a search term." }
     *
     * @param keyword partial title to match (query parameter, optional)
     */

    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchBooks(
            @RequestParam(required = false) String keyword) {

        // --- US6: input validation feedback ---
        // If no keyword provided or blank, return a 400 with a clear message
        if (keyword == null || keyword.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Please enter a search term."));
        }

        // Delegate search to the service layer
        List<Book> results = bookService.searchByTitle(keyword);

        // --- US6: result feedback message ---
        String message = results.isEmpty()
                ? "No results found for '" + keyword.trim() + "'. Try a different title."
                : "Found " + results.size() + " book(s) matching '" + keyword.trim() + "'.";

        return ResponseEntity.ok(Map.of("results", results, "message", message));
    }
}
