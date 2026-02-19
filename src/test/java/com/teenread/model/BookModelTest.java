package com.teenread.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Book model.
 *
 * Sprint 2 retro improvement:
 *   Edge cases on the model itself are verified before any feature merge.
 */
class BookModelTest {

    @Test
    void defaultConstructor_createsBookWithNullFields() {
        // No-arg constructor required by Jackson
        Book book = new Book();
        assertNull(book.getId());
        assertNull(book.getTitle());
        assertNull(book.getAuthor());
        assertFalse(book.isAvailable()); // boolean default = false
    }

    @Test
    void fullConstructor_setsAllFields() {
        Book book = new Book(1L, "The Giver", "Lois Lowry", true);
        assertEquals(1L,          book.getId());
        assertEquals("The Giver", book.getTitle());
        assertEquals("Lois Lowry",book.getAuthor());
        assertTrue(book.isAvailable());
    }

    @Test
    void setters_mutateFieldsCorrectly() {
        Book book = new Book();
        book.setId(5L);
        book.setTitle("Wonder");
        book.setAuthor("R.J. Palacio");
        book.setAvailable(true);

        assertEquals(5L,          book.getId());
        assertEquals("Wonder",    book.getTitle());
        assertEquals("R.J. Palacio", book.getAuthor());
        assertTrue(book.isAvailable());
    }

    @Test
    void setAvailable_false_marksBookUnavailable() {
        Book book = new Book(1L, "Holes", "Louis Sachar", true);
        book.setAvailable(false);
        assertFalse(book.isAvailable());
    }

    @Test
    void toString_containsKeyFields() {
        Book book = new Book(2L, "Divergent", "Veronica Roth", false);
        String str = book.toString();
        assertTrue(str.contains("Divergent"));
        assertTrue(str.contains("false"));
    }
}