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
  async rewrites() {
    return [{ source: "/api/:path*", destination: `${apiUrl}/api/:path*` }];
  },
};

export default nextConfig;
