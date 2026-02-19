package com.teenread.repository;

import com.teenread.model.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for BookRepository.
 *
 * Sprint 2 retro improvement:
 *   New edge-case tests for findById and save added BEFORE BorrowService merged.
 *
 * No Spring context needed – BookRepository is a plain Java class.
 */
class BookRepositoryTest {

    // A fresh repository (with 12 seeded books) for every test
    private BookRepository repo;

    @BeforeEach
    void setUp() {
        repo = new BookRepository();
    }

    // ---------------------------------------------------------------
    // findAll
    // ---------------------------------------------------------------

    @Test
    void findAll_returns12SeededBooks() {
        assertEquals(12, repo.findAll().size());
    }

    @Test
    void findAll_returnsDefensiveCopy() {
        // Mutating the returned list must not affect the internal list
        List<Book> list = repo.findAll();
        list.clear();
        assertEquals(12, repo.findAll().size(), "Internal list should be unchanged");
    }

    // ---------------------------------------------------------------
    // searchByTitle (Sprint 1 – regression)
    // ---------------------------------------------------------------

    @Test
    void searchByTitle_findsPartialMatch() {
        List<Book> results = repo.searchByTitle("Hunger");
        assertEquals(1, results.size());
        assertEquals("The Hunger Games", results.get(0).getTitle());
    }

    @Test
    void searchByTitle_isCaseInsensitive() {
        List<Book> results = repo.searchByTitle("hunger games");
        assertEquals(1, results.size());
    }

    @Test
    void searchByTitle_returnsEmptyForNoMatch() {
        List<Book> results = repo.searchByTitle("zzz-no-match");
        assertTrue(results.isEmpty());
    }

    // ---------------------------------------------------------------
    // findById (Sprint 2)
    // ---------------------------------------------------------------

    @Test
    void findById_returnsBookWhenExists() {
        Optional<Book> result = repo.findById(1L);
        assertTrue(result.isPresent());
        assertEquals("The Hunger Games", result.get().getTitle());
    }

    @Test
    void findById_returnsEmptyForNonExistentId() {
        Optional<Book> result = repo.findById(999L);
        assertFalse(result.isPresent());
    }

    @Test
    void findById_returnsEmptyForNegativeId() {
        // Edge case: negative ids should never exist
        assertFalse(repo.findById(-1L).isPresent());
    }

    // ---------------------------------------------------------------
    // save (Sprint 2)
    // ---------------------------------------------------------------

    @Test
    void save_updatesExistingBook() {
        // Retrieve book 1, flip its availability, save it
        Book book = repo.findById(1L).orElseThrow();
        book.setAvailable(false);
        repo.save(book);

        // Re-fetch and verify the change persisted
        Book updated = repo.findById(1L).orElseThrow();
        assertFalse(updated.isAvailable(), "Book should now be unavailable");
    }

    @Test
    void save_doesNotIncreaseListSize() {
        // Saving an update should replace, not add
        Book book = repo.findById(2L).orElseThrow();
        book.setAvailable(false);
        repo.save(book);
        assertEquals(12, repo.findAll().size(), "List size should remain 12");
    }
}