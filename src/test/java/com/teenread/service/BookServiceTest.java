// path: src/test/java/com/teenread/service/BookServiceTest.java
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
 * Sprint 2 retro improvement:
 *   Additional edge-case tests added BEFORE any Sprint 2 feature touches this service.
 */
@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    private Book book1;
    private Book book2;

    @BeforeEach
    void setUp() {
        book1 = new Book(1L, "The Hunger Games", "Suzanne Collins", true);
        book2 = new Book(2L, "Harry Potter",     "J.K. Rowling",    true);
    }

    // ---------------------------------------------------------------
    // getAllBooks (US1)
    // ---------------------------------------------------------------

    @Test
    void getAllBooks_returnsFullList() {
        when(bookRepository.findAll()).thenReturn(Arrays.asList(book1, book2));
        List<Book> result = bookService.getAllBooks();
        assertEquals(2, result.size());
        verify(bookRepository, times(1)).findAll();
    }

    @Test
    void getAllBooks_returnsEmptyListWhenNoBooksExist() {
        when(bookRepository.findAll()).thenReturn(Collections.emptyList());
        assertTrue(bookService.getAllBooks().isEmpty());
    }

    // ---------------------------------------------------------------
    // searchByTitle (US2) – Sprint 2 adds more edge-case coverage
    // ---------------------------------------------------------------

    @Test
    void searchByTitle_returnsMatchingBooks() {
        when(bookRepository.searchByTitle("Hunger"))
                .thenReturn(Collections.singletonList(book1));
        List<Book> result = bookService.searchByTitle("Hunger");
        assertEquals(1, result.size());
        assertEquals("The Hunger Games", result.get(0).getTitle());
    }

    @Test
    void searchByTitle_trimsWhitespace() {
        when(bookRepository.searchByTitle("Harry")).thenReturn(Collections.singletonList(book2));
        bookService.searchByTitle("  Harry  ");
        verify(bookRepository).searchByTitle("Harry");
    }

    @Test
    void searchByTitle_returnsEmptyListForNoMatch() {
        when(bookRepository.searchByTitle("xyz")).thenReturn(Collections.emptyList());
        assertTrue(bookService.searchByTitle("xyz").isEmpty());
    }

    @Test
    void searchByTitle_throwsForNullKeyword() {
        assertThrows(IllegalArgumentException.class, () -> bookService.searchByTitle(null));
        verifyNoInteractions(bookRepository);
    }

    @Test
    void searchByTitle_throwsForBlankKeyword() {
        assertThrows(IllegalArgumentException.class, () -> bookService.searchByTitle("   "));
        verifyNoInteractions(bookRepository);
    }

    @Test
    void searchByTitle_throwsForEmptyStringKeyword() {
        // Edge case: empty string (not just whitespace)
        assertThrows(IllegalArgumentException.class, () -> bookService.searchByTitle(""));
        verifyNoInteractions(bookRepository);
    }

    @Test
    void searchByTitle_singleCharacterKeyword_delegatesToRepository() {
        // Edge case: a single character is a valid keyword
        when(bookRepository.searchByTitle("H")).thenReturn(Arrays.asList(book1, book2));
        List<Book> result = bookService.searchByTitle("H");
        assertEquals(2, result.size());
        verify(bookRepository).searchByTitle("H");
    }
}