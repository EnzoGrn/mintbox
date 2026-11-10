import type { NextConfig } from "next";
import { apiUrl } from "./src/lib/api/api-url";

/**
 * The frontend forwards /api/* to the backend, so the browser only ever talks to one
 * origin: no CORS, and session cookies stay first-party (ADR 0003). In production, the
 * reverse proxy plays this role.
 */
const nextConfig: NextConfig = {
  cacheComponents: true,
  partialPrefetching: true,
  turbopack: {
    rules: {
      "*.css": {
        loaders: ["@tailwindcss/turbopack"],
        as: "*.css",
      },
    },
  },
  images: {
    // Catalog pictures come from the AmiiboAPI repository, the source of the catalog
    // snapshot (ADR 0011). Next.js resizes them and serves them from its own origin.
    remotePatterns: [new URL("https://raw.githubusercontent.com/8bitDream/AmiiboAPI/**")],
  },
  async rewrites() {
    return [{ source: "/api/:path*", destination: `${apiUrl}/api/:path*` }];
  },
};

export default nextConfig;
