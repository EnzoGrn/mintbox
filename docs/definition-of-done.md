# Definition of done

A change is done when every item below that applies to it is true. The pull request template
repeats this list as a checklist.

## Code

- `./mvnw verify` passes locally, and the CI is green on the pull request.
- Business rules are covered by unit tests on the domain, without Spring or a database. A
  test for a numbered rule (`LST-3`, `ORD-5`...) says so in its name or a comment.
- Persistence and queries are covered by integration tests against a real PostgreSQL
  (Testcontainers), never an in-memory substitute.
- Every HTTP endpoint is tested through MockMvc, including its error cases (400, 404...),
  which return RFC 9457 problem details.
- Module boundaries and domain purity tests pass (`ModularityTest`, `DomainPurityTest`).
- No `TODO` without a linked issue.

## Database

- Schema changes go through a new Flyway migration, in the module's folder. A migration that
  has been merged is never edited.
- An index is added with the query that needs it, with a comment saying which query.

## Documentation

- The [domain model](domain/) is updated when a rule, a state or an event changes.
- An [ADR](adr/) records any decision that is expensive to undo, affects several modules, or
  would surprise someone reading the code. An ADR that turns out wrong is amended by a new
  one, never rewritten.
- The API documentation is updated when an endpoint is added or changed.
- READMEs still describe how to run the project.

## Operations

- Business operations log one meaningful line at `INFO` (for example, a catalog
  synchronization and its counts). Errors are logged once, where they are handled.
- Logs never contain secrets or personal data (emails, addresses, payment details).
