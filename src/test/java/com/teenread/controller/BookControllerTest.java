package com.teenread.controller;

import com.teenread.model.Book;
import com.teenread.service.BookService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration-slice tests for BookController using Spring's MockMvc.
 *
 * @WebMvcTest loads only the web layer (controllers, filters, etc.)
 * without starting a real server. BookService is mocked via @MockBean.
 *
 * These tests verify:
 *  - HTTP status codes
 *  - Response Content-Type
 *  - JSON response body structure and values
 */
@WebMvcTest(BookController.class)

public class BookControllerTest {

    /** MockMvc lets us send HTTP requests to the controller without a real server */
    @Autowired
    private MockMvc mockMvc;

    /** @MockBean replaces the real BookService bean in the Spring test context */
    @MockBean
    private BookService bookService;

    // ---------------------------------------------------------------
    // GET /api/books  (US1)
    // ---------------------------------------------------------------

    @Test
    void getAllBooks_returns200WithBookList() throws Exception {
        // Arrange: service will return two books
        Book b1 = new Book(1L, "The Hunger Games", "Suzanne Collins", true);
        Book b2 = new Book(2L, "Harry Potter",     "J.K. Rowling",    true);
        when(bookService.getAllBooks()).thenReturn(Arrays.asList(b1, b2));

        // Act & Assert: perform GET and verify response
        mockMvc.perform(get("/api/books")
                        .accept(MediaType.APPLICATION_JSON))
                // 200 OK
                .andExpect(status().isOk())
                // Content-Type is JSON
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                // Response body is a JSON array with 2 elements
                .andExpect(jsonPath("$.length()").value(2))
                // First book's title matches
                .andExpect(jsonPath("$[0].title").value("The Hunger Games"))
                // Second book's author matches
                .andExpect(jsonPath("$[1].author").value("J.K. Rowling"));
    }

    @Test
    void getAllBooks_returns200WithEmptyArrayWhenNoBooksExist() throws Exception {
        // Arrange
        when(bookService.getAllBooks()).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                // Empty array, not null
                .andExpect(jsonPath("$.length()").value(0));
    }

    // ---------------------------------------------------------------
    // GET /api/books/search?keyword=…  (US2 + US6)
    // ---------------------------------------------------------------

    @Test
    void searchBooks_returns200WithResultsAndMessage() throws Exception {
        // Arrange
        Book b1 = new Book(1L, "The Hunger Games", "Suzanne Collins", true);
        when(bookService.searchByTitle("Hunger"))
                .thenReturn(Collections.singletonList(b1));

        // Act & Assert
        mockMvc.perform(get("/api/books/search")
                        .param("keyword", "Hunger")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                // "results" array has 1 element
                .andExpect(jsonPath("$.results.length()").value(1))
                .andExpect(jsonPath("$.results[0].title").value("The Hunger Games"))
                // US6: message field is present and mentions the keyword
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void searchBooks_returns200WithEmptyResultsAndNoResultsMessage() throws Exception {
        // Arrange: no books match "xyz"
        when(bookService.searchByTitle("xyz"))
                .thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/api/books/search").param("keyword", "xyz"))
                .andExpect(status().isOk())
                // Empty results array
                .andExpect(jsonPath("$.results.length()").value(0))
                // US6: message must exist and signal no results
                .andExpect(jsonPath("$.message").value(
                        org.hamcrest.Matchers.containsString("No results found")));
    }

    @Test
    void searchBooks_returns400WhenKeywordIsMissing() throws Exception {
        // Act & Assert: no keyword param at all → 400 Bad Request
        mockMvc.perform(get("/api/books/search"))
                .andExpect(status().isBadRequest())
                // US6: message tells the user what to do
                .andExpect(jsonPath("$.message").value("Please enter a search term."));
    }

    @Test
    void searchBooks_returns400WhenKeywordIsBlank() throws Exception {
        // Act & Assert: blank keyword → 400 Bad Request
        mockMvc.perform(get("/api/books/search").param("keyword", "   "))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Please enter a search term."));
    }

}
