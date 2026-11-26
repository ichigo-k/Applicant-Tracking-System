# Applicant Tracking System

A REST API for running a hiring pipeline. Teams post jobs, candidates apply, and recruiters move each application through a strict stage workflow, adding notes and scores along the way. Moving an application to `HIRED` or `REJECTED` triggers an async notification.

Built with Java 21, Spring Boot, Spring Data JPA and PostgreSQL. Originally written as a backend take-home for Aihrly.

## Highlights

- **Enforced stage machine.** Each stage decides which moves are legal, so illegal jumps are rejected:

  ```
  APPLIED → SCREENING → INTERVIEW → OFFER → HIRED
      └──────────┴───────────┴─────────┴──→ REJECTED
  ```

- **Full stage history** for every application.
- **Recruiter notes** and three separate scores: culture fit, interview and assessment.
- **Async notifications** using `@Async`. Reaching a terminal stage writes a row to `notifications` without blocking the request.
- **Team-member attribution** through the `X-Team-Member-Id` header.
- **One-call seed endpoint** that creates sample team members, jobs and applications.
- **Swagger UI**, DTOs mapped with MapStruct, validation, central exception handling, and unit and integration tests (H2 + Mockito).

## API

| Method | Path | Description |
| --- | --- | --- |
| `POST` | `/api/jobs` | Create a job |
| `GET` | `/api/jobs` | List jobs |
| `GET` | `/api/jobs/{id}` | Get a job |
| `POST` | `/api/jobs/{jobId}/applications` | Apply to a job |
| `GET` | `/api/jobs/{jobId}/applications` | List applications for a job |
| `GET` | `/api/applications/{id}` | Get an application with its history |
| `PATCH` | `/api/applications/{id}/stage` | Move to the next stage |
| `POST` / `GET` | `/api/applications/{id}/notes` | Add or list notes |
| `PUT` | `/api/applications/{id}/scores/{culture-fit,interview,assessment}` | Set a score |
| `GET` | `/api/seed` | Load sample data |

Full interactive docs are at `/swagger-ui.html`.

---

## Running locally

### With Docker 

Make sure Docker is running, then:

```bash
docker compose up --build
```

That starts Postgres and the app together. The API will be available at `http://localhost:8080`.

### Without Docker

You'll need a local Postgres instance. Update the connection details in `application.properties` or set these environment variables:

```bash
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/ats
SPRING_DATASOURCE_USERNAME=ats_user
SPRING_DATASOURCE_PASSWORD=ats_password
```

Then run:

```bash
./mvnw spring-boot:run
```

---

## Running the tests

```bash
./mvnw verify
```


---

## Seeding data

Hit this endpoint once the app is running:

```
GET http://localhost:8080/api/seed
```

It creates 3 team members, 4 jobs, and 5 applications with notes, scores, and stage history. The response includes all the IDs you need to start testing other endpoints right away.

---

## API docs

Swagger UI is available at:

```
http://localhost:8080/swagger-ui.html
```

---

## Assumptions

- The seed endpoint is additive — calling it multiple times creates fresh records each time.


- `ddl-auto` is set to `create-drop` in both the main and test configs. The schema is recreated on every startup and dropped on shutdown, keeping the database in a clean state throughout development and testing. In production this would be switched to `validate`.

---

## Part 2 - Deep Dive

I went with Option A (background job). When an application moves to `HIRED` or `REJECTED`, a notification is dispatched asynchronously using Spring's `@Async`. It logs the event, inserts a row into the `notifications` table with the application ID, email, type, and sent timestamp, and does not block the PATCH response.

---

## What I'd improve with more time

- Idempotent seeding: skip if data already exists
- Pagination on notes and stage history
- Soft deletes on jobs and applications
- Move the `X-Team-Member-Id` resolution into a filter/interceptor so services don't have to repeat the lookup
