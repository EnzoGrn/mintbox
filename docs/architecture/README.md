# Architecture

This page describes Mintbox with the first two levels of the
[C4 model](https://c4model.com/): the system in its environment, then the containers it is
made of. The modules inside the backend are described in the
[domain model](../domain/modules.md).

The diagrams use Mermaid flowcharts styled after C4, because they render directly on GitHub
and are easier to maintain than Mermaid's experimental C4 syntax.

## Level 1: system context

Who uses Mintbox, and which external systems it relies on.

```mermaid
flowchart TB
    visitor["<b>Visitor</b><br/>[Person]<br/>Browses the catalog<br/>and the listings"]
    member["<b>Member</b><br/>[Person]<br/>Tracks a collection,<br/>buys and sells figures"]
    moderator["<b>Moderator</b><br/>[Person]<br/>Handles reports<br/>and disputes"]

    mintbox["<b>Mintbox</b><br/>[Software system]<br/>Collection tracker and<br/>peer-to-peer marketplace"]

    images["<b>Image host</b><br/>[External system]<br/>AmiiboAPI's maintained<br/>fork on GitHub"]
    oauth["<b>Identity providers</b><br/>[External system]<br/>Google, Apple"]
    stripe["<b>Stripe</b><br/>[External system]<br/>Payments, escrow, payouts"]
    shipping["<b>Shipping aggregator</b><br/>[External system]<br/>Rates, labels, tracking"]
    email["<b>Email provider</b><br/>[External system]<br/>Transactional emails"]

    visitor -- "Browses" --> mintbox
    member -- "Uses" --> mintbox
    moderator -- "Moderates" --> mintbox

    visitor -- "Loads catalog images from" --> images
    mintbox -- "Signs members in with" --> oauth
    mintbox -- "Takes payments and pays sellers through" --> stripe
    mintbox -- "Buys labels and tracks parcels through" --> shipping
    mintbox -- "Sends emails through" --> email
    stripe -. "Sends webhooks to" .-> mintbox
    shipping -. "Sends tracking updates to" .-> mintbox

    classDef person fill:#08427b,stroke:#052e56,color:#fff
    classDef system fill:#1168bd,stroke:#0b4884,color:#fff
    classDef external fill:#999,stroke:#6b6b6b,color:#fff
    class visitor,member,moderator person
    class mintbox system
    class images,oauth,stripe,shipping,email external
```

- The catalog itself ships with the application as a versioned snapshot: Mintbox calls no
  external service to know which figures exist
  (see [ADR 0011](../adr/0011-amiibo-snapshot-as-source-of-truth.md)).
- Catalog images are loaded by the visitor's browser straight from their source: Mintbox
  doesn't host them (see [ADR 0007](../adr/0007-versioned-catalog-snapshot.md)).

## Level 2: containers

What runs, and how the pieces talk to each other.

```mermaid
flowchart TB
    member["<b>Member</b><br/>[Person]"]

    subgraph mintbox ["Mintbox"]
        proxy["<b>Reverse proxy</b><br/>[Caddy]<br/>TLS, routing,<br/>single site for cookies"]
        web["<b>Web application</b><br/>[Next.js, TypeScript]<br/>Server-rendered pages<br/>and interactive UI"]
        api["<b>API application</b><br/>[Java, Spring Boot]<br/>Modular monolith:<br/>all business logic"]
        db[("<b>Database</b><br/>[PostgreSQL + PostGIS]<br/>Business data, sessions,<br/>event publications")]
        storage[("<b>Object storage</b><br/>[S3-compatible]<br/>Listing photos")]
    end

    external["<b>External systems</b><br/>Stripe, shipping, email,<br/>identity providers"]

    member -- "HTTPS" --> proxy
    proxy -- "/*" --> web
    proxy -- "/api/*" --> api
    web -- "Server-side rendering:<br/>REST over HTTP" --> api
    api -- "JDBC" --> db
    api -- "S3 API" --> storage
    api -- "HTTPS" --> external

    classDef person fill:#08427b,stroke:#052e56,color:#fff
    classDef container fill:#438dd5,stroke:#2e6295,color:#fff
    classDef external fill:#999,stroke:#6b6b6b,color:#fff
    class member person
    class proxy,web,api,db,storage container
    class external external
```

- **The reverse proxy serves the web app and the API under the same site**, so the
  session cookie is first-party and no CORS is needed
  (see [ADR 0003](../adr/0003-server-side-sessions-over-jwt.md)).
- **The web app holds no business logic.** It renders pages and calls the API, either from
  the browser or from the server when it renders a page for search engines.
- **The API is the only container that talks to the database, the object storage and the
  external systems.**
- **The database also stores the sessions and the domain event publications**, so there is
  no extra container for them (see
  [ADR 0002](../adr/0002-modular-monolith-with-spring-modulith.md)).
- **Object storage** is described in
  [ADR 0008](../adr/0008-s3-compatible-object-storage.md).
- The choice of Caddy as reverse proxy will be confirmed when I set up the deployment.

## Local development

`compose.yaml`, at the root of the repository, starts the infrastructure the API needs:

| Service | Image | Port | Role |
|---------|-------|------|------|
| `postgres` | PostgreSQL 18 + PostGIS 3.6 | `5432` | Database |
| `storage` | SeaweedFS | `8333` | S3-compatible object storage |
| `mailpit` | Mailpit | `1025` (SMTP), `8025` (web UI) | Catches every email sent locally |

The API and the web app will be added to the same file once they exist, so that
`docker compose up` starts the whole stack.
