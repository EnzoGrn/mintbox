import createClient from "openapi-fetch";
import { apiUrl } from "./api-url";
import type { paths } from "./schema";

/**
 * Typed client of the backend API, generated from docs/api/openapi.json: a path, parameter
 * or field the backend doesn't have fails the type check.
 *
 * On the server, requests go straight to the backend. In the browser, they stay on the
 * page's origin and Next.js forwards /api/* to the backend (see next.config.ts).
 */
export const api = createClient<paths>({
  baseUrl: typeof window === "undefined" ? apiUrl : "",
});
