/**
 * TeenRead Hub – Borrow Module (Sprint 2)
 *
 * Responsibilities:
 *   - US3: Send POST /api/borrow/{bookId} when a user clicks Borrow
 *   - US3: Show the due date in a success message (US6)
 *   - US4: Fetch and render the "My Books" list
 *   - US6: Show clear success / error feedback for all borrow actions
 *
 * This module is intentionally separate from app.js so each file has
 * a single clear responsibility (Sprint 2 retro: smaller focused commits).
 *
 * Functions exposed to app.js via the global scope:
 *   handleBorrowClick(bookId, buttonElement)
 *   loadMyBooks()
 */

// ================================================================
// CONSTANTS
// ================================================================

/** Borrow API base URL */
const API_BORROW = '/api/borrow';

// ================================================================
// DOM REFERENCES  (My Books panel)
// ================================================================

const myBooksList    = document.getElementById('my-books-list');
const myBooksFeedback = document.getElementById('my-books-feedback');
const myBooksLoading = document.getElementById('my-books-loading');

// ================================================================
// UTILITY – My Books panel feedback
// ================================================================

/**
 * Displays a feedback message in the My Books panel (US6).
 *
 * @param {string} text - message content
 * @param {string} type - 'success' | 'warning' | 'error'
 */
function showMyBooksFeedback(text, type) {
    myBooksFeedback.classList.remove('success', 'warning', 'error', 'hidden');
    myBooksFeedback.textContent = text;
    myBooksFeedback.classList.add(type);
}

/** Hides the My Books feedback message */
function hideMyBooksFeedback() {
    myBooksFeedback.classList.remove('success', 'warning', 'error');
    myBooksFeedback.classList.add('hidden');
    myBooksFeedback.textContent = '';
}

// ================================================================
// BORROW ACTION  (US3)
// ================================================================

/**
 * Handles a click on a Borrow button.
 * Sends POST /api/borrow/{bookId} and processes the response.
 *
 * Called by app.js after borrow buttons are attached to the DOM.
 *
 * On success:
 *   - Disables the button and changes its label (prevents duplicate borrow)
 *   - Shows a success feedback message with the due date (US6)
 *   - Reloads the full book list so availability reflects the change
 *
 * On failure:
 *   - Shows an error feedback message (US6)
 *
 * @param {number} bookId         - id of the book to borrow
 * @param {HTMLButtonElement} btn - the Borrow button element (to disable it)
 */
async function handleBorrowClick(bookId, btn) {
    // Immediately disable the button to prevent double-clicks
    btn.disabled = true;
    btn.textContent = 'Borrowing…';

    try {
        const response = await fetch(`${API_BORROW}/${bookId}`, {
            method: 'POST',
            // No body needed – bookId is in the URL path
        });

        const data = await response.json();

        if (!response.ok) {
            // US6: show the error message from the server or a generic fallback
            showBrowseFeedback(data.message || 'Could not borrow this book.', 'error');
            // Re-enable the button so the user can try again
            btn.disabled = false;
            btn.textContent = 'Borrow';
            return;
        }

        // US6: success message includes the due date (US3 acceptance criteria)
        showBrowseFeedback(data.message, 'success');

        // Mark the button as permanently disabled with a "Borrowed" label
        btn.textContent = 'Borrowed ✓';
        btn.disabled = true;

        // Reload the catalogue so the availability badge updates on the card
        // loadAllBooks is defined in app.js which loads before borrow.js
        if (typeof loadAllBooks === 'function') {
            loadAllBooks();
        }

    } catch (error) {
        showBrowseFeedback('Borrow failed. Please check your connection.', 'error');
        btn.disabled = false;
        btn.textContent = 'Borrow';
        console.error('handleBorrowClick error:', error);
    }
}

/**
 * Proxy to show feedback in the Browse panel.
 * showFeedback is defined in app.js; accessed via the global scope.
 *
 * @param {string} text - message text
 * @param {string} type - 'success' | 'warning' | 'error'
 */
function showBrowseFeedback(text, type) {
    if (typeof showFeedback === 'function') {
        showFeedback(text, type);
    }
}

// ================================================================
// MY BOOKS – US4
// ================================================================

/**
 * Fetches all borrow records from GET /api/borrow/my-books.
 * Renders them as cards in the My Books panel.
 *
 * Called by app.js navigation when the "My Books" tab is clicked.
 * Also exposed globally so it can be triggered from other modules.
 */
async function loadMyBooks() {
    // Show loading, clear old content, hide stale feedback
    myBooksLoading.classList.remove('hidden');
    myBooksList.innerHTML = '';
    hideMyBooksFeedback();

    try {
        const response = await fetch(`${API_BORROW}/my-books`);
        if (!response.ok) throw new Error(`Server error: ${response.status}`);

        const borrowedBooks = await response.json();

        // Hide the loading indicator now data is ready
        myBooksLoading.classList.add('hidden');

        if (borrowedBooks.length === 0) {
            // US4: empty state – user has not borrowed any books yet
            myBooksList.innerHTML = `
                <div class="empty-state" role="status">
                    <div class="empty-state-icon">📂</div>
                    <p class="empty-state-text">
                        You have not borrowed any books yet. Go to Browse to get started!
                    </p>
                </div>
            `;
            return;
        }

        // Render one card per borrow record
        myBooksList.innerHTML = borrowedBooks.map(createMyBookCardHTML).join('');

        // US6: summary feedback
        showMyBooksFeedback(
            `You have ${borrowedBooks.length} borrowed book(s).`, 'success');

    } catch (error) {
        myBooksLoading.classList.add('hidden');
        showMyBooksFeedback('Could not load your books. Please try again.', 'error');
        console.error('loadMyBooks error:', error);
    }
}

/**
 * Builds the HTML for a single My Books card (US4).
 * Shows the book title, author, and due date prominently.
 *
 * @param {Object} record - { bookId, title, author, dueDate }
 * @returns {string} HTML string for the card
 */
function createMyBookCardHTML(record) {
    // Use escapeHTML from app.js (loaded before this module)
    const safeTitle  = typeof escapeHTML === 'function' ? escapeHTML(record.title)  : record.title;
    const safeAuthor = typeof escapeHTML === 'function' ? escapeHTML(record.author) : record.author;

    return `
        <article class="book-card" aria-label="Borrowed: ${safeTitle}">
            <div class="book-title">${safeTitle}</div>
            <div class="book-author">by ${safeAuthor}</div>
            <div class="book-availability">
                <span class="badge badge-unavailable">📖 Currently Reading</span>
            </div>
            <!-- US3 / US4: due date visible on each borrowed card -->
            <div class="due-date-label">
                📅 Due: ${record.dueDate}
            </div>
        </article>
    `;
}