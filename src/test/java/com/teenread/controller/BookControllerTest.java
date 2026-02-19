package com.teenread.controller;

import com.teenread.model.Book;
import com.teenread.service.BookService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.teenread.exception.GlobalExceptionHandler;

/**
 * MockMvc slice tests for BookController.
 *
 * Sprint 2: GlobalExceptionHandler imported so error-path tests work correctly.
 * Sprint 2 retro improvement: extra edge-case tests added before merge.
 */
@WebMvcTest(BookController.class)
@Import(GlobalExceptionHandler.class)
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    // ---------------------------------------------------------------
    // GET /api/books  (US1)
    // ---------------------------------------------------------------

    @Test
    void getAllBooks_returns200WithBookList() throws Exception {
        Book b1 = new Book(1L, "The Hunger Games", "Suzanne Collins", true);
        Book b2 = new Book(2L, "Harry Potter",     "J.K. Rowling",    true);
        when(bookService.getAllBooks()).thenReturn(Arrays.asList(b1, b2));

        mockMvc.perform(get("/api/books").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").value("The Hunger Games"))
                .andExpect(jsonPath("$[1].author").value("J.K. Rowling"));
    }

    @Test
    void getAllBooks_returns200WithEmptyArray() throws Exception {
        when(bookService.getAllBooks()).thenReturn(Collections.emptyList());
        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    // ---------------------------------------------------------------
    // GET /api/books/search (US2 + US6)
    // ---------------------------------------------------------------

    @Test
    void searchBooks_returns200WithResultsAndMessage() throws Exception {
        Book b1 = new Book(1L, "The Hunger Games", "Suzanne Collins", true);
        when(bookService.searchByTitle("Hunger")).thenReturn(Collections.singletonList(b1));

        mockMvc.perform(get("/api/books/search").param("keyword", "Hunger"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results.length()").value(1))
                .andExpect(jsonPath("$.results[0].title").value("The Hunger Games"))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void searchBooks_returns200WithEmptyResultsAndNoResultsMessage() throws Exception {
        when(bookService.searchByTitle("xyz")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/books/search").param("keyword", "xyz"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results.length()").value(0))
                .andExpect(jsonPath("$.message").value(containsString("No results found")));
    }

    @Test
    void searchBooks_returns400WhenKeywordIsMissing() throws Exception {
        mockMvc.perform(get("/api/books/search"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Please enter a search term."));
    }

    @Test
    void searchBooks_returns400WhenKeywordIsBlank() throws Exception {
        mockMvc.perform(get("/api/books/search").param("keyword", "   "))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Please enter a search term."));
    }

    @Test
    void searchBooks_messageContainsKeywordOnSuccess() throws Exception {
        Book b = new Book(5L, "Percy Jackson", "Rick Riordan", true);
        when(bookService.searchByTitle("Percy")).thenReturn(Collections.singletonList(b));

        mockMvc.perform(get("/api/books/search").param("keyword", "Percy"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(containsString("Percy")));
    }
}