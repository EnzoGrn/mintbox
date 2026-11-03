# Backend

Spring Boot application, structured as a **modular monolith** with Spring Modulith (see
[ADR 0002](../docs/adr/0002-modular-monolith-with-spring-modulith.md)).

## Requirements

- JDK 25
- Docker, for the local infrastructure and, later, the integration tests

Maven doesn't need to be installed: the Maven wrapper (`mvnw`) downloads the right version.

## Commands

Run from this folder. On Windows, use `.\mvnw.cmd` instead of `./mvnw`.

| Command | What it does |
|---------|--------------|
| `./mvnw verify` | Compiles, runs every test and checks the module boundaries |
| `./mvnw spring-boot:run` | Starts the API on <http://localhost:8080> |

Once the API is running, <http://localhost:8080/actuator/health> should answer
`{"status":"UP"}`.

## Code layout

```
io.github.enzogrn.mintbox
├── MintboxApplication     Entry point
├── shared                 Types every module needs (Money, typed identifiers, AggregateRoot)
└── catalog                Public API of the catalog module (events)
    └── domain             Aggregates, value objects, repository interfaces: plain Java
```

Each business module described in the [domain model](../docs/domain/modules.md) becomes a
direct sub-package of `io.github.enzogrn.mintbox` as it is built. Inside a module, the
`domain`, `application` and `infrastructure` packages have distinct roles, described in
[ADR 0009](../docs/adr/0009-framework-free-domain-model.md). A test fails the build if a
domain package depends on a framework.

`./mvnw verify` also generates diagrams of the modules, from the actual code, in
`target/spring-modulith-docs`.
