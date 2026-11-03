# Backend

Spring Boot application, structured as a **modular monolith** with Spring Modulith (see
[ADR 0002](../docs/adr/0002-modular-monolith-with-spring-modulith.md)).

## Requirements

- JDK 25
- Docker, running: the integration tests start their own PostgreSQL with Testcontainers,
  and the application uses the one from `compose.yaml`

Maven doesn't need to be installed: the Maven wrapper (`mvnw`) downloads the right version.

## Commands

Run from this folder. On Windows, use `.\mvnw.cmd` instead of `./mvnw`.

| Command | What it does |
|---------|--------------|
| `./mvnw verify` | Compiles, runs every test and checks the module boundaries |
| `./mvnw spring-boot:run` | Starts the API on <http://localhost:8080>. Run `docker compose up -d` at the repository root first |

Once the API is running, <http://localhost:8080/actuator/health> should answer
`{"status":"UP"}`.

## Code layout

```
io.github.enzogrn.mintbox
├── MintboxApplication     Entry point
├── shared                 Types every module needs (Money, typed identifiers, AggregateRoot)
└── catalog                Public API of the catalog module (events)
    ├── domain             Aggregates, value objects, ports (repository, catalog source): plain Java
    ├── application        Use cases (synchronization) and read queries with their views
    └── infrastructure
        ├── persistence    JPA for writes, SQL with JdbcClient for reads
        ├── snapshot       Reads the catalog snapshot, imports it at startup
        └── web            REST controller
```

Writes go through the domain model; reads use SQL queries returning views
([ADR 0012](../docs/adr/0012-read-queries-bypass-the-domain-model.md)).

## HTTP API

| Endpoint | Returns |
|----------|---------|
| `GET /api/catalog/entries` | One page of entries sorted by name. Filters: `productLine`, `series`, `franchise`, `figureType`. Paging: `limit` (1 to 100, default 24) and `cursor` |
| `GET /api/catalog/entries/{id}` | One entry, with its release dates |
| `GET /api/catalog/series` | Every series with its number of entries. Filters: `productLine`, `figureType` |

To get the next page, pass the `nextCursor` of a response back as `cursor`, with the same
filters. Errors are [RFC 9457](https://www.rfc-editor.org/rfc/rfc9457) problem details
(`application/problem+json`).

```bash
curl "http://localhost:8080/api/catalog/entries?series=Super%20Smash%20Bros.&limit=3"
curl "http://localhost:8080/api/catalog/series?figureType=FIGURE"
```

At startup, the application synchronizes the catalog with
`src/main/resources/catalog/amiibo.json`
([ADR 0011](../docs/adr/0011-amiibo-snapshot-as-source-of-truth.md)). The first start imports
about 900 entries; the next ones find nothing to change.

Database migrations live in `src/main/resources/db/migration/<module>/`, and each module
has its own PostgreSQL schema ([ADR 0010](../docs/adr/0010-one-database-schema-per-module.md)).

Each business module described in the [domain model](../docs/domain/modules.md) becomes a
direct sub-package of `io.github.enzogrn.mintbox` as it is built. Inside a module, the
`domain`, `application` and `infrastructure` packages have distinct roles, described in
[ADR 0009](../docs/adr/0009-framework-free-domain-model.md). A test fails the build if a
domain package depends on a framework.

`./mvnw verify` also generates diagrams of the modules, from the actual code, in
`target/spring-modulith-docs`.
