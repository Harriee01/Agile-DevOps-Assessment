# Sprint 1 Planning

## Sprint Goal

Deliver a usable read-only **TeenRead Hub** that allows teenagers to browse books, search by title, and clearly understand system feedback, with a stable CI/CD pipeline.

---

## Sprint 1 User Story Selection

| ID  | User Story | Estimate (SP) | Rationale |
|-----|------------|---------------|-----------|
| **US1** | Browse available books | **3** | Core foundation; required for all other features |
| **US2** | Search books by title | **3** | Enhances usability; depends on US1 data |
| **US6** | Clear feedback messages | **2** | Cross-cutting UX support for all actions |

**Total Sprint 1 Load: 8 Story Points**  


---

## Sprint 1 Definition of Done (DoD)

- Acceptance criteria for each story met
- REST endpoints implemented and reachable
- UI renders correctly in browser (no blocking errors)
- Unit tests written for services and repositories
- Controller tests written using MockMvc
- All tests passing locally and in CI
- No critical or high-severity defects open
- CI/CD pipeline passes build, test, and package stages
- README updated if user-facing behavior changes  