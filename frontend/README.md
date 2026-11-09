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
| `npm run typecheck` | Checks the types with the TypeScript compiler |
| `npm run build` | Builds the production version |

The app forwards every `/api/*` request to the backend, so the browser only talks to one
origin: no CORS, and session cookies stay first-party
([ADR 0003](../docs/adr/0003-server-side-sessions-over-jwt.md)). Set `MINTBOX_API_URL` to
point to a backend elsewhere than `http://localhost:8080`.

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
