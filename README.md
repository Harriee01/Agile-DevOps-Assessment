# Agile-DevOps-Assessment


# TeenRead Hub

A simple, safe e-library for teenagers built with Spring Boot 3 and vanilla JavaScript.

## Tech Stack

- **Backend:** Java 17, Spring Boot 3, Maven
- **Frontend:** HTML5, CSS3, JavaScript (fetch API)
- **Storage:** In-memory List (no database)
- **No authentication, no external services**

## Sprint 1 Features

- US1: Browse a list of available books
- US2: Search books by title
- US6: Clear user feedback messages

## Running the Application

### Prerequisites

- Java 17+
- Maven 3.8+

### Steps
```bash
# Clone the repository
git clone https://github.com/your-org/teenread-hub.git
cd teenread-hub

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

## API Endpoints (Sprint 1)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /api/books | Returns all books |
| GET | /api/books/search?keyword=… | Search books by title |

## CI/CD

Pipeline runs on every push: build → test → package.
