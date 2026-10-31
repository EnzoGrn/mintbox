# 0002. Modular monolith with Spring Modulith

- **Status:** Accepted
- **Date:** 2026-10-30

## Context

Mintbox covers several business areas: catalog, accounts, collection, listings, offers,
messaging, orders, payments, reviews and notifications. Each has its own rules and evolves
at its own pace, so I want clear boundaries between them.

On the other hand, I build and run the project alone, on a hosting budget of a few euros a
month. There's no team to scale and no need to deploy parts independently.

## Decision

I build the backend as a single Spring Boot application, organized as a **modular
monolith**:

- Each business area gets its own top-level package (`catalog`, `listing`, `order`...).
  It exposes a small public API and hides everything else in an `internal` sub-package.
- I use **Spring Modulith** to check these boundaries in a test
  (`ApplicationModules.verify()`). If a module reaches into another one's internals, the
  build fails.
- When a module needs data from another, it calls that module's public API. When it needs
  to trigger a side effect, it publishes a **domain event** instead (for example,
  `OrderPaid` triggers a notification and schedules the seller's payout).
- I persist events with Spring Modulith's **event publication registry** (JDBC). It works
  as a transactional outbox: if the transaction commits, the event gets delivered, even if
  the application crashes before the listener runs.

## Consequences

- I get one build, one deployment and one database, and I can use transactions within a
  module.
- Tests enforce the boundaries instead of network calls, so refactoring stays cheap while
  the domain model is still moving.
- If I ever need to extract a module into its own service, it's already talking through
  events. Notifications and image processing are the obvious candidates.
- It still takes discipline: with a shared database, cross-module joins are tempting. Each
  module owns its tables, and no module reads another module's tables.

## Alternatives considered

- **Microservices:** distributed transactions, service discovery, several deployments and
  higher hosting costs, for no benefit at this scale.
- **A classic layered monolith** (`controller`, `service`, `repository` packages): simple
  at first, but business areas get tangled and the boundaries can't be checked.
- **A Maven multi-module build:** it enforces boundaries at compile time, but makes the
  build more complex. Spring Modulith gives me similar guarantees, plus event support and
  generated module documentation, in a single Maven module.
