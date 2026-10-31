# 0004. PostgreSQL for search, no dedicated search engine

- **Status:** Accepted
- **Date:** 2026-10-30

## Context

Search needs to handle:

- text search on catalog entries and listings that tolerates typos ("pikachuu",
  "zelda botw"),
- filters on series, character, type, condition, price, region and delivery method,
- a radius search around the user's location,
- sorting by relevance, price, date and distance, with cursor pagination.

The catalog has around a thousand entries. Even in a small real deployment, there would be
a few tens of thousands of active listings at most.

## Decision

I implement search in **PostgreSQL**, which already holds the data:

- **Full-text search** with `tsvector` columns and GIN indexes for word-based relevance.
- **`pg_trgm`** trigram similarity for typo tolerance and autocompletion.
- **PostGIS** (`geography` columns with a GiST index) for radius filtering and sorting by
  distance.
- Plain B-tree indexes for structured filters and keyset (cursor) pagination.

Each module hides its search queries behind its own query interface, so I can swap the
implementation without touching the callers.

## Consequences

- I have no second datastore to run, sync, back up or pay for.
- Search results always match the data: no indexing delay, no reindexing jobs.
- Ranking is less sophisticated than with a dedicated engine (no learned relevance, limited
  facets). That's fine for a finite catalog with structured attributes.
- I need to check query performance with `EXPLAIN ANALYZE` on realistic volumes, which the
  demo data seeder will provide.

## Alternatives considered

- **Elasticsearch or OpenSearch:** the best relevance and facets, but a heavy JVM service
  that would eat most of the hosting budget, plus a sync pipeline (dual writes or change
  data capture) to keep it consistent.
- **Meilisearch or Typesense:** lighter, with great typo tolerance, but still a second
  datastore to keep in sync. I'll reconsider them only if PostgreSQL turns out not to be
  enough.
