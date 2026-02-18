package com.teenread.model;

public class Book {
    // Unique identifier for each book
    private Long id;

    // Full title of the book
    private String title;

    // Full name of the author
    private String author;

    // true = available to borrow; false = already borrowed
    private boolean available;

    // ---------------------------------------------------------------
    // Constructors
    // ---------------------------------------------------------------

    /** No-arg constructor required for JSON serialisation by Jackson. */
    public Book() {}

    /**
     * Convenience constructor used when pre-loading the in-memory list.
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
