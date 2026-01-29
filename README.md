# Flashcards Learning Platform - Backend

REST API backend for a flashcards learning web application built with Spring Boot.

## Tech Stack

- **Java 21**
- **Spring Boot 3.5.6**
- **PostgreSQL 16**
- **Spring Security + JWT**
- **Spring Data JPA**
- **Gradle 8.11**

## Quick Start with Docker

The easiest way to run the application:

```bash
docker-compose up --build
```

This will start:
- PostgreSQL database on port `5432`
- Backend API on port `8080`

The API will be available at `http://localhost:8080`

## Local Development

### Prerequisites

- Java 21
- PostgreSQL 16
- Gradle 8.11+

### Setup

1. Clone the repository

2. Create `.env` file (copy from `.env.example`):
```bash
cp .env.example .env
```

3. Configure your `.env` file with your database credentials

4. Run the application:
```bash
./gradlew bootRun
```

## Configuration

### Environment Variables

| Variable | Description | Example |
|----------|-------------|---------|
| `SPRING_DATASOURCE_URL` | Database connection URL | `jdbc:postgresql://localhost:5432/flashcards` |
| `SPRING_DATASOURCE_USERNAME` | Database username | `postgres` |
| `SPRING_DATASOURCE_PASSWORD` | Database password | `your_password` |
| `JWT_SECRET_KEY` | JWT signing key (base64, min 256 bits) | `your_secret_key` |
| `SUPPORT_EMAIL` | Gmail address for sending emails | `your_email@gmail.com` |
| `APP_PASSWORD` | Gmail app password | `your_app_password` |
| `CSRF_SECURE_COOKIE` | CSRF cookie secure flag | `false` (dev) / `true` (prod) |

## API Documentation

### Authentication

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/signup` | Register new user |
| POST | `/api/auth/login` | Login (returns JWT cookie) |
| POST | `/api/auth/verify` | Verify email with code |
| POST | `/api/auth/resend` | Resend verification email |
| POST | `/api/auth/logout` | Logout (clears JWT cookie) |

### Decks

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/decks` | Get user's decks (paginated) |
| GET | `/api/decks/public` | Get public decks (paginated) |
| GET | `/api/decks/{id}` | Get deck details |
| POST | `/api/decks` | Create new deck |
| PUT | `/api/decks/{id}` | Update deck |
| DELETE | `/api/decks/{id}` | Delete deck |

#### Query Parameters for GET /api/decks

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `page` | int | 0 | Page number (0-indexed) |
| `size` | int | 9 | Items per page |
| `sortBy` | string | createdAt | Sort field: `name`, `createdAt`, `averageDifficulty` |
| `sortDir` | string | desc | Sort direction: `asc`, `desc` |
| `isPublic` | boolean | - | Filter by visibility |

#### Query Parameters for GET /api/decks/public

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `page` | int | 0 | Page number (0-indexed) |
| `size` | int | 9 | Items per page |
| `sortBy` | string | createdAt | Sort field: `name`, `createdAt`, `cardCount` |
| `sortDir` | string | desc | Sort direction: `asc`, `desc` |
| `sizeFilter` | string | - | Filter: `small` (1-20), `medium` (21-60), `large` (61+), `empty` (0) |
| `minCards` | int | - | Minimum card count |

### Cards

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/decks/{deckId}/cards` | Get all cards in deck |
| GET | `/api/decks/{deckId}/cards/{cardId}` | Get single card |
| POST | `/api/decks/{deckId}/cards` | Create card |
| PUT | `/api/decks/{deckId}/cards/{cardId}` | Update card |
| DELETE | `/api/decks/{deckId}/cards/{cardId}` | Delete card |

### Quick Learn

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/decks/{deckId}/learn/start` | Start learning session |
| POST | `/api/decks/{deckId}/learn/submit` | Submit answers and get stats |

## Response Format

### Paginated Response

```json
{
  "content": [...],
  "page": 0,
  "size": 9,
  "totalElements": 45,
  "totalPages": 5,
  "first": true,
  "last": false
}
```

### Deck Response

```json
{
  "id": 1,
  "name": "Java Basics",
  "isPublic": true,
  "createdAt": "2024-01-15T10:30:00",
  "cardCount": 25,
  "ownerUsername": "john_doe",
  "averageDifficulty": 2.4
}
```

## Security

- **JWT Authentication**: Tokens stored in HttpOnly secure cookies
- **CSRF Protection**: Cookie-based CSRF tokens for SPA
- **Password Encryption**: BCrypt with strength 10
- **Email Verification**: 6-digit code sent to email

## License

MIT
