package com.teenread.model;

import java.time.LocalDate;

/**
 * Domain model representing a borrow record (US3 / US4).
 *
 * Created when a user borrows a book.
 * Stored in BorrowedBookRepository's in-memory list.
 *
 * Contains a snapshot of the book's title and author at borrow time
 * so the "My Books" view still works if the catalogue changes.
 */

public class BorrowedBook {
    /** The id of the Book that was borrowed */
    private Long bookId;

    /** Snapshot of the book title at borrow time */
    private String title;

    /** Snapshot of the author at borrow time */
    private String author;

    /**
     * The date on which the borrowed book must be returned (US3).
     * Calculated as: today + app.borrow.due-days
     */
    private LocalDate dueDate;

    // ---------------------------------------------------------------
    // Constructors
    // ---------------------------------------------------------------

    /** No-arg constructor for Jackson */
    public BorrowedBook() {}

    /**
     * Constructor used by BorrowService when creating a new borrow record.
     *
     * @param bookId  id of the borrowed book
     * @param title   title snapshot
     * @param author  author snapshot
     * @param dueDate calculated due date
     */
    public BorrowedBook(Long bookId, String title, String author, LocalDate dueDate) {
        this.bookId  = bookId;
        this.title   = title;
        this.author  = author;
        this.dueDate = dueDate;
    }

    // ---------------------------------------------------------------
    // Getters & Setters
    // ---------------------------------------------------------------

    public Long getBookId()                    { return bookId; }
    public void setBookId(Long bookId)         { this.bookId = bookId; }

    public String getTitle()                   { return title; }
    public void setTitle(String title)         { this.title = title; }

    public String getAuthor()                  { return author; }
    public void setAuthor(String author)       { this.author = author; }

    public LocalDate getDueDate()              { return dueDate; }
    public void setDueDate(LocalDate dueDate)  { this.dueDate = dueDate; }

    @Override
    public String toString() {
        return "BorrowedBook{bookId=" + bookId +
                ", title='" + title + '\'' +
                ", dueDate=" + dueDate + '}';
    }
}
