#TicketSmart

Backend ticketing management system built with **Spring Boot 3.3**, **Java 17**, and **PostgreSQL 16**, following a classic layered architecture (`Controller` → `Service` → `Repository` → `Entity`).

Features **concurrency-safe atomic bookings**, **demand-based dynamic pricing**, **social graph connectivity via BFS**, and a **priority waitlist** featuring FIFO tie-breaking and automated cleanup upon promotion.

##Table of Contents

* [Tech Stack](#-tech-stack)

* [Architecture](#-architecture)

* [Data Model](#-data-model)

* [Core Features](#-core-features)

* [API Endpoints](#-api-endpoints)

* [Local Setup](#-local-setup)

* [Design Decisions & Trade-offs](#-design-decisions--trade-offs)

##Tech Stack

| Layer / Concern | Technology | 
 | ----- | ----- | 
| **Language** | Java 17 | 
| **Framework** | Spring Boot 3.3 (Web, Data JPA, Validation, Scheduling) | 
| **Database** | PostgreSQL 16 | 
| **ORM** | Hibernate (via Spring Data JPA) | 
| **Boilerplate** | Lombok | 
| **API Docs** | springdoc-openapi (Swagger UI) | 
| **Build Tool** | Maven | 

##Architecture

```
Controller  →  Service  →  Repository  →  PostgreSQL
   (HTTP)      (Biz Logic)    (JPA/SQL)

```

* **Controller**: HTTP transport layer, input/output mapping, zero business logic.

* **Service**: Core domain rules, validations, waitlist promotion flows, and scheduling tasks.

* **Repository**: Spring Data JPA interfaces & custom native/JPQL atomic queries.

* **Entity vs DTO**: Strict separation. Entities talk to DB; DTOs expose safe API payloads (preventing password/internal graph leakage).

* **Global Error Handling**: Centralized `GlobalExceptionHandler` converts `RuntimeException` into clean JSON responses (`{"message": "..."}`) with appropriate HTTP status codes.

##Data Model

| Entity | Role | 
 | ----- | ----- | 
| **User** | System users (`HOST` or `VIEWER`) | 
| **Event** | Events with available ticket counters, base prices, and host reference | 
| **Reservation** | Booking records: user, event, status (`PENDING`, `CONFIRMED`, `EXPIRED`, `CANCELLED`), and locked price at booking time | 
| **Friendship** | Symmetric social graph edges between users | 
| **WaitListEntry** | Waitlist entries tracking user, event, and join timestamp (`createdAt`) | 

> **Core Design Choice**: Tickets are *not* standalone entities; they are managed via a counter (`Event.availableTickets`). A `Reservation` tracks individual booking attempts, state transitions, and price locking.

##Core Features

1. **Concurrency-Safe Booking (`tryReserveTicket`)**:
   Skips heavy pessimistic/optimistic locks. Executes an atomic conditional UPDATE:

   ```
   UPDATE events SET available_tickets = available_tickets - 1 
   WHERE id = :eventId AND available_tickets > 0;
   
   ```

   If 0 rows are affected, it fails fast without thread-blocking.

2. **Social Connectivity Check (`isConnected` via BFS)**:
   `FriendshipService.isConnected(userId1, userId2, maxDepth)` builds an adjacency list (`Map<Long, List<Long>>`) on-the-fly and runs bounded BFS for symmetric friendships.

3. **Priority Waitlist with FIFO Tie-Breaking**:
   Score formula:
   

   $$
   \text{score} = (\text{wait seconds} / 10) + (\text{confirmed friends count} \times 20)
   $$

   * **FIFO Tie-Breaking**: Sorted by higher score DESC, then earliest registration (`createdAt`) ASC.

   * **Dynamic Cleanup**: Computed live per query; entries are cleaned up (`removeFromWaitList`) immediately upon promotion to a freed reservation slot.

4. **Dynamic Pricing (Strategy Pattern)**:
   `DemandBasedPricingStrategy` applies a +30% surge if available ticket capacity drops below 10%. Price locks permanently on `Reservation.lockedPrice`.

5. **Expired Reservation Sweeper (`@Scheduled`)**:
   Runs every minute, transitions overdue `PENDING` bookings to `EXPIRED`, and either promotes the top waitlist candidate (consuming the freed slot into a new `PENDING` reservation + cleanup) or returns the ticket to general availability if the queue is empty.

##API Endpoints

| Method | Endpoint | Description | 
 | ----- | ----- | ----- | 
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
| `GET` | `/api/waitlist/score` | Get score for a specific user/event | 

**Interactive API Docs (Swagger UI)**: `http://localhost:8081/swagger-ui/index.html`

##Local Setup

1. Create a PostgreSQL database named `ticketing_db`:

   ```
   CREATE DATABASE ticketing_db;
   
   ```

2. Configure `src/main/resources/application.properties`:

   ```
   spring.datasource.url=jdbc:postgresql://localhost:5432/ticketing_db
   spring.datasource.username=postgres
   spring.datasource.password=your_password
   spring.jpa.hibernate.ddl-auto=update
   server.port=8081
   
   ```

3. Run the app:

   ```
   mvn spring-boot:run
   
   ```

4. Access Swagger UI at `http://localhost:8081/swagger-ui/index.html`.

## ⚖️ Design Decisions & Trade-offs

* **Atomic UPDATE vs. Explicit Locks**: Scales significantly better under high concurrent request volume without blocking thread pools.

* **Live Score Evaluation vs. PriorityQueue**: Heaps suffer from stale priority issues when social graph edges change. For event waitlists (dozens/hundreds of entries), $O(n \log n)$ sorting on query is fast and consistent.

* **Friend Bonus Weight (+20)**: Calibrated so social connectivity offers a meaningful boost without completely starving long-waiting users (FIFO fairness).

* **Confirmed Lock Rule**: `CONFIRMED` reservations are finalized to protect organizer revenue; refunds/cancellations for confirmed tickets require a separate dedicated workflow.
