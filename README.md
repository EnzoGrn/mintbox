# Mintbox

[![Backend](https://github.com/EnzoGrn/mintbox/actions/workflows/backend.yml/badge.svg)](https://github.com/EnzoGrn/mintbox/actions/workflows/backend.yml)

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
| Storage | S3-compatible object storage (SeaweedFS locally) |
| Frontend | Next.js, TypeScript, Tailwind CSS |
| Payments | Stripe Connect (test mode) |
| Testing | JUnit 5, AssertJ, Testcontainers, ArchUnit, Playwright |
| Infrastructure | Docker Compose, GitHub Actions |

The reasoning behind each significant choice is recorded in
[Architecture Decision Records](docs/adr/).

## Documentation

- [Architecture](docs/architecture/): system context and containers (C4 model).
- [Domain model](docs/domain/): glossary, modules, aggregates, state machines and events.
- [Architecture Decision Records](docs/adr/): what I decided, and why.

## Repository layout

```
.
├── compose.yaml   Local development stack
├── backend/       Spring Boot application (modular monolith)
├── frontend/      Next.js web application
├── infra/         Service configuration, deployment and operations
└── docs/          Architecture, domain model, decision records
```

## Getting started

Requirements: Docker with Docker Compose.

```bash
docker compose up
```

For now, this starts the infrastructure: PostgreSQL, S3-compatible storage and a local
mail catcher (web UI at <http://localhost:8025>). The backend and the frontend will join
the same command as they are built.

Default settings work out of the box. To change a port or a password, copy `.env.example`
to `.env` and edit it.

## Credits

The amiibo catalog data comes from [AmiiboAPI](https://github.com/N3evin/AmiiboAPI)
(MIT license, © 2017 Nevin Vu), converted with [`tools/catalog`](tools/catalog/).

## License

[MIT](LICENSE)
