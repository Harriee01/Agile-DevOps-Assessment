package com.teenread.service;

import com.teenread.model.Book;
import com.teenread.model.BorrowedBook;
import com.teenread.repository.BookRepository;
import com.teenread.repository.BorrowedBookRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Service layer for borrowing operations (US3 / US4).
 *
 * Responsibilities:
 *   - Validate that a book exists and is available before borrowing
 *   - Calculate the due date from today + configurable number of days
 *   - Mark the book unavailable in BookRepository
 *   - Persist a BorrowedBook record in BorrowedBookRepository
 *   - Log every borrow action to the console (Sprint 2 monitoring requirement)
 *
 * Sprint 2 retro improvement:
 *   Edge cases (book not found, already borrowed) are tested BEFORE merging.
 */
@Service

public class BorrowService {

    /**
     * SLF4J logger – outputs to console via Logback (Spring Boot default).
     * Satisfies the "console logging for borrow actions" monitoring requirement.
     */
    private static final Logger log = LoggerFactory.getLogger(BorrowService.class);

    /** Book catalogue repository */
    private final BookRepository bookRepository;

    /** Borrow records repository */
    private final BorrowedBookRepository borrowedBookRepository;

    /**
     * Number of days from today before a borrowed book is due.
     * Read from application.properties: app.borrow.due-days (default 14).
     */
    @Value("${app.borrow.due-days:14}")
    private int dueDays;

    /**
     * Constructor injection of both repositories.
     *
     * @param bookRepository         catalogue repository
     * @param borrowedBookRepository borrow-record repository
     */
    public BorrowService(BookRepository bookRepository,
                         BorrowedBookRepository borrowedBookRepository) {
        this.bookRepository         = bookRepository;
        this.borrowedBookRepository = borrowedBookRepository;
    }

    // ---------------------------------------------------------------
    // US3 – Borrow a book
    // ---------------------------------------------------------------

    /**
     * Attempts to borrow a book by its id.
     *
     * Business rules:
     *   1. Book must exist in the catalogue
     *   2. Book must currently be available (not already borrowed)
     *   3. Due date = today + dueDays
     *
     * Side effects on success:
     *   - Book.available set to false and saved
     *   - BorrowedBook record saved
     *   - INFO log line emitted
     *
     * @param bookId the id of the book to borrow
     * @return the created BorrowedBook record
     * @throws IllegalArgumentException if bookId is null
     * @throws IllegalStateException    if book not found or already borrowed
     */
    public BorrowedBook borrowBook(Long bookId) {
        // Guard: null id is a programming error
        if (bookId == null) {
            throw new IllegalArgumentException("Book ID must not be null");
        }

        // Rule 1: book must exist
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalStateException(
                        "Book not found with id: " + bookId));

        // Rule 2: book must be available
        if (!book.isAvailable()) {
            log.warn("Borrow attempt on already-borrowed book [id={}] title='{}'",
                    bookId, book.getTitle());
            throw new IllegalStateException(
                    "Book '" + book.getTitle() + "' is already borrowed.");
        }

        // Rule 3: calculate due date
        LocalDate dueDate = LocalDate.now().plusDays(dueDays);

        // Mark the book as unavailable and persist the change
        book.setAvailable(false);
        bookRepository.save(book);

        // Create and persist the borrow record
        BorrowedBook record = new BorrowedBook(
                book.getId(), book.getTitle(), book.getAuthor(), dueDate);
        borrowedBookRepository.save(record);

        // Monitoring: log the successful borrow action to the console
        log.info("BORROW ACTION – bookId={} title='{}' dueDate={}",
                bookId, book.getTitle(), dueDate);

        return record;
    }

    // ---------------------------------------------------------------
    // US4 – View borrowed books
    // ---------------------------------------------------------------

    /**
     * Returns all active borrow records.
     * Used by the "My Books" section to list what the user has borrowed.
     *
     * @return list of all BorrowedBook records (may be empty)
     */
    public List<BorrowedBook> getMyBooks() {
        List<BorrowedBook> myBooks = borrowedBookRepository.findAll();

        // Monitoring: log how many borrowed books were retrieved
        log.debug("MY BOOKS query – {} record(s) returned", myBooks.size());

        return myBooks;
    }

}
