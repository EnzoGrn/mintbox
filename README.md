# Mintbox

> A marketplace and collection tracker for toys-to-life collectible figures.

**Mintbox** is a portfolio project: a peer-to-peer marketplace where collectors can track
their collection, build a wishlist, and buy or sell figures with each other.

The name comes from *mint in box*, the collectors' term for a figure that is still new in
its original packaging.

The catalog is designed to hold several product lines of NFC figures. The first one
supported is amiibo; others, such as Skylanders, can be added without changing the rest
of the domain.

It is built as a **modular monolith** in Java / Spring Boot, with a strong focus on
**domain-driven design**, explicit architecture boundaries, and testing.

> [!NOTE]
> **Disclaimer:** Mintbox is an independent, non-commercial project. It is not affiliated
> with, endorsed by, or sponsored by Nintendo, Activision, or any other manufacturer. All
> product names, characters and images referenced in the catalog are the property of their
> respective owners.

## Status

🚧 **Early development.** No feature is usable yet. See the [roadmap](#roadmap).

## Why this project

General-purpose marketplaces treat every item as free text. Collectible figures, however,
come from a **finite, well-known catalog**. Mintbox takes advantage of that:

- **Catalog first:** every listing is attached to a catalog entry, so search, prices and
  statistics are reliable.
- **Collection first, marketplace second:** collectors come to track what they own and
  what they are missing. Selling a duplicate is one click away.
- **Market data:** price history and "fair price" indicators derived from real sales.

## Roadmap

Each milestone ends with something that can be demonstrated.

| # | Milestone | Demo |
|---|-----------|------|
| 0 | Foundations | Repository, domain model, architecture decisions, local stack |
| 1 | Catalog | Browse and search the full catalog, typo-tolerant |
| 2 | Accounts & collection | Sign up, track a collection, series progress, wishlist |
| 3 | Listings | Create a listing from the catalog, photos, filtered search |
| 4 | Offers & messaging | Make, counter and accept offers; chat per listing |
| 5 | Checkout | Reservation, escrow payment (Stripe test mode), order lifecycle, reviews |
| 6 | Public demo | Moderation, notifications, demo data, periodic reset, deployment |

## Tech stack

| Area | Choice |
|------|--------|
| Backend | Java 25, Spring Boot, Spring Modulith, Spring Security, Spring Data JPA |
| Database | PostgreSQL (full-text search, `pg_trgm`, PostGIS), Flyway |
| Storage | S3-compatible object storage (MinIO locally) |
| Frontend | Next.js, TypeScript, Tailwind CSS |
| Payments | Stripe Connect (test mode) |
| Testing | JUnit 5, AssertJ, Testcontainers, ArchUnit, Playwright |
| Infrastructure | Docker Compose, GitHub Actions |

The reasoning behind each significant choice is recorded in
[Architecture Decision Records](docs/adr/).

## Documentation

- [Domain model](docs/domain/): glossary, modules, aggregates, state machines and events.
- [Architecture Decision Records](docs/adr/): what I decided, and why.

## Repository layout

```
.
├── backend/    Spring Boot application (modular monolith)
├── frontend/   Next.js web application
├── infra/      Docker Compose, deployment and operations
└── docs/       Architecture, domain model, decision records
```

## Getting started

_Coming with milestone 0._ The goal is a single command to run the whole stack locally:

```bash
docker compose up
```

## License

[MIT](LICENSE)
