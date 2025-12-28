# Final Project REST API

This repository contains a Spring Boot 3 application for an online store. It exposes JWT-secured endpoints for managing users, roles, products, categories, reviews, orders, and order items.

## Project structure
- **Spring Boot** application code under `src/main/java`
- **Liquibase** database migrations under `src/main/resources/db/changelog`
- **Configuration** profiles in `src/main/resources`
- **Postman** request collection at `src/postman_collection.json`

## Prerequisites
- Java 17 (the Gradle wrapper will download Gradle automatically)
- PostgreSQL 16 or compatible
- Docker (optional, for containerized runs)

## Environment variables
Copy `.env.example` to `.env` and adjust values as needed:
```bash
cp .env.example .env
```
Key variables include database connection details and JWT settings (`JWT_SECRET` must be at least 32 characters). The default application port is `8000`.

## Running locally
1. Start PostgreSQL and create the database specified in `.env` (default `online_store`).
2. Apply migrations automatically by starting the app; Liquibase runs at startup.
3. Launch the application:
```bash
./gradlew bootRun
```
The API will be available at http://localhost:8000.

## Running with Docker Compose
1. Ensure `.env` is populated (see above).
2. Build and start the app and database containers:
```bash
docker compose up --build
```
The API will be exposed on the port defined by `SERVER_PORT` in `.env` (defaults to 8000).

## Running tests
Execute the test suite with:
```bash
./gradlew test
```

## API testing
Import `src/postman_collection.json` into Postman to explore the available endpoints and example requests.
