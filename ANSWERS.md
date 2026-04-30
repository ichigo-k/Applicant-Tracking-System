# ANSWERS.md

## 1. Schema question

The main tables are `applications`, `application_notes`, `stage_history`, and `scores`. Each child table has an `application_id` foreign key pointing back to `applications`, and `application_notes` / `stage_history` also hold a reference to the `team_members` table for attribution.

Indexes are defined directly on the JPA entities using `@Index` inside `@Table`. `application_id` is indexed on `application_notes`, `stage_history`, and `scores` since every child table query filters by it. `job_id` is indexed on `applications` since listing applications per job is a core read path. Hibernate picks these up and creates them when the schema is generated.

For `GET /api/applications/{id}` the service makes roughly 3 round-trips: one to load the application with its notes and stage history, one to fetch the latest scores via the custom JPQL query in `ScoreRepository`, and Hibernate fires additional lazy fetches when the mapper resolves author/member names. It could be collapsed into one query with JOIN FETCH but it is fine at this scale.

---

## 2. Scoring design trade-off

### (a) Three endpoints vs one generic

Three separate endpoints are better when each score type might have different validation rules in the future, when the frontend submits them one at a time, or when you want the URL to be self-documenting. A single generic endpoint makes more sense if you want to submit all three in one call or if the types are truly identical and will stay that way. For a pipeline UI where recruiters score one dimension at a time, three endpoints felt more natural.

### (b) Score history

I already built it this way intentionally. Every submission inserts a new row rather than overwriting. On read, `findLatestScoresByApplicationId` picks the most recent row per type using a subquery. Score history is already in the database for free. If the product team wanted to expose it, it would just be a new read endpoint that returns all rows for an application without the latest-only filter. No schema change needed.

---

## 3. Debugging question

A recruiter says they moved a candidate to Interview but it still shows Screening.

- Check `stage_history` for that application ordered by `changed_at`. If the row is there, the write happened. If not, the request never completed.
- Check the `applications` table directly. If history has the row but the stage is still SCREENING, there is a transaction or commit issue.
- Check server logs for the PATCH request at that timestamp. A 200 with no exception means the service thought it succeeded.
- Ask the recruiter what the UI showed immediately after. If it briefly showed INTERVIEW and then reverted, that is a frontend caching issue, not a backend one.
- Check if another team member moved it back to SCREENING after the fact.
- Check for a double-click scenario where two requests fired. The second would have been an invalid transition returning 400.
- Reproduce it locally using the seed endpoint and verify the DB state matches the response.

---

## 4. Honest self-assessment

- **Java / Spring Boot**: 4/5 - The assessment asked for C# but I built this in Java which is where my strength is. I have no C# experience but I am actively open to learning it and see the transition as straightforward given how similar the ecosystems are.
- **SQL**: 3/5 - solid on queries and indexes, less experienced with migrations and query optimization at scale.
- **Git**: 4/5 - comfortable with branching, rebasing, and keeping commits focused.
- **REST API design**: 4/5 - think carefully about resource naming, status codes, and error shapes.
- **Writing tests**: 3/5 - can write meaningful unit and integration tests, still building the habit of writing them first.
