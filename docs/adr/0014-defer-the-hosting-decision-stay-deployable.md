# 0014. Defer the hosting decision, stay deployable

- **Status:** Accepted
- **Date:** 2026-11-03

## Context

Mintbox is an open-source portfolio project, built in my spare time with no deadline. The
initial plan ended with a public demo: a server, a domain name, transactional emails and
object storage in the cloud.

The options I looked at in November 2026:

| Option | Cost | Drawback |
|--------|------|----------|
| A small VPS (Hetzner) with Docker Compose | about 6 to 8 EUR a month, with an IPv4 address and a domain | A recurring cost, and security updates to apply |
| Oracle Cloud Always Free | Free | Oracle halved its free ARM allowance in June 2026 without notice; creating instances often fails for lack of capacity |
| A platform's free tier (Render) | Free | Services sleep after 15 minutes of inactivity, and the free PostgreSQL database expires after 30 days |

For now, there is nothing to show online. What a reviewer values is the code, and the
evidence that the project could run in production: container images, a production
configuration, a deployment pipeline, backups.

## Decision

- **Mintbox stays deployable at all times**: container images for the backend and the
  frontend, a production Docker Compose file with the reverse proxy, and configuration
  through environment variables only, with no secret in the repository.
- **No hosting is rented until there is a demo worth showing.** Until then, the project runs
  locally with `docker compose up`, with its local stand-ins for external services: Mailpit
  for emails, SeaweedFS for object storage, and Stripe in test mode, with its CLI forwarding
  webhooks to the local application.
- **The hosting choice is made in milestone 6**, in a new ADR, between a paid VPS and a free
  tier, based on the offers available at that time.
- **The demo material doesn't depend on hosting**: demo data and account, screenshots and a
  short recording in the README, so that the project can be judged without running it.

## Consequences

- No running cost now, and nothing that breaks while I'm not looking.
- The production setup is built and run locally, so it is verified even before it has a
  real server: the reverse proxy serving the frontend and the API under the same site
  ([ADR 0003](0003-server-side-sessions-over-jwt.md)) is part of the local stack.
- The production object storage ([ADR 0008](0008-s3-compatible-object-storage.md)) and the
  email provider stay undecided until the hosting is.
- Choosing later means choosing with up-to-date prices: free tiers and entry-level offers
  changed several times in 2025 and 2026.

## Alternatives considered

- **Rent a VPS now:** the closest to production, but a recurring cost for a demo with
  nothing to show yet.
- **Never deploy, local only:** free and simple, but a public demo can matter when
  presenting the project, and keeping it deployable costs little.
