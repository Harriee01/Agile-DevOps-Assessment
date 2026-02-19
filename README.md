# Agile-DevOps-Assessment


# TeenRead Hub

A simple, safe e-library for teenagers built with Spring Boot 3 and vanilla JavaScript.

## Tech Stack

- **Backend:** Java 21, Spring Boot 3, Maven
- **Frontend:** HTML5, CSS3, JavaScript (fetch API)
- **Storage:** In-memory List (no database)
- **No authentication, no external services**

## Features

### Sprint 1

- US1: Browse a list of available books
- US2: Search books by title
- US6: Clear user feedback messages


### Sprint 2

- US3: Borrow a book and receive a due date
- US4: View borrowed books ("My Books" section)
- US5: See simple book recommendations
- Monitoring: /health endpoint + console logging for borrow actions

## Sprint 2 Retrospective Improvements Applied

1. **Increased test coverage**: Edge cases added before every feature merge
2. **Smaller focused commits**: Each commit touches one logical change only


## Running the Application

### Prerequisites

- Java 21+
- Maven 3.8+

### Steps
```bash
# Clone the repository
git clone https://github.com/Harriee01/Agile-DevOps-Assessment.git
cd Agile-DevOps-Assessment

# Build the project
mvn clean package

# Run the application
mvn spring-boot:run
```

Open your browser at: [http://localhost:8080](http://localhost:8080)

## Running Tests
```bash
mvn test
```

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /api/books | Returns all books (US1) |
| GET | /api/books/search?keyword=… | Search books by title (US2) |
| POST | /api/borrow/{bookId} | Borrow a book (US3) |
| GET | /api/borrow/my-books | List all borrowed books (US4) |
| GET | /api/recommendations | Get recommended books (US5) |
| GET | /health | Application health status |

## CI/CD Pipeline

Stages: **build → test → package**
Pipeline blocks on any failing test. All 70 commits follow
conventional commit format: `type(scope): description`.
