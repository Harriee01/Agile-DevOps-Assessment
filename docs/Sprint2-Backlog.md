# Sprint 2 Sprint Backlog (Tasks)

## US3 – Borrow Book (5 SP)

- Write `BorrowService` tests (happy path + guards)
- Create `BorrowedBook` model
- Implement `BorrowedBookRepository`
- Extend `BookRepository` with `findById` and `save`
- Implement `BorrowService.borrowBook`
- Add `POST /api/borrow/{bookId}` controller endpoint
- Add global exception handling
- Add **Borrow** button to UI and JavaScript handling

---

## US4 – My Books (3 SP)

- Write tests for `getMyBooks` service
- Implement `BorrowService.getMyBooks`
- Add `GET /api/borrow/my-books` endpoint
- Create **My Books** UI panel
- Add frontend lazy-loading logic

---

## US5 – Recommendations (2 SP)

- Write `RecommendationService` tests first
- Implement recommendation logic with a cap
- Add `GET /api/recommendations` endpoint
- Create recommendations UI section
- Handle empty and partial recommendation cases  