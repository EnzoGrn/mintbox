import type { NextConfig } from "next";

/**
 * Where the backend API runs. The frontend forwards /api/* to it, so the browser only ever
 * talks to one origin: no CORS, and session cookies stay first-party (ADR 0003). In
 * production, the reverse proxy plays this role.
 */
const apiUrl = process.env.MINTBOX_API_URL ?? "http://localhost:8080";

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
