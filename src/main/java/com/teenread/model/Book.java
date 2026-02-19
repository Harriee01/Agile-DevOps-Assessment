package com.teenread.model;

/**
 * Domain model representing a single book in the TeenRead Hub catalogue.
 *
 * Sprint 1: id, title, author, available
 * Sprint 2: no new fields needed here – availability is toggled by BorrowService
 *
 * Plain Java class (no JPA annotations) – stored entirely in-memory.
 */

public class Book {
    // Unique identifier for each book
    private Long id;

    // Full title of the book
    private String title;

    // Full name of the author
    private String author;

    /**
     * Availability flag.
     * true  = on the shelf, can be borrowed
     * false = currently borrowed by a user
     */
    private boolean available;

    // ---------------------------------------------------------------
    // Constructors
    // ---------------------------------------------------------------

    /** No-arg constructor required for JSON serialisation by Jackson. */
    public Book() {}

    /**
     * Full constructor used when seeding the in-memory catalogue.
     *
     * @param id        unique book ID
     * @param title     book title
     * @param author    book author
     * @param available initial availability flag
     */
    public Book(Long id, String title, String author, boolean available) {
        this.id        = id;
        this.title     = title;
        this.author    = author;
        this.available = available;
    }

    // ---------------------------------------------------------------
    // Getters & Setters  (Jackson serialises public getters to JSON)
    // ---------------------------------------------------------------

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    // ---------------------------------------------------------------
    // toString – useful in logs and test failure messages
    // ---------------------------------------------------------------

    @Override
    public String toString() {
        return "Book{id=" + id +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", available=" + available + '}';
    }
}
