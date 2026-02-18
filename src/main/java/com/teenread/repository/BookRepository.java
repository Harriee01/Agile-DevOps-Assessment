package com.teenread.repository;

import com.teenread.model.Book;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * In-memory repository that acts as the sole data store for Sprint 1.
 * No database, no JPA – just a plain Java List pre-loaded with seed data.
 *
 * Annotated with @Repository so Spring registers it as a bean and
 * so that Spring can translate any data-access exceptions if needed.
 */

@Repository
public class BookRepository {
    // The master list of all books held in JVM memory.
    // Initialised once when the Spring context starts.
    private final List<Book> books = new ArrayList<>();

    /**
     * Constructor: called once by Spring on startup.
     * Seeds 12 books that are representative of teen reading interests.
     */
    public BookRepository() {
        // Each Book(id, title, author, available)
        books.add(new Book(1L,  "The Hunger Games",              "Suzanne Collins",   true));
        books.add(new Book(2L,  "Harry Potter and the Sorcerer's Stone", "J.K. Rowling", true));
        books.add(new Book(3L,  "Divergent",                     "Veronica Roth",     true));
        books.add(new Book(4L,  "The Maze Runner",               "James Dashner",     true));
        books.add(new Book(5L,  "Percy Jackson: The Lightning Thief", "Rick Riordan", true));
        books.add(new Book(6L,  "Ender's Game",                  "Orson Scott Card",  true));
        books.add(new Book(7L,  "The Giver",                     "Lois Lowry",        true));
        books.add(new Book(8L,  "Holes",                         "Louis Sachar",      true));
        books.add(new Book(9L,  "Wonder",                        "R.J. Palacio",      true));
        books.add(new Book(10L, "Tuck Everlasting",              "Natalie Babbitt",   true));
        books.add(new Book(11L, "Hatchet",                       "Gary Paulsen",      true));
        books.add(new Book(12L, "The Outsiders",                 "S.E. Hinton",       true));
    }

    /**
     * Returns a copy of the full book list so callers cannot mutate
     * the internal list directly.
     *
     * @return unmodifiable snapshot of all books
     */
    public List<Book> findAll() {
        // Return a new ArrayList so the internal list stays protected
        return new ArrayList<>(books);
    }

    /**
     * Case-insensitive keyword search against book titles.
     * Uses Java Stream filter + String.contains for simplicity.
     *
     * @param keyword the search term (partial or full title)
     * @return list of books whose title contains the keyword
     */
    public List<Book> searchByTitle(String keyword) {
        // Normalise keyword to lower-case once, then compare each title
        String lowerKeyword = keyword.toLowerCase();

        return books.stream()
                // Keep only books whose lower-cased title contains the keyword
                .filter(book -> book.getTitle().toLowerCase().contains(lowerKeyword))
                // Collect matched books into a new list
                .collect(Collectors.toList());
    }

}
