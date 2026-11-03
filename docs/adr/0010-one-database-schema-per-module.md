# 0010. One database schema per module

- **Status:** Accepted
- **Date:** 2026-11-03

## Context

[ADR 0002](0002-modular-monolith-with-spring-modulith.md) states that each module owns its
tables and that no module reads another module's tables. Spring Modulith checks the
boundaries in the Java code, but nothing checks them in the database: with every table in
the same `public` schema, ownership is only a naming convention.

## Decision

- **Each module has its own PostgreSQL schema**, named after the module: `catalog`,
  `listing`, `ordering`... Its tables live there, for example `catalog.catalog_entry`.
- **Each module has its own folder of Flyway migrations**, under
  `src/main/resources/db/migration/<module>/`. Flyway runs them all, in version order, with
  a single history table in the `public` schema.
- **Migration versions are global**: `V1`, `V2`... across all modules, since Flyway needs a
  single order. The file name says which module it belongs to through its folder.
- **The JPA entities name their schema explicitly** (`@Table(schema = "catalog")`).

## Consequences

- Who owns a table is visible in the database itself, in SQL queries and in any database
  tool.
- A query joining two modules' tables stands out immediately (`catalog.x JOIN listing.y`),
  in code review as well as in logs.
- If a module is ever extracted into its own service, its schema can move to its own
  database as is.
- Later, I can give each module its own database user, with access to its schema only.
  That would turn the convention into a guarantee, at the cost of one connection pool per
  module, so I'm not doing it now.

## Alternatives considered

- **Everything in `public`, with a table prefix per module** (`catalog_entry`,
  `listing_listing`): works, but ownership remains a convention, and prefixes get awkward.
- **One database per module:** the strongest isolation, but cross-module consistency
  (the checkout, see the [aggregates](../domain/aggregates.md#the-checkout-exception)) would
  need distributed transactions, which ADR 0002 rules out.
