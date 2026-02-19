package com.teenread.service;

import com.teenread.model.Book;
import com.teenread.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for RecommendationService (US5).
 *
 * Sprint 2 retro improvement:
 *   Edge cases (empty catalogue, fewer books than count, all borrowed) written
 *   and verified BEFORE the RecommendationController is implemented.
 */
@ExtendWith(MockitoExtension.class)
class RecommendationServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private RecommendationService recommendationService;

    @BeforeEach
    void setUp() {
        // Set the configurable count to 3 (matching application.properties default)
        ReflectionTestUtils.setField(recommendationService, "recommendationCount", 3);
    }

    // ---------------------------------------------------------------
    // Happy path
    // ---------------------------------------------------------------

    @Test
    void getRecommendations_returnsUpToCountAvailableBooks() {
        // 5 available books in the catalogue
        List<Book> books = Arrays.asList(
                new Book(1L, "A", "Author", true),
                new Book(2L, "B", "Author", true),
                new Book(3L, "C", "Author", true),
                new Book(4L, "D", "Author", true),
                new Book(5L, "E", "Author", true)
        );
        when(bookRepository.findAll()).thenReturn(books);

        List<Book> recs = recommendationService.getRecommendations();

        // Must return exactly 3 (the configured count)
        assertEquals(3, recs.size());
    }

    @Test
    void getRecommendations_onlyIncludesAvailableBooks() {
        // Mix of available and borrowed
        List<Book> books = Arrays.asList(
                new Book(1L, "Available", "A", true),
                new Book(2L, "Borrowed",  "B", false),
                new Book(3L, "Available2","C", true)
        );
        when(bookRepository.findAll()).thenReturn(books);
        ReflectionTestUtils.setField(recommendationService, "recommendationCount", 3);

        List<Book> recs = recommendationService.getRecommendations();

        // Only 2 available books exist – should return both, not throw
        assertTrue(recs.size() <= 2);
        assertTrue(recs.stream().allMatch(Book::isAvailable));
    }

    // ---------------------------------------------------------------
    // Edge cases (retro improvement)
    // ---------------------------------------------------------------

    @Test
    void getRecommendations_returnsEmptyListWhenNoBooksAvailable() {
        when(bookRepository.findAll()).thenReturn(Collections.emptyList());
        List<Book> recs = recommendationService.getRecommendations();
        assertTrue(recs.isEmpty());
    }

    @Test
    void getRecommendations_returnsFewerThanCountWhenCatalogueIsSmall() {
        // Only 2 available books but count = 3
        List<Book> books = Arrays.asList(
                new Book(1L, "Only One",  "A", true),
                new Book(2L, "Only Two",  "B", true)
        );
        when(bookRepository.findAll()).thenReturn(books);

        List<Book> recs = recommendationService.getRecommendations();

        // Must return 2, not throw an exception for asking for 3
        assertEquals(2, recs.size());
    }

    @Test
    void getRecommendations_returnsEmptyWhenAllBooksAreBorrowed() {
        List<Book> books = Arrays.asList(
                new Book(1L, "Borrowed1", "A", false),
                new Book(2L, "Borrowed2", "B", false)
        );
        when(bookRepository.findAll()).thenReturn(books);

        List<Book> recs = recommendationService.getRecommendations();
        assertTrue(recs.isEmpty());
    }

    @Test
    void getRecommendations_respectsDifferentCountConfiguration() {
        // Change count to 1
        ReflectionTestUtils.setField(recommendationService, "recommendationCount", 1);
        List<Book> books = Arrays.asList(
                new Book(1L, "A", "X", true),
                new Book(2L, "B", "Y", true),
                new Book(3L, "C", "Z", true)
        );
        when(bookRepository.findAll()).thenReturn(books);

        List<Book> recs = recommendationService.getRecommendations();
        assertEquals(1, recs.size());
    }
}