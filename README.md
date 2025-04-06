# GitHub Repository Fetcher API

A Spring Boot application that allows users to retrieve non-fork GitHub repositories with their branch information for a specified GitHub username.

## Features

- Fetch all non-fork repositories for a given GitHub username
- Get detailed branch information including the latest commit SHA for each repository
- Parallel processing of repository requests for improved performance
- Error handling for various scenarios (user not found, rate limiting, etc.)
- API documentation with Swagger/OpenAPI

## Tech Stack

- Java 21
- Spring Boot 3.4.4
- Spring Web MVC
- SpringDoc OpenAPI UI 2.8.6
- Maven
- JUnit
- Mockito

## Getting Started

### Building the Application

```bash
./mvnw clean install
```

### Running the Application

```bash
./mvnw spring-boot:run
```

The application will start on port 8080 by default.

## API Reference

### Endpoints

#### Get User Repositories

```
GET /api/v1/users/{username}/repositories
```

Retrieves all non-fork repositories with their branches for the specified GitHub username.

**Path Parameters:**
- `username` - GitHub username

**Success Response:**
- **Code:** 200 OK
- **Content:** 
```json
[
    {
        "repositoryName": "repo-name",
        "ownerLogin": "username",
        "branches": [
            {
                "name": "main",
                "commit": {
                    "sha": "commit-sha"
                }
            }
        ]
    }
]
```

**Error Responses:**
- **Code:** 400 BAD REQUEST
    - **Content:** `{ "status": 400, "message": "Username cannot be null or empty" }`
- **Code:** 404 NOT FOUND
    - **Content:** `{ "status": 404, "message": "User not found: (username)" }`
- **Code:** 403 FORBIDDEN
    - **Content:** `{ "status": 403, "message": "Rate limit exceeded. Please try again later" }`
- **Code:** 500 INTERNAL SERVER ERROR
    - **Content:** `{ "status": 500, "message": "Internal server error." }`


## API Documentation

Once the application is running, you can access the Swagger UI at:

```
http://localhost:8080/swagger-ui.html
```

## Architecture

The application follows a clean architecture approach with the following layers:

- **API Layer** - Controllers, DTOs, and exception handlers
- **Infrastructure Layer** - Services, clients, and configuration
- **Domain Layer** - Models and business logic
- **Application Layer** - Use cases and application services
- **CQRS** - Command Query Responsibility Segregation for handling commands and queries separately

## Testing

Run the tests using:

```bash
./mvnw test
```
Implements integration tests and mocking Github API service.

## Error Handling

The application includes error handling for:
- User not found
- GitHub API rate limiting
- Generic application errors (Global handler)