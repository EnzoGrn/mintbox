# 0011. Amiibo snapshot as the source of truth

- **Status:** Accepted. Amends [0007](0007-versioned-catalog-snapshot.md).
- **Date:** 2026-11-03

## Context

[ADR 0007](0007-versioned-catalog-snapshot.md) planned a versioned snapshot of the amiibo
catalog as a fallback, and a scheduled job synchronizing the catalog with AmiiboAPI.

While building the import, I found that the AmiiboAPI repository was archived by its owner
on 2025-12-31. It is now read-only, so its data won't receive the amiibo released after that
date. A synchronization job would have nothing new to synchronize.

The repository's database file is still available, under the MIT license. Its format is
compact but implicit: each amiibo is stored under a 16-digit hexadecimal identifier that
encodes its character, type, series and game series, decoded through lookup tables.

## Decision

- **The snapshot is the source of truth for the amiibo catalog**, not a fallback. It lives in
  `backend/src/main/resources/catalog/amiibo.json`.
- **The snapshot uses Mintbox's own format**: one explicit entry per amiibo, with readable
  fields matching the domain model, sorted by identifier. A diff of the file shows exactly
  which entries changed.
- **I converted AmiiboAPI's database once**, with `tools/catalog/convert_amiiboapi.py`. The
  script stays in the repository, so the conversion can be reproduced and reviewed.
- **From now on, I maintain the snapshot by hand**: new amiibo are added to the file through
  a pull request.
- **The application synchronizes the catalog with the snapshot at every startup.** Since a
  synchronization only writes what changed, this costs one query when nothing changed, and
  deploying a new snapshot is enough to update the catalog.
- **There is no HTTP client for AmiiboAPI.** The `CatalogSource` port stays, so that another
  source (another product line, or a maintained API) can be plugged in later.

## Consequences

- The catalog is deterministic: the same version of the code always has the same catalog,
  and no external service is needed at runtime.
- New amiibo appear only when I add them to the snapshot. For a portfolio project, a few
  additions a year are an acceptable manual task.
- Images are still loaded from the archived repository on GitHub, which keeps serving its
  files. Entries I add by hand will need an image source of their own.
- A test checks the snapshot shipped with the application (size, unique identifiers, domain
  rules), so a bad edit fails the build instead of the next startup.
- Since there is no HTTP integration for the catalog, WireMock and Resilience4j will first
  be used with the payment provider, where they are needed anyway.

## Alternatives considered

- **Keep the planned synchronization job against AmiiboAPI:** code and tests for a source
  that no longer changes.
- **Import AmiiboAPI's file as is, and decode identifiers at import time:** keeps a
  dependency on an undocumented encoding in the application code, and makes the snapshot
  impossible to edit by hand.
- **Scrape Nintendo's website for new releases:** fragile, and likely against its terms of
  use.
