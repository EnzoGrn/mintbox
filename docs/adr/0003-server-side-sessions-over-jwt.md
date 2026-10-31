# 0003. Server-side sessions over JWT

- **Status:** Accepted
- **Date:** 2026-10-30

## Context

Users sign in with an email and password, or with Google or Apple (OAuth2). The only client
for now is the Next.js web app; I might add a mobile app later.

I want users to see their active sessions and sign out of all devices, and I want a
suspension or a ban to take effect immediately.

## Decision

I use **server-side sessions** managed by Spring Security, stored in PostgreSQL with
**Spring Session JDBC**. The browser holds a cookie that is `HttpOnly`, `Secure` and
`SameSite=Lax`.

- I serve the frontend and the API from the **same site** behind the reverse proxy (for
  example `mintbox.example` and `mintbox.example/api`). The cookie is first-party and I
  don't need any CORS configuration.
- Since the browser sends cookies automatically, I protect state-changing requests with
  Spring Security's **CSRF tokens**.
- I don't add Redis for sessions until load actually requires it.

## Consequences

- Revoking a session, signing out everywhere or banning a user takes effect immediately:
  I just delete rows.
- JavaScript can never read the session cookie, which rules out token theft through XSS.
- I have no refresh-token rotation to write and get wrong.
- Every authenticated request reads the session store. At this scale it's a cheap indexed
  lookup, and I can move it to Redis later without touching application code.
- A future mobile app will need either cookie handling or a dedicated token flow. I'll
  write a new ADR when I get there.

## Alternatives considered

- **Stateless JWTs with refresh tokens:** revoking them needs a deny-list, which brings
  server state back. Tokens kept in browser storage are exposed to XSS, and rotation adds
  complexity. Their main advantage, not sharing a session store across many services,
  doesn't apply to a single application.
- **In-memory sessions:** they're lost on every restart and don't work with more than one
  instance.
