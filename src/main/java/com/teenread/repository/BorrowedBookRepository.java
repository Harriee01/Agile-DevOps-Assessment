package com.teenread.repository;

import com.teenread.model.BorrowedBook;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * In-memory repository for borrow records (US3 / US4).
 *
 * Stores one BorrowedBook entry per borrow action.
 * Looked up by bookId to prevent duplicate borrows.
 */
@Repository

public class BorrowedBookRepository {
    /**
     * List of all active borrow records.
     * Starts empty; grows as users borrow books.
     */
    private final List<BorrowedBook> borrowedBooks = new ArrayList<>();

    /**
     * Saves a new borrow record.
     * Called by BorrowService immediately after marking a book unavailable.
     *
     * @param borrowedBook the new borrow record to persist
     */
    public void save(BorrowedBook borrowedBook) {
        borrowedBooks.add(borrowedBook);
    }

    /**
     * Returns all active borrow records.
     * Used by the "My Books" endpoint (US4).
     *
     * @return defensive copy of all borrow records
     */
    public List<BorrowedBook> findAll() {
        return new ArrayList<>(borrowedBooks);
    }

    /**
     * Finds a borrow record by the book id it references.
     * Used to check whether a book has already been borrowed (duplicate-borrow guard).
     *
     * @param bookId the book id to look up
     * @return Optional wrapping the found record, or empty if not borrowed
     */
    public Optional<BorrowedBook> findByBookId(Long bookId) {
        return borrowedBooks.stream()
                .filter(b -> b.getBookId().equals(bookId))
                .findFirst();
    }

    /**
     * Returns the count of all borrow records.
     * Used by the health endpoint and for assertions in tests.
     *
     * @return number of borrow records in the list
     */
    public int count() {
        return borrowedBooks.size();
    }
}
