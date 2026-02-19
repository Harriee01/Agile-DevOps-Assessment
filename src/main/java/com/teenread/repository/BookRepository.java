package com.teenread.repository;

import com.teenread.model.Book;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * In-memory repository for the book catalogue.
 *
 * Sprint 1: findAll, searchByTitle
 *  * Sprint 2: findById, save (to toggle availability flag)
 *  *
 *  * The internal list is the single source of truth for book state.
 *  * All mutations (borrow → available=false) go through save().
 */

@Repository
public class BookRepository {
    // The master list of all books held in JVM memory.
    // Initialised once in the constructor; mutated via save().
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
     * Finds a single book by its unique id.
     * Returns Optional.empty() if no book with that id exists.
     *
     * Used by BorrowService before marking a book unavailable (US3).
     *
     * @param id the book id to look up
     * @return Optional wrapping the found Book, or empty
     */

    public Optional<Book> findById(Long id) {
        return books.stream()
                .filter(b -> b.getId().equals(id))
                .findFirst();
    }

    /**
     * Persists changes to an existing book back into the in-memory list.
     *
     * Finds the book by id and replaces it in-place.
     * No-op if the book id is not found (should not happen in normal flow).
     *
     * Used by BorrowService to flip available → false after borrowing (US3).
     *
     * @param updatedBook the book with mutated state to save
     */

    public void save(Book updatedBook) {
        for (int i = 0; i < books.size(); i++) {
            if (books.get(i).getId().equals(updatedBook.getId())) {
                // Replace the old Book object at this index with the updated one
                books.set(i, updatedBook);
                return;
            }
        }
        // If we reach here the book was not found – log-worthy in a real system
    }

    /**
     * Case-insensitive partial-title search using Java Streams.
     *
     * @param keyword partial or full title (already trimmed by the service layer)
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
