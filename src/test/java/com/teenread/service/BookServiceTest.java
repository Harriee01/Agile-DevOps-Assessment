package com.teenread.service;

import com.teenread.model.Book;
import com.teenread.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for BookService.
 *
 * Uses Mockito to replace the real BookRepository with a test double,
 * so these tests run fast and in isolation (no Spring context needed).
 *
 * @ExtendWith(MockitoExtension.class) enables Mockito annotations in JUnit 5.
 */
@ExtendWith(MockitoExtension.class)
public class BookServiceTest {

    /** Mockito creates a fake BookRepository – no real data loaded */
    @Mock
    private BookRepository bookRepository;

    /** Mockito creates BookService and injects the mock repository into it */
    @InjectMocks
    private BookService bookService;

    // Shared test fixtures created fresh before each test
    private Book book1;
    private Book book2;

    @BeforeEach
    void setUp() {
        // Two books to use across multiple test cases
        book1 = new Book(1L, "The Hunger Games", "Suzanne Collins", true);
        book2 = new Book(2L, "Harry Potter", "J.K. Rowling", true);
    }

    // ---------------------------------------------------------------
    // getAllBooks() tests  (US1)
    // ---------------------------------------------------------------

    @Test
    void getAllBooks_returnsFullList() {
        // Arrange: mock repository returns both books
        when(bookRepository.findAll()).thenReturn(Arrays.asList(book1, book2));

        // Act
        List<Book> result = bookService.getAllBooks();

        // Assert: service passes the full list through unchanged
        assertEquals(2, result.size(), "Should return 2 books");
        assertEquals("The Hunger Games", result.get(0).getTitle());
        assertEquals("Harry Potter",     result.get(1).getTitle());

        // Verify: repository was called exactly once
        verify(bookRepository, times(1)).findAll();
    }

    @Test
    void getAllBooks_returnsEmptyListWhenNoBooksExist() {
        // Arrange
        when(bookRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<Book> result = bookService.getAllBooks();

        // Assert
        assertTrue(result.isEmpty(), "Should return empty list when repo has no books");
    }

    // ---------------------------------------------------------------
    // searchByTitle() tests  (US2)
    // ---------------------------------------------------------------

    @Test
    void searchByTitle_returnsMatchingBooks() {
        // Arrange: only book1 matches "Hunger"
        when(bookRepository.searchByTitle("Hunger"))
                .thenReturn(Collections.singletonList(book1));

        // Act
        List<Book> result = bookService.searchByTitle("Hunger");

        // Assert
        assertEquals(1, result.size());
        assertEquals("The Hunger Games", result.get(0).getTitle());
    }

    @Test
    void searchByTitle_trimsWhitespaceBeforeSearching() {
        // Arrange: "  Harry  " should be trimmed to "Harry"
        when(bookRepository.searchByTitle("Harry"))
                .thenReturn(Collections.singletonList(book2));

        // Act: call with extra whitespace
        List<Book> result = bookService.searchByTitle("  Harry  ");

        // Assert: repository received the trimmed keyword
        verify(bookRepository).searchByTitle("Harry");
        assertEquals(1, result.size());
    }

    @Test
    void searchByTitle_returnsEmptyListWhenNoMatch() {
        // Arrange
        when(bookRepository.searchByTitle("xyz")).thenReturn(Collections.emptyList());

        // Act
        List<Book> result = bookService.searchByTitle("xyz");

        // Assert
        assertTrue(result.isEmpty(), "Should return empty list for unmatched keyword");
    }

    @Test
    void searchByTitle_throwsExceptionWhenKeywordIsNull() {
        // Act & Assert: null keyword must throw IllegalArgumentException
        assertThrows(IllegalArgumentException.class,
                () -> bookService.searchByTitle(null),
                "Null keyword should throw IllegalArgumentException");

        // Verify: repository should never be called for invalid input
        verifyNoInteractions(bookRepository);
    }

    @Test
    void searchByTitle_throwsExceptionWhenKeywordIsBlank() {
        // Act & Assert: blank keyword (spaces only) must throw
        assertThrows(IllegalArgumentException.class,
                () -> bookService.searchByTitle("   "),
                "Blank keyword should throw IllegalArgumentException");

        verifyNoInteractions(bookRepository);
    }

}
