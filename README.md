# Ticketing System

## Project overview

This project is a backend ticketing system built with Spring Boot 3 (Java 21) that simulates a real-world high-demand event reservation platform.

The architecture was designed with a strong focus on:

- Concurrency control
- Data consistency
- Clean separation of responsibilities

## Project objective

The main objective of this project is to design and implement a robust booking workflow capable of handling concurrent seat reservations safely and efficiently.

The system aims to:

- Prevent double-booking of seats under high concurrency
- Integrate external payment providers (Stripe)
- Implement temporary seat locking using Redis
- Capture performance and concurrency metrics for analysis

This project serves as a practical exploration of:

- Transaction management
- Distributed locking strategies
- High-concurrency backend design

## Main problem solved

The core problem addressed by this system is:

How to safely handle multiple users attempting to reserve the same seat at the same time without creating inconsistent data or duplicate bookings.

Without proper concurrency control, a booking system may suffer from:

- Double reservations
- Deadlocks
- Inconsistent seat states
- Race conditions

This project solves the problem using:

- Redis-based temporary seat locking
- Database-level integrity constraints

## Database

The system uses PostgreSQL as the primary relational database.

Why PostgreSQL?

PostgreSQL was selected because it provides:

- Strong ACID guarantees
- Reliable transactional behavior
- Foreign key constraints and data integrity enforcement

## Redis

Although PostgreSQL ensures transactional integrity, it is not sufficient for temporary seat reservation control.

Redis is used as an in-memory locking layer to:

- Temporarily hold seats during checkout
- Prevent race conditions
- Reduce contention on the database

## Payment Integration

The system integrates with Stripe Checkout for payment processing.

# Ticketing - Usage Guide

This document explains how to run the API locally and where to find Swagger.

## Prerequisites

- Java 21
- PostgreSQL
- Redis
- (Optional) Stripe CLI, if you want to test webhooks locally

## Configuration (Environment Variables)

The application reads its configuration from environment variables (see the file named .env-example).

Minimum required to start the app:

- DB_URL
  - Example: jdbc:postgresql://localhost:5432/ticketing
- DB_USERNAME
  - Example: spring_user
- DB_PASSWORD
  - Example: spring_pass
- DB_SCHEMA
  - Example: spring_schema
- SECRET_KEY
  - JWT signing key (if not set, a default value is used)

Redis (for seat holds/locks):

- REDIS_HOST
  - Example: localhost
- REDIS_PORT
  - Example: 6379

Stripe (needed to use checkout and webhooks):

- STRIPE_SECRET_KEY
- STRIPE_WEBHOOK_SECRET
- FRONTEND_URL
  - Example: http://localhost:3000

## Run the application

From the application/ folder:

- Run in dev mode
  ./gradlew bootRun

- Build the JAR
  ./gradlew bootJar

- Run the JAR
  java -jar build/libs/\*.jar

By default, the API runs at http://localhost:8080.

## Swagger / OpenAPI

Swagger is enabled and accessible without authentication.

- Swagger UI:
  - http://localhost:8080/swagger-ui.html
  - If your setup redirects, try: http://localhost:8080/swagger-ui/index.html

- OpenAPI JSON:
  - http://localhost:8080/v3/api-docs

## Quick Auth (JWT)

Public endpoints (no token required):

- Login: POST /api/v1/login
- Register:
  - POST /api/v1/users/reg/client
  - POST /api/v1/users/reg/organizer

After login, the response returns a token. Use it in requests to protected endpoints using the Authorization header:

    Authorization: Bearer Your_Token

## Stripe webhooks (local testing)

The webhook endpoint is exposed without auth at:

- POST /webhook

You can forward Stripe events to your machine using Stripe CLI:

    stripe listen --forward-to localhost:8080/webhook

Stripe CLI will print a secret that starts with whsec\_. Copy that value and set it as STRIPE_WEBHOOK_SECRET.

To simulate events with Stripe CLI:

    stripe trigger payment_intent.succeeded
    stripe trigger payment_intent.payment_failed

# Load Testing - Usage Guide

This module contains Gatling load tests for the Ticketing API.

## Prerequisites

- Java (JDK) installed
- The Ticketing API running and reachable
- A database with data that matches the test inputs (showId, seatIds, user credentials)

Optional:

- Stripe is not required to run the load test, but your API must be able to respond to the checkout endpoint.

## What this load test does

The main simulation logs in and then immediately starts a checkout.

Flow:

1. POST /api/v1/login
2. POST /api/v1/checkout/ with the JWT token (Authorization: Bearer ...)

The goal is to simulate many authenticated users starting checkout concurrently and verify there are no concurrency issues.

## Important files to review before running

1. Simulation

- src/gatling/java/simulations/LoadTestSimulation.java
  - Base URL is currently set to http://localhost:8080
  - Injection is currently set to 1000 users at once

2. Request bodies

- src/gatling/resources/login.json
  - Must contain a valid email and password that exist in your database

- src/gatling/resources/start_checkout.json
  - Must contain a showId and seatIds that exist and are available for booking

If your API is not running on localhost:8080, update the baseUrl inside LoadTestSimulation.

## Run the simulations

From the load-testing/ folder:

- Run the main load test
  ./gradlew gatlingRun --simulation simulations.LoadTestSimulation

- Run all simulations (sequentially)
  ./gradlew gatlingRun --all

Notes:

- If you change the number of users, edit LoadTestSimulation and update atOnceUsers(1000).
- If you want to run without interactive prompts, add:
  ./gradlew gatlingRun --non-interactive --simulation simulations.LoadTestSimulation

## View the reports

After a run finishes, Gatling generates an HTML report.

Report location:

- build/reports/gatling/<run-folder>/index.html

Open the index.html file in your browser.

## Troubleshooting

- 401 Unauthorized on login
  - Update src/gatling/resources/login.json with correct credentials

- 400 or 404 on checkout
  - Verify the API is running
  - Verify the endpoint path is correct (/api/v1/checkout/)
  - Update src/gatling/resources/start_checkout.json with valid showId and seatIds

- Lots of failed requests due to seat conflicts
  - This can be expected if all virtual users request the same seatIds
  - Consider generating different seatIds per user
