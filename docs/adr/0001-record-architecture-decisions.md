# 0001. Record architecture decisions

- **Status:** Accepted
- **Date:** 2026-10-30

## Context

The code shows what I built, but not why I built it that way. Choices like "monolith or
microservices" or "sessions or JWT" are easy to make on the fly and hard to explain six
months later. Since Mintbox is a portfolio project, the reasoning matters as much as the
result.

## Decision

I write an ADR for every significant architecture decision, in `docs/adr/`, numbered in
order, using the template in this folder's README.

I consider a decision significant when it's expensive to undo, affects several modules,
or would surprise someone reading the code for the first time.

## Consequences

- My reasoning lives next to the code and goes through the same pull requests.
- To reverse a decision, I have to write a new ADR, which forces me to think it through.
- Each decision takes a bit of writing. I think that's worth it.

## Alternatives considered

- **Not writing anything down:** I'd lose the reasoning, and the project would read as a
  list of technologies rather than a set of choices.
- **A wiki outside the repository:** it would drift out of sync with the code, and nobody
  browsing the repository would see it.
