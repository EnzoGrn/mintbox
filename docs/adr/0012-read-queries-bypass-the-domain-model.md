# 0012. Read queries bypass the domain model

- **Status:** Accepted
- **Date:** 2026-11-03

## Context

The domain model exists to protect business rules when something changes: an aggregate is
loaded entirely, checked, modified and saved (see
[ADR 0009](0009-framework-free-domain-model.md)).

Most requests, however, only read: browse the catalog, show an entry, list series with
their size. Going through aggregates for these would load more than needed (every field,
every release date), map it twice (table to JPA entity to domain object), then map it a third
time into the shape the page needs. Aggregates also can't express what reads need most:
sorting, filtering, pagination, counts.

## Decision

- **Writes go through the domain model**: application services load aggregates through
  their repository, call their methods, and save them.
- **Reads don't**: each module exposes query interfaces (for the catalog,
  `CatalogQueries`) that return **views**, records shaped for what the client displays.
- **Queries are implemented in plain SQL with Spring's `JdbcClient`**, in the module's
  infrastructure. Each query selects only the columns its view needs.
- **Both sides use the same tables.** This is a light form of CQRS: separate code paths for
  reading and writing, but no separate read database and no synchronization between them.
- **Pagination is by cursor (keyset)**, not by page number: a page starts right after the
  last row of the previous one, using an index on the sort columns.

## Consequences

- Reads are as fast as the SQL can be, and the SQL is visible and reviewable, with the index
  that supports it next to it in a migration.
- The domain model stays focused on rules; it never grows getters or query methods just for
  display.
- A change to a table may need to be reflected in two places: the JPA entity and the SQL
  queries. Integration tests run both against the real schema, so a mismatch fails the build.
- Cursor pagination can't jump to "page 7". The catalog is browsed by scrolling or "load
  more", so that's not a loss, and it stays fast and stable while entries are added.

## Alternatives considered

- **Reads through the aggregates and their repository:** one code path, but slow and
  awkward for lists, and it pushes display concerns into the domain.
- **Spring Data JPA projections:** less SQL to write, but dynamic filters and keyset
  pagination end up in JPQL strings or the Criteria API, which are harder to read than SQL.
- **A separate read store** (a search index, materialized views): only worth it when reads
  and writes need different shapes or scales, which isn't the case yet.
- **Offset pagination** (`LIMIT 24 OFFSET 480`): allows jumping to a page, but PostgreSQL
  still reads and discards every skipped row, and an entry added meanwhile shifts every page.
