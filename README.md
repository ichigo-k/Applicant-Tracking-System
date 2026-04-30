# Applicant Tracking System - Aihrly Take-Home

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
