# TicketSmart

Backend ticketing management system built with **Spring Boot 3.3**, **Java 17**, and **PostgreSQL 16**, following a classic layered architecture (`Controller` → `Service` → `Repository` → `Entity`).

## Tech Stack
* **Language**: Java 17
* **Framework**: Spring Boot 3.3 (Web, Data JPA, Validation, Scheduling)
* **Database**: PostgreSQL 16
* **ORM**: Hibernate (via Spring Data JPA)
* **Boilerplate**: Lombok
* **API Docs**: springdoc-openapi (Swagger UI)
* **Build Tool**: Maven

## Architecture
Controller  →  Service  →  Repository  →  PostgreSQL<br>
(HTTP)  (Biz Logic)  (JPA/SQL)

* **Controller**: HTTP transport layer, input/output mapping, zero business logic.
* **Service**: Core domain rules, validations, waitlist promotion flows, and scheduling tasks.
* **Repository**: Spring Data JPA interfaces & custom native/JPQL atomic queries.
* **Entity vs DTO**: Strict separation. Entities talk to DB; DTOs expose safe API payloads.
* **Global Error Handling**: Centralized `GlobalExceptionHandler` converts `RuntimeException` into clean JSON responses.

## Data Model
* **User**: System users (`HOST` or `VIEWER`)
* **Event**: Events with available ticket counters, base prices, and host reference
* **Reservation**: Booking records: user, event, status (`PENDING`, `CONFIRMED`, `EXPIRED`, `CANCELLED`), locked price
* **Friendship**: Symmetric social graph edges between users
* **WaitListEntry**: Waitlist entries tracking user, event, and join timestamp (`createdAt`)

## Core Features
1. **Concurrency-Safe Booking (`tryReserveTicket`)**:
   ```sql
   UPDATE events SET available_tickets = available_tickets - 1 
   WHERE id = :eventId AND available_tickets > 0;
1. Social Connectivity Check (isConnected via BFS): Bounded BFS over symmetric friendship adjacency list.<br>
2. Priority Waitlist with FIFO Tie-Breaking: Score formula score = (wait seconds / 10) + (confirmed friends count * 20), sorted by score DESC then createdAt ASC.<br>
3. Dynamic Pricing: +30% surge if available capacity drops below 10%, locked on reservation.<br>
4. Expired Reservation Sweeper (@Scheduled): Moves overdue PENDING to EXPIRED and promotes top waitlist candidate or returns ticket to pool.<br>

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/api/users` | Create user |
| `GET` | `/api/users/{id}` | Get user details |
| `GET` | `/api/users` | List all users |
| `POST` | `/api/events` | Create event |
| `GET` | `/api/events/{id}` | Get event details |
| `GET` | `/api/events` | List all events |
| `POST` | `/api/reservations` | Concurrency-safe ticket reservation |
| `PUT` | `/api/reservations/{id}/confirm` | Confirm payment |
| `PUT` | `/api/reservations/{id}/cancel` | Cancel reservation |
| `GET` | `/api/reservations/user/{userId}` | Get user reservations |
| `POST` | `/api/friendships` | Create friendship edge |
| `GET` | `/api/friendships/connected` | Check social connectivity (BFS) |
| `POST` | `/api/waitlist` | Join waitlist |
| `DELETE` | `/api/waitlist` | Leave waitlist |
| `GET` | `/api/waitlist/event/{eventId}` | Get waitlist sorted by priority score |
| `GET` | `/api/waitlist/score` | Get score for specific user/event |

Swagger UI: http://localhost:8081/swagger-ui/index.html<br>

Local Setup<br>
1. Create DB: CREATE DATABASE ticketing_db;<br>
2. Configure application.properties:<br>
spring.datasource.url=jdbc:postgresql://localhost:5432/ticketing_db
spring.datasource.username=postgres
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
server.port=8081<br>
3. Run: mvn spring-boot:run

Design Decisions & Trade-offs<br>
* Atomic UPDATE vs Explicit Locks: Better throughput under high concurrency.<br>
* Live Score Evaluation vs PriorityQueue: Avoids stale heap state on graph updates.<br>
* Friend Bonus (+20): Balances social perks with FIFO fairness.<br>
* Confirmed Lock Rule: Protects revenue; separate flow for refunds.
