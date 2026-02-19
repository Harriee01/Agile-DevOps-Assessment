package com.teenread.controller;

import com.teenread.exception.GlobalExceptionHandler;
import com.teenread.model.BorrowedBook;
import com.teenread.service.BorrowService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * MockMvc slice tests for BorrowController (US3 / US4).
 *
 * Sprint 2 retro improvement:
 *   Error-path and edge-case tests written BEFORE the controller is considered "done".
 *   GlobalExceptionHandler imported so @ExceptionHandler mappings are active.
 */
@WebMvcTest(BorrowController.class)
@Import(GlobalExceptionHandler.class)
class BorrowControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BorrowService borrowService;

    // ---------------------------------------------------------------
    // POST /api/borrow/{bookId}  (US3)
    // ---------------------------------------------------------------

    @Test
    void borrowBook_returns200WithBorrowedBookAndMessage() throws Exception {
        LocalDate due = LocalDate.now().plusDays(14);
        BorrowedBook record = new BorrowedBook(1L, "The Hunger Games", "Suzanne Collins", due);
        when(borrowService.borrowBook(1L)).thenReturn(record);

        mockMvc.perform(post("/api/borrow/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.borrowedBook.title").value("The Hunger Games"))
                // US3: due date must be present in the response
                .andExpect(jsonPath("$.borrowedBook.dueDate").exists())
                // US6: message contains the due date
                .andExpect(jsonPath("$.message").value(containsString("Due back by")));
    }

    @Test
    void borrowBook_returns400WhenBookNotFound() throws Exception {
        when(borrowService.borrowBook(999L))
                .thenThrow(new IllegalStateException("Book not found with id: 999"));

        mockMvc.perform(post("/api/borrow/999"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("999")));
    }

    @Test
    void borrowBook_returns400WhenBookAlreadyBorrowed() throws Exception {
        when(borrowService.borrowBook(1L))
                .thenThrow(new IllegalStateException("Book 'The Giver' is already borrowed."));

        mockMvc.perform(post("/api/borrow/1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("already borrowed")));
    }

    // ---------------------------------------------------------------
    // GET /api/borrow/my-books  (US4)
    // ---------------------------------------------------------------

    @Test
    void getMyBooks_returns200WithListOfBorrowedBooks() throws Exception {
        LocalDate due = LocalDate.now().plusDays(14);
        BorrowedBook r1 = new BorrowedBook(1L, "Wonder", "R.J. Palacio", due);
        BorrowedBook r2 = new BorrowedBook(2L, "Holes",  "Louis Sachar", due);
        when(borrowService.getMyBooks()).thenReturn(Arrays.asList(r1, r2));

        mockMvc.perform(get("/api/borrow/my-books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").value("Wonder"))
                // US4: due date visible for each borrowed book
                .andExpect(jsonPath("$[0].dueDate").exists());
    }

    @Test
    void getMyBooks_returns200WithEmptyArrayWhenNoBooksAreBorrowed() throws Exception {
        when(borrowService.getMyBooks()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/borrow/my-books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}