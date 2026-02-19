package com.teenread.controller;

import com.teenread.repository.BookRepository;
import com.teenread.repository.BorrowedBookRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * MockMvc slice tests for HealthController (Sprint 2 monitoring).
 *
 * Sprint 2 retro improvement:
 *   Health endpoint tests written BEFORE the endpoint is considered complete.
 */
@WebMvcTest(HealthController.class)
class HealthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookRepository bookRepository;

    @MockBean
    private BorrowedBookRepository borrowedBookRepository;

    @Test
    void health_returns200WithStatusUp() throws Exception {
        when(bookRepository.findAll()).thenReturn(Collections.emptyList());
        when(borrowedBookRepository.count()).thenReturn(0);

        mockMvc.perform(get("/health"))
                .andExpect(status().isOk())
                // Monitoring: status must be "UP"
                .andExpect(jsonPath("$.status").value("UP"))
                // Timestamp must be present
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void health_reportsTotalBooksCount() throws Exception {
        // Simulate 12 seeded books and 2 borrow records
        when(bookRepository.findAll()).thenReturn(
                java.util.Arrays.asList(
                        new com.teenread.model.Book(1L, "A", "A", true),
                        new com.teenread.model.Book(2L, "B", "B", false)
                )
        );
        when(borrowedBookRepository.count()).thenReturn(1);

        mockMvc.perform(get("/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalBooks").value(2))
                .andExpect(jsonPath("$.borrowedBooks").value(1));
    }

    @Test
    void health_returns200EvenWithNoBorrowedBooks() throws Exception {
        when(bookRepository.findAll()).thenReturn(Collections.emptyList());
        when(borrowedBookRepository.count()).thenReturn(0);

        mockMvc.perform(get("/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.borrowedBooks").value(0));
    }
}
