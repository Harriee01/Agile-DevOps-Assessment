package com.teenread.service;

import com.teenread.model.Book;
import com.teenread.model.BorrowedBook;
import com.teenread.repository.BookRepository;
import com.teenread.repository.BorrowedBookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for BorrowService (US3 / US4).
 *
 * Sprint 2 retro improvement:
 *   All edge cases (null id, not found, already borrowed) written and
 *   verified BEFORE BorrowController or frontend code is merged.
 *
 * @ExtendWith(MockitoExtension.class) enables Mockito annotations in JUnit 5.
 */
@ExtendWith(MockitoExtension.class)
class BorrowServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BorrowedBookRepository borrowedBookRepository;

    @InjectMocks
    private BorrowService borrowService;

    @BeforeEach
    void setUp() {
        // Inject the @Value field manually since there's no Spring context
        ReflectionTestUtils.setField(borrowService, "dueDays", 14);
    }

    // ---------------------------------------------------------------
    // borrowBook – happy path (US3)
    // ---------------------------------------------------------------

    @Test
    void borrowBook_success_returnsBorrowedBook() {
        Book book = new Book(1L, "The Hunger Games", "Suzanne Collins", true);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        BorrowedBook result = borrowService.borrowBook(1L);

        // Returned record has correct data
        assertEquals(1L,                "The Hunger Games".equals(result.getTitle()) ? 1L : -1L,
                "Title mismatch");
        assertEquals("The Hunger Games", result.getTitle());
        assertEquals("Suzanne Collins",  result.getAuthor());
        // Due date must be today + 14 days
        assertEquals(LocalDate.now().plusDays(14), result.getDueDate());

        // Book must be saved as unavailable
        verify(bookRepository).save(argThat(b -> !b.isAvailable()));
        // Borrow record must be persisted
        verify(borrowedBookRepository).save(any(BorrowedBook.class));
    }

    @Test
    void borrowBook_setsBookAvailabilityToFalse() {
        Book book = new Book(2L, "Divergent", "Veronica Roth", true);
        when(bookRepository.findById(2L)).thenReturn(Optional.of(book));

        borrowService.borrowBook(2L);

        // The book object passed to save must have available=false
        verify(bookRepository).save(argThat(b -> !b.isAvailable()));
    }

    @Test
    void borrowBook_dueDateIsConfigurableViaDueDays() {
        // Change dueDays to 7 and verify the due date adjusts accordingly
        ReflectionTestUtils.setField(borrowService, "dueDays", 7);
        Book book = new Book(3L, "Wonder", "R.J. Palacio", true);
        when(bookRepository.findById(3L)).thenReturn(Optional.of(book));

        BorrowedBook result = borrowService.borrowBook(3L);

        assertEquals(LocalDate.now().plusDays(7), result.getDueDate());
    }

    // ---------------------------------------------------------------
    // borrowBook – edge cases (retro improvement)
    // ---------------------------------------------------------------

    @Test
    void borrowBook_throwsForNullBookId() {
        assertThrows(IllegalArgumentException.class, () -> borrowService.borrowBook(null));
        verifyNoInteractions(bookRepository);
        verifyNoInteractions(borrowedBookRepository);
    }

    @Test
    void borrowBook_throwsWhenBookNotFound() {
        when(bookRepository.findById(999L)).thenReturn(Optional.empty());
        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> borrowService.borrowBook(999L));
        assertTrue(ex.getMessage().contains("999"));
        verify(borrowedBookRepository, never()).save(any());
    }

    @Test
    void borrowBook_throwsWhenBookAlreadyBorrowed() {
        Book book = new Book(1L, "The Giver", "Lois Lowry", false); // already borrowed
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> borrowService.borrowBook(1L));
        assertTrue(ex.getMessage().contains("already borrowed"));
        verify(borrowedBookRepository, never()).save(any());
    }

    // ---------------------------------------------------------------
    // getMyBooks (US4)
    // ---------------------------------------------------------------

    @Test
    void getMyBooks_returnsAllBorrowRecords() {
        BorrowedBook r1 = new BorrowedBook(1L, "Wonder", "R.J. Palacio", LocalDate.now());
        BorrowedBook r2 = new BorrowedBook(2L, "Holes",  "Louis Sachar", LocalDate.now());
        when(borrowedBookRepository.findAll()).thenReturn(java.util.Arrays.asList(r1, r2));

        java.util.List<BorrowedBook> result = borrowService.getMyBooks();
        assertEquals(2, result.size());
    }

    @Test
    void getMyBooks_returnsEmptyListWhenNoBooksAreBorrowed() {
        when(borrowedBookRepository.findAll()).thenReturn(java.util.Collections.emptyList());
        assertTrue(borrowService.getMyBooks().isEmpty());
    }
}