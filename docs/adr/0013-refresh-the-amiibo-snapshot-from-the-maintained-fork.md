# 0013. Refresh the amiibo snapshot from the maintained fork

- **Status:** Accepted. Amends [0011](0011-amiibo-snapshot-as-source-of-truth.md).
- **Date:** 2026-11-03

## Context

[ADR 0011](0011-amiibo-snapshot-as-source-of-truth.md) assumed that, with the original
AmiiboAPI repository archived, nobody would update the amiibo data anymore, so I would
maintain the snapshot by hand.

That assumption turned out to be wrong. When the original API was announced to end, a
long-time contributor continued it through a fork,
[8bitDream/AmiiboAPI](https://github.com/8bitDream/AmiiboAPI), which now serves
[amiiboapi.org](https://amiiboapi.org/). The fork:

- is active, with automated workflows that check and update its data,
- keeps the same database format and the same MIT license,
- already contains the 20 amiibo released in 2026 that the archived data lacks, plus
  corrections to existing entries,
- serves the images of every amiibo, including the new ones.

## Decision

- **The snapshot stays the source of truth at runtime**, as decided in ADR 0011: the
  application never calls AmiiboAPI.
- **The snapshot is now generated from the fork**, data and image URLs included, with the
  same conversion script.
- **A scheduled GitHub Actions workflow refreshes it every week.** It downloads the fork's
  database, converts it, validates the result against the domain rules, and opens a pull
  request if the catalog changed.
- **Each refresh is a reviewed pull request**, the way Dependabot proposes dependency
  updates: I see which amiibo are added or corrected before they reach the catalog.

## Consequences

- New amiibo reach the catalog within a week of being added upstream, without manual
  editing, and still through a reviewed diff.
- The runtime keeps no external dependency: if the fork stops, the workflow fails and the
  catalog simply stops growing, exactly as with ADR 0011.
- Pull requests opened with the workflow's token don't trigger the other workflows. The
  refresh workflow therefore runs the snapshot validation itself, before opening the pull
  request.
- The repository must allow GitHub Actions to create pull requests (a repository setting).

## Alternatives considered

- **Keep maintaining the snapshot by hand (ADR 0011):** no longer justified now that the
  data is maintained upstream.
- **Synchronize at runtime from amiiboapi.org:** gets new amiibo without a pull request,
  but adds a runtime dependency, an HTTP client and its failure modes, and removes the
  review step.
- **A third-party action to open the pull request** (such as `create-pull-request`): less
  shell script, but another dependency with write access to the repository. The GitHub CLI
  is preinstalled on the runners and does the job.
