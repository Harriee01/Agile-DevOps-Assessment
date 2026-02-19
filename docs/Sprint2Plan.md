# Sprint 2 Plan

## Sprint Goal

Complete interactive and personalized features (borrow, my books, recommendations) while improving quality through test-first development and smaller commits, enforced by CI/CD.

---

## Sprint 2 User Story Selection

| ID  | User Story | Estimate (SP) | Rationale |
|-----|------------|---------------|-----------|
| **US3** | Borrow a book with due date | **5** | Highest business value; core interaction |
| **US4** | View my borrowed books | **3** | Natural follow-up to borrowing |
| **US5** | View recommendations | **2** | Nice-to-have discovery feature |

**Total Sprint 2 Load: 10 Story Points**

---

## Sprint 2 Definition of Done (DoD)

- Tests written before feature code (TDD-style)
- All acceptance criteria satisfied
- Borrow rules enforced (not found, already borrowed, null input)
- UI actions disable appropriately to prevent double actions
- Unit tests cover happy paths and edge cases
- Controller tests validate HTTP status codes and responses
- CI/CD pipeline blocks failing logic before merge
- No regression failures from Sprint 1 functionality
- Commit history shows small, focused, logical commits  