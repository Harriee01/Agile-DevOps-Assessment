package com.teenread.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

/**
 * Centralised exception handler for all REST controllers.
 *
 * @RestControllerAdvice intercepts exceptions thrown from any @RestController
 * and converts them into structured JSON error responses with appropriate HTTP
 * status codes – keeping individual controller methods free of try/catch.
 *
 * Sprint 2 retro improvement:
 *   Externalising error mapping here makes it easy to test error paths
 *   independently from controller logic.
 */
@RestControllerAdvice

public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handles IllegalArgumentException – bad input from the client.
     *
     * Examples:
     *   - Null or blank search keyword
     *   - Null book ID passed to borrow endpoint
     *
     * HTTP 400 Bad Request.
     *
     * @param ex the caught exception
     * @return 400 response with { "error": "Bad Request", "message": "…" }
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleBadRequest(IllegalArgumentException ex) {
        // Log at WARN level – not a server error, but worth noting
        log.warn("Bad request: {}", ex.getMessage());
        return ResponseEntity.badRequest()
                .body(Map.of(
                        "error",   "Bad Request",
                        "message", ex.getMessage()
                ));
    }

    /**
     * Handles IllegalStateException – a valid request that cannot be fulfilled
     * given the current state of the data.
     *
     * Examples:
     *   - Book not found (should be 404 but kept 400 for simplicity in this sprint)
     *   - Book already borrowed
     *
     * HTTP 400 Bad Request.
     *
     * @param ex the caught exception
     * @return 400 response with { "error": "Bad Request", "message": "…" }
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, String>> handleIllegalState(IllegalStateException ex) {
        log.warn("Illegal state: {}", ex.getMessage());
        return ResponseEntity.badRequest()
                .body(Map.of(
                        "error",   "Bad Request",
                        "message", ex.getMessage()
                ));
    }

    /**
     * Catch-all handler for any unexpected exception.
     * Returns a generic 500 response so stack traces are never exposed to clients.
     *
     * @param ex the uncaught exception
     * @return 500 response with generic message
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneric(Exception ex) {
        // Log at ERROR level with full stack trace for investigation
        log.error("Unexpected error", ex);
        return ResponseEntity.internalServerError()
                .body(Map.of(
                        "error",   "Internal Server Error",
                        "message", "Something went wrong. Please try again."
                ));
    }
}
