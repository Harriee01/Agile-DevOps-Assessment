package com.teenread.controller;

import com.teenread.model.BorrowedBook;
import com.teenread.service.BorrowService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST controller for borrowing operations (US3 / US4).
 *
 * Base path: /api/borrow
 *
 * Endpoints:
 *   POST /api/borrow/{bookId}   – borrow a book (US3)
 *   GET  /api/borrow/my-books   – list all borrowed books (US4)
 *
 * Error handling is centralised in GlobalExceptionHandler.
 * This controller stays thin – no try/catch blocks needed here.
 */
@RestController
@RequestMapping("/api/borrow")
@CrossOrigin(origins = "*")

public class BorrowController {

    private final BorrowService borrowService;

    /**
     * Constructor injection of BorrowService.
     *
     * @param borrowService the borrow business-logic service
     */
    public BorrowController(BorrowService borrowService) {
        this.borrowService = borrowService;
    }

    // ---------------------------------------------------------------
    // US3 – Borrow a book
    // ---------------------------------------------------------------

    /**
     * POST /api/borrow/{bookId}
     *
     * Borrows the book identified by {bookId}.
     * On success returns 200 with the borrow record and a US6 feedback message.
     *
     * Errors (book not found, already borrowed) are mapped to 400/404
     * by GlobalExceptionHandler so this method stays clean.
     *
     * @param bookId path variable identifying the book to borrow
     * @return 200 OK with BorrowedBook JSON + feedback message
     */
    @PostMapping("/{bookId}")
    public ResponseEntity<Map<String, Object>> borrowBook(@PathVariable Long bookId) {
        // Delegate to service – exceptions bubble up to GlobalExceptionHandler
        BorrowedBook record = borrowService.borrowBook(bookId);

        // US6: success message shown to the user
        String message = "You borrowed '" + record.getTitle() +
                "'. Due back by: " + record.getDueDate();

        return ResponseEntity.ok(Map.of(
                "borrowedBook", record,
                "message",      message
        ));
    }

    // ---------------------------------------------------------------
    // US4 – View borrowed books
    // ---------------------------------------------------------------

    /**
     * GET /api/borrow/my-books
     *
     * Returns the list of all borrow records for the "My Books" section.
     * Response: 200 OK [ { bookId, title, author, dueDate }, … ]
     */
    @GetMapping("/my-books")
    public ResponseEntity<List<BorrowedBook>> getMyBooks() {
        return ResponseEntity.ok(borrowService.getMyBooks());
    }
}
