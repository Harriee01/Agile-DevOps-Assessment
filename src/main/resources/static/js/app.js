/**
 * TeenRead Hub – Frontend Application (Sprint 1)
 *
 * Responsibilities:
 *  - US1: Fetch and render the full book catalogue on page load
 *  - US2: Handle search input and display filtered results
 *  - US6: Show clear feedback messages for all user actions
 *
 * Uses the native fetch() API – no frameworks, no build tools.
 * All DOM manipulation is done via standard Web APIs.
 */

// ================================================================
// CONSTANTS
// ================================================================

/** Base URL of the REST API served by Spring Boot on the same origin */
const API_BASE = '/api/books';

// ================================================================
// DOM ELEMENT REFERENCES
// Grab elements once and reuse – avoids repeated getElementById calls
// ================================================================

/** The text input where the user types a search keyword */
const searchInput = document.getElementById('search-input');

/** The "Search" button */
const searchBtn = document.getElementById('search-btn');

/** The "Clear" button that resets search state */
const clearBtn = document.getElementById('clear-btn');

/** The div that displays success / warning / error feedback (US6) */
const feedbackMessage = document.getElementById('feedback-message');

/** The container div that holds the book cards (US1 / US2) */
const bookList = document.getElementById('book-list');

/** The loading spinner shown while data is being fetched */
const loadingIndicator = document.getElementById('loading-indicator');

// ================================================================
// UTILITY FUNCTIONS
// ================================================================

/**
 * Shows the loading indicator and hides the book grid.
 * Called before any fetch() request is fired.
 */
function showLoading() {
    loadingIndicator.classList.remove('hidden');
    bookList.innerHTML = '';        // Clear any existing cards
}

/**
 * Hides the loading indicator.
 * Called after the fetch() response has been processed.
 */
function hideLoading() {
    loadingIndicator.classList.add('hidden');
}

/**
 * Displays a user-facing feedback message (US6).
 *
 * @param {string} text     - The message text to display
 * @param {string} type     - One of: 'success' | 'warning' | 'error'
 */
function showFeedback(text, type) {
    // Remove all type classes first to avoid stale styling
    feedbackMessage.classList.remove('success', 'warning', 'error', 'hidden');

    // Set the message text
    feedbackMessage.textContent = text;

    // Apply the appropriate colour class ('success', 'warning', or 'error')
    feedbackMessage.classList.add(type);
}

/**
 * Hides the feedback message area.
 * Called when the user starts a new interaction or clears the search.
 */
function hideFeedback() {
    feedbackMessage.classList.remove('success', 'warning', 'error');
    feedbackMessage.classList.add('hidden');
    feedbackMessage.textContent = '';
}

/**
 * Builds the HTML for a single book card (US1).
 *
 * @param {Object} book - Book object from the API
 *   { id, title, author, available }
 * @returns {string} HTML string for one book card
 */
function createBookCardHTML(book) {
    // Determine availability badge class and label
    const badgeClass = book.available ? 'badge-available' : 'badge-unavailable';
    const badgeText  = book.available ? '✅ Available'    : '❌ Borrowed';

    // Return the card HTML using a template literal
    return `
        <article class="book-card" aria-label="Book: ${escapeHTML(book.title)}">
            <div class="book-title">${escapeHTML(book.title)}</div>
            <div class="book-author">by ${escapeHTML(book.author)}</div>
            <div class="book-availability">
                <span class="badge ${badgeClass}">${badgeText}</span>
            </div>
        </article>
    `;
}

/**
 * Renders a list of books into the #book-list grid.
 * If the list is empty, shows an empty-state placeholder.
 *
 * @param {Array} books - Array of book objects from the API
 */
function renderBooks(books) {
    // Handle empty results (US2: "No results found" in DOM)
    if (!books || books.length === 0) {
        bookList.innerHTML = `
            <div class="empty-state" role="status">
                <div class="empty-state-icon">📭</div>
                <p class="empty-state-text">No books found.</p>
            </div>
        `;
        return;
    }

    // Build one HTML string from all cards and inject in one operation
    // (one innerHTML assignment is faster than many appendChild calls)
    bookList.innerHTML = books.map(createBookCardHTML).join('');
}

/**
 * Escapes HTML special characters in a string to prevent XSS injection.
 * Called before inserting any API-returned string into the DOM.
 *
 * @param {string} str - Raw string that may contain HTML characters
 * @returns {string}   - Safely escaped string
 */
function escapeHTML(str) {
    // Use the browser's own text node to do the escaping
    const div = document.createElement('div');
    div.appendChild(document.createTextNode(str));
    return div.innerHTML;
}

// ================================================================
// API CALLS
// ================================================================

/**
 * US1 – Fetches the full book catalogue from GET /api/books.
 * Renders all books and shows a feedback message on error.
 */
async function loadAllBooks() {
    showLoading();
    hideFeedback();

    try {
        // Fetch all books from the Spring Boot REST endpoint
        const response = await fetch(API_BASE);

        // If the server returned a non-2xx status, treat it as an error
        if (!response.ok) {
            throw new Error(`Server error: ${response.status}`);
        }

        // Parse the JSON array of book objects
        const books = await response.json();

        // Render the books into the DOM
        renderBooks(books);

        // US6: subtle success feedback (catalogue loaded)
        showFeedback(`${books.length} book(s) in the catalogue.`, 'success');

    } catch (error) {
        // Network failure or unexpected server error
        hideLoading();
        showFeedback('Could not load books. Please refresh the page.', 'error');
        console.error('loadAllBooks error:', error);
    } finally {
        // Always hide the spinner, even if an error occurred
        hideLoading();
    }
}

/**
 * US2 – Searches books by title keyword via GET /api/books/search?keyword=…
 * Renders matching results and shows a feedback message (US6).
 *
 * @param {string} keyword - The search term entered by the user
 */
async function searchBooks(keyword) {
    // Trim whitespace before checking emptiness
    const trimmed = keyword.trim();

    // US6: validate locally before sending the request
    if (!trimmed) {
        showFeedback('Please enter a search term before clicking Search.', 'error');
        return;
    }

    showLoading();

    try {
        // Encode keyword to handle spaces and special chars safely in the URL
        const url = `${API_BASE}/search?keyword=${encodeURIComponent(trimmed)}`;
        const response = await fetch(url);

        // Parse the response body regardless of status code
        const data = await response.json();

        if (!response.ok) {
            // Server returned 400 Bad Request – show the server's message
            showFeedback(data.message || 'Invalid search request.', 'error');
            renderBooks([]);
            return;
        }

        // Render the search results (may be an empty array)
        renderBooks(data.results);

        // US6: show the feedback message returned by the server
        // - "Found X book(s) matching '…'" for hits
        // - "No results found for '…'" for misses
        const feedbackType = data.results.length > 0 ? 'success' : 'warning';
        showFeedback(data.message, feedbackType);

    } catch (error) {
        // Network failure
        showFeedback('Search failed. Please check your connection.', 'error');
        console.error('searchBooks error:', error);
    } finally {
        hideLoading();
    }
}

// ================================================================
// EVENT LISTENERS
// ================================================================

/**
 * "Search" button click handler.
 * Reads the current value of the search input and triggers the API call.
 */
searchBtn.addEventListener('click', () => {
    searchBooks(searchInput.value);
});

/**
 * Press Enter inside the search input – same as clicking Search.
 * Improves UX so users don't have to reach for the mouse.
 */
searchInput.addEventListener('keydown', (event) => {
    if (event.key === 'Enter') {
        searchBooks(searchInput.value);
    }
});

/**
 * "Clear" button click handler.
 * Resets the search field, hides feedback, and reloads the full catalogue.
 */
clearBtn.addEventListener('click', () => {
    searchInput.value = '';     // Clear the text field
    hideFeedback();             // Remove any visible feedback message
    loadAllBooks();             // Reload the complete book list (US1)
});

// ================================================================
// INITIALISATION
// ================================================================

/**
 * On DOMContentLoaded the page fetches and displays all books immediately.
 * This satisfies US1: "Book list is visible on the homepage".
 *
 * DOMContentLoaded fires after the HTML is parsed but before images load –
 * the ideal moment to run initialisation JavaScript.
 */
document.addEventListener('DOMContentLoaded', () => {
    loadAllBooks();
});