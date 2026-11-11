# Frontend

Next.js web application (App Router, React, TypeScript, Tailwind CSS).

## Requirements

- Node.js 24 (see `.nvmrc`)
- The backend running on <http://localhost:8080> for pages that show data (see the
  [backend README](../backend/README.md))

## Commands

| Command | What it does |
|---------|--------------|
| `npm install` | Installs the dependencies |
| `npm run dev` | Starts the app on <http://localhost:3000>, reloading on every change |
| `npm run lint` | Checks the code with ESLint |
| `npm run typecheck` | Generates the route types, then checks the types with the TypeScript compiler |
| `npm run build` | Builds the production version |
| `npm run api:types` | Regenerates the API types from the [contract](../docs/api/) |

The app forwards every `/api/*` request to the backend, so the browser only talks to one
origin: no CORS, and session cookies stay first-party
([ADR 0003](../docs/adr/0003-server-side-sessions-over-jwt.md)). Set `MINTBOX_API_URL` to
point to a backend elsewhere than `http://localhost:8080`. It is read at build time too, so
set it before `npm run build`.

## Calling the API

The API's TypeScript types are generated from its contract,
[`docs/api/openapi.json`](../docs/api/), into `src/lib/api/schema.d.ts`. The client in
`src/lib/api/client.ts` uses them: a path, parameter or field the backend doesn't have fails
the type check, not the page.

```ts
const { data, error } = await api.GET("/api/catalog/series", {
  params: { query: { figureType: "FIGURE" } },
});
```

When the contract changes, run `npm run api:types` and commit the result with it. The CI
fails if the committed types don't match the contract.

Server components that call the API render inside a `<Suspense>` boundary and call
`connection()` first: the page shell is prerendered at build time without a backend, and the
data streams in when the page is requested. The home page's catalog count
(`src/components/catalog-stats.tsx`) is the example to follow, including what to show when the
backend is down.

The functions in `src/lib/api/catalog.ts` cache the catalog's data for an hour
(`"use cache"`): it only changes when its snapshot is refreshed.

## Pages

| Path | What it shows |
|------|---------------|
| `/` | The home page, with the size of the catalog |
| `/catalog` | Every catalog entry, sorted by name, 24 at a time with a "Load more" button |
| `/catalog/{id}` | One figure: its details, release dates, and the sections later milestones will fill |

The catalog's filters live in its URL, so a filtered view can be shared and bookmarked:
`/catalog?type=card&series=Animal%20Crossing`. `src/lib/catalog/search.ts` reads and writes
these URLs.

Catalog pictures are loaded from the AmiiboAPI repository on GitHub, then resized and served
by Next.js (`images` in `next.config.ts`). A picture that can't be loaded is replaced by a
silhouette.

Figure pages aren't generated at build time, since that would need a running backend during
the build: each one is rendered on its first request, then cached. An unknown figure shows the
app's "not found" page. Because the page has already started streaming by then, its HTTP status
is 200 rather than 404; Next.js adds a `noindex` tag so that search engines skip it.

## Design

The visual identity is defined in one place: the tokens at the top of
[`src/app/globals.css`](src/app/globals.css). Components only use the Tailwind utilities
generated from them (`bg-canvas`, `text-ink`, `border-line`...), never raw colors, so that
the dark theme applies everywhere.

- **Neutral surfaces, colorful figures.** A warm off-white or deep navy background lets the
  figures bring the color, whatever their product line.
- **Mint is the brand color**, from the name: primary actions and highlights only.
- **Sun yellow is for badges**, styled like stickers ("New", "Soon").
- **Figure cards look like a blister pack**: a hang tab on top, a bubble around the figure.
- **Fredoka for headings, Manrope for text and data.** Both are self-hosted from npm
  (Fontsource): the build never contacts Google, and neither do visitors' browsers.
- **Light and dark themes** follow the system preference until the visitor picks one.

Only real data is shown: a catalog entry has no condition, price or description, so cards
don't invent any. Those belong to listings, which come in a later milestone.
