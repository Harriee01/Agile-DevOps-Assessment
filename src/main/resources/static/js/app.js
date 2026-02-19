/**
 * TeenRead Hub – Core Frontend Module (Sprint 1 + Sprint 2)
 *
 * Responsibilities:
 *   - US1: Fetch and render the full book catalogue on page load
 *   - US2: Handle search input and display filtered results
 *   - US6: Show clear feedback messages for browsing/search actions
 *   - Navigation: switch between Browse, My Books, and Recommendations panels
 *
 * Sprint 2 change: Borrow button added to each book card.
 * Borrow action logic lives in borrow.js (separation of concerns).
 */

// ================================================================
// CONSTANTS
// ================================================================

/** Base URL for the books REST API */
const API_BOOKS = '/api/books';

// ================================================================
// DOM REFERENCES – resolved once for performance
// ================================================================

const searchInput     = document.getElementById('search-input');
const searchBtn       = document.getElementById('search-btn');
const clearBtn        = document.getElementById('clear-btn');
const feedbackMessage = document.getElementById('feedback-message');
const bookList        = document.getElementById('book-list');
const loadingIndicator = document.getElementById('loading-indicator');

// Navigation buttons (NodeList → Array for easy iteration)
const navButtons = Array.from(document.querySelectorAll('.nav-btn'));

// All panel sections
const panels = Array.from(document.querySelectorAll('.panel'));

// ================================================================
// NAVIGATION
// ================================================================

/**
 * Shows the panel whose id matches targetPanelId and hides all others.
 * Updates the active state on the nav buttons.
 *
 * @param {string} targetPanelId - id of the panel to show
 */
function showPanel(targetPanelId) {
    panels.forEach(panel => {
        // Show the target panel; hide everything else
        if (panel.id === targetPanelId) {
            panel.classList.remove('hidden');
        } else {
            panel.classList.add('hidden');
        }
    });

    // Sync active styling on nav buttons
    navButtons.forEach(btn => {
        btn.classList.toggle('active', btn.dataset.panel === targetPanelId);
    });
}

/**
 * Attaches click listeners to every nav button.
 * Each button's data-panel attribute determines which panel to reveal.
 */
function initNavigation() {
    navButtons.forEach(btn => {
        btn.addEventListener('click', () => {
            const targetPanel = btn.dataset.panel;
            showPanel(targetPanel);

            // Lazily load content when a panel is first visited
            if (targetPanel === 'my-books-panel') {
                // borrow.js exposes loadMyBooks globally
                if (typeof loadMyBooks === 'function') loadMyBooks();
            }
            if (targetPanel === 'recommendations-panel') {
                // recommendations.js exposes loadRecommendations globally
                if (typeof loadRecommendations === 'function') loadRecommendations();
            }
        });
    });
}

// ================================================================
// UTILITY FUNCTIONS
// ================================================================

/** Shows the loading spinner and clears the book grid */
function showLoading() {
    loadingIndicator.classList.remove('hidden');
    bookList.innerHTML = '';
}

/** Hides the loading spinner */
function hideLoading() {
    loadingIndicator.classList.add('hidden');
}

/**
 * Displays a feedback message to the user (US6).
 *
 * @param {string} text - message content
 * @param {string} type - 'success' | 'warning' | 'error'
 */
function showFeedback(text, type) {
    feedbackMessage.classList.remove('success', 'warning', 'error', 'hidden');
    feedbackMessage.textContent = text;
    feedbackMessage.classList.add(type);
}

/** Hides and clears the feedback message area */
function hideFeedback() {
    feedbackMessage.classList.remove('success', 'warning', 'error');
    feedbackMessage.classList.add('hidden');
    feedbackMessage.textContent = '';
}

/**
 * Escapes special HTML characters to prevent XSS.
 * Called before inserting any API-derived string into the DOM.
 *
 * @param {string} str - raw string
 * @returns {string} safely escaped string
 */
function escapeHTML(str) {
    const div = document.createElement('div');
    div.appendChild(document.createTextNode(str));
    return div.innerHTML;
}

// ================================================================
// BOOK CARD RENDERING  (US1)
// ================================================================

/**
 * Builds the HTML string for one book card.
 * Includes a Borrow button that triggers borrow.js when clicked (US3).
 *
 * @param {Object} book - { id, title, author, available }
 * @returns {string} HTML string for the card
 */
function createBookCardHTML(book) {
    const badgeClass = book.available ? 'badge-available' : 'badge-unavailable';
    const badgeText  = book.available ? '✅ Available'    : '❌ Borrowed';

    // Borrow button is disabled when the book is already borrowed
    const borrowDisabled = book.available ? '' : 'disabled';
    const borrowLabel    = book.available ? 'Borrow' : 'Unavailable';

    return `
        <article class="book-card" aria-label="Book: ${escapeHTML(book.title)}">
            <div class="book-title">${escapeHTML(book.title)}</div>
            <div class="book-author">by ${escapeHTML(book.author)}</div>
            <div class="book-availability">
                <span class="badge ${badgeClass}">${badgeText}</span>
            </div>
            <button
                class="btn btn-borrow"
                data-book-id="${book.id}"
                ${borrowDisabled}
                aria-label="Borrow ${escapeHTML(book.title)}">
                ${borrowLabel}
            </button>
        </article>
    `;
}

/**
 * Renders an array of books into the #book-list grid.
 * Shows an empty-state placeholder if the array is empty.
 *
 * @param {Array} books - array of book objects from the API
 */
function renderBooks(books) {
    if (!books || books.length === 0) {
        bookList.innerHTML = `
            <div class="empty-state" role="status">
                <div class="empty-state-icon">📭</div>
                <p class="empty-state-text">No books found.</p>
            </div>
        `;
        return;
    }
    // One innerHTML write is faster than many appendChild calls
    bookList.innerHTML = books.map(createBookCardHTML).join('');

    // Attach borrow click handlers after injecting cards into the DOM
    // borrow.js must be loaded before app.js uses this function
    attachBorrowHandlers();
}

// ================================================================
// BORROW HANDLER ATTACHMENT
// (actual borrow logic lives in borrow.js – see that file)
// ================================================================

/**
 * Finds all Borrow buttons in the current book grid and attaches
 * click listeners that delegate to borrow.js handleBorrowClick().
 *
 * Called after every renderBooks() invocation.
 */
function attachBorrowHandlers() {
    document.querySelectorAll('.btn-borrow:not([disabled])').forEach(btn => {
        btn.addEventListener('click', () => {
            const bookId = parseInt(btn.dataset.bookId, 10);
            // handleBorrowClick is defined in borrow.js
            if (typeof handleBorrowClick === 'function') {
                handleBorrowClick(bookId, btn);
            }
        });
    });
}

// ================================================================
// API CALLS
// ================================================================

/**
 * US1 – Fetches the full book catalogue (GET /api/books).
 * Called on page load and after a "Clear" search.
 */
async function loadAllBooks() {
    showLoading();
    hideFeedback();

    try {
        const response = await fetch(API_BOOKS);
        if (!response.ok) throw new Error(`Server error: ${response.status}`);

        const books = await response.json();
        renderBooks(books);

        // US6: quiet success feedback showing total catalogue size
        showFeedback(`${books.length} book(s) in the catalogue.`, 'success');
    } catch (error) {
        showFeedback('Could not load books. Please refresh the page.', 'error');
        console.error('loadAllBooks error:', error);
    } finally {
        hideLoading();
    }
}

/**
 * US2 – Searches books by title (GET /api/books/search?keyword=…).
 * Trims the keyword locally before sending the request.
 *
 * @param {string} keyword - raw value from the search input
 */
async function searchBooks(keyword) {
    const trimmed = keyword.trim();

    // US6: client-side validation before the API call
    if (!trimmed) {
        showFeedback('Please enter a search term before clicking Search.', 'error');
        return;
    }

    showLoading();

    try {
        const url      = `${API_BOOKS}/search?keyword=${encodeURIComponent(trimmed)}`;
        const response = await fetch(url);
        const data     = await response.json();

        if (!response.ok) {
            showFeedback(data.message || 'Invalid search request.', 'error');
            renderBooks([]);
            return;
        }

        renderBooks(data.results);

        // US6: display the server-provided message (success or warning)
        const feedbackType = data.results.length > 0 ? 'success' : 'warning';
        showFeedback(data.message, feedbackType);
    } catch (error) {
        showFeedback('Search failed. Please check your connection.', 'error');
        console.error('searchBooks error:', error);
    } finally {
        hideLoading();
    }
}

// ================================================================
// EVENT LISTENERS
// ================================================================

// Search button click
searchBtn.addEventListener('click', () => searchBooks(searchInput.value));

// Enter key inside search input
searchInput.addEventListener('keydown', e => {
    if (e.key === 'Enter') searchBooks(searchInput.value);
});

// Clear button: reset input, hide feedback, reload full catalogue
clearBtn.addEventListener('click', () => {
    searchInput.value = '';
    hideFeedback();
    loadAllBooks();
});

// ================================================================
// INITIALISATION
// ================================================================

document.addEventListener('DOMContentLoaded', () => {
    initNavigation();   // Wire up nav tab buttons
    loadAllBooks();     // US1: load catalogue immediately on page open
});