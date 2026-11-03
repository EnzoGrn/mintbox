# Architecture Decision Records

I use this folder to record the significant architecture decisions I make on Mintbox:
what I chose, why, and what it costs. The format follows
[Michael Nygard's template](https://cognitect.com/blog/2011/11/15/documenting-architecture-decisions).

Once I accept an ADR, I don't edit it. If I change my mind, I write a new ADR that
supersedes the old one, and I update the old one's status to point to it.

## Index

| # | Title | Status |
|---|-------|--------|
| [0001](0001-record-architecture-decisions.md) | Record architecture decisions | Accepted |
| [0002](0002-modular-monolith-with-spring-modulith.md) | Modular monolith with Spring Modulith | Accepted |
| [0003](0003-server-side-sessions-over-jwt.md) | Server-side sessions over JWT | Accepted |
| [0004](0004-postgresql-for-search.md) | PostgreSQL for search, no dedicated search engine | Accepted |
| [0005](0005-money-as-integer-minor-units.md) | Money as integer minor units | Accepted |
| [0006](0006-multi-product-line-catalog.md) | Multi-product-line catalog | Accepted |
| [0007](0007-versioned-catalog-snapshot.md) | Versioned catalog snapshot | Amended by 0011 |
| [0008](0008-s3-compatible-object-storage.md) | S3-compatible object storage | Accepted |
| [0009](0009-framework-free-domain-model.md) | Framework-free domain model | Accepted |
| [0010](0010-one-database-schema-per-module.md) | One database schema per module | Accepted |
| [0011](0011-amiibo-snapshot-as-source-of-truth.md) | Amiibo snapshot as the source of truth | Accepted |

## Template

```markdown
# NNNN. Short title

- **Status:** Proposed | Accepted | Superseded by [NNNN](NNNN-title.md)
- **Date:** YYYY-MM-DD

## Context

What problem am I facing, and what constraints do I have?

## Decision

What I decided to do.

## Consequences

What gets easier, what gets harder, and what I need to keep an eye on.

## Alternatives considered

What else I looked at, and why I didn't pick it.
```
