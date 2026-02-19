package com.teenread.controller;

import com.teenread.exception.GlobalExceptionHandler;
import com.teenread.model.Book;
import com.teenread.service.RecommendationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * MockMvc slice tests for RecommendationController (US5).
 *
 * Sprint 2 retro improvement:
 *   Empty-result case tested BEFORE merge to verify US5 AC
 *   ("recommendations do not block core functionality").
 */
@WebMvcTest(RecommendationController.class)
@Import(GlobalExceptionHandler.class)
class RecommendationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RecommendationService recommendationService;

    @Test
    void getRecommendations_returns200WithListOfBooks() throws Exception {
        Book b1 = new Book(1L, "The Giver",  "Lois Lowry",   true);
        Book b2 = new Book(2L, "Holes",      "Louis Sachar", true);
        Book b3 = new Book(3L, "Hatchet",    "Gary Paulsen", true);
        when(recommendationService.getRecommendations()).thenReturn(Arrays.asList(b1, b2, b3));

        mockMvc.perform(get("/api/recommendations"))
                .andExpect(status().isOk())
                // US5: at least 3 recommended books shown
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].title").value("The Giver"));
    }

    @Test
    void getRecommendations_returns200WithEmptyArrayWhenNoAvailableBooks() throws Exception {
        // US5 AC: empty recommendations must NOT cause an error response
        when(recommendationService.getRecommendations()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/recommendations"))
                .andExpect(status().isOk())          // 200, not 500 or 404
                .andExpect(jsonPath("$.length()").value(0));
    }
}