package com.teenread.repository;

import com.teenread.model.BorrowedBook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for BorrowedBookRepository.
 *
 * Sprint 2 retro improvement:
 *   All repository operations tested with edge cases BEFORE service layer merged.
 */
class BorrowedBookRepositoryTest {

    private BorrowedBookRepository repo;

    @BeforeEach
    void setUp() {
        // Each test gets a clean, empty repository
        repo = new BorrowedBookRepository();
    }

    @Test
    void initialState_isEmpty() {
        assertEquals(0, repo.count());
        assertTrue(repo.findAll().isEmpty());
    }

    @Test
    void save_addsBorrowRecord() {
        BorrowedBook record = new BorrowedBook(1L, "Wonder", "R.J. Palacio",
                LocalDate.now().plusDays(14));
        repo.save(record);
        assertEquals(1, repo.count());
    }

    @Test
    void findAll_returnsAllSavedRecords() {
        repo.save(new BorrowedBook(1L, "Wonder", "R.J. Palacio", LocalDate.now().plusDays(14)));
        repo.save(new BorrowedBook(2L, "Holes",  "Louis Sachar", LocalDate.now().plusDays(14)));
        assertEquals(2, repo.findAll().size());
    }

    @Test
    void findAll_returnsDefensiveCopy() {
        repo.save(new BorrowedBook(1L, "Wonder", "R.J. Palacio", LocalDate.now().plusDays(14)));
        List<BorrowedBook> copy = repo.findAll();
        copy.clear();
        assertEquals(1, repo.count(), "Internal list must not be affected");
    }

    @Test
    void findByBookId_returnsRecordWhenExists() {
        repo.save(new BorrowedBook(5L, "Ender's Game", "Orson Scott Card",
                LocalDate.now().plusDays(14)));
        Optional<BorrowedBook> result = repo.findByBookId(5L);
        assertTrue(result.isPresent());
        assertEquals("Ender's Game", result.get().getTitle());
    }

    @Test
    void findByBookId_returnsEmptyWhenNotBorrowed() {
        Optional<BorrowedBook> result = repo.findByBookId(999L);
        assertFalse(result.isPresent());
    }

    @Test
    void count_incrementsWithEachSave() {
        assertEquals(0, repo.count());
        repo.save(new BorrowedBook(1L, "A", "B", LocalDate.now()));
        assertEquals(1, repo.count());
        repo.save(new BorrowedBook(2L, "C", "D", LocalDate.now()));
        assertEquals(2, repo.count());
    }
}