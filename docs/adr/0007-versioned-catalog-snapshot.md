# 0007. Versioned catalog snapshot

- **Status:** Accepted. Amended by [0011](0011-amiibo-snapshot-as-source-of-truth.md): the
  snapshot is now the source of truth, without synchronization with the API.
- **Date:** 2026-10-31

## Context

The amiibo catalog comes from AmiiboAPI, a community project. It has no availability
guarantee and no clear license, and it could change its format or disappear. Mintbox must
still start, run its tests and show its demo if the API is down.

The catalog images belong to their manufacturers. I don't want to host copies of them in a
public portfolio project.

## Decision

- I keep a **snapshot of the catalog** as a JSON file per product line, committed in the
  repository. On first start, the application imports the catalog from this snapshot, not
  from the API.
- A **synchronization job** fetches the API on a schedule and applies the differences:
  new entries are added, changed entries are updated, and nothing is ever deleted (see
  rule CAT-2 in the [aggregates](../domain/aggregates.md#catalog-entry)). If the API is
  unreachable, the job logs it and the application keeps running with the catalog it has.
- I refresh the snapshot through a command that I run by hand, and the change goes through
  a normal pull request, so I can review what changed.
- **Images are referenced by their URL at the source**, not downloaded or stored. If the
  source is unavailable, the frontend shows a neutral placeholder.
- Tests use the snapshot, or a smaller fixture derived from it, never the live API. HTTP
  calls to the API are tested against a simulated server (WireMock).

## Consequences

- The project starts and its tests pass without any network access.
- The catalog in the repository has a history: each update is a reviewed diff.
- If the API disappears, the catalog freezes at its last snapshot instead of breaking, and
  I can maintain the file by hand.
- Images depend on a third party. That is the price of not hosting them; the placeholder
  keeps the interface usable.

## Alternatives considered

- **Always read from the API at startup:** the project would fail to start, and tests
  would be flaky, whenever the API is slow or down.
- **A database dump instead of a JSON file:** harder to review in a pull request, and tied
  to the schema of the moment.
- **Downloading and hosting the images:** faster and more reliable, but it means
  redistributing images I don't own.
