package com.teenread.service;

import com.teenread.model.Book;
import com.teenread.repository.BookRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service layer for book-related business logic.
 *
 * Sits between the REST controller and the repository.
 * Responsibilities for Sprint 1:
 *   - Delegate retrieval of all books to the repository
 *   - Validate search input before delegating to the repository
 *   - Return structured results the controller can serialise to JSON
 *
 * @Service marks this class as a Spring-managed service bean.
 */
@Service

public class BookService {

    // Repository injected by Spring constructor injection (preferred over @Autowired field)
    private final BookRepository bookRepository;

    /**
     * Constructor injection: Spring automatically provides the BookRepository bean.
     *
     * @param bookRepository the in-memory book data store
     */
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    // ---------------------------------------------------------------
    // US1 – Browse available books
    // ---------------------------------------------------------------

    /**
     * Retrieves the full catalogue of books from the repository.
     * No filtering at this layer; the controller will serialise the list to JSON.
     *
     * @return list of all Book objects
     */
    public List<Book> getAllBooks() {
        // Delegate directly to the repository; no extra logic needed for US1
        return bookRepository.findAll();
    }

    // ---------------------------------------------------------------
    // US2 – Search books by title
    // ---------------------------------------------------------------

    /**
     * Searches books by partial, case-insensitive title match.
     *
     * Edge cases (retro improvement – explicit guards):
     *   - null keyword        → IllegalArgumentException
     *   - blank-only keyword  → IllegalArgumentException
     *
     * @param keyword the search term
     * @return matching books (empty list if none match)
     * @throws IllegalArgumentException for null or blank keyword
     */
    public List<Book> searchByTitle(String keyword) {
        // Guard: reject null or blank keywords to prevent meaningless queries
        if (keyword == null || keyword.isBlank()) {
            throw new IllegalArgumentException("Search keyword must not be blank");
        }

        // Strip leading/trailing whitespace that users might accidentally type
        String cleanKeyword = keyword.trim();

        // Delegate to repository which performs the actual filtering
        return bookRepository.searchByTitle(cleanKeyword);
    }

}
