# 0008. S3-compatible object storage

- **Status:** Accepted
- **Date:** 2026-10-31

## Context

Members upload photos of the figures they sell. These files don't belong in the database:
they are large, they would bloat backups, and they need to be served efficiently.

The initial plan was MinIO locally and an S3-compatible service in production. In the
meantime, MinIO stopped publishing binaries and Docker images for its community edition
(October 2025), and its repository was archived (April 2026). Building on it today would
mean depending on an unmaintained project.

The hosting budget is a few euros a month.

## Decision

- **The API only talks to object storage through the S3 API**, with the AWS SDK for Java.
  No code depends on a specific storage product; only the endpoint and the credentials
  change between environments.
- **Locally, I use SeaweedFS**, which is actively maintained, publishes versioned Docker
  images and exposes an S3 endpoint.
- **In production, I plan to use Cloudflare R2**, which is S3-compatible, has a free tier
  and charges no egress fees. This will be confirmed when I set up the deployment.
- **The API creates the buckets it needs at startup** if they don't exist, so there is no
  manual setup step.

## Consequences

- Switching storage product is a configuration change, not a code change.
- Integration tests run against a SeaweedFS container started by Testcontainers, so they
  exercise the real S3 API.
- Small differences between S3 implementations can exist. I only use the basic operations
  (put, get, delete, presigned URLs), which every implementation supports.

## Alternatives considered

- **MinIO:** the reference for local S3, but no longer maintained as open source.
- **Garage:** lightweight and maintained, but it needs a configuration file and a layout
  step before the first start, which is heavier for a one-command local setup.
- **RustFS:** close to MinIO in usage, but still in pre-release at the time of writing.
- **Storing files in PostgreSQL:** no extra service, but bigger backups and no CDN-friendly
  way to serve images.
- **The local file system:** simplest, but it doesn't work with more than one instance,
  and the code would differ between local and production.
