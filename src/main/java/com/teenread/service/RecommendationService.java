package com.teenread.service;

import com.teenread.model.Book;
import com.teenread.repository.BookRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service layer for book recommendations (US5).
 *
 * Strategy for Sprint 2:
 *   Return the first N available books from the catalogue, shuffled so
 *   the recommendations feel fresh on each page load.
 *   This is intentionally simple – does not block core functionality (US5 AC).
 *
 * Sprint 2 retro improvement:
 *   Edge case where fewer than N books are available is tested before merge.
 */
@Service

public class RecommendationService {

    private static final Logger log = LoggerFactory.getLogger(RecommendationService.class);

    /** Full catalogue repository – recommendations drawn from available books */
    private final BookRepository bookRepository;

    /**
     * Maximum number of recommendations to return.
     * Configured via application.properties: app.recommendations.count (default 3).
     */
    @Value("${app.recommendations.count:3}")
    private int recommendationCount;

    /**
     * Constructor injection of the book repository.
     *
     * @param bookRepository the in-memory book catalogue
     */
    public RecommendationService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    // ---------------------------------------------------------------
    // US5 – Get recommendations
    // ---------------------------------------------------------------

    /**
     * Returns up to {@code recommendationCount} available books, shuffled.
     *
     * Algorithm:
     *   1. Fetch all books
     *   2. Filter to only available ones (borrowed books are excluded)
     *   3. Shuffle the filtered list for variety
     *   4. Return the first recommendationCount items (or fewer if not enough)
     *
     * Edge case (retro improvement): if fewer than recommendationCount books
     * are available, the method returns however many exist (no exception).
     *
     * @return list of recommended Book objects (1–recommendationCount entries)
     */
    public List<Book> getRecommendations() {
        // Step 1 & 2: get all available books
        List<Book> available = bookRepository.findAll().stream()
                .filter(Book::isAvailable)
                .collect(Collectors.toList());

        // Edge case: no available books at all
        if (available.isEmpty()) {
            log.warn("No available books to recommend");
            return Collections.emptyList();
        }

        // Step 3: shuffle in-place so results vary per call
        Collections.shuffle(available);

        // Step 4: trim to the configured count (subList is safe even if smaller)
        List<Book> recommendations = available.subList(
                0, Math.min(recommendationCount, available.size()));

        log.debug("RECOMMENDATIONS – returning {} book(s)", recommendations.size());

        return recommendations;
    }
}
