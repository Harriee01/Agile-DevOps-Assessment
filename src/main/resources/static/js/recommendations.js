/**
 * TeenRead Hub – Recommendations Module (Sprint 2)
 *
 * Responsibility:
 *   - US5: Fetch and render book recommendations from GET /api/recommendations
 *   - Shows at least 3 recommended books (or fewer if catalogue is small)
 *   - Does NOT block core browse/borrow functionality (US5 AC)
 *
 * Exposed globally:
 *   loadRecommendations()  – called by app.js when the "Picks for You" tab opens
 */

// ================================================================
// CONSTANTS
// ================================================================

/** Recommendations REST endpoint */
const API_RECOMMENDATIONS = '/api/recommendations';

// ================================================================
// DOM REFERENCES
// ================================================================

const recList    = document.getElementById('rec-list');
const recLoading = document.getElementById('rec-loading');

// ================================================================
// MAIN FUNCTION  (US5)
// ================================================================

/**
 * Fetches recommended books and renders them into the Recommendations panel.
 *
 * On success: renders up to 3 gold-accented book cards.
 * On empty:   shows a friendly "check back later" empty state.
 * On error:   shows an error message but does NOT throw (must not block core UI).
 */
async function loadRecommendations() {
    // Show loading indicator and clear any previous results
    recLoading.classList.remove('hidden');
    recList.innerHTML = '';

    try {
        const response = await fetch(API_RECOMMENDATIONS);

        // Non-2xx response – treat as error but handle gracefully
        if (!response.ok) throw new Error(`Server error: ${response.status}`);

        const books = await response.json();

        // Always hide the spinner once data arrives
        recLoading.classList.add('hidden');

        if (!books || books.length === 0) {
            // US5 edge case: no available books to recommend
            recList.innerHTML = `
                <div class="empty-state" role="status">
                    <div class="empty-state-icon">🔍</div>
                    <p class="empty-state-text">
                        No recommendations right now. Check back after returning some books!
                    </p>
                </div>
            `;
            return;
        }

        // Render each recommendation as a gold-accented card
        recList.innerHTML = books.map(createRecommendationCardHTML).join('');

    } catch (error) {
        // US5 AC: error must NOT block core functionality
        recLoading.classList.add('hidden');
        recList.innerHTML = `
            <div class="empty-state" role="status">
                <div class="empty-state-icon">⚠️</div>
                <p class="empty-state-text">
                    Could not load recommendations. Browse the full catalogue instead.
                </p>
            </div>
        `;
        console.error('loadRecommendations error:', error);
    }
}

/**
 * Builds the HTML for a single recommendation card (US5).
 * Uses a gold accent stripe (rec-card class) to distinguish from browse cards.
 *
 * @param {Object} book - { id, title, author, available }
 * @returns {string} HTML for one recommendation card
 */
function createRecommendationCardHTML(book) {
    // Reuse escapeHTML from app.js if available
    const safeTitle  = typeof escapeHTML === 'function' ? escapeHTML(book.title)  : book.title;
    const safeAuthor = typeof escapeHTML === 'function' ? escapeHTML(book.author) : book.author;

    return `
        <article class="book-card rec-card" aria-label="Recommended: ${safeTitle}">
            <div class="book-title">🌟 ${safeTitle}</div>
            <div class="book-author">by ${safeAuthor}</div>
            <div class="book-availability">
                <span class="badge badge-available">✅ Available</span>
            </div>
            <!-- Prompt the user to go borrow this recommended book -->
            <p style="font-size:0.82rem;color:#718096;margin-top:0.4rem;">
                Go to Browse to borrow this book!
            </p>
        </article>
    `;
}