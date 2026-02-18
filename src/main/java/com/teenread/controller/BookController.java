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
     *
     * Returns the full book catalogue as a JSON array.
     * Frontend calls this on page load to populate the book list.
     *
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
    // US2 – Search books by title
    // ---------------------------------------------------------------

    /**
     * GET /api/books/search?keyword=…
     *
     * Accepts a query parameter 'keyword' and returns matching books.
     * Also returns a user-facing feedback message (US6 integration).
     *
     * Response 200 OK with body:
     *   { "results": [...], "message": "Found X book(s) matching '…'" }
     *
     * Response 400 Bad Request if keyword is missing or blank:
     *   { "message": "Please enter a search term." }
     *
     * @param keyword the search term provided by the user via query string
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
        String message;
        if (results.isEmpty()) {
            // US2 acceptance criteria: "No results found" message when applicable
            message = "No results found for '" + keyword.trim() + "'. Try a different title.";
        } else {
            // US6: success message tells the user how many results matched
            message = "Found " + results.size() + " book(s) matching '" + keyword.trim() + "'.";
        }

        // Build response map containing both the results list and the feedback message
        Map<String, Object> response = Map.of(
                "results", results,   // the matching books (may be empty)
                "message", message    // human-readable feedback (US6)
        );

        // Return 200 OK in all cases where the keyword was valid
        return ResponseEntity.ok(response);
    }
}
