# Sprint 1 Sprint Backlog (Tasks)

## US1 – Browse Books (3 SP)

- Create Book model
- Implement in-memory `BookRepository` with seed data
- Implement `BookService.getAllBooks`
- Create `GET /api/books` controller endpoint
- Build basic UI (HTML/CSS) for book listing
- Write unit + controller tests

---

## US2 – Search Books (3 SP)

- Add `searchByTitle` to repository
- Implement service-level validation (null/blank)
- Create `GET /api/books/search` endpoint
- Add search bar to UI
- Handle empty results gracefully
- Write unit + controller tests

---

## US6 – Feedback Messages (2 SP)

- Add feedback container to UI with `aria-live`
- Implement success/error/warning styles
- Add JavaScript helpers to show/hide messages
- Integrate feedback into load/search flows  